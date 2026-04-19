package io.github.nextentity.core.configuration;

import io.github.nextentity.core.PersistExecutor;

import java.util.List;

public interface PersistConfiguration {

    List<PostProcessor<PersistExecutor>> getPostProcessors();

}
