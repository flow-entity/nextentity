package io.github.nextentity.api;

/// 字符串表达式接口，提供字符串相关的查询操作方法。
///
/// 提供模糊匹配（like、startsWith、contains）、字符串函数（lower、upper、trim）
/// 以及条件匹配方法。
///
/// @param <T> 实体类型
/// @author HuangChengwei
/// @see SimpleExpression 基本比较操作
/// @since 1.0.0
public interface StringExpression<T> extends SimpleExpression<T, String> {

    /// 子字符串最大长度，用于表示截取到字符串末尾
    ///
    /// 当 substring(offset) 不指定长度时，使用此值表示截取到末尾。
    int MAX_SUBSTRING_LENGTH = Integer.MAX_VALUE >> 1;

    /// 匹配指定的字符串模式。
    ///
    /// @param value 字符串模式
    /// @return 断言对象
    Predicate<T> like(String value);

    /// 匹配以指定字符串开头的模式。
    ///
    /// @param value 前缀字符串
    /// @return 断言对象
    default Predicate<T> startsWith(String value) {
        return like(value + '%');
    }

    /// 匹配以指定字符串结尾的模式。
    ///
    /// @param value 后缀字符串
    /// @return 断言对象
    default Predicate<T> endsWith(String value) {
        return like('%' + value);
    }

    /// 匹配包含指定字符串的模式。
    ///
    /// @param value 包含的字符串
    /// @return 断言对象
    default Predicate<T> contains(String value) {
        return like('%' + value + '%');
    }

    /// 不匹配指定的字符串模式。
    ///
    /// @param value 字符串模式
    /// @return 断言对象
    Predicate<T> notLike(String value);

    /// 不匹配以指定字符串开头的模式。
    ///
    /// @param value 前缀字符串
    /// @return 断言对象
    default Predicate<T> notStartsWith(String value) {
        return notLike(value + '%');
    }

    /// 不匹配以指定字符串结尾的模式。
    ///
    /// @param value 后缀字符串
    /// @return 断言对象
    default Predicate<T> notEndsWith(String value) {
        return notLike('%' + value);
    }

    /// 不匹配包含指定字符串的模式。
    ///
    /// @param value 包含的字符串
    /// @return 断言对象
    default Predicate<T> notContains(String value) {
        return notLike('%' + value + '%');
    }

    /// 如果值不为 null，则匹配指定的字符串模式。
    ///
    /// @param value 字符串模式
    /// @return 断言对象
    Predicate<T> likeIfNotNull(String value);

    /// 如果值不为 null，则匹配以指定字符串开头的模式。
    ///
    /// @param value 前缀字符串
    /// @return 断言对象
    default Predicate<T> startsWithIfNotNull(String value) {
        return value == null ? likeIfNotNull(null) : likeIfNotNull(value + '%');
    }

    /// 如果值不为 null，则匹配以指定字符串结尾的模式。
    ///
    /// @param value 后缀字符串
    /// @return 断言对象
    default Predicate<T> endsWithIfNotNull(String value) {
        return value == null ? likeIfNotNull(null) : likeIfNotNull('%' + value);
    }

    /// 如果值不为 null，则匹配包含指定字符串的模式。
    ///
    /// @param value 包含的字符串
    /// @return 断言对象
    default Predicate<T> containsIfNotNull(String value) {
        return value == null ? likeIfNotNull(null) : likeIfNotNull('%' + value + '%');
    }

    /// 如果值不为 null，则不匹配指定的字符串模式。
    ///
    /// @param value 字符串模式
    /// @return 断言对象
    Predicate<T> notLikeIfNotNull(String value);

    /// 如果值不为 null，则不匹配以指定字符串开头的模式。
    ///
    /// @param value 前缀字符串
    /// @return 断言对象
    default Predicate<T> notStartsWithIfNotNull(String value) {
        return value == null ? notLikeIfNotNull(null) : notLikeIfNotNull(value + '%');
    }

    /// 如果值不为 null，则不匹配以指定字符串结尾的模式。
    ///
    /// @param value 后缀字符串
    /// @return 断言对象
    default Predicate<T> notEndsWithIfNotNull(String value) {
        return value == null ? notLikeIfNotNull(null) : notLikeIfNotNull('%' + value);
    }

    /// 如果值不为 null，则不匹配包含指定字符串的模式。
    ///
    /// @param value 包含的字符串
    /// @return 断言对象
    default Predicate<T> notContainsIfNotNull(String value) {
        return value == null ? notLikeIfNotNull(null) : notLikeIfNotNull('%' + value + '%');
    }

    /// 如果字符串不为空，则匹配指定的字符串模式。
    ///
    /// @param value 字符串模式
    /// @return 断言对象
    Predicate<T> likeIfNotEmpty(String value);

    /// 如果字符串不为空，则匹配以指定字符串开头的模式。
    ///
    /// @param value 前缀字符串
    /// @return 断言对象
    default Predicate<T> startsWithIfNotEmpty(String value) {
        return value == null || value.isEmpty() ? likeIfNotEmpty(null) : likeIfNotEmpty(value + '%');
    }

    /// 如果字符串不为空，则匹配以指定字符串结尾的模式。
    ///
    /// @param value 后缀字符串
    /// @return 断言对象
    default Predicate<T> endsWithIfNotEmpty(String value) {
        return value == null || value.isEmpty() ? likeIfNotEmpty(null) : likeIfNotEmpty('%' + value);
    }

    /// 如果字符串不为空，则匹配包含指定字符串的模式。
    ///
    /// @param value 包含的字符串
    /// @return 断言对象
    default Predicate<T> containsIfNotEmpty(String value) {
        return value == null || value.isEmpty() ? likeIfNotEmpty(null) : likeIfNotEmpty('%' + value + '%');
    }

    /// 如果字符串不为空，则不匹配指定的字符串模式。
    ///
    /// @param value 字符串模式
    /// @return 断言对象
    Predicate<T> notLikeIfNotEmpty(String value);

    /// 如果字符串不为空，则不匹配以指定字符串开头的模式。
    ///
    /// @param value 前缀字符串
    /// @return 断言对象
    default Predicate<T> notStartsWithIfNotEmpty(String value) {
        return value == null || value.isEmpty() ? notLikeIfNotEmpty(null) : notLikeIfNotEmpty(value + '%');
    }

    /// 如果字符串不为空，则不匹配以指定字符串结尾的模式。
    ///
    /// @param value 后缀字符串
    /// @return 断言对象
    default Predicate<T> notEndsWithIfNotEmpty(String value) {
        return value == null || value.isEmpty() ? notLikeIfNotEmpty(null) : notLikeIfNotEmpty('%' + value);
    }

    /// 如果字符串不为空，则不匹配包含指定字符串的模式。
    ///
    /// @param value 包含的字符串
    /// @return 断言对象
    default Predicate<T> notContainsIfNotEmpty(String value) {
        return value == null || value.isEmpty() ? notLikeIfNotEmpty(null) : notLikeIfNotEmpty('%' + value + '%');
    }

    /// 将字符串转换为小写。
    ///
    /// @return 小写字符串表达式
    StringExpression<T> lower();

    /// 将字符串转换为大写。
    ///
    /// @return 大写字符串表达式
    StringExpression<T> upper();

    /// 截取子字符串。
    ///
    /// @param offset 起始位置
    /// @param length 子字符串长度
    /// @return 子字符串表达式
    StringExpression<T> substring(int offset, int length);

    /// 从指定位置截取子字符串到末尾。
    ///
    /// @param offset 起始位置
    /// @return 子字符串表达式
    default io.github.nextentity.api.StringExpression<T> substring(int offset) {
        return substring(offset, MAX_SUBSTRING_LENGTH);
    }

    /// 去除字符串首尾空白字符。
    ///
    /// @return 去除空白后的字符串表达式
    StringExpression<T> trim();

    /// 获取字符串长度。
    ///
    /// @return 长度表达式
    NumberExpression<T, Integer> length();

    /// 如果字符串不为空，则匹配相等的字符串。
    ///
    /// @param value 比较值
    /// @return 断言对象
    Predicate<T> eqIfNotEmpty(String value);
}