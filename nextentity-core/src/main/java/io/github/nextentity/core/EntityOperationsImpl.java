package io.github.nextentity.core;

import io.github.nextentity.api.DeleteWhereStep;
import io.github.nextentity.api.EntityDescriptor;
import io.github.nextentity.api.EntityOperations;
import io.github.nextentity.api.UpdateSetStep;
import org.jspecify.annotations.NonNull;

import java.util.function.Supplier;

/// EntityOperations 接口的默认实现。
///
/// 继承 DefaultQueryBuilder 获得完整的查询构建能力，
/// 并通过组合 UpdateExecutor 添加实体更新能力。
///
/// @param <T> 实体类型
/// @author HuangChengwei
/// @since 2.0.0
public class EntityOperationsImpl<T> extends EntityQueryImpl<T> implements EntityOperations<T> {

    private final @NonNull UpdateExecutor updateExecutor;

    /// 创建 EntityOperationsImpl 实例。
    ///
    /// @param descriptor        查询上下文，提供查询所需依赖
    /// @param updateExecutor 更新执行器，用于执行实体更新操作
    public EntityOperationsImpl(@NonNull QueryDescriptor<T> descriptor,
                                @NonNull UpdateExecutor updateExecutor) {
        super(descriptor);
        this.updateExecutor = updateExecutor;
    }

    @Override
    public void insert(T entity) {
        updateExecutor.insert(entity, descriptor);
    }

    @Override
    public void insertAll(Iterable<T> entities) {
        updateExecutor.insertAll(entities, descriptor);
    }

    @Override
    public void update(T entity) {
        updateExecutor.update(entity, descriptor);
    }

    @Override
    public void updateAll(Iterable<T> entities) {
        updateExecutor.updateAll(entities, descriptor);
    }

    @Override
    public void delete(T entity) {
        updateExecutor.delete(entity, descriptor);
    }

    @Override
    public void deleteAll(Iterable<T> entities) {
        updateExecutor.deleteAll(entities, descriptor);
    }

    @Override
    public UpdateSetStep<T> update() {
        return updateExecutor.update(descriptor);
    }

    @Override
    public DeleteWhereStep<T> delete() {
        return updateExecutor.delete(descriptor);
    }

    @Override
    public void doInTransaction(Runnable command) {
        updateExecutor.doInTransaction(command);
    }

    @Override
    public <X> X doInTransaction(Supplier<X> command) {
        return updateExecutor.doInTransaction(command);
    }

    @Override
    public EntityDescriptor<T> descriptor() {
        return descriptor;
    }
}