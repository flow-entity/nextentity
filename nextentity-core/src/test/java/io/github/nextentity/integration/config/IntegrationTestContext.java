package io.github.nextentity.integration.config;

import io.github.nextentity.api.DeleteWhereStep;
import io.github.nextentity.api.UpdateSetStep;
import io.github.nextentity.core.*;
import io.github.nextentity.core.interceptor.InterceptorSelector;
import io.github.nextentity.core.meta.impl.DefaultMetamodel;
import io.github.nextentity.integration.entity.*;
import org.jspecify.annotations.NonNull;

import java.util.function.Supplier;

///
/// Database configuration for integration tests.
/// Encapsulates DataSource, QueryExecutor, UpdateExecutor, and test data.
///
/// @author HuangChengwei
public interface IntegrationTestContext {

    QueryExecutor getQueryExecutor();

    PersistExecutor getUpdateExecutor();

    /// 创建实体上下文，用于更新操作。
    ///
    /// @param entityClass 实体类
    /// @param <T>         实体类型
    /// @return 实体上下文实例
    default <T> EntityTemplateDescriptor<?, T> getEntityContext(Class<T> entityClass) {
        DefaultMetamodel metamodel = DefaultMetamodel.of();
        EntityTemplateFactoryConfig config = new EntityTemplateFactoryConfig(
                metamodel, getUpdateExecutor(), getQueryExecutor(), InterceptorSelector.empty(),
                QueryProperties.DEFAULT
        );
        return new EntityTemplateDescriptor<>(config, entityClass);
    }

    default EntityQueryImpl<Employee> queryEmployees() {
        return new EntityQueryImpl<>(getEntityContext(Employee.class));
    }

    default EntityQueryImpl<Department> queryDepartments() {
        return new EntityQueryImpl<>(getEntityContext(Department.class));
    }

    default EntityQueryImpl<LockableEntity> queryLockableEntities() {
        return new EntityQueryImpl<>(getEntityContext(LockableEntity.class));
    }

    default EntityQueryImpl<AutoIncrementEntity> queryAutoIncrementEntities() {
        return new EntityQueryImpl<>(getEntityContext(AutoIncrementEntity.class));
    }

    default EntityQueryImpl<Category> queryCategories() {
        return new EntityQueryImpl<>(getEntityContext(Category.class));
    }

    default EntityQueryImpl<SalesOrder> querySalesOrders() {
        return new EntityQueryImpl<>(getEntityContext(SalesOrder.class));
    }

    default EntityQueryImpl<Customer> queryCustomers() {
        return new EntityQueryImpl<>(getEntityContext(Customer.class));
    }

    @NonNull IntegrationTestContext reset();

    <T> T doInTransaction(Supplier<T> runnable);

    default void doInTransaction(Runnable runnable) {
        doInTransaction(() -> {
            runnable.run();
            return null;
        });
    }

    <T> UpdateSetStep<T> update(Class<T> type);

    <T> UpdateSetStep<T> update(EntityTemplateDescriptor<?, T> type);

    default <T> DeleteWhereStep<T> delete(EntityTemplateDescriptor<?, T> entityContext) {
        return new DeleteWhereStepImpl<>(entityContext);
    }
}
