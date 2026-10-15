package com.shankong.admin.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 返回给前端的用户视图对象
 */
@Data
@Builder
public class UserVO {
    private String token;
    private String userTypeName;
    private UserDetailResponseDTO userInfo;

    @Data
    @Builder
    public static class UserDetailResponseDTO {
        private Long id;
        private String username;
        private String realName;
        private Integer userType;
        private Integer status;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
}
