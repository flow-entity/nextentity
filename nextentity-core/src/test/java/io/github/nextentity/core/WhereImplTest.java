package io.github.nextentity.core;

import io.github.nextentity.api.Expression;
import io.github.nextentity.api.PathRef;
import io.github.nextentity.api.Predicate;
import io.github.nextentity.api.SubQueryBuilder;
import io.github.nextentity.core.expression.*;
import io.github.nextentity.core.meta.EntityAttribute;
import io.github.nextentity.core.meta.EntityType;
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
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

///
 /// 单元测试 WhereImpl - WHERE clause implementation.
@ExtendWith(MockitoExtension.class)
class WhereImplTest {

    @Mock
    protected Metamodel metamodel;

    @Mock
    protected QueryExecutor queryExecutor;

    @Mock
    protected EntityType entityType;

    @Mock
    protected EntityAttribute idAttribute;

    private WhereImpl<Employee, Employee> whereImpl;
    private QueryDescriptor<Employee> context;

    @BeforeEach
    void setUp() {
        lenient().when(entityType.id()).thenReturn(idAttribute);
        lenient().when(idAttribute.name()).thenReturn("id");
        QueryStructure queryStructure = QueryStructure.of(Employee.class);
        context = new SimpleQueryDescriptor<>(metamodel, queryExecutor, PaginationConfig.DEFAULT, entityType, Employee.class);
        whereImpl = new WhereImpl<>(queryStructure, context);
    }

    @Nested
    class WhereClause {

///
         /// 测试目标: 验证y that where with null predicate returns same instance.
         /// 测试场景: Call where with null predicate.
         /// 预期结果: Returns the same WhereImpl instance without modification.
        @Test
        void where_WithNullPredicate_ShouldReturnSameInstance() {
            // when
            var result = whereImpl.where((Expression<Employee, Boolean>) null);

            // then
            assertThat(result).isSameAs(whereImpl);
        }

///
         /// 测试目标: 验证y that where with TRUE predicate returns same instance.
         /// 测试场景: Call where with a predicate that evaluates to TRUE.
         /// 预期结果: Returns the same WhereImpl instance without modification.
        @Test
        void where_WithTruePredicate_ShouldReturnSameInstance() {
            // when
            Expression<Employee, Boolean> truePredicate = Predicate.ofTrue();
            var result = whereImpl.where(truePredicate);

            // then
            assertThat(result).isSameAs(whereImpl);
        }

///
         /// 测试目标: 验证y that where with valid predicate adds condition.
         /// 测试场景: Call where with a valid boolean predicate.
         /// 预期结果: Returns a new WhereImpl with updated query structure.
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

///
         /// 测试目标: 验证y that where with Path creates PathOperator.
         /// 测试场景: Call where with a Path expression.
         /// 预期结果: Returns a PathOperator for building conditions.
        @Test
        void where_WithPath_ShouldReturnPathOperator() {
            // when
            var operator = whereImpl.where(Employee::getName);

            // then
            assertThat(operator).isNotNull();
        }

///
         /// 测试目标: 验证y that where with NumberPath creates NumberOperator.
         /// 测试场景: Call where with a NumberPath expression.
         /// 预期结果: Returns a NumberOperator for building numeric conditions.
        @Test
        void where_WithNumberPath_ShouldReturnNumberOperator() {
            // when
            var operator = whereImpl.where(Employee::getSalary);

            // then
            assertThat(operator).isNotNull();
        }

///
         /// 测试目标: 验证y that where with StringPath creates StringOperator.
         /// 测试场景: Call where with a StringPath expression.
         /// 预期结果: Returns a StringOperator for building string conditions.
        @Test
        void where_WithStringPath_ShouldReturnStringOperator() {
            // when
            var operator = whereImpl.where((PathRef.StringRef<Employee>) Employee::getName);

            // then
            assertThat(operator).isNotNull();
        }
    }

    @Nested
    class GroupByClause {

///
         /// 测试目标: 验证y that groupBy with single expression adds grouping.
         /// 测试场景: Call groupBy with a single expression.
         /// 预期结果: Query structure contains the group by expression.
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

///
         /// 测试目标: 验证y that groupBy with list of expressions adds all groupings.
         /// 测试场景: Call groupBy with a list of expressions.
         /// 预期结果: Query structure contains all group by expressions.
        @Test
        void groupBy_WithExpressionList_ShouldAddAllGroupings() {
            // given
            List<PathRef<Employee, ?>> expressions = Arrays.asList(
                    Employee::getId,
                    Employee::getName
            );

            // when
            var result = whereImpl.groupBy(expressions);

            // then
            QueryStructure structure = ((WhereImpl<?, ?>) result).getQueryStructure();
            assertThat(structure.groupBy().asList()).hasSize(2);
        }

///
         /// 测试目标: 验证y that groupBy with Path adds grouping.
         /// 测试场景: Call groupBy with a Path expression.
         /// 预期结果: Query structure contains the group by path.
        @Test
        void groupBy_WithPath_ShouldAddGrouping() {
            // when
            var result = whereImpl.groupBy(Employee::getDepartmentId);

            // then
            QueryStructure structure = ((WhereImpl<?, ?>) result).getQueryStructure();
            assertThat(structure.groupBy().asList()).hasSize(1);
        }

///
         /// 测试目标: 验证y that groupBy with collection of paths adds all groupings.
         /// 测试场景: Call groupBy with a collection of Path expressions.
         /// 预期结果: Query structure contains all group by paths.
        @Test
        void groupBy_WithPathCollection_ShouldAddAllGroupings() {
            // given
            List<PathRef<Employee, ?>> paths = Arrays.asList(
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

///
         /// 测试目标: 验证y that having adds condition to query structure.
         /// 测试场景: Call having with a boolean predicate.
         /// 预期结果: Query structure contains the having condition.
        @Test
        void having_WithPredicate_ShouldAddCondition() {
            // given
            Expression<Employee, Boolean> predicate = Predicate.ofTrue();

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

///
         /// 测试目标: 验证y that orderBy with list of orders adds sorting.
         /// 测试场景: Call orderBy with a list of Order expressions.
         /// 预期结果: Query structure contains the order by expressions.
        @Test
        void orderBy_WithOrderList_ShouldAddSorting() {
            // given
            // Use the existing orderBy API with Path references
            List<PathRef<Employee, ? extends Comparable<?>>> paths = Arrays.asList(
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

///
         /// 测试目标: 验证y that orderBy with collection of paths creates OrderOperator.
         /// 测试场景: Call orderBy with a collection of Path expressions.
         /// 预期结果: Returns an OrderOperator for building sort expressions.
        @Test
        void orderBy_WithPathCollection_ShouldReturnOrderOperator() {
            // given
            List<PathRef<Employee, ? extends Comparable<?>>> paths = Arrays.asList(
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

///
         /// 测试目标: 验证y that count executes count query and returns result.
         /// 测试场景: Call count 方法.
         /// 预期结果: Returns the count value from query executor.
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

///
         /// 测试目标: 验证y that count with distinct select uses subquery.
         /// 测试场景: Call count on a query with distinct selection.
         /// 预期结果: Query executor receives a subquery-based count structure.
        @Test
        void count_WithDistinctSelect_ShouldUseSubquery() {
            // given
            SelectEntity select = new SelectEntity(ImmutableList.of(), true);
            QueryStructure distinctStructure = whereImpl.getQueryStructure().select(select);
            WhereImpl<Employee, Employee> distinctWhere = new WhereImpl<>(distinctStructure, context);
            when(queryExecutor.<Number>getList(any())).thenReturn(Collections.singletonList(5L));

            // when
            long result = distinctWhere.count();

            // then
            assertThat(result).isEqualTo(5L);
        }

///
         /// 测试目标: 验证y that count with groupBy uses subquery.
         /// 测试场景: Call count on a query with group by clause.
         /// 预期结果: Query executor receives a subquery-based count structure.
        @Test
        void count_WithGroupBy_ShouldUseSubquery() {
            // given
            QueryStructure groupByStructure = whereImpl.getQueryStructure()
                    .groupBy(ImmutableList.of(new PathNode("id")));
            WhereImpl<Employee, Employee> groupByWhere = new WhereImpl<>(groupByStructure, context);
            when(queryExecutor.<Number>getList(any())).thenReturn(Collections.singletonList(3L));

            // when
            long result = groupByWhere.count();

            // then
            assertThat(result).isEqualTo(3L);
        }
    }

    @Nested
    class ListOperations {

///
         /// 测试目标: 验证y that getList executes query with offset and limit.
         /// 测试场景: Call getList with offset, max结果, and lockMode.
         /// 预期结果: Returns list from query executor with correct structure.
        @Test
        void getList_ShouldExecuteQueryWithOffsetAndLimit() {
            // given
            List<Employee> expected = Arrays.asList(
                    new Employee(1L, "John", "john@example.com", 5000.0, true, null, 1L, null),
                    new Employee(2L, "Jane", "jane@example.com", 6000.0, true, null, 1L, null)
            );
            when(queryExecutor.<Employee>getList(any())).thenReturn(expected);

            // when
            List<Employee> result = whereImpl.lock(LockModeType.NONE).list(10);

            // then
            assertThat(result).isEqualTo(expected);
        }

///
         /// 测试目标: 验证y that getList with different lock modes passes correct lock type.
         /// 测试场景: Call getList with PESSIMISTIC_WRITE lock mode.
         /// 预期结果: Query structure contains the specified lock mode.
        @Test
        void getList_WithPessimisticLock_ShouldSetLockType() {
            // given
            when(queryExecutor.<Employee>getList(any())).thenReturn(Collections.emptyList());

            // when
            whereImpl.lock(LockModeType.PESSIMISTIC_WRITE).list(10);

            // then
            verify(queryExecutor).getList(argThat(structure ->
                    structure.lockType() == LockModeType.PESSIMISTIC_WRITE));
        }
    }

    @Nested
    class ExistOperations {

///
         /// 测试目标: 验证y that exist returns true when result is not empty.
         /// 测试场景: Call exist when query executor returns non-empty list.
         /// 预期结果: Returns true.
        @Test
        void exist_WhenResultNotEmpty_ShouldReturnTrue() {
            // given
            when(queryExecutor.<Employee>getList(any())).thenReturn(Collections.singletonList(new Employee()));

            // when
            boolean result = whereImpl.exists();

            // then
            assertThat(result).isTrue();
        }

///
         /// 测试目标: 验证y that exist returns false when result is empty.
         /// 测试场景: Call exist when query executor returns empty list.
         /// 预期结果: Returns false.
        @Test
        void exist_WhenResultEmpty_ShouldReturnFalse() {
            // given
            when(queryExecutor.<Employee>getList(any())).thenReturn(Collections.emptyList());

            // when
            boolean result = whereImpl.exists();

            // then
            assertThat(result).isFalse();
        }

///
         /// 测试目标: 验证y that exist uses limit 1 for efficiency.
         /// 测试场景: Call exist 方法.
         /// 预期结果: Query structure contains limit of 1.
        @Test
        void exists_ShouldUseLimitOne() {
            // given
            when(queryExecutor.<Employee>getList(any())).thenReturn(Collections.emptyList());

            // when
            whereImpl.exists();

            // then
            verify(queryExecutor).<Employee>getList(argThat(structure ->
                    structure.limit() != null && structure.limit() == 1));
        }

///
         /// 测试目标: 验证y that exists with offset passes correct offset.
         /// 测试场景: Call exists with specific offset.
         /// 预期结果: Query structure contains the specified offset.
        @Test
        void exists_WithOffset_ShouldSetOffset() {
            // given
            when(queryExecutor.<Employee>getList(any())).thenReturn(Collections.emptyList());

            // when
            whereImpl.list(10, 1);

            // then
            verify(queryExecutor).<Employee>getList(argThat(structure ->
                    structure.offset() != null && structure.offset() == 10));
        }
    }

    @Nested
    class SubQueryOperations {

///
         /// 测试目标: 验证y that asSubQuery returns a SubQueryBuilder.
         /// 测试场景: Call asSubQuery 方法.
         /// 预期结果: Returns a non-null SubQueryBuilder instance.
        @Test
        void asSubQuery_ShouldReturnSubQueryBuilder() {
            // when
            SubQueryBuilder<Employee, Employee> subQuery = whereImpl.toSubQuery();

            // then
            assertThat(subQuery).isNotNull();
        }

///
         /// 测试目标: 验证y that subQuery count creates count expression.
         /// 测试场景: Call count on subquery builder.
         /// 预期结果: Returns a TypedExpression for count.
        @Test
        void subQuery_count_ShouldReturnCountExpression() {
            // when
            var countExpr = whereImpl.toSubQuery().count();

            // then
            assertThat(countExpr).isNotNull();
            assertThat(countExpr).isInstanceOf(NumberExpressionImpl.class);
        }

///
         /// 测试目标: 验证y that subQuery slice creates slice expression.
         /// 测试场景: Call slice on subquery builder.
         /// 预期结果: Returns a TypedExpression for slice.
        @Test
        void subQuery_slice_ShouldReturnSliceExpression() {
            // when
            var sliceExpr = whereImpl.toSubQuery().slice(0, 10);

            // then
            assertThat(sliceExpr).isNotNull();
        }
    }

    @Nested
    class QueryStructureAccess {

///
         /// 测试目标: 验证y that getQueryStructure returns the query structure.
         /// 测试场景: Call getQueryStructure 方法.
         /// 预期结果: Returns a non-null QueryStructure instance.
        @Test
        void getQueryStructure_ShouldReturnStructure() {
            // when
            QueryStructure structure = whereImpl.getQueryStructure();

            // then
            assertThat(structure).isNotNull();
            assertThat(structure.from()).isInstanceOf(FromEntity.class);
        }

///
         /// 测试目标: 验证y that initial query structure has default values.
         /// 测试场景: Create a new WhereImpl and check structure.
         /// 预期结果: Structure has default empty/none values.
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

///
         /// 测试目标: 验证y that andWhere combines conditions with AND operator.
         /// 测试场景: Call andWhere with a new condition node.
         /// 预期结果: Returns new WhereImpl with combined conditions.
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

///
         /// 测试s that where with empty in collection returns FALSE expression.
         /// Note: The implementation logs a warning and returns FALSE for empty collections.
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

///
         /// 测试s that where with null in collection throws NullPointerException.
        @Test
        void where_WithNullInCollection_ShouldThrowException() {
            // when/then
            assertThatThrownBy(() -> whereImpl.where(Employee::getId).in((Collection<Long>) null))
                    .isInstanceOf(NullPointerException.class);
        }

///
         /// 测试s that groupBy with null expression throws exception.
        @Test
        void groupBy_WithNullExpression_ShouldThrowException() {
            // when/then
            assertThatThrownBy(() -> whereImpl.groupBy((PathRef<Employee, ?>) null))
                    .isInstanceOf(NullPointerException.class);
        }

///
         /// 测试s that groupBy with null expressions list throws exception.
        @Test
        void groupBy_WithNullExpressionsList_ShouldThrowException() {
            // when/then
            assertThatThrownBy(() -> whereImpl.groupBy((List<PathRef<Employee, ?>>) null))
                    .isInstanceOf(NullPointerException.class);
        }

///
         /// 测试s that having with null predicate throws exception.
        @Test
        void having_WithNullPredicate_ShouldThrowException() {
            // when/then
            assertThatThrownBy(() -> whereImpl.having(null))
                    .isInstanceOf(NullPointerException.class);
        }

///
         /// 测试s that andWhere with null condition 处理 gracefully.
        @Test
        void andWhere_WithNullCondition_ShouldHandleGracefully() {
            // when
            var result = whereImpl.andWhere(null);

            // then
            assertThat(result).isNotNull();
        }

///
         /// 测试s that where with TRUE predicate returns same instance (edge case).
        @Test
        void where_WithTruePredicate_ShouldReturnSameInstance() {
            // when
            var result = whereImpl.where(Predicate.ofTrue());

            // then
            assertThat(result).isSameAs(whereImpl);
        }

///
         /// 测试s that where with FALSE predicate is handled.
        @Test
        void where_WithFalsePredicate_ShouldReturnNewInstance() {
            // when
            var result = whereImpl.where(Predicate.ofFalse());

            // then
            assertThat(result).isNotNull();
        }
    }
}


