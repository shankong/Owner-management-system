package com.shankong.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 用户登录DTO
 */
@Data
public class UserLoginDTO {
    @NotBlank(message = "邮箱不能为空")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "邮箱格式错误")
    private String email;

    @NotBlank(message = "密码不能为空")
    private String password;
}
