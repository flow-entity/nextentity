package io.github.nextentity.core.meta;

public non-sealed interface ProjectionEmbeddedAttribute extends ProjectionComplexAttribute {

    @Override
    default boolean isEmbeddable() {
        return true;
    }

    @Override
    EntityEmbeddedAttribute getEntityAttribute();
}
