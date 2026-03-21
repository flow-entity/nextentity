package io.github.nextentity.test.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.nextentity.core.QueryBuilder;
import io.github.nextentity.core.QueryExecutor;
import io.github.nextentity.core.UpdateExecutor;
import io.github.nextentity.core.exception.TransactionRequiredException;
import io.github.nextentity.core.meta.Metamodel;
import io.github.nextentity.jdbc.*;
import io.github.nextentity.test.entity.Department;
import io.github.nextentity.test.entity.Employee;
import io.github.nextentity.test.fixtures.TestDataFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.function.Supplier;

/**
 * Base class for integration tests with H2 in-memory database.
 */
public abstract class AbstractIntegrationTest {

    protected static final String H2_URL = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=MySQL";
    protected static final String H2_USER = "sa";
    protected static final String H2_PASSWORD = "";

    protected DataSource dataSource;
    protected Metamodel metamodel;
    protected QueryExecutor queryExecutor;
    protected UpdateExecutor updateExecutor;
    private SqlDialectSelector dialectSelector;

    @BeforeEach
    void setUpDatabase() throws SQLException {
        dataSource = createDataSource();
        metamodel = createMetamodel();
        dialectSelector = new SqlDialectSelector().
                setQuerySqlBuilder(new MySqlQuerySqlBuilder())
                .setUpdateSqlBuilder(new MySqlUpdateSqlBuilder());
        queryExecutor = createQueryExecutor();
        updateExecutor = createUpdateExecutor();

        initializeSchema();
        doInTransaction(this::initializeData);
    }

    @AfterEach
    void tearDownDatabase() throws SQLException {
        cleanupData();
        if (dataSource instanceof HikariDataSource hikariDataSource) {
            hikariDataSource.close();
        }
    }

    protected DataSource createDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(H2_URL);
        config.setUsername(H2_USER);
        config.setPassword(H2_PASSWORD);
        config.setDriverClassName("org.h2.Driver");
        return new HikariDataSource(config);
    }

    protected Metamodel createMetamodel() {
        return TestMetamodel.create();
    }

    protected QueryExecutor createQueryExecutor() {
        ConnectionProvider connectionProvider = createConnectionProvider();
        JdbcQueryExecutor.ResultCollector collector = new JdbcResultCollector();
        return new JdbcQueryExecutor(metamodel, dialectSelector, connectionProvider, collector);
    }

    protected UpdateExecutor createUpdateExecutor() {
        ConnectionProvider connectionProvider = createConnectionProvider();
        return new JdbcUpdateExecutor(dialectSelector, connectionProvider, metamodel);
    }

    protected ConnectionProvider createConnectionProvider() {
        return new TestConnectionProvider(dataSource);
    }

    protected void initializeSchema() throws SQLException {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {

            // Create department table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS Department (
                    id BIGINT PRIMARY KEY,
                    name VARCHAR(100) NOT NULL,
                    location VARCHAR(100),
                    budget DOUBLE,
                    active BOOLEAN,
                    createdAt TIMESTAMP
                )
                """);

            // Create employee table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS Employee (
                    id BIGINT PRIMARY KEY,
                    name VARCHAR(100) NOT NULL,
                    email VARCHAR(100),
                    salary DOUBLE,
                    active BOOLEAN,
                    status VARCHAR(20),
                    departmentId BIGINT,
                    hireDate DATE,
                    createdAt TIMESTAMP
                )
                """);
        }
    }

    protected void initializeData() {
        List<Department> departments = TestDataFactory.createDepartments();
        List<Employee> employees = TestDataFactory.createEmployees();

        // Insert departments
        updateExecutor.insertAll(departments, Department.class);

        // Insert employees
        updateExecutor.insertAll(employees, Employee.class);
    }

    protected void cleanupData() throws SQLException {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM Employee");
            stmt.execute("DELETE FROM Department");
        }
    }

    protected <T> QueryBuilder<T> query(Class<T> entityType) {
        return new QueryBuilder<>(metamodel, queryExecutor, entityType);
    }

    protected Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    /**
     * Execute an action in a transaction.
     */
    protected void doInTransaction(Runnable action) {
        try (Connection conn = dataSource.getConnection()) {
            boolean autoCommit = conn.getAutoCommit();
            try {
                if (autoCommit) {
                    conn.setAutoCommit(false);
                }
                TestConnectionProvider.setCurrentConnection(conn);
                action.run();
                conn.commit();
            } catch (Throwable e) {
                conn.rollback();
                throw new RuntimeException(e);
            } finally {
                TestConnectionProvider.clearCurrentConnection();
                if (autoCommit) {
                    conn.setAutoCommit(true);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Execute an action in a transaction and return a result.
     */
    protected <T> T doInTransaction(Supplier<T> action) {
        try (Connection conn = dataSource.getConnection()) {
            boolean autoCommit = conn.getAutoCommit();
            try {
                if (autoCommit) {
                    conn.setAutoCommit(false);
                }
                TestConnectionProvider.setCurrentConnection(conn);
                T result = action.get();
                conn.commit();
                return result;
            } catch (Throwable e) {
                conn.rollback();
                throw new RuntimeException(e);
            } finally {
                TestConnectionProvider.clearCurrentConnection();
                if (autoCommit) {
                    conn.setAutoCommit(true);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Test connection provider that supports thread-local connections for transactions.
     */
    private static class TestConnectionProvider implements ConnectionProvider {

        private static final ThreadLocal<Connection> currentConnection = new ThreadLocal<>();
        private final DataSource dataSource;

        TestConnectionProvider(DataSource dataSource) {
            this.dataSource = dataSource;
        }

        static void setCurrentConnection(Connection connection) {
            currentConnection.set(connection);
        }

        static void clearCurrentConnection() {
            currentConnection.remove();
        }

        @Override
        public <T> T execute(ConnectionCallback<T> action) throws SQLException {
            Connection conn = currentConnection.get();
            if (conn != null) {
                // Already in a transaction
                if (conn.getAutoCommit()) {
                    throw new TransactionRequiredException();
                }
                return action.doInConnection(conn);
            } else {
                // Not in a transaction - for queries, this is fine
                try (Connection connection = dataSource.getConnection()) {
                    return action.doInConnection(connection);
                }
            }
        }
    }
}
