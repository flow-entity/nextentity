package io.github.nextentity.api;

/**
 * 字符串表达式接口，提供字符串相关的查询操作方法。
 *
 * @param <T> 实体类型
 * @author HuangChengwei
 * @since 2026/1/7
 */
public interface StringExpression<T> extends SimpleExpression<T, String> {
    /**
     * 匹配指定的字符串模式。
     *
     * @param value 字符串模式
     * @return 谓词对象
     */
    Predicate<T> like(String value);

    /**
     * 匹配以指定字符串开头的模式。
     *
     * @param value 前缀字符串
     * @return 谓词对象
     */
    default Predicate<T> startWith(String value) {
        return like(value + '%');
    }

    /**
     * 匹配以指定字符串结尾的模式。
     *
     * @param value 后缀字符串
     * @return 谓词对象
     */
    default Predicate<T> endsWith(String value) {
        return like('%' + value);
    }

    /**
     * 匹配包含指定字符串的模式。
     *
     * @param value 包含的字符串
     * @return 谓词对象
     */
    default Predicate<T> contains(String value) {
        return like('%' + value + '%');
    }

    /**
     * 不匹配指定的字符串模式。
     *
     * @param value 字符串模式
     * @return 谓词对象
     */
    Predicate<T> notLike(String value);

    /**
     * 不匹配以指定字符串开头的模式。
     *
     * @param value 前缀字符串
     * @return 谓词对象
     */
    default Predicate<T> notStartWith(String value) {
        return notLike(value + '%');
    }

    /**
     * 不匹配以指定字符串结尾的模式。
     *
     * @param value 后缀字符串
     * @return 谓词对象
     */
    default Predicate<T> notEndsWith(String value) {
        return notLike('%' + value);
    }

    /**
     * 不匹配包含指定字符串的模式。
     *
     * @param value 包含的字符串
     * @return 谓词对象
     */
    default Predicate<T> notContains(String value) {
        return notLike('%' + value + '%');
    }

    /**
     * 如果值不为null，则匹配指定的字符串模式。
     *
     * @param value 字符串模式
     * @return 谓词对象
     */
    Predicate<T> likeIfNotNull(String value);

    /**
     * 如果值不为null，则匹配以指定字符串开头的模式。
     *
     * @param value 前缀字符串
     * @return 谓词对象
     */
    default Predicate<T> startWithIfNotNull(String value) {
        return value == null ? likeIfNotNull(null) : likeIfNotNull(value + '%');
    }

    /**
     * 如果值不为null，则匹配以指定字符串结尾的模式。
     *
     * @param value 后缀字符串
     * @return 谓词对象
     */
    default Predicate<T> endsWithIfNotNull(String value) {
        return value == null ? likeIfNotNull(null) : likeIfNotNull('%' + value);
    }

    /**
     * 如果值不为null，则匹配包含指定字符串的模式。
     *
     * @param value 包含的字符串
     * @return 谓词对象
     */
    default Predicate<T> containsIfNotNull(String value) {
        return value == null ? likeIfNotNull(null) : likeIfNotNull('%' + value + '%');
    }

    /**
     * 如果值不为null，则不匹配指定的字符串模式。
     *
     * @param value 字符串模式
     * @return 谓词对象
     */
    Predicate<T> notLikeIfNotNull(String value);

    /**
     * 如果值不为null，则不匹配以指定字符串开头的模式。
     *
     * @param value 前缀字符串
     * @return 谓词对象
     */
    default Predicate<T> notStartWithIfNotNull(String value) {
        return value == null ? notLikeIfNotNull(null) : notLikeIfNotNull(value + '%');
    }

    /**
     * 如果值不为null，则不匹配以指定字符串结尾的模式。
     *
     * @param value 后缀字符串
     * @return 谓词对象
     */
    default Predicate<T> notEndsWithIfNotNull(String value) {
        return value == null ? notLikeIfNotNull(null) : notLikeIfNotNull('%' + value);
    }

    /**
     * 如果值不为null，则不匹配包含指定字符串的模式。
     *
     * @param value 包含的字符串
     * @return 谓词对象
     */
    default Predicate<T> notContainsIfNotNull(String value) {
        return value == null ? notLikeIfNotNull(null) : notLikeIfNotNull('%' + value + '%');
    }


    /**
     * 如果值不为空，则匹配指定的字符串模式。
     *
     * @param value 字符串模式
     * @return 谓词对象
     */
    Predicate<T> likeIfNotEmpty(String value);

    /**
     * 如果值不为空，则匹配以指定字符串开头的模式。
     *
     * @param value 前缀字符串
     * @return 谓词对象
     */
    default Predicate<T> startWithIfNotEmpty(String value) {
        return value == null || value.isEmpty() ? likeIfNotEmpty(null) : likeIfNotEmpty(value + '%');
    }

    /**
     * 如果值不为空，则匹配以指定字符串结尾的模式。
     *
     * @param value 后缀字符串
     * @return 谓词对象
     */
    default Predicate<T> endsWithIfNotEmpty(String value) {
        return value == null || value.isEmpty() ? likeIfNotEmpty(null) : likeIfNotEmpty('%' + value);
    }

    /**
     * 如果值不为空，则匹配包含指定字符串的模式。
     *
     * @param value 包含的字符串
     * @return 谓词对象
     */
    default Predicate<T> containsIfNotEmpty(String value) {
        return value == null || value.isEmpty() ? likeIfNotEmpty(null) : likeIfNotEmpty('%' + value + '%');
    }

    /**
     * 如果值不为空，则不匹配指定的字符串模式。
     *
     * @param value 字符串模式
     * @return 谓词对象
     */
    Predicate<T> notLikeIfNotEmpty(String value);

    /**
     * 如果值不为空，则不匹配以指定字符串开头的模式。
     *
     * @param value 前缀字符串
     * @return 谓词对象
     */
    default Predicate<T> notStartWithIfNotEmpty(String value) {
        return value == null || value.isEmpty() ? notLikeIfNotEmpty(null) : notLikeIfNotEmpty(value + '%');
    }

    /**
     * 如果值不为空，则不匹配以指定字符串结尾的模式。
     *
     * @param value 后缀字符串
     * @return 谓词对象
     */
    default Predicate<T> notEndsWithIfNotEmpty(String value) {
        return value == null || value.isEmpty() ? notLikeIfNotEmpty(null) : notLikeIfNotEmpty('%' + value);
    }

    /**
     * 如果值不为空，则不匹配包含指定字符串的模式。
     *
     * @param value 包含的字符串
     * @return 谓词对象
     */
    default Predicate<T> notContainsIfNotEmpty(String value) {
        return value == null || value.isEmpty() ? notLikeIfNotEmpty(null) : notLikeIfNotEmpty('%' + value + '%');
    }

    /**
     * 将字符串转换为小写。
     *
     * @return 小写字符串表达式
     */
    io.github.nextentity.api.StringExpression<T> lower();

    /**
     * 将字符串转换为大写。
     *
     * @return 大写字符串表达式
     */
    io.github.nextentity.api.StringExpression<T> upper();

    /**
     * 截取字符串的子串。
     *
     * @param offset 起始偏移量
     * @param length 子串长度
     * @return 子串表达式
     */
    io.github.nextentity.api.StringExpression<T> substring(int offset, int length);

    /**
     * 从指定偏移量开始截取字符串。
     *
     * @param offset 起始偏移量
     * @return 子串表达式
     */
    default io.github.nextentity.api.StringExpression<T> substring(int offset) {
        return substring(offset, Integer.MAX_VALUE);
    }

    /**
     * 去除字符串两端的空白字符。
     *
     * @return 去除空白后的字符串表达式
     */
    io.github.nextentity.api.StringExpression<T> trim();

    /**
     * 获取字符串的长度。
     *
     * @return 长度表达式
     */
    NumberExpression<T, Integer> length();

    /**
     * 如果值不为空，则匹配相等的字符串。
     *
     * @param value 比较值
     * @return 谓词对象
     */
    Predicate<T> eqIfNotEmpty(String value);
}
