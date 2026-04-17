package io.github.nextentity.core.meta.impl;

import io.github.nextentity.core.exception.ConfigurationException;
import io.github.nextentity.core.meta.*;
import io.github.nextentity.core.reflect.schema.Accessor;
import io.github.nextentity.core.reflect.schema.Attribute;
import io.github.nextentity.core.reflect.schema.Schema;
import io.github.nextentity.core.reflect.schema.SchemaAttribute;
import io.github.nextentity.core.reflect.schema.impl.AttributeSet;
import io.github.nextentity.core.reflect.schema.impl.DefaultAttribute;
import io.github.nextentity.core.util.ImmutableArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class DefaultProjectionSchemaAttribute
        extends DefaultProjectionSchema
        implements ProjectionSchemaAttribute {


    private static final Logger log = LoggerFactory.getLogger(DefaultProjectionSchemaAttribute.class);
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
        int ordinal = 0;
        for (ProjectionAttribute projectionAttribute : attributes) {
            ProjectionAttribute cur;
            if (projectionAttribute instanceof ProjectionBasicAttribute basicAttribute) {
                cur = new DefaultProjectionBasicAttribute(this, basicAttribute.source(), basicAttribute, ordinal++);
            } else if (projectionAttribute instanceof ProjectionSchemaAttribute schemaAttribute) {
                cur = new DefaultProjectionSchemaAttribute(this, schemaAttribute.source(), schemaAttribute, metamodel, ordinal++);
            } else {
                throw new ConfigurationException(
                        "Unknown projection attribute type '" + projectionAttribute.getClass().getName() +
                        "' when resolving projection attribute '" + path() +
                        "' from source path '" + source.path() + "'.");
            }
            result.add(cur);
        }
        return new AttributeSet<>(result);
    }

    @Override
    public EntitySchemaAttribute source() {
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
}
