package io.github.nextentity.core;

import io.github.nextentity.core.meta.Metamodel;

public interface PersistConfig {

    Metamodel metamodel();

    PersistExecutor persistExecutor();

}
