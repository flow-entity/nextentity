package io.github.nextentity.integration.config;

import com.zaxxer.hikari.HikariDataSource;
import io.github.nextentity.core.util.Maps;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.boot.model.naming.PhysicalNamingStrategySnakeCaseImpl;
import org.hibernate.engine.jdbc.dialect.internal.StandardDialectResolver;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.hibernate.tool.schema.Action;
import org.testcontainers.containers.JdbcDatabaseContainer;

import javax.sql.DataSource;
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
public abstract class AbstractTestcontainersDbConfigProvider {

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
                .put(FORMAT_SQL, true)
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
}
