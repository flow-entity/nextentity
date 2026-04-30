package io.github.nextentity.core.meta.impl;

import io.github.nextentity.core.TypeCastUtil;
import io.github.nextentity.core.exception.ConfigurationException;
import io.github.nextentity.core.expression.PathNode;
import io.github.nextentity.core.meta.*;
import io.github.nextentity.core.reflect.schema.Accessor;
import io.github.nextentity.core.reflect.schema.impl.DefaultAccessor;
import io.github.nextentity.core.util.ImmutableArray;
import io.github.nextentity.core.util.Lazy;
import jakarta.persistence.FetchType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DefaultEntitySchemaAttribute
        extends DefaultEntitySchema
        implements EntitySchemaAttribute {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DefaultEntitySchemaAttribute.class);

    private final Accessor accessor;
    private final DefaultEntitySchema declareBy;
    private final PathNode path;
    private final FetchType fetchType;
    /** 标记该属性是否为嵌入字段（{@code @Embedded}） */
    private final boolean embedded;

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
        this.embedded = resolver.isEmbedded(accessor);
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
        if (isEmbedded()) {
            List<DefaultAccessor> accessors = DefaultAccessor.of(type());
            ArrayList<EntityAttribute> attributes = new ArrayList<>();
            for (DefaultAccessor accessor : accessors) {
                if (resolver.isTransient(accessor)) {
                    continue;
                }
                boolean isComplexType = !DefaultAccessor.of(accessor.type()).isEmpty();
                DefaultMetamodelAttribute attr = new DefaultMetamodelAttribute(this, accessor);
                if (isComplexType && resolver.isEmbedded(accessor)) {
                    attributes.add(new DefaultEntitySchemaAttribute(attr, this, metamodel));
                } else if (resolver.isBasicField(accessor)) {
                    attributes.add(new DefaultEntityBasicAttribute(attr, this, resolver));
                } else {
                    log.warn("ignored attribute {}", accessor.field());
                }
            }
            return new Attributes(attributes, null, null, null, null);
        } else {
            DefaultEntitySchema schema = DefaultEntitySchema.of(type(), metamodel);
            ImmutableArray<? extends EntityAttribute> entityAttributes = schema.getAttributes();
            List<EntityAttribute> entityAttributeList = new ArrayList<>(entityAttributes.size());
            EntityBasicAttribute id = null;
            EntityBasicAttribute version = null;
            for (EntityAttribute attribute : entityAttributes) {
                EntityAttribute cur;
                if (attribute instanceof EntityBasicAttribute basicAttribute) {
                    var attr = new DefaultEntityBasicAttribute(basicAttribute, this, resolver);
                    cur = attr;
                    if (basicAttribute.isId()) {
                        id = attr;
                    } else if (basicAttribute.isVersion()) {
                        version = attr;
                    }
                } else if (attribute instanceof EntitySchemaAttribute schemaAttribute) {
                    cur = new DefaultEntitySchemaAttribute(schemaAttribute, this, metamodel);
                } else {
                    throw new ConfigurationException("Unknown entity attribute type '" + attribute.getClass().getName() + "' when resolving schema attribute '" + this.path + "' for entity '" + type().getName() + "'.");
                }
                entityAttributeList.add(cur);
            }
            var sourceAttribute = resolver.getJoinSourceAttribute(declareBy(), accessor());
            var targetAttribute = resolver.getJoinTargetAttribute(schema, accessor());
            return new Attributes(entityAttributeList, id, version, sourceAttribute, targetAttribute);
        }
    }

    @Override
    public FetchType getFetchType() {
        return fetchType;
    }

    @Override
    public EntitySchema schema() {
        return this;
    }

    /**
     * 返回该属性是否为嵌入字段。
     *
     * @return 如果是 {@code @Embedded} 注解的嵌入字段则返回 {@code true}
     */
    @Override
    public boolean isEmbedded() {
        return embedded;
    }
}
