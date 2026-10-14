package com.shankong.admin.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserVO {
    private Long id;
    private String username;
    private String realName;
    private String phone;
    private String userTypeName;
    private String statusName;
    private LocalDateTime createdAt;
}
