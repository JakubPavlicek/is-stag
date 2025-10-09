package com.stag.identity.shared.util;

import java.util.function.Consumer;

public class ObjectUtils {

    private ObjectUtils() {
    }

    public static <T> void updateIfNotNull(T value, Consumer<T> setter) {
        if (value != null) {
            setter.accept(value);
        }
    }

    public static <T> T getValueOrDefault(T newValue, T currentValue) {
        return newValue != null ? newValue : currentValue;
    }

}
