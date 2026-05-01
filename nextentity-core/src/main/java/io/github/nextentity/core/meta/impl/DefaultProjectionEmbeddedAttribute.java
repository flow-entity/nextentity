package io.github.nextentity.core.meta.impl;

import io.github.nextentity.core.expression.PathNode;
import io.github.nextentity.core.meta.*;
import io.github.nextentity.core.reflect.schema.Accessor;
import io.github.nextentity.core.util.ImmutableArray;

import java.util.ArrayList;

public class DefaultProjectionEmbeddedAttribute
        extends DefaultProjectionSchema
        implements ProjectionEmbeddedAttribute {

    private final DefaultProjectionSchema declareBy;
    private final EntityEmbeddedAttribute source;
    private final Accessor accessor;
    private final PathNode path;

    public DefaultProjectionEmbeddedAttribute(DefaultProjectionSchema declareBy,
                                              EntityEmbeddedAttribute source,
                                              MetamodelAttribute attribute,
                                              DefaultMetamodel metamodel) {
        super(source.schema(), attribute.type(), metamodel);
        this.declareBy = declareBy;
        this.source = source;
        this.accessor = attribute.accessor();
        this.path = declareBy.getPath(attribute.name());
    }

    @Override
    protected AttributeSet<ProjectionAttribute> createAttributes() {
        ProjectionSchema projection = new DefaultProjectionSchema(source.schema(), accessor.type(), metamodel);
        ImmutableArray<? extends ProjectionAttribute> attributes = projection.getAttributes();
        ArrayList<ProjectionAttribute> result = new ArrayList<>(attributes.size());
        for (ProjectionAttribute projectionAttribute : attributes) {
            var item = ProjectionAttributeFactory.createAttribute(
                    this,
                    projectionAttribute,
                    metamodel
            );
            result.add(item);
        }
        return new AttributeSet<>(result);
    }

    @Override
    public EntityEmbeddedAttribute getEntityAttribute() {
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
    public ProjectionSchema schema() {
        return this;
    }
}
