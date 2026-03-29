package io.github.nextentity.api;

/// String path interface, representing the path of the string type attribute of the entity.
///
/// @param <T> Entity type
/// @author HuangChengwei
/// @since 1.0.0
public interface StringPath<T> extends StringExpression<T>, Path<T, String> {
    /// Creates a string path from the specified string reference.
    ///
    /// @param path String reference
    /// @param <T>  Entity type
    /// @return String path
    static <T> StringPath<T> of(PathRef.StringRef<T> path) {
        return EntityRoot.<T>of().get(path);
    }
}
