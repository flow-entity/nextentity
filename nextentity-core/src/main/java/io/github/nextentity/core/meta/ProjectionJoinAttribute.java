package io.github.nextentity.core.meta;

import io.github.nextentity.core.exception.ConfigurationException;
import io.github.nextentity.core.reflect.schema.SchemaAttribute;
import jakarta.persistence.FetchType;

/// 投影连接属性接口，表示 DTO 投影中通过自定义 JOIN 关联到源实体未定义的表。
///
/// 与 {@link ProjectionSchemaAttribute} 复用源实体已有的 JPA 关联不同，
/// 连接属性可以在投影中定义源实体不存在的关联关系，直接通过
/// {@link #sourceAttribute()} 和 {@link #targetAttribute()} 指定外键关系，
/// JOIN 到任意目标表。
///
/// @see ProjectionAttribute
/// @see ProjectionSchemaAttribute
public non-sealed interface ProjectionJoinAttribute extends ProjectionAttribute, SchemaAttribute, Fetchable {

    /// 获取此连接属性的目标 Schema。
    ///
    /// 目标可能是 {@link ProjectionSchema}（投影到另一个 DTO）或 {@link EntitySchema}（直接关联实体）。
    ///
    /// @return 目标 Schema，为 {@link ProjectionSchema} 或 {@link EntitySchema}
    MetamodelSchema<?> target();

    default EntitySchema getEntitySchema() {
        if (target() instanceof EntitySchema entityType) {
            return entityType;
        } else if (target() instanceof ProjectionSchema projectionSchema) {
            return projectionSchema.getEntitySchema();
        } else {
            throw new ConfigurationException(
                    "Unsupported target type for projection join attribute: " +
                    target().getClass().getName() + ", expected EntitySchema or ProjectionSchema");
        }
    }

    /// 获取声明实体中此连接属性的名称。
    ///
    /// @return 连接属性名
    EntityBasicAttribute sourceAttribute();

    /// 获取引用目标实体外键列名。
    ///
    /// @return 引用列名
    EntityBasicAttribute targetAttribute();

    @Override
    FetchType fetchType();

}
