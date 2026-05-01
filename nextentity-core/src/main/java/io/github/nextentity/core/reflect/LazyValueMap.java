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

    private final Map<Method, Object> values = new ConcurrentHashMap<>();

    /// 加载方法对应的值，如果值是 {@link Resolvable} 则触发加载并替换。
    ///
    /// @param method 方法
    /// @return 加载后的值
    public Object get(Method method) {
        Object result = values.get(method);
        if (result instanceof Resolvable resolvable) {
            return resolvable.get();
        }
        return result;
    }

    public void put(Method getter, Object value) {
        if (value == null) {
            value = NullValue.of();
        }
        values.put(getter, value);
    }

    public boolean containsKey(Method method) {
        return values.containsKey(method);
    }

    @Override
    public final boolean equals(Object o) {
        return o instanceof LazyValueMap that && values.equals(that.values);
    }

    @Override
    public int hashCode() {
        return values.hashCode();
    }

    sealed interface Resolvable permits LazyValue, NullValue {
        Object get();
    }

    private static final class NullValue implements Resolvable {
        private static final NullValue INSTANCE = new NullValue();

        static NullValue of() {
            return INSTANCE;
        }

        @Override
        public Object get() {
            return null;
        }
    }

    /// 判断是否存在非 null 的值。
    /// 用于非根级代理构造时判断是否应创建代理对象：当所有值均为 null 时返回 null 而非空代理。
    public boolean hasNonNullValue() {
        for (Object value : values.values()) {
            if (value != null && value != NullValue.of()) {
                return true;
            }
        }
        return false;
    }
}
