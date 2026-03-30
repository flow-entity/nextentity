package io.github.nextentity.spring;

import io.github.nextentity.api.Select;
import io.github.nextentity.core.UpdateExecutor;

public interface NextEntityFactory {

    <T> Select<T> queryBuilder(Class<T> entityType);

    UpdateExecutor updateExecutor();

}
