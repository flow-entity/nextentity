package io.github.nextentity.api;

import java.util.List;

/**
 * 子查询构建器接口，用于构建和执行子查询操作。
 *
 * @param <T> 实体类型
 * @param <U> 查询结果类型
 * @author HuangChengwei
 * @since 2026/1/7
 */
public
interface SubQueryBuilder<T, U> extends TypedExpression<T, List<U>> {
    /**
     * 获取查询结果的总数。
     *
     * @return 计数表达式
     */
    TypedExpression<T, Long> count();

    /**
     * 截取查询结果的一部分。
     *
     * @param offset 起始偏移量
     * @param maxResult 最大结果数
     * @return 截取后的结果表达式
     */
    TypedExpression<T, List<U>> slice(int offset, int maxResult);

    /**
     * 获取单个查询结果。
     *
     * @return 单个结果表达式
     */
    default TypedExpression<T, U> getSingle() {
        return getSingle(-1);
    }

    /**
     * 从指定偏移量开始获取单个查询结果。
     *
     * @param offset 起始偏移量
     * @return 单个结果表达式
     */
    TypedExpression<T, U> getSingle(int offset);

    /**
     * 获取第一个查询结果。
     *
     * @return 第一个结果表达式
     */
    default TypedExpression<T, U> getFirst() {
        return getFirst(-1);
    }

    /**
     * 从指定偏移量开始获取第一个查询结果。
     *
     * @param offset 起始偏移量
     * @return 第一个结果表达式
     */
    TypedExpression<T, U> getFirst(int offset);
}
