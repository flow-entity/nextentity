package io.github.nextentity.spring;

import io.github.nextentity.api.DeleteWhereStep;
import io.github.nextentity.api.QueryBuilder;
import io.github.nextentity.api.UpdateWhereStep;
import io.github.nextentity.core.UpdateExecutor;

/// NextEntity 工厂接口。
///
/// 该接口定义了创建 NextEntity 核心组件的工厂方法：
/// - QueryBuilder：用于构建类型安全的查询
/// - UpdateExecutor：用于执行插入、更新、删除操作
/// - UpdateWhereStep：用于条件批量更新
/// - DeleteWhereStep：用于条件批量删除
///
/// 实现类可以选择不同的数据库访问策略：
/// - JDBC 模式：纯 JDBC 操作
/// - JPA 模式：结合 JPA 和 JDBC
///
/// @author HuangChengwei
/// @since 1.0.0
public interface NextEntityFactory {

    /// 创建指定实体类型的查询构建器。
    ///
    /// @param entityType 实体类型
    /// @param <T>        实体类型参数
    /// @return 查询构建器实例
    <T> QueryBuilder<T> queryBuilder(Class<T> entityType);

    /// 获取更新执行器。
    ///
    /// @return 更新执行器实例
    UpdateExecutor updateExecutor();

}
