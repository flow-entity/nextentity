package io.github.nextentity.core.expression.impl;

import io.github.nextentity.api.Expression;
import io.github.nextentity.api.SortOrder;
import io.github.nextentity.api.model.Order;

record OrderImpl<T>(Expression expression, SortOrder order) implements Order<T> {
}
