package io.github.nextentity.core.meta;

/**
 * Identity value converter that returns values unchanged.
 * Used for types that don't require conversion between entity and database representations.
 */
public class IdentityValueConverter implements ValueConverter<Object, Object> {
    public static final IdentityValueConverter INSTANCE = new IdentityValueConverter(Object.class);

    private final Class<?> type;

    public static IdentityValueConverter of() {
        return INSTANCE;
    }

    public IdentityValueConverter(Class<?> type) {
        this.type = type;
    }

    @Override
    public Object convertToDatabaseColumn(Object attributeValue) {
        return attributeValue;
    }

    @Override
    public Object convertToEntityAttribute(Object databaseValue) {
        return databaseValue;
    }

    @Override
    public Class<?> getDatabaseColumnType() {
        return type;
    }
}
