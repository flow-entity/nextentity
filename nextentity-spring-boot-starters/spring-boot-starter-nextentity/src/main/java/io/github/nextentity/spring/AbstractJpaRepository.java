package io.github.nextentity.spring;

import io.github.nextentity.api.Repository;

import java.io.Serializable;

public class AbstractJpaRepository<T, ID extends Serializable> extends AbstractJdbcRepository<T, ID> {
    protected final Repository<ID, T> repository;

    protected AbstractJpaRepository(JpaRepositoryFactoryConfiguration configuration) {
        super(configuration.jdbc());
        repository = configuration.getRepositoryFactory().getRepository(getEntityType());
    }

    protected Repository<ID, T> getJpaRepository() {
        return repository;
    }

    protected Repository<ID, T> repository() {
        return getJpaRepository();
    }
}
