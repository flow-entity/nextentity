package io.github.nextentity.core.meta.impl;

import io.github.nextentity.core.expression.PathNode;
import io.github.nextentity.core.meta.EntityAttribute;
import io.github.nextentity.core.meta.EntityEmbeddedAttribute;
import io.github.nextentity.core.meta.EntitySchema;
import io.github.nextentity.core.meta.MetamodelAttribute;
import io.github.nextentity.core.reflect.schema.Accessor;
import io.github.nextentity.core.reflect.schema.impl.DefaultAccessor;
import io.github.nextentity.core.util.Lazy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DefaultEntityEmbeddedAttribute extends DefaultEntitySchema implements EntityEmbeddedAttribute {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DefaultEntityEmbeddedAttribute.class);

    private final Accessor accessor;
    private final DefaultEntitySchema declareBy;
    private final PathNode path;

    public DefaultEntityEmbeddedAttribute(MetamodelAttribute attribute,
                                          DefaultEntitySchema declareBy,
                                          DefaultMetamodel metamodel) {
        super(attribute.type(), metamodel);
        this.declareBy = declareBy;
        this.accessor = attribute.accessor();
        this.path = declareBy.getPath(attribute.name());
    }

    @Override
    protected Lazy<? extends AttributeSet<EntityAttribute>> getAttributesSupplier() {
        return super.getAttributesSupplier();
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
        Map<String, String> attributeOverrides = getAttributeOverrides();
        List<DefaultAccessor> accessors = DefaultAccessor.of(type());
        ArrayList<EntityAttribute> attributes = new ArrayList<>();
        for (DefaultAccessor accessor : accessors) {
            if (resolver.isTransient(accessor)) {
                continue;
            }
            List<DefaultAccessor> subAccessors = DefaultAccessor.of(accessor.type());
            boolean isComplexType = !subAccessors.isEmpty();
            DefaultMetamodelAttribute attr = new DefaultMetamodelAttribute(this, accessor);
            if (isComplexType && resolver.isEmbedded(accessor)) {
                attributes.add(new DefaultEntityEmbeddedAttribute(attr, this, metamodel));
            } else if (resolver.isBasicField(accessor)) {
                String columnName = attributeOverrides.getOrDefault(
                        accessor.name(),
                        resolver.getColumnName(accessor)
                );
                attributes.add(new DefaultEntityBasicAttribute(
                        attr, this, resolver, columnName));
            } else {
                log.warn("ignored attribute {}", accessor.field());
            }
        }
        return new AttributeSet<>(attributes);
    }

    @Override
    protected Map<String, String> getAttributeOverrides() {
        return mergeAttributeOverrides(resolver, accessor, declareBy(), name());
    }

    @Override
    public EntitySchema schema() {
        return this;
    }

}
