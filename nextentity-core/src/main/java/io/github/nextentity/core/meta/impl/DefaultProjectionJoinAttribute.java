package io.github.nextentity.core.meta.impl;

import io.github.nextentity.core.meta.*;
import io.github.nextentity.core.reflect.schema.Accessor;
import io.github.nextentity.core.reflect.schema.Attribute;
import io.github.nextentity.core.reflect.schema.impl.AttributeSet;
import io.github.nextentity.core.reflect.schema.impl.DefaultAttribute;
import io.github.nextentity.core.util.ImmutableArray;
import jakarta.persistence.FetchType;

import java.util.ArrayList;

public class DefaultProjectionJoinAttribute
        extends DefaultProjectionSchema
        implements ProjectionJoinAttribute {

    private final DefaultProjectionSchema declareBy;
    private final DefaultAttribute attribute;
    private final EntityBasicAttribute sourceAttribute;
    private final EntityBasicAttribute targetAttribute;
    private final EntityType target;
    private final FetchType fetchType;

    public DefaultProjectionJoinAttribute(DefaultProjectionSchema declareBy,
                                          EntityBasicAttribute sourceAttribute,
                                          EntityBasicAttribute targetAttribute,
                                          EntityType target,
                                          DefaultMetamodel metamodel,
                                          FetchType fetchType,
                                          Attribute attribute) {
        super(declareBy.getEntitySchema(), attribute.type(), metamodel);
        this.declareBy = declareBy;
        this.attribute = new DefaultAttribute(declareBy, attribute);
        this.sourceAttribute = sourceAttribute;
        this.targetAttribute = targetAttribute;
        this.target = target;
        this.fetchType = fetchType == null ? FetchType.EAGER : fetchType;
    }

    @Override
    protected AttributeSet<ProjectionAttribute> createAttributes() {
        ProjectionSchema projection = target.getProjection(type());
        ArrayList<ProjectionAttribute> result = new ArrayList<>();
        for (ProjectionAttribute projectionAttribute : projection.getAttributes()) {
            var item = ProjectionAttributeFactory.createAttribute(
                    this,
                    projectionAttribute.getEntityAttribute(),
                    projectionAttribute,
                    metamodel
            );
            result.add(item);
        }
        return new AttributeSet<>(result);
    }

    @Override
    public EntityAttribute getEntityAttribute() {
        return sourceAttribute;
    }

    @Override
    public EntityType getTargetEntityType() {
        return target;
    }

    @Override
    public EntityBasicAttribute getSourceAttribute() {
        return sourceAttribute;
    }

    @Override
    public EntityBasicAttribute getTargetAttribute() {
        return targetAttribute;
    }

    @Override
    public Accessor accessor() {
        return attribute.accessor();
    }

    @Override
    public ProjectionSchema declareBy() {
        return declareBy;
    }

    @Override
    public ImmutableArray<String> path() {
        return attribute.path();
    }

    @Override
    public FetchType getFetchType() {
        return fetchType;
    }
}
