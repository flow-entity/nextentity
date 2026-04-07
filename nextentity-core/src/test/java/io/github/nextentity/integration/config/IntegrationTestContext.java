package io.github.nextentity.integration.config;

import io.github.nextentity.core.DefaultQueryBuilder;
import io.github.nextentity.core.QueryExecutor;
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
        return new DefaultQueryBuilder<>(JpaMetamodel.of(), getQueryExecutor(), Employee.class);
    }

    default DefaultQueryBuilder<Department> queryDepartments() {
        return new DefaultQueryBuilder<>(JpaMetamodel.of(), getQueryExecutor(), Department.class);
    }

    default DefaultQueryBuilder<LockableEntity> queryLockableEntities() {
        return new DefaultQueryBuilder<>(JpaMetamodel.of(), getQueryExecutor(), LockableEntity.class);
    }

    default DefaultQueryBuilder<AutoIncrementEntity> queryAutoIncrementEntities() {
        return new DefaultQueryBuilder<>(JpaMetamodel.of(), getQueryExecutor(), AutoIncrementEntity.class);
    }

    @NonNull IntegrationTestContext reset();
}
