package io.github.nextentity.api.model;

import io.github.nextentity.api.Expression;
import io.github.nextentity.api.SortOrder;

import java.io.Serializable;

/**
 * Order interface, defining the expression and sort order for ordering.
 *
 * @param <T> Entity type
 * @author HuangChengwei
 * @since 1.0.0
 */
public interface Order<T> extends Serializable {

    /**
     * Gets the sort expression.
     *
     * @return Sort expression
     */
    Expression expression();

    /**
     * Gets the sort order.
     *
     * @return Sort order
     */
    SortOrder order();

}
