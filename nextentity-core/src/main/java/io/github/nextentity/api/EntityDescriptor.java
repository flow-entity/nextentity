package io.github.nextentity.api;

import io.github.nextentity.core.meta.EntityType;
import io.github.nextentity.core.meta.Metamodel;

/// 实体描述符接口，提供实体元数据访问。
///
/// 该接口封装了实体的元信息，包括：
/// - Metamodel：全局元数据注册表
/// - EntityType：特定实体的类型元数据
/// - Class<E>：实体类本身
///
/// ## 主要用途
///
/// - 条件更新/删除构建器获取实体表名和列映射
/// - 表达式构建器获取属性元数据
/// - Repository 基类访问实体类型信息
///
/// @param <E> 实体类型
/// @author HuangChengwei
/// @see Metamodel 元数据注册表
/// @see EntityType 实体类型元数据
/// @since 2.2.0
public interface EntityDescriptor<E> {

    /// 返回实体元数据注册表。
    ///
    /// @return Metamodel 实例
    Metamodel metamodel();

    /// 返回实体类型元数据。
    ///
    /// @return EntityType 实例
    EntityType entityType();

    /// 返回实体类。
    ///
    /// @return 实体 Class 实例
    Class<E> entityClass();
}