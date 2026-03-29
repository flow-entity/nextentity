package io.github.nextentity.core.expression;

/**
 * Record representing a SELECT clause that selects a single expression.
 * <p>
 * Used for queries that select a single column or computed value.
 *
 * @param expression the expression to select
 * @param distinct whether to apply DISTINCT
 * @author HuangChengwei
 * @since 1.0.0
 */
public record SelectExpression(ExpressionNode expression, boolean distinct) implements Selected {
}
