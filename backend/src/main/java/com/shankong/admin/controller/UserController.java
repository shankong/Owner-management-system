package com.shankong.admin.controller;

import com.shankong.admin.common.Result;
import com.shankong.admin.dto.SendEmailCodeDTO;
import com.shankong.admin.dto.UserLoginDTO;
import com.shankong.admin.dto.UserRegisterDTO;
import com.shankong.admin.dto.UserVO;
import com.shankong.admin.enums.EmailCodeScene;
import com.shankong.admin.service.EmailCodeService;
import com.shankong.admin.service.UserService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;
    @Resource
    private EmailCodeService emailCodeService;

    /**
     * 用户登录
     * @param userLoginDTO 用户登录DTO
     * @return Result<UserVO> 返回登录后的用户VO对象
     */
    @PostMapping("/login")
    public Result<UserVO> userLogin(@RequestBody @Valid UserLoginDTO userLoginDTO)  {
        return Result.success(userService.userLoginService(userLoginDTO));
    }

    /**
     * 管理员注册
     * @param userRegisterDTO 用户注册DTO
     * @return Result<UserVO> 返回注册后的用户VO对象
     */
    @PostMapping("/adminRegister")
    public Result<UserVO> adminRegister(@RequestBody @Valid UserRegisterDTO userRegisterDTO)  {
        return Result.success(userService.adminRegisterService(userRegisterDTO));
    }

    /**
     * 发送注册邮箱验证码
     * @param sendEmailCodeDTO 发送邮箱验证码DTO
     * @return Result<Void> 返回空值, 表示发送成功
     */
    @PostMapping("/adminRegister/emailCode")
    public Result<Void> sendEmailCode(@RequestBody @Valid SendEmailCodeDTO sendEmailCodeDTO)  {
        emailCodeService.seedCode(sendEmailCodeDTO.getEmail(), EmailCodeScene.REGISTER);
        return Result.success();
    }
}