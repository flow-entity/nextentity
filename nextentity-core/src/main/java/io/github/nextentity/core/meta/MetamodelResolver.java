package io.github.nextentity.core.meta;

import io.github.nextentity.core.meta.impl.DefaultEntitySchema;
import io.github.nextentity.core.reflect.schema.Attribute;
import io.github.nextentity.core.reflect.schema.SchemaAttribute;
import jakarta.persistence.FetchType;

/// 元模型解析器接口，从 JPA 注解中提取实体元数据。
///
/// 负责将 JPA 注解（如 {@code @Column}、{@code @JoinColumn}、{@code @Id} 等）
/// 解析为框架内部的元模型结构。是 JPA 注解与 NextEntity 元模型之间的桥梁。
///
/// 通常由框架内部使用，应用代码不直接依赖此接口。
public interface MetamodelResolver {

    /// 获取实体类对应的数据库表名。
    ///
    /// @param type 实体类
    /// @return 表名
    String getTableName(Class<?> type);

    /// 检查属性是否被标记为 transient（不持久化）。
    ///
    /// @param attribute 属性
    /// @return 如果是 transient 则返回 {@code true}
    boolean isTransient(Attribute attribute);

    /// 检查属性是否为基本字段（非关联字段）。
    ///
    /// @param attribute 属性
    /// @return 如果是基本字段则返回 {@code true}
    boolean isBasicField(Attribute attribute);

    /// 检查属性是否为乐观锁版本字段。
    ///
    /// @param attribute 属性
    /// @return 如果是版本字段则返回 {@code true}
    boolean isVersionField(Attribute attribute);

    /// 获取属性对应的数据库列名。
    ///
    /// @param attribute 属性
    /// @return 列名
    String getColumnName(Attribute attribute);

    /// 检查属性是否被标记为主键。
    ///
    /// @param attribute 属性
    /// @return 如果是主键则返回 {@code true}
    boolean isMarkedId(Attribute attribute);

    /// 获取属性的值转换器（自定义数据库类型映射）。
    ///
    /// @param attribute 属性
    /// @return 值转换器，如果没有则返回 null
    ValueConverter<?, ?> databaseType(Attribute attribute);

    /// 检查属性对应的列是否可更新。
    ///
    /// @param attribute 属性
    /// @return 如果可更新则返回 {@code true}
    boolean isUpdatable(Attribute attribute);

    /// 获取关联属性的外键列名。
    ///
    /// @param attribute 关联属性
    /// @return 外键列名
    String getJoinColumnName(Attribute attribute);

    /// 获取关联属性引用的目标列名。
    ///
    /// @param attribute 关联属性
    /// @return 引用列名
    String getReferencedColumnName(Attribute attribute);

    /// 检查属性是否为多对一或一对一关联。
    ///
    /// @param attribute 属性
    /// @return 如果是 ToOne 关联则返回 {@code true}
    boolean isAnyToOne(SchemaAttribute attribute);

    /// 获取关联属性在源实体中的外键属性。
    ///
    /// @param sourceSchema 源实体的 schema
    /// @param attribute    关联属性
    /// @return 源端外键属性
    EntityBasicAttribute getJoinSourceAttribute(DefaultEntitySchema sourceSchema, Attribute attribute);

    /// 获取关联属性在目标实体中的引用属性。
    ///
    /// @param targetSchema 目标实体的 schema
    /// @param attribute    关联属性
    /// @return 目标端引用属性
    EntityBasicAttribute getJoinTargetAttribute(DefaultEntitySchema targetSchema, Attribute attribute);

    /// 获取关联属性映射到目标实体的嵌套路径。
    ///
    /// @param attribute 关联属性
    /// @return 属性名路径
    Iterable<String> getMappedEntityPath(Attribute attribute);

    /// 获取投影显式 JOIN 的目标类型。
    ///
    /// @param attribute 投影属性
    /// @return 目标类型，如果未标注则返回 null
    Class<?> getProjectionJoinTarget(Attribute attribute);

    /// 获取投影显式 JOIN 的源属性名。
    ///
    /// @param attribute 投影属性
    /// @return 源属性名，未标注返回 null
    String getProjectionJoinSourceAttribute(Attribute attribute);

    /// 获取投影显式 JOIN 的目标属性名。
    ///
    /// @param attribute 投影属性
    /// @return 目标属性名，未标注返回 null
    String getProjectionJoinTargetAttribute(Attribute attribute);

    /// 检查实体关联属性是否与投影的 schema 属性匹配。
    ///
    /// @param entitySchemaAttribute 实体的关联属性
    /// @param schemaAttribute       投影的 schema 属性
    /// @return 如果匹配则返回 {@code true}
    boolean matchProjectionSchemaAttribute(EntitySchemaAttribute entitySchemaAttribute, SchemaAttribute schemaAttribute);

    /// 检查实体基本属性是否与投影的基本属性匹配。
    ///
    /// @param entityBasicAttribute 实体的基本属性
    /// @param attribute            投影属性
    /// @return 如果匹配则返回 {@code true}
    boolean matchProjectionBasicAttribute(EntityBasicAttribute entityBasicAttribute, Attribute attribute);

    /// 获取实体类对应的实体名称。
    ///
    /// @param type 实体类
    /// @return 实体名称
    String getEntityName(Class<?> type);

    /// 获取属性的加载策略。
    ///
    /// 用于确定实体属性的加载策略。若属性未标注加载策略注解，
    /// 返回 {@code null}，表示应使用全局默认配置。
    ///
    /// @param attribute 要检查的属性
    /// @return 加载策略，或 {@code null} 表示使用全局默认
    FetchType getFetchType(Attribute attribute);
}
