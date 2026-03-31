package io.github.nextentity.api;

/// String path interface, representing the path of the string type attribute of the entity.
///
/// Also extends PathRef.StringRef to allow StringPath instances to be passed where StringRef
/// parameters are expected in query building methods.
///
/// @param <T> Entity type
/// @author HuangChengwei
/// @since 1.0.0
public interface StringPath<T> extends StringExpression<T>, Path<T, String>, PathRef.StringRef<T> {
    /// Creates a string path from the specified string reference.
    ///
    /// @param path String reference
    /// @param <T>  Entity type
    /// @return String path
    static <T> StringPath<T> of(PathRef.StringRef<T> path) {
        return EntityRoot.<T>of().get(path);
    }
}
