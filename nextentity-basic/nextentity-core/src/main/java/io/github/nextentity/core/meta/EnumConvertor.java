package io.github.nextentity.core.meta;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class EnumConvertor extends AbstractValueConvertor {

    private Object[] values;

    @Override
    public Object toDatabaseValue(Object attributeValue) {
        return attributeValue == null ? null : ((Enum<?>) attributeValue).ordinal();
    }

    @Override
    public Object toAttributeValue(Object databaseValue) {
        return databaseValue instanceof Integer index ? values[index] : databaseValue;
    }

    @Override
    public void init(Class<?> propertyType, Class<?> databaseType) {
        try {
            super.init(propertyType, databaseType);
            Method method = propertyType.getDeclaredMethod("values");
            this.values = (Object[]) method.invoke(null);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
