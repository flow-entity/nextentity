package io.github.nextentity.core.expression;

/**
 * Sealed interface representing the FROM clause of a query.
 * <p>
 * Permitted subtypes:
 * <ul>
 *   <li>{@link FromEntity} - select from an entity table</li>
 *   <li>{@link FromSubQuery} - select from a subquery</li>
 * </ul>
 *
 * @author HuangChengwei
 * @since 1.0.0
 */
public sealed interface From permits FromEntity, FromSubQuery {
}
