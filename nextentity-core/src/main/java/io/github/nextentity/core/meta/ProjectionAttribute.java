package io.github.nextentity.core.meta;

/// 投影属性元数据接口，表示 DTO 或 Interface 投影中的一个字段映射。
///
/// 每个 {@code ProjectionAttribute} 都关联到一个 {@link EntityAttribute}（源属性），
/// 建立从投影字段到实体字段的映射关系。
///
/// 采用 sealed hierarchy 设计：
/// - {@link ProjectionBasicAttribute}：映射到实体的基本属性
/// - {@link ProjectionSchemaAttribute}：映射到实体的关联属性
/// - {@link ProjectionJoinAttribute}：显式 JOIN 到目标表
///
/// @see EntityAttribute
/// @see ProjectionSchema
public sealed interface ProjectionAttribute
        extends MetamodelAttribute permits
        ProjectionBasicAttribute,
        ProjectionComplexAttribute {

    /// 获取此投影属性对应的实体属性。
    ///
    /// @return 源实体属性
    EntityAttribute getEntityAttribute();

    @Override
    ProjectionSchema declareBy();
}
