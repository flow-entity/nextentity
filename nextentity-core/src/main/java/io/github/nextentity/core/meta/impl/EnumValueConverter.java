package io.github.nextentity.core.meta.impl;

import io.github.nextentity.core.TypeCastUtil;
import io.github.nextentity.core.meta.ValueConverter;
import io.github.nextentity.core.reflect.ReflectUtil;

/// 枚举值转换器，使用 ordinal 进行实体属性与数据库列之间的转换。
///
/// @param <E> 枚举类型
/// @author HuangChengwei
/// @since 2.0.0
public class EnumValueConverter<E extends Enum<E>> implements ValueConverter<E, Integer> {
    private final Class<E> enumType;

    public EnumValueConverter(Class<E> enumType) {
        this.enumType = enumType;
    }

    public static EnumValueConverter<? extends Enum<?>> of(Class<?> enumType) {
        return newEnumEnumValueConverter(enumType);
    }

    private static <E extends Enum<E>> EnumValueConverter<E> newEnumEnumValueConverter(Class<?> type) {
        Class<E> enumType = TypeCastUtil.cast(type);
        return new EnumValueConverter<>(enumType);
    }

    @Override
    public Integer convertToDatabaseColumn(E attribute) {
        return attribute != null ? attribute.ordinal() : null;
    }

    @Override
    public E convertToEntityAttribute(Integer dbData) {
        if (dbData == null) {
            return null;
        }
        //noinspection unchecked
        return (E) ReflectUtil.getEnum(enumType, dbData);
    }

    @Override
    public Class<Integer> getDatabaseColumnType() {
        return Integer.class;
    }
}
