package io.github.nextentity.api;

import io.github.nextentity.api.ExpressionBuilder.NumberOperator;
import io.github.nextentity.api.ExpressionBuilder.PathOperator;
import io.github.nextentity.api.ExpressionBuilder.StringOperator;
import io.github.nextentity.api.Path.NumberRef;
import io.github.nextentity.api.Path.StringRef;

/**
 * 查询条件构建步骤接口，提供添加查询条件的方法。
 *
 * @param <T> 实体类型
 * @param <U> 查询结果类型
 * @author HuangChengwei
 * @since 2026/1/7
 */
public
interface SelectWhereStep<T, U> extends SelectOrderByStep<T, U> {

    /**
     * 添加布尔表达式作为查询条件。
     *
     * @param predicate 布尔表达式
     * @return 查询条件构建步骤
     */
    SelectWhereStep<T, U> where(TypedExpression<T, Boolean> predicate);

    /**
     * 添加路径作为查询条件。
     *
     * @param path 路径
     * @param <N> 路径类型
     * @return 路径操作符
     */
    <N> PathOperator<T, N, ? extends SelectWhereStep<T, U>> where(Path<T, N> path);

    /**
     * 添加数字路径作为查询条件。
     *
     * @param path 数字路径
     * @param <N> 数字类型
     * @return 数字操作符
     */
    <N extends Number> NumberOperator<T, N, ? extends SelectWhereStep<T, U>> where(NumberRef<T, N> path);

    /**
     * 添加字符串路径作为查询条件。
     *
     * @param path 字符串路径
     * @return 字符串操作符
     */
    StringOperator<T, ? extends SelectWhereStep<T, U>> where(StringRef<T> path);

    /**
     * 添加路径表达式作为查询条件。
     *
     * @param path 路径表达式
     * @param <N> 路径类型
     * @return 路径操作符
     */
    <N> PathOperator<T, N, ? extends SelectWhereStep<T, U>> where(PathExpression<T, N> path);

    /**
     * 添加数字路径作为查询条件。
     *
     * @param path 数字路径
     * @param <N> 数字类型
     * @return 数字操作符
     */
    <N extends Number> NumberOperator<T, N, ? extends SelectWhereStep<T, U>> where(NumberPath<T, N> path);

    /**
     * 添加字符串路径作为查询条件。
     *
     * @param path 字符串路径
     * @return 字符串操作符
     */
    StringOperator<T, ? extends SelectWhereStep<T, U>> where(StringPath<T> path);


}
