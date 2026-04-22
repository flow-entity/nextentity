package io.github.nextentity.core.meta.impl;

import io.github.nextentity.core.meta.*;
import io.github.nextentity.core.reflect.schema.Attribute;
import io.github.nextentity.core.reflect.schema.Schema;
import io.github.nextentity.core.reflect.schema.SchemaAttribute;
import io.github.nextentity.core.reflect.schema.impl.AbstractSchema;
import io.github.nextentity.core.reflect.schema.impl.AttributeSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class DefaultProjectionSchema
        extends AbstractSchema<AttributeSet<ProjectionAttribute>, ProjectionAttribute>
        implements ProjectionSchema {

    private static final Logger log = LoggerFactory.getLogger(DefaultProjectionSchema.class);
    protected final EntitySchema entitySchema;
    protected final DefaultMetamodel metamodel;
    protected final MetamodelResolver resolver;

    public DefaultProjectionSchema(EntitySchema entitySchema,
                                   Class<?> type,
                                   DefaultMetamodel metamodel) {
        super(type);
        this.entitySchema = entitySchema;
        this.metamodel = metamodel;
        this.resolver = metamodel.getResolver();
    }

    @Override
    protected AttributeSet<ProjectionAttribute> createAttributes() {
        Schema beanSchema = Schema.of(type);
        List<ProjectionAttribute> attributes = new ArrayList<>();
        int ordinal = 0;
        for (Attribute attribute : beanSchema.getAttributes()) {
            Class<?> joinTarget = resolver.getProjectionJoinTarget(attribute);
            if (joinTarget != null) {
                if (!(attribute instanceof SchemaAttribute)) {
                    log.warn("Attribute '{}' in projection '{}' is not a SchemaAttribute, skipped join mapping",
                            attribute.name(), type.getSimpleName());
                    continue;
                }
                String sourceName = resolver.getProjectionJoinSourceAttribute(attribute);
                String targetName = resolver.getProjectionJoinTargetAttribute(attribute);
                if (sourceName == null || targetName == null) {
                    log.warn("Projection join '{}' in '{}' is missing source/target attribute, skipped",
                            attribute.name(), type.getSimpleName());
                    continue;
                }
                EntityBasicAttribute sourceAttribute = (EntityBasicAttribute) entitySchema.getAttribute(sourceName);
                EntityType targetEntitySchema = metamodel.getEntity(joinTarget);
                EntityBasicAttribute targetAttribute = (EntityBasicAttribute) targetEntitySchema.getAttribute(targetName);
                var attr = new DefaultProjectionJoinAttribute(this,
                        sourceAttribute,
                        targetAttribute,
                        targetEntitySchema,
                        metamodel,
                        ordinal++,
                        resolver.getFetchType(attribute),
                        attribute);
                attributes.add(attr);
                continue;
            }
            Iterable<String> path = resolver.getMappedEntityPath(attribute);
            if (path != null) {
                EntityAttribute entityAttribute = entitySchema.getAttribute(path);
                switch (entityAttribute) {
                    case EntitySchemaAttribute source -> {
                        if (!(attribute instanceof SchemaAttribute bsa)) {
                            log.warn("Attribute '{}' in projection '{}' is not a SchemaAttribute but maps to EntitySchemaAttribute, skipped",
                                    attribute.name(), type.getSimpleName());
                            continue;
                        }
                        if (resolver.matchProjectionSchemaAttribute(source, bsa)) {
                            DefaultProjectionSchema declareBy = this;
                            var attr = new DefaultProjectionSchemaAttribute(declareBy, source, bsa, metamodel, ordinal++);
                            attributes.add(attr);
                        }
                    }
                    case EntityBasicAttribute eba -> {
                        if (attribute instanceof SchemaAttribute) {
                            log.warn("Attribute '{}' in projection '{}' is a SchemaAttribute but maps to EntityBasicAttribute, expected simple Attribute, skipped",
                                    attribute.name(), type.getSimpleName());
                            continue;
                        }
                        if (resolver.matchProjectionBasicAttribute(eba, attribute)) {
                            DefaultProjectionSchema declareBy = this;
                            var attr = new DefaultProjectionBasicAttribute(declareBy, eba, attribute, ordinal++);
                            attributes.add(attr);
                        }
                    }
                    case null ->
                            log.warn("No attribute found for attribute {} in entity schema {}", attribute, entitySchema);
                    default -> {
                    }
                }
            }
        }
        return new AttributeSet<>(attributes);
    }

    @Override
    public ProjectionSchemaAttribute getAttribute(Iterable<String> fieldNames) {
        return (ProjectionSchemaAttribute) super.getAttribute(fieldNames);
    }

    @Override
    public EntitySchema getEntitySchema() {
        return entitySchema;
    }
}
