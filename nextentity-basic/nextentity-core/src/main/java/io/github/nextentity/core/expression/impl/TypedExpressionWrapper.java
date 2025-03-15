package io.github.nextentity.core.expression.impl;

import io.github.nextentity.api.TypedExpression;

public interface TypedExpressionWrapper {

    TypedExpression<?, ?> unwrap();

}
