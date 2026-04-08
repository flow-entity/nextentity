package io.github.nextentity.spring;

import io.github.nextentity.api.QueryBuilder;
import io.github.nextentity.core.PaginationConfig;
import io.github.nextentity.core.UpdateExecutor;
import io.github.nextentity.core.meta.Metamodel;

/// NextEntity 上下文接口。
///
/// 该接口提供 NextEntity 核心组件的访问入口：
/// - QueryBuilder：用于构建类型安全的查询（每次调用创建新实例）
/// - UpdateExecutor：用于执行插入、更新、删除操作（共享实例）
/// - Metamodel：实体元模型（共享实例）
/// - PaginationConfig：分页配置（共享实例）
///
/// 实现类可以选择不同的数据库访问策略：
/// - JDBC 模式：纯 JDBC 操作
/// - JPA 模式：结合 JPA 和 JDBC
///
/// @author HuangChengwei
/// @since 1.0.0
public interface NextEntityContext {

    /// 创建指定实体类型的查询构建器。
    ///
    /// 每次调用都会创建新的 QueryBuilder 实例，
    /// 因为 QueryBuilder 在构建查询过程中会累积状态。
    ///
    /// @param entityType 实体类型
    /// @param <T>        实体类型参数
    /// @return 新的查询构建器实例
    <T> QueryBuilder<T> createQueryBuilder(Class<T> entityType);

    /// 获取共享的更新执行器实例。
    ///
    /// UpdateExecutor 是无状态的，可以安全地在多个 Repository 间共享。
    ///
    /// @return 更新执行器实例
    UpdateExecutor getUpdateExecutor();

    /// 获取共享的元模型实例。
    ///
    /// Metamodel 包含实体类的元数据信息，
    /// 如表名、属性映射、关联关系等。
    ///
    /// @return 元模型实例
    Metamodel getMetamodel();

    /// 获取共享的分页配置实例。
    ///
    /// PaginationConfig 包含分页查询的相关配置，
    /// 如是否自动添加主键排序、日志级别等。
    ///
    /// @return 分页配置实例
    PaginationConfig getPaginationConfig();

}
