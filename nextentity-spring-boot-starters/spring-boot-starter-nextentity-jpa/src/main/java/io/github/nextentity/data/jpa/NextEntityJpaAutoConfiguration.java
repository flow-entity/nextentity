package io.github.nextentity.data.jpa;

import io.github.nextentity.core.RepositoryFactory;
import io.github.nextentity.core.SimpleQueryConfig;
import io.github.nextentity.core.UpdateExecutor;
import io.github.nextentity.core.meta.Metamodel;
import io.github.nextentity.data.TransactionalUpdateExecutor;
import io.github.nextentity.jdbc.JdbcQueryExecutor;
import io.github.nextentity.jpa.JpaQueryExecutor;
import io.github.nextentity.jpa.JpaUpdateExecutor;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.SharedEntityManagerCreator;

@Configuration
public class NextEntityJpaAutoConfiguration {
    public static final String JPA_REPOSITORY_FACTORY_BEAN_NAME = "repositoryFactory";

    @Primary
    @Bean(name = JPA_REPOSITORY_FACTORY_BEAN_NAME)
    protected RepositoryFactory jpaEntitiesFactory(JpaQueryExecutor queryExecutor,
                                                   UpdateExecutor updateExecutor,
                                                   Metamodel metamodel) {
        return new RepositoryFactory(queryExecutor, updateExecutor, metamodel);
    }

    @Bean
    @Primary
    protected JpaQueryExecutor jpaQueryExecutor(EntityManager entityManager,
                                                Metamodel metamodel,
                                                JdbcQueryExecutor executor) {
        return new JpaQueryExecutor(entityManager, metamodel, executor);
    }

    @Bean("jpaUpdate")
    @Primary
    protected UpdateExecutor jpaUpdateExecutor(EntityManager entityManager,
                                               JpaQueryExecutor jpaQueryExecutor,
                                               Metamodel metamodel) {

        SimpleQueryConfig config = new SimpleQueryConfig()
                .metamodel(metamodel)
                .queryExecutor(jpaQueryExecutor);
        JpaUpdateExecutor jpaUpdate = new JpaUpdateExecutor(entityManager, config);
        return new TransactionalUpdateExecutor(jpaUpdate);
    }

    @Bean
    protected EntityManager entityManager(EntityManagerFactory entityManagerFactory) {
        return SharedEntityManagerCreator.createSharedEntityManager(entityManagerFactory);
    }

}
