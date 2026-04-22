package io.github.nextentity.core.expression;

import io.github.nextentity.api.SortOrder;
import io.github.nextentity.core.util.ImmutableList;
import jakarta.persistence.LockModeType;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

///
/// 单元测试 QueryStructure.
class QueryStructureTest {

    @Nested
    class FactoryMethods {

        ///
        /// 测试目标: 验证y of(Selected, From) creates structure with default values.
        /// 测试场景: Call of() with select and from.
        /// 预期结果: Returns QueryStructure with empty where, groupBy, orderBy, having and null offset/limit.
        @Test
        void of_WithSelectAndFrom_ShouldCreateWithDefaults() {
            // given
            Selected select = new SelectEntity(ImmutableList.empty(), false);
            From from = new FromEntity(String.class);

            // when
            QueryStructure result = QueryStructure.of(select, from);

            // then
            assertThat(result.select()).isSameAs(select);
            assertThat(result.from()).isSameAs(from);
            assertThat(result.where()).isInstanceOf(EmptyNode.class);
            assertThat(result.groupBy()).isEmpty();
            assertThat(result.orderBy()).isEmpty();
            assertThat(result.having()).isInstanceOf(EmptyNode.class);
            assertThat(result.offset()).isNull();
            assertThat(result.limit()).isNull();
            assertThat(result.lockType()).isEqualTo(LockModeType.NONE);
        }

        ///
        /// 测试目标: 验证y of(Class) creates structure from entity type.
        /// 测试场景: Call of() with entity class.
        /// 预期结果: Returns QueryStructure with SelectEntity and FromEntity.
        @Test
        void of_WithClass_ShouldCreateFromEntityType() {
            // when
            QueryStructure result = QueryStructure.of(String.class);

            // then
            assertThat(result.select()).isInstanceOf(SelectEntity.class);
            assertThat(result.from()).isInstanceOf(FromEntity.class);
            assertThat(((FromEntity) result.from()).type()).isEqualTo(String.class);
        }
    }

    @Nested
    class SelectMethod {

        ///
        /// 测试目标: 验证y select() updates select clause.
        /// 测试场景: Call select() with new Selected.
        /// 预期结果: Returns new QueryStructure with updated select.
        @Test
        void select_ShouldReturnNewInstanceWithUpdatedSelect() {
            // given
            QueryStructure original = QueryStructure.of(String.class);
            Selected newSelect = new SelectProjection(String.class, false);

            // when
            QueryStructure result = original.select(newSelect);

            // then
            assertThat(result.select()).isSameAs(newSelect);
            assertThat(result.from()).isSameAs(original.from());
            assertThat(original.select()).isNotSameAs(newSelect);
        }
    }

    @Nested
    class FromMethod {

        ///
        /// 测试目标: 验证y from() updates from clause.
        /// 测试场景: Call from() with new From.
        /// 预期结果: Returns new QueryStructure with updated from.
        @Test
        void from_ShouldReturnNewInstanceWithUpdatedFrom() {
            // given
            QueryStructure original = QueryStructure.of(String.class);
            From newFrom = new FromEntity(Integer.class);

            // when
            QueryStructure result = original.from(newFrom);

            // then
            assertThat(result.from()).isSameAs(newFrom);
            assertThat(original.from()).isNotSameAs(newFrom);
        }
    }

    @Nested
    class WhereMethod {

        ///
        /// 测试目标: 验证y where() updates where clause.
        /// 测试场景: Call where() with ExpressionNode.
        /// 预期结果: Returns new QueryStructure with updated where.
        @Test
        void where_ShouldReturnNewInstanceWithUpdatedWhere() {
            // given
            QueryStructure original = QueryStructure.of(String.class);
            ExpressionNode newWhere = new LiteralNode("test");

            // when
            QueryStructure result = original.where(newWhere);

            // then
            assertThat(result.where()).isSameAs(newWhere);
            assertThat(original.where()).isInstanceOf(EmptyNode.class);
        }
    }

    @Nested
    class GroupByMethod {

        ///
        /// 测试目标: 验证y groupBy() updates group by clause.
        /// 测试场景: Call groupBy() with list of expressions.
        /// 预期结果: Returns new QueryStructure with updated groupBy.
        @Test
        void groupBy_ShouldReturnNewInstanceWithUpdatedGroupBy() {
            // given
            QueryStructure original = QueryStructure.of(String.class);
            ImmutableList<ExpressionNode> newGroupBy = ImmutableList.of(new LiteralNode("col1"));

            // when
            QueryStructure result = original.groupBy(newGroupBy);

            // then
            assertThat(result.groupBy()).isSameAs(newGroupBy);
            assertThat(original.groupBy()).isEmpty();
        }
    }

    @Nested
    class OrderByMethod {

        ///
        /// 测试目标: 验证y orderBy() updates order by clause.
        /// 测试场景: Call orderBy() with list of sort expressions.
        /// 预期结果: Returns new QueryStructure with updated orderBy.
        @Test
        void orderBy_ShouldReturnNewInstanceWithUpdatedOrderBy() {
            // given
            QueryStructure original = QueryStructure.of(String.class);
            SortExpression sortExpr = new SortExpression(new LiteralNode("id"), SortOrder.ASC);
            ImmutableList<SortExpression> newOrderBy = ImmutableList.of(sortExpr);

            // when
            QueryStructure result = original.orderBy(newOrderBy);

            // then
            assertThat(result.orderBy()).isSameAs(newOrderBy);
            assertThat(original.orderBy()).isEmpty();
        }
    }

    @Nested
    class HavingMethod {

        ///
        /// 测试目标: 验证y having() updates having clause.
        /// 测试场景: Call having() with ExpressionNode.
        /// 预期结果: Returns new QueryStructure with updated having.
        @Test
        void having_ShouldReturnNewInstanceWithUpdatedHaving() {
            // given
            QueryStructure original = QueryStructure.of(String.class);
            ExpressionNode newHaving = new LiteralNode("count > 0");

            // when
            QueryStructure result = original.having(newHaving);

            // then
            assertThat(result.having()).isSameAs(newHaving);
            assertThat(original.having()).isInstanceOf(EmptyNode.class);
        }
    }

    @Nested
    class OffsetMethod {

        ///
        /// 测试目标: 验证y offset() updates offset value.
        /// 测试场景: Call offset() with integer value.
        /// 预期结果: Returns new QueryStructure with updated offset.
        @Test
        void offset_ShouldReturnNewInstanceWithUpdatedOffset() {
            // given
            QueryStructure original = QueryStructure.of(String.class);

            // when
            QueryStructure result = original.offset(10);

            // then
            assertThat(result.offset()).isEqualTo(10);
            assertThat(original.offset()).isNull();
        }
    }

    @Nested
    class LimitMethod {

        ///
        /// 测试目标: 验证y limit() updates limit value.
        /// 测试场景: Call limit() with integer value.
        /// 预期结果: Returns new QueryStructure with updated limit.
        @Test
        void limit_ShouldReturnNewInstanceWithUpdatedLimit() {
            // given
            QueryStructure original = QueryStructure.of(String.class);

            // when
            QueryStructure result = original.limit(20);

            // then
            assertThat(result.limit()).isEqualTo(20);
            assertThat(original.limit()).isNull();
        }
    }

    @Nested
    class LockTypeMethod {

        ///
        /// 测试目标: 验证y lockType() updates lock type.
        /// 测试场景: Call lockType() with LockModeType.
        /// 预期结果: Returns new QueryStructure with updated lockType.
        @Test
        void lockType_ShouldReturnNewInstanceWithUpdatedLockType() {
            // given
            QueryStructure original = QueryStructure.of(String.class);

            // when
            QueryStructure result = original.lockType(LockModeType.PESSIMISTIC_WRITE);

            // then
            assertThat(result.lockType()).isEqualTo(LockModeType.PESSIMISTIC_WRITE);
            assertThat(original.lockType()).isEqualTo(LockModeType.NONE);
        }
    }

    @Nested
    class RemoveOffsetLimitMethod {

        ///
        /// 测试目标: 验证y removeOffsetLimit() removes both offset and limit.
        /// 测试场景: Call removeOffsetLimit() when both offset and limit are set.
        /// 预期结果: Returns new QueryStructure with null offset and limit.
        @Test
        void removeOffsetLimit_WhenBothSet_ShouldRemoveBoth() {
            // given
            QueryStructure original = QueryStructure.of(String.class).offset(10).limit(20);

            // when
            QueryStructure result = original.removeOffsetLimit();

            // then
            assertThat(result.offset()).isNull();
            assertThat(result.limit()).isNull();
        }

        ///
        /// 测试目标: 验证y removeOffsetLimit() returns same instance when both are null.
        /// 测试场景: Call removeOffsetLimit() when both offset and limit are null.
        /// 预期结果: Returns same instance.
        @Test
        void removeOffsetLimit_WhenBothNull_ShouldReturnSameInstance() {
            // given
            QueryStructure original = QueryStructure.of(String.class);

            // when
            QueryStructure result = original.removeOffsetLimit();

            // then
            assertThat(result).isSameAs(original);
        }
    }
}
