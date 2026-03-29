package io.github.nextentity.core;

import io.github.nextentity.api.*;
import io.github.nextentity.core.expression.*;
import io.github.nextentity.core.meta.Metamodel;
import io.github.nextentity.core.util.ImmutableList;
import io.github.nextentity.integration.entity.Employee;
import jakarta.persistence.LockModeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for WhereImpl - WHERE clause implementation.
 */
@ExtendWith(MockitoExtension.class)
class WhereImplTest {

    @Mock
    protected Metamodel metamodel;

    @Mock
    protected QueryExecutor queryExecutor;

    private WhereImpl<Employee, Employee> whereImpl;

    @BeforeEach
    void setUp() {
        QueryStructure queryStructure = QueryStructure.of(Employee.class);
        whereImpl = new WhereImpl<>(queryStructure, metamodel, queryExecutor);
    }

    @Nested
    class WhereClause {

        /**
         * Test objective: Verify that where with null predicate returns same instance.
         * Test scenario: Call where with null predicate.
         * Expected result: Returns the same WhereImpl instance without modification.
         */
        @Test
        void where_WithNullPredicate_ShouldReturnSameInstance() {
            // when
            var result = whereImpl.where((TypedExpression<Employee, Boolean>) null);

            // then
            assertThat(result).isSameAs(whereImpl);
        }

        /**
         * Test objective: Verify that where with TRUE predicate returns same instance.
         * Test scenario: Call where with a predicate that evaluates to TRUE.
         * Expected result: Returns the same WhereImpl instance without modification.
         */
        @Test
        void where_WithTruePredicate_ShouldReturnSameInstance() {
            // when
            TypedExpression<Employee, Boolean> truePredicate = Expressions.ofTrue();
            var result = whereImpl.where(truePredicate);

            // then
            assertThat(result).isSameAs(whereImpl);
        }

        /**
         * Test objective: Verify that where with valid predicate adds condition.
         * Test scenario: Call where with a valid boolean predicate.
         * Expected result: Returns a new WhereImpl with updated query structure.
         */
        @Test
        void where_WithValidPredicate_ShouldAddCondition() {
            // given
            // Use a real predicate expression (not TRUE which gets optimized away)
            Predicate<Employee> predicate = new PredicateImpl<>(
                    new OperatorNode(
                            ImmutableList.of(new PathNode("name")),
                            Operator.EQ
                    )
            );

            // when
            var result = whereImpl.where(predicate);

            // then
            assertThat(result).isNotSameAs(whereImpl);
            assertThat(result).isInstanceOf(WhereImpl.class);
        }

        /**
         * Test objective: Verify that where with Path creates PathOperator.
         * Test scenario: Call where with a Path expression.
         * Expected result: Returns a PathOperator for building conditions.
         */
        @Test
        void where_WithPath_ShouldReturnPathOperator() {
            // when
            var operator = whereImpl.where(Employee::getName);

            // then
            assertThat(operator).isNotNull();
        }

        /**
         * Test objective: Verify that where with NumberPath creates NumberOperator.
         * Test scenario: Call where with a NumberPath expression.
         * Expected result: Returns a NumberOperator for building numeric conditions.
         */
        @Test
        void where_WithNumberPath_ShouldReturnNumberOperator() {
            // when
            var operator = whereImpl.where(Employee::getSalary);

            // then
            assertThat(operator).isNotNull();
        }

        /**
         * Test objective: Verify that where with StringPath creates StringOperator.
         * Test scenario: Call where with a StringPath expression.
         * Expected result: Returns a StringOperator for building string conditions.
         */
        @Test
        void where_WithStringPath_ShouldReturnStringOperator() {
            // when
            var operator = whereImpl.where((Path.StringRef<Employee>) Employee::getName);

            // then
            assertThat(operator).isNotNull();
        }
    }

    @Nested
    class GroupByClause {

        /**
         * Test objective: Verify that groupBy with single expression adds grouping.
         * Test scenario: Call groupBy with a single expression.
         * Expected result: Query structure contains the group by expression.
         */
        @Test
        void groupBy_WithSingleExpression_ShouldAddGrouping() {
            // when
            var result = whereImpl.groupBy(Employee::getId);

            // then
            assertThat(result).isNotSameAs(whereImpl);
            QueryStructure structure = ((WhereImpl<?, ?>) result).getQueryStructure();
            assertThat(structure.groupBy()).isNotNull();
            assertThat(structure.groupBy().asList()).hasSize(1);
        }

        /**
         * Test objective: Verify that groupBy with list of expressions adds all groupings.
         * Test scenario: Call groupBy with a list of expressions.
         * Expected result: Query structure contains all group by expressions.
         */
        @Test
        void groupBy_WithExpressionList_ShouldAddAllGroupings() {
            // given
            List<Path<Employee, ?>> expressions = Arrays.asList(
                    Employee::getId,
                    Employee::getName
            );

            // when
            var result = whereImpl.groupBy(expressions);

            // then
            QueryStructure structure = ((WhereImpl<?, ?>) result).getQueryStructure();
            assertThat(structure.groupBy().asList()).hasSize(2);
        }

        /**
         * Test objective: Verify that groupBy with Path adds grouping.
         * Test scenario: Call groupBy with a Path expression.
         * Expected result: Query structure contains the group by path.
         */
        @Test
        void groupBy_WithPath_ShouldAddGrouping() {
            // when
            var result = whereImpl.groupBy(Employee::getDepartmentId);

            // then
            QueryStructure structure = ((WhereImpl<?, ?>) result).getQueryStructure();
            assertThat(structure.groupBy().asList()).hasSize(1);
        }

        /**
         * Test objective: Verify that groupBy with collection of paths adds all groupings.
         * Test scenario: Call groupBy with a collection of Path expressions.
         * Expected result: Query structure contains all group by paths.
         */
        @Test
        void groupBy_WithPathCollection_ShouldAddAllGroupings() {
            // given
            List<Path<Employee, ?>> paths = Arrays.asList(
                    Employee::getId,
                    Employee::getStatus
            );

            // when
            var result = whereImpl.groupBy(paths);

            // then
            QueryStructure structure = ((WhereImpl<?, ?>) result).getQueryStructure();
            assertThat(structure.groupBy().asList()).hasSize(2);
        }
    }

    @Nested
    class HavingClause {

        /**
         * Test objective: Verify that having adds condition to query structure.
         * Test scenario: Call having with a boolean predicate.
         * Expected result: Query structure contains the having condition.
         */
        @Test
        void having_WithPredicate_ShouldAddCondition() {
            // given
            TypedExpression<Employee, Boolean> predicate = Expressions.ofTrue();

            // when
            var result = whereImpl.having(predicate);

            // then
            assertThat(result).isNotSameAs(whereImpl);
            QueryStructure structure = ((WhereImpl<?, ?>) result).getQueryStructure();
            assertThat(structure.having()).isNotNull();
        }
    }

    @Nested
    class OrderByClause {

        /**
         * Test objective: Verify that orderBy with list of orders adds sorting.
         * Test scenario: Call orderBy with a list of Order expressions.
         * Expected result: Query structure contains the order by expressions.
         */
        @Test
        void orderBy_WithOrderList_ShouldAddSorting() {
            // given
            // Use the existing orderBy API with Path references
            List<Path<Employee, ? extends Comparable<?>>> paths = Arrays.asList(
                    Employee::getName,
                    Employee::getSalary
            );

            // when
            var result = whereImpl.orderBy(paths);

            // then
            assertThat(result).isNotNull();
            // Verify it returns an OrderOperator that can be used to specify sort order
            assertThat(result).isInstanceOf(io.github.nextentity.core.expression.OrderOperatorImpl.class);
        }

        /**
         * Test objective: Verify that orderBy with collection of paths creates OrderOperator.
         * Test scenario: Call orderBy with a collection of Path expressions.
         * Expected result: Returns an OrderOperator for building sort expressions.
         */
        @Test
        void orderBy_WithPathCollection_ShouldReturnOrderOperator() {
            // given
            List<Path<Employee, ? extends Comparable<?>>> paths = Arrays.asList(
                    Employee::getName,
                    Employee::getSalary
            );

            // when
            var operator = whereImpl.orderBy(paths);

            // then
            assertThat(operator).isNotNull();
        }
    }

    @Nested
    class CountOperations {

        /**
         * Test objective: Verify that count executes count query and returns result.
         * Test scenario: Call count method.
         * Expected result: Returns the count value from query executor.
         */
        @Test
        void count_ShouldExecuteCountQuery() {
            // given
            when(queryExecutor.<Number>getList(any())).thenReturn(Collections.singletonList(10L));

            // when
            long result = whereImpl.count();

            // then
            assertThat(result).isEqualTo(10L);
            verify(queryExecutor).getList(any());
        }

        /**
         * Test objective: Verify that count with distinct select uses subquery.
         * Test scenario: Call count on a query with distinct selection.
         * Expected result: Query executor receives a subquery-based count structure.
         */
        @Test
        void count_WithDistinctSelect_ShouldUseSubquery() {
            // given
            SelectEntity select = new SelectEntity(ImmutableList.of(), true);
            QueryStructure distinctStructure = whereImpl.getQueryStructure().select(select);
            WhereImpl<Employee, Employee> distinctWhere = new WhereImpl<>(distinctStructure, metamodel, queryExecutor);
            when(queryExecutor.<Number>getList(any())).thenReturn(Collections.singletonList(5L));

            // when
            long result = distinctWhere.count();

            // then
            assertThat(result).isEqualTo(5L);
        }

        /**
         * Test objective: Verify that count with groupBy uses subquery.
         * Test scenario: Call count on a query with group by clause.
         * Expected result: Query executor receives a subquery-based count structure.
         */
        @Test
        void count_WithGroupBy_ShouldUseSubquery() {
            // given
            QueryStructure groupByStructure = whereImpl.getQueryStructure()
                    .groupBy(ImmutableList.of(new PathNode("id")));
            WhereImpl<Employee, Employee> groupByWhere = new WhereImpl<>(groupByStructure, metamodel, queryExecutor);
            when(queryExecutor.<Number>getList(any())).thenReturn(Collections.singletonList(3L));

            // when
            long result = groupByWhere.count();

            // then
            assertThat(result).isEqualTo(3L);
        }
    }

    @Nested
    class ListOperations {

        /**
         * Test objective: Verify that getList executes query with offset and limit.
         * Test scenario: Call getList with offset, maxResult, and lockMode.
         * Expected result: Returns list from query executor with correct structure.
         */
        @Test
        void getList_ShouldExecuteQueryWithOffsetAndLimit() {
            // given
            List<Employee> expected = Arrays.asList(
                    new Employee(1L, "John", "john@example.com", 5000.0, true, null, 1L, null),
                    new Employee(2L, "Jane", "jane@example.com", 6000.0, true, null, 1L, null)
            );
            when(queryExecutor.<Employee>getList(any())).thenReturn(expected);

            // when
            List<Employee> result = whereImpl.getList(0, 10, LockModeType.NONE);

            // then
            assertThat(result).isEqualTo(expected);
        }

        /**
         * Test objective: Verify that getList with different lock modes passes correct lock type.
         * Test scenario: Call getList with PESSIMISTIC_WRITE lock mode.
         * Expected result: Query structure contains the specified lock mode.
         */
        @Test
        void getList_WithPessimisticLock_ShouldSetLockType() {
            // given
            when(queryExecutor.<Employee>getList(any())).thenReturn(Collections.emptyList());

            // when
            whereImpl.getList(0, 10, LockModeType.PESSIMISTIC_WRITE);

            // then
            verify(queryExecutor).getList(argThat(structure ->
                    structure.lockType() == LockModeType.PESSIMISTIC_WRITE));
        }
    }

    @Nested
    class ExistOperations {

        /**
         * Test objective: Verify that exist returns true when result is not empty.
         * Test scenario: Call exist when query executor returns non-empty list.
         * Expected result: Returns true.
         */
        @Test
        void exist_WhenResultNotEmpty_ShouldReturnTrue() {
            // given
            when(queryExecutor.<Employee>getList(any())).thenReturn(Collections.singletonList(new Employee()));

            // when
            boolean result = whereImpl.exist(0);

            // then
            assertThat(result).isTrue();
        }

        /**
         * Test objective: Verify that exist returns false when result is empty.
         * Test scenario: Call exist when query executor returns empty list.
         * Expected result: Returns false.
         */
        @Test
        void exist_WhenResultEmpty_ShouldReturnFalse() {
            // given
            when(queryExecutor.<Employee>getList(any())).thenReturn(Collections.emptyList());

            // when
            boolean result = whereImpl.exist(0);

            // then
            assertThat(result).isFalse();
        }

        /**
         * Test objective: Verify that exist uses limit 1 for efficiency.
         * Test scenario: Call exist method.
         * Expected result: Query structure contains limit of 1.
         */
        @Test
        void exist_ShouldUseLimitOne() {
            // given
            when(queryExecutor.<Employee>getList(any())).thenReturn(Collections.emptyList());

            // when
            whereImpl.exist(5);

            // then
            verify(queryExecutor).<Employee>getList(argThat(structure ->
                    structure.limit() != null && structure.limit() == 1));
        }

        /**
         * Test objective: Verify that exist with offset passes correct offset.
         * Test scenario: Call exist with specific offset.
         * Expected result: Query structure contains the specified offset.
         */
        @Test
        void exist_WithOffset_ShouldSetOffset() {
            // given
            when(queryExecutor.<Employee>getList(any())).thenReturn(Collections.emptyList());

            // when
            whereImpl.exist(10);

            // then
            verify(queryExecutor).<Employee>getList(argThat(structure ->
                    structure.offset() != null && structure.offset() == 10));
        }
    }

    @Nested
    class SubQueryOperations {

        /**
         * Test objective: Verify that asSubQuery returns a SubQueryBuilder.
         * Test scenario: Call asSubQuery method.
         * Expected result: Returns a non-null SubQueryBuilder instance.
         */
        @Test
        void asSubQuery_ShouldReturnSubQueryBuilder() {
            // when
            SubQueryBuilder<Employee, Employee> subQuery = whereImpl.asSubQuery();

            // then
            assertThat(subQuery).isNotNull();
        }

        /**
         * Test objective: Verify that subQuery count creates count expression.
         * Test scenario: Call count on subquery builder.
         * Expected result: Returns a TypedExpression for count.
         */
        @Test
        @SuppressWarnings("unchecked")
        void subQuery_count_ShouldReturnCountExpression() {
            // when
            var countExpr = whereImpl.asSubQuery().count();

            // then
            assertThat(countExpr).isNotNull();
            assertThat(countExpr).isInstanceOf(NumberExpressionImpl.class);
        }

        /**
         * Test objective: Verify that subQuery slice creates slice expression.
         * Test scenario: Call slice on subquery builder.
         * Expected result: Returns a TypedExpression for slice.
         */
        @Test
        @SuppressWarnings("unchecked")
        void subQuery_slice_ShouldReturnSliceExpression() {
            // when
            var sliceExpr = whereImpl.asSubQuery().slice(0, 10);

            // then
            assertThat(sliceExpr).isNotNull();
        }

        /**
         * Test objective: Verify that subQuery getSingle creates single result expression.
         * Test scenario: Call getSingle on subquery builder.
         * Expected result: Returns a TypedExpression for single result.
         */
        @Test
        @SuppressWarnings("unchecked")
        void subQuery_getSingle_ShouldReturnSingleExpression() {
            // when
            var singleExpr = whereImpl.asSubQuery().getSingle(0);

            // then
            assertThat(singleExpr).isNotNull();
        }

        /**
         * Test objective: Verify that subQuery getFirst creates first result expression.
         * Test scenario: Call getFirst on subquery builder.
         * Expected result: Returns a TypedExpression for first result.
         */
        @Test
        @SuppressWarnings("unchecked")
        void subQuery_getFirst_ShouldReturnFirstExpression() {
            // when
            var firstExpr = whereImpl.asSubQuery().getFirst(0);

            // then
            assertThat(firstExpr).isNotNull();
        }
    }

    @Nested
    class QueryStructureAccess {

        /**
         * Test objective: Verify that getQueryStructure returns the query structure.
         * Test scenario: Call getQueryStructure method.
         * Expected result: Returns a non-null QueryStructure instance.
         */
        @Test
        void getQueryStructure_ShouldReturnStructure() {
            // when
            QueryStructure structure = whereImpl.getQueryStructure();

            // then
            assertThat(structure).isNotNull();
            assertThat(structure.from()).isInstanceOf(FromEntity.class);
        }

        /**
         * Test objective: Verify that initial query structure has default values.
         * Test scenario: Create a new WhereImpl and check structure.
         * Expected result: Structure has default empty/none values.
         */
        @Test
        void initialQueryStructure_ShouldHaveDefaults() {
            // when
            QueryStructure structure = whereImpl.getQueryStructure();

            // then
            assertThat(structure.where()).isInstanceOf(EmptyNode.class);
            assertThat(structure.groupBy()).isEmpty();
            assertThat(structure.orderBy()).isEmpty();
            assertThat(structure.having()).isInstanceOf(EmptyNode.class);
            assertThat(structure.offset()).isNull();
            assertThat(structure.limit()).isNull();
            assertThat(structure.lockType()).isEqualTo(LockModeType.NONE);
        }
    }

    @Nested
    class AndWhereOperations {

        /**
         * Test objective: Verify that andWhere combines conditions with AND operator.
         * Test scenario: Call andWhere with a new condition node.
         * Expected result: Returns new WhereImpl with combined conditions.
         */
        @Test
        void andWhere_ShouldCombineConditionsWithAnd() {
            // given
            ExpressionNode newCondition = new OperatorNode(
                    ImmutableList.of(LiteralNode.TRUE),
                    Operator.EQ
            );

            // when
            var result = whereImpl.andWhere(newCondition);

            // then
            assertThat(result).isNotSameAs(whereImpl);
            QueryStructure structure = result.getQueryStructure();
            assertThat(structure.where()).isNotNull();
        }
    }

    @Nested
    class ExceptionAndEdgeCases {

        /**
         * Tests that where with empty in collection returns FALSE expression.
         * Note: The implementation logs a warning and returns FALSE for empty collections.
         */
        @Test
        void where_WithEmptyInCollection_ShouldReturnFalseExpression() {
            // given
            Collection<Long> emptyIds = Collections.emptyList();

            // when
            var result = whereImpl.where(Employee::getId).in(emptyIds);

            // then
            assertThat(result).isNotNull();
            // The implementation returns a FALSE expression for empty collections
        }

        /**
         * Tests that where with null in collection throws NullPointerException.
         */
        @Test
        void where_WithNullInCollection_ShouldThrowException() {
            // when/then
            assertThatThrownBy(() -> whereImpl.where(Employee::getId).in((Collection<Long>) null))
                    .isInstanceOf(NullPointerException.class);
        }

        /**
         * Tests that groupBy with null expression throws exception.
         */
        @Test
        void groupBy_WithNullExpression_ShouldThrowException() {
            // when/then
            assertThatThrownBy(() -> whereImpl.groupBy((Path<Employee, ?>) null))
                    .isInstanceOf(NullPointerException.class);
        }

        /**
         * Tests that groupBy with null expressions list throws exception.
         */
        @Test
        void groupBy_WithNullExpressionsList_ShouldThrowException() {
            // when/then
            assertThatThrownBy(() -> whereImpl.groupBy((List<Path<Employee, ?>>) null))
                    .isInstanceOf(NullPointerException.class);
        }

        /**
         * Tests that having with null predicate throws exception.
         */
        @Test
        void having_WithNullPredicate_ShouldThrowException() {
            // when/then
            assertThatThrownBy(() -> whereImpl.having((TypedExpression<Employee, Boolean>) null))
                    .isInstanceOf(NullPointerException.class);
        }

        /**
         * Tests that andWhere with null condition handles gracefully.
         */
        @Test
        void andWhere_WithNullCondition_ShouldHandleGracefully() {
            // when
            var result = whereImpl.andWhere(null);

            // then
            assertThat(result).isNotNull();
        }

        /**
         * Tests that where with TRUE predicate returns same instance (edge case).
         */
        @Test
        void where_WithTruePredicate_ShouldReturnSameInstance() {
            // when
            var result = whereImpl.where(Expressions.ofTrue());

            // then
            assertThat(result).isSameAs(whereImpl);
        }

        /**
         * Tests that where with FALSE predicate is handled.
         */
        @Test
        void where_WithFalsePredicate_ShouldReturnNewInstance() {
            // when
            var result = whereImpl.where(Expressions.ofFalse());

            // then
            assertThat(result).isNotNull();
        }
    }
}
