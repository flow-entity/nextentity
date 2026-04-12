package io.github.nextentity.integration.config;

import io.github.nextentity.api.UpdateSetStep;
import io.github.nextentity.core.*;
import io.github.nextentity.core.exception.SqlException;
import io.github.nextentity.core.meta.EntityType;
import io.github.nextentity.core.meta.Metamodel;
import io.github.nextentity.integration.config.env.DatabaseEnvironmentVariables;
import io.github.nextentity.integration.config.fixtures.TestDataFactory;
import io.github.nextentity.jdbc.*;
import io.github.nextentity.jpa.JpaPersistExecutor;
import io.github.nextentity.jpa.JpaQueryExecutor;
import io.github.nextentity.meta.jpa.JpaMetamodel;
import jakarta.persistence.EntityManager;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Objects;
import java.util.function.Supplier;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@EntityScan("io.github.nextentity.integration.entity")
@SpringBootApplication
public class IntegrationTestApplication {

    @Bean
    public IntegrationTestContext jdbcIntegrationTestContext(JdbcTemplate jdbcTemplate, SpringConnectionProvider connectionProvider, TransactionTemplate transactionTemplate) {
        DataSource dataSource = Objects.requireNonNull(jdbcTemplate.getDataSource());
        SqlDialect sqlDialect;
        try {
            sqlDialect = SqlDialect.detectFromDataSource(dataSource);
        } catch (SQLException e) {
            throw new SqlException(e);
        }
        Metamodel metamodel = JpaMetamodel.of();
        SqlBuilder sqlBuilder =  SqlBuilder.of(sqlDialect);
        JdbcQueryExecutor queryExecutor = new JdbcQueryExecutor(metamodel, sqlBuilder, connectionProvider, new JdbcResultCollector());
        PersistExecutor updateExecutor = new JdbcPersistExecutor(sqlBuilder, connectionProvider);
        updateExecutor = new TransactionUpdateExecutor(updateExecutor, new TransactionOperations() {
            @Override
            public <T> T executeInTransaction(Supplier<T> operation) {
                return transactionTemplate.execute(_ -> operation.get());
            }
        });
        return new SpringIntegrationTestContext(queryExecutor, updateExecutor, "jdbc");
    }

    @Bean
    public IntegrationTestContext jpaIntegrationTestContext(EntityManager entityManager,
                                                            JdbcTemplate jdbcTemplate,
                                                            SpringConnectionProvider connectionProvider,
                                                            TransactionTemplate transactionTemplate) {

        DataSource dataSource = Objects.requireNonNull(jdbcTemplate.getDataSource());
        SqlDialect sqlDialect;
        try {
            sqlDialect = SqlDialect.detectFromDataSource(dataSource);
        } catch (SQLException e) {
            throw new SqlException(e);
        }
        Metamodel metamodel = JpaMetamodel.of();
        SqlBuilder sqlBuilder = SqlBuilder.of(sqlDialect);
        JdbcQueryExecutor jdbcQueryExecutor = new JdbcQueryExecutor(metamodel, sqlBuilder, connectionProvider, new JdbcResultCollector());
        JpaQueryExecutor queryExecutor = new JpaQueryExecutor(entityManager, metamodel, jdbcQueryExecutor);
        PersistExecutor updateExecutor = new JpaPersistExecutor(entityManager);
        updateExecutor = new TransactionUpdateExecutor(updateExecutor, new TransactionOperations() {
            @Override
            public <T> T executeInTransaction(Supplier<T> operation) {
                return transactionTemplate.execute(_ -> operation.get());
            }
        });
        return new SpringIntegrationTestContext(queryExecutor, updateExecutor, "jpa");
    }

    @Component
    public static class SpringConnectionProvider implements ConnectionProvider {
        private final JdbcTemplate jdbcTemplate;

        @Autowired
        public SpringConnectionProvider(JdbcTemplate jdbcTemplate, TransactionTemplate transactionTemplate) {
            this.jdbcTemplate = jdbcTemplate;
        }

        @Override
        public <T> T execute(ConnectionCallback<T> action) {
            return jdbcTemplate.execute(action::doInConnection);
        }

    }

    @Component
    public static class DatabaseInitializer {
        private final JdbcTemplate jdbcTemplate;
        private final EntityManager entityManager;
        private final ApplicationContext applicationContext;

        public DatabaseInitializer(JdbcTemplate jdbcTemplate,
                                   EntityManager entityManager,
                                   ApplicationContext applicationContext) {
            this.jdbcTemplate = jdbcTemplate;
            this.entityManager = entityManager;
            this.applicationContext = applicationContext;
        }

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
            TestDataFactory.createLockableEntities().forEach(entityManager::persist);
            entityManager.flush();
            entityManager.clear();
        }
    }

    public static class SpringIntegrationTestContext implements IntegrationTestContext {
        @Autowired
        private DatabaseInitializer databaseInitializer;
        @Autowired
        private ApplicationContext applicationContext;

        private final String name;
        private final QueryExecutor queryExecutor;
        private final PersistExecutor updateExecutor;

        public SpringIntegrationTestContext(QueryExecutor queryExecutor, PersistExecutor updateExecutor, String name) {
            this.queryExecutor = queryExecutor;
            this.updateExecutor = updateExecutor;
            this.name = name;
        }

        @Override
        public QueryExecutor getQueryExecutor() {
            return queryExecutor;
        }

        @Override
        public PersistExecutor getUpdateExecutor() {
            return updateExecutor;
        }

        @Override
        @Transactional
        public @NonNull IntegrationTestContext reset() {
            databaseInitializer.reset();
            return this;
        }

        @Override
        public <T> T doInTransaction(Supplier<T> command) {
            return applicationContext.getBean(TransactionTemplate.class).execute(_ -> command.get());
        }

        @Override
        public String toString() {
            DatabaseEnvironmentVariables dbEnv = applicationContext.getBean(DatabaseEnvironmentVariables.class);
            return dbEnv.name() + "-" + name;
        }

        @Override
        public <T> UpdateSetStep<T> update(Class<T> type) {
            PersistDescriptor<T> descriptor = new PersistDescriptor<>() {
                @Override
                public PersistExecutor persistExecutor() {
                    return updateExecutor;
                }

                @Override
                public Metamodel metamodel() {
                    return JpaMetamodel.of();
                }

                @Override
                public EntityType entityType() {
                    return JpaMetamodel.of().getEntity(type);
                }

                @Override
                public Class<T> entityClass() {
                    return type;
                }
            };
            return new UpdateSetStepImpl<>(descriptor);
        }

        @Override
        public <T> UpdateSetStep<T> update(EntityTemplateDescriptor<T> type) {
            return new UpdateSetStepImpl<>(type);
        }
    }

}