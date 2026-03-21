package io.github.nextentity.integration.config;

import io.github.nextentity.core.QueryExecutor;
import io.github.nextentity.core.UpdateExecutor;
import io.github.nextentity.core.meta.Metamodel;
import io.github.nextentity.integration.config.fixtures.TestDataFactory;
import io.github.nextentity.jdbc.*;
import io.github.nextentity.meta.jpa.JpaMetamodel;
import io.github.nextentity.integration.entity.Department;
import io.github.nextentity.integration.entity.Employee;
import org.testcontainers.containers.MySQLContainer;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * MySQL database configuration provider using Testcontainers.
 *
 * @author HuangChengwei
 */
public class Mysql extends AbstractTestcontainersDbConfigProvider implements DbConfigProvider {

    private static final MySQLContainer<?> MYSQL_CONTAINER;
    private static DbConfig instance;

    static {
        MYSQL_CONTAINER = new MySQLContainer<>("mysql:latest")
                .withDatabaseName("testdb")
                .withUsername("test")
                .withPassword("test");
        MYSQL_CONTAINER.start();
    }

    @Override
    protected MySQLContainer<?> getContainer() {
        return MYSQL_CONTAINER;
    }

    @Override
    public DbConfig getConfig() {
        if (instance != null) {
            return instance;
        }
        synchronized (Mysql.class) {
            if (instance != null) {
                return instance;
            }
            instance = createConfigInternal();
            return instance;
        }
    }

    private DbConfig createConfigInternal() {
        DataSource dataSource = getDataSource();
        Metamodel metamodel = createMetamodel();
        SqlDialectSelector dialectSelector = new SqlDialectSelector()
                .setQuerySqlBuilder(new MySqlQuerySqlBuilder())
                .setUpdateSqlBuilder(new MySqlUpdateSqlBuilder());

        ConnectionProvider connectionProvider = new SimpleConnectionProvider(dataSource);
        QueryExecutor queryExecutor = new JdbcQueryExecutor(metamodel, dialectSelector, connectionProvider, new JdbcResultCollector());
        UpdateExecutor updateExecutor = new JdbcUpdateExecutor(dialectSelector, connectionProvider, metamodel);

        DbConfig config = new DbConfig(dataSource, metamodel, queryExecutor, updateExecutor, "mysql");

        // Initialize test data
        initializeTestData(config);

        return config;
    }

    private void initializeTestData(DbConfig config) {
        try (Connection conn = config.getDataSource().getConnection()) {
            boolean autoCommit = conn.getAutoCommit();
            try {
                conn.setAutoCommit(false);

                // Drop existing tables and recreate schema
                try (var stmt = conn.createStatement()) {
                    // Drop tables if they exist (to clean up previous test data)
                    stmt.execute("DROP TABLE IF EXISTS employee");
                    stmt.execute("DROP TABLE IF EXISTS department");

                    // Create fresh tables
                    stmt.execute("""
                        CREATE TABLE department (
                            id BIGINT PRIMARY KEY,
                            name VARCHAR(100) NOT NULL,
                            location VARCHAR(100),
                            budget DOUBLE,
                            active BOOLEAN,
                            created_at TIMESTAMP
                        )
                        """);

                    stmt.execute("""
                        CREATE TABLE employee (
                            id BIGINT PRIMARY KEY,
                            name VARCHAR(100) NOT NULL,
                            email VARCHAR(100),
                            salary DOUBLE,
                            active BOOLEAN,
                            status INT,
                            department_id BIGINT,
                            hire_date DATE,
                            created_at TIMESTAMP
                        )
                        """);
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


        // Insert test data - use the current connection directly
        ConnectionProvider connectionProvider = new SimpleConnectionProvider(config.getDataSource());
        UpdateExecutor executor = new JdbcUpdateExecutor(
                new SqlDialectSelector().setQuerySqlBuilder(new MySqlQuerySqlBuilder()).setUpdateSqlBuilder(new MySqlUpdateSqlBuilder()),
                connectionProvider, config.getMetamodel());

        List<Department> departments = TestDataFactory.createDepartments();
        List<Employee> employees = TestDataFactory.createEmployees();

        for (Department dept : departments) {
            executor.insert(dept, Department.class);
        }
        for (Employee emp : employees) {
            executor.insert(emp, Employee.class);
        }
    }

    private Metamodel createMetamodel() {
        return JpaMetamodel.of();
    }
}
