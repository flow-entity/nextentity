package io.github.nextentity.core.meta;

import io.github.nextentity.core.TypeCastUtil;
import io.github.nextentity.core.reflect.schema.Attribute;
import io.github.nextentity.core.reflect.schema.Attributes;
import io.github.nextentity.core.reflect.schema.Schema;
import io.github.nextentity.core.util.ImmutableArray;

public interface EntitySchema extends Schema {

    EntityAttribute id();

    String tableName();

    @Override
    default ImmutableArray<? extends EntityAttribute> getPrimitives() {
        ImmutableArray<? extends Attribute> attributes = Schema.super.getPrimitives();
        return TypeCastUtil.cast(attributes);
    }

    EntityAttribute version();

    @Override
    Attributes attributes();
}
