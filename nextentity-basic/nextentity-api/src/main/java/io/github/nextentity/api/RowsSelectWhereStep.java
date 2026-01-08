package io.github.nextentity.api;

import io.github.nextentity.api.ExpressionBuilder.NumberOperator;
import io.github.nextentity.api.ExpressionBuilder.PathOperator;
import io.github.nextentity.api.ExpressionBuilder.StringOperator;
import io.github.nextentity.api.Path.NumberRef;
import io.github.nextentity.api.Path.StringRef;

/**
 * 行选择条件步骤接口，提供行选择的条件构建方法。
 * <p>
 * 继承自SelectGroupByStep和SelectWhereStep，提供分组和条件构建功能。
 *
 * @param <T> 实体类型
 * @param <U> 结果类型
 * @author HuangChengwei
 * @since 2026/1/7
 */
public
interface RowsSelectWhereStep<T, U> extends SelectGroupByStep<T, U>, SelectWhereStep<T, U> {

    /**
     * 添加指定的条件谓词。
     *
     * @param predicate 条件谓词
     * @return RowsSelectWhereStep实例
     */
    RowsSelectWhereStep<T, U> where(TypedExpression<T, Boolean> predicate);

    /**
     * 基于指定路径构建条件。
     *
     * @param path 路径
     * @param <N> 路径类型
     * @return PathOperator实例
     */
    <N> PathOperator<T, N, RowsSelectWhereStep<T, U>> where(Path<T, N> path);

    /**
     * 基于指定数字路径构建条件。
     *
     * @param path 数字路径
     * @param <N> 数字类型
     * @return NumberOperator实例
     */
    <N extends Number> NumberOperator<T, N, RowsSelectWhereStep<T, U>> where(NumberRef<T, N> path);

    /**
     * 基于指定字符串路径构建条件。
     *
     * @param path 字符串路径
     * @return StringOperator实例
     */
    StringOperator<T, RowsSelectWhereStep<T, U>> where(StringRef<T> path);

    /**
     * 基于指定路径表达式构建条件。
     *
     * @param path 路径表达式
     * @param <N> 路径类型
     * @return PathOperator实例
     */
    <N> PathOperator<T, N, RowsSelectWhereStep<T, U>> where(PathExpression<T, N> path);

    /**
     * 基于指定数字路径表达式构建条件。
     *
     * @param path 数字路径表达式
     * @param <N> 数字类型
     * @return NumberOperator实例
     */
    <N extends Number> NumberOperator<T, N, RowsSelectWhereStep<T, U>> where(NumberPath<T, N> path);

    /**
     * 基于指定字符串路径表达式构建条件。
     *
     * @param path 字符串路径表达式
     * @return StringOperator实例
     */
    StringOperator<T, RowsSelectWhereStep<T, U>> where(StringPath<T> path);

}
