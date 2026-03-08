package io.github.nextentity.api;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

/**
 * 表达式构建器接口，提供构建各种表达式的方法。
 * <p>
 * 用于构建查询条件表达式，支持等于、不等于、大于、小于等操作。
 *
 * @param <T> 实体类型
 * @param <U> 表达式值类型
 * @param <B> 构建器返回类型
 * @author HuangChengwei
 * @since 2026/1/7
 */
public interface ExpressionBuilder<T, U, B> {

    /**
     * 等于指定值。
     *
     * @param value 比较值
     * @return 构建器实例
     */
    B eq(U value);

    /**
     * 当值不为null时等于指定值。
     *
     * @param value 比较值
     * @return 构建器实例
     */
    B eqIfNotNull(U value);

    /**
     * 等于另一个表达式。
     *
     * @param expression 另一个表达式
     * @return 构建器实例
     */
    B eq(TypedExpression<T, U> expression);

    /**
     * 不等于指定值。
     *
     * @param value 比较值
     * @return 构建器实例
     */
    B ne(U value);

    /**
     * 当值不为null时不等于指定值。
     *
     * @param value 比较值
     * @return 构建器实例
     */
    B neIfNotNull(U value);

    /**
     * 不等于另一个表达式。
     *
     * @param expression 另一个表达式
     * @return 构建器实例
     */
    B ne(TypedExpression<T, U> expression);

    /**
     * 在指定值数组中。
     *
     * @param values 值数组
     * @return 构建器实例
     */
    @SuppressWarnings({"unchecked"})
    B in(U... values);

    /**
     * 在指定表达式列表中。
     *
     * @param expressions 表达式列表
     * @return 构建器实例
     */
    B in(@NotNull List<? extends TypedExpression<T, U>> expressions);

    /**
     * 在指定表达式的值列表中。
     *
     * @param expressions 表达式
     * @return 构建器实例
     */
    B in(@NotNull TypedExpression<T, List<U>> expressions);

    /**
     * 在指定值集合中。
     *
     * @param values 值集合
     * @return 构建器实例
     */
    B in(@NotNull Collection<? extends U> values);

    /**
     * 不在指定值数组中。
     *
     * @param values 值数组
     * @return 构建器实例
     */
    @SuppressWarnings({"unchecked"})
    B notIn(U... values);

    /**
     * 不在指定表达式列表中。
     *
     * @param expressions 表达式列表
     * @return 构建器实例
     */
    B notIn(@NotNull List<? extends TypedExpression<T, U>> expressions);

    /**
     * 不在指定值集合中。
     *
     * @param values 值集合
     * @return 构建器实例
     */
    B notIn(@NotNull Collection<? extends U> values);

    /**
     * 为null。
     *
     * @return 构建器实例
     */
    B isNull();

    /**
     * 不为null。
     *
     * @return 构建器实例
     */
    B isNotNull();

    /**
     * 大于等于指定值。
     *
     * @param value 比较值
     * @return 构建器实例
     */
    B ge(U value);

    /**
     * 大于指定值。
     *
     * @param value 比较值
     * @return 构建器实例
     */
    B gt(U value);

    /**
     * 小于等于指定值。
     *
     * @param value 比较值
     * @return 构建器实例
     */
    B le(U value);

    /**
     * 小于指定值。
     *
     * @param value 比较值
     * @return 构建器实例
     */
    B lt(U value);

    /**
     * 当值不为null时大于等于指定值。
     *
     * @param value 比较值
     * @return 构建器实例
     */
    B geIfNotNull(U value);

    /**
     * 当值不为null时大于指定值。
     *
     * @param value 比较值
     * @return 构建器实例
     */
    B gtIfNotNull(U value);

    /**
     * 当值不为null时小于等于指定值。
     *
     * @param value 比较值
     * @return 构建器实例
     */
    B leIfNotNull(U value);

    /**
     * 当值不为null时小于指定值。
     *
     * @param value 比较值
     * @return 构建器实例
     */
    B ltIfNotNull(U value);

    /**
     * 在指定范围内。
     *
     * @param l 左边界值
     * @param r 右边界值
     * @return 构建器实例
     */
    B between(U l, U r);

    /**
     * 不在指定范围内。
     *
     * @param l 左边界值
     * @param r 右边界值
     * @return 构建器实例
     */
    B notBetween(U l, U r);

    /**
     * 大于等于另一个表达式。
     *
     * @param expression 另一个表达式
     * @return 构建器实例
     */
    B ge(TypedExpression<T, U> expression);

    /**
     * 大于另一个表达式。
     *
     * @param expression 另一个表达式
     * @return 构建器实例
     */
    B gt(TypedExpression<T, U> expression);

    /**
     * 小于等于另一个表达式。
     *
     * @param expression 另一个表达式
     * @return 构建器实例
     */
    B le(TypedExpression<T, U> expression);

    /**
     * 小于另一个表达式。
     *
     * @param expression 另一个表达式
     * @return 构建器实例
     */
    B lt(TypedExpression<T, U> expression);

    /**
     * 在两个表达式之间。
     *
     * @param l 左边界表达式
     * @param r 右边界表达式
     * @return 构建器实例
     */
    B between(TypedExpression<T, U> l, TypedExpression<T, U> r);

    /**
     * 在表达式和值之间。
     *
     * @param l 左边界表达式
     * @param r 右边界值
     * @return 构建器实例
     */
    B between(TypedExpression<T, U> l, U r);

    /**
     * 在值和表达式之间。
     *
     * @param l 左边界值
     * @param r 右边界表达式
     * @return 构建器实例
     */
    B between(U l, TypedExpression<T, U> r);

    /**
     * 不在两个表达式之间。
     *
     * @param l 左边界表达式
     * @param r 右边界表达式
     * @return 构建器实例
     */
    B notBetween(TypedExpression<T, U> l, TypedExpression<T, U> r);

    /**
     * 不在表达式和值之间。
     *
     * @param l 左边界表达式
     * @param r 右边界值
     * @return 构建器实例
     */
    B notBetween(TypedExpression<T, U> l, U r);

    /**
     * 不在值和表达式之间。
     *
     * @param l 左边界值
     * @param r 右边界表达式
     * @return 构建器实例
     */
    B notBetween(U l, TypedExpression<T, U> r);

    /**
     * 数字操作符接口，提供数字特有的操作方法。
     *
     * @param <T> 实体类型
     * @param <U> 数字类型
     * @param <B> 构建器返回类型
     */
    interface NumberOperator<T, U extends Number, B> extends ExpressionBuilder<T, U, B> {
        /**
         * 加法操作，与指定值相加。
         *
         * @param value 要相加的值
         * @return 数字操作符实例
         */
        NumberOperator<T, U, B> add(U value);

        /**
         * 减法操作，减去指定值。
         *
         * @param value 要减去的值
         * @return 数字操作符实例
         */
        NumberOperator<T, U, B> subtract(U value);

        /**
         * 乘法操作，与指定值相乘。
         *
         * @param value 要相乘的值
         * @return 数字操作符实例
         */
        NumberOperator<T, U, B> multiply(U value);

        /**
         * 除法操作，除以指定值。
         *
         * @param value 要除以的值
         * @return 数字操作符实例
         */
        NumberOperator<T, U, B> divide(U value);

        /**
         * 取模操作，与指定值取模。
         *
         * @param value 要取模的值
         * @return 数字操作符实例
         */
        NumberOperator<T, U, B> mod(U value);

        /**
         * 条件加法操作，当值不为null时与指定值相加。
         *
         * @param value 要相加的值
         * @return 数字操作符实例
         */
        default NumberOperator<T, U, B> addIfNotNull(U value) {
            return value == null ? this : add(value);
        }

        /**
         * 条件减法操作，当值不为null时减去指定值。
         *
         * @param value 要减去的值
         * @return 数字操作符实例
         */
        default NumberOperator<T, U, B> subtractIfNotNull(U value) {
            return value == null ? this : subtract(value);
        }

        /**
         * 条件乘法操作，当值不为null时与指定值相乘。
         *
         * @param value 要相乘的值
         * @return 数字操作符实例
         */
        default NumberOperator<T, U, B> multiplyIfNotNull(U value) {
            return value == null ? this : multiply(value);
        }

        /**
         * 条件除法操作，当值不为null时除以指定值。
         *
         * @param value 要除以的值
         * @return 数字操作符实例
         */
        default NumberOperator<T, U, B> divideIfNotNull(U value) {
            return value == null ? this : divide(value);
        }

        /**
         * 条件取模操作，当值不为null时与指定值取模。
         *
         * @param value 要取模的值
         * @return 数字操作符实例
         */
        default NumberOperator<T, U, B> modIfNotNull(U value) {
            return value == null ? this : mod(value);
        }

        /**
         * 加法操作，与另一个表达式相加。
         *
         * @param expression 另一个表达式
         * @return 数字操作符实例
         */
        NumberOperator<T, U, B> add(TypedExpression<T, U> expression);

        /**
         * 减法操作，减去另一个表达式。
         *
         * @param expression 另一个表达式
         * @return 数字操作符实例
         */
        NumberOperator<T, U, B> subtract(TypedExpression<T, U> expression);

        /**
         * 乘法操作，与另一个表达式相乘。
         *
         * @param expression 另一个表达式
         * @return 数字操作符实例
         */
        NumberOperator<T, U, B> multiply(TypedExpression<T, U> expression);

        /**
         * 除法操作，除以另一个表达式。
         *
         * @param expression 另一个表达式
         * @return 数字操作符实例
         */
        NumberOperator<T, U, B> divide(TypedExpression<T, U> expression);

        /**
         * 取模操作，与另一个表达式取模。
         *
         * @param expression 另一个表达式
         * @return 数字操作符实例
         */
        NumberOperator<T, U, B> mod(TypedExpression<T, U> expression);

    }

    /**
     * 路径操作符接口，提供路径相关的操作方法。
     *
     * @param <T> 实体类型
     * @param <U> 路径值类型
     * @param <B> 构建器返回类型
     */
    interface PathOperator<T, U, B> extends ExpressionBuilder<T, U, B> {

        /**
         * 获取指定路径的路径操作符。
         *
         * @param path 路径
         * @param <V> 路径值类型
         * @return 路径操作符实例
         */
        <V> PathOperator<T, V, B> get(Path<U, V> path);

        /**
         * 获取指定字符串路径的字符串操作符。
         *
         * @param path 字符串路径
         * @return 字符串操作符实例
         */
        StringOperator<T, B> get(Path.StringRef<U> path);

        /**
         * 获取指定数字路径的数字操作符。
         *
         * @param path 数字路径
         * @param <V> 数字类型
         * @return 数字操作符实例
         */
        <V extends Number> NumberOperator<T, V, B> get(Path.NumberRef<U, V> path);

    }

    /**
     * 字符串操作符接口，提供字符串特有的操作方法。
     *
     * @param <T> 实体类型
     * @param <B> 构建器返回类型
     */
    // TODO 未添加测试用例
    interface StringOperator<T, B> extends ExpressionBuilder<T, String, B> {

        /**
         * 当字符串不为空时等于指定值。
         *
         * @param value 比较值
         * @return 字符串操作符实例
         */
        B eqIfNotEmpty(String value);

        /**
         * 模糊匹配指定值。
         *
         * @param value 匹配值
         * @return 字符串操作符实例
         */
        B like(String value);

        /**
         * 以指定值开头。
         *
         * @param value 开头值
         * @return 字符串操作符实例
         */
        default B startWith(String value) {
            return like(value + '%');
        }

        /**
         * 以指定值结尾。
         *
         * @param value 结尾值
         * @return 字符串操作符实例
         */
        default B endsWith(String value) {
            return like('%' + value);
        }

        /**
         * 包含指定值。
         *
         * @param value 包含值
         * @return 字符串操作符实例
         */
        default B contains(String value) {
            return like('%' + value + '%');
        }

        /**
         * 不模糊匹配指定值。
         *
         * @param value 匹配值
         * @return 字符串操作符实例
         */
        B notLike(String value);

        /**
         * 不以指定值开头。
         *
         * @param value 开头值
         * @return 字符串操作符实例
         */
        default B notStartWith(String value) {
            return notLike(value + '%');
        }

        /**
         * 不以指定值结尾。
         *
         * @param value 结尾值
         * @return 字符串操作符实例
         */
        default B notEndsWith(String value) {
            return notLike('%' + value);
        }

        /**
         * 不包含指定值。
         *
         * @param value 包含值
         * @return 字符串操作符实例
         */
        default B notContains(String value) {
            return notLike('%' + value + '%');
        }

        /**
         * 当值不为null时模糊匹配指定值。
         *
         * @param value 匹配值
         * @return 字符串操作符实例
         */
        B likeIfNotNull(String value);

        /**
         * 当值不为null时以指定值开头。
         *
         * @param value 开头值
         * @return 字符串操作符实例
         */
        default B startWithIfNotNull(String value) {
            return likeIfNotNull(value == null ? null : value + '%');
        }

        /**
         * 当值不为null时以指定值结尾。
         *
         * @param value 结尾值
         * @return 字符串操作符实例
         */
        default B endsWithIfNotNull(String value) {
            return likeIfNotNull(value == null ? null : '%' + value);
        }

        /**
         * 当值不为null时包含指定值。
         *
         * @param value 包含值
         * @return 字符串操作符实例
         */
        default B containsIfNotNull(String value) {
            return likeIfNotNull(value == null ? null : '%' + value + '%');
        }

        /**
         * 当值不为null时不模糊匹配指定值。
         *
         * @param value 匹配值
         * @return 字符串操作符实例
         */
        B notLikeIfNotNull(String value);

        /**
         * 当值不为null时不以指定值开头。
         *
         * @param value 开头值
         * @return 字符串操作符实例
         */
        default B notStartWithIfNotNull(String value) {
            return notLikeIfNotNull(value == null ? null : value + '%');
        }

        /**
         * 当值不为null时不以指定值结尾。
         *
         * @param value 结尾值
         * @return 字符串操作符实例
         */
        default B notEndsWithIfNotNull(String value) {
            return notLikeIfNotNull(value == null ? null : '%' + value);
        }

        /**
         * 当值不为null时不包含指定值。
         *
         * @param value 包含值
         * @return 字符串操作符实例
         */
        default B notContainsIfNotNull(String value) {
            return notLikeIfNotNull(value == null || value.isEmpty() ? null : '%' + value + '%');
        }

        /**
         * 当字符串不为空时模糊匹配指定值。
         *
         * @param value 匹配值
         * @return 字符串操作符实例
         */
        default B likeIfNotEmpty(String value) {
            return value == null || value.isEmpty() ? likeIfNotNull(null) : like(value);
        }

        /**
         * 当字符串不为空时以指定值开头。
         *
         * @param value 开头值
         * @return 字符串操作符实例
         */
        default B startWithIfNotEmpty(String value) {
            return likeIfNotEmpty(value == null || value.isEmpty() ? null : value + '%');
        }

        /**
         * 当字符串不为空时以指定值结尾。
         *
         * @param value 结尾值
         * @return 字符串操作符实例
         */
        default B endsWithIfNotEmpty(String value) {
            return likeIfNotEmpty(value == null || value.isEmpty() ? null : '%' + value);
        }

        /**
         * 当字符串不为空时包含指定值。
         *
         * @param value 包含值
         * @return 字符串操作符实例
         */
        default B containsIfNotEmpty(String value) {
            return likeIfNotEmpty(value == null || value.isEmpty() ? null : '%' + value + '%');
        }

        /**
         * 当字符串不为空时不模糊匹配指定值。
         *
         * @param value 匹配值
         * @return 字符串操作符实例
         */
        B notLikeIfNotEmpty(String value);

        /**
         * 当字符串不为空时不以指定值开头。
         *
         * @param value 开头值
         * @return 字符串操作符实例
         */
        default B notStartWithIfNotEmpty(String value) {
            return notLikeIfNotEmpty(value == null || value.isEmpty() ? null : value + '%');
        }

        /**
         * 当字符串不为空时不以指定值结尾。
         *
         * @param value 结尾值
         * @return 字符串操作符实例
         */
        default B notEndsWithIfNotEmpty(String value) {
            return notLikeIfNotEmpty(value == null || value.isEmpty() ? null : '%' + value);
        }

        /**
         * 当字符串不为空时不包含指定值。
         *
         * @param value 包含值
         * @return 字符串操作符实例
         */
        default B notContainsIfNotEmpty(String value) {
            return notLikeIfNotNull(value == null || value.isEmpty() ? null : '%' + value + '%');
        }

        /**
         * 转换为小写。
         *
         * @return 字符串操作符实例
         */
        StringOperator<T, B> lower();

        /**
         * 转换为大写。
         *
         * @return 字符串操作符实例
         */
        StringOperator<T, B> upper();

        /**
         * 截取子字符串。
         *
         * @param offset 偏移量
         * @param length 长度
         * @return 字符串操作符实例
         */
        StringOperator<T, B> substring(int offset, int length);

        /**
         * 截取子字符串，从指定偏移量开始到结束。
         *
         * @param offset 偏移量
         * @return 字符串操作符实例
         */
        default StringOperator<T, B> substring(int offset) {
            return substring(offset, Integer.MAX_VALUE);
        }

        /**
         * 去除首尾空格。
         *
         * @return 字符串操作符实例
         */
        StringOperator<T, B> trim();

        /**
         * 获取字符串长度。
         *
         * @return 数字操作符实例
         */
        NumberOperator<T, Integer, B> length();

    }

    /**
     * 连接操作符接口，提供逻辑与操作。
     *
     * @param <T> 实体类型
     */
    interface Conjunction<T> extends TypedExpression<T, Boolean> {

        /**
         * 与指定路径的路径操作符进行连接。
         *
         * @param path 路径
         * @param <R> 路径值类型
         * @return 路径操作符实例
         */
        <R> PathOperator<T, R, Conjunction<T>> and(Path<T, R> path);

        /**
         * 与指定数字路径的数字操作符进行连接。
         *
         * @param path 数字路径
         * @param <R> 数字类型
         * @return 数字操作符实例
         */
        <R extends Number> NumberOperator<T, R, Conjunction<T>> and(Path.NumberRef<T, R> path);

        /**
         * 与指定字符串路径的字符串操作符进行连接。
         *
         * @param path 字符串路径
         * @return 字符串操作符实例
         */
        StringOperator<T, Conjunction<T>> and(Path.StringRef<T> path);

        /**
         * 与另一个表达式进行连接。
         *
         * @param expression 另一个表达式
         * @return 连接操作符实例
         */
        Conjunction<T> and(TypedExpression<T, Boolean> expression);

        /**
         * 与多个表达式进行连接。
         *
         * @param expressions 表达式集合
         * @return 连接操作符实例
         */
        Conjunction<T> and(Iterable<? extends TypedExpression<T, Boolean>> expressions);

        /**
         * 转换为谓词。
         *
         * @return 谓词实例
         */
        Predicate<T> toPredicate();

    }

    /**
     * 析取操作符接口，提供逻辑或操作。
     *
     * @param <T> 实体类型
     */
    interface Disjunction<T> extends TypedExpression<T, Boolean> {

        /**
         * 与指定路径的路径操作符进行析取。
         *
         * @param path 路径
         * @param <N> 路径值类型
         * @return 路径操作符实例
         */
        <N> PathOperator<T, N, Disjunction<T>> or(Path<T, N> path);

        /**
         * 与指定数字路径的数字操作符进行析取。
         *
         * @param path 数字路径
         * @param <N> 数字类型
         * @return 数字操作符实例
         */
        <N extends Number> NumberOperator<T, N, Disjunction<T>> or(Path.NumberRef<T, N> path);

        /**
         * 与指定字符串路径的字符串操作符进行析取。
         *
         * @param path 字符串路径
         * @return 字符串操作符实例
         */
        StringOperator<T, ? extends Disjunction<T>> or(Path.StringRef<T> path);

        /**
         * 与另一个表达式进行析取。
         *
         * @param predicate 另一个表达式
         * @return 析取操作符实例
         */
        Disjunction<T> or(TypedExpression<T, Boolean> predicate);

        /**
         * 与多个表达式进行析取。
         *
         * @param expressions 表达式集合
         * @return 析取操作符实例
         */
        Disjunction<T> or(Iterable<? extends TypedExpression<T, Boolean>> expressions);

        /**
         * 转换为谓词。
         *
         * @return 谓词实例
         */
        Predicate<T> toPredicate();

    }
}
