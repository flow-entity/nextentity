package io.github.nextentity.core.reflect;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public final class InstanceInvocationHandler implements InvocationHandler {
    private static final Object NULL = new Object();

    private final Class<?> resultType;
    private final Map<Method, Object> data;

    public InstanceInvocationHandler(Class<?> resultType, Map<Method, Object> data) {
        this.resultType = resultType;
        this.data = data;
    }

    public InstanceInvocationHandler(Class<?> resultType, Map<Method, Object> data, Map<Method, Function<Map<Method, Object>, Object>> lazyAttributes) {
        this.resultType = resultType;
        if (lazyAttributes != null && !lazyAttributes.isEmpty()) {
            data = wrapAsConcurrentHashMap(data);
            for (Map.Entry<Method, Function<Map<Method, Object>, Object>> entry : lazyAttributes.entrySet()) {
                data.put(entry.getKey(), new LazyWrapper(entry.getValue()));
            }
        }
        this.data = data;
    }

    private Map<Method, Object> wrapAsConcurrentHashMap(Map<Method, Object> data) {
        Map<Method, Object> map = new ConcurrentHashMap<>();
        for (Map.Entry<Method, Object> entry : data.entrySet()) {
            Object value = entry.getValue();
            Method key = entry.getKey();
            if (key != null) {
                map.put(key, value == null ? NULL : value);
            }
        }
        return map;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (data.containsKey(method)) {
            Object value = data.get(method);
            if (value instanceof LazyWrapper wrapper) {
                return wrapper.get(data, method);
            }
            return value == NULL ? null : value;
        }
        if (method.getDeclaringClass() == Object.class) {
            return method.invoke(this, args);
        }
        if (method.isDefault()) {
            return ReflectUtil.invokeDefaultMethod(proxy, method, args);
        }
        throw new AbstractMethodError(method.toString());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (Proxy.isProxyClass(o.getClass())) {
            o = Proxy.getInvocationHandler(o);
        }
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InstanceInvocationHandler that = (InstanceInvocationHandler) o;
        return Objects.equals(resultType, that.resultType) && Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        int result = data.hashCode();
        result = 31 * result + resultType.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return resultType.getSimpleName() + "@" + System.identityHashCode(resultType);
    }

    public Class<?> resultType() {
        return this.resultType;
    }

    public Map<Method, Object> data() {
        return this.data;
    }

    private record LazyWrapper(Function<Map<Method, Object>, Object> target) {
        public Object get(Map<Method, Object> data, Method method) {
            return data.computeIfPresent(method, (_, v) -> {
                Object value = v == this ? target.apply(data) : v;
                return value == null ? NULL : value;
            });
        }
    }

}
