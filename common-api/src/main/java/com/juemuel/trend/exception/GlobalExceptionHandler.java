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
     * 拦截自定义业务异常（基类）
     */
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException ex) {
        // 可以直接使用 BusinessException 提供的 toResult 方法返回统一结构
        return ex.toResult();
    }

    @ExceptionHandler(ConflictException.class)
    public Result<?> handleConflictException(ConflictException ex) {
        log.warn("资源冲突异常：{}", ex.getMessage());
        return Result.error(ex.getCode(), ex.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    public Result<?> handleNotFoundException(NotFoundException ex) {
        log.warn("资源未找到：{}", ex.getMessage());
        return Result.error(ex.getCode(), ex.getMessage());
    }
    @ExceptionHandler(PermissionDeniedException.class)
    public Result<?> handlePermissionDeniedException(PermissionDeniedException ex) {
        log.warn("权限不足：{}", ex.getMessage());
        return Result.error(ex.getCode(), ex.getMessage());
    }

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
     * 拦截所有未处理的异常
     */
    @ExceptionHandler(Throwable.class)
    public Result<?> handleAllUncaughtException(HttpServletRequest request, Throwable ex) {
        log.error("[{}] {} 出现未知异常", request.getMethod(), request.getRequestURI(), ex);
        return Result.error(500, "系统内部错误，请联系管理员");
    }
}
