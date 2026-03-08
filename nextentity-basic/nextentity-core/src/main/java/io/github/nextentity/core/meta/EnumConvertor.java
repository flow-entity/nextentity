package io.github.nextentity.core.meta;

import io.github.nextentity.core.reflect.schema.Attribute;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class EnumConvertor implements ValueConvertor<Object, Integer> {

    private final Object[] values;

    @Override
    public Integer convertToDatabaseColumn(Object attributeValue) {
        return attributeValue == null ? null : ((Enum<?>) attributeValue).ordinal();
    }

    @Override
    public Object convertToEntityAttribute(Integer databaseValue) {
        return databaseValue == null ? null : values[databaseValue];
    }

    public EnumConvertor(Attribute attribute) {
        try {
            Class<?> attributeType = attribute.type();
            Method method = attributeType.getDeclaredMethod("values");
            this.values = (Object[]) method.invoke(null);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Class<Integer> getDatabaseColumnType() {
        return Integer.class;
    }
}
