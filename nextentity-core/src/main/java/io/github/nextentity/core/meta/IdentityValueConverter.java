package io.github.nextentity.core.meta;

///
/// Identity value converter that returns values unchanged.
///
/// Used for types that don't require conversion between entity and database
/// representations, such as String, Integer, and other basic types.
///
/// This converter is a singleton with a shared instance available via {@link #of()}.
///
/// @author HuangChengwei
/// @since 1.0.0
///
public class IdentityValueConverter implements ValueConverter<Object, Object> {

    ///
    /// Shared singleton instance for generic Object type.
    ///
    public static final IdentityValueConverter INSTANCE = new IdentityValueConverter(Object.class);

    private final Class<?> type;

    ///
    /// Returns the singleton instance.
    ///
    /// @return the shared identity converter instance
    ///
    public static IdentityValueConverter of() {
        return INSTANCE;
    }

    ///
    /// Creates a new identity converter for a specific type.
    ///
    /// @param type the type this converter handles
    ///
    public IdentityValueConverter(Class<?> type) {
        this.type = type;
    }

    ///
    /// Returns the value unchanged.
    ///
    /// @param attributeValue the entity attribute value
    /// @return the same value
    ///
    @Override
    public Object convertToDatabaseColumn(Object attributeValue) {
        return attributeValue;
    }

    ///
    /// Returns the value unchanged.
    ///
    /// @param databaseValue the database column value
    /// @return the same value
    ///
    @Override
    public Object convertToEntityAttribute(Object databaseValue) {
        return databaseValue;
    }

    ///
    /// Returns the type this converter handles.
    ///
    /// @return the type
    ///
    @Override
    public Class<?> getDatabaseColumnType() {
        return type;
    }
}
