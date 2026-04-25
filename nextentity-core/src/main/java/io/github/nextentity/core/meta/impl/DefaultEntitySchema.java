package io.github.nextentity.core.meta.impl;

import io.github.nextentity.core.exception.ConfigurationException;
import io.github.nextentity.core.meta.*;
import io.github.nextentity.core.reflect.schema.Attribute;
import io.github.nextentity.core.reflect.schema.SchemaAttribute;
import io.github.nextentity.core.reflect.schema.impl.AbstractSchema;
import io.github.nextentity.core.reflect.schema.impl.DefaultSchema;
import io.github.nextentity.core.util.Lazy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认实体类型实现，实现 EntityType 接口。
 * 提供实体元数据的核心实现，包括属性、表名、版本字段、投影等。
 */
public class DefaultEntitySchema extends AbstractSchema<EntityAttributeSet, EntityAttribute> implements EntityType {

    private static final Logger log = LoggerFactory.getLogger(DefaultEntitySchema.class);

    protected final DefaultMetamodel metamodel;
    protected final MetamodelResolver resolver;
    protected final String tableName;
    protected final String entityName;
    protected final Map<Class<?>, ProjectionSchema> projections = new ConcurrentHashMap<>();

    protected DefaultEntitySchema(Class<?> type, DefaultMetamodel metamodel) {
        super(type);
        this.metamodel = metamodel;
        this.resolver = metamodel.getResolver();
        this.tableName = resolver.getTableName(type);
        this.entityName = resolver.getEntityName(type);
    }

    public static DefaultEntitySchema of(Class<?> type, DefaultMetamodel metamodel) {
        return metamodel.getDefaultEntitySchema(type);
    }

    @Override
    public EntityBasicAttribute id() {
        return attributesSupplier.get().id();
    }

    @Override
    public String tableName() {
        return tableName;
    }

    @Override
    public String entityName() {
        return entityName;
    }

    @Override
    public EntityBasicAttribute version() {
        return attributesSupplier.get().version();
    }

    @Override
    public EntityAttribute getAttribute(Iterable<String> fieldNames) {
        return (EntityAttribute) super.getAttribute(fieldNames);
    }

    @Override
    public ProjectionSchema getProjection(Class<?> type) {
        return projections.computeIfAbsent(type,
                t -> new DefaultProjectionSchema(this, t, metamodel));
    }

    @Override
    protected EntityAttributeSet createAttributes() {
        DefaultSchema javaSchema = DefaultSchema.of(type);
        EntityBasicAttribute idAttribute = null;
        EntityBasicAttribute versionAttribute = null;
        boolean hasVersion = false;
        ArrayList<EntityAttribute> attributes = new ArrayList<>();
        for (Attribute attribute : javaSchema.getAttributes()) {
            if (resolver.isTransient(attribute)) {
                continue;
            }
            if (attribute instanceof SchemaAttribute schemaAttribute
                && resolver.isAnyToOne(schemaAttribute)) {
                var entitySchemaAttribute = new DefaultEntitySchemaAttribute(
                        schemaAttribute, this, metamodel);
                attributes.add(entitySchemaAttribute);
            } else if (resolver.isBasicField(attribute)) {
                boolean versionField = false;
                if (resolver.isVersionField(attribute)) {
                    if (hasVersion) {
                        log.warn("duplicate attributes: {}, ignored", attribute.name());
                        continue;
                    }
                    hasVersion = true;
                    versionField = true;
                }
                var entityAttribute = new DefaultEntityBasicAttribute(
                        attribute, this, resolver);
                attributes.add(entityAttribute);
                if (resolver.isMarkedId(attribute)) {
                    idAttribute = entityAttribute;
                }
                if (versionField) {
                    versionAttribute = entityAttribute;
                }
            } else {
                log.warn("ignored attribute {}", attribute.field());
            }
        }
        if (idAttribute == null) {
            EntityBasicAttribute found = null;
            for (EntityAttribute i : attributes) {
                if (i.name().equals("id") && i instanceof EntityBasicAttribute eba) {
                    found = eba;
                    break;
                }
            }
            idAttribute = found;
            if (idAttribute == null) {
                throw new ConfigurationException(
                        "No ID attribute found for entity '" + entityName + "' (" + type.getName() + "). " +
                        "Please annotate the ID field with @Id or ensure a field named 'id' exists.");
            }
        }
        return new EntityAttributeSet(attributes, idAttribute, versionAttribute);
    }

    @Override
    protected Lazy<? extends EntityAttributeSet> getAttributesSupplier() {
        return super.getAttributesSupplier();
    }
}