package io.github.nextentity.core.meta;

import io.github.nextentity.core.reflect.schema.SimpleAttribute;

///
/// Simple implementation of {@link ProjectionAttribute}.
///
/// This class provides a concrete implementation for projection attributes
/// that delegate to a source entity attribute for value conversion and
/// database mapping.
///
/// @author HuangChengwei
/// @since 1.0.0
///
public class SimpleProjectionAttribute extends SimpleAttribute implements ProjectionAttribute {

    private final EntityAttribute sourceAttribute;

    ///
    /// Creates a new SimpleProjectionAttribute instance.
    ///
    /// @param sourceAttribute the source entity attribute to delegate to
    ///
    public SimpleProjectionAttribute(EntityAttribute sourceAttribute) {
        this.sourceAttribute = sourceAttribute;
    }

    ///
    /// Gets the source entity attribute.
    ///
    /// @return the source entity attribute
    ///
    @Override
    public EntityAttribute source() {
        return sourceAttribute;
    }

    ///
    /// Inherits updatable status from the source entity attribute.
    ///
    /// @return the updatable status of the source attribute
    ///
    @Override
    public boolean isUpdatable() {
        return sourceAttribute.isUpdatable();
    }
}
