package io.github.nextentity.core.meta;

import io.github.nextentity.core.reflect.schema.Attribute;

///
/// Interface representing an attribute that maps to a database column.
///
/// Extends {@link Attribute} with database-specific metadata such as
/// value converters and update behavior.
///
public interface DatabaseColumnAttribute extends Attribute {

    ///
    /// Gets the value converter for this attribute.
    ///
    /// The converter is responsible for translating between the entity attribute
    /// type and the database column type.
    ///
    /// @return the value converter
    ///
    ValueConverter<?, ?> valueConvertor();

    ///
    /// Checks if this attribute is updatable.
    ///
    /// @return {@code true} if the column can be updated, {@code false} otherwise
    ///
    boolean isUpdatable();

    ///
    /// Gets the database column type.
    ///
    /// If the value converter specifies a database type, that type is returned.
    /// Otherwise, the entity attribute type is returned.
    ///
    /// @return the database column type
    ///
    default Class<?> getDatabaseColumnType() {
        Class<?> databaseType = valueConvertor().getDatabaseColumnType();
        if (databaseType == null) {
            return type();
        }
        return databaseType;
    }

}
