package io.github.nextentity.core.meta;

import io.github.nextentity.core.reflect.schema.Attributes;

///
/// Simple implementation of {@link ProjectionType}.
///
/// This class provides a concrete implementation for projection type metadata
/// that maps entity attributes to DTO/projection class fields.
///
/// @author HuangChengwei
/// @since 1.0.0
///
public class SimpleProjection implements ProjectionType {

    private final Class<?> type;
    private final EntitySchema entityType;
    private Attributes attributes;

    ///
    /// Creates a new SimpleProjection instance.
    ///
    /// @param type the projection class
    /// @param entityType the source entity schema
    ///
    public SimpleProjection(Class<?> type, EntitySchema entityType) {
        this.type = type;
        this.entityType = entityType;
    }

    ///
    /// Gets the source entity schema.
    ///
    /// @return the entity schema this projection maps from
    ///
    @Override
    public EntitySchema source() {
        return entityType;
    }

    @Override
    public Attributes attributes() {
        return attributes;
    }

    @Override
    public Class<?> type() {
        return type;
    }

    ///
    /// Sets the projection attributes.
    ///
    /// @param attributes the projection attributes
    ///
    public void setAttributes(Attributes attributes) {
        this.attributes = attributes;
    }


}
