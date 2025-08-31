package io.github.nextentity.core.expression;

import io.github.nextentity.api.Expression;
import io.github.nextentity.api.model.LockModeType;
import io.github.nextentity.api.model.Order;
import io.github.nextentity.api.model.Tuple;
import io.github.nextentity.core.util.ImmutableArray;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * @author HuangChengwei
 * @since 2024/4/17 下午1:28
 */
public interface QueryStructure extends Expression {

    Selected select();

    From from();

    Expression where();

    List<? extends Expression> groupBy();

    List<? extends Order<?>> orderBy();

    Expression having();

    Integer offset();

    Integer limit();

    LockModeType lockType();

    interface From extends Serializable {

        Class<?> type();

        interface FromEntity extends From {
        }

        interface FromSubQuery extends From, QueryStructure {
            @Override
            default Class<?> type() {
                return select().type();
            }

        }

    }

    interface Selected {
        Class<?> type();

        boolean distinct();

        class SelectPrimitive implements Selected {
            private Class<?> type = Object.class;
            private boolean distinct;
            private Expression expression;

            public SelectPrimitive() {
            }

            public Class<?> type() {
                return this.type;
            }

            public boolean distinct() {
                return this.distinct;
            }

            public Expression expression() {
                return this.expression;
            }

            public SelectPrimitive type(Class<?> type) {
                this.type = type;
                return this;
            }

            public SelectPrimitive distinct(boolean distinct) {
                this.distinct = distinct;
                return this;
            }

            public SelectPrimitive expression(Expression expression) {
                this.expression = expression;
                return this;
            }

            public boolean equals(final Object o) {
                if (o == this) return true;
                if (!(o instanceof SelectPrimitive other)) return false;
                if (!other.canEqual(this)) return false;
                final Object this$type = this.type();
                final Object other$type = other.type();
                if (!Objects.equals(this$type, other$type)) return false;
                if (this.distinct() != other.distinct()) return false;
                final Object this$expression = this.expression();
                final Object other$expression = other.expression();
                return Objects.equals(this$expression, other$expression);
            }

            protected boolean canEqual(final Object other) {
                return other instanceof SelectPrimitive;
            }

            public int hashCode() {
                final int PRIME = 59;
                int result = 1;
                final Object $type = this.type();
                result = result * PRIME + ($type == null ? 43 : $type.hashCode());
                result = result * PRIME + (this.distinct() ? 79 : 97);
                final Object $expression = this.expression();
                result = result * PRIME + ($expression == null ? 43 : $expression.hashCode());
                return result;
            }

            public String toString() {
                return "QueryStructure.Selected.SelectPrimitive(type=" + this.type() + ", distinct=" + this.distinct() + ", expression=" + this.expression() + ")";
            }
        }

        class SelectArray implements Selected {
            private boolean distinct;
            private ImmutableArray<? extends SelectPrimitive> items;

            public SelectArray() {
            }

            @Override
            public Class<?> type() {
                return Tuple.class;
            }

            public boolean distinct() {
                return this.distinct;
            }

            public ImmutableArray<? extends SelectPrimitive> items() {
                return this.items;
            }

            public SelectArray distinct(boolean distinct) {
                this.distinct = distinct;
                return this;
            }

            public SelectArray items(ImmutableArray<? extends SelectPrimitive> items) {
                this.items = items;
                return this;
            }

            public boolean equals(final Object o) {
                if (o == this) return true;
                if (!(o instanceof SelectArray other)) return false;
                if (!other.canEqual(this)) return false;
                if (this.distinct() != other.distinct()) return false;
                final Object this$items = this.items();
                final Object other$items = other.items();
                return Objects.equals(this$items, other$items);
            }

            protected boolean canEqual(final Object other) {
                return other instanceof SelectArray;
            }

            public int hashCode() {
                final int PRIME = 59;
                int result = 1;
                result = result * PRIME + (this.distinct() ? 79 : 97);
                final Object $items = this.items();
                result = result * PRIME + ($items == null ? 43 : $items.hashCode());
                return result;
            }

            public String toString() {
                return "QueryStructure.Selected.SelectArray(distinct=" + this.distinct() + ", items=" + this.items() + ")";
            }
        }

        class SelectEntity implements Selected {
            private Class<?> type;
            private boolean distinct;
            private Collection<? extends InternalPathExpression> fetch;

            public SelectEntity() {
            }

            public SelectEntity(SelectEntity select) {
                this.type = select.type();
                this.distinct = select.distinct();
                this.fetch = select.fetch();
            }

            public Class<?> type() {
                return this.type;
            }

            public boolean distinct() {
                return this.distinct;
            }

            public Collection<? extends InternalPathExpression> fetch() {
                return this.fetch;
            }

            public SelectEntity type(Class<?> type) {
                this.type = type;
                return this;
            }

            public SelectEntity distinct(boolean distinct) {
                this.distinct = distinct;
                return this;
            }

            public SelectEntity fetch(Collection<? extends InternalPathExpression> fetch) {
                this.fetch = fetch;
                return this;
            }

            public boolean equals(final Object o) {
                if (o == this) return true;
                if (!(o instanceof SelectEntity other)) return false;
                if (!other.canEqual(this)) return false;
                final Object this$type = this.type();
                final Object other$type = other.type();
                if (!Objects.equals(this$type, other$type)) return false;
                if (this.distinct() != other.distinct()) return false;
                final Object this$fetch = this.fetch();
                final Object other$fetch = other.fetch();
                return Objects.equals(this$fetch, other$fetch);
            }

            protected boolean canEqual(final Object other) {
                return other instanceof SelectEntity;
            }

            public int hashCode() {
                final int PRIME = 59;
                int result = 1;
                final Object $type = this.type();
                result = result * PRIME + ($type == null ? 43 : $type.hashCode());
                result = result * PRIME + (this.distinct() ? 79 : 97);
                final Object $fetch = this.fetch();
                result = result * PRIME + ($fetch == null ? 43 : $fetch.hashCode());
                return result;
            }

            public String toString() {
                return "QueryStructure.Selected.SelectEntity(type=" + this.type() + ", distinct=" + this.distinct() + ", fetch=" + this.fetch() + ")";
            }
        }

        class SelectProjection implements Selected {
            private Class<?> type;
            private Class<?> entityType;
            private boolean distinct;

            public SelectProjection() {
            }

            public Class<?> type() {
                return this.type;
            }

            public Class<?> entityType() {
                return this.entityType;
            }

            public boolean distinct() {
                return this.distinct;
            }

            public SelectProjection type(Class<?> type) {
                this.type = type;
                return this;
            }

            public SelectProjection entityType(Class<?> entityType) {
                this.entityType = entityType;
                return this;
            }

            public SelectProjection distinct(boolean distinct) {
                this.distinct = distinct;
                return this;
            }

            public boolean equals(final Object o) {
                if (o == this) return true;
                if (!(o instanceof SelectProjection other)) return false;
                if (!other.canEqual(this)) return false;
                final Object this$type = this.type();
                final Object other$type = other.type();
                if (!Objects.equals(this$type, other$type)) return false;
                final Object this$entityType = this.entityType();
                final Object other$entityType = other.entityType();
                if (!Objects.equals(this$entityType, other$entityType))
                    return false;
                return this.distinct() == other.distinct();
            }

            protected boolean canEqual(final Object other) {
                return other instanceof SelectProjection;
            }

            public int hashCode() {
                final int PRIME = 59;
                int result = 1;
                final Object $type = this.type();
                result = result * PRIME + ($type == null ? 43 : $type.hashCode());
                final Object $entityType = this.entityType();
                result = result * PRIME + ($entityType == null ? 43 : $entityType.hashCode());
                result = result * PRIME + (this.distinct() ? 79 : 97);
                return result;
            }

            public String toString() {
                return "QueryStructure.Selected.SelectProjection(type=" + this.type() + ", entityType=" + this.entityType() + ", distinct=" + this.distinct() + ")";
            }
        }


    }
}
