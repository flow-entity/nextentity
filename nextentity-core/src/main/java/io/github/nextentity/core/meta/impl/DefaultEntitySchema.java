package io.github.nextentity.core.meta.impl;

import io.github.nextentity.core.exception.ConfigurationException;
import io.github.nextentity.core.meta.*;
import io.github.nextentity.core.reflect.schema.impl.DefaultAccessor;
import io.github.nextentity.core.util.Lazy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认实体类型实现，实现 EntityType 接口。
 * 提供实体元数据的核心实现，包括属性、表名、版本字段、投影等。
 */
public class DefaultEntitySchema extends AbstractMetamodelSchema<EntityAttribute> implements EntityType {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DefaultEntitySchema.class);

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
        return attributesSupplier.get() instanceof EntityAttributeSet entityAttributeSet ? entityAttributeSet.id() : null;
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
        return attributesSupplier.get() instanceof EntityAttributeSet eas ? eas.version() : null;
    }

    @Override
    public EntityAttribute getAttribute(Iterable<String> fieldNames) {
        return super.getAttribute(fieldNames);
    }

    @Override
    public EntityAttribute getAttribute(String name) {
        return super.getAttribute(name);
    }

    @Override
    public ProjectionSchema getProjection(Class<?> type) {
        return projections.computeIfAbsent(type,
                t -> new DefaultProjectionSchema(this, t, metamodel));
    }

    @Override
    protected AttributeSet<EntityAttribute> createAttributes() {
        List<DefaultAccessor> accessors = DefaultAccessor.of(type);
        EntityBasicAttribute idAttribute = null;
        EntityBasicAttribute versionAttribute = null;
        boolean hasVersion = false;
        ArrayList<EntityAttribute> attributes = new ArrayList<>();
        for (DefaultAccessor accessor : accessors) {
            if (resolver.isTransient(accessor)) {
                continue;
            }
            boolean isComplexType = !DefaultAccessor.of(accessor.type()).isEmpty();
            DefaultMetamodelAttribute attr = new DefaultMetamodelAttribute(this, accessor);
            if (isComplexType && (resolver.isAnyToOne(attr) || resolver.isEmbedded(accessor))) {
                if (resolver.isEmbedded(accessor)) {
                    var embeddedAttribute = new DefaultEmbeddedAttribute(attr, this, metamodel);
                    attributes.add(embeddedAttribute);
                } else {
                    var entitySchemaAttribute = new DefaultEntitySchemaAttribute(
                            attr, this, metamodel);
                    attributes.add(entitySchemaAttribute);
                }
            } else if (resolver.isBasicField(accessor)) {
                boolean versionField = false;
                if (resolver.isVersionField(accessor)) {
                    if (hasVersion) {
                        log.warn("duplicate attributes: {}, ignored", accessor.name());
                        continue;
                    }
                    hasVersion = true;
                    versionField = true;
                }
                var entityAttribute = new DefaultEntityBasicAttribute(
                        attr, this, resolver);
                attributes.add(entityAttribute);
                if (resolver.isMarkedId(accessor)) {
                    idAttribute = entityAttribute;
                }
                if (versionField) {
                    versionAttribute = entityAttribute;
                }
            } else {
                log.warn("ignored attribute {}", accessor.field());
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
            if (idAttribute == null && !(this instanceof EmbeddedAttribute)) {
                throw new ConfigurationException(
                        "No ID attribute found for entity '" + entityName + "' (" + type.getName() + "). " +
                        "Please annotate the ID field with @Id or ensure a field named 'id' exists.");
            }
        }
        return new EntityAttributeSet(attributes, idAttribute, versionAttribute);
    }

    @Override
    protected Lazy<? extends AttributeSet<EntityAttribute>> getAttributesSupplier() {
        return attributesSupplier;
    }

    /// 获取当前实体/嵌入类型上的 @AttributeOverride / @AttributeOverrides 映射。
    protected Map<String, String> getAttributeOverrides() {
        return resolver.getAttributeOverrides(type());
    }
}
