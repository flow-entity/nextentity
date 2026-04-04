package io.github.nextentity.core.reflect.schema;

import io.github.nextentity.core.util.Lazy;

/// 具有延迟属性构建的模式属性的抽象基类。
///
/// 该类扩展 {@link SimpleAttribute} 并实现 {@link SchemaAttribute}
/// 以通过模板方法提供嵌套属性的延迟初始化
/// {@link #buildAttributes()}。
///
/// 子类实现 {@link #buildAttributes()} 来定义如何构建
/// 嵌套属性。
///
/// @author HuangChengwei
/// @since 1.0.0
public abstract class AbstractSchemaAttribute extends SimpleAttribute implements SchemaAttribute {

    private final Lazy<Attributes> attributes = new Lazy<>(this::buildAttributes);

    /// 构建此模式属性的嵌套属性。
    ///
    /// 在首次访问属性时延迟调用。
    ///
    /// @return 构建的属性
    protected abstract Attributes buildAttributes();

    /// 获取嵌套属性，如果需要则延迟构建它们。
    ///
    /// @return 嵌套属性
    public Attributes attributes() {
        return attributes.get();
    }

}
