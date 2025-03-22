package io.github.nextentity.core.expression.impl;

import io.github.nextentity.core.expression.Literal;

record LiteralImpl(Object value) implements Literal, AbstractExpression {
    static final Literal TRUE = new LiteralImpl(true);
    static final Literal FALSE = new LiteralImpl(false);
}
