package io.github.nextentity.api;

/**
 * String expression interface, providing string-related query operation methods.
 *
 * @param <T> Entity type
 * @author HuangChengwei
 * @since 1.0.0
 */
public interface StringExpression<T> extends SimpleExpression<T, String> {
    /**
     * Matches the specified string pattern.
     *
     * @param value String pattern
     * @return Predicate object
     */
    Predicate<T> like(String value);

    /**
     * Matches patterns starting with the specified string.
     *
     * @param value Prefix string
     * @return Predicate object
     */
    default Predicate<T> startsWith(String value) {
        return like(value + '%');
    }

    /**
     * Matches patterns ending with the specified string.
     *
     * @param value Suffix string
     * @return Predicate object
     */
    default Predicate<T> endsWith(String value) {
        return like('%' + value);
    }

    /**
     * Matches patterns containing the specified string.
     *
     * @param value Contained string
     * @return Predicate object
     */
    default Predicate<T> contains(String value) {
        return like('%' + value + '%');
    }

    /**
     * Does not match the specified string pattern.
     *
     * @param value String pattern
     * @return Predicate object
     */
    Predicate<T> notLike(String value);

    /**
     * Does not match patterns starting with the specified string.
     *
     * @param value Prefix string
     * @return Predicate object
     */
    default Predicate<T> notStartsWith(String value) {
        return notLike(value + '%');
    }

    /**
     * Does not match patterns ending with the specified string.
     *
     * @param value Suffix string
     * @return Predicate object
     */
    default Predicate<T> notEndsWith(String value) {
        return notLike('%' + value);
    }

    /**
     * Does not match patterns containing the specified string.
     *
     * @param value Contained string
     * @return Predicate object
     */
    default Predicate<T> notContains(String value) {
        return notLike('%' + value + '%');
    }

    /**
     * If the value is not null, matches the specified string pattern.
     *
     * @param value String pattern
     * @return Predicate object
     */
    Predicate<T> likeIfNotNull(String value);

    /**
     * If the value is not null, matches patterns starting with the specified string.
     *
     * @param value Prefix string
     * @return Predicate object
     */
    default Predicate<T> startsWithIfNotNull(String value) {
        return value == null ? likeIfNotNull(null) : likeIfNotNull(value + '%');
    }

    /**
     * If the value is not null, matches patterns ending with the specified string.
     *
     * @param value Suffix string
     * @return Predicate object
     */
    default Predicate<T> endsWithIfNotNull(String value) {
        return value == null ? likeIfNotNull(null) : likeIfNotNull('%' + value);
    }

    /**
     * If the value is not null, matches patterns containing the specified string.
     *
     * @param value Contained string
     * @return Predicate object
     */
    default Predicate<T> containsIfNotNull(String value) {
        return value == null ? likeIfNotNull(null) : likeIfNotNull('%' + value + '%');
    }

    /**
     * If the value is not null, does not match the specified string pattern.
     *
     * @param value String pattern
     * @return Predicate object
     */
    Predicate<T> notLikeIfNotNull(String value);

    /**
     * If the value is not null, does not match patterns starting with the specified string.
     *
     * @param value Prefix string
     * @return Predicate object
     */
    default Predicate<T> notStartsWithIfNotNull(String value) {
        return value == null ? notLikeIfNotNull(null) : notLikeIfNotNull(value + '%');
    }

    /**
     * If the value is not null, does not match patterns ending with the specified string.
     *
     * @param value Suffix string
     * @return Predicate object
     */
    default Predicate<T> notEndsWithIfNotNull(String value) {
        return value == null ? notLikeIfNotNull(null) : notLikeIfNotNull('%' + value);
    }

    /**
     * If the value is not null, does not match patterns containing the specified string.
     *
     * @param value Contained string
     * @return Predicate object
     */
    default Predicate<T> notContainsIfNotNull(String value) {
        return value == null ? notLikeIfNotNull(null) : notLikeIfNotNull('%' + value + '%');
    }


    /**
     * If the value is not empty, matches the specified string pattern.
     *
     * @param value String pattern
     * @return Predicate object
     */
    Predicate<T> likeIfNotEmpty(String value);

    /**
     * If the value is not empty, matches patterns starting with the specified string.
     *
     * @param value Prefix string
     * @return Predicate object
     */
    default Predicate<T> startsWithIfNotEmpty(String value) {
        return value == null || value.isEmpty() ? likeIfNotEmpty(null) : likeIfNotEmpty(value + '%');
    }

    /**
     * If the value is not empty, matches patterns ending with the specified string.
     *
     * @param value Suffix string
     * @return Predicate object
     */
    default Predicate<T> endsWithIfNotEmpty(String value) {
        return value == null || value.isEmpty() ? likeIfNotEmpty(null) : likeIfNotEmpty('%' + value);
    }

    /**
     * If the value is not empty, matches patterns containing the specified string.
     *
     * @param value Contained string
     * @return Predicate object
     */
    default Predicate<T> containsIfNotEmpty(String value) {
        return value == null || value.isEmpty() ? likeIfNotEmpty(null) : likeIfNotEmpty('%' + value + '%');
    }

    /**
     * If the value is not empty, does not match the specified string pattern.
     *
     * @param value String pattern
     * @return Predicate object
     */
    Predicate<T> notLikeIfNotEmpty(String value);

    /**
     * If the value is not empty, does not match patterns starting with the specified string.
     *
     * @param value Prefix string
     * @return Predicate object
     */
    default Predicate<T> notStartsWithIfNotEmpty(String value) {
        return value == null || value.isEmpty() ? notLikeIfNotEmpty(null) : notLikeIfNotEmpty(value + '%');
    }

    /**
     * If the value is not empty, does not match patterns ending with the specified string.
     *
     * @param value Suffix string
     * @return Predicate object
     */
    default Predicate<T> notEndsWithIfNotEmpty(String value) {
        return value == null || value.isEmpty() ? notLikeIfNotEmpty(null) : notLikeIfNotEmpty('%' + value);
    }

    /**
     * If the value is not empty, does not match patterns containing the specified string.
     *
     * @param value Contained string
     * @return Predicate object
     */
    default Predicate<T> notContainsIfNotEmpty(String value) {
        return value == null || value.isEmpty() ? notLikeIfNotEmpty(null) : notLikeIfNotEmpty('%' + value + '%');
    }

    /**
     * Converts the string to lowercase.
     *
     * @return Lowercase string expression
     */
    StringExpression<T> lower();

    /**
     * Converts the string to uppercase.
     *
     * @return Uppercase string expression
     */
    StringExpression<T> upper();

    /**
     * Substrings the string.
     *
     * @param offset Starting offset
     * @param length Substring length
     * @return Substring expression
     */
    StringExpression<T> substring(int offset, int length);

    /**
     * Substrings the string starting from the specified offset.
     *
     * @param offset Starting offset
     * @return Substring expression
     */
    default io.github.nextentity.api.StringExpression<T> substring(int offset) {
        return substring(offset, Integer.MAX_VALUE);
    }

    /**
     * Trims whitespace characters from both ends of the string.
     *
     * @return Trimmed string expression
     */
    StringExpression<T> trim();

    /**
     * Gets the length of the string.
     *
     * @return Length expression
     */
    NumberExpression<T, Integer> length();

    /**
     * If the value is not empty, matches an equal string.
     *
     * @param value Comparison value
     * @return Predicate object
     */
    Predicate<T> eqIfNotEmpty(String value);
}
