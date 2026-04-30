package io.github.nextentity.core.meta;

import jakarta.persistence.FetchType;

/// 可懒加载属性标记接口。
///
/// 由 {@link EntitySchemaAttribute} 和 {@link ProjectionSchemaAttribute} 共同实现，
/// 用于统一判断属性是否需要延迟加载。
public sealed interface JoinAttribute extends MetamodelAttribute
        permits EntitySchemaAttribute, ProjectionJoinAttribute, ProjectionSchemaAttribute {

    /// 获取此连接属性指向的目标 Schema。
    ///
    /// 对于实体关联（{@link EntitySchemaAttribute}）返回目标实体 Schema；
    /// 对于投影关联（{@link ProjectionSchemaAttribute}）返回目标投影 Schema。
    ///
    /// @return 目标 Schema
    MetamodelSchema<?> schema();

    @Override
    default boolean isObject() {
        return true;
    }

    @Override
    default boolean isPrimitive() {
        return false;
    }

    /// 获取此关联的连接表名。
    ///
    /// 对于多对多关系，返回中间连接表。
    /// 对于其他关系，返回目标实体的表名。
    ///
    /// @return 连接表名
    EntityType getTargetEntityType();

    /// 获取声明实体中此连接属性的名称。
    ///
    /// @return 连接属性名
    EntityBasicAttribute getSourceAttribute();

    /// 获取引用目标实体外键列名。
    ///
    /// @return 引用列名
    EntityBasicAttribute getTargetAttribute();

    /// 获取加载策略。
    ///
    /// 优先级：投影级 @Fetch > source().fetchType() > 全局默认
    ///
    /// @return FetchType.LAZY 或 FetchType.EAGER，或 null（使用全局默认）
    FetchType getFetchType();

}
