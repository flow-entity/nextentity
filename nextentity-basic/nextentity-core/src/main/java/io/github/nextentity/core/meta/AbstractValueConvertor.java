package io.github.nextentity.core.meta;


public abstract class AbstractValueConvertor implements ValueConvertor {

    private Class<?> propertyType;
    private Class<?> databaseType;

    @Override
    public void init(Class<?> propertyType, Class<?> databaseType) {
        this.propertyType = propertyType;
        this.databaseType = databaseType;
    }

    @Override
    public Class<?> getAttributeType() {
        return propertyType;
    }

    @Override
    public Class<?> getDatabaseType() {
        return databaseType;
    }
}
