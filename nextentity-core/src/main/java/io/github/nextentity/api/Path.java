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

    /// Creates a path expression from the specified path reference.
    ///
    /// @param path Path reference
    /// @param <T>  Entity type
    /// @param <U>  Value type
    /// @return Path expression
    static <T, U> Path<T, U> of(PathRef<T, U> path) {
        return EntityRoot.<T>of().get(path);
    }

    /// Creates a boolean path from the specified boolean reference.
    ///
    /// @param path Boolean reference
    /// @param <T> Entity type
    /// @return Boolean path
    static <T> BooleanPath<T> of(PathRef.BooleanRef<T> path) {
        return BooleanPath.of(path);
    }

    /// Creates a number path from the specified number reference.
    ///
    /// @param path Number reference
    /// @param <T> Entity type
    /// @param <U> Number type
    /// @return Number path
    static <T, U extends Number> NumberPath<T, U> of(PathRef.NumberRef<T, U> path) {
        return NumberPath.of(path);
    }

    /// Creates a string path from the specified string reference.
    ///
    /// @param path String reference
    /// @param <T> Entity type
    /// @return String path
    static <T> StringPath<T> of(PathRef.StringRef<T> path) {
        return StringPath.of(path);
    }

    // type-unsafe

    /// Creates a path expression from the specified field name (type-unsafe).
    ///
    /// @param path Field name
    /// @param <T> Entity type
    /// @param <U> Value type
    /// @return Path expression
    static <T, U> Path<T, U> of(String path) {
        return EntityRoot.<T>of().path(path);
    }

}
