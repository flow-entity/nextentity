package io.github.nextentity.core.meta;

import io.github.nextentity.core.reflect.schema.Attributes;

public class SimpleProjection implements ProjectionType {

    private final Class<?> type;
    private final EntitySchema entityType;
    private Attributes attributes;

    public SimpleProjection(Class<?> type, EntitySchema entityType) {
        this.type = type;
        this.entityType = entityType;
    }

    @Override
    public EntitySchema source() {
        return entityType;
    }

    @Override
    public Attributes attributes() {
        return attributes;
    }

    @Override
    public Class<?> type() {
        return type;
    }

    public void setAttributes(Attributes attributes) {
        this.attributes = attributes;
    }


}
