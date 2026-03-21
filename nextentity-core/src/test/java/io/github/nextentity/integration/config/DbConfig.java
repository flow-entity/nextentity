package io.github.nextentity.integration.config;

import io.github.nextentity.core.QueryBuilder;
import io.github.nextentity.core.QueryExecutor;
import io.github.nextentity.core.UpdateExecutor;
import io.github.nextentity.core.meta.Metamodel;
import io.github.nextentity.integration.entity.Department;
import io.github.nextentity.integration.entity.Employee;

import javax.sql.DataSource;

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
    private final String implType;
    private final ContainerContext containerContext;

    public DbConfig(ContainerContext containerContext,
                    DataSource dataSource,
                    Metamodel metamodel,
                    QueryExecutor queryExecutor,
                    UpdateExecutor updateExecutor,
                    String dialect,
                    String implType) {
        this.containerContext = containerContext;
        this.dataSource = dataSource;
        this.metamodel = metamodel;
        this.queryExecutor = queryExecutor;
        this.updateExecutor = updateExecutor;
        this.dialect = dialect;
        this.implType = implType;
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

    public DbConfig reset() {
        containerContext.reset(this);
        return this;
    }

    @Override
    public String toString() {
        return dialect + "-" + implType;
    }
}
