package io.github.nextentity.core.converter;

import io.github.nextentity.core.reflect.PrimitiveTypes;
import io.github.nextentity.core.util.ImmutableList;

import java.util.List;
import java.util.stream.Stream;

/// 类型转换器的复合实现类，可以管理多个类型转换器。
///
/// @author HuangChengwei
/// @since 1.0.0
public class TypeConverters implements TypeConverter {
    /// 类型转换器列表。
    private final List<TypeConverter> converters;

    /// 构造函数，使用类型转换器列表初始化。
    ///
    /// @param converters 类型转换器列表
    public TypeConverters(List<TypeConverter> converters) {
        this.converters = converters.stream()
                .flatMap(converter -> {
                    if (converter.getClass() == TypeConverters.class) {
                        return ((TypeConverters) converter).converters.stream();
                    } else {
                        return Stream.of(converter);
                    }
                })
                .distinct().collect(ImmutableList.collector(converters.size()));
    }

    /// 将指定值转换为目标类型。
    ///
    /// @param value      要转换的值
    /// @param targetType 目标类型
    /// @return 转换后的值
    @Override
    public Object convert(Object value, Class<?> targetType) {
        if (value == null || targetType.isInstance(value)) {
            return value;
        }
        if (targetType.isPrimitive() && PrimitiveTypes.getWrapper(targetType).isInstance(value)) {
            return value;
        }
        for (TypeConverter converter : converters) {
            value = converter.convert(value, targetType);
            if (targetType.isInstance(value)) {
                return value;
            }
        }
        return value;
    }
}
