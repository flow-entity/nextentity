package io.github.nextentity.core;

import io.github.nextentity.core.event.EntityEventListener;
import io.github.nextentity.core.event.EntityEventType;
import io.github.nextentity.core.expression.ExpressionNode;
import io.github.nextentity.core.expression.UpdateStructure;
import io.github.nextentity.core.util.ImmutableList;
import org.jspecify.annotations.NonNull;

import java.util.List;

///
/// 持久化执行器的抽象基类，负责在 CRUD 操作前后发布事件。
///
/// 子类只需实现 doInsertAll、doUpdateAll、doDeleteAll、doUpdate、doDelete 方法，
/// 事件发布逻辑由本类统一处理。
///
/// @author HuangChengwei
/// @since 2.0.0
public abstract class AbstractPersistExecutor implements PersistExecutor {

    @Override
    public <T> void insertAll(@NonNull Iterable<T> entities, @NonNull PersistDescriptor<T> descriptor) {
        List<T> list = ImmutableList.ofIterable(entities);
        fireEvent(descriptor, EntityEventType.BEFORE_INSERT, list);
        doInsertAll(list, descriptor);
        fireEvent(descriptor, EntityEventType.AFTER_INSERT, list);
    }

    @Override
    public <T> void updateAll(@NonNull Iterable<T> entities, @NonNull PersistDescriptor<T> descriptor) {
        List<T> list = ImmutableList.ofIterable(entities);
        fireEvent(descriptor, EntityEventType.BEFORE_UPDATED, list);
        doUpdateAll(list, descriptor);
        fireEvent(descriptor, EntityEventType.AFTER_UPDATED, list);
    }

    @Override
    public <T> void deleteAll(@NonNull Iterable<T> entities, @NonNull PersistDescriptor<T> descriptor) {
        List<T> list = ImmutableList.ofIterable(entities);
        fireEvent(descriptor, EntityEventType.BEFORE_DELETED, list);
        doDeleteAll(list, descriptor);
        fireEvent(descriptor, EntityEventType.AFTER_DELETED, list);
    }

    @Override
    public <T> int update(@NonNull UpdateStructure structure, @NonNull PersistDescriptor<T> descriptor) {
        return doUpdate(structure, descriptor);
    }

    @Override
    public <T> int delete(@NonNull ExpressionNode predicate, @NonNull PersistDescriptor<T> descriptor) {
        return doDelete(predicate, descriptor);
    }

    protected abstract <T> void doInsertAll(List<T> entities, PersistDescriptor<T> descriptor);

    protected abstract <T> void doUpdateAll(List<T> entities, PersistDescriptor<T> descriptor);

    protected abstract <T> void doDeleteAll(List<T> entities, PersistDescriptor<T> descriptor);

    protected abstract <T> int doUpdate(UpdateStructure structure, PersistDescriptor<T> descriptor);

    protected abstract <T> int doDelete(ExpressionNode predicate, PersistDescriptor<T> descriptor);

    private <T> void fireEvent(PersistDescriptor<T> descriptor, EntityEventType eventType, List<T> entities) {
        EntityEventListener listener = descriptor.persistConfig().eventListener();
        if (listener != null) {
            listener.on(descriptor.entityClass(), eventType, entities);
        }
    }

}
