package io.github.nextentity.core.meta.impl;

import io.github.nextentity.core.TypeCastUtil;
import io.github.nextentity.core.exception.ConfigurationException;
import io.github.nextentity.core.expression.PathNode;
import io.github.nextentity.core.meta.*;
import io.github.nextentity.core.reflect.schema.Accessor;
import io.github.nextentity.core.reflect.schema.Attribute;
import io.github.nextentity.core.reflect.schema.impl.DefaultAttribute;
import io.github.nextentity.core.util.ImmutableArray;
import io.github.nextentity.core.util.Lazy;
import jakarta.persistence.FetchType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DefaultEntitySchemaAttribute
        extends DefaultEntitySchema
        implements EntitySchemaAttribute {

    private final DefaultAttribute attribute;
    private final PathNode path;

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

    public DefaultEntitySchemaAttribute(Attribute attribute,
                                        DefaultEntitySchema declareBy,
                                        DefaultMetamodel metamodel) {
        super(attribute.type(), metamodel);
        this.attribute = new DefaultAttribute(declareBy, attribute);
        this.path = new PathNode(this.attribute.path().toArray(String[]::new));
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
        return attribute.accessor();
    }

    @Override
    public DefaultEntitySchema declareBy() {
        return (DefaultEntitySchema) attribute.declareBy();
    }

    @Override
    public PathNode path() {
        return this.path;
    }

    @Override
    protected Attributes createAttributes() {
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
                throw new ConfigurationException(
                        "Unknown entity attribute type '" + attribute.getClass().getName() +
                        "' when resolving schema attribute '" + this.path +
                        "' for entity '" + type().getName() + "'.");
            }
            entityAttributeList.add(cur);
        }
        var sourceAttribute = resolver.getJoinSourceAttribute(declareBy(), this);
        var targetAttribute = resolver.getJoinTargetAttribute(schema, this);
        return new Attributes(entityAttributeList, id, version, sourceAttribute, targetAttribute);
    }

    @Override
    public FetchType getFetchType() {
        return resolver.getFetchType(attribute);
    }

    @Override
    public EntitySchema schema() {
        return this;
    }
}