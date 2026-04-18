package io.github.nextentity.core.reflect;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Objects;

public final class InstanceInvocationHandler implements InvocationHandler {

    private final Class<?> resultType;
    private final ResultMap data;

    InstanceInvocationHandler(Class<?> resultType, ResultMap data) {
        this.resultType = resultType;
        this.data = data;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = data.get(method);
        if (result != null) {
            if (data.isNull(result)) {
                return null;
            }
            if (result instanceof AttributeLoader loader) {
                result = loader.load();
                data.replace(method, loader, result);
            }
            return result;
        }
        if (method.getDeclaringClass() == Object.class) {
            return method.invoke(this, args);
        }
        if (method.isDefault()) {
            return ReflectUtil.invokeDefaultMethod(proxy, method, args);
        }
        for (Method m : data.keySet()) {
            if (m.getName().equals(method.getName())) {
                System.out.println(m.equals(method));
            }
        }
        throw new AbstractMethodError(method.toString());
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
