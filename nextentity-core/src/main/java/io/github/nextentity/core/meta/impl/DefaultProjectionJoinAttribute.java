package io.github.nextentity.core.meta.impl;

import io.github.nextentity.core.exception.ConfigurationException;
import io.github.nextentity.core.meta.*;
import io.github.nextentity.core.reflect.schema.Accessor;
import io.github.nextentity.core.reflect.schema.Attribute;
import io.github.nextentity.core.reflect.schema.Schema;
import io.github.nextentity.core.reflect.schema.impl.AttributeSet;
import io.github.nextentity.core.reflect.schema.impl.DefaultAttribute;
import io.github.nextentity.core.util.ImmutableArray;
import jakarta.persistence.FetchType;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class DefaultProjectionJoinAttribute
        extends DefaultProjectionSchema
        implements ProjectionJoinAttribute {

    private final DefaultProjectionSchema declareBy;
    private final DefaultAttribute attribute;
    private final EntityBasicAttribute sourceAttribute;
    private final EntityBasicAttribute targetAttribute;
    private final MetamodelSchema<?> target;
    private final FetchType fetchType;

    public DefaultProjectionJoinAttribute(DefaultProjectionSchema declareBy,
                                          EntityBasicAttribute sourceAttribute,
                                          EntityBasicAttribute targetAttribute,
                                          MetamodelSchema<?> target,
                                          DefaultMetamodel metamodel,
                                          int ordinal,
                                          FetchType fetchType,
                                          Attribute attribute) {
        super(declareBy.getEntitySchema(), attribute.type(), metamodel);
        this.declareBy = declareBy;
        this.attribute = new DefaultAttribute(declareBy, attribute, ordinal);
        this.sourceAttribute = sourceAttribute;
        this.targetAttribute = targetAttribute;
        this.target = target;
        this.fetchType = fetchType == null ? FetchType.EAGER : fetchType;
    }

    @Override
    protected AttributeSet<ProjectionAttribute> createAttributes() {
        ProjectionSchema projection;
        if (target instanceof EntityType entityType) {
            projection = entityType.getProjection(type());
        } else if (target instanceof ProjectionSchema schema) {
            projection = schema;
        } else {
            throw new ConfigurationException(
                    "Unsupported target type for projection join attribute: " +
                    target.getClass().getName() + ", expected EntityType or ProjectionSchema");
        }
        ArrayList<ProjectionAttribute> result = new ArrayList<>();
        AtomicInteger ordinal = new AtomicInteger();
        for (ProjectionAttribute projectionAttribute : projection.getAttributes()) {
            var item = ProjectionAttributeFactory.createAttribute(
                    this,
                    projectionAttribute.source(),
                    projectionAttribute,
                    metamodel,
                    ordinal);
            result.add(item);
        }
        return new AttributeSet<>(result);
    }

    @Override
    public EntityAttribute source() {
        return sourceAttribute;
    }

    @Override
    public MetamodelSchema<?> target() {
        return target;
    }

    @Override
    public EntityBasicAttribute sourceAttribute() {
        return sourceAttribute;
    }

    @Override
    public EntityBasicAttribute targetAttribute() {
        return targetAttribute;
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
    public FetchType fetchType() {
        return fetchType;
    }
}
