package io.github.nextentity.core.expression;

import io.github.nextentity.api.Expression;
import io.github.nextentity.api.NumberExpression;
import io.github.nextentity.api.NumberPath;

/// 数字表达式的具体实现类，提供了数字运算的功能。
///
/// @param <T> 实体类型
/// @param <U> 数字类型
public class NumberExpressionImpl<T, U extends Number> extends SimpleExpressionImpl<T, U> implements NumberPath<T, U> {

    /// 使用表达式节点构造数字表达式实例。
    ///
    /// @param root 根表达式节点
    public NumberExpressionImpl(ExpressionNode root) {
        super(root);
    }

    /// 与另一个表达式相加。
    ///
    /// @param expression 要相加的表达式
    /// @return 相加后的数字表达式
    @Override
    public NumberExpression<T, U> add(Expression<T, U> expression) {
        return new NumberExpressionImpl<>(operate(Operator.ADD, getNode(expression)));
    }

    /// 与另一个表达式相减。
    ///
    /// @param expression 要相减的表达式
    /// @return 相减后的数字表达式
    @Override
    public NumberExpression<T, U> subtract(Expression<T, U> expression) {
        return new NumberExpressionImpl<>(operate(Operator.SUBTRACT, getNode(expression)));
    }

    /// 与另一个表达式相乘。
    ///
    /// @param expression 要相乘的表达式
    /// @return 相乘后的数字表达式
    @Override
    public NumberExpression<T, U> multiply(Expression<T, U> expression) {
        return new NumberExpressionImpl<>(operate(Operator.MULTIPLY, getNode(expression)));
    }

    /// 与另一个表达式相除。
    ///
    /// @param expression 要相除的表达式
    /// @return 相除后的数字表达式
    @Override
    public NumberExpression<T, U> divide(Expression<T, U> expression) {
        return new NumberExpressionImpl<>(operate(Operator.DIVIDE, getNode(expression)));
    }

    /// 与另一个表达式取模。
    ///
    /// @param expression 要取模的表达式
    /// @return 取模后的数字表达式
    @Override
    public NumberExpression<T, U> mod(Expression<T, U> expression) {
        return new NumberExpressionImpl<>(operate(Operator.MOD, getNode(expression)));
    }

    /// 计算总和。
    ///
    /// @return 求和后的数字表达式
    @Override
    public NumberExpression<T, U> sum() {
        return new NumberExpressionImpl<>(operate(Operator.SUM));
    }

    /// 计算平均值。
    ///
    /// @return 平均值数字表达式（Double类型）
    @Override
    public NumberExpression<T, Double> avg() {
        return new NumberExpressionImpl<>(operate(Operator.AVG));
    }

    /// 计算最大值。
    ///
    /// @return 最大值数字表达式
    @Override
    public NumberExpression<T, U> max() {
        return new NumberExpressionImpl<>(operate(Operator.MAX));
    }

    /// 计算最小值。
    ///
    /// @return 最小值数字表达式
    @Override
    public NumberExpression<T, U> min() {
        return new NumberExpressionImpl<>(operate(Operator.MIN));
    }
}
