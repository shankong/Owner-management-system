package com.shankong.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shankong.admin.common.Result;
import com.shankong.admin.dto.UserLoginDTO;
import com.shankong.admin.dto.UserVO;
import com.shankong.admin.entity.User;
import com.shankong.admin.mapper.UserMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Resource
    private UserMapper userMapper;

    public String userLoginService(UserLoginDTO userLoginDTO) {

        // 登陆账号
        String username = userLoginDTO.getAccount();
        // 登陆密码
        String password = userLoginDTO.getPassword();

        try{
            // 更具登录名查询用户
            User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
