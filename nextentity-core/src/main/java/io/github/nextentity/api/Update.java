package io.github.nextentity.api;

import org.jspecify.annotations.NonNull;

/// Update operation interface, providing insert, update and delete methods.
///
/// @param <T> Entity type
/// @author HuangChengwei
/// @since 1.0.0
@Deprecated
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
