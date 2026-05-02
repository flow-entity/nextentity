package io.github.nextentity.core.meta;

public sealed interface ComplexAttribute extends MetamodelAttribute permits ProjectionComplexAttribute, EntityComplexAttribute {

    MetamodelSchema<?> schema();

    default boolean isEmbeddable() {
        return false;
    }

    @Override
    default boolean isPrimitive() {
        return false;
    }

    @Override
    default boolean isObject() {
        return true;
    }
}
