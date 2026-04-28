package io.github.nextentity.core.reflect;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/// 代理对象的方法值映射，提供线程安全的方法到值的映射，
/// 并内置 {@link LazyValue} 延迟加载解析。
///
/// @author HuangChengwei
/// @since 2.2.2
public class LazyValueMap {

    private final Map<Method, Object> target = new ConcurrentHashMap<>();

    /// 加载方法对应的值，如果值是 {@link LazyValue} 则触发加载并替换。
    ///
    /// @param method 方法
    /// @return 加载后的值
    public Object get(Method method) {
        Object result = target.get(method);
        if (result instanceof LazyValue lazyValue) {
            return lazyValue.get();
        }
        return result;
    }

    public void put(Method getter, Object value) {
        target.put(getter, value);
    }

    public boolean containsKey(Method method) {
        return target.containsKey(method);
    }

    @Override
    public final boolean equals(Object o) {
        return o instanceof LazyValueMap that && target.equals(that.target);
    }

    @Override
    public int hashCode() {
        return target.hashCode();
    }
}
