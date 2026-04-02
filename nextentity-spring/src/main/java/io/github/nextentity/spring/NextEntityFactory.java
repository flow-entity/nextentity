package io.github.nextentity.spring;

import io.github.nextentity.api.DeleteWhereStep;
import io.github.nextentity.api.QueryBuilder;
import io.github.nextentity.api.UpdateWhereStep;
import io.github.nextentity.core.UpdateExecutor;

public interface NextEntityFactory {

    <T> QueryBuilder<T> queryBuilder(Class<T> entityType);

    UpdateExecutor updateExecutor();

    /// Creates a conditional update builder for the specified entity type.
    ///
    /// @param entityType Entity class
    /// @param <T> Entity type
    /// @return Update where step builder
    /// @since 2.1
    <T> UpdateWhereStep<T> updateWhereStep(Class<T> entityType);

    /// Creates a conditional delete builder for the specified entity type.
    ///
    /// @param entityType Entity class
    /// @param <T> Entity type
    /// @return Delete where step builder
    /// @since 2.1
    <T> DeleteWhereStep<T> deleteWhereStep(Class<T> entityType);

}
