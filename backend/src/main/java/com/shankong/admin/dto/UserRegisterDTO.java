package com.shankong.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegisterDTO {
    @NotBlank(message = "用户名不能为空")
    private String account;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 50, message = "密码长度必须在6到50之间")
    private String password;

    @Pattern(regexp = "^1[3-9]\\d{9}$|^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "手机号或邮箱格式错误")
    @NotBlank(message = "手机号或邮箱不能为空")
    private String emailOrPhone;

    @Size(min = 2, max = 10, message = "姓名长度必须在2到10之间")
    private String realName;
}
