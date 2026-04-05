package io.github.nextentity.api;

/// 数值表达式接口，提供数值类型表达式的操作方法。
///
/// 继承 SimpleExpression，提供基本的表达式操作方法，并添加数值特有的操作。
///
/// ## 使用示例
///
/// ```java
/// // 算术运算
/// NumberExpression<User, Integer> salary = Path.of(User::getSalary);
/// NumberExpression<User, Integer> annualSalary = salary.multiply(12);
/// NumberExpression<User, Integer> withBonus = salary.add(bonus);
///
/// // 聚合函数
/// NumberExpression<User, Long> count = Path.of(User::getId).count();
/// NumberExpression<User, Double> avgSalary = Path.of(User::getSalary).avg();
/// NumberExpression<User, BigDecimal> maxSalary = Path.of(User::getSalary).max();
///
/// // 比较运算
/// Predicate<User> p1 = Path.of(User::getAge).gt(18);
/// Predicate<User> p2 = Path.of(User::getSalary).between(5000, 10000);
///
/// // 条件运算
/// Predicate<User> p3 = Path.of(User::getSalary).multiplyIfNotNull(bonusRate).gt(100000);
/// ```
///
/// @param <T> 实体类型
/// @param <U> 数值类型
/// @author HuangChengwei
/// @since 1.0.0
public interface NumberExpression<T, U extends Number> extends SimpleExpression<T, U> {
    /// 加法操作，加上另一个表达式的值。
    ///
    /// @param expression 另一个表达式
    /// @return 加法结果表达式
    NumberExpression<T, U> add(Expression<T, U> expression);

    /// 减法操作，减去另一个表达式的值。
    ///
    /// @param expression 另一个表达式
    /// @return 减法结果表达式
    NumberExpression<T, U> subtract(Expression<T, U> expression);

    /// 乘法操作，乘以另一个表达式的值。
    ///
    /// @param expression 另一个表达式
    /// @return 乘法结果表达式
    NumberExpression<T, U> multiply(Expression<T, U> expression);

    /// 除法操作，除以另一个表达式的值。
    ///
    /// @param expression 另一个表达式
    /// @return 除法结果表达式
    NumberExpression<T, U> divide(Expression<T, U> expression);

    /// 取模操作，对另一个表达式的值取模。
    ///
    /// @param expression 另一个表达式
    /// @return 取模结果表达式
    NumberExpression<T, U> mod(Expression<T, U> expression);

    /// 求和操作。
    ///
    /// @return 求和结果表达式
    NumberExpression<T, U> sum();

    /// 求平均值操作。
    ///
    /// @return 平均值结果表达式
    NumberExpression<T, Double> avg();

    /// 求最大值操作。
    ///
    /// @return 最大值结果表达式
    NumberExpression<T, U> max();

    /// 求最小值操作。
    ///
    /// @return 最小值结果表达式
    NumberExpression<T, U> min();

    /// 加法操作，加上指定值。
    ///
    /// @param value 要加的值
    /// @return 加法结果表达式
    default NumberExpression<T, U> add(U value) {
        return add(root().literal(value));
    }

    /// 减法操作，减去指定值。
    ///
    /// @param value 要减的值
    /// @return 减法结果表达式
    default NumberExpression<T, U> subtract(U value) {
        return subtract(root().literal(value));
    }

    /// 乘法操作，乘以指定值。
    ///
    /// @param value 要乘的值
    /// @return 乘法结果表达式
    default NumberExpression<T, U> multiply(U value) {
        return multiply(root().literal(value));
    }

    /// 除法操作，除以指定值。
    ///
    /// @param value 要除的值
    /// @return 除法结果表达式
    default NumberExpression<T, U> divide(U value) {
        return divide(root().literal(value));
    }

    /// 取模操作，对指定值取模。
    ///
    /// @param value 取模的值
    /// @return 取模结果表达式
    default NumberExpression<T, U> mod(U value) {
        return mod(root().literal(value));
    }

    /// 条件加法操作，如果值不为 null 则加上指定值。
    ///
    /// @param value 要加的值
    /// @return 加法结果表达式，如果值为 null 则返回当前表达式
    default NumberExpression<T, U> addIfNotNull(U value) {
        return value == null ? this : add(value);
    }

    /// 条件减法操作，如果值不为 null 则减去指定值。
    ///
    /// @param value 要减的值
    /// @return 减法结果表达式，如果值为 null 则返回当前表达式
    default NumberExpression<T, U> subtractIfNotNull(U value) {
        return value == null ? this : subtract(value);
    }

    /// 条件乘法操作，如果值不为 null 则乘以指定值。
    ///
    /// @param value 要乘的值
    /// @return 乘法结果表达式，如果值为 null 则返回当前表达式
    default NumberExpression<T, U> multiplyIfNotNull(U value) {
        return value == null ? this : multiply(value);
    }

    /// 条件除法操作，如果值不为 null 则除以指定值。
    ///
    /// @param value 要除的值
    /// @return 除法结果表达式，如果值为 null 则返回当前表达式
    default NumberExpression<T, U> divideIfNotNull(U value) {
        return value == null ? this : divide(value);
    }

    /// 条件取模操作，如果值不为 null 则对指定值取模。
    ///
    /// @param value 取模的值
    /// @return 取模结果表达式，如果值为 null 则返回当前表达式
    default NumberExpression<T, U> modIfNotNull(U value) {
        return value == null ? this : mod(value);
    }

    private EntityRoot<T> root() {
        return EntityRoot.of();
    }
}