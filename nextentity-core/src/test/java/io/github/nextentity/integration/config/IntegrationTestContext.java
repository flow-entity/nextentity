package io.github.nextentity.integration.config;

import io.github.nextentity.core.DefaultQueryBuilder;
import io.github.nextentity.core.EntityContext;
import io.github.nextentity.core.PaginationConfig;
import io.github.nextentity.core.QueryExecutor;
import io.github.nextentity.core.SimpleEntityContext;
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

    /// 创建实体上下文，用于更新操作。
    ///
    /// @param entityClass 实体类
    /// @param <T>         实体类型
    /// @return 实体上下文实例
    default <T> EntityContext<T> getEntityContext(Class<T> entityClass) {
        var metamodel = JpaMetamodel.of();
        return new SimpleEntityContext<>(metamodel, metamodel.getEntity(entityClass), entityClass);
    }

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
