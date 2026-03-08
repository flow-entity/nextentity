package io.github.nextentity.api;

/**
 * 谓词接口，表示查询条件。
 * <p>
 * 提供逻辑操作方法，如非、与、或等。
 *
 * @param <T> 实体类型
 * @author HuangChengwei
 * @since 2026/1/7
 */
public interface Predicate<T> extends SimpleExpression<T, Boolean>, ExpressionBuilder.Conjunction<T>, ExpressionBuilder.Disjunction<T> {
    /**
     * 逻辑非操作。
     *
     * @return 否定后的谓词
     */
    io.github.nextentity.api.Predicate<T> not();

    /**
     * 逻辑与操作，与另一个谓词组合。
     *
     * @param predicate 另一个谓词
     * @return 组合后的谓词
     */
    io.github.nextentity.api.Predicate<T> and(TypedExpression<T, Boolean> predicate);

    /**
     * 逻辑或操作，与另一个谓词组合。
     *
     * @param predicate 另一个谓词
     * @return 组合后的谓词
     */
    io.github.nextentity.api.Predicate<T> or(TypedExpression<T, Boolean> predicate);

    /**
     * 逻辑与操作，与多个谓词组合。
     *
     * @param predicate 谓词数组
     * @return 组合后的谓词
     */
    io.github.nextentity.api.Predicate<T> and(TypedExpression<T, Boolean>[] predicate);

    /**
     * 逻辑或操作，与多个谓词组合。
     *
     * @param predicate 谓词数组
     * @return 组合后的谓词
     */
    io.github.nextentity.api.Predicate<T> or(TypedExpression<T, Boolean>[] predicate);

    /**
     * 逻辑与操作，与多个谓词组合。
     *
     * @param predicates 谓词迭代器
     * @return 组合后的谓词
     */
    io.github.nextentity.api.Predicate<T> and(Iterable<? extends TypedExpression<T, Boolean>> predicates);

    /**
     * 逻辑或操作，与多个谓词组合。
     *
     * @param predicates 谓词迭代器
     * @return 组合后的谓词
     */
    io.github.nextentity.api.Predicate<T> or(Iterable<? extends TypedExpression<T, Boolean>> predicates);
}
