package io.github.nextentity.core.meta;

import io.github.nextentity.core.reflect.schema.Attribute;
import io.github.nextentity.core.reflect.schema.Attributes;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

/**
 * Simple implementation of {@link EntityType}.
 * <p>
 * This class provides a concrete implementation for entity type metadata
 * with support for lazy projection type generation and caching.
 * <p>
 * Entity attributes including ID and version are set after construction
 * via {@link #setAttributes(Attributes)}.
 *
 * @author HuangChengwei
 * @since 1.0.0
 */
public class SimpleEntity implements EntityType {

    private final Map<Class<?>, ProjectionType> projections = new ConcurrentHashMap<>();
    private final Class<?> type;
    private final String tableName;
    private final BiFunction<EntityType, Class<?>, ProjectionType> projectionTypeGenerator;
    private Attributes attributes;
    private EntityAttribute id;
    private EntityAttribute version;

    /**
     * Creates a new SimpleEntity instance.
     *
     * @param type the entity class
     * @param tableName the database table name
     * @param projectionTypeGenerator function to generate projection types
     */
    public SimpleEntity(Class<?> type,
                        String tableName,
                        BiFunction<EntityType, Class<?>, ProjectionType> projectionTypeGenerator) {
        this.type = type;
        this.tableName = tableName;
        this.projectionTypeGenerator = projectionTypeGenerator;
    }

    /**
     * Sets the entity attributes and extracts ID and version attributes.
     *
     * @param attributes the entity attributes
     */
    public void setAttributes(Attributes attributes) {
        this.attributes = attributes;
        EntityAttribute version = null;
        EntityAttribute id = null;
        for (Attribute attribute : attributes) {
            if (attribute instanceof EntityAttribute column) {
                if (column.isId()) {
                    id = column;
                } else if (column.isVersion()) {
                    version = column;
                }
            }
        }
        this.id = id;
        this.version = version;
    }

    /**
     * Gets the projection type for the specified class, caching the result.
     *
     * @param type the projection class
     * @return the cached or newly generated projection type
     */
    @Override
    public ProjectionType getProjection(Class<?> type) {
        return projections.computeIfAbsent(type, this::generateProjectionType);
    }

    private ProjectionType generateProjectionType(Class<?> type) {
        return projectionTypeGenerator.apply(this, type);
    }

    @Override
    public EntityAttribute id() {
        return id;
    }

    @Override
    public String tableName() {
        return tableName;
    }

    @Override
    public EntityAttribute version() {
        return version;
    }

    @Override
    public Attributes attributes() {
        return attributes;
    }

    @Override
    public Class<?> type() {
        return type;
    }
}
