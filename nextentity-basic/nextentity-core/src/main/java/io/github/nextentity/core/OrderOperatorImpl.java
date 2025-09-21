package io.github.nextentity.core;

import io.github.nextentity.api.*;
import io.github.nextentity.api.model.EntityRoot;
import io.github.nextentity.api.model.Order;
import io.github.nextentity.core.expression.PathNode;
import io.github.nextentity.core.expression.SortExpression;
import io.github.nextentity.core.util.ImmutableList;
import io.github.nextentity.core.util.Paths;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class OrderOperatorImpl<T, U> implements OrderOperator<T, U> {
    private final WhereImpl<T, U> builder;
    private final Collection<Path<T, Comparable<?>>> orderByPaths;

    public OrderOperatorImpl(WhereImpl<T, U> builder, Collection<Path<T, Comparable<?>>> orderByPaths) {
        this.builder = builder;
        this.orderByPaths = orderByPaths;
    }

    @NotNull
    private List<SortExpression> asOrderList(SortOrder sort) {
        return orderByPaths
                .stream()
                .map(path -> new SortExpression(PathNode.of(path), sort))
                .collect(ImmutableList.collector(orderByPaths.size()));
    }

    @Override
    public SelectOrderByStep<T, U> sort(SortOrder order) {
        return builder.addOrderBy(asOrderList(order));
    }

    @Override
    public EntityRoot<T> root() {
        return Paths.root();
    }
}
