package io.github.nextentity.core.meta;

import io.github.nextentity.core.reflect.schema.SimpleAttribute;

/// {@link ProjectionAttribute} 的简单实现。
///
/// 此类为投影属性提供了具体实现，
/// 委托给源实体属性进行值转换和数据库映射。
///
/// @author HuangChengwei
/// @since 1.0.0
public class SimpleProjectionAttribute extends SimpleAttribute implements ProjectionAttribute {

    private final EntityAttribute sourceAttribute;

    /// 创建新的 SimpleProjectionAttribute 实例。
    ///
    /// @param sourceAttribute 要委托的源实体属性
    public SimpleProjectionAttribute(EntityAttribute sourceAttribute) {
        this.sourceAttribute = sourceAttribute;
    }

    /// 获取源实体属性。
    ///
    /// @return 源实体属性
    @Override
    public EntityAttribute source() {
        return sourceAttribute;
    }

    /// 从源实体属性继承可更新状态。
    ///
    /// @return 源属性的可更新状态
    @Override
    public boolean isUpdatable() {
        return sourceAttribute.isUpdatable();
    }
}
