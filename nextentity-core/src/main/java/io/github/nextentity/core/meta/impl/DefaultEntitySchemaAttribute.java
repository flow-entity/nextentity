package io.github.nextentity.core.meta.impl;

import io.github.nextentity.core.TypeCastUtil;
import io.github.nextentity.core.exception.ConfigurationException;
import io.github.nextentity.core.expression.PathNode;
import io.github.nextentity.core.meta.*;
import io.github.nextentity.core.reflect.schema.Accessor;
import io.github.nextentity.core.util.ImmutableArray;
import io.github.nextentity.core.util.Lazy;
import jakarta.persistence.FetchType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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
                case EntityEmbeddedAttribute embedded -> cur = new DefaultEntityEmbeddedAttribute(embedded, this, metamodel);
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

    @Override
    protected Map<String, String> getAttributeOverrides() {
        return mergeAttributeOverrides(resolver, accessor, declareBy(), name());
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
