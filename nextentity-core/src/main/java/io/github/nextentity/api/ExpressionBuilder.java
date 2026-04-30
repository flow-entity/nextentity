package io.github.nextentity.api;

import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.List;

/// 表达式构建器接口，提供构建各种表达式的方法。
///
/// 用于构建查询条件表达式，支持等于、不等于、大于、小于等操作。
///
/// ## 基本用法
///
/// ```java
/// // 等于操作
/// repository.query()
///     .where(User::getName).eq("张三")
///     .list();
///
/// // 条件操作（仅当值不为 null 时才添加条件）
/// repository.query()
///     .where(User::getAge).gtIfNotNull(minAge)
///     .where(User::getStatus).eqIfNotNull(status)
///     .list();
///
/// // 范围查询
/// repository.query()
///     .where(User::getAge).between(18, 60)
///     .list();
///
/// // IN 查询
/// repository.query()
///     .where(User::getId).in(1L, 2L, 3L)
///     .list();
///
/// // 空值判断
/// repository.query()
///     .where(User::getEmail).isNotNull()
///     .list();
/// ```
///
/// @param <T> 实体类型
/// @param <U> 表达式值类型
/// @param <B> 构建器返回类型
/// @author HuangChengwei
/// @since 1.0.0
public interface ExpressionBuilder<T, U, B> {

    /// 等于指定值。
    ///
    /// @param value 比较值
    /// @return 构建器实例
    B eq(U value);

    /// 如果值不为 null，则等于指定值。
    ///
    /// 适用于可选参数的场景：
    /// ```java
    /// public List<User> search(String name) {
    ///     return repository.query()
    ///         .where(User::getName).eqIfNotNull(name)
    ///         .list();
    /// }
    /// ```
    ///
    /// @param value 比较值
    /// @return 构建器实例
    B eqIfNotNull(U value);

    /// 等于另一个表达式的值。
    ///
    /// @param expression 另一个表达式
    /// @return 构建器实例
    B eq(Expression<T, U> expression);

    /// 不等于指定值。
    ///
    /// @param value 比较值
    /// @return 构建器实例
    B ne(U value);

    /// 如果值不为 null，则不等于指定值。
    ///
    /// @param value 比较值
    /// @return 构建器实例
    B neIfNotNull(U value);

    /// 不等于另一个表达式的值。
    ///
    /// @param expression 另一个表达式
    /// @return 构建器实例
    B ne(Expression<T, U> expression);

    /// 在指定值数组中。
    ///
    /// 示例：
    /// ```java
    /// .where(User::getId).in(1L, 2L, 3L)
    /// ```
    ///
    /// 注意：空数组（length=0）时，条件恒为 false（相当于 `WHERE 1=0`），
    /// 查询将不返回任何结果。
    ///
    /// @param values 值数组
    /// @return 构建器实例
    @SuppressWarnings({"unchecked"})
    B in(U... values);

    /// 在指定表达式列表的值中。
    ///
    /// @param expressions 表达式列表
    /// @return 构建器实例
    B in(@NonNull List<? extends Expression<T, U>> expressions);

    /// 在指定表达式的值列表中。
    ///
    /// @param expressions 表达式
    /// @return 构建器实例
    B in(@NonNull Expression<T, List<U>> expressions);

    /// 在指定值集合中。
    ///
    /// 注意：空集合（size=0）时，条件恒为 false（相当于 `WHERE 1=0`），
    /// 查询将不返回任何结果。
    ///
    /// @param values 值集合
    /// @return 构建器实例
    B in(@NonNull Collection<? extends U> values);

    /// 不在指定值数组中。
    ///
    /// 注意：空数组（length=0）时，条件恒为 true（相当于 `WHERE 1=1`），
    /// 该过滤条件对所有行都成立，等同于未添加此条件。
    ///
    /// @param values 值数组
    /// @return 构建器实例
    @SuppressWarnings({"unchecked"})
    B notIn(U... values);

    /// 不在指定表达式列表的值中。
    ///
    /// @param expressions 表达式列表
    /// @return 构建器实例
    B notIn(@NonNull List<? extends Expression<T, U>> expressions);

    /// 不在指定值集合中。
    ///
    /// 注意：空集合（size=0）时，条件恒为 true（相当于 `WHERE 1=1`），
    /// 该过滤条件对所有行都成立，等同于未添加此条件。
    ///
    /// @param values 值集合
    /// @return 构建器实例
    B notIn(@NonNull Collection<? extends U> values);

    /// 如果集合不为 null，则在指定值集合中。
    ///
    /// 适用于可选列表参数的场景：
    /// ```java
    /// public List<User> search(List<Long> ids) {
    ///     return repository.query()
    ///         .where(User::getId).inIfNotNull(ids)
    ///         .list();
    /// }
    /// ```
    ///
    /// 注意：集合为 null 时条件跳过，空集合（size=0）时生成 `WHERE 1=0`。
    ///
    /// @param values 值集合，可为 null
    /// @return 构建器实例
    B inIfNotNull(Collection<? extends U> values);

    /// 如果集合不为 null，则不在指定值集合中。
    ///
    /// 注意：集合为 null 时条件跳过，空集合（size=0）时生成 `WHERE 1=1`。
    ///
    /// @param values 值集合，可为 null
    /// @return 构建器实例
    B notInIfNotNull(Collection<? extends U> values);

    /// 值为 null。
    ///
    /// @return 构建器实例
    B isNull();

    /// 值不为 null。
    ///
    /// @return 构建器实例
    B isNotNull();

    /// 大于等于指定值。
    ///
    /// @param value 比较值
    /// @return 构建器实例
    B ge(U value);

    /// 大于指定值。
    ///
    /// @param value 比较值
    /// @return 构建器实例
    B gt(U value);

    /// 小于等于指定值。
    ///
    /// @param value 比较值
    /// @return 构建器实例
    B le(U value);

    /// 小于指定值。
    ///
    /// @param value 比较值
    /// @return 构建器实例
    B lt(U value);

    /// 如果值不为 null，则大于等于指定值。
    ///
    /// @param value 比较值
    /// @return 构建器实例
    B geIfNotNull(U value);

    /// 如果值不为 null，则大于指定值。
    ///
    /// @param value 比较值
    /// @return 构建器实例
    B gtIfNotNull(U value);

    /// 如果值不为 null，则小于等于指定值。
    ///
    /// @param value 比较值
    /// @return 构建器实例
    B leIfNotNull(U value);

    /// 如果值不为 null，则小于指定值。
    ///
    /// @param value 比较值
    /// @return 构建器实例
    B ltIfNotNull(U value);

    /// 在指定范围内。
    ///
    /// @param l 左边界值
    /// @param r 右边界值
    /// @return 构建器实例
    B between(U l, U r);

    /// 不在指定范围内。
    ///
    /// @param l 左边界值
    /// @param r 右边界值
    /// @return 构建器实例
    B notBetween(U l, U r);

    /// 大于等于另一个表达式的值。
    ///
    /// @param expression 另一个表达式
    /// @return 构建器实例
    B ge(Expression<T, U> expression);

    /// 大于另一个表达式的值。
    ///
    /// @param expression 另一个表达式
    /// @return 构建器实例
    B gt(Expression<T, U> expression);

    /// 小于等于另一个表达式的值。
    ///
    /// @param expression 另一个表达式
    /// @return 构建器实例
    B le(Expression<T, U> expression);

    /// 小于另一个表达式的值。
    ///
    /// @param expression 另一个表达式
    /// @return 构建器实例
    B lt(Expression<T, U> expression);

    /// 在两个表达式之间。
    ///
    /// @param l 左边界表达式
    /// @param r 右边界表达式
    /// @return 构建器实例
    B between(Expression<T, U> l, Expression<T, U> r);

    /// 在表达式和值之间。
    ///
    /// @param l 左边界表达式
    /// @param r 右边界值
    /// @return 构建器实例
    B between(Expression<T, U> l, U r);

    /// 在值和表达式之间。
    ///
    /// @param l 左边界值
    /// @param r 右边界表达式
    /// @return 构建器实例
    B between(U l, Expression<T, U> r);

    /// 不在两个表达式之间。
    ///
    /// @param l 左边界表达式
    /// @param r 右边界表达式
    /// @return 构建器实例
    B notBetween(Expression<T, U> l, Expression<T, U> r);

    /// 不在表达式和值之间。
    ///
    /// @param l 左边界表达式
    /// @param r 右边界值
    /// @return 构建器实例
    B notBetween(Expression<T, U> l, U r);

    /// 不在值和表达式之间。
    ///
    /// @param l 左边界值
    /// @param r 右边界表达式
    /// @return 构建器实例
    B notBetween(U l, Expression<T, U> r);

    /// 数值操作器接口，提供数值特有的操作方法。
    ///
    /// 示例：
    /// ```java
    /// // 算术运算
    /// .where(User::getAge).add(10).gt(30)  // age + 10 > 30
    ///
    /// // 条件运算
    /// .where(User::getSalary).multiplyIfNotNull(bonusRate)
    /// ```
    ///
    /// @param <T> 实体类型
    /// @param <U> 数值类型
    /// @param <B> 构建器返回类型
    interface NumberOperator<T, U extends Number, B> extends ExpressionBuilder<T, U, B> {
        /// 加法操作，加上指定值。
        ///
        /// @param value 要加的值
        /// @return 数值操作器实例
        NumberOperator<T, U, B> add(U value);

        /// 减法操作，减去指定值。
        ///
        /// @param value 要减的值
        /// @return 数值操作器实例
        NumberOperator<T, U, B> subtract(U value);

        /// 乘法操作，乘以指定值。
        ///
        /// @param value 要乘的值
        /// @return 数值操作器实例
        NumberOperator<T, U, B> multiply(U value);

        /// 除法操作，除以指定值。
        ///
        /// @param value 要除的值
        /// @return 数值操作器实例
        NumberOperator<T, U, B> divide(U value);

        /// 取模操作，对指定值取模。
        ///
        /// @param value 取模的值
        /// @return 数值操作器实例
        NumberOperator<T, U, B> mod(U value);

        /// 条件加法操作，如果值不为 null 则加上指定值。
        ///
        /// @param value 要加的值
        /// @return 数值操作器实例
        default NumberOperator<T, U, B> addIfNotNull(U value) {
            return value == null ? this : add(value);
        }

        /// 条件减法操作，如果值不为 null 则减去指定值。
        ///
        /// @param value 要减的值
        /// @return 数值操作器实例
        default NumberOperator<T, U, B> subtractIfNotNull(U value) {
            return value == null ? this : subtract(value);
        }

        /// 条件乘法操作，如果值不为 null 则乘以指定值。
        ///
        /// @param value 要乘的值
        /// @return 数值操作器实例
        default NumberOperator<T, U, B> multiplyIfNotNull(U value) {
            return value == null ? this : multiply(value);
        }

        /// 条件除法操作，如果值不为 null 则除以指定值。
        ///
        /// @param value 要除的值
        /// @return 数值操作器实例
        default NumberOperator<T, U, B> divideIfNotNull(U value) {
            return value == null ? this : divide(value);
        }

        /// 条件取模操作，如果值不为 null 则对指定值取模。
        ///
        /// @param value 取模的值
        /// @return 数值操作器实例
        default NumberOperator<T, U, B> modIfNotNull(U value) {
            return value == null ? this : mod(value);
        }

        /// 加法操作，加上另一个表达式的值。
        ///
        /// @param expression 另一个表达式
        /// @return 数值操作器实例
        NumberOperator<T, U, B> add(Expression<T, U> expression);

        /// 减法操作，减去另一个表达式的值。
        ///
        /// @param expression 另一个表达式
        /// @return 数值操作器实例
        NumberOperator<T, U, B> subtract(Expression<T, U> expression);

        /// 乘法操作，乘以另一个表达式的值。
        ///
        /// @param expression 另一个表达式
        /// @return 数值操作器实例
        NumberOperator<T, U, B> multiply(Expression<T, U> expression);

        /// 除法操作，除以另一个表达式的值。
        ///
        /// @param expression 另一个表达式
        /// @return 数值操作器实例
        NumberOperator<T, U, B> divide(Expression<T, U> expression);

        /// 取模操作，对另一个表达式的值取模。
        ///
        /// @param expression 另一个表达式
        /// @return 数值操作器实例
        NumberOperator<T, U, B> mod(Expression<T, U> expression);

    }

    /// 路径操作器接口，提供路径相关的操作方法。
    ///
    /// 用于访问嵌套属性：
    /// ```java
    /// // 访问关联对象的属性
    /// .where(User::getDepartment).get(Department::getName).eq("技术部")
    /// ```
    ///
    /// @param <T> 实体类型
    /// @param <U> 路径值类型
    /// @param <B> 构建器返回类型
    interface PathOperator<T, U, B> extends ExpressionBuilder<T, U, B> {

        /// 获取指定路径的路径操作器。
        ///
        /// @param path 路径
        /// @param <V>  路径值类型
        /// @return 路径操作器实例
        <V> PathOperator<T, V, B> get(PathRef<U, V> path);

        /// 获取指定字符串路径的字符串操作器。
        ///
        /// @param path 字符串路径
        /// @return 字符串操作器实例
        StringOperator<T, B> get(PathRef.StringRef<U> path);

        /// 获取指定数值路径的数值操作器。
        ///
        /// @param path 数值路径
        /// @param <V>  数值类型
        /// @return 数值操作器实例
        <V extends Number> NumberOperator<T, V, B> get(PathRef.NumberRef<U, V> path);

    }

    /// 字符串操作器接口，提供字符串特有的操作方法。
    ///
    /// 示例：
    /// ```java
    /// // 模糊匹配
    /// .where(User::getName).like("%张%")
    /// .where(User::getName).startsWith("张")
    /// .where(User::getName).contains("三")
    ///
    /// // 字符串函数
    /// .where(User::getName).lower().eq("john")
    /// .where(User::getName).length().gt(5)
    /// ```
    ///
    /// @param <T> 实体类型
    /// @param <B> 构建器返回类型
    interface StringOperator<T, B> extends ExpressionBuilder<T, String, B> {

        /// 如果字符串不为空，则等于指定值。
        ///
        /// @param value 比较值
        /// @return 字符串操作器实例
        B eqIfNotEmpty(String value);

        /// 模糊匹配指定值。
        ///
        /// @param value 匹配值
        /// @return 字符串操作器实例
        B like(String value);

        /// 以指定值开头。
        ///
        /// @param value 开头值
        /// @return 字符串操作器实例
        default B startsWith(String value) {
            return like(value + '%');
        }

        /// 以指定值结尾。
        ///
        /// @param value 结尾值
        /// @return 字符串操作器实例
        default B endsWith(String value) {
            return like('%' + value);
        }

        /// 包含指定值。
        ///
        /// @param value 包含值
        /// @return 字符串操作器实例
        default B contains(String value) {
            return like('%' + value + '%');
        }

        /// 不模糊匹配指定值。
        ///
        /// @param value 匹配值
        /// @return 字符串操作器实例
        B notLike(String value);

        /// 不以指定值开头。
        ///
        /// @param value 开头值
        /// @return 字符串操作器实例
        default B notStartsWith(String value) {
            return notLike(value + '%');
        }

        /// 不以指定值结尾。
        ///
        /// @param value 结尾值
        /// @return 字符串操作器实例
        default B notEndsWith(String value) {
            return notLike('%' + value);
        }

        /// 不包含指定值。
        ///
        /// @param value 包含值
        /// @return 字符串操作器实例
        default B notContains(String value) {
            return notLike('%' + value + '%');
        }

        /// 如果值不为 null，则模糊匹配指定值。
        ///
        /// @param value 匹配值
        /// @return 字符串操作器实例
        B likeIfNotNull(String value);

        /// 如果值不为 null，则以指定值开头。
        ///
        /// @param value 开头值
        /// @return 字符串操作器实例
        default B startsWithIfNotNull(String value) {
            return likeIfNotNull(value == null ? null : value + '%');
        }

        /// 如果值不为 null，则以指定值结尾。
        ///
        /// @param value 结尾值
        /// @return 字符串操作器实例
        default B endsWithIfNotNull(String value) {
            return likeIfNotNull(value == null ? null : '%' + value);
        }

        /// 如果值不为 null，则包含指定值。
        ///
        /// @param value 包含值
        /// @return 字符串操作器实例
        default B containsIfNotNull(String value) {
            return likeIfNotNull(value == null ? null : '%' + value + '%');
        }

        /// 如果值不为 null，则不模糊匹配指定值。
        ///
        /// @param value 匹配值
        /// @return 字符串操作器实例
        B notLikeIfNotNull(String value);

        /// 如果值不为 null，则不以指定值开头。
        ///
        /// @param value 开头值
        /// @return 字符串操作器实例
        default B notStartsWithIfNotNull(String value) {
            return notLikeIfNotNull(value == null ? null : value + '%');
        }

        /// 如果值不为 null，则不以指定值结尾。
        ///
        /// @param value 结尾值
        /// @return 字符串操作器实例
        default B notEndsWithIfNotNull(String value) {
            return notLikeIfNotNull(value == null ? null : '%' + value);
        }

        /// 如果值不为 null，则不包含指定值。
        ///
        /// @param value 包含值
        /// @return 字符串操作器实例
        default B notContainsIfNotNull(String value) {
            return notLikeIfNotNull(value == null ? null : '%' + value + '%');
        }

        /// 如果字符串不为空，则模糊匹配指定值。
        ///
        /// @param value 匹配值
        /// @return 字符串操作器实例
        default B likeIfNotEmpty(String value) {
            return value == null || value.isEmpty() ? likeIfNotNull(null) : like(value);
        }

        /// 如果字符串不为空，则以指定值开头。
        ///
        /// @param value 开头值
        /// @return 字符串操作器实例
        default B startsWithIfNotEmpty(String value) {
            return likeIfNotEmpty(value == null || value.isEmpty() ? null : value + '%');
        }

        /// 如果字符串不为空，则以指定值结尾。
        ///
        /// @param value 结尾值
        /// @return 字符串操作器实例
        default B endsWithIfNotEmpty(String value) {
            return likeIfNotEmpty(value == null || value.isEmpty() ? null : '%' + value);
        }

        /// 如果字符串不为空，则包含指定值。
        ///
        /// @param value 包含值
        /// @return 字符串操作器实例
        default B containsIfNotEmpty(String value) {
            return likeIfNotEmpty(value == null || value.isEmpty() ? null : '%' + value + '%');
        }

        /// 如果字符串不为空，则不模糊匹配指定值。
        ///
        /// @param value 匹配值
        /// @return 字符串操作器实例
        B notLikeIfNotEmpty(String value);

        /// 如果字符串不为空，则不以指定值开头。
        ///
        /// @param value 开头值
        /// @return 字符串操作器实例
        default B notStartsWithIfNotEmpty(String value) {
            return notLikeIfNotEmpty(value == null || value.isEmpty() ? null : value + '%');
        }

        /// 如果字符串不为空，则不以指定值结尾。
        ///
        /// @param value 结尾值
        /// @return 字符串操作器实例
        default B notEndsWithIfNotEmpty(String value) {
            return notLikeIfNotEmpty(value == null || value.isEmpty() ? null : '%' + value);
        }

        /// 如果字符串不为空，则不包含指定值。
        ///
        /// @param value 包含值
        /// @return 字符串操作器实例
        default B notContainsIfNotEmpty(String value) {
            return notLikeIfNotEmpty(value == null || value.isEmpty() ? null : '%' + value + '%');
        }

        /// 转换为小写。
        ///
        /// @return 字符串操作器实例
        StringOperator<T, B> lower();

        /// 转换为大写。
        ///
        /// @return 字符串操作器实例
        StringOperator<T, B> upper();

        /// 截取子字符串。
        ///
        /// @param offset 起始位置
        /// @param length 长度
        /// @return 字符串操作器实例
        StringOperator<T, B> substring(int offset, int length);

        /// 截取子字符串，从指定位置到末尾。
        ///
        /// @param offset 起始位置
        /// @return 字符串操作器实例
        default StringOperator<T, B> substring(int offset) {
            return substring(offset, StringExpression.MAX_SUBSTRING_LENGTH);
        }

        /// 去除首尾空白字符。
        ///
        /// @return 字符串操作器实例
        StringOperator<T, B> trim();

        /// 获取字符串长度。
        ///
        /// @return 数值操作器实例
        NumberOperator<T, Integer, B> length();

    }

    /// 合取操作器接口，提供逻辑 AND 操作。
    ///
    /// 示例：
    /// ```java
    /// .where(User::getAge).gt(18)
    ///     .and(User::getStatus).eq("ACTIVE")
    ///     .and(User::getName).like("%张%")
    /// ```
    ///
    /// @param <T> 实体类型
    interface Conjunction<T> extends Expression<T, Boolean> {

        /// 与指定路径的路径操作器连接。
        ///
        /// @param path 路径
        /// @param <R>  路径值类型
        /// @return 路径操作器实例
        <R> PathOperator<T, R, Conjunction<T>> and(PathRef<T, R> path);

        /// 与指定数值路径的数值操作器连接。
        ///
        /// @param path 数值路径
        /// @param <R>  数值类型
        /// @return 数值操作器实例
        <R extends Number> NumberOperator<T, R, Conjunction<T>> and(PathRef.NumberRef<T, R> path);

        /// 与指定字符串路径的字符串操作器连接。
        ///
        /// @param path 字符串路径
        /// @return 字符串操作器实例
        StringOperator<T, Conjunction<T>> and(PathRef.StringRef<T> path);

        /// 与另一个表达式连接。
        ///
        /// @param expression 另一个表达式
        /// @return 合取操作器实例
        Conjunction<T> and(Expression<T, Boolean> expression);

        /// 与多个表达式连接。
        ///
        /// @param expressions 表达式集合
        /// @return 合取操作器实例
        Conjunction<T> and(Iterable<? extends Expression<T, Boolean>> expressions);

        /// 转换为断言。
        ///
        /// @return 断言实例
        Predicate<T> toPredicate();

    }

    /// 析取操作器接口，提供逻辑 OR 操作。
    ///
    /// 示例：
    /// ```java
    /// .where(User::getStatus).eq("ACTIVE")
    ///     .or(User::getStatus).eq("PENDING")
    /// ```
    ///
    /// @param <T> 实体类型
    interface Disjunction<T> extends Expression<T, Boolean> {

        /// 与指定路径的路径操作器析取。
        ///
        /// @param path 路径
        /// @param <N>  路径值类型
        /// @return 路径操作器实例
        <N> PathOperator<T, N, Disjunction<T>> or(PathRef<T, N> path);

        /// 与指定数值路径的数值操作器析取。
        ///
        /// @param path 数值路径
        /// @param <N>  数值类型
        /// @return 数值操作器实例
        <N extends Number> NumberOperator<T, N, Disjunction<T>> or(PathRef.NumberRef<T, N> path);

        /// 与指定字符串路径的字符串操作器析取。
        ///
        /// @param path 字符串路径
        /// @return 字符串操作器实例
        StringOperator<T, ? extends Disjunction<T>> or(PathRef.StringRef<T> path);

        /// 与另一个表达式析取。
        ///
        /// @param predicate 另一个表达式
        /// @return 析取操作器实例
        Disjunction<T> or(Expression<T, Boolean> predicate);

        /// 与多个表达式析取。
        ///
        /// @param expressions 表达式集合
        /// @return 析取操作器实例
        Disjunction<T> or(Iterable<? extends Expression<T, Boolean>> expressions);

        /// 转换为断言。
        ///
        /// @return 断言实例
        Predicate<T> toPredicate();

    }
}