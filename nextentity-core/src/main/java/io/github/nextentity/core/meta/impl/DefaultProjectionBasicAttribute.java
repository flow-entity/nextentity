package io.github.nextentity.core.meta.impl;

import io.github.nextentity.core.meta.EntityBasicAttribute;
import io.github.nextentity.core.meta.ProjectionBasicAttribute;
import io.github.nextentity.core.reflect.schema.Attribute;
import io.github.nextentity.core.reflect.schema.impl.DefaultAttribute;

public class DefaultProjectionBasicAttribute
        extends DefaultAttribute
        implements ProjectionBasicAttribute {

    private final EntityBasicAttribute source;

    public DefaultProjectionBasicAttribute(DefaultProjectionSchema declareBy,
                                           EntityBasicAttribute source,
                                           Attribute attribute) {
        super(declareBy, attribute);
        this.source = source;
    }

    @Override
    public EntityBasicAttribute getEntityAttribute() {
        return source;
    }

    @Override
    public DefaultProjectionSchema declareBy() {
        return (DefaultProjectionSchema) super.declareBy();
    }
}
