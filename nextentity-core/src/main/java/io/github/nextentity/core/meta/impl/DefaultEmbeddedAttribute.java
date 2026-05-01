package io.github.nextentity.core.meta.impl;

import io.github.nextentity.core.TypeCastUtil;
import io.github.nextentity.core.expression.PathNode;
import io.github.nextentity.core.meta.EmbeddedAttribute;
import io.github.nextentity.core.meta.EntityAttribute;
import io.github.nextentity.core.meta.EntitySchema;
import io.github.nextentity.core.meta.MetamodelAttribute;
import io.github.nextentity.core.reflect.schema.Accessor;
import io.github.nextentity.core.reflect.schema.impl.DefaultAccessor;
import io.github.nextentity.core.util.Lazy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DefaultEmbeddedAttribute extends DefaultEntitySchema implements EmbeddedAttribute {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DefaultEmbeddedAttribute.class);

    private final Accessor accessor;
    private final DefaultEntitySchema declareBy;
    private final PathNode path;

    public DefaultEmbeddedAttribute(MetamodelAttribute attribute,
                                    DefaultEntitySchema declareBy,
                                    DefaultMetamodel metamodel) {
        super(attribute.type(), metamodel);
        this.declareBy = declareBy;
        this.accessor = attribute.accessor();
        this.path = declareBy.getPath(attribute.name());
    }

    @Override
    protected Lazy<? extends AttributeSet<EntityAttribute>> getAttributesSupplier() {
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
        Map<String, String> attributeOverrides = getAttributeOverrides();
        List<DefaultAccessor> accessors = DefaultAccessor.of(type());
        ArrayList<EntityAttribute> attributes = new ArrayList<>();
        for (DefaultAccessor accessor : accessors) {
            if (resolver.isTransient(accessor)) {
                continue;
            }
            boolean isComplexType = !DefaultAccessor.of(accessor.type()).isEmpty();
            DefaultMetamodelAttribute attr = new DefaultMetamodelAttribute(this, accessor);
            if (isComplexType && resolver.isEmbedded(accessor)) {
                attributes.add(new DefaultEmbeddedAttribute(attr, this, metamodel));
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
    public EntitySchema schema() {
        return this;
    }

}
