package io.github.nextentity.core.reflect;

import io.github.nextentity.core.util.NullableConcurrentMap;

import java.lang.reflect.Method;

/// 代理对象的方法值映射，提供线程安全的方法到值的映射，
/// 并内置 {@link LazyValue} 延迟加载解析。
///
/// @author HuangChengwei
/// @since 2.2.2
public class LazyValueMap extends NullableConcurrentMap<Method, Object> {

    /// 加载方法对应的值，如果值是 {@link LazyValue} 则触发加载并替换。
    ///
    /// 使用 {@link #compute} 保证原子性，同一方法仅有一个线程执行加载。
    ///
    /// @param method 方法
    /// @return 加载后的值
    public Object get(Method method) {
        Object result = compute(method, (_, current) -> {
            if (current instanceof LazyValue loader) {
                return wrapValue(loader.load());
            }
            return current;
        });
        return unwrapValue(result);
    }
}
