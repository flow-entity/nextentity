package io.github.nextentity.core.reflect;

import java.util.Objects;
import java.util.function.Function;

/// 懒加载属性加载器
public final class LazyValue {
    private volatile Function<Object, Object> loader;
    private final Object identifier;
    private Object value;


    public LazyValue(Function<Object, Object> loader, Object identifier) {
        this.loader = loader;
        this.identifier = identifier;
    }

    public Object get() {
        preventReentry();
        if (loader != null) {
            synchronized (this) {
                if (loader != null) {
                    value = loader.apply(identifier);
                    loader = null;
                }
            }
        }
        return value;
    }

    private void preventReentry() {
        if (Thread.holdsLock(this)) {
            throw new IllegalStateException("Recursive invocation of a LazyConstant's computing function: " + loader);
        }
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof LazyValue lazyValue && Objects.equals(identifier, lazyValue.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(identifier);
    }
}