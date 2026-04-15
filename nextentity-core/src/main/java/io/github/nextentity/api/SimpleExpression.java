package io.github.nextentity.api;

import io.github.nextentity.api.model.Order;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.List;

/// 简单表达式接口，提供基本的表达式操作方法。
///
/// ## 使用示例
///
/// ```java
/// // 比较操作
/// Predicate<User> p1 = Path.of(User::getAge).gt(18);
/// Predicate<User> p2 = Path.of(User::getName).eq("张三");
///
/// // 范围查询
/// Predicate<User> p3 = Path.of(User::getAge).between(18, 60);
///
/// // IN 查询
/// Predicate<User> p4 = Path.of(User::getId).in(1L, 2L, 3L);
///
/// // 空值判断
/// Predicate<User> p5 = Path.of(User::getEmail).isNotNull();
///
/// // 排序
/// Order<User> order = Path.of(User::getName).asc();
///
/// // 聚合函数
/// NumberExpression<User, Long> count = Path.of(User::getId).count();
/// ```
///
/// @param <T> 实体类型
/// @param <U> 表达式值类型
/// @author HuangChengwei
/// @since 1.0.0
public interface SimpleExpression<T, U> extends Expression<T, U> {

    /// 统计表达式值的数量。
    ///
    /// @return 计数表达式
    NumberExpression<T, Long> count();

    /// 统计不同表达式值的数量。
    ///
    /// @return 不同计数表达式
    NumberExpression<T, Long> countDistinct();

    /// 等于指定值。
    ///
    /// @param value 比较值
    /// @return 断言对象
    Predicate<T> eq(U value);

    /// 如果值不为 null，则等于指定值。
    ///
    /// @param value 比较值
    /// @return 断言对象
    Predicate<T> eqIfNotNull(U value);

    /// 等于另一个表达式的值。
    ///
    /// @param value 另一个表达式
    /// @return 断言对象
    Predicate<T> eq(Expression<T, U> value);

    /// 不等于指定值。
    ///
    /// @param value 比较值
    /// @return 断言对象
    Predicate<T> ne(U value);

    /// 如果值不为 null，则不等于指定值。
    ///
    /// @param value 比较值
    /// @return 断言对象
    Predicate<T> neIfNotNull(U value);

    /// 不等于另一个表达式的值。
    ///
    /// @param value 另一个表达式
    /// @return 断言对象
    Predicate<T> ne(Expression<T, U> value);

    /// 在指定表达式列表的值中。
    ///
    /// @param expressions 表达式列表
    /// @return 断言对象
    Predicate<T> in(@NonNull Expression<T, List<U>> expressions);

    /// 在指定值数组中。
    ///
    /// 注意：空数组（length=0）时，条件恒为 false（相当于 `WHERE 1=0`），
    /// 查询将不返回任何结果。
    ///
    /// @param values 值数组
    /// @return 断言对象
    @SuppressWarnings("unchecked")
    Predicate<T> in(U... values);

    /// 在指定表达式列表的值中。
    ///
    /// @param values 表达式列表
    /// @return 断言对象
    Predicate<T> in(@NonNull List<? extends Expression<T, U>> values);

    /// 在指定集合中。
    ///
    /// 注意：空集合（size=0）时，条件恒为 false（相当于 `WHERE 1=0`），
    /// 查询将不返回任何结果。
    ///
    /// @param values 值集合
    /// @return 断言对象
    Predicate<T> in(@NonNull Collection<? extends U> values);

    /// 不在指定值数组中。
    ///
    /// 注意：空数组（length=0）时，条件恒为 true（相当于 `WHERE 1=1`），
    /// 该过滤条件对所有行都成立，等同于未添加此条件。
    ///
    /// @param values 值数组
    /// @return 断言对象
    @SuppressWarnings("unchecked")
    Predicate<T> notIn(U... values);

    /// 不在指定表达式列表的值中。
    ///
    /// @param values 表达式列表
    /// @return 断言对象
    Predicate<T> notIn(@NonNull List<? extends Expression<T, U>> values);

    /// 不在指定集合中。
    ///
    /// 注意：空集合（size=0）时，条件恒为 true（相当于 `WHERE 1=1`），
    /// 该过滤条件对所有行都成立，等同于未添加此条件。
    ///
    /// @param values 值集合
    /// @return 断言对象
    Predicate<T> notIn(@NonNull Collection<? extends U> values);

    /// 如果集合不为 null，则在指定集合中。
    ///
    /// 注意：集合为 null 时条件跳过，空集合（size=0）时生成 `WHERE 1=0`。
    ///
    /// @param values 值集合，可为 null
    /// @return 断言对象
    default Predicate<T> inIfNotNull(Collection<? extends U> values) {
        return values == null ? Predicate.ofTrue() : in(values);
    }

    /// 如果集合不为 null，则不在指定集合中。
    ///
    /// 注意：集合为 null 时条件跳过，空集合（size=0）时生成 `WHERE 1=1`。
    ///
    /// @param values 值集合，可为 null
    /// @return 断言对象
    default Predicate<T> notInIfNotNull(Collection<? extends U> values) {
        return values == null ? Predicate.ofTrue() : notIn(values);
    }

    /// 值为 null。
    ///
    /// @return 断言对象
    Predicate<T> isNull();

    /// 值不为 null。
    ///
    /// @return 断言对象
    Predicate<T> isNotNull();

    /// 大于等于另一个表达式的值。
    ///
    /// @param expression 另一个表达式
    /// @return 断言对象
    Predicate<T> ge(Expression<T, U> expression);

    /// 大于另一个表达式的值。
    ///
    /// @param expression 另一个表达式
    /// @return 断言对象
    Predicate<T> gt(Expression<T, U> expression);

    /// 小于等于另一个表达式的值。
    ///
    /// @param expression 另一个表达式
    /// @return 断言对象
    Predicate<T> le(Expression<T, U> expression);

    /// 小于另一个表达式的值。
    ///
    /// @param expression 另一个表达式
    /// @return 断言对象
    Predicate<T> lt(Expression<T, U> expression);

    /// 在两个表达式的值之间。
    ///
    /// @param l 左边界表达式
    /// @param r 右边界表达式
    /// @return 断言对象
    Predicate<T> between(Expression<T, U> l, Expression<T, U> r);

    /// 不在两个表达式的值之间。
    ///
    /// @param l 左边界表达式
    /// @param r 右边界表达式
    /// @return 断言对象
    Predicate<T> notBetween(Expression<T, U> l, Expression<T, U> r);

    /// 升序排序。
    ///
    /// @return Order 对象
    default Order<T> asc() {
        return sort(SortOrder.ASC);
    }

    /// 降序排序。
    ///
    /// @return Order 对象
    default Order<T> desc() {
        return sort(SortOrder.DESC);
    }

    /// 按指定的排序方向排序。
    ///
    /// @param order 排序方向
    /// @return Order 对象
    Order<T> sort(SortOrder order);

    /// 大于等于指定值。
    ///
    /// @param value 比较值
    /// @return 断言对象
    Predicate<T> ge(U value);

    /// 大于指定值。
    ///
    /// @param value 比较值
    /// @return 断言对象
    Predicate<T> gt(U value);

    /// 小于等于指定值。
    ///
    /// @param value 比较值
    /// @return 断言对象
    Predicate<T> le(U value);

    /// 小于指定值。
    ///
    /// @param value 比较值
    /// @return 断言对象
    Predicate<T> lt(U value);

    /// 如果值不为 null，则大于等于指定值。
    ///
    /// @param value 比较值
    /// @return 断言对象
    Predicate<T> geIfNotNull(U value);

    /// 如果值不为 null，则大于指定值。
    ///
    /// @param value 比较值
    /// @return 断言对象
    Predicate<T> gtIfNotNull(U value);

    /// 如果值不为 null，则小于等于指定值。
    ///
    /// @param value 比较值
    /// @return 断言对象
    Predicate<T> leIfNotNull(U value);

    /// 如果值不为 null，则小于指定值。
    ///
    /// @param value 比较值
    /// @return 断言对象
    Predicate<T> ltIfNotNull(U value);

    /// 在两个值之间。
    ///
    /// @param l 左边界值
    /// @param r 右边界值
    /// @return 断言对象
    Predicate<T> between(U l, U r);

    /// 不在两个值之间。
    ///
    /// @param l 左边界值
    /// @param r 右边界值
    /// @return 断言对象
    Predicate<T> notBetween(U l, U r);

    /// 在表达式和值之间。
    ///
    /// @param l 左边界表达式
    /// @param r 右边界值
    /// @return 断言对象
    Predicate<T> between(Expression<T, U> l, U r);

    /// 在值和表达式之间。
    ///
    /// @param l 左边界值
    /// @param r 右边界表达式
    /// @return 断言对象
    Predicate<T> between(U l, Expression<T, U> r);

    /// 不在表达式和值之间。
    ///
    /// @param l 左边界表达式
    /// @param r 右边界值
    /// @return 断言对象
    Predicate<T> notBetween(Expression<T, U> l, U r);

    /// 不在值和表达式之间。
    ///
    /// @param l 左边界值
    /// @param r 右边界表达式
    /// @return 断言对象
    Predicate<T> notBetween(U l, Expression<T, U> r);

    /// 获取表达式的最大值。
    ///
    /// @return 最大值表达式
    SimpleExpression<T, U> max();

    /// 获取表达式的最小值。
    ///
    /// @return 最小值表达式
    SimpleExpression<T, U> min();
}