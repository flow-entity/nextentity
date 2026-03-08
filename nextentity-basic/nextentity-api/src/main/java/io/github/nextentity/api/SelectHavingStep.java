package io.github.nextentity.api;

/**
 * 选择分组条件步骤接口，提供分组后的条件构建方法。
 * <p>
 * 继承自SelectOrderByStep，用于在分组后添加筛选条件。
 *
 * @param <T> 实体类型
 * @param <U> 结果类型
 * @author HuangChengwei
 * @since 2026/1/7
 */
public
interface SelectHavingStep<T, U> extends SelectOrderByStep<T, U> {

    /**
     * 添加指定的分组条件谓词。
     *
     * @param predicate 条件谓词
     * @return SelectOrderByStep实例
     */
    SelectOrderByStep<T, U> having(TypedExpression<T, Boolean> predicate);

}
