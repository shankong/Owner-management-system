package com.shankong.admin.common;

import lombok.Data;

/**
 * 通用返回结果对象
 * @param <T>
 */
@Data
public class Result <T> {
    private String code;
    private String message;
    private T data;

    /**
     * 成功返回结果
     * @return Result<T> 成功返回结果对象
     */
    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.setCode("200");
        result.setMessage("success");
        return result;
    }
    /**
     * 成功返回结果
     * @param data 返回数据
     * @return Result<T>
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode("200");
        result.setMessage("success");
        result.setData(data);
        return result;
    }
    /**
     * 错误返回结果
     * @param code 错误码
     * @param message 错误信息
     * @return Result<T>
     */
    public static <T> Result<T> error(String code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
}
