package io.github.nextentity.core.meta;

import io.github.nextentity.core.reflect.schema.Schema;

/// DTO 和投影结果映射的投影类型接口。
///
/// 该接口扩展 {@link Schema} 并提供用于映射的元数据
/// 查询结果到非实体类型，如 DTO、记录或接口。
///
/// 投影类型从 {@link EntityType#getProjection(Class)} 获取
/// 并定义实体属性如何映射到投影字段。
///
/// @author HuangChengwei
/// @since 1.0.0
public interface ProjectionType extends Schema {

    /// 获取此投影映射的源实体模式。
    ///
    /// @return 源实体模式
    Schema source();

}
