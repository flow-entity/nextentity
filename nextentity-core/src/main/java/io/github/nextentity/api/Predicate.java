package io.github.nextentity.api;

import io.github.nextentity.core.expression.PredicateImpl;

/// 断言接口，表示查询条件。
///
/// 继承 SimpleExpression，提供逻辑操作方法（NOT、AND、OR）。
///
/// @param <T> 实体类型
/// @author HuangChengwei
/// @see SimpleExpression 断言创建方法示例
/// @see #ofTrue() 预定义常量断言
/// @since 1.0.0
public interface Predicate<T> extends SimpleExpression<T, Boolean>, ExpressionBuilder.Conjunction<T>, ExpressionBuilder.Disjunction<T> {

    /// 创建始终为 true 的断言。
    ///
    /// @param <T> 实体类型
    /// @return true 断言
    @SuppressWarnings("unchecked")
    static <T> Predicate<T> ofTrue() {
        return (Predicate<T>) PredicateImpl.TRUE;
    }

    /// 创建始终为 false 的断言。
    ///
    /// @param <T> 实体类型
    /// @return false 断言
    @SuppressWarnings("unchecked")
    static <T> Predicate<T> ofFalse() {
        return (Predicate<T>) PredicateImpl.FALSE;
    }

    /// 逻辑非操作。
    ///
    /// @return 取反后的断言
    Predicate<T> not();

    /// 逻辑与操作，与另一个断言组合。
    ///
    /// @param predicate 另一个断言
    /// @return 组合后的断言
    Predicate<T> and(Expression<T, Boolean> predicate);

    /// 逻辑或操作，与另一个断言组合。
    ///
    /// @param predicate 另一个断言
    /// @return 组合后的断言
    Predicate<T> or(Expression<T, Boolean> predicate);

    /// 逻辑与操作，与多个断言组合。
    ///
    /// @param predicate 断言数组
    /// @return 组合后的断言
    Predicate<T> and(Expression<T, Boolean>[] predicate);

    /// 逻辑或操作，与多个断言组合。
    ///
    /// @param predicate 断言数组
    /// @return 组合后的断言
    Predicate<T> or(Expression<T, Boolean>[] predicate);

    /// 逻辑与操作，与多个断言组合。
    ///
    /// @param predicates 断言迭代器
    /// @return 组合后的断言
    Predicate<T> and(Iterable<? extends Expression<T, Boolean>> predicates);

    /// 逻辑或操作，与多个断言组合。
    ///
    /// @param predicates 断言迭代器
    /// @return 组合后的断言
    Predicate<T> or(Iterable<? extends Expression<T, Boolean>> predicates);
}