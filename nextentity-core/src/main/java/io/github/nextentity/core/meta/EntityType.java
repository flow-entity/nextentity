package io.github.nextentity.core.meta;

/**
 * @author HuangChengwei
 * @since 1.0.0
 */
public interface EntityType extends EntitySchema {

    ProjectionType getProjection(Class<?> type);

}
