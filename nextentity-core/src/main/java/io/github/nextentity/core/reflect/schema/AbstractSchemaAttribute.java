package io.github.nextentity.core.reflect.schema;

import io.github.nextentity.core.util.Lazy;

///
/// Abstract base class for schema attributes with lazy attribute building.
/// <p>
/// This class extends {@link SimpleAttribute} and implements {@link SchemaAttribute}
/// to provide lazy initialization of nested attributes via the template method
/// {@link #buildAttributes()}.
/// <p>
/// Subclasses implement {@link #buildAttributes()} to define how nested
/// attributes are constructed.
///
/// @author HuangChengwei
/// @since 1.0.0
///
public abstract class AbstractSchemaAttribute extends SimpleAttribute implements SchemaAttribute {

    private final Lazy<Attributes> attributes = new Lazy<>(this::buildAttributes);

    ///
    /// Builds the nested attributes for this schema attribute.
    /// <p>
    /// Called lazily when attributes are first accessed.
    ///
    /// @return the built attributes
    ///
    protected abstract Attributes buildAttributes();

    ///
    /// Gets the nested attributes, building them lazily if needed.
    ///
    /// @return the nested attributes
    ///
    public Attributes attributes() {
        return attributes.get();
    }

}
