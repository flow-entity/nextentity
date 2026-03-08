package io.github.nextentity.spring;

import io.github.nextentity.core.QueryExecutor;
import io.github.nextentity.core.RepositoryFactory;
import io.github.nextentity.core.SimpleQueryConfig;
import io.github.nextentity.core.UpdateExecutor;
import io.github.nextentity.core.meta.Metamodel;
import io.github.nextentity.jpa.JpaQueryExecutor;
import io.github.nextentity.jpa.JpaUpdateExecutor;
import io.github.nextentity.meta.jpa.JpaMetamodel;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.orm.jpa.SharedEntityManagerCreator;

public class JpaRepositoryFactoryConfiguration {

    private final RepositoryFactory repositoryFactory;
    private final JdbcRepositoryFactoryConfiguration jdbcRepositoryFactoryConfiguration;

    public JpaRepositoryFactoryConfiguration(EntityManagerFactory entityManagerFactory,
                                             JdbcRepositoryFactoryConfiguration jdbcRepositoryFactoryConfiguration) {
        EntityManager entityManager = entityManager(entityManagerFactory);
        QueryExecutor jdbcQueryExecutor = jdbcRepositoryFactoryConfiguration.getRepositoryFactory().queryExecutor();
        Metamodel metamodel = metamodel();
        JpaQueryExecutor jpaQueryExecutor = jpaQueryExecutor(entityManager, metamodel, jdbcQueryExecutor);
        UpdateExecutor updateExecutor = jpaUpdateExecutor(entityManager, jpaQueryExecutor, metamodel);
        repositoryFactory = new RepositoryFactory(jpaQueryExecutor, updateExecutor, metamodel);
        this.jdbcRepositoryFactoryConfiguration = jdbcRepositoryFactoryConfiguration;
    }

    public JdbcRepositoryFactoryConfiguration jdbc() {
        return jdbcRepositoryFactoryConfiguration;
    }

    protected Metamodel metamodel() {
        return JpaMetamodel.of();
    }

    protected UpdateExecutor jpaUpdateExecutor(EntityManager entityManager,
                                               JpaQueryExecutor jpaQueryExecutor,
                                               Metamodel metamodel) {

        SimpleQueryConfig config = new SimpleQueryConfig()
                .metamodel(metamodel)
                .queryExecutor(jpaQueryExecutor);
        return new JpaUpdateExecutor(entityManager, config);
    }

    protected EntityManager entityManager(EntityManagerFactory entityManagerFactory) {
        return SharedEntityManagerCreator.createSharedEntityManager(entityManagerFactory);
    }


    protected JpaQueryExecutor jpaQueryExecutor(EntityManager entityManager,
                                                Metamodel metamodel,
                                                QueryExecutor executor) {
        return new JpaQueryExecutor(entityManager, metamodel, executor);
    }

    public RepositoryFactory getRepositoryFactory() {
        return this.repositoryFactory;
    }
}
