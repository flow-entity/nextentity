package io.github.nextentity.core.meta;

import io.github.nextentity.core.SelectItem;
import io.github.nextentity.core.TypeCastUtil;

/**
 * Interface representing an entity attribute that can be persisted to the database.
 * <p>
 * Extends {@link DatabaseColumnAttribute} with entity-specific operations such as
 * column name, version support, and identity support.
 */
public non-sealed interface EntityAttribute extends DatabaseColumnAttribute, SelectItem {

    /**
     * Gets the database column name for this attribute.
     *
     * @return the column name
     */
    String columnName();

    /**
     * Checks if this attribute is a version field for optimistic locking.
     *
     * @return {@code true} if this is a version field, {@code false} otherwise
     */
    boolean isVersion();

    /**
     * Checks if this attribute is an identity (primary key) field.
     *
     * @return {@code true} if this is an identity field, {@code false} otherwise
     */
    boolean isId();

    /**
     * Gets the database value from the entity using the value converter.
     *
     * @param entity the entity instance
     * @return the database value
     */
    default Object getDatabaseValue(Object entity) {
        Object o = get(entity);
        return valueConvertor().convertToDatabaseColumn(TypeCastUtil.unsafeCast(o));
    }

    /**
     * Sets the entity attribute value from a database value using the value converter.
     *
     * @param entity the entity instance
     * @param value the database value
     */
    default void setByDatabaseValue(Object entity, Object value) {
        value = valueConvertor().convertToEntityAttribute(TypeCastUtil.unsafeCast(value));
        set(entity, value);
    }

}
