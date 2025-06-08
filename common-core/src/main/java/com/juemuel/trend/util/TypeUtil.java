package com.juemuel.trend.util;

public class TypeUtil {

    // 工具方法：安全地转换为 Long
    public static Long parseLong(Object value) {
        if (value == null) return null;
        if (value instanceof Long) {
            return (Long) value;
        } else if (value instanceof String) {
            try {
                return Long.parseLong((String) value);
            } catch (NumberFormatException ignored) {}
        }
        return null;
    }
}
