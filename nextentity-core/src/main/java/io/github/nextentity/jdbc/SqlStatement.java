package io.github.nextentity.jdbc;

import io.github.nextentity.core.SqlLogger;

///
/// SQL语句接口
///
/// 该接口定义了SQL语句的基本规范，包括SQL语句文本和参数列表，
/// 为各种类型的SQL语句（查询、更新、插入、删除）提供了统一的表示方式。
///
/// @author HuangChengwei
/// @since 1.0.0
///
public interface SqlStatement {

    /// 获取SQL语句文本
    ///
    /// @return SQL语句字符串
    String sql();

    /// 获取参数列表
    ///
    /// @return SQL语句中使用的参数迭代器
    Iterable<?> parameters();

    /// 调试输出SQL语句及参数
    ///
    /// 输出SQL语句文本和参数到日志，便于调试
    default void debug() {
        SqlLogger.debug(sql());
        SqlLogger.debug("sql parameters:{}", parameters());
    }

}
