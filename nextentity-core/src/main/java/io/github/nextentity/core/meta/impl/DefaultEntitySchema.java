package io.github.nextentity.core.meta.impl;

import io.github.nextentity.core.exception.ConfigurationException;
import io.github.nextentity.core.meta.*;
import io.github.nextentity.core.reflect.schema.Accessor;
import io.github.nextentity.core.reflect.schema.impl.DefaultAccessor;
import io.github.nextentity.core.util.Lazy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/// 默认实体类型实现，实现 EntityType 接口。
/// 提供实体元数据的核心实现，包括属性、表名、版本字段、投影等。
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
                    var embeddedAttribute = new DefaultEntityEmbeddedAttribute(attr, this, metamodel);
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
            if (idAttribute == null && !(this instanceof EntityEmbeddedAttribute)) {
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

    /// 合并当前字段的 @AttributeOverride 与父级传递的点号路径覆盖。
    ///
    /// 如父级的 {@code address.street} 在当前层级为 {@code address} 时，
    /// 截取前缀后变为 {@code street}。
    ///
    /// @param resolver  元模型解析器
    /// @param accessor  当前字段的访问器
    /// @param declareBy 声明当前字段的父级 Schema
    /// @param name      当前字段名称
    /// @return 合并后的字段名 → 列名映射
    protected static Map<String, String> mergeAttributeOverrides(
            MetamodelResolver resolver,
            Accessor accessor,
            DefaultEntitySchema declareBy,
            String name) {
        Map<String, String> overrides = resolver.getAttributeOverrides(accessor);
        Map<String, String> parent = declareBy.getAttributeOverrides();
        for (Map.Entry<String, String> entry : parent.entrySet()) {
            String[] split = entry.getKey().split("\\.");
            if (split.length > 1 && split[0].equals(name)) {
                String key = Arrays.stream(split).skip(1)
                        .collect(Collectors.joining("."));
                overrides.put(key, entry.getValue());
            }
        }
        return overrides;
    }

}
