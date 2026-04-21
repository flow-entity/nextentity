package io.github.nextentity.core.meta;

import io.github.nextentity.core.reflect.schema.SchemaAttribute;
import jakarta.persistence.FetchType;

/// 实体关联属性接口，表示实体类中引用其他实体的关联关系。
///
/// 对应 JPA 的 {@code @ManyToOne}、{@code @OneToMany}、{@code @OneToOne}、{@code @ManyToMany} 注解。
/// 提供关联的目标实体类型、源端外键属性和目标端引用属性等元数据。
///
/// 实现 {@link Fetchable} 接口，支持配置关联的延迟加载策略。
///
/// @see EntityBasicAttribute
/// @see Fetchable
public non-sealed interface EntitySchemaAttribute extends EntityAttribute, SchemaAttribute, Fetchable {

    /// 获取此关联的连接表名。
    ///
    /// 对于多对多关系，返回中间连接表。
    /// 对于其他关系，返回目标实体的表名。
    ///
    /// @return 连接表名
    EntitySchema target();

    /// 获取声明实体中此连接属性的名称。
    ///
    /// @return 连接属性名
    EntityBasicAttribute sourceAttribute();

    /// 获取引用目标实体外键列名。
    ///
    /// @return 引用列名
    EntityBasicAttribute targetAttribute();

}
