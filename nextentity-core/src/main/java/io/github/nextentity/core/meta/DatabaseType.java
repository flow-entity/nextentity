package io.github.nextentity.core.meta;

/**
 * @author HuangChengwei
 * @since 1.0.0
 */
public interface DatabaseType {

    Class<?> databaseType();

    Object toDatabaseType(Object value);

    Object toAttributeType(Object value);

}
