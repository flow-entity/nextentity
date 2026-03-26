package io.github.nextentity.integration.config;

import io.github.nextentity.core.QueryBuilder;
import io.github.nextentity.core.QueryExecutor;
import io.github.nextentity.core.UpdateExecutor;
import io.github.nextentity.integration.entity.Department;
import io.github.nextentity.integration.entity.Employee;
import io.github.nextentity.meta.jpa.JpaMetamodel;
import org.jspecify.annotations.NonNull;

/**
 * Database configuration for integration tests.
 * Encapsulates DataSource, QueryExecutor, UpdateExecutor, and test data.
 *
 * @author HuangChengwei
 */
public interface IntegrationTestContext {

    QueryExecutor getQueryExecutor();

    UpdateExecutor getUpdateExecutor();

    default QueryBuilder<Employee> queryEmployees() {
        return new QueryBuilder<>(JpaMetamodel.of(), getQueryExecutor(), Employee.class);
    }

    default QueryBuilder<Department> queryDepartments() {
        return new QueryBuilder<>(JpaMetamodel.of(), getQueryExecutor(), Department.class);
    }

    @NonNull IntegrationTestContext reset();
}
