package io.github.nextentity.core.expression;

import io.github.nextentity.api.OrderOperator;
import io.github.nextentity.api.OrderByStep;
import io.github.nextentity.api.SortOrder;
import io.github.nextentity.core.WhereImpl;
import io.github.nextentity.core.util.ImmutableList;

public class OrderOperatorImpl<T, U> implements OrderOperator<T, U> {
    private final WhereImpl<T, U> builder;
    private final ImmutableList<ExpressionNode> orders;

    public OrderOperatorImpl(WhereImpl<T, U> builder, ImmutableList<ExpressionNode> orders) {
        this.builder = builder;
        this.orders = orders;
    }

    @Override
    public OrderByStep<T, U> sort(SortOrder order) {
        ImmutableList<SortExpression> s0 = builder.getQueryStructure().orderBy();
        SortExpression[] res = new SortExpression[s0.size() + orders.size()];
        int i = 0;
        for (SortExpression sortExpression : s0) {
            res[i++] = sortExpression;
        }
        for (ExpressionNode expressionNode : orders) {
            res[i++] = new SortExpression(expressionNode, order);
        }
        return builder.update(builder.getQueryStructure().orderBy(ImmutableList.of(res)));
    }

}
