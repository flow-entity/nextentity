package io.github.nextentity.integration.config;

import io.github.nextentity.core.EntityQueryImpl;
import io.github.nextentity.api.EntityDescriptor;
import io.github.nextentity.core.PaginationConfig;
import io.github.nextentity.core.QueryExecutor;
import io.github.nextentity.core.SimpleEntityDescriptor;
import io.github.nextentity.core.SimpleQueryDescriptor;
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
    default <T> EntityDescriptor<T> getEntityContext(Class<T> entityClass) {
        var metamodel = JpaMetamodel.of();
        return new SimpleEntityDescriptor<>(metamodel, metamodel.getEntity(entityClass), entityClass);
    }

    default EntityQueryImpl<Employee> queryEmployees() {
        var metamodel = JpaMetamodel.of();
        return new EntityQueryImpl<>(new SimpleQueryDescriptor<>(metamodel, getQueryExecutor(), PaginationConfig.DEFAULT, metamodel.getEntity(Employee.class), Employee.class));
    }

    default EntityQueryImpl<Department> queryDepartments() {
        var metamodel = JpaMetamodel.of();
        return new EntityQueryImpl<>(new SimpleQueryDescriptor<>(metamodel, getQueryExecutor(), PaginationConfig.DEFAULT, metamodel.getEntity(Department.class), Department.class));
    }

    default EntityQueryImpl<LockableEntity> queryLockableEntities() {
        var metamodel = JpaMetamodel.of();
        return new EntityQueryImpl<>(new SimpleQueryDescriptor<>(metamodel, getQueryExecutor(), PaginationConfig.DEFAULT, metamodel.getEntity(LockableEntity.class), LockableEntity.class));
    }

    default EntityQueryImpl<AutoIncrementEntity> queryAutoIncrementEntities() {
        var metamodel = JpaMetamodel.of();
        return new EntityQueryImpl<>(new SimpleQueryDescriptor<>(metamodel, getQueryExecutor(), PaginationConfig.DEFAULT, metamodel.getEntity(AutoIncrementEntity.class), AutoIncrementEntity.class));
    }

    @NonNull IntegrationTestContext reset();
}
