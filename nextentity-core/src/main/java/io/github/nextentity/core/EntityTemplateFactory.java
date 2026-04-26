package io.github.nextentity.core;

import io.github.nextentity.core.event.EntityEventListener;
import io.github.nextentity.core.event.EntityEventType;
import io.github.nextentity.core.interceptor.ConstructInterceptor;
import io.github.nextentity.core.interceptor.InterceptorSelector;
import io.github.nextentity.core.meta.Metamodel;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public final class EntityTemplateFactory implements EntityOperationsFactory, QueryConfig, PersistConfig {

    private final Metamodel metamodel;
    private final PersistExecutor persistExecutor;
    private final QueryExecutor queryExecutor;
    private final InterceptorSelector<ConstructInterceptor> constructors;
    private final QueryProperties properties;
    private final EntityEventListenerSet eventListener = new EntityEventListenerSet();

    public EntityTemplateFactory(@NonNull EntityTemplateFactoryConfig config) {
        this.metamodel = config.metamodel();
        this.persistExecutor = config.persistExecutor();
        this.queryExecutor = config.queryExecutor();
        this.constructors = config.constructors();
        this.properties = config.properties();
    }

    public <T> EntityTemplate<T> template(Class<T> entityType) {
        EntityTemplateDescriptor<?, T> descriptor = new EntityTemplateDescriptor<>(this, entityType);
        return new EntityTemplate<>(descriptor);
    }

    @Override
    public <T> EntityTemplate<T> operations(Class<T> entityType) {
        return template(entityType);
    }

    public void registerEventListener(EntityEventListener listener) {
        Objects.requireNonNull(listener, "listener must not be null");
        this.eventListener.add(listener);
    }

    @Override
    public Metamodel metamodel() {
        return metamodel;
    }

    @Override
    public PersistExecutor persistExecutor() {
        return persistExecutor;
    }

    @Override
    public QueryExecutor queryExecutor() {
        return queryExecutor;
    }

    @Override
    public InterceptorSelector<ConstructInterceptor> constructors() {
        return constructors;
    }

    @Override
    public QueryProperties properties() {
        return properties;
    }

    @Override
    public EntityEventListener eventListener() {
        return eventListener;
    }

    /// 复合监听器集合，按注册顺序依次调用所有监听器。
    ///
    /// **异常行为：**若某个监听器抛出异常，后续监听器将不会被调用，
    /// 异常将向上传播。如需异常隔离（一个监听器的失败不影响其他监听器），
    /// 请在监听器实现内部自行 try-catch。
    private static class EntityEventListenerSet implements EntityEventListener {
        private final CopyOnWriteArrayList<EntityEventListener> listeners = new CopyOnWriteArrayList<>();

        @Override
        public <T> void on(Class<T> entityType, EntityEventType eventType, List<T> entities, int affectedRows) {
            for (EntityEventListener listener : listeners) {
                listener.on(entityType, eventType, entities, affectedRows);
            }
        }

        public void add(EntityEventListener listener) {
            listeners.add(listener);
        }
    }
}

