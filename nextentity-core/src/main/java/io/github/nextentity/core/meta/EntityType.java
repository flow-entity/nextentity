package io.github.nextentity.core.meta;

/**
 * Entity type interface extending {@link EntitySchema} with projection support.
 * <p>
 * This interface provides entity metadata along with the ability to retrieve
 * projection type metadata for DTO/projection classes associated with this entity.
 * <p>
 * EntityType instances are obtained from the {@link Metamodel} and contain
 * all metadata needed for query building and entity persistence.
 *
 * @author HuangChengwei
 * @since 1.0.0
 */
public interface EntityType extends EntitySchema {

    /**
     * Gets the projection type metadata for the specified projection class.
     * <p>
     * Projection types define how query results are mapped to DTOs or
     * other non-entity result types.
     *
     * @param type the projection class to retrieve metadata for
     * @return the projection type metadata
     * @throws IllegalArgumentException if no projection metadata exists for the given type
     */
    ProjectionType getProjection(Class<?> type);

}
