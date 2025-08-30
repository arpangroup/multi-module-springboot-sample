package com.trustai.common.utils;

import java.util.Arrays;

public class EnumUtil {
    public static <E extends Enum<E>> E fromString(Class<E> enumClass, String input) {
        return Arrays.stream(enumClass.getEnumConstants())
                .filter(e -> e.name().equalsIgnoreCase(input))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid " + enumClass.getSimpleName() + ": " + input));
    }

}
