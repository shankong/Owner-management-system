package com.shankong.admin.enums;

import lombok.Getter;

/**
 * 邮箱验证码场景枚举
 */
@Getter
public enum EmailCodeScene {
    REGISTER("register:"),  // 注册场景
    LOGIN("login:");        // 登录场景

    private final String prefix;  // 邮箱验证码场景前缀

    EmailCodeScene(String prefix) { this.prefix = prefix; }
}
