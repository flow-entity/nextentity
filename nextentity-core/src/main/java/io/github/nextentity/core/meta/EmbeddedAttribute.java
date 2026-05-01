package io.github.nextentity.core.meta;

public non-sealed interface EmbeddedAttribute extends EntityAttribute {

    EntitySchema schema();

    @Override
    default boolean isPrimitive() {
        return false;
    }
}
