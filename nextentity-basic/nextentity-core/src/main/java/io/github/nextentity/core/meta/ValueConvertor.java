package io.github.nextentity.core.meta;

public interface ValueConvertor {

    Object toDatabaseValue(Object attributeValue);

    Object toAttributeValue(Object databaseValue);

    void init(Class<?> propertyType, Class<?> databaseType);

    Class<?> getAttributeType();

    Class<?> getDatabaseType();

}
