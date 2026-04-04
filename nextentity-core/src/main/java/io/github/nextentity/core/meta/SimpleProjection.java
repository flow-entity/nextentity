package io.github.nextentity.core.meta;

import io.github.nextentity.core.reflect.schema.Attributes;

/// {@link ProjectionType} 的简单实现。
///
/// 此类为投影类型元数据提供了具体实现，
/// 将实体属性映射到 DTO/投影类字段。
///
/// @author HuangChengwei
/// @since 1.0.0
public class SimpleProjection implements ProjectionType {

    private final Class<?> type;
    private final EntitySchema entityType;
    private Attributes attributes;

    /// 创建新的 SimpleProjection 实例。
    ///
    /// @param type 投影类
    /// @param entityType 源实体模式
    public SimpleProjection(Class<?> type, EntitySchema entityType) {
        this.type = type;
        this.entityType = entityType;
    }

    /// 获取源实体模式。
    ///
    /// @return 此投影映射的实体模式
    @Override
    public EntitySchema source() {
        return entityType;
    }

    @Override
    public Attributes attributes() {
        return attributes;
    }

    @Override
    public Class<?> type() {
        return type;
    }

    /// 设置投影属性。
    ///
    /// @param attributes 投影属性
    public void setAttributes(Attributes attributes) {
        this.attributes = attributes;
    }


}
