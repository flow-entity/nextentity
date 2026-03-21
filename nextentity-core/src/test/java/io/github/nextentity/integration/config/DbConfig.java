package io.github.nextentity.integration.config;

import io.github.nextentity.core.QueryBuilder;
import io.github.nextentity.core.QueryExecutor;
import io.github.nextentity.core.UpdateExecutor;
import io.github.nextentity.core.meta.Metamodel;
import io.github.nextentity.integration.config.fixtures.TestDataFactory;
import io.github.nextentity.integration.entity.Department;
import io.github.nextentity.integration.entity.Employee;
import io.github.nextentity.jdbc.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Database configuration for integration tests.
 * Encapsulates DataSource, QueryExecutor, UpdateExecutor, and test data.
 *
 * @author HuangChengwei
 */
public class DbConfig {

    private final DataSource dataSource;
    private final Metamodel metamodel;
    private final QueryExecutor queryExecutor;
    private final UpdateExecutor updateExecutor;
    private final String dialect;

    private List<Employee> employees;
    private List<Department> departments;



    public DbConfig(DataSource dataSource, Metamodel metamodel,
                    QueryExecutor queryExecutor, UpdateExecutor updateExecutor, String dialect) {
        this.dataSource = dataSource;
        this.metamodel = metamodel;
        this.queryExecutor = queryExecutor;
        this.updateExecutor = updateExecutor;
        this.dialect = dialect;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public Metamodel getMetamodel() {
        return metamodel;
    }

    public QueryExecutor getQueryExecutor() {
        return queryExecutor;
    }

    public UpdateExecutor getUpdateExecutor() {
        return updateExecutor;
    }

    public String getDialect() {
        return dialect;
    }

    public QueryBuilder<Employee> queryEmployees() {
        return new QueryBuilder<>(metamodel, queryExecutor, Employee.class);
    }

    public QueryBuilder<Department> queryDepartments() {
        return new QueryBuilder<>(metamodel, queryExecutor, Department.class);
    }

    /**
     * Resets test data by dropping and recreating tables.
     * Should be called before each test to ensure clean state.
     */
    public void resetTestData() {
        try (Connection conn = dataSource.getConnection()) {
            boolean autoCommit = conn.getAutoCommit();
            try {
                conn.setAutoCommit(false);

                try (var stmt = conn.createStatement()) {
                    if ("mysql".equals(dialect)) {
                        stmt.execute("DROP TABLE IF EXISTS employee");
                        stmt.execute("DROP TABLE IF EXISTS department");
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
                                status VARCHAR(20),
                                department_id BIGINT,
                                hire_date DATE,
                                created_at TIMESTAMP
                            )
                            """);
                    } else if ("postgresql".equals(dialect)) {
                        stmt.execute("DROP TABLE IF EXISTS employee");
                        stmt.execute("DROP TABLE IF EXISTS department");
                        stmt.execute("""
                            CREATE TABLE "department" (
                                id BIGINT PRIMARY KEY,
                                name VARCHAR(100) NOT NULL,
                                location VARCHAR(100),
                                budget DOUBLE PRECISION,
                                active BOOLEAN,
                                created_at TIMESTAMP
                            )
                            """);
                        stmt.execute("""
                            CREATE TABLE "employee" (
                                id BIGINT PRIMARY KEY,
                                name VARCHAR(100) NOT NULL,
                                email VARCHAR(100),
                                salary DOUBLE PRECISION,
                                active BOOLEAN,
                                status INTEGER,
                                department_id BIGINT,
                                hire_date DATE,
                                created_at TIMESTAMP
                            )
                            """);
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

        // Re-insert test data
        UpdateExecutor executor = new JdbcUpdateExecutor(
                new SqlDialectSelector()
                        .setQuerySqlBuilder(dialect.equals("mysql") ? new MySqlQuerySqlBuilder() : new PostgresqlQuerySqlBuilder())
                        .setUpdateSqlBuilder(dialect.equals("mysql") ? new MySqlUpdateSqlBuilder() : new PostgreSqlUpdateSqlBuilder()),
                new SimpleConnectionProvider(dataSource),
                metamodel);

        List<Department> departments = TestDataFactory.createDepartments();
        List<Employee> employees = TestDataFactory.createEmployees();

        for (Department dept : departments) {
            executor.insert(dept, Department.class);
        }
        for (Employee emp : employees) {
            executor.insert(emp, Employee.class);
        }

        this.departments = departments;
        this.employees = employees;
    }
}
