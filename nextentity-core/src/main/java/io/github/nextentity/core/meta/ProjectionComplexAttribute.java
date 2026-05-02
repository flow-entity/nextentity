package io.github.nextentity.core.meta;

public sealed interface ProjectionComplexAttribute extends ProjectionAttribute, ComplexAttribute permits
        ProjectionJoinAttribute,
        ProjectionSchemaAttribute,
        ProjectionEmbeddedAttribute {

    @Override
    ProjectionSchema schema();

}
