package io.github.nextentity.integration.config;

import com.zaxxer.hikari.HikariDataSource;
import io.github.nextentity.core.QueryExecutor;
import io.github.nextentity.core.UpdateExecutor;
import io.github.nextentity.core.meta.Metamodel;
import io.github.nextentity.core.util.Maps;
import io.github.nextentity.integration.config.fixtures.TestDataFactory;
import io.github.nextentity.integration.entity.Department;
import io.github.nextentity.integration.entity.Employee;
import io.github.nextentity.jdbc.JdbcQueryExecutor;
import io.github.nextentity.jdbc.JdbcResultCollector;
import io.github.nextentity.jdbc.JdbcUpdateExecutor;
import io.github.nextentity.jdbc.SqlDialectSelector;
import io.github.nextentity.jpa.JpaQueryExecutor;
import io.github.nextentity.jpa.JpaUpdateExecutor;
import io.github.nextentity.meta.jpa.JpaMetamodel;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.boot.model.naming.PhysicalNamingStrategySnakeCaseImpl;
import org.hibernate.engine.jdbc.dialect.internal.StandardDialectResolver;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.hibernate.tool.schema.Action;
import org.jspecify.annotations.NonNull;
import org.testcontainers.containers.JdbcDatabaseContainer;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static org.hibernate.cfg.AvailableSettings.*;

/**
 * Abstract base class for Testcontainers-based database configuration providers.
 * <p>
 * Provides shared logic for creating DataSource and EntityManagerFactory
 * from a JdbcDatabaseContainer instance.
 *
 * @author HuangChengwei
 */
public abstract class AbstractContainerContext implements ContainerContext {

    private List<IntegrationTestContext> contexts;
    private EntityManager entityManager;

    /**
     * Returns the Testcontainers database container instance.
     * The container should be started before this method is called.
     *
     * @return the JdbcDatabaseContainer instance
     */
    protected abstract JdbcDatabaseContainer<?> getContainer();

    public DataSource getDataSource() {
        JdbcDatabaseContainer<?> container = getContainer();
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(container.getJdbcUrl());
        dataSource.setUsername(container.getUsername());
        dataSource.setPassword(container.getPassword());
        dataSource.setDriverClassName(container.getDriverClassName());
        return dataSource;
    }

    public EntityManagerFactory getEntityManagerFactory() {
        JdbcDatabaseContainer<?> container = getContainer();
        Map<String, Object> properties = Maps.<String, Object>hashmap()
                .put(JAKARTA_JDBC_DRIVER, container.getDriverClassName())
                .put(JAKARTA_JDBC_URL, container.getJdbcUrl())
                .put(JAKARTA_JDBC_USER, container.getUsername())
                .put(JAKARTA_JDBC_PASSWORD, container.getPassword())
                .put(DIALECT_RESOLVERS, StandardDialectResolver.class.getName())
                .put(HBM2DDL_AUTO, Action.UPDATE)
                .put(SHOW_SQL, true)
                .put(FORMAT_SQL, false)
                .put(QUERY_STARTUP_CHECKING, false)
                .put(GENERATE_STATISTICS, false)
                .put(USE_SECOND_LEVEL_CACHE, false)
                .put(USE_QUERY_CACHE, false)
                .put(USE_STRUCTURED_CACHE, false)
                .put(STATEMENT_BATCH_SIZE, 2000)
                .put(PHYSICAL_NAMING_STRATEGY, PhysicalNamingStrategySnakeCaseImpl.class)
                .build();
        return new HibernatePersistenceProvider()
                .createContainerEntityManagerFactory(new HibernateUnitInfo(), properties);
    }

    @Override
    public List<IntegrationTestContext> getConfigs() {
        if (contexts == null) {

            DataSource dataSource = getDataSource();
            Metamodel metamodel = JpaMetamodel.of();
            SqlDialectSelector dialectSelector = getSqlDialectSelector();

            SimpleConnectionProvider connectionProvider = new SimpleConnectionProvider(dataSource);
            QueryExecutor jdbcQueryExecutor = new JdbcQueryExecutor(metamodel, dialectSelector, connectionProvider, new JdbcResultCollector());
            UpdateExecutor jdbcUpdateExecutor = new JdbcUpdateExecutor(dialectSelector, connectionProvider, metamodel);

            IntegrationTestContext jdbc = new IntegrationTestContext(this, dataSource, metamodel, jdbcQueryExecutor, jdbcUpdateExecutor, getDialect(), "jdbc");

            this.entityManager = getEntityManagerFactory().createEntityManager();
            JpaQueryExecutor jpaQueryExecutor = new JpaQueryExecutor(entityManager, metamodel, jdbcQueryExecutor);
            JpaUpdateExecutor jpaUpdateExecutor = new JpaUpdateExecutor(entityManager);

            IntegrationTestContext jpa = new IntegrationTestContext(this, getDataSource(), metamodel, jpaQueryExecutor, jpaUpdateExecutor, getDialect(), "jpa");

            contexts = List.of(jdbc, jpa);

        }
        return contexts;
    }

    protected abstract SqlDialectSelector getSqlDialectSelector();

    protected abstract @NonNull List<String> resetDdlSql();

    protected abstract @NonNull String getDialect();

    @Override
    public void reset(IntegrationTestContext context) {
        resetTable(context);
        // Insert test data - use the current connection directly
        UpdateExecutor executor = context.getUpdateExecutor();

        List<Department> departments = TestDataFactory.createDepartments();
        List<Employee> employees = TestDataFactory.createEmployees();
        entityManager.clear();
        executor.insertAll(departments, Department.class);
        executor.insertAll(employees, Employee.class);
    }

    protected void resetTable(IntegrationTestContext context) {
        DataSource dataSource = context.getDataSource();
        try (Connection conn = dataSource.getConnection()) {
            boolean autoCommit = conn.getAutoCommit();
            try {
                conn.setAutoCommit(false);

                // Drop existing tables and recreate schema
                try (var stmt = conn.createStatement()) {
                    // Drop tables if they exist (to clean up previous test data)

                    List<String> sqls = resetDdlSql();
                    for (String sql : sqls) {
                        stmt.execute(sql);
                    }
                }
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw new RuntimeException(e);
            } finally {
                if (autoCommit) {
                    try {
                        conn.setAutoCommit(true);
                    } catch (SQLException e) {
                        // ignore
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
