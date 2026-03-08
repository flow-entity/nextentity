package io.github.nextentity.api;

/**
 * 数字表达式接口，提供数字类型的表达式操作方法。
 * <p>
 * 继承自SimpleExpression，提供基本的表达式操作方法，同时添加数字特有的操作方法。
 *
 * @param <T> 实体类型
 * @param <U> 数字类型
 * @author HuangChengwei
 * @since 2026/1/7
 */
public interface NumberExpression<T, U extends Number> extends SimpleExpression<T, U> {
    /**
     * 加法操作，与另一个表达式相加。
     *
     * @param expression 另一个表达式
     * @return 加法结果表达式
     */
    io.github.nextentity.api.NumberExpression<T, U> add(TypedExpression<T, U> expression);

    /**
     * 减法操作，减去另一个表达式。
     *
     * @param expression 另一个表达式
     * @return 减法结果表达式
     */
    io.github.nextentity.api.NumberExpression<T, U> subtract(TypedExpression<T, U> expression);

    /**
     * 乘法操作，与另一个表达式相乘。
     *
     * @param expression 另一个表达式
     * @return 乘法结果表达式
     */
    io.github.nextentity.api.NumberExpression<T, U> multiply(TypedExpression<T, U> expression);

    /**
     * 除法操作，除以另一个表达式。
     *
     * @param expression 另一个表达式
     * @return 除法结果表达式
     */
    io.github.nextentity.api.NumberExpression<T, U> divide(TypedExpression<T, U> expression);

    /**
     * 取模操作，与另一个表达式取模。
     *
     * @param expression 另一个表达式
     * @return 取模结果表达式
     */
    io.github.nextentity.api.NumberExpression<T, U> mod(TypedExpression<T, U> expression);

    /**
     * 求和操作。
     *
     * @return 求和结果表达式
     */
    io.github.nextentity.api.NumberExpression<T, U> sum();

    /**
     * 求平均值操作。
     *
     * @return 平均值结果表达式
     */
    io.github.nextentity.api.NumberExpression<T, Double> avg();

    /**
     * 求最大值操作。
     *
     * @return 最大值结果表达式
     */
    io.github.nextentity.api.NumberExpression<T, U> max();

    /**
     * 求最小值操作。
     *
     * @return 最小值结果表达式
     */
    io.github.nextentity.api.NumberExpression<T, U> min();

    /**
     * 加法操作，与指定值相加。
     *
     * @param value 要相加的值
     * @return 加法结果表达式
     */
    default io.github.nextentity.api.NumberExpression<T, U> add(U value) {
        return add(root().literal(value));
    }

    /**
     * 减法操作，减去指定值。
     *
     * @param value 要减去的值
     * @return 减法结果表达式
     */
    default io.github.nextentity.api.NumberExpression<T, U> subtract(U value) {
        return subtract(root().literal(value));
    }

    /**
     * 乘法操作，与指定值相乘。
     *
     * @param value 要相乘的值
     * @return 乘法结果表达式
     */
    default io.github.nextentity.api.NumberExpression<T, U> multiply(U value) {
        return multiply(root().literal(value));
    }

    /**
     * 除法操作，除以指定值。
     *
     * @param value 要除以的值
     * @return 除法结果表达式
     */
    default io.github.nextentity.api.NumberExpression<T, U> divide(U value) {
        return divide(root().literal(value));
    }

    /**
     * 取模操作，与指定值取模。
     *
     * @param value 要取模的值
     * @return 取模结果表达式
     */
    default io.github.nextentity.api.NumberExpression<T, U> mod(U value) {
        return mod(root().literal(value));
    }

    /**
     * 条件加法操作，当值不为null时与指定值相加。
     *
     * @param value 要相加的值
     * @return 加法结果表达式或当前表达式（如果值为null）
     */
    default io.github.nextentity.api.NumberExpression<T, U> addIfNotNull(U value) {
        return value == null ? this : add(value);
    }

    /**
     * 条件减法操作，当值不为null时减去指定值。
     *
     * @param value 要减去的值
     * @return 减法结果表达式或当前表达式（如果值为null）
     */
    default io.github.nextentity.api.NumberExpression<T, U> subtractIfNotNull(U value) {
        return value == null ? this : subtract(value);
    }

    /**
     * 条件乘法操作，当值不为null时与指定值相乘。
     *
     * @param value 要相乘的值
     * @return 乘法结果表达式或当前表达式（如果值为null）
     */
    default io.github.nextentity.api.NumberExpression<T, U> multiplyIfNotNull(U value) {
        return value == null ? this : multiply(value);
    }

    /**
     * 条件除法操作，当值不为null时除以指定值。
     *
     * @param value 要除以的值
     * @return 除法结果表达式或当前表达式（如果值为null）
     */
    default io.github.nextentity.api.NumberExpression<T, U> divideIfNotNull(U value) {
        return value == null ? this : divide(value);
    }

    /**
     * 条件取模操作，当值不为null时与指定值取模。
     *
     * @param value 要取模的值
     * @return 取模结果表达式或当前表达式（如果值为null）
     */
    default io.github.nextentity.api.NumberExpression<T, U> modIfNotNull(U value) {
        return value == null ? this : mod(value);
    }

}
