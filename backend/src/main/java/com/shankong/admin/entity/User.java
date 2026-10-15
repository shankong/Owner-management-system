package com.shankong.admin.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体类
 */
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

    public String getUserTypeName(Integer userType){
        return switch (userType) {
            case 1 -> "管理员";
            case 2 -> "业主";
            case 3 -> "维修工";
            default -> "未知用户类型";
        };
    }

    public String getStatusName(Integer status){
        return switch (status) {
            case 0 -> "禁用";
            case 1 -> "正常";
            default -> "未知状态";
        };
    }
}
