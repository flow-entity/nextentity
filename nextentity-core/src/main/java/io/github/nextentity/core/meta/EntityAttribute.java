package io.github.nextentity.core.meta;

import io.github.nextentity.core.SelectItem;
import io.github.nextentity.core.expression.ExpressionNode;
import io.github.nextentity.core.expression.PathNode;
import io.github.nextentity.core.reflect.schema.Attribute;
import io.github.nextentity.core.reflect.schema.Schema;

public sealed interface EntityAttribute extends Attribute, SelectItem
        permits EntityBasicAttribute, EntitySchemaAttribute {
    @Override
    PathNode path();

    @Override
    default ExpressionNode expression() {
        return path();
    }


    @Override
    EntitySchema declareBy();
}
