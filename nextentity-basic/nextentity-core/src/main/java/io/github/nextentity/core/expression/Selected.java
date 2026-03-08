package io.github.nextentity.core.expression;

public sealed interface Selected permits SelectEntity, SelectExpression, SelectExpressions, SelectProjection {
    boolean distinct();
}
