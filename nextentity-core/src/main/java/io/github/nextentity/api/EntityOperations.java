package io.github.nextentity.api;

public interface EntityOperations<T> extends EntityPersistor<T> {

    EntityQuery<T> query();

}