package com.shankong.admin.controller;

import com.shankong.admin.common.Result;
import com.shankong.admin.dto.UserLoginDTO;
import com.shankong.admin.dto.UserVO;
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

    @PostMapping("/login")
    public Result<UserVO> userLogin(@RequestBody @Valid UserLoginDTO userLoginDTO)  {
        return Result.success(userService.userLoginService(userLoginDTO));
    }

}
