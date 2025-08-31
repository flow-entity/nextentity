package io.github.nextentity.core.meta;

import io.github.nextentity.core.expression.InternalPathExpression;
import io.github.nextentity.core.reflect.schema.Attribute;
import io.github.nextentity.core.reflect.schema.ReflectType;
import io.github.nextentity.core.reflect.schema.Schema;
import io.github.nextentity.core.util.ImmutableList;

import java.util.ArrayDeque;
import java.util.List;

public interface EntityAttribute extends Attribute, InternalPathExpression {

    String columnName();

    Schema declareBy();

    ValueConvertor valueConvertor();

    boolean isVersion();

    boolean isId();

    default Object getDatabaseValue(Object entity) {
        Object o = get(entity);
        return valueConvertor().toDatabaseValue(o);
    }

    default void setByDatabaseValue(Object entity, Object value) {
        value = valueConvertor().toAttributeValue(value);
        set(entity, value);
    }


    @Override
    default int deep() {
        return attributePaths().size();
    }

    InternalPathExpression path();

    default List<? extends Attribute> attributePaths() {
        ReflectType cur = this;
        ArrayDeque<Attribute> attributes = new ArrayDeque<>(2);
        while (true) {
            if (cur instanceof Attribute attribute) {
                attributes.addFirst(attribute);
                cur = attribute.declareBy();
            } else {
                break;
            }
        }
        return new ImmutableList<>(attributes);
    }
}
