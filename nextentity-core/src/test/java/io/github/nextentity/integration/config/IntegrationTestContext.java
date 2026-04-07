package io.github.nextentity.integration.config;

import io.github.nextentity.core.DefaultQueryBuilder;
import io.github.nextentity.core.PaginationConfig;
import io.github.nextentity.core.QueryExecutor;
import io.github.nextentity.core.SimpleQueryContext;
import io.github.nextentity.core.UpdateExecutor;
import io.github.nextentity.integration.entity.AutoIncrementEntity;
import io.github.nextentity.integration.entity.Department;
import io.github.nextentity.integration.entity.Employee;
import io.github.nextentity.integration.entity.LockableEntity;
import io.github.nextentity.meta.jpa.JpaMetamodel;
import org.jspecify.annotations.NonNull;

///
 /// Database configuration for integration tests.
 /// Encapsulates DataSource, QueryExecutor, UpdateExecutor, and test data.
 ///
 /// @author HuangChengwei
public interface IntegrationTestContext {

    QueryExecutor getQueryExecutor();

    UpdateExecutor getUpdateExecutor();

    default DefaultQueryBuilder<Employee> queryEmployees() {
        return new DefaultQueryBuilder<>(new SimpleQueryContext(JpaMetamodel.of(), getQueryExecutor(), PaginationConfig.DEFAULT), Employee.class);
    }

    default DefaultQueryBuilder<Department> queryDepartments() {
        return new DefaultQueryBuilder<>(new SimpleQueryContext(JpaMetamodel.of(), getQueryExecutor(), PaginationConfig.DEFAULT), Department.class);
    }

    default DefaultQueryBuilder<LockableEntity> queryLockableEntities() {
        return new DefaultQueryBuilder<>(new SimpleQueryContext(JpaMetamodel.of(), getQueryExecutor(), PaginationConfig.DEFAULT), LockableEntity.class);
    }

    default DefaultQueryBuilder<AutoIncrementEntity> queryAutoIncrementEntities() {
        return new DefaultQueryBuilder<>(new SimpleQueryContext(JpaMetamodel.of(), getQueryExecutor(), PaginationConfig.DEFAULT), AutoIncrementEntity.class);
    }

    @NonNull IntegrationTestContext reset();
}
