package io.github.nextentity.core.meta;

/// 实体类型接口，扩展 {@link EntitySchema} 并提供投影支持。
///
/// 此接口提供实体元数据以及检索与此实体关联的DTO/投影类的投影类型元数据的能力。
///
/// EntityType 实例从 {@link Metamodel} 中获取，包含查询构建和实体持久化所需的所有元数据。
///
/// @author HuangChengwei
/// @since 1.0.0
public interface EntityType extends EntitySchema {

    /// 获取指定投影类的投影类型元数据。
    ///
    /// 投影类型定义查询结果如何映射到DTO或其他非实体结果类型。
    ///
    /// @param type 要检索元数据的投影类
    /// @return 投影类型元数据
    /// @throws IllegalArgumentException 如果给定类型不存在投影元数据则抛出
    ProjectionType getProjection(Class<?> type);

}
