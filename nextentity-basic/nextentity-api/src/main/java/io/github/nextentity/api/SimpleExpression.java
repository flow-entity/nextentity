package io.github.nextentity.api;

import io.github.nextentity.api.model.EntityRoot;
import io.github.nextentity.api.model.Order;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

/**
 * 简单表达式接口，提供基本的表达式操作方法。
 *
 * @param <T> 实体类型
 * @param <U> 表达式值类型
 * @author HuangChengwei
 * @since 2026/1/7
 */
public interface SimpleExpression<T, U> extends TypedExpression<T, U> {

    /**
     * 获取实体根对象。
     *
     * @return 实体根对象
     */
    EntityRoot<T> root();

    /**
     * 计算表达式值的数量。
     *
     * @return 计数表达式
     */
    NumberExpression<T, Long> count();

    /**
     * 计算表达式值的不同数量。
     *
     * @return 不同计数表达式
     */
    NumberExpression<T, Long> countDistinct();

    /**
     * 等于指定值。
     *
     * @param value 比较值
     * @return 谓词对象
     */
    Predicate<T> eq(U value);

    /**
     * 如果值不为null，则等于指定值。
     *
     * @param value 比较值
     * @return 谓词对象
     */
    Predicate<T> eqIfNotNull(U value);

    /**
     * 等于另一个表达式的值。
     *
     * @param value 另一个表达式
     * @return 谓词对象
     */
    Predicate<T> eq(TypedExpression<T, U> value);

    /**
     * 不等于指定值。
     *
     * @param value 比较值
     * @return 谓词对象
     */
    Predicate<T> ne(U value);

    /**
     * 如果值不为null，则不等于指定值。
     *
     * @param value 比较值
     * @return 谓词对象
     */
    Predicate<T> neIfNotNull(U value);

    /**
     * 不等于另一个表达式的值。
     *
     * @param value 另一个表达式
     * @return 谓词对象
     */
    Predicate<T> ne(TypedExpression<T, U> value);

    /**
     * 在指定表达式列表的值中。
     *
     * @param expressions 表达式列表
     * @return 谓词对象
     */
    Predicate<T> in(@NotNull TypedExpression<T, List<U>> expressions);

    /**
     * 在指定值数组中。
     *
     * @param values 值数组
     * @return 谓词对象
     */
    @SuppressWarnings("unchecked")
    Predicate<T> in(U... values);

    /**
     * 在指定表达式列表的值中。
     *
     * @param values 表达式列表
     * @return 谓词对象
     */
    Predicate<T> in(@NotNull List<? extends TypedExpression<T, U>> values);

    /**
     * 在指定集合中。
     *
     * @param values 值集合
     * @return 谓词对象
     */
    Predicate<T> in(@NotNull Collection<? extends U> values);

    /**
     * 不在指定值数组中。
     *
     * @param values 值数组
     * @return 谓词对象
     */
    @SuppressWarnings("unchecked")
    Predicate<T> notIn(U... values);

    /**
     * 不在指定表达式列表的值中。
     *
     * @param values 表达式列表
     * @return 谓词对象
     */
    Predicate<T> notIn(@NotNull List<? extends TypedExpression<T, U>> values);

    /**
     * 不在指定集合中。
     *
     * @param values 值集合
     * @return 谓词对象
     */
    Predicate<T> notIn(@NotNull Collection<? extends U> values);

    /**
     * 值为null。
     *
     * @return 谓词对象
     */
    Predicate<T> isNull();

    /**
     * 值不为null。
     *
     * @return 谓词对象
     */
    Predicate<T> isNotNull();

    /**
     * 大于等于另一个表达式的值。
     *
     * @param expression 另一个表达式
     * @return 谓词对象
     */
    Predicate<T> ge(TypedExpression<T, U> expression);

    /**
     * 大于另一个表达式的值。
     *
     * @param expression 另一个表达式
     * @return 谓词对象
     */
    Predicate<T> gt(TypedExpression<T, U> expression);

    /**
     * 小于等于另一个表达式的值。
     *
     * @param expression 另一个表达式
     * @return 谓词对象
     */
    Predicate<T> le(TypedExpression<T, U> expression);

    /**
     * 小于另一个表达式的值。
     *
     * @param expression 另一个表达式
     * @return 谓词对象
     */
    Predicate<T> lt(TypedExpression<T, U> expression);

    /**
     * 在两个表达式的值之间。
     *
     * @param l 左边界表达式
     * @param r 右边界表达式
     * @return 谓词对象
     */
    Predicate<T> between(TypedExpression<T, U> l, TypedExpression<T, U> r);

    /**
     * 不在两个表达式的值之间。
     *
     * @param l 左边界表达式
     * @param r 右边界表达式
     * @return 谓词对象
     */
    Predicate<T> notBetween(TypedExpression<T, U> l, TypedExpression<T, U> r);

    /**
     * 按升序排序。
     *
     * @return 排序对象
     */
    default Order<T> asc() {
        return sort(SortOrder.ASC);
    }

    /**
     * 按降序排序。
     *
     * @return 排序对象
     */
    default Order<T> desc() {
        return sort(SortOrder.DESC);
    }

    /**
     * 按指定排序方向排序。
     *
     * @param order 排序方向
     * @return 排序对象
     */
    Order<T> sort(SortOrder order);

    /**
     * 大于等于指定值。
     *
     * @param value 比较值
     * @return 谓词对象
     */
    default Predicate<T> ge(U value) {
        return ge(root().literal(value));
    }

    /**
     * 大于指定值。
     *
     * @param value 比较值
     * @return 谓词对象
     */
    default Predicate<T> gt(U value) {
        return gt(root().literal(value));
    }

    /**
     * 小于等于指定值。
     *
     * @param value 比较值
     * @return 谓词对象
     */
    default Predicate<T> le(U value) {
        return le(root().literal(value));
    }

    /**
     * 小于指定值。
     *
     * @param value 比较值
     * @return 谓词对象
     */
    default Predicate<T> lt(U value) {
        return lt(root().literal(value));
    }

    /**
     * 如果值不为null，则大于等于指定值。
     *
     * @param value 比较值
     * @return 谓词对象
     */
    Predicate<T> geIfNotNull(U value);

    /**
     * 如果值不为null，则大于指定值。
     *
     * @param value 比较值
     * @return 谓词对象
     */
    Predicate<T> gtIfNotNull(U value);

    /**
     * 如果值不为null，则小于等于指定值。
     *
     * @param value 比较值
     * @return 谓词对象
     */
    Predicate<T> leIfNotNull(U value);

    /**
     * 如果值不为null，则小于指定值。
     *
     * @param value 比较值
     * @return 谓词对象
     */
    Predicate<T> ltIfNotNull(U value);

    /**
     * 在两个值之间。
     *
     * @param l 左边界值
     * @param r 右边界值
     * @return 谓词对象
     */
    default Predicate<T> between(U l, U r) {
        EntityRoot<T> eb = root();
        return between(eb.literal(l), eb.literal(r));
    }

    /**
     * 不在两个值之间。
     *
     * @param l 左边界值
     * @param r 右边界值
     * @return 谓词对象
     */
    default Predicate<T> notBetween(U l, U r) {
        EntityRoot<T> eb = root();
        return notBetween(eb.literal(l), eb.literal(r));
    }

    /**
     * 在表达式和值之间。
     *
     * @param l 左边界表达式
     * @param r 右边界值
     * @return 谓词对象
     */
    default Predicate<T> between(TypedExpression<T, U> l, U r) {
        return between(l, root().literal(r));
    }

    /**
     * 在值和表达式之间。
     *
     * @param l 左边界值
     * @param r 右边界表达式
     * @return 谓词对象
     */
    default Predicate<T> between(U l, TypedExpression<T, U> r) {
        return between(root().literal(l), r);
    }

    /**
     * 不在表达式和值之间。
     *
     * @param l 左边界表达式
     * @param r 右边界值
     * @return 谓词对象
     */
    default Predicate<T> notBetween(TypedExpression<T, U> l, U r) {
        return notBetween(l, root().literal(r));
    }

    /**
     * 不在值和表达式之间。
     *
     * @param l 左边界值
     * @param r 右边界表达式
     * @return 谓词对象
     */
    default Predicate<T> notBetween(U l, TypedExpression<T, U> r) {
        return notBetween(root().literal(l), r);
    }

}
