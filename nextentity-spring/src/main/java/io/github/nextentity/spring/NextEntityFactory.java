package io.github.nextentity.spring;

import io.github.nextentity.api.QueryBuilder;
import io.github.nextentity.core.UpdateExecutor;

public interface NextEntityFactory {

    <T> QueryBuilder<T> queryBuilder(Class<T> entityType);

    UpdateExecutor updateExecutor();

}
