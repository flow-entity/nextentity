package io.github.nextentity.core.reflect.schema;

public sealed interface ReflectType permits Attribute, Schema {

    Class<?> type();

    default boolean isObject() {
        return false;
    }

    default boolean isPrimitive() {
        return !isObject();
    }

}
