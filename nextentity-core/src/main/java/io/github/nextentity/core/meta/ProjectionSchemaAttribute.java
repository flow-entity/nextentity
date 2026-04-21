package io.github.nextentity.core.meta;

import io.github.nextentity.core.reflect.schema.SchemaAttribute;

/// 投影关联属性接口，复用源实体已定义的 JPA 关联关系。
///
/// 与 {@link ProjectionJoinAttribute} 可自定义 JOIN 到任意表不同，
/// 此接口要求源实体已通过 JPA 注解定义了对应的关联（如 {@code @ManyToOne}）。
/// 例如投影类中的 {@code DepartmentInfo department} 复用实体的 {@code department} 关联。
/// 实现 {@link Fetchable} 接口，支持在投影级别覆盖关联的延迟加载策略。
///
/// @see ProjectionAttribute
/// @see EntitySchemaAttribute
/// @see Fetchable
public non-sealed interface ProjectionSchemaAttribute extends ProjectionAttribute, SchemaAttribute, Fetchable {

    /// 获取此投影属性对应的实体关联属性。
    ///
    /// @return 源实体关联属性
    @Override
    EntitySchemaAttribute source();
}
