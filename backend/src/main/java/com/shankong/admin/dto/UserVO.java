package com.shankong.admin.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 返回给前端的用户视图对象
 */
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
