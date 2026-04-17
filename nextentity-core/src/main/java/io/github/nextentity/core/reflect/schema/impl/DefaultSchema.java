package io.github.nextentity.core.reflect.schema.impl;

import io.github.nextentity.core.reflect.schema.Attribute;
import io.github.nextentity.core.reflect.schema.Schema;

import java.util.ArrayList;
import java.util.List;

public class DefaultSchema extends AbstractSchema<AttributeSet<Attribute>, Attribute> implements Schema {

    protected DefaultSchema(Class<?> type) {
        super(type);
    }

    public static DefaultSchema of(Class<?> type) {
        return new DefaultSchema(type);
    }

    protected AttributeSet<Attribute> createAttributes() {
        List<DefaultAccessor> accessors = DefaultAccessor.of(type);

        // 创建属性列表
        ArrayList<Attribute> attributes = new ArrayList<>();
        int ordinal = 0;
        for (DefaultAccessor accessor : accessors) {
            List<DefaultAccessor> nestedAccessors = DefaultAccessor.of(accessor.type());
            if (nestedAccessors.isEmpty()) {
                DefaultAttribute attribute = new DefaultAttribute(this, accessor, ordinal++);
                attributes.add(attribute);
            } else {
                attributes.add(new DefaultSchemaAttribute(accessor, this, ordinal++));
            }
        }

        return new AttributeSet<>(attributes);
    }

}
