package com.shankong.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserLoginDTO {
    @NotBlank(message = "用户名或手机号不能为空")
    private String account;

    @NotBlank(message = "密码不能为空")
    private String password;
}
