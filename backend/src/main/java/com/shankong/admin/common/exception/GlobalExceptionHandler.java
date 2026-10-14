package com.shankong.admin.common.exception;

import com.shankong.admin.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理
 */
@Slf4j
// 全局异常处理类。所有的controller的异常都会被这里处理
@RestControllerAdvice
public class GlobalExceptionHandler {
    // 处理可以遇见的业务异常(比如用户不存在、密码错误等业务异常)
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        return Result.error(e.getCode(), e.getMessage());
    }

    // @Valid 校验失败异常，比如密码为空、手机号格式错误等校验失败异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        return Result.error("400", message);
    }

    // 请求体缺失或 JSON 格式错误：是客户端的问题，返回 400
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<Void> handleNotReadable(HttpMessageNotReadableException e) {
        return Result.error("400", "请求体格式不正确，请检查是否为合法的 JSON");
    }

    // 其他异常，日志记录异常信息
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("全局异常处理", e);
        return Result.error("500", "服务器繁忙，请稍后再试");
    }
}
