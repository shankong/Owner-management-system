package com.shankong.admin.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String realName;
    private String phone;
    private String email;
    private Integer userType;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
