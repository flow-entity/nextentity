package io.github.nextentity.core.reflect;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

public final class ResultMap {
    private static final Object NULL = new Object();

    private final Map<Method, Object> target = new ConcurrentHashMap<>();

    public void replaceAll(BiFunction<? super Method, Object, ?> function) {
        target.replaceAll(function);
    }

    public Object get(Method method) {
        return unwrapIfNull(target.get(method));
    }

    public boolean replace(Method method, Object loader, Object result) {
        return target.replace(method, wrapIfNull(loader), wrapIfNull(result));
    }

    public void put(Method getter, Object value) {
        target.put(getter, wrapIfNull(value));
    }

    private Object wrapIfNull(Object value) {
        return value == null ? NULL : value;
    }

    private Object unwrapIfNull(Object value) {
        return value == NULL ? null : value;
    }

    public boolean isEmpty() {
        return target.isEmpty();
    }
}
