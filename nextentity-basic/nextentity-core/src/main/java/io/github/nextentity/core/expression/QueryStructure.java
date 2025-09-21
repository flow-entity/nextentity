package io.github.nextentity.core.expression;

import io.github.nextentity.api.model.LockModeType;
import io.github.nextentity.core.util.ImmutableList;

public record QueryStructure(

        Selected select,

        From from,

        ExpressionNode where,

        ImmutableList<ExpressionNode> groupBy,

        ImmutableList<SortExpression> orderBy,

        ExpressionNode having,

        Integer offset,

        Integer limit,

        LockModeType lockType

) implements ExpressionNode {

    public static QueryStructure of(Selected select, From from) {
        return new QueryStructure(
                select,
                from,
                EmptyNode.INSTANCE,
                ImmutableList.of(),
                ImmutableList.of(),
                EmptyNode.INSTANCE,
                null,
                null,
                LockModeType.NONE);
    }

    public static QueryStructure of(Class<?> type) {
        return new QueryStructure(
                new SelectEntity(ImmutableList.empty(), false),
                new FromEntity(type),
                EmptyNode.INSTANCE,
                ImmutableList.of(),
                ImmutableList.of(),
                EmptyNode.INSTANCE,
                null,
                null,
                LockModeType.NONE);
    }

    public QueryStructure selectFrom(Selected select, From from) {
        return new QueryStructure(select, from, where, groupBy, orderBy, having, offset, limit, lockType);
    }

    public QueryStructure select(Selected select) {
        return new QueryStructure(select, from, where, groupBy, orderBy, having, offset, limit, lockType);
    }

    public QueryStructure from(From from) {
        return new QueryStructure(select, from, where, groupBy, orderBy, having, offset, limit, lockType);
    }

    public QueryStructure where(ExpressionNode where) {
        return new QueryStructure(select, from, where, groupBy, orderBy, having, offset, limit, lockType);
    }

    public QueryStructure groupBy(ImmutableList<ExpressionNode> groupBy) {
        return new QueryStructure(select, from, where, groupBy, orderBy, having, offset, limit, lockType);
    }

    public QueryStructure orderBy(ImmutableList<SortExpression> orderBy) {
        return new QueryStructure(select, from, where, groupBy, orderBy, having, offset, limit, lockType);
    }

    public QueryStructure having(ExpressionNode having) {
        return new QueryStructure(select, from, where, groupBy, orderBy, having, offset, limit, lockType);
    }

    public QueryStructure offset(Integer offset) {
        return new QueryStructure(select, from, where, groupBy, orderBy, having, offset, limit, lockType);
    }

    public QueryStructure limit(Integer limit) {
        return new QueryStructure(select, from, where, groupBy, orderBy, having, offset, limit, lockType);
    }

    public QueryStructure lockType(LockModeType lockType) {
        return new QueryStructure(select, from, where, groupBy, orderBy, having, offset, limit, lockType);
    }

    public QueryStructure removeOffsetLimit() {
        if (offset == null && limit == null) {
            return this;
        }
        return new QueryStructure(select, from, where, groupBy, orderBy, having, null, null, lockType);
    }
}