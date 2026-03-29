package io.github.nextentity.api;

/// Typed expression interface, representing an expression with a specific value type.
///
/// ## Building Expression Instances
///
/// Expression instances can be created through the `of` static methods provided by subinterfaces:
///
/// - **`Path.of(PathRef)`** - Create a path expression from a method reference (e.g., `User::getName`)
/// - **`BooleanPath.of(PathRef.BooleanRef)`** - Create a boolean path expression
/// - **`NumberPath.of(PathRef.NumberRef)`** - Create a numeric path expression
/// - **`StringPath.of(PathRef.StringRef)`** - Create a string path expression
/// - **`EntityPath.of(PathRef)`** - Create an entity path expression for nested entity access
/// - **`Expression.of(value)`** - Create a literal expression from a value
///
/// Example usage:
/// ```java
/// // Path expression via method reference
/// Path<User, String> path = Path.of(User::getName);
///
/// // String-specific path
/// StringPath<User> stringPath = StringPath.of(User::getName);
///
/// // Numeric path
/// NumberPath<User, Integer> agePath = NumberPath.of(User::getAge);
///
/// // Literal expression
/// Expression<User, String> literal = Expression.of("John");
/// ```
///
/// @param <T> Entity type
/// @param <U> Expression value type
/// @author HuangChengwei
/// @since 1.0.0
@SuppressWarnings("unused")
public interface Expression<T, U> {
    /// Creates a typed expression from the specified value.
    ///
    /// @param value Literal value
    /// @param <T>   Entity type
    /// @param <U>   Value type
    /// @return Typed expression
    static <T, U> Expression<T, U> of(U value) {
        return EntityRoot.<T>of().literal(value);
    }
}
