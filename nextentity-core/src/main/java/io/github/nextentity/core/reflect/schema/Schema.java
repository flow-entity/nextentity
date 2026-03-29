package io.github.nextentity.core.reflect.schema;

import io.github.nextentity.core.util.ImmutableArray;

///
/// Schema interface representing a structured type with attributes.
/// <p>
/// This interface extends {@link ReflectType} to provide access to
/// attributes (fields/properties) of a class, supporting nested path
/// traversal and attribute discovery.
/// <p>
/// Schema is the base interface for both entity types and projection types.
///
/// @author HuangChengwei
/// @since 1.0.0
///
public non-sealed interface Schema extends ReflectType {

    ///
    /// Gets all attributes of this schema.
    ///
    /// @return the attributes collection
    ///
    Attributes attributes();

    ///
    /// Gets the primitive (non-association) attributes of this schema.
    /// <p>
    /// Primitive attributes are simple fields that map directly to
    /// database columns, not associations to other entities.
    ///
    /// @return an immutable array of primitive attributes
    ///
    default ImmutableArray<? extends Attribute> getPrimitives() {
        return attributes().getPrimitives();
    }

    ///
    /// Gets an attribute by name.
    ///
    /// @param name the attribute name
    /// @return the attribute
    /// @throws IllegalArgumentException if no attribute with the given name exists
    ///
    default Attribute getAttribute(String name) {
        return attributes().get(name);
    }

    ///
    /// Gets an attribute by a nested path of field names.
    /// <p>
    /// Traverses nested schemas to find the final attribute.
    ///
    /// @param fieldNames the path of field names
    /// @return the attribute at the end of the path
    /// @throws IllegalArgumentException if the path is invalid
    ///
    default Attribute getAttribute(Iterable<String> fieldNames) {
        ReflectType schema = this;
        for (String fieldName : fieldNames) {
            schema = ((Schema) schema).getAttribute(fieldName);
        }
        return (Attribute) schema;
    }

    ///
    /// Indicates this is an object type (not primitive).
    ///
    /// @return always true for schema types
    ///
    default boolean isObject() {
        return true;
    }

}
