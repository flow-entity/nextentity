package io.github.nextentity.core.expression.impl;

import io.github.nextentity.api.Expression;
import io.github.nextentity.api.model.LockModeType;
import io.github.nextentity.api.model.Order;
import io.github.nextentity.core.expression.QueryStructure;
import io.github.nextentity.core.expression.QueryStructure.From.FromSubQuery;
import io.github.nextentity.core.expression.QueryStructure.Selected.SelectEntity;
import io.github.nextentity.core.meta.EntitySchema;
import io.github.nextentity.core.util.ImmutableList;

import java.util.List;
import java.util.Objects;

class QueryStructureImpl implements FromSubQuery {

    private final Selected select;

    private final From from;

    private final Expression where;

    private final List<? extends Expression> groupBy;

    private final List<? extends Order<?>> orderBy;

    private final Expression having;

    private final Integer offset;

    private final Integer limit;

    private final LockModeType lockType;

    public QueryStructureImpl(QueryStructure queryStructure) {
        this(
                queryStructure.select(),
                queryStructure.from(),
                queryStructure.where(),
                queryStructure.groupBy(),
                queryStructure.orderBy(),
                queryStructure.having(),
                queryStructure.offset(),
                queryStructure.limit(),
                queryStructure.lockType()
        );
    }

    public QueryStructureImpl(Selected select, From from) {
        this(select,
                from,
                ExpressionImpls.TRUE,
                ImmutableList.of(),
                ImmutableList.of(),
                ExpressionImpls.TRUE,
                null,
                null,
                LockModeType.NONE);
    }

    public QueryStructureImpl(Selected select,
                              From from,
                              Expression where,
                              List<? extends Expression> groupBy,
                              List<? extends Order<?>> orderBy,
                              Expression having,
                              Integer offset,
                              Integer limit,
                              LockModeType lockType) {
        this.select = select;
        this.from = from;
        this.where = where;
        this.groupBy = groupBy;
        this.orderBy = orderBy;
        this.having = having;
        this.offset = offset;
        this.limit = limit;
        this.lockType = lockType;
    }


    public QueryStructureImpl(Class<?> entityType) {
        this(new SelectEntity().type(entityType), new FromEntityImpl(entityType));
    }

    public QueryStructureImpl(EntitySchema entityType) {
        this(entityType.type());
    }

    public Selected select() {
        return this.select;
    }

    public From from() {
        return this.from;
    }

    public Expression where() {
        return this.where;
    }

    public List<? extends Expression> groupBy() {
        return this.groupBy;
    }

    public List<? extends Order<?>> orderBy() {
        return this.orderBy;
    }

    public Expression having() {
        return this.having;
    }

    public Integer offset() {
        return this.offset;
    }

    public Integer limit() {
        return this.limit;
    }

    public LockModeType lockType() {
        return this.lockType;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof QueryStructureImpl other)) return false;
        if (!other.canEqual(this)) return false;
        final Object this$select = this.select();
        final Object other$select = other.select();
        if (!Objects.equals(this$select, other$select)) return false;
        final Object this$from = this.from();
        final Object other$from = other.from();
        if (!Objects.equals(this$from, other$from)) return false;
        final Object this$where = this.where();
        final Object other$where = other.where();
        if (!Objects.equals(this$where, other$where)) return false;
        final Object this$groupBy = this.groupBy();
        final Object other$groupBy = other.groupBy();
        if (!Objects.equals(this$groupBy, other$groupBy)) return false;
        final Object this$orderBy = this.orderBy();
        final Object other$orderBy = other.orderBy();
        if (!Objects.equals(this$orderBy, other$orderBy)) return false;
        final Object this$having = this.having();
        final Object other$having = other.having();
        if (!Objects.equals(this$having, other$having)) return false;
        final Object this$offset = this.offset();
        final Object other$offset = other.offset();
        if (!Objects.equals(this$offset, other$offset)) return false;
        final Object this$limit = this.limit();
        final Object other$limit = other.limit();
        if (!Objects.equals(this$limit, other$limit)) return false;
        final Object this$lockType = this.lockType();
        final Object other$lockType = other.lockType();
        return Objects.equals(this$lockType, other$lockType);
    }

    protected boolean canEqual(final Object other) {
        return other instanceof QueryStructureImpl;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $select = this.select();
        result = result * PRIME + ($select == null ? 43 : $select.hashCode());
        final Object $from = this.from();
        result = result * PRIME + ($from == null ? 43 : $from.hashCode());
        final Object $where = this.where();
        result = result * PRIME + ($where == null ? 43 : $where.hashCode());
        final Object $groupBy = this.groupBy();
        result = result * PRIME + ($groupBy == null ? 43 : $groupBy.hashCode());
        final Object $orderBy = this.orderBy();
        result = result * PRIME + ($orderBy == null ? 43 : $orderBy.hashCode());
        final Object $having = this.having();
        result = result * PRIME + ($having == null ? 43 : $having.hashCode());
        final Object $offset = this.offset();
        result = result * PRIME + ($offset == null ? 43 : $offset.hashCode());
        final Object $limit = this.limit();
        result = result * PRIME + ($limit == null ? 43 : $limit.hashCode());
        final Object $lockType = this.lockType();
        result = result * PRIME + ($lockType == null ? 43 : $lockType.hashCode());
        return result;
    }

    public String toString() {
        return "QueryStructureImpl(select=" + this.select() + ", from=" + this.from() + ", where=" + this.where() + ", groupBy=" + this.groupBy() + ", orderBy=" + this.orderBy() + ", having=" + this.having() + ", offset=" + this.offset() + ", limit=" + this.limit() + ", lockType=" + this.lockType() + ")";
    }
}
