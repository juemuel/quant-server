package com.juemuel.trend.exception;

public class PermissionDeniedException extends BusinessException {
    public PermissionDeniedException(String message) {
        super(403, message);
    }
}
