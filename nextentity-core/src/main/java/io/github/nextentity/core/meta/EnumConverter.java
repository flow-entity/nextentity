package io.github.nextentity.core.meta;

import io.github.nextentity.core.exception.ReflectiveException;
import io.github.nextentity.core.reflect.schema.Attribute;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

///
/// Enum converter that converts enum values to their ordinal positions.
///
/// @param <E> the enum type
///
public class EnumConverter<E extends Enum<E>> implements ValueConverter<E, Integer> {

    private final E[] values;

    @Override
    public Integer convertToDatabaseColumn(E attributeValue) {
        return attributeValue == null ? null : attributeValue.ordinal();
    }

    @Override
    public E convertToEntityAttribute(Integer databaseValue) {
        return databaseValue == null ? null : values[databaseValue];
    }

    @SuppressWarnings("unchecked")
    public EnumConverter(Attribute attribute) {
        try {
            Class<E> attributeType = (Class<E>) attribute.type();
            Method method = attributeType.getDeclaredMethod("values");
            this.values = (E[]) method.invoke(null);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new ReflectiveException(e);
        }
    }

    @Override
    public Class<Integer> getDatabaseColumnType() {
        return Integer.class;
    }
}
