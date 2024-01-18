package org.jingtao8a.util;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class SingletonFactory {
    private static final Map<String, Object> OBJECT_MAP = new ConcurrentHashMap<>();

    public static <T> T getInstance(Class<T> c) {
        if (c == null) {
            throw new IllegalArgumentException();
        }
        String key = c.toString();
        if (OBJECT_MAP.containsKey(key)) {
            return (T)OBJECT_MAP.get(key);
        }
        OBJECT_MAP.computeIfAbsent(key, k-> {
            try {
                return c.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        return (T)OBJECT_MAP.get(key);
    }
}
