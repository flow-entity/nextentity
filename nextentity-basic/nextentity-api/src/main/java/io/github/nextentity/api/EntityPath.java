package io.github.nextentity.api;

public interface EntityPath<T, U> extends PathExpression<T, U> {
    <R> io.github.nextentity.api.EntityPath<T, R> get(Path<U, R> path);

    StringPath<T> get(Path.StringRef<U> path);

    <R extends Number> NumberPath<T, R> get(Path.NumberRef<U, R> path);

    <R> PathExpression<T, R> get(PathExpression<U, R> path);

    StringPath<T> get(StringPath<U> path);

    BooleanPath<T> get(Path.BooleanRef<T> path);

    <R extends Number> NumberPath<T, R> get(NumberPath<U, R> path);

}
