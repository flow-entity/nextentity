package io.github.nextentity.core.meta.impl;

import io.github.nextentity.core.meta.*;
import io.github.nextentity.core.reflect.schema.Accessor;
import io.github.nextentity.core.reflect.schema.Attribute;
import io.github.nextentity.core.reflect.schema.Schema;
import io.github.nextentity.core.reflect.schema.SchemaAttribute;
import io.github.nextentity.core.reflect.schema.impl.AttributeSet;
import io.github.nextentity.core.reflect.schema.impl.DefaultAttribute;
import io.github.nextentity.core.util.ImmutableArray;
import jakarta.persistence.FetchType;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class DefaultProjectionSchemaAttribute
        extends DefaultProjectionSchema
        implements ProjectionSchemaAttribute {

    private final DefaultProjectionSchema declareBy;
    private final Attribute attribute;
    private final EntitySchemaAttribute source;

    public DefaultProjectionSchemaAttribute(DefaultProjectionSchema declareBy,
                                            EntitySchemaAttribute source,
                                            SchemaAttribute attribute,
                                            DefaultMetamodel metamodel,
                                            int ordinal) {
        super(source.declareBy(), attribute.type(), metamodel);
        this.declareBy = declareBy;
        this.attribute = new DefaultAttribute(declareBy, attribute, ordinal);
        this.source = source;
    }

    @Override
    protected AttributeSet<ProjectionAttribute> createAttributes() {
        DefaultEntitySchema entity = (DefaultEntitySchemaAttribute) source;
        ProjectionSchema projection = entity.getProjection(attribute.type());
        ImmutableArray<? extends ProjectionAttribute> attributes = projection.getAttributes();
        ArrayList<ProjectionAttribute> result = new ArrayList<>(attributes.size());
        AtomicInteger ordinal = new AtomicInteger();
        for (ProjectionAttribute projectionAttribute : attributes) {
            var item = ProjectionAttributeFactory.createAttribute(
                    this,
                    projectionAttribute.getEntityAttribute(),
                    projectionAttribute,
                    metamodel,
                    ordinal);
            result.add(item);
        }
        return new AttributeSet<>(result);
    }

    @Override
    public EntitySchemaAttribute getEntityAttribute() {
        return source;
    }

    @Override
    public Accessor accessor() {
        return attribute.accessor();
    }

    @Override
    public Schema declareBy() {
        return declareBy;
    }

    @Override
    public ImmutableArray<String> path() {
        return attribute.path();
    }

    @Override
    public int ordinal() {
        return attribute.ordinal();
    }

    @Override
    public EntityType getTargetEntityType() {
        return source.getTargetEntityType();
    }

    @Override
    public EntityBasicAttribute getSourceAttribute() {
        return source.getSourceAttribute();
    }

    @Override
    public EntityBasicAttribute getTargetAttribute() {
        return source.getTargetAttribute();
    }

    @Override
    public FetchType getFetchType() {
        // 优先级：投影级 @Fetch > source().fetchType() > 全局默认
        FetchType projectionFetch = resolver.getFetchType(attribute);
        if (projectionFetch != null) {
            return projectionFetch;
        }
        return getEntityAttribute().getFetchType();
    }
}
