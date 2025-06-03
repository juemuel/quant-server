package com.juemuel.trend.exception;

import com.juemuel.trend.http.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * 全局异常处理器（适配 Result 返回结构）
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 拦截参数校验异常（如 @Valid 注解触发的异常）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        // 获取第一个错误信息
        String errorMessage = Optional.ofNullable(ex.getBindingResult().getAllErrors())
                .filter(errors -> !errors.isEmpty())
                .map(errors -> errors.get(0))
                .map(ObjectError::getDefaultMessage)
                .orElse("参数校验失败");

        log.warn("参数校验失败：{}", errorMessage);
        return Result.error(400, errorMessage);
    }

    /**
     * 拦截自定义业务异常：如“分组项已存在”
     */
    @ExceptionHandler(DuplicateGroupItemException.class)
    public Result<?> handleDuplicateGroupItemException(DuplicateGroupItemException ex) {
        log.warn("业务异常：{}", ex.getMessage());
        return Result.error(409, ex.getMessage()); // 409 Conflict 表示资源冲突
    }

    /**
     * 拦截所有未处理的异常
     */
    @ExceptionHandler(Throwable.class)
    public Result<?> handleAllUncaughtException(HttpServletRequest request, Throwable ex) {
        log.error("[{}] {} 出现未知异常", request.getMethod(), request.getRequestURI(), ex);
        return Result.error(500, "系统内部错误，请联系管理员");
    }
}
