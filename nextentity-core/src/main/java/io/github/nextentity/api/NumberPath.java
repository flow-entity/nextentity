package io.github.nextentity.api;

/// Number path interface, representing the path of the entity's numeric type attribute.
///
/// Extends NumberExpression and PathExpression, providing number expression operations and path expression functions.
///
/// @param <T> Entity type
/// @param <U> Number type
/// @author HuangChengwei
/// @since 1.0.0
public interface NumberPath<T, U extends Number> extends NumberExpression<T, U>, Path<T, U> {
    /// Creates a number path from the specified number reference.
    ///
    /// @param path Number reference
    /// @param <T>  Entity type
    /// @param <U>  Number type
    /// @return Number path
    static <T, U extends Number> NumberPath<T, U> of(PathRef.NumberRef<T, U> path) {
        return EntityRoot.<T>of().get(path);
    }
}
