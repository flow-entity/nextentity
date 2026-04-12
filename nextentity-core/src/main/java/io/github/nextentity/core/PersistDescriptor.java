package io.github.nextentity.core;

import io.github.nextentity.api.EntityDescriptor;

public interface PersistDescriptor<T> extends EntityDescriptor<T> {

    PersistExecutor persistExecutor();

}
