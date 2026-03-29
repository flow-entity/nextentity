package io.github.nextentity.core.reflect.schema;

///
/// Interface for attributes that are also schemas (nested types).
/// <p>
/// This interface combines {@link Schema} and {@link Attribute} to represent
/// attributes that have their own nested attributes, such as association
/// fields (JoinAttribute) that reference another entity with its own schema.
///
/// @author HuangChengwei
/// @since 1.0.0
///
public interface SchemaAttribute extends Schema, Attribute {

}
