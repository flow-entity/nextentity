package io.github.nextentity.core.reflect;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public final class InstanceInvocationHandler implements InvocationHandler {
    private static final Object NULL = new Object();

    private final Class<?> resultType;
    private final Map<Method, Object> data;

    InstanceInvocationHandler(Class<?> resultType, Map<Method, Object> data) {
        this(resultType, data, null);
    }

    InstanceInvocationHandler(Class<?> resultType,
                              Map<Method, Object> data,
                              Map<Method, LazyLoader> lazyAttributeLoaders) {
        this.resultType = resultType;
        data.replaceAll((_, value) -> wrapIfNull(value));
        if (lazyAttributeLoaders != null && !lazyAttributeLoaders.isEmpty()) {
            this.data = wrapAsConcurrentHashMap(data, lazyAttributeLoaders);
        } else {
            this.data = data;
        }
    }

    private Map<Method, Object> wrapAsConcurrentHashMap(Map<Method, Object> data,
                                                        Map<Method, LazyLoader> lazyAttributeLoaders) {
        ConcurrentHashMap<Method, Object> map = new ConcurrentHashMap<>(data);
        for (Map.Entry<Method, LazyLoader> entry : lazyAttributeLoaders.entrySet()) {
            map.put(entry.getKey(), new LazyWrapper(entry.getValue()));
        }
        return map;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = data.get(method);
        if (result != null) {
            if (result instanceof LazyWrapper wrapper) {
                LazyLoader loader = wrapper.loader();
                result = wrapIfNull(loader.load(this));
                data.replace(method, wrapper, result);
            }
            return unwrapIfNull(result);
        }
        if (method.getDeclaringClass() == Object.class) {
            return method.invoke(this, args);
        }
        if (method.isDefault()) {
            return ReflectUtil.invokeDefaultMethod(proxy, method, args);
        }
        throw new AbstractMethodError(method.toString());
    }

    private Object wrapIfNull(Object value) {
        return value == null ? NULL : value;
    }

    private Object unwrapIfNull(Object value) {
        return value == NULL ? null : value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (Proxy.isProxyClass(o.getClass())) {
            o = Proxy.getInvocationHandler(o);
            if (this == o) return true;
        }
        if (getClass() != o.getClass()) return false;
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

    private record LazyWrapper(LazyLoader loader) {
    }

}
