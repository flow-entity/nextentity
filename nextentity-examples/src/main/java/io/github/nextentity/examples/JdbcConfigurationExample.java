package io.github.nextentity.examples;

import io.github.nextentity.core.QueryBuilder;
import io.github.nextentity.core.QueryExecutor;
import io.github.nextentity.core.UpdateExecutor;
import io.github.nextentity.core.meta.Metamodel;
import io.github.nextentity.examples.entity.Employee;
import io.github.nextentity.meta.jpa.JpaMetamodel;

import java.util.List;

/**
 * JDBC Configuration Example.
 * <p>
 * This example demonstrates how to use NextEntity with pure JDBC
 * for maximum performance and control.
 *
 * <h2>Dependencies (pom.xml):</h2>
 * <pre>{@code
 * <dependencies>
 *     <!-- NextEntity Core -->
 *     <dependency>
 *         <groupId>io.github.flow-entity</groupId>
 *         <artifactId>nextentity-core</artifactId>
 *         <version>2.0.0</version>
 *     </dependency>
 *
 *     <!-- JDBC Driver (e.g., MySQL) -->
 *     <dependency>
 *         <groupId>com.mysql</groupId>
 *         <artifactId>mysql-connector-j</artifactId>
 *     </dependency>
 *
 *     <!-- Connection Pool (HikariCP) -->
 *     <dependency>
 *         <groupId>com.zaxxer</groupId>
 *         <artifactId>HikariCP</artifactId>
 *     </dependency>
 * </dependencies>
 * }</pre>
 *
 * <h2>JDBC vs JPA:</h2>
 * <ul>
 *   <li><b>JDBC:</b> Better performance, more control, no JPA dependency</li>
 *   <li><b>JPA:</b> Easier entity management, lazy loading, caching</li>
 * </ul>
 */
public class JdbcConfigurationExample {

    private final QueryExecutor queryExecutor;
    private final UpdateExecutor updateExecutor;

    /**
     * Example JDBC setup with HikariCP.
     * <p>
     * <pre>{@code
     * // Create DataSource
     * HikariConfig config = new HikariConfig();
     * config.setJdbcUrl("jdbc:mysql://localhost:3306/mydb");
     * config.setUsername("root");
     * config.setPassword("password");
     * config.setDriverClassName("com.mysql.cj.jdbc.Driver");
     *
     * DataSource dataSource = new HikariDataSource(config);
     *
     * // Create executors (using Metamodel for entity metadata)
     * Metamodel metamodel = JpaMetamodel.of();
     * QueryExecutor queryExecutor = new JdbcQueryExecutor(dataSource, metamodel);
     * UpdateExecutor updateExecutor = new JdbcUpdateExecutor(dataSource, metamodel);
     * }</pre>
     */
    public JdbcConfigurationExample(QueryExecutor queryExecutor, UpdateExecutor updateExecutor) {
        this.queryExecutor = queryExecutor;
        this.updateExecutor = updateExecutor;
    }

    /**
     * Query example with JDBC.
     * <p>
     * Same API as JPA, but uses pure JDBC under the hood.
     * <pre>{@code
     * // Query employees
     * Metamodel metamodel = JpaMetamodel.of();
     *
     * List<Employee> employees = new QueryBuilder<>(
     *     metamodel, queryExecutor, Employee.class
     * )
     * .where(Employee::getActive).eq(true)
     * .orderBy(Employee::getName).asc()
     * .getList();
     * }</pre>
     */
    public List<Employee> findActiveEmployees() {
        Metamodel metamodel = JpaMetamodel.of();
        return new QueryBuilder<>(metamodel, queryExecutor, Employee.class)
                .where(Employee::getActive).eq(true)
                .orderBy(Employee::getName).asc()
                .getList();
    }

    /**
     * Batch insert with JDBC.
     * <p>
     * JDBC batch operations are typically faster than JPA.
     * <pre>{@code
     * // Batch insert
     * List<Employee> employees = List.of(
     *     createEmployee(1L, "Alice", "alice@example.com"),
     *     createEmployee(2L, "Bob", "bob@example.com"),
     *     createEmployee(3L, "Charlie", "charlie@example.com")
     * );
     *
     * // Uses JDBC batch update for efficiency
     * updateExecutor.insertAll(employees, Employee.class);
     * }</pre>
     */
    public void batchInsertEmployees(List<Employee> employees) {
        updateExecutor.insertAll(employees, Employee.class);
    }

    /**
     * Transaction management with JDBC.
     * <p>
     * <pre>{@code
     * // Execute in transaction
     * updateExecutor.doInTransaction(() -> {
     *     // Insert department
     *     Department dept = new Department(1L, "Engineering", "Building A", 100000.0, true);
     *     updateExecutor.insert(dept, Department.class);
     *
     *     // Insert employees
     *     Employee emp = new Employee();
     *     emp.setDepartmentId(1L);
     *     updateExecutor.insert(emp, Employee.class);
     * });
     * }</pre>
     */
    public void createDepartmentWithEmployee() {
        updateExecutor.doInTransaction(() -> {
            // Operations within transaction
            // If any operation fails, all are rolled back
        });
    }

    /**
     * JDBC-specific optimizations.
     * <p>
     * <pre>{@code
     * // For best JDBC performance:
     * // 1. Use connection pooling (HikariCP)
     * // 2. Configure batch size
     * // 3. Use rewriteBatchedStatements=true for MySQL
     *
     * // HikariCP configuration
     * HikariConfig config = new HikariConfig();
     * config.setMaximumPoolSize(20);
     * config.setMinimumIdle(5);
     * config.setConnectionTimeout(30000);
     * config.setIdleTimeout(600000);
     * config.setMaxLifetime(1800000);
     *
     * // MySQL batch optimization
     * config.setJdbcUrl("jdbc:mysql://localhost/db?rewriteBatchedStatements=true");
     * }</pre>
     */
}