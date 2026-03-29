package io.github.nextentity.api;

/**
 * Entity path interface, representing the association path between entities.
 * <p>
 * Extends PathExpression, used to handle association property access between entities.
 *
 * @param <T> Entity type
 * @param <U> Property type
 * @author HuangChengwei
 * @since 1.0.0
 */
public interface EntityPath<T, U> extends PathExpression<T, U> {
    /**
     * Gets the sub-path of the specified path.
     *
     * @param path Path
     * @param <R> Result type
     * @return Sub-path
     */
    <R> EntityPath<T, R> get(Path<U, R> path);

    /**
     * Gets the string path of the specified string reference.
     *
     * @param path String reference
     * @return String path
     */
    StringPath<T> get(Path.StringRef<U> path);

    /**
     * Gets the number path of the specified number reference.
     *
     * @param path Number reference
     * @param <R> Number type
     * @return Number path
     */
    <R extends Number> NumberPath<T, R> get(Path.NumberRef<U, R> path);

    /**
     * Gets the sub-path expression of the specified path expression.
     *
     * @param path Path expression
     * @param <R> Result type
     * @return Sub-path expression
     */
    <R> PathExpression<T, R> get(PathExpression<U, R> path);

    /**
     * Gets the string path of the specified string path.
     *
     * @param path String path
     * @return String path
     */
    StringPath<T> get(StringPath<U> path);

    /**
     * Gets the boolean path of the specified boolean reference.
     *
     * @param path Boolean reference
     * @return Boolean path
     */
    BooleanPath<T> get(Path.BooleanRef<T> path);

    /**
     * Gets the number path of the specified number path.
     *
     * @param path Number path
     * @param <R> Number type
     * @return Number path
     */
    <R extends Number> NumberPath<T, R> get(NumberPath<U, R> path);

}
