package io.github.nextentity.api;

/// Path expression interface, representing the path expression of an entity attribute.
///
/// Extends SimpleExpression, providing basic expression operation methods.
///
/// @param <T> Entity type
/// @param <U> Expression value type
/// @author HuangChengwei
/// @since 1.0.0
public interface Path<T, U> extends SimpleExpression<T, U> {

    static <T, U> Path<T, U> of(PathRef<T, U> path) {
        return EntityRoot.<T>of().get(path);
    }

    static <T> BooleanPath<T> of(PathRef.BooleanRef<T> path) {
        return BooleanPath.of(path);
    }

    static <T, U extends Number> NumberPath<T, U> of(PathRef.NumberRef<T, U> path) {
        return NumberPath.of(path);
    }

    static <T> StringPath<T> of(PathRef.StringRef<T> path) {
        return StringPath.of(path);
    }

    // type-unsafe

    static <T, U> Path<T, U> of(String path) {
        return EntityRoot.<T>of().path(path);
    }

}
