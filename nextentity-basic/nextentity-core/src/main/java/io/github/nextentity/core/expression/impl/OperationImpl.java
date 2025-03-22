package io.github.nextentity.core.expression.impl;

import io.github.nextentity.api.Expression;
import io.github.nextentity.core.expression.Operation;
import io.github.nextentity.core.expression.Operator;

import java.util.List;

record OperationImpl(List<? extends Expression> operands, Operator operator) implements Operation, AbstractExpression {
}
