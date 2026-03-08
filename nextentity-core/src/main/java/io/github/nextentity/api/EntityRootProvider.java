package io.github.nextentity.api;

import io.github.nextentity.api.model.EntityRoot;

/**
 * Entity root provider interface, used to get the root path of the entity.
 *
 * @param <T> Entity type
 * @author HuangChengwei
 * @since 1.0.0
 */
public interface EntityRootProvider<T> {

    /**
     * Gets the root path of the entity.
     *
     * @return Entity root path
     */
    EntityRoot<T> root();
}
