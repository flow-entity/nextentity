package io.github.nextentity.core.meta;

/// 投影 Schema 接口，描述 DTO 或 Interface 投影类的属性映射元数据。
///
/// 每个投影类都关联一个 {@link EntitySchema}（源实体），
/// 通过 {@link ProjectionAttribute} 集合描述投影字段到实体字段的映射关系。
///
/// @see MetamodelSchema
/// @see ProjectionAttribute
/// @see EntitySchema
public interface ProjectionSchema extends MetamodelSchema<ProjectionAttribute> {

    /// 获取此投影对应的源实体 Schema。
    ///
    /// @return 源实体 Schema
    EntitySchema getEntitySchema();
}
