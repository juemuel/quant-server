package com.juemuel.trend.exception;

public class ConflictException extends BusinessException {
    public ConflictException(String message) {
        super(409, message);
    }

    public ConflictException(String message, Object... args) {
        super(409, String.format(message, args));
    }
}
