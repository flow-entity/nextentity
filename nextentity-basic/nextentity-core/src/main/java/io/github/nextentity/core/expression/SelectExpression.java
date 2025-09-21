package io.github.nextentity.core.expression;

public record SelectExpression(ExpressionNode expression, boolean distinct) implements Selected {
}
