package io.github.nextentity.core.expression;

import io.github.nextentity.core.util.ImmutableArray;

public record SelectExpressions(ImmutableArray<ExpressionNode> items, boolean distinct) implements Selected {
}
