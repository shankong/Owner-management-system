package com.shankong.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shankong.admin.common.exception.BusinessException;
import com.shankong.admin.dto.UserLoginDTO;
import com.shankong.admin.dto.UserVO;
import com.shankong.admin.entity.User;
import com.shankong.admin.mapper.UserMapper;
import com.shankong.admin.service.convert.UserConvert;
import com.shankong.admin.utils.JwtTokenUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    /**
     * 用户登陆服务
     *
     * @param userLoginDTO
     * @return Result<UserVO>
     */
    public UserVO userLoginService(UserLoginDTO userLoginDTO) {

        // 登陆账号
        String account = userLoginDTO.getAccount();
        // 登陆密码
        String password = userLoginDTO.getPassword();


        // 更具登录名查询用户
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, account)
                        .or()
                        .eq(User::getPhone, account));
        // 校验用户是否存在
        if (user == null) {
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
        log.info("token:{}", token);

        // 构建VO并返回
        return UserConvert.entityToVO(token, UserConvert.entityToDetailResponse(user));
    }
}
