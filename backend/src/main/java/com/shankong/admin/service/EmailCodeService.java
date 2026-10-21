package com.shankong.admin.service;

import com.shankong.admin.common.exception.BusinessException;
import com.shankong.admin.enums.EmailCodeScene;
import com.shankong.admin.utils.EmailUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;

/**
 * 邮件验证码服务类
 */
@Service
public class EmailCodeService {
    // 随机数工具类，安全加密，UUID也是用的这个
    private static final SecureRandom RANDOM = new SecureRandom();

    // 引入Redis模板，用于操作Redis
    // Redis模板是Spring Boot提供的一种操作Redis的工具类，它提供了对Redis的读写操作
    @Resource(name = "stringRedisTemplate")
    private StringRedisTemplate redisTemplate;

    @Resource
    private EmailUtil emailUtil;

    /**
     * 发送注册验证码邮件
     * @param email 用户邮箱
     */
    public void seedCode(String email, EmailCodeScene scene) {
        // 发送验证码缓存时效键前缀
        String limitKey = scene.getPrefix() + "limit:" + email;
        // 验证码缓存键前缀
        String codeKey = scene.getPrefix() + "code:" + email;
        // 60秒内只能发送一次验证码
        Boolean ok = redisTemplate.opsForValue()
                .setIfAbsent(limitKey, "1", Duration.ofSeconds(60));
        // Boolean是包装类型，永远用 equals，不要用 ==
        if(Boolean.FALSE.equals(ok)){
            throw new BusinessException("400", "发送太频繁，请稍后再试");
        }

        // 生成六位数验证码（%06d 保证前导零）
        String code = String.format("%06d", RANDOM.nextInt(1_000_000));
        // 缓存验证码到Redis，过期时间为5分钟
        redisTemplate.opsForValue().set(codeKey, code, Duration.ofMinutes(5));
        redisTemplate.delete(scene.getPrefix() + "fail:" + email);

        // 发送验证码邮件
        try {
            emailUtil.seedEmail(email, code);
        } catch (Exception e) {
            // 验证码发送失败，放开60s时限，让用户重新发送
            redisTemplate.delete(limitKey);
            throw e;
        }
    }
    /**
     * 校验注册验证码
     * @param email 用户邮箱
     * @param code  用户输入的验证码
     */
    public void verify(String email, String code, EmailCodeScene scene){
        // 错误次数缓存键前缀
        String failKey  = scene.getPrefix() + "fail:" + email;
        // 验证码缓存键前缀
        String codeKey = scene.getPrefix() + "code:" + email;
        // 从Redis中获取验证码
        String redisCode = redisTemplate.opsForValue().get(codeKey);
        // 校验验证码是否过期
        if(redisCode == null){
            throw new BusinessException("400", "验证码已过期，请重新发送");
        }
        /*
          引用数据类型(Long, Integer等)，有一个 -128~127 有缓存，
          这个范围内的值，== 会返回true，指向的地址是同一个缓存池对象
          Long a = 100L;
          Long b = 100L;
          System.out.println(a == b);  返回 true
         */
        // 校验验证码是否正确
        if(!redisCode.equals(code)){
            // 验证码错误，增加错误次数
            Long n = redisTemplate.opsForValue().increment(failKey);
            if(n != null && n == 1L){
                redisTemplate.expire(failKey, Duration.ofMinutes(5));
            }
            if(n != null && n >= 5){
                redisTemplate.delete(codeKey);
                throw new BusinessException("400", "验证码错误次数超过5次，请稍后再试");
            }
            throw new BusinessException("400", "验证码错误");
        }
        // 验证码通过后立即删除
        redisTemplate.delete(codeKey);
        redisTemplate.delete(failKey);
    }
}
