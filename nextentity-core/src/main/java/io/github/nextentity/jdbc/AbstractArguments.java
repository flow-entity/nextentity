package io.github.nextentity.jdbc;

import io.github.nextentity.core.meta.ValueConverter;

///
/// 抽象参数类
///
/// 该类为参数处理提供了一个基础实现，维护了一个索引计数器，
/// 并实现了获取下一个参数的逻辑。
///
/// @author HuangChengwei
/// @since 1.0.0
///
public abstract class AbstractArguments implements Arguments {

    private int index;

    /// 获取下一个参数值
    ///
    /// @param convertor 值转换器
    /// @return 参数值
    @Override
    public Object next(ValueConverter<?, ?> convertor) {
        return get(index++, convertor);
    }

}