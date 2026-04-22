package io.github.nextentity.jdbc;

import io.github.nextentity.core.meta.ValueConverter;

///
/// 参数接口
///
/// 该接口定义了参数获取的方法，用于从数据源中获取指定索引位置的参数值，
/// 或获取下一个参数值。
///
/// @author HuangChengwei
/// @since 1.0.0
///
public interface Arguments {

    /// 获取指定索引位置的参数值
    ///
    /// @param index     参数索引
    /// @param convertor 值转换器
    /// @return 参数值
    Object get(int index, ValueConverter<?, ?> convertor);

    /// 获取下一个参数值
    ///
    /// @param convertor 值转换器
    /// @return 参数值
    Object next(ValueConverter<?, ?> convertor);

}
