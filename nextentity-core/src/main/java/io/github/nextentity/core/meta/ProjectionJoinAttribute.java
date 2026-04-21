package io.github.nextentity.core.meta;

import io.github.nextentity.core.reflect.schema.SchemaAttribute;
import jakarta.persistence.FetchType;

/// 投影连接属性接口，表示 DTO 投影中通过自定义 JOIN 关联到源实体未定义的表。
///
/// 与 {@link ProjectionSchemaAttribute} 复用源实体已有的 JPA 关联不同，
/// 连接属性可以在投影中定义源实体不存在的关联关系，直接通过
/// {@link #getSourceAttribute()} 和 {@link #getTargetAttribute()} 指定外键关系，
/// JOIN 到任意目标表。
///
/// @see ProjectionAttribute
/// @see ProjectionSchemaAttribute
public non-sealed interface ProjectionJoinAttribute extends ProjectionAttribute, SchemaAttribute, JoinAttribute {

}
