package io.github.nextentity.core.converter;

import io.github.nextentity.core.util.ImmutableList;

import java.util.List;

/// 类型转换器接口，用于将一个类型的值转换为另一个类型的值。
///
/// @author HuangChengwei
/// @since 1.0.0
public interface TypeConverter {

    /// 将指定值转换为目标类型。
    ///
    /// @param value      要转换的值
    /// @param targetType 目标类型
    /// @return 转换后的值
    Object convert(Object value, Class<?> targetType);

    /// 创建默认类型转换器，包含数字、枚举和本地日期时间转换器。
    ///
    /// @return 默认类型转换器实例
    static TypeConverter ofDefault() {
        return of(NumberConverter.of(), EnumConverter.of(), LocalDateTimeConverter.of());
    }

    /// 创建包含指定转换器的类型转换器。
    ///
    /// @param converters 类型转换器数组
    /// @return 组合类型转换器实例
    static TypeConverter of(TypeConverter... converters) {
        return new TypeConverters(ImmutableList.of(converters));
    }

    /// 创建包含指定转换器列表的类型转换器。
    ///
    /// @param converters 类型转换器列表
    /// @return 组合类型转换器实例
    static TypeConverter of(List<TypeConverter> converters) {
        return new TypeConverters(converters);
    }

}
