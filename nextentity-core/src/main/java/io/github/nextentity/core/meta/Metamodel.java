package io.github.nextentity.core.meta;

/**
 * Metamodel interface for managing entity type metadata.
 * <p>
 * This interface provides access to {@link EntityType} instances that contain
 * metadata about entity classes, including table names, attributes, and
 * relationship information.
 * <p>
 * The metamodel is typically configured during application startup and provides
 * the foundation for query building and entity persistence operations.
 *
 * @author HuangChengwei
 * @since 1.0.0
 */
public interface Metamodel {

    /**
     * Gets the entity type metadata for the specified class.
     * <p>
     * The returned {@link EntityType} contains information about the entity's
     * table structure, attributes, and relationships.
     *
     * @param type the entity class to retrieve metadata for
     * @return the entity type metadata
     * @throws IllegalArgumentException if no metadata exists for the given type
     */
    EntityType getEntity(Class<?> type);
}
