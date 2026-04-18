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
    private final ResultMap data;

    InstanceInvocationHandler(Class<?> resultType,
                              ResultMap data) {
        this.resultType = resultType;
        data.replaceAll((_, value) -> wrapIfNull(value));
        this.data = data;
    }

    private Map<Method, Object> wrapAsConcurrentHashMap(Map<Method, Object> data) {
        ConcurrentHashMap<Method, Object> map = new ConcurrentHashMap<>(data);

        return map;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = data.get(method);
        if (result != null) {
            if (result instanceof AttributeLoader loader) {
                result = wrapIfNull(loader.load());
                data.replace(method, loader, result);
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

}
