package io.github.nextentity.core.reflect.schema;

import io.github.nextentity.core.util.ImmutableArray;

public non-sealed interface Schema extends ReflectType {

    Attributes attributes();

    default ImmutableArray<? extends Attribute> getPrimitives() {
        return attributes().getPrimitives();
    }

    default Attribute getAttribute(String name) {
        return attributes().get(name);
    }

    default Attribute getAttribute(Iterable<String> fieldNames) {
        ReflectType schema = this;
        for (String fieldName : fieldNames) {
            try {
                schema = ((Schema) schema).getAttribute(fieldName);
            } catch (ClassCastException e) {
                throw e;
            }
        }
        return (Attribute) schema;
    }

    default boolean isObject() {
        return true;
    }

}
