package com.juemuel.trend.exception;

public class NotFoundException extends BusinessException {
    public NotFoundException(String message) {
        super(404, message);
    }

    public NotFoundException(String message, Object... args) {
        super(404, String.format(message, args));
    }
}
