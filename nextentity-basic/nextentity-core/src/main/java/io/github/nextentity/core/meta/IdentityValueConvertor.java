package io.github.nextentity.core.meta;

public class IdentityValueConvertor extends AbstractValueConvertor {

    @Override
    public Object toDatabaseValue(Object attributeValue) {
        return attributeValue;
    }

    @Override
    public Object toAttributeValue(Object databaseValue) {
        return databaseValue;
    }
}
