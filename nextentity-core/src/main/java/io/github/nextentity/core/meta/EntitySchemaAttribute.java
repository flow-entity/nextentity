package io.github.nextentity.core.meta;

/// 实体关联属性接口，表示实体类中引用其他实体的关联关系。
///
/// 对应 JPA 的 {@code @ManyToOne}、{@code @OneToMany}、{@code @OneToOne}、{@code @ManyToMany} 注解。
/// 提供关联的目标实体类型、源端外键属性和目标端引用属性等元数据。
///
/// 实现 {@link JoinAttribute} 接口，支持配置关联的延迟加载策略。
///
/// @see EntityBasicAttribute
/// @see JoinAttribute
public non-sealed interface EntitySchemaAttribute extends EntityComplexAttribute, JoinAttribute {
    EntitySchema schema();
}
