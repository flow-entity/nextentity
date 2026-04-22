package io.github.nextentity.core.constructor;

import io.github.nextentity.core.reflect.schema.Attribute;

import java.util.stream.Stream;

/// 属性绑定
///
/// 将值构造器与属性元数据关联，职责是"绑定属性"而非"构造值"。
/// 与 ValueConstructor 形成组合关系：ObjectConstructor 持有 PropertyBinding[]，
/// PropertyBinding 持有 ValueConstructor。
///
/// @param attribute        属性元数据（使用现有 Attribute 接口）
/// @param valueConstructor 值构造器（负责构造值）
/// @author HuangChengwei
/// @since 2.2.2
public record PropertyBinding(Attribute attribute, ValueConstructor valueConstructor) {

    /// 获取 Attribute（包含 setter、getter、type 等元数据）
    @Override
    public Attribute attribute() {
        return attribute;
    }

    /// 获取值构造器
    @Override
    public ValueConstructor valueConstructor() {
        return valueConstructor;
    }

    /// 获取所有列（委托给值构造器）
    public Stream<Column> getColumns() {
        return valueConstructor.columns().stream();
    }

    /// 判断是否嵌套属性（值构造器是否为 ObjectConstructor）
    public boolean isNested() {
        return valueConstructor instanceof ObjectConstructor;
    }
}