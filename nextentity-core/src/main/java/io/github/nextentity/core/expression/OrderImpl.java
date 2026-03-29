package io.github.nextentity.core.expression;

import io.github.nextentity.api.Expression;
import io.github.nextentity.api.SortOrder;
import io.github.nextentity.api.model.Order;

public record OrderImpl<T>(Expression<T, ?> expression, SortOrder order) implements Order<T> {


}
