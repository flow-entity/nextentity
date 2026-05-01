package io.github.nextentity.core.meta.impl;

import io.github.nextentity.core.meta.*;
import io.github.nextentity.core.reflect.schema.impl.DefaultAccessor;

import java.util.ArrayList;
import java.util.List;

public class DefaultProjectionSchema
        extends AbstractMetamodelSchema<ProjectionAttribute>
        implements ProjectionSchema {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DefaultProjectionSchema.class);
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
        List<DefaultAccessor> accessors = DefaultAccessor.of(type);
        List<ProjectionAttribute> attributes = new ArrayList<>();
        for (DefaultAccessor accessor : accessors) {
            Class<?> joinTarget = resolver.getProjectionJoinTarget(accessor);
            if (joinTarget != null) {
                boolean isComplexType = !DefaultAccessor.of(accessor.type()).isEmpty();
                if (!isComplexType) {
                    log.warn("Attribute '{}' in projection '{}' is not a complex type, skipped join mapping",
                            accessor.name(), type.getSimpleName());
                    continue;
                }
                String sourceName = resolver.getProjectionJoinSourceAttribute(accessor);
                String targetName = resolver.getProjectionJoinTargetAttribute(accessor);
                if (sourceName == null || targetName == null) {
                    log.warn("Projection join '{}' in '{}' is missing source/target attribute, skipped",
                            accessor.name(), type.getSimpleName());
                    continue;
                }
                EntityBasicAttribute sourceAttribute = (EntityBasicAttribute) entitySchema.getAttribute(sourceName);
                EntityType targetEntitySchema = metamodel.getEntity(joinTarget);
                EntityBasicAttribute targetAttribute = (EntityBasicAttribute) targetEntitySchema.getAttribute(targetName);
                DefaultMetamodelAttribute attr = new DefaultMetamodelAttribute(this, accessor);
                var projAttr = new DefaultProjectionJoinAttribute(this,
                        sourceAttribute,
                        targetAttribute,
                        targetEntitySchema,
                        metamodel,
                        attr);
                attributes.add(projAttr);
                continue;
            }
            Iterable<String> path = resolver.getMappedEntityPath(accessor);
            if (path != null) {
                EntityAttribute entityAttribute = entitySchema.getAttribute(path);
                switch (entityAttribute) {
                    case EntitySchemaAttribute source -> {
                        boolean isComplexType = !DefaultAccessor.of(accessor.type()).isEmpty();
                        if (!isComplexType) {
                            log.warn("Attribute '{}' in projection '{}' is not a complex type but maps to EntitySchemaAttribute, skipped",
                                    accessor.name(), type.getSimpleName());
                            continue;
                        }
                        DefaultMetamodelAttribute attr = new DefaultMetamodelAttribute(this, accessor);
                        if (resolver.matchProjectionSchemaAttribute(source, attr)) {
                            var projAttr = new DefaultProjectionSchemaAttribute(this, source, attr, metamodel);
                            attributes.add(projAttr);
                        }
                    }
                    case EntityBasicAttribute eba -> {
                        boolean isComplexType = !DefaultAccessor.of(accessor.type()).isEmpty();
                        if (isComplexType) {
                            log.warn("Attribute '{}' in projection '{}' is a complex type but maps to EntityBasicAttribute, expected simple type, skipped",
                                    accessor.name(), type.getSimpleName());
                            continue;
                        }
                        DefaultMetamodelAttribute attr = new DefaultMetamodelAttribute(this, accessor);
                        if (resolver.matchProjectionBasicAttribute(eba, attr)) {
                            var projAttr = new DefaultProjectionBasicAttribute(this, eba, attr);
                            attributes.add(projAttr);
                        }
                    }
                    case null ->
                            log.warn("No attribute found for path {} in entity schema {}", path, entitySchema);
                    default -> {
                    }
                }
            }
        }
        return new AttributeSet<>(attributes);
    }

    @Override
    public ProjectionAttribute getAttribute(Iterable<String> fieldNames) {
        return super.getAttribute(fieldNames);
    }

    @Override
    public ProjectionAttribute getAttribute(String name) {
        return super.getAttribute(name);
    }

    @Override
    public EntitySchema getEntitySchema() {
        return entitySchema;
    }
}
