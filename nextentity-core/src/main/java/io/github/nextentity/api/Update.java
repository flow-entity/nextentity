package io.github.nextentity.api;

import org.jspecify.annotations.NonNull;

/// Entity-level CRUD operations interface.
///
/// This interface provides methods for insert, update, and delete operations
/// on individual entities or batches of entities.
///
/// For conditional batch operations (UPDATE WHERE / DELETE WHERE),
/// use {@link io.github.nextentity.spring.AbstractRepository#updateWhere()}
/// and {@link io.github.nextentity.spring.AbstractRepository#deleteWhere()}.
///
/// @param <T> Entity type
/// @author HuangChengwei
/// @since 1.0.0
public interface Update<T> {

    /// Insert a single entity.
    ///
    /// @param entity Entity object
    void insert(@NonNull T entity);

    /// Batch insert entities.
    ///
    /// @param entities List of entities
    void insert(@NonNull Iterable<T> entities);

    /// Batch update entities.
    ///
    /// @param entities List of entities
    void update(@NonNull Iterable<T> entities);

    /// Update a single entity.
    ///
    /// @param entity Entity object
    void update(@NonNull T entity);

    /// Batch delete entities.
    ///
    /// @param entities List of entities
    void delete(@NonNull Iterable<T> entities);

    /// Delete a single entity.
    ///
    /// @param entity Entity object
    void delete(@NonNull T entity);

}
