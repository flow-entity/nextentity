package io.github.nextentity.core.meta;

import io.github.nextentity.core.reflect.schema.AbstractSchemaAttribute;
import io.github.nextentity.core.reflect.schema.Attributes;

import java.util.function.Function;

///
/// Simple implementation of {@link ProjectionJoinAttribute}.
///
/// This class provides a concrete implementation for projection join attributes
/// that represent nested projection mappings for entity associations.
///
/// Attributes are built lazily via a supplied function to support recursive
/// projection structures.
///
/// @author HuangChengwei
/// @since 1.0.0
///
public class SimpleProjectionJoinAttribute extends AbstractSchemaAttribute implements ProjectionJoinAttribute {

    private final JoinAttribute source;
    private final Function<? super SimpleProjectionJoinAttribute, Attributes> attributeBuilder;

    ///
    /// Creates a new SimpleProjectionJoinAttribute instance.
    ///
    /// @param source the source join attribute
    /// @param attributeBuilder function to build nested projection attributes
    ///
    public SimpleProjectionJoinAttribute(JoinAttribute source, Function<? super SimpleProjectionJoinAttribute, Attributes> attributeBuilder) {
        this.source = source;
        this.attributeBuilder = attributeBuilder;
    }

    ///
    /// Gets the source join attribute.
    ///
    /// @return the source join attribute
    ///
    @Override
    public JoinAttribute source() {
        return source;
    }

    ///
    /// Builds the nested projection attributes lazily.
    ///
    /// @return the nested projection attributes
    ///
    @Override
    protected Attributes buildAttributes() {
        return attributeBuilder.apply(this);
    }


}
