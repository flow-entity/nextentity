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
        var metamodel = JpaMetamodel.of();
        return new DefaultQueryBuilder<>(new SimpleQueryContext<>(metamodel, getQueryExecutor(), PaginationConfig.DEFAULT, metamodel.getEntity(Employee.class), Employee.class));
    }

    default DefaultQueryBuilder<Department> queryDepartments() {
        var metamodel = JpaMetamodel.of();
        return new DefaultQueryBuilder<>(new SimpleQueryContext<>(metamodel, getQueryExecutor(), PaginationConfig.DEFAULT, metamodel.getEntity(Department.class), Department.class));
    }

    default DefaultQueryBuilder<LockableEntity> queryLockableEntities() {
        var metamodel = JpaMetamodel.of();
        return new DefaultQueryBuilder<>(new SimpleQueryContext<>(metamodel, getQueryExecutor(), PaginationConfig.DEFAULT, metamodel.getEntity(LockableEntity.class), LockableEntity.class));
    }

    default DefaultQueryBuilder<AutoIncrementEntity> queryAutoIncrementEntities() {
        var metamodel = JpaMetamodel.of();
        return new DefaultQueryBuilder<>(new SimpleQueryContext<>(metamodel, getQueryExecutor(), PaginationConfig.DEFAULT, metamodel.getEntity(AutoIncrementEntity.class), AutoIncrementEntity.class));
    }

    @NonNull IntegrationTestContext reset();
}
