package io.github.nextentity.jdbc;

import io.github.nextentity.api.EntityOperations;
import io.github.nextentity.core.EntityOperationsFactory;
import io.github.nextentity.jdbc.configuration.EntityOperationsConfiguration;

// TODO
public class JdbcEntityOperationsFactory implements EntityOperationsFactory {

    // 只依赖EntityOperationsConfiguration，其他必要的依赖放到EntityOperationsConfiguration
    public JdbcEntityOperationsFactory(EntityOperationsConfiguration config) {
    }

    @Override
    public <T> EntityOperations<T> operations(Class<T> entityType) {
        return null;
    }
}
