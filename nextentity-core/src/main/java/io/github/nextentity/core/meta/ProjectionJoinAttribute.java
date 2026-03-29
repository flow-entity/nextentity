package io.github.nextentity.core.meta;

///
/// Projection join attribute interface for mapping projection association fields.
///
/// This interface extends {@link ProjectionType} and represents a projection field
/// that maps to an entity association (join attribute).
///
/// Allows building nested projections for complex query result structures.
///
/// @author HuangChengwei
/// @since 1.0.0
///
public interface ProjectionJoinAttribute extends ProjectionType {

    ///
    /// Gets the source join attribute that this projection field maps from.
    ///
    /// @return the source join attribute
    ///
    JoinAttribute source();
}
