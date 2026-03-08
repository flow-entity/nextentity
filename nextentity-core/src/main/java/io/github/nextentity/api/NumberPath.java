package io.github.nextentity.api;

/**
 * Number path interface, representing the path of the entity's numeric type attribute.
 * <p>
 * Extends NumberExpression and PathExpression, providing number expression operations and path expression functions.
 *
 * @param <T> Entity type
 * @param <U> Number type
 * @author HuangChengwei
 * @since 1.0.0
 */
public interface NumberPath<T, U extends Number> extends NumberExpression<T, U>, PathExpression<T, U> {
}
