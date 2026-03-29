package io.github.nextentity.api.model;

import io.github.nextentity.api.*;

/// Entity root interface, providing entity attribute access and path building methods.
///
/// @param <T> Entity type
/// @author HuangChengwei
/// @since 1.0.0
///
public interface EntityRoot<T> {

    ///
    /// Creates a literal expression.
    ///
    /// @param value Literal value
    /// @param <U> Literal type
    /// @return Literal expression
    ///
    <U> TypedExpression<T, U> literal(U value);

    ///
    /// Gets the entity path expression for the specified path.
    ///
    /// @param path Property path
    /// @param <U> Property type
    /// @return Entity path expression
    ///
    <U> EntityPath<T, U> get(Path<T, U> path);

    ///
    /// Gets the boolean path expression for the specified boolean property path.
    ///
    /// @param path Boolean property path
    /// @return Boolean path expression
    ///
    BooleanPath<T> get(Path.BooleanRef<T> path);

    ///
    /// Gets the string path expression for the specified string property path.
    ///
    /// @param path String property path
    /// @return String path expression
    ///
    StringPath<T> get(Path.StringRef<T> path);

    ///
    /// Gets the number path expression for the specified numeric property path.
    ///
    /// @param path Numeric property path
    /// @param <U> Numeric type
    /// @return Number path expression
    ///
    <U extends Number> NumberPath<T, U> get(Path.NumberRef<T, U> path);

    ///
    /// Creates a path expression for the specified path.
    ///
    /// @param path Property path
    /// @param <U> Property type
    /// @return Path expression
    ///
    <U> PathExpression<T, U> path(Path<T, U> path);

    ///
    /// Creates an entity path expression for the specified path.
    ///
    /// @param path Property path
    /// @param <U> Property type
    /// @return Entity path expression
    ///
    <U> EntityPath<T, U> entity(Path<T, U> path);

    ///
    /// Creates a string path expression for the specified string path.
    ///
    /// @param path String property path
    /// @return String path expression
    ///
    StringPath<T> string(Path<T, String> path);

    ///
    /// Creates a number path expression for the specified numeric path.
    ///
    /// @param path Numeric property path
    /// @param <U> Numeric type
    /// @return Number path expression
    ///
    <U extends Number> NumberPath<T, U> number(Path<T, U> path);

    ///
    /// Creates a boolean path expression for the specified boolean path.
    ///
    /// @param path Boolean property path
    /// @return Boolean path expression
    ///
    BooleanPath<T> bool(Path<T, Boolean> path);

    // type-unsafe
    ///
    /// Creates a path expression by field name (type-unsafe).
    ///
    /// @param fieldName Field name
    /// @param <U> Property type
    /// @return Path expression
    ///
    <U> PathExpression<T, U> path(String fieldName);

    ///
    /// Creates an entity path expression by field name (type-unsafe).
    ///
    /// @param fieldName Field name
    /// @param <U> Property type
    /// @return Entity path expression
    ///
    <U> EntityPath<T, U> entityPath(String fieldName);

    ///
    /// Creates a string path expression by field name (type-unsafe).
    ///
    /// @param fieldName Field name
    /// @return String path expression
    ///
    StringPath<T> stringPath(String fieldName);

    ///
    /// Creates a number path expression by field name (type-unsafe).
    ///
    /// @param fieldName Field name
    /// @param <U> Numeric type
    /// @return Number path expression
    ///
    <U extends Number> NumberPath<T, U> numberPath(String fieldName);

    ///
    /// Creates a boolean path expression by field name (type-unsafe).
    ///
    /// @param fieldName Field name
    /// @return Boolean path expression
    ///
    BooleanPath<T> booleanPath(String fieldName);

}
