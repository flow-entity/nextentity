package io.github.nextentity.api;

public interface StringExpression<T> extends SimpleExpression<T, String> {
    Predicate<T> like(String value);

    default Predicate<T> startWith(String value) {
        return like(value + '%');
    }

    default Predicate<T> endsWith(String value) {
        return like('%' + value);
    }

    default Predicate<T> contains(String value) {
        return like('%' + value + '%');
    }

    Predicate<T> notLike(String value);

    default Predicate<T> notStartWith(String value) {
        return notLike(value + '%');
    }

    default Predicate<T> notEndsWith(String value) {
        return notLike('%' + value);
    }

    default Predicate<T> notContains(String value) {
        return notLike('%' + value + '%');
    }

    Predicate<T> likeIfNotNull(String value);

    default Predicate<T> startWithIfNotNull(String value) {
        return value == null ? likeIfNotNull(null) : likeIfNotNull(value + '%');
    }

    default Predicate<T> endsWithIfNotNull(String value) {
        return value == null ? likeIfNotNull(null) : likeIfNotNull('%' + value);
    }

    default Predicate<T> containsIfNotNull(String value) {
        return value == null ? likeIfNotNull(null) : likeIfNotNull('%' + value + '%');
    }

    Predicate<T> notLikeIfNotNull(String value);

    default Predicate<T> notStartWithIfNotNull(String value) {
        return value == null ? notLikeIfNotNull(null) : notLikeIfNotNull(value + '%');
    }

    default Predicate<T> notEndsWithIfNotNull(String value) {
        return value == null ? notLikeIfNotNull(null) : notLikeIfNotNull('%' + value);
    }

    default Predicate<T> notContainsIfNotNull(String value) {
        return value == null ? notLikeIfNotNull(null) : notLikeIfNotNull('%' + value + '%');
    }


    Predicate<T> likeIfNotEmpty(String value);

    default Predicate<T> startWithIfNotEmpty(String value) {
        return value == null || value.isEmpty() ? likeIfNotEmpty(null) : likeIfNotEmpty(value + '%');
    }

    default Predicate<T> endsWithIfNotEmpty(String value) {
        return value == null || value.isEmpty() ? likeIfNotEmpty(null) : likeIfNotEmpty('%' + value);
    }

    default Predicate<T> containsIfNotEmpty(String value) {
        return value == null || value.isEmpty() ? likeIfNotEmpty(null) : likeIfNotEmpty('%' + value + '%');
    }

    Predicate<T> notLikeIfNotEmpty(String value);

    default Predicate<T> notStartWithIfNotEmpty(String value) {
        return value == null || value.isEmpty() ? notLikeIfNotEmpty(null) : notLikeIfNotEmpty(value + '%');
    }

    default Predicate<T> notEndsWithIfNotEmpty(String value) {
        return value == null || value.isEmpty() ? notLikeIfNotEmpty(null) : notLikeIfNotEmpty('%' + value);
    }

    default Predicate<T> notContainsIfNotEmpty(String value) {
        return value == null || value.isEmpty() ? notLikeIfNotEmpty(null) : notLikeIfNotEmpty('%' + value + '%');
    }

    io.github.nextentity.api.StringExpression<T> lower();

    io.github.nextentity.api.StringExpression<T> upper();

    io.github.nextentity.api.StringExpression<T> substring(int offset, int length);

    default io.github.nextentity.api.StringExpression<T> substring(int offset) {
        return substring(offset, Integer.MAX_VALUE);
    }

    io.github.nextentity.api.StringExpression<T> trim();

    NumberExpression<T, Integer> length();

    Predicate<T> eqIfNotEmpty(String value);
}
