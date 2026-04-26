package io.github.nextentity.core;

import io.github.nextentity.core.event.EntityEventListener;
import io.github.nextentity.core.event.EntityEventType;
import io.github.nextentity.core.interceptor.ConstructInterceptor;
import io.github.nextentity.core.interceptor.InterceptorSelector;
import io.github.nextentity.core.meta.Metamodel;
import org.jspecify.annotations.NonNull;

import java.util.List;
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

    private static class EntityEventListenerSet implements EntityEventListener {
        private final CopyOnWriteArrayList<EntityEventListener> listeners = new CopyOnWriteArrayList<>();

        @Override
        public <T> void on(Class<T> entityType, EntityEventType eventType, List<T> entities) {
            for (EntityEventListener listener : listeners) {
                listener.on(entityType, eventType, entities);
            }
        }

        public void add(EntityEventListener listener) {
            listeners.add(listener);
        }
    }
}

