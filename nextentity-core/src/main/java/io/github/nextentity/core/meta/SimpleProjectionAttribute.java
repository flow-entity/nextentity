package io.github.nextentity.core.meta;

import io.github.nextentity.core.reflect.schema.SimpleAttribute;

public class SimpleProjectionAttribute extends SimpleAttribute implements ProjectionAttribute {
    private final EntityAttribute sourceAttribute;

    public SimpleProjectionAttribute(EntityAttribute sourceAttribute) {
        this.sourceAttribute = sourceAttribute;
    }

    @Override
    public EntityAttribute source() {
        return sourceAttribute;
    }

    @Override
    public boolean isUpdatable() {
        return sourceAttribute.isUpdatable();
    }
}
