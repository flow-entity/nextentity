package io.github.nextentity.api.model;

import io.github.nextentity.api.*;

public interface EntityRoot<T> {

    <U> TypedExpression<T, U> literal(U value);

    <U> EntityPath<T, U> get(Path<T, U> path);

    BooleanPath<T> get(Path.BooleanRef<T> path);

    StringPath<T> get(Path.StringRef<T> path);

    <U extends Number> NumberPath<T, U> get(Path.NumberRef<T, U> path);

    <U> PathExpression<T, U> path(Path<T, U> path);

    <U> EntityPath<T, U> entity(Path<T, U> path);

    StringPath<T> string(Path<T, String> path);

    <U extends Number> NumberPath<T, U> number(Path<T, U> path);

    BooleanPath<T> bool(Path<T, Boolean> path);


    // type-unsafe

    <U> PathExpression<T, U> path(String fieldName);

    <U> EntityPath<T, U> entityPath(String fieldName);

    StringPath<T> stringPath(String fieldName);

    <U extends Number> NumberPath<T, U> numberPath(String fieldName);

    BooleanPath<T> booleanPath(String fieldName);

}
