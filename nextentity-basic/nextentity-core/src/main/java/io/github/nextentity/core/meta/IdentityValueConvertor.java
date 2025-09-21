package io.github.nextentity.core.meta;

public class IdentityValueConvertor implements ValueConvertor<Object, Object> {
    public static final IdentityValueConvertor INSTANCE = new IdentityValueConvertor(Object.class);

    private final Class<?> type;

    public static IdentityValueConvertor of() {
        return INSTANCE;
    }

    public IdentityValueConvertor(Class<?> type) {
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
