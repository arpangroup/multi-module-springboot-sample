package com.trustai.common.utils;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.util.Map;

public class FieldUpdateUtil {
    public static void updateFields(Object target, Map<String, String> updates) {
        BeanWrapper wrapper = new BeanWrapperImpl(target);
        updates.forEach((key, value) -> {
            if (wrapper.isWritableProperty(key)) {
                Object castedValue = convertValue(wrapper.getPropertyType(key), value);
                wrapper.setPropertyValue(key, castedValue);
            } else {
                throw new IllegalArgumentException("Invalid field: " + key);
            }
        });
    }

    private static Object convertValue(Class<?> type, String value) {
        if (type.equals(Boolean.class) || type.equals(boolean.class)) {
            return Boolean.parseBoolean(value);
        }
        return value;
    }
}
