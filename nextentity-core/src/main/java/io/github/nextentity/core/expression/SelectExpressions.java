package io.github.nextentity.core.expression;

import io.github.nextentity.core.util.ImmutableArray;

///
/// Record representing a SELECT clause that selects multiple expressions.
/// <p>
/// Used for queries that select multiple columns or computed values.
///
/// @param items the expressions to select
/// @param distinct whether to apply DISTINCT
/// @author HuangChengwei
/// @since 1.0.0
///
public record SelectExpressions(ImmutableArray<ExpressionNode> items, boolean distinct) implements Selected {
}
