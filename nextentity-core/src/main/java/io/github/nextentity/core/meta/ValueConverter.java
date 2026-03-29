package io.github.nextentity.core.meta;

///
/// Converter interface for converting between entity attribute types and database column types.
///
/// @param <X> the entity attribute type
/// @param <Y> the database column type
///
public interface ValueConverter<X, Y> {

    ///
    /// Converts the entity attribute value to a database column value.
    ///
    /// @param attributeValue the entity attribute value
    /// @return the database column value
    ///
    Y convertToDatabaseColumn(X attributeValue);

    ///
    /// Converts the database column value to an entity attribute value.
    ///
    /// @param databaseValue the database column value
    /// @return the entity attribute value
    ///
    X convertToEntityAttribute(Y databaseValue);

    ///
    /// Gets the database column type.
    ///
    /// @return the database column type, or null if not specified
    ///
    default Class<? extends Y> getDatabaseColumnType() {
        return null;
    }

}
