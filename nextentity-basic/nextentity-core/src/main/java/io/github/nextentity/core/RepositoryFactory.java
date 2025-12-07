package io.github.nextentity.core;

import io.github.nextentity.api.Path;
import io.github.nextentity.api.Repository;
import io.github.nextentity.core.meta.Metamodel;

import java.io.Serializable;

/**
 * @author HuangChengwei
 * @since 2024-04-08 15:12
 */

public class RepositoryFactory implements QueryConfig {

    private final QueryExecutor queryExecutor;
    private final UpdateExecutor updateExecutor;
    private final QueryPostProcessor queryPostProcessor;
    private final Metamodel metamodel;


    public RepositoryFactory(QueryExecutor queryExecutor,
                             UpdateExecutor updateExecutor,
                             QueryPostProcessor queryPostProcessor,
                             Metamodel metamodel) {
        this.queryExecutor = queryExecutor;
        this.updateExecutor = updateExecutor;
        this.queryPostProcessor = queryPostProcessor;
        this.metamodel = metamodel;
    }

    public <T, ID extends Serializable> Repository<ID, T> getRepository(Class<T> entityType) {
        if (entityType.isAssignableFrom(Persistable.class)) {
            return getRepository(entityType, TypeCastUtil.unsafeCast((Path<Persistable<ID>, ID>) Persistable::getId));
        }
        return new RepositoryImpl<>(this, entityType);
    }

    public <T, ID extends Serializable> Repository<ID, T> getRepository(Class<T> entityType, Path<T, ID> idPath) {
        return new RepositoryImpl<>(this, entityType, idPath);
    }

    @Override
    public Metamodel metamodel() {
        return metamodel;
    }

    @Override
    public QueryExecutor queryExecutor() {
        return queryExecutor;
    }

    public UpdateExecutor getUpdateExecutor() {
        return updateExecutor;
    }

    public Metamodel getMetamodel() {
        return metamodel;
    }
}
