package io.github.nextentity.core.reflect;

import java.util.function.Supplier;

public interface BatchAttributeLoader {
    Supplier<Object> addForeignKey(AttributeLoader foreignKey);
}
