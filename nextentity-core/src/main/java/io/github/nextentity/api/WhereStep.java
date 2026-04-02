package io.github.nextentity.api;

import io.github.nextentity.api.ExpressionBuilder.NumberOperator;
import io.github.nextentity.api.ExpressionBuilder.PathOperator;
import io.github.nextentity.api.ExpressionBuilder.StringOperator;
import io.github.nextentity.api.PathRef.NumberRef;
import io.github.nextentity.api.PathRef.StringRef;

/// 行选择条件步骤接口，提供条件构建方法。
///
/// 继承 SelectGroupByStep 和 WhereStep，提供分组和条件构建功能。
///
/// ## 使用示例
///
/// ```java
/// // 简单条件
/// List<User> users = repository.query()
///     .where(User::getAge).gt(18)
///     .getList();
///
/// // 多个条件（AND 关系）
/// List<User> users = repository.query()
///     .where(User::getAge).gt(18)
///     .where(User::getStatus).eq("ACTIVE")
///     .getList();
///
/// // 使用路径表达式
/// List<User> users = repository.query()
///     .where(Path.of(User::getName)).like("%张%")
///     .getList();
/// ```
///
/// @param <T> 实体类型
/// @param <U> 结果类型
/// @author HuangChengwei
/// @since 1.0.0
public interface WhereStep<T, U> extends GroupByStep<T, U>, BaseWhereStep<T, U> {

    /// 添加指定的条件断言。
    ///
    /// @param predicate 条件断言
    /// @return WhereStep 实例
    WhereStep<T, U> where(Expression<T, Boolean> predicate);

    /// 基于指定路径构建条件。
    ///
    /// @param path 路径
    /// @param <N> 路径类型
    /// @return PathOperator 实例
    <N> PathOperator<T, N, WhereStep<T, U>> where(PathRef<T, N> path);

    /// 基于指定数值路径构建条件。
    ///
    /// @param path 数值路径
    /// @param <N> 数值类型
    /// @return NumberOperator 实例
    <N extends Number> NumberOperator<T, N, WhereStep<T, U>> where(NumberRef<T, N> path);

    /// 基于指定字符串路径构建条件。
    ///
    /// @param path 字符串路径
    /// @return StringOperator 实例
    StringOperator<T, WhereStep<T, U>> where(StringRef<T> path);

    /// 基于指定路径表达式构建条件。
    ///
    /// @param path 路径表达式
    /// @param <N> 路径类型
    /// @return PathOperator 实例
    <N> PathOperator<T, N, WhereStep<T, U>> where(Path<T, N> path);

    /// 基于指定数值路径表达式构建条件。
    ///
    /// @param path 数值路径表达式
    /// @param <N> 数值类型
    /// @return NumberOperator 实例
    <N extends Number> NumberOperator<T, N, WhereStep<T, U>> where(NumberPath<T, N> path);

    /// 基于指定字符串路径表达式构建条件。
    ///
    /// @param path 字符串路径表达式
    /// @return StringOperator 实例
    StringOperator<T, WhereStep<T, U>> where(StringPath<T> path);

}