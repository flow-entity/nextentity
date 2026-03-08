package io.github.nextentity.core.expression;

import io.github.nextentity.api.SortOrder;
import io.github.nextentity.api.TypedExpression;
import io.github.nextentity.api.model.Order;

public record OrderImpl<T>(TypedExpression<T, ?> expression, SortOrder order) implements Order<T> {


}
