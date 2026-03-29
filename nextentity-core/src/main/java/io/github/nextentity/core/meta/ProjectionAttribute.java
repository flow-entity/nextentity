package io.github.nextentity.core.meta;

///
/// Projection attribute interface for mapping projection fields to entity attributes.
///
/// This interface extends {@link DatabaseColumnAttribute} and provides a link
/// between projection fields and their corresponding entity attributes.
///
/// Projection attributes inherit value conversion from their source entity attributes.
///
/// @author HuangChengwei
/// @since 1.0.0
///
public interface ProjectionAttribute extends DatabaseColumnAttribute {

    ///
    /// Gets the source entity attribute that this projection attribute maps from.
    ///
    /// @return the source entity attribute
    ///
    EntityAttribute source();

    ///
    /// Gets the value converter for this attribute.
    ///
    /// Delegates to the source entity attribute's value converter.
    ///
    /// @return the value converter from the source entity attribute
    ///
    default ValueConverter<?, ?> valueConvertor() {
        return source().valueConvertor();
    }
}
