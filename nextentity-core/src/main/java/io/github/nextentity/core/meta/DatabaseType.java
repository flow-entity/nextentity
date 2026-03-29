package io.github.nextentity.core.meta;

/**
 * Database type interface for type conversion between entity and database types.
 * <p>
 * This interface provides methods to convert values between the Java entity
 * attribute type and the corresponding database column type.
 * <p>
 * Implementations handle type-specific conversions such as enum to string/integer,
 * timestamp conversions, and custom type mappings.
 *
 * @author HuangChengwei
 * @since 1.0.0
 */
public interface DatabaseType {

    /**
     * Gets the database column type for this attribute.
     *
     * @return the database type class
     */
    Class<?> databaseType();

    /**
     * Converts a value from entity attribute type to database column type.
     *
     * @param value the entity attribute value
     * @return the converted database value
     */
    Object toDatabaseType(Object value);

    /**
     * Converts a value from database column type to entity attribute type.
     *
     * @param value the database column value
     * @return the converted entity attribute value
     */
    Object toAttributeType(Object value);

}
