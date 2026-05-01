package io.github.nextentity.core.meta.impl;

import io.github.nextentity.core.TypeCastUtil;
import io.github.nextentity.core.exception.ConfigurationException;
import io.github.nextentity.core.expression.PathNode;
import io.github.nextentity.core.meta.*;
import io.github.nextentity.core.reflect.schema.Accessor;
import io.github.nextentity.core.util.ImmutableArray;
import io.github.nextentity.core.util.Lazy;
import jakarta.persistence.FetchType;

import java.util.*;
import java.util.stream.Collectors;

public class DefaultEntitySchemaAttribute
        extends DefaultEntitySchema
        implements EntitySchemaAttribute {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DefaultEntitySchemaAttribute.class);

    private final Accessor accessor;
    private final DefaultEntitySchema declareBy;
    private final PathNode path;
    private final FetchType fetchType;

    protected static class Attributes extends EntityAttributeSet {
        private final EntityBasicAttribute sourceAttribute;
        private final EntityBasicAttribute targetAttribute;

        public Attributes(Collection<EntityAttribute> attributes,
                          EntityBasicAttribute id,
                          EntityBasicAttribute version,
                          EntityBasicAttribute sourceAttribute,
                          EntityBasicAttribute targetAttribute) {
            super(attributes, id, version);
            this.sourceAttribute = sourceAttribute;
            this.targetAttribute = targetAttribute;
        }

        public EntityBasicAttribute sourceAttribute() {
            return sourceAttribute;
        }

        public EntityBasicAttribute targetAttribute() {
            return targetAttribute;
        }
    }

    public DefaultEntitySchemaAttribute(MetamodelAttribute attribute,
                                        DefaultEntitySchema declareBy,
                                        DefaultMetamodel metamodel) {
        super(attribute.type(), metamodel);
        this.declareBy = declareBy;
        this.accessor = attribute.accessor();
        this.path = declareBy.getPath(attribute.name());
        this.fetchType = resolver.getFetchType(this);
    }

    @Override
    public EntityType getTargetEntityType() {
        return this;
    }

    @Override
    public EntityBasicAttribute getSourceAttribute() {
        return this.getAttributesSupplier().get().sourceAttribute();
    }

    @Override
    public EntityBasicAttribute getTargetAttribute() {
        return this.getAttributesSupplier().get().targetAttribute();
    }

    @Override
    protected Lazy<? extends Attributes> getAttributesSupplier() {
        return TypeCastUtil.unsafeCast(super.getAttributesSupplier());
    }

    @Override
    public Accessor accessor() {
        return accessor;
    }

    @Override
    public DefaultEntitySchema declareBy() {
        return declareBy;
    }

    @Override
    public PathNode path() {
        return this.path;
    }

    @Override
    protected AttributeSet<EntityAttribute> createAttributes() {
        DefaultEntitySchema schema = DefaultEntitySchema.of(type(), metamodel);
        ImmutableArray<? extends EntityAttribute> entityAttributes = schema.getAttributes();
        List<EntityAttribute> entityAttributeList = new ArrayList<>(entityAttributes.size());
        EntityBasicAttribute id = null;
        EntityBasicAttribute version = null;
        for (EntityAttribute attribute : entityAttributes) {
            EntityAttribute cur;
            switch (attribute) {
                case EntityBasicAttribute basic -> {
                    var attr = new DefaultEntityBasicAttribute(basic, this, resolver);
                    cur = attr;
                    if (basic.isId()) {
                        id = attr;
                    } else if (basic.isVersion()) {
                        version = attr;
                    }
                }
                case EmbeddedAttribute embedded -> cur = new DefaultEmbeddedAttribute(embedded, this, metamodel);
                case EntitySchemaAttribute schemaAttribute ->
                        cur = new DefaultEntitySchemaAttribute(schemaAttribute, this, metamodel);
                default ->
                        throw new ConfigurationException("Unknown entity attribute type '" + attribute.getClass().getName() + "' when resolving schema attribute '" + this.path + "' for entity '" + type().getName() + "'.");
            }
            entityAttributeList.add(cur);
        }
        var sourceAttribute = resolver.getJoinSourceAttribute(declareBy(), accessor());
        var targetAttribute = resolver.getJoinTargetAttribute(schema, accessor());
        return new Attributes(entityAttributeList, id, version, sourceAttribute, targetAttribute);
    }

    /// 合并当前字段的 @AttributeOverride 与父级传递的点号路径覆盖。
    /// 如父级的 address.street 在当前层级为 address 时，截取前缀后变为 street。
    protected Map<String, String> getAttributeOverrides() {
        Map<String, String> overrides = resolver.getAttributeOverrides(accessor);
        Map<String, String> parent = declareBy().getAttributeOverrides();
        for (Map.Entry<String, String> entry : parent.entrySet()) {
            String[] split = entry.getKey().split("\\.");
            if (split.length > 1 && split[0].equals(name())) {
                String key = Arrays.stream(split).skip(1)
                        .collect(Collectors.joining("."));
                overrides.put(key, entry.getValue());
            }
        }
        return overrides;
    }

    @Override
    public FetchType getFetchType() {
        return fetchType;
    }

    @Override
    public EntitySchema schema() {
        return this;
    }

}
