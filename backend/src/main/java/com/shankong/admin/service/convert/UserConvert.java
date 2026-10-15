package com.shankong.admin.service.convert;

import com.shankong.admin.dto.UserVO;
import com.shankong.admin.entity.User;

public class UserConvert {
    // 构建响应DTO
    public static UserVO.UserDetailResponseDTO entityToDetailResponse(User user) {
        return UserVO.UserDetailResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .userType(user.getUserType())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
    // 构建VO
    public static UserVO entityToVO(String token, UserVO.UserDetailResponseDTO userInfo) {
        return UserVO.builder()
                .token(token)
                .userTypeName(getUserType(userInfo.getUserType()))
                .userInfo(userInfo)
                .build();
    }

    /**
     * 获取用户类型名称
     * @param userType 用户类型
     * @return 用户类型名称
     */
       private static String getUserType(Integer userType) {
        if (userType == null) {
            return "未知";
        }
        return switch (userType) {
            case 1 -> "管理员";
            case 2 -> "业主";
            case 3 -> "维修工";
            default -> "未知";
        };
    }
}
