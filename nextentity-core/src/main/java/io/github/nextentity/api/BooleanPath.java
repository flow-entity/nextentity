package io.github.nextentity.api;

/// Boolean path interface, representing the path of the entity's boolean type attribute.
///
/// Also extends PathRef.BooleanRef to allow BooleanPath instances to be passed where BooleanRef
/// parameters are expected in query building methods.
///
/// @param <T> Entity type
/// @author HuangChengwei
/// @since 1.0.0
public interface BooleanPath<T> extends Predicate<T>, Path<T, Boolean>, PathRef.BooleanRef<T> {
    /// Creates a boolean path from the specified boolean reference.
    ///
    /// @param path Boolean reference
    /// @param <T>  Entity type
    /// @return Boolean path
    static <T> BooleanPath<T> of(PathRef.BooleanRef<T> path) {
        return EntityRoot.<T>of().get(path);
    }
}
