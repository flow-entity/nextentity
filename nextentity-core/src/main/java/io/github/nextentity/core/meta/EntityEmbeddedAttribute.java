package io.github.nextentity.core.meta;

public non-sealed interface EntityEmbeddedAttribute extends EntityComplexAttribute {

    EntitySchema schema();

    @Override
    default boolean isEmbeddable() {
        return true;
    }
}
