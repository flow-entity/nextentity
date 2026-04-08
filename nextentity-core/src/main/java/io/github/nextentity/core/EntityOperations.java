package io.github.nextentity.core;

import io.github.nextentity.api.DeleteWhereStep;
import io.github.nextentity.api.EntityUpdater;
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
public class EntityOperations<T> extends EntityQueryImpl<T> implements EntityUpdater<T> {

    private final @NonNull UpdateExecutor updateExecutor;

    /// 创建 EntityOperationsImpl 实例。
    ///
    /// @param context        查询上下文，提供查询所需依赖
    /// @param updateExecutor 更新执行器，用于执行实体更新操作
    public EntityOperations(@NonNull QueryContext<T> context,
                            @NonNull UpdateExecutor updateExecutor) {
        super(context);
        this.updateExecutor = updateExecutor;
    }

    @Override
    public void insert(T entity) {
        updateExecutor.insert(entity, entityContext());
    }

    @Override
    public void insertAll(Iterable<T> entities) {
        updateExecutor.insertAll(entities, entityContext());
    }

    @Override
    public void update(T entity) {
        updateExecutor.update(entity, entityContext());
    }

    @Override
    public void updateAll(Iterable<T> entities) {
        updateExecutor.updateAll(entities, entityContext());
    }

    @Override
    public void delete(T entity) {
        updateExecutor.delete(entity, entityContext());
    }

    @Override
    public void deleteAll(Iterable<T> entities) {
        updateExecutor.deleteAll(entities, entityContext());
    }

    @Override
    public UpdateSetStep<T> update() {
        return updateExecutor.update(entityContext());
    }

    @Override
    public DeleteWhereStep<T> delete() {
        return updateExecutor.delete(entityContext());
    }

    @Override
    public void doInTransaction(Runnable command) {
        updateExecutor.doInTransaction(command);
    }

    @Override
    public <X> X doInTransaction(Supplier<X> command) {
        return updateExecutor.doInTransaction(command);
    }

    /// 获取实体上下文。
    ///
    /// 从 QueryContext（继承自 EntityContext）获取。
    ///
    /// @return 实体上下文实例
    private EntityContext<T> entityContext() {
        return context;
    }
}