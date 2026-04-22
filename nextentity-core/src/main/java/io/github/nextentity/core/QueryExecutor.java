package io.github.nextentity.core;

import io.github.nextentity.jdbc.QueryContext;
import org.jspecify.annotations.NonNull;

import java.util.List;

/// 查询执行器接口，用于执行 SELECT 查询。
///
/// 该接口负责执行查询构建器构建的查询结构
/// 并返回实体结果列表。
///
/// 实现通常使用 JDBC 或 JPA 与数据库交互。
///
/// @author HuangChengwei
/// @since 1.0.0
public interface QueryExecutor {

    ///
    /// 执行查询并返回实体列表结果。
    ///
    /// 查询上下文包含构建和执行 SQL 查询所需的所有信息，
    /// 包括选择、过滤、排序和分页。
    ///
    /// 实现会在执行前调用 context.init() 完成初始化，
    /// 并设置 expandReferencePath 等配置。
    ///
    /// @param <T>     结果实体类型
    /// @param context 查询上下文
    /// @return 符合查询条件的实体列表
    /// @throws NullPointerException 如果 context 为 null
    ///
    <T> List<T> getList(@NonNull QueryContext context);
}
