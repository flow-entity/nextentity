package io.github.nextentity.core.util;

import java.util.Objects;
import java.util.function.Supplier;

public class Lazy<T> {
    private volatile T instance;
    private volatile Supplier<T> supplier;

    public Lazy(Supplier<T> supplier) {
        this.supplier = Objects.requireNonNull(supplier);
    }

    public T get() {
        if (supplier != null) {
            synchronized (this) {
                if (supplier != null) {
                    instance = supplier.get();
                    supplier = null;
                }
            }
        }
        return instance;
    }
}
