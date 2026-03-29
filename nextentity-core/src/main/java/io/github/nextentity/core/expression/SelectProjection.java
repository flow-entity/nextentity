package io.github.nextentity.core.expression;

///
/// Record representing a SELECT clause that maps results to a projection/DTO class.
/// <p>
/// Used when query results should be mapped to a non-entity class such as
/// a DTO, record, or interface projection.
///
/// @param type the projection class
/// @param distinct whether to apply DISTINCT
/// @author HuangChengwei
/// @since 1.0.0
///
public record SelectProjection(Class<?> type, boolean distinct) implements Selected {
}
