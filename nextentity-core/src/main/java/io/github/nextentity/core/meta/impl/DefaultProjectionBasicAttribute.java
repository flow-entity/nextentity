package io.github.nextentity.core.meta.impl;

import io.github.nextentity.core.meta.EntityBasicAttribute;
import io.github.nextentity.core.meta.MetamodelAttribute;
import io.github.nextentity.core.meta.ProjectionBasicAttribute;

public class DefaultProjectionBasicAttribute
        extends DefaultMetamodelAttribute
        implements ProjectionBasicAttribute {

    private final EntityBasicAttribute source;

    public DefaultProjectionBasicAttribute(DefaultProjectionSchema declareBy,
                                           EntityBasicAttribute source,
                                           MetamodelAttribute attribute) {
        super(declareBy, attribute.accessor());
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
