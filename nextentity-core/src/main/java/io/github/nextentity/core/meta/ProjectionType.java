package io.github.nextentity.core.meta;

import io.github.nextentity.core.reflect.schema.Schema;

///
/// Projection type interface for DTO and projection result mapping.
///
/// This interface extends {@link Schema} and provides metadata for mapping
/// query results to non-entity types such as DTOs, records, or interfaces.
///
/// Projection types are obtained from {@link EntityType#getProjection(Class)}
/// and define how entity attributes map to projection fields.
///
/// @author HuangChengwei
/// @since 1.0.0
///
public interface ProjectionType extends Schema {

    ///
    /// Gets the source entity schema that this projection maps from.
    ///
    /// @return the source entity schema
    ///
    Schema source();

}
