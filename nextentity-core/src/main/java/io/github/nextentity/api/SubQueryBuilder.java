package io.github.nextentity.api;

import java.util.List;

/// 子查询构建器接口，用于构建和执行子查询操作。
///
/// ## 使用示例
///
/// ```java
/// // 子查询获取数量
/// Expression<User, Long> countExpr = repository.query()
///     .where(User::getStatus).eq("ACTIVE")
///     .toSubQuery()
///     .count();
///
/// // 子查询限制结果
/// Expression<User, List<User>> limitedExpr = repository.query()
///     .where(User::getStatus).eq("ACTIVE")
///     .toSubQuery()
///     .limit(10);
/// ```
///
/// @param <T> 实体类型
/// @param <U> 查询结果类型
/// @author HuangChengwei
/// @since 1.0.0
public interface SubQueryBuilder<T, U> extends Expression<T, List<U>> {
    /// 获取查询结果的总数。
    ///
    /// @return 计数表达式
    Expression<T, Long> count();

    /// 限制结果数量。
    ///
    /// @param limit 最大结果数
    /// @return 限制后的结果表达式
    default Expression<T, List<U>> limit(int limit) {
        return slice(0, limit);
    }

    /// 获取查询结果的一部分。
    ///
    /// @param offset 起始偏移量
    /// @param limit  最大结果数
    /// @return 分片结果表达式
    Expression<T, List<U>> slice(int offset, int limit);

}