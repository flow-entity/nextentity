package io.github.nextentity.integration.config;

import io.github.nextentity.api.UpdateSetStep;
import io.github.nextentity.core.*;
import io.github.nextentity.core.exception.SqlException;
import io.github.nextentity.core.interceptor.InterceptorSelector;
import io.github.nextentity.core.event.EntityEventListener;
import io.github.nextentity.core.event.EntityEventType;
import io.github.nextentity.core.meta.EntityType;
import io.github.nextentity.core.meta.Metamodel;
import io.github.nextentity.core.meta.impl.DefaultMetamodel;
import io.github.nextentity.integration.config.env.DatabaseEnvironmentVariables;
import io.github.nextentity.integration.config.fixtures.TestDataFactory;
import io.github.nextentity.integration.entity.AutoIncrementEntity;
import io.github.nextentity.integration.entity.Category;
import io.github.nextentity.integration.entity.Customer;
import io.github.nextentity.integration.entity.Department;
import io.github.nextentity.integration.entity.Employee;
import io.github.nextentity.integration.entity.LockableEntity;
import io.github.nextentity.integration.entity.SalesOrder;
import io.github.nextentity.jdbc.*;
import io.github.nextentity.jpa.JpaConfig;
import io.github.nextentity.jpa.JpaPersistExecutor;
import io.github.nextentity.jpa.JpaQueryExecutor;
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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@EntityScan("io.github.nextentity.integration.entity")
@SpringBootApplication
public class IntegrationTestApplication {

    @Bean
    public IntegrationTestContext jdbcIntegrationTestContext(JdbcTemplate jdbcTemplate,
                                                             SpringConnectionProvider connectionProvider,
                                                             TransactionTemplate transactionTemplate,
                                                             DatabaseInitializer databaseInitializer) {
        DataSource dataSource = Objects.requireNonNull(jdbcTemplate.getDataSource());
        SqlDialect sqlDialect;
        try {
            sqlDialect = SqlDialect.detectFromDataSource(dataSource);
        } catch (SQLException e) {
            throw new SqlException(e);
        }
        Metamodel metamodel = DefaultMetamodel.of();
        SqlBuilder sqlBuilder = SqlBuilder.of(sqlDialect);
        JdbcQueryExecutor queryExecutor = new JdbcQueryExecutor(
                metamodel,
                sqlBuilder,
                connectionProvider,
                new JdbcResultCollector(),
                JdbcConfig.DEFAULT,
                InterceptorSelector.empty()
        );
        PersistExecutor updateExecutor = new JdbcPersistExecutor(sqlBuilder, connectionProvider);
        updateExecutor = new TransactionUpdateExecutor(updateExecutor, new TransactionOperations() {
            @Override
            public <T> T executeInTransaction(Supplier<T> operation) {
                return transactionTemplate.execute(_ -> operation.get());
            }
        });
        EntityTemplateFactory entityTemplateFactory = new EntityTemplateFactory(new EntityTemplateFactoryConfig(
                metamodel,
                updateExecutor,
                queryExecutor,
                InterceptorSelector.empty(),
                QueryProperties.DEFAULT
        ));
        entityTemplateFactory.registerEventListener(databaseInitializer.changeTracker());
        return new SpringIntegrationTestContext(entityTemplateFactory, "jdbc");
    }

    @Bean
    public IntegrationTestContext jpaIntegrationTestContext(EntityManager entityManager,
                                                            JdbcTemplate jdbcTemplate,
                                                            SpringConnectionProvider connectionProvider,
                                                            TransactionTemplate transactionTemplate,
                                                            DatabaseInitializer databaseInitializer) {

        DataSource dataSource = Objects.requireNonNull(jdbcTemplate.getDataSource());
        SqlDialect sqlDialect;
        try {
            sqlDialect = SqlDialect.detectFromDataSource(dataSource);
        } catch (SQLException e) {
            throw new SqlException(e);
        }
        Metamodel metamodel = DefaultMetamodel.of();
        SqlBuilder sqlBuilder = SqlBuilder.of(sqlDialect);
        JdbcQueryExecutor jdbcQueryExecutor = new JdbcQueryExecutor(metamodel,
                sqlBuilder,
                connectionProvider,
                new JdbcResultCollector(),
                JdbcConfig.DEFAULT,
                InterceptorSelector.empty());
        JpaQueryExecutor queryExecutor = new JpaQueryExecutor(entityManager, jdbcQueryExecutor, JpaConfig.DEFAULT);
        PersistExecutor updateExecutor = new JpaPersistExecutor(entityManager);
        updateExecutor = new TransactionUpdateExecutor(updateExecutor, new TransactionOperations() {
            @Override
            public <T> T executeInTransaction(Supplier<T> operation) {
                return transactionTemplate.execute(_ -> operation.get());
            }
        });

        EntityTemplateFactory entityTemplateFactory = new EntityTemplateFactory(new EntityTemplateFactoryConfig(
                metamodel,
                updateExecutor,
                queryExecutor,
                InterceptorSelector.empty(),
                QueryProperties.DEFAULT
        ));
        entityTemplateFactory.registerEventListener(databaseInitializer.changeTracker());
        return new SpringIntegrationTestContext(entityTemplateFactory, "jpa");
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
        private static final List<Class<?>> RESET_ORDER = List.of(
                SalesOrder.class,
                Customer.class,
                Employee.class,
                Department.class,
                Category.class,
                LockableEntity.class,
                AutoIncrementEntity.class
        );

        private static final Set<Class<?>> FIXTURE_ENTITY_TYPES = Set.of(
                Department.class,
                Category.class,
                Employee.class,
                LockableEntity.class,
                Customer.class,
                SalesOrder.class
        );

        private final JdbcTemplate jdbcTemplate;
        private final EntityManager entityManager;
        private final ApplicationContext applicationContext;
        private final Metamodel metamodel = DefaultMetamodel.of();
        private final Map<Class<?>, String> tableNames = new ConcurrentHashMap<>();
        private final Set<Class<?>> dirtyEntityTypes = ConcurrentHashMap.newKeySet();

        public DatabaseInitializer(JdbcTemplate jdbcTemplate,
                                   EntityManager entityManager,
                                   ApplicationContext applicationContext) {
            this.jdbcTemplate = jdbcTemplate;
            this.entityManager = entityManager;
            this.applicationContext = applicationContext;
        }

        @Transactional
        public void init() {
            DatabaseEnvironmentVariables dbEnv = applicationContext.getBean(DatabaseEnvironmentVariables.class);
            for (String sql : dbEnv.ddl()) {
                jdbcTemplate.execute(sql);
            }
            loadFixtures();
        }

        @Transactional
        public void reset() {
            entityManager.clear();
            Set<Class<?>> changedEntityTypes = collectResetEntityTypes();
            for (Class<?> entityType : RESET_ORDER) {
                if (changedEntityTypes.contains(entityType)) {
                    jdbcTemplate.execute("DELETE FROM " + tableName(entityType));
                }
            }
            reloadFixtures(changedEntityTypes);
            dirtyEntityTypes.clear();
        }

        public EntityEventListener changeTracker() {
            return new EntityEventListener() {
                @Override
                public <T> void on(Class<T> entityType, EntityEventType eventType, List<T> entities, int affectedRows) {
                    switch (eventType) {
                        default -> dirtyEntityTypes.add(entityType);
                    }
                }
            };
        }

        private Set<Class<?>> collectResetEntityTypes() {
            LinkedHashSet<Class<?>> types = new LinkedHashSet<>(dirtyEntityTypes);
            if (types.contains(SalesOrder.class)) {
                types.add(Customer.class);
            }
            if (types.contains(Employee.class)) {
                types.add(Department.class);
            }
            if (types.contains(Customer.class)) {
                types.add(SalesOrder.class);
            }
            return types;
        }

        private void loadFixtures() {
            reloadFixtures(FIXTURE_ENTITY_TYPES);
            entityManager.clear();
        }

        private void reloadFixtures(Set<Class<?>> entityTypes) {
            if (entityTypes.contains(Department.class)) {
                TestDataFactory.createDepartments().forEach(entityManager::persist);
                entityManager.flush();
            }
            if (entityTypes.contains(Category.class)) {
                TestDataFactory.createCategories().forEach(entityManager::persist);
                entityManager.flush();
            }
            if (entityTypes.contains(Employee.class)) {
                TestDataFactory.createEmployees().forEach(entityManager::persist);
                entityManager.flush();
            }
            if (entityTypes.contains(LockableEntity.class)) {
                TestDataFactory.createLockableEntities().forEach(entityManager::persist);
                entityManager.flush();
            }
            if (entityTypes.contains(Customer.class)) {
                TestDataFactory.createCustomers().forEach(entityManager::persist);
                entityManager.flush();
            }
            if (entityTypes.contains(SalesOrder.class)) {
                TestDataFactory.createSalesOrders().forEach(entityManager::persist);
                entityManager.flush();
            }
        }

        private String tableName(Class<?> entityType) {
            return tableNames.computeIfAbsent(entityType, type -> {
                EntityType entity = metamodel.getEntity(type);
                return entity.tableName();
            });
        }
    }

    public static class SpringIntegrationTestContext implements IntegrationTestContext {
        @Autowired
        private DatabaseInitializer databaseInitializer;
        @Autowired
        private ApplicationContext applicationContext;

        private final String name;
        private final EntityTemplateFactory entityTemplateFactory;

        public SpringIntegrationTestContext(EntityTemplateFactory entityTemplateFactory, String name) {
            this.entityTemplateFactory = entityTemplateFactory;
            this.name = name;
        }

        @Override
        public EntityTemplateFactory getEntityTemplateFactory() {
            return entityTemplateFactory;
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
            return new UpdateSetStepImpl<>(getEntityContext(type));
        }
    }

}
