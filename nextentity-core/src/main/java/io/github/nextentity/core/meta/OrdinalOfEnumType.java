package io.github.nextentity.core.meta;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Database type converter for enum types using ordinal values.
 * <p>
 * This class converts enum values to their ordinal positions (0, 1, 2, ...)
 * for database storage, and converts ordinal integers back to enum values.
 * <p>
 * Use this when enums are stored as integers in the database.
 *
 * @author HuangChengwei
 * @since 1.0.0
 */
public class OrdinalOfEnumType implements DatabaseType {

    private final Class<?> databaseType;
    private final Object[] values;

    /**
     * Creates a new OrdinalOfEnumType instance for the given enum type.
     *
     * @param attributeType the enum class
     * @throws RuntimeException if the enum values cannot be retrieved
     */
    public OrdinalOfEnumType(Class<?> attributeType) {
        this.databaseType = Integer.class;
        try {
            Method method = attributeType.getDeclaredMethod("values");
            this.values = (Object[]) method.invoke(null);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns Integer as the database type.
     *
     * @return Integer.class
     */
    @Override
    public Class<?> databaseType() {
        return databaseType;
    }

    /**
     * Converts an enum value to its ordinal position.
     *
     * @param value the enum value
     * @return the ordinal position, or null if value is null
     */
    @Override
    public Object toDatabaseType(Object value) {
        return value == null ? null : ((Enum<?>) value).ordinal();
    }

    /**
     * Converts an ordinal position to the corresponding enum value.
     *
     * @param value the ordinal position
     * @return the enum value, or the original value if not an Integer
     */
    @Override
    public Object toAttributeType(Object value) {
        return value instanceof Integer index ? values[index] : value;
    }
}
