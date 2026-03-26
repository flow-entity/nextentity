package io.github.nextentity.integration.config.spring;

import io.github.nextentity.core.QueryExecutor;
import io.github.nextentity.core.UpdateExecutor;
import io.github.nextentity.integration.config.IntegrationTestContext;
import io.github.nextentity.integration.config.env.DatabaseEnvironmentVariables;
import io.github.nextentity.integration.config.fixtures.TestDataFactory;
import io.github.nextentity.jdbc.ConnectionProvider;
import jakarta.persistence.EntityManager;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;

@SpringBootApplication
public class IntegrationTestApplication {

    @Bean
    public IntegrationTestContext jdbcIntegrationTestContext() {
        // TODO return new SpringIntegrationTestContext with JdbcQueryExecutor and JdbcUpdateExecutor
        return null;
    }

    @Bean
    public IntegrationTestContext jpaIntegrationTestContext() {
        // TODO return new SpringIntegrationTestContext with JpaQueryExecutor and JpaUpdateExecutor
        return null;
    }

    @Component
    public static class SpringConnectionProvider implements ConnectionProvider {
        @Autowired
        JdbcTemplate jdbcTemplate;
        @Autowired
        TransactionTemplate transactionTemplate;

        public SpringConnectionProvider(JdbcTemplate jdbcTemplate, TransactionTemplate transactionTemplate) {
            this.jdbcTemplate = jdbcTemplate;
            this.transactionTemplate = transactionTemplate;
        }

        @Override
        public <T> T execute(ConnectionCallback<T> action) {
            return jdbcTemplate.execute(action::doInConnection);
        }

        @Override
        public <T> T executeInTransaction(ConnectionCallback<T> action) {
            return transactionTemplate.execute(ignore -> execute(action));
        }
    }

    @Component
    public static class DatabaseInitializer {
        @Autowired
        JdbcTemplate jdbcTemplate;
        @Autowired
        EntityManager entityManager;
        @Autowired
        ApplicationContext applicationContext;

        @Transactional
        public void reset() {
            DatabaseEnvironmentVariables dbEnv = applicationContext.getBean(DatabaseEnvironmentVariables.class);
            for (String sql : dbEnv.ddl()) {
                jdbcTemplate.execute(sql);
            }
            TestDataFactory.createDepartments().forEach(entityManager::persist);
            entityManager.flush();
            TestDataFactory.createEmployees().forEach(entityManager::persist);
            entityManager.flush();
            entityManager.clear();
        }
    }

    public static class SpringIntegrationTestContext implements IntegrationTestContext {
        @Autowired
        DataSource dataSource;
        @Autowired
        DatabaseInitializer databaseInitializer;

        private final String name;
        private final QueryExecutor queryExecutor;
        private final UpdateExecutor updateExecutor;

        public SpringIntegrationTestContext(QueryExecutor queryExecutor, UpdateExecutor updateExecutor, String name) {
            this.queryExecutor = queryExecutor;
            this.updateExecutor = updateExecutor;
            this.name = name;
        }

        @Override
        public DataSource getDataSource() {
            return dataSource;
        }

        @Override
        public QueryExecutor getQueryExecutor() {
            return queryExecutor;
        }

        @Override
        public UpdateExecutor getUpdateExecutor() {
            return updateExecutor;
        }

        @Override
        @Transactional
        public @NonNull IntegrationTestContext reset() {
            databaseInitializer.reset();
            return this;
        }

        @Override
        public String toString() {
            return name;
        }
    }

}
