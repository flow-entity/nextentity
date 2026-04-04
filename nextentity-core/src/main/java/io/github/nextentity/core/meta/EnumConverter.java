package io.github.nextentity.core.meta;

import io.github.nextentity.core.exception.ReflectiveException;
import io.github.nextentity.core.reflect.schema.Attribute;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/// 将枚举值转换为其序号位置的枚举转换器。
///
/// @param <E> 枚举类型
/// @author HuangChengwei
/// @since 1.0.0
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
