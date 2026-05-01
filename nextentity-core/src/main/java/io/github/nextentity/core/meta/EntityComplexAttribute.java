package io.github.nextentity.core.meta;

public sealed interface EntityComplexAttribute
        extends EntityAttribute, ComplexAttribute
        permits EntityEmbeddedAttribute, EntitySchemaAttribute {
}
