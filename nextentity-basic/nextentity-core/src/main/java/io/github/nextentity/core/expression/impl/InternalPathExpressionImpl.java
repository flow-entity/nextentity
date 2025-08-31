package io.github.nextentity.core.expression.impl;

import java.util.Arrays;

record InternalPathExpressionImpl(String[] paths) implements AbstractInternalPathExpression, AbstractExpression {
    @Override
    public String toString() {
        return "InternalPathExpressionImpl{" +
               "paths=" + Arrays.toString(paths) +
               '}';
    }
}
