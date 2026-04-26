package io.github.nextentity.jdbc;

import io.github.nextentity.core.constructor.QueryContext;

/// 查询SQL构建器接口
///
/// 用于构建查询相关的SQL语句
public interface QuerySqlBuilder {
    /// 构建查询SQL语句
    ///
    /// @param context 查询上下文
    /// @return 查询SQL语句对象
    QuerySqlStatement buildQueryStatement(QueryContext context);
}
