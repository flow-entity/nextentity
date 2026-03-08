package io.github.nextentity.spring;

import java.io.Serializable;

public abstract class AbstractJdbcRepository<T, ID extends Serializable> extends AbstractRepository<T, ID> {

    protected AbstractJdbcRepository(JdbcRepositoryFactoryConfiguration configuration) {
        super(configuration.getRepositoryFactory());
    }

}
