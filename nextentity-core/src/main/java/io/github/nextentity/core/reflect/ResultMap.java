package io.github.nextentity.core.reflect;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class ResultMap {
    private static final Object NULL = new Object();

    private final Map<Method, Object> target = new ConcurrentHashMap<>();

    public Object get(Method method) {
        return target.get(method);
    }

    public boolean isNull(Object value) {
        return value == NULL;
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

    public boolean isEmpty() {
        return target.isEmpty();
    }

    public Set<Method> keySet() {
        return target.keySet();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ResultMap resultMap)) return false;

        return target.equals(resultMap.target);
    }

    @Override
    public int hashCode() {
        return target.hashCode();
    }
}
