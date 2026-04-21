package io.github.nextentity.core.meta;

/// 用于管理实体类型元数据的元模型接口。
///
/// 该接口提供对 {@link EntityType} 实例的访问，这些实例包含
/// 关于实体类的元数据，包括表名、属性和
/// 关系信息。
///
/// 元模型通常在应用程序启动期间配置，并提供
/// 查询构建和实体持久化操作的基础。
///
/// @author HuangChengwei
/// @since 1.0.0
public interface Metamodel {

    /// 获取指定类的实体类型元数据。
    ///
    /// 返回的 {@link EntityType} 包含有关实体表结构、属性和关系的信息。
    ///
    /// @param type 要获取元数据的实体类
    /// @return 实体类型元数据
    /// @throws IllegalArgumentException 如果给定类型不存在元数据
    EntityType getEntity(Class<?> type);

}
