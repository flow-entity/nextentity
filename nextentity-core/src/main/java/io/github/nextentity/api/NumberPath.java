package io.github.nextentity.api;

/// Number path interface, representing the path of the entity's numeric type attribute.
///
/// Also extends PathRef.NumberRef to allow NumberPath instances to be passed where NumberRef
/// parameters are expected in query building methods.
///
/// @param <T> Entity type
/// @param <U> Number type
/// @author HuangChengwei
/// @since 1.0.0
public interface NumberPath<T, U extends Number> extends NumberExpression<T, U>, Path<T, U>, PathRef.NumberRef<T, U> {
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
