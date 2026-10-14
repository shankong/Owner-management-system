package com.shankong.admin.common.exception;

import lombok.Getter;

/**
 * 业务异常
 */
@Getter
public class BusinessException extends RuntimeException {

    private final String code;

    public BusinessException(String message) {
        this("400", message);
    }

    public BusinessException(String code, String message) {
        super(message);          // 这句必须有，否则 e.getMessage() 是 null
        this.code = code;
    }
}
