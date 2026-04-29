package io.github.nextentity.core.meta.impl;

import io.github.nextentity.core.expression.PathNode;
import io.github.nextentity.core.meta.*;
import io.github.nextentity.core.reflect.schema.Accessor;
import io.github.nextentity.core.util.ImmutableArray;
import jakarta.persistence.FetchType;

import java.util.ArrayList;

public class DefaultProjectionSchemaAttribute
        extends DefaultProjectionSchema
        implements ProjectionSchemaAttribute {

    private final DefaultProjectionSchema declareBy;
    private final EntitySchemaAttribute source;

    private final Accessor accessor;
    private final PathNode path;
    private final FetchType fetchType;

    public DefaultProjectionSchemaAttribute(DefaultProjectionSchema declareBy,
                                            EntitySchemaAttribute source,
                                            MetamodelAttribute attribute,
                                            DefaultMetamodel metamodel) {
        super(source.declareBy(), attribute.type(), metamodel);
        this.declareBy = declareBy;
        this.source = source;
        this.accessor = attribute.accessor();
        this.path = declareBy.getPath(attribute.name());
        this.fetchType = resolver.getFetchType(this);
    }

    @Override
    protected AttributeSet<ProjectionAttribute> createAttributes() {
        DefaultEntitySchema entity = (DefaultEntitySchemaAttribute) source;
        ProjectionSchema projection = entity.getProjection(accessor.type());
        ImmutableArray<? extends ProjectionAttribute> attributes = projection.getAttributes();
        ArrayList<ProjectionAttribute> result = new ArrayList<>(attributes.size());
        for (ProjectionAttribute projectionAttribute : attributes) {
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
    public EntitySchemaAttribute getEntityAttribute() {
        return source;
    }

    @Override
    public Accessor accessor() {
        return accessor;
    }

    @Override
    public ProjectionSchema declareBy() {
        return declareBy;
    }

    @Override
    public PathNode path() {
        return path;
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
        return fetchType;
    }

    @Override
    public ProjectionSchema schema() {
        return this;
    }
}
