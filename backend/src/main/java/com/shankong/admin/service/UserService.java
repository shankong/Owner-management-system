package com.shankong.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shankong.admin.common.exception.BusinessException;
import com.shankong.admin.dto.UserLoginDTO;
import com.shankong.admin.dto.UserRegisterDTO;
import com.shankong.admin.dto.UserVO;
import com.shankong.admin.entity.User;
import com.shankong.admin.enums.EmailCodeScene;
import com.shankong.admin.mapper.UserMapper;
import com.shankong.admin.service.convert.UserConvert;
import com.shankong.admin.utils.JwtTokenUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 用户服务类
 */
@Slf4j
@Service
public class UserService {
    @Resource
    private UserMapper userMapper;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private EmailCodeService emailCodeService;

    /**
     * 用户登陆服务
     * @param userLoginDTO 用户登陆DTO对象
     * @return UserVO 用户返货给前端的VO对象
     */
    public UserVO userLoginService(UserLoginDTO userLoginDTO) {
        // 登陆的邮箱
        String email = userLoginDTO.getEmail();
        // 登陆密码
        String password = userLoginDTO.getPassword();

        // 根据用户邮箱查询用户
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getEmail, email)); // 根据邮箱查询用户
        // 校验用户是否存在
        if(user == null) {
            throw new BusinessException("400", "用户不存在");
        }
        // 密文哈希密码校验
        // matches(用户输入的明文，数据库中的密文密码)
        else if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException("400", "用户名或密码错误");
        }
        // 校验用户状态是否正常
        else if (user.getStatus() == 0) {
            throw new BusinessException("400", "用户已被禁用，请联系管理员");
        }

        // 生成token
        String token = JwtTokenUtil.generateToken(user.getId(), user.getUsername(), user.getUserType());

        // 构建VO并返回
        return UserConvert.entityToVO(token, UserConvert.entityToDetailResponse(user));
    }

    /**
     * 管理员注册服务
     * @param userRegisterDTO 用户注册DTO对象
     * @return UserVO 用户返货给前端的VO对象
     */
    @Transactional
    public UserVO adminRegisterService(UserRegisterDTO userRegisterDTO) {
        // 注册用户名
        String username = userRegisterDTO.getUsername();
        // 注册密码
        String password = userRegisterDTO.getPassword();
        // 注册邮箱
        String email = userRegisterDTO.getEmail();
        // 校验用户名是否已被占用
        if(existsByUsername(username)){
            throw new BusinessException("400", "用户名已被占用");
        }
        // 校验邮箱是否已被占用
        if(existsByEmailOrPhone(email)){
            throw new BusinessException("400", "邮箱已被占用");
        }

        // 注册管理员
        User admin = new User();
        admin.setUsername(username);
        admin.setPassword(passwordEncoder.encode(password));
        admin.setEmail(email);
        admin.setRealName(userRegisterDTO.getRealName());
        admin.setStatus(1); // 正常状态
        admin.setUserType(1); // 管理员类型
        admin.setCreatedAt(LocalDateTime.now());

        emailCodeService.verify(email, userRegisterDTO.getEmailCode(), EmailCodeScene.REGISTER);
        // 数据库插入数据
        userMapper.insert(admin);

        // 构建VO并返回
        return UserConvert.entityToVO(null, UserConvert.entityToDetailResponse(admin));
    }

    /**
     * 判断用户名是否已被占用
     * @param username 用户名
     * @return true:已被占用 false:未被占用
     */
    private boolean existsByUsername(String username) {
        return userMapper.exists(
                new LambdaQueryWrapper<User>()
                    .eq(User::getUsername, username));
    }

    /**
     * 判断是手机号或者邮箱
     * @param emailOrPhone 邮箱或手机号
     * @return true:手机号 false:邮箱
     */
    public boolean isEmailOrPhone(String emailOrPhone) {
        return emailOrPhone.matches("^1[3-9]\\d{9}$");
    }

    /**
     * 判断邮箱或手机号是否已被占用
     * @param emailOrPhone 邮箱或手机号
     * @return true:已被占用 false:未被占用
     */
    public boolean existsByEmailOrPhone(String emailOrPhone) {
        return userMapper.exists(
                new LambdaQueryWrapper<User>()
                    .eq(User::getPhone, emailOrPhone)
                    .or()
                    .eq(User::getEmail, emailOrPhone));
    }
}
