package io.github.nextentity.core.reflect;

import java.util.function.Supplier;

public interface BatchAttributeLoader {
    AttributeLoader addForeignKey(Object foreignKey);
}
