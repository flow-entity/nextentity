package io.github.nextentity.core;

import io.github.nextentity.api.OrderOperator;
import io.github.nextentity.api.Path;
import io.github.nextentity.api.OrderByStep;
import io.github.nextentity.api.SortOrder;
import io.github.nextentity.api.model.EntityRoot;
import io.github.nextentity.core.expression.PathNode;
import io.github.nextentity.core.expression.SortExpression;
import io.github.nextentity.core.util.ImmutableList;
import io.github.nextentity.core.util.Paths;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.List;

public class OrderOperatorImpl<T, U> implements OrderOperator<T, U> {
    private final WhereImpl<T, U> builder;
    private final Collection<Path<T, Comparable<?>>> orderByPaths;

    public OrderOperatorImpl(WhereImpl<T, U> builder, Collection<Path<T, Comparable<?>>> orderByPaths) {
        this.builder = builder;
        this.orderByPaths = orderByPaths;
    }

    @NonNull
    private List<SortExpression> asOrderList(SortOrder sort) {
        return orderByPaths
                .stream()
                .map(path -> new SortExpression(PathNode.of(path), sort))
                .collect(ImmutableList.collector(orderByPaths.size()));
    }

    @Override
    public OrderByStep<T, U> sort(SortOrder order) {
        return builder.addOrderBy(asOrderList(order));
    }

    public EntityRoot<T> root() {
        return Paths.root();
    }
}
