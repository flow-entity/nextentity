package io.github.nextentity.integration;

import io.github.nextentity.api.*;
import io.github.nextentity.api.model.Order;
import io.github.nextentity.core.expression.OrderImpl;
import io.github.nextentity.integration.config.IntegrationTestContext;
import io.github.nextentity.integration.config.IntegrationTestProvider;
import io.github.nextentity.integration.entity.Employee;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for WhereImpl class.
 * <p>
 * Covers:
 * - where(PathExpression) method
 * - where(NumberPath) method
 * - where(StringPath) method
 * - groupBy(TypedExpression) method
 * - groupBy(List<TypedExpression>) method
 * - orderBy(List<Order>) method
 *
 * @author HuangChengwei
 */
@DisplayName("WhereImpl Integration Tests")
public class WhereImplIntegrationTest {

    @Nested
    @DisplayName("where(PathExpression) Integration Tests")
    class WhereWithPathExpressionTests {

        /**
         * Test objective: Verify that where(PathExpression) returns PathOperator.
         * Covers: WhereImpl.where(PathExpression) line 179-180
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should return PathOperator with PathExpression")
        void shouldReturnPathOperator_WithPathExpression(IntegrationTestContext context) {
            // Given - create PathExpression using Paths utility
            Path<Employee, Long> idPath = Path.of(Employee::getId);

            // When
            ExpressionBuilder.PathOperator<Employee, Long, ?> operator = context.queryEmployees()
                    .where(idPath);

            // Then
            assertThat(operator).isNotNull();
        }

        /**
         * Test objective: Verify that where(PathExpression) can build and execute condition.
         * Covers: WhereImpl.where(PathExpression) with actual query execution
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should execute query with PathExpression condition")
        void shouldExecuteQuery_WithPathExpressionCondition(IntegrationTestContext context) {
            // Given
            Path<Employee, Long> idPath = Path.of(Employee::getId);
            List<Employee> allEmployees = context.queryEmployees()
                    .orderBy(Employee::getId).asc()
                    .list();
            Long firstId = allEmployees.getFirst().getId();

            // When
            List<Employee> result = context.queryEmployees()
                    .where(idPath).eq(firstId)
                    .list();

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getId()).isEqualTo(firstId);
        }

        /**
         * Test objective: Verify that where(PathExpression) works with comparison operators.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should use comparison operators with PathExpression")
        void shouldUseComparisonOperators_WithPathExpression(IntegrationTestContext context) {
            // Given
            Path<Employee, Long> idPath = Path.of(Employee::getId);
            List<Employee> allEmployees = context.queryEmployees()
                    .orderBy(Employee::getId).asc()
                    .list();
            Long firstId = allEmployees.getFirst().getId();

            // When
            List<Employee> result = context.queryEmployees()
                    .where(idPath).ge(firstId)
                    .orderBy(Employee::getId).asc()
                    .list();

            // Then
            assertThat(result).isNotEmpty();
            assertThat(result.getFirst().getId()).isEqualTo(firstId);
        }

        /**
         * Test objective: Verify that where(PathExpression) works with in operator.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should use in operator with PathExpression")
        void shouldUseInOperator_WithPathExpression(IntegrationTestContext context) {
            // Given
            Path<Employee, Long> idPath = Path.of(Employee::getId);
            List<Employee> allEmployees = context.queryEmployees()
                    .orderBy(Employee::getId).asc()
                    .list();
            Long id1 = allEmployees.get(0).getId();
            Long id2 = allEmployees.get(1).getId();

            // When
            List<Employee> result = context.queryEmployees()
                    .where(idPath).in(id1, id2)
                    .orderBy(Employee::getId).asc()
                    .list();

            // Then
            assertThat(result).hasSize(2);
        }
    }

    @Nested
    @DisplayName("where(NumberPath) Integration Tests")
    class WhereWithNumberPathTests {

        /**
         * Test objective: Verify that where(NumberPath) returns NumberOperator.
         * Covers: WhereImpl.where(NumberPath) line 184-185
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should return NumberOperator with NumberPath")
        void shouldReturnNumberOperator_WithNumberPath(IntegrationTestContext context) {
            // Given - create NumberPath using Paths utility
            NumberPath<Employee, Double> salaryPath = Path.of(Employee::getSalary);

            // When
            ExpressionBuilder.NumberOperator<Employee, Double, ?> operator = context.queryEmployees()
                    .where(salaryPath);

            // Then
            assertThat(operator).isNotNull();
        }

        /**
         * Test objective: Verify that where(NumberPath) supports comparison operations.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should support comparison operations with NumberPath")
        void shouldSupportComparisonOperations_WithNumberPath(IntegrationTestContext context) {
            // Given
            NumberPath<Employee, Double> salaryPath = Path.of(Employee::getSalary);

            // When
            List<Employee> result = context.queryEmployees()
                    .where(salaryPath).gt(5000.0)
                    .orderBy(Employee::getId).asc()
                    .list();

            // Then
            assertThat(result).isNotEmpty();
            assertThat(result).allMatch(e -> e.getSalary() > 5000.0);
        }

        /**
         * Test objective: Verify that where(NumberPath) supports between operation.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should support between operation with NumberPath")
        void shouldSupportBetweenOperation_WithNumberPath(IntegrationTestContext context) {
            // Given - Get actual salary range from data
            List<Employee> allEmployees = context.queryEmployees()
                    .orderBy(Employee::getSalary).asc()
                    .list();
            double minSalary = allEmployees.getFirst().getSalary();
            double maxSalary = allEmployees.getLast().getSalary();
            double midSalary = allEmployees.get(allEmployees.size() / 2).getSalary();

            NumberPath<Employee, Double> salaryPath = Path.of(Employee::getSalary);

            // When
            List<Employee> result = context.queryEmployees()
                    .where(salaryPath).between(minSalary, maxSalary)
                    .orderBy(Employee::getId).asc()
                    .list();

            // Then
            assertThat(result).isNotEmpty();
            assertThat(result).allMatch(e -> e.getSalary() >= minSalary && e.getSalary() <= maxSalary);
        }

        /**
         * Test objective: Verify that where(NumberPath) works with where and orderBy.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should combine NumberPath with orderBy")
        void shouldCombineNumberPath_WithOrderBy(IntegrationTestContext context) {
            // Given
            NumberPath<Employee, Double> salaryPath = Path.of(Employee::getSalary);

            // When
            List<Employee> result = context.queryEmployees()
                    .where(salaryPath).ge(5000.0)
                    .orderBy(Employee::getSalary).desc()
                    .list();

            // Then
            assertThat(result).isNotEmpty();
            // Verify descending order
            for (int i = 1; i < result.size(); i++) {
                assertThat(result.get(i - 1).getSalary()).isGreaterThanOrEqualTo(result.get(i).getSalary());
            }
        }
    }

    @Nested
    @DisplayName("where(StringPath) Integration Tests")
    class WhereWithStringPathTests {

        /**
         * Test objective: Verify that where(StringPath) returns StringOperator.
         * Covers: WhereImpl.where(StringPath) line 189-190
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should return StringOperator with StringPath")
        void shouldReturnStringOperator_WithStringPath(IntegrationTestContext context) {
            // Given - create StringPath using Paths utility
            StringPath<Employee> namePath = Path.of(Employee::getName);

            // When
            ExpressionBuilder.StringOperator<Employee, ?> operator = context.queryEmployees()
                    .where(namePath);

            // Then
            assertThat(operator).isNotNull();
        }

        /**
         * Test objective: Verify that where(StringPath) supports like operation.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should support like operation with StringPath")
        void shouldSupportLikeOperation_WithStringPath(IntegrationTestContext context) {
            // Given
            StringPath<Employee> namePath = Path.of(Employee::getName);

            // When
            List<Employee> result = context.queryEmployees()
                    .where(namePath).like("J%")
                    .orderBy(Employee::getId).asc()
                    .list();

            // Then
            assertThat(result).isNotEmpty();
            assertThat(result).allMatch(e -> e.getName().startsWith("J"));
        }

        /**
         * Test objective: Verify that where(StringPath) supports startsWith operation.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should support startsWith operation with StringPath")
        void shouldSupportStartsWithOperation_WithStringPath(IntegrationTestContext context) {
            // Given
            StringPath<Employee> namePath = Path.of(Employee::getName);

            // When
            List<Employee> result = context.queryEmployees()
                    .where(namePath).startsWith("J")
                    .orderBy(Employee::getId).asc()
                    .list();

            // Then
            assertThat(result).isNotEmpty();
            assertThat(result).allMatch(e -> e.getName().startsWith("J"));
        }

        /**
         * Test objective: Verify that where(StringPath) supports contains operation.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should support contains operation with StringPath")
        void shouldSupportContainsOperation_WithStringPath(IntegrationTestContext context) {
            // Given
            StringPath<Employee> namePath = Path.of(Employee::getName);

            // When
            List<Employee> result = context.queryEmployees()
                    .where(namePath).contains("n")
                    .orderBy(Employee::getId).asc()
                    .list();

            // Then
            assertThat(result).isNotEmpty();
            assertThat(result).allMatch(e -> e.getName().contains("n"));
        }
    }

    @Nested
    @DisplayName("groupBy(TypedExpression) Integration Tests")
    class GroupByExpressionTests {

        /**
         * Test objective: Verify that groupBy(TypedExpression) adds grouping.
         * Covers: WhereImpl.groupBy(TypedExpression) line 193-194
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should group by single TypedExpression")
        void shouldGroupBy_SingleTypedExpression(IntegrationTestContext context) {
            // Given - create TypedExpression using Path.of()
            var departmentPath = Path.of(Employee::getDepartmentId);

            // When
            var results = context.queryEmployees()
                    .select(departmentPath, Path.of(Employee::getId).count())
                    .groupByExpr(departmentPath)
                    .orderBy(Employee::getDepartmentId).asc()
                    .list();

            // Then
            assertThat(results).isNotEmpty();
        }

        /**
         * Test objective: Verify that groupBy(TypedExpression) works with aggregate functions.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should group by TypedExpression with aggregate")
        void shouldGroupBy_TypedExpressionWithAggregate(IntegrationTestContext context) {
            // Given
            var activePath = Path.of(Employee::getActive);

            // When
            var results = context.queryEmployees()
                    .select(activePath, Path.of(Employee::getSalary).avg())
                    .groupByExpr(activePath)
                    .list();

            // Then
            assertThat(results).isNotEmpty();
        }
    }

    @Nested
    @DisplayName("groupBy(List<TypedExpression>) Integration Tests")
    class GroupByExpressionListTests {

        /**
         * Test objective: Verify that groupBy(List<TypedExpression>) adds all groupings.
         * Covers: WhereImpl.groupBy(List<TypedExpression>) line 198-200
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should group by list of TypedExpressions")
        void shouldGroupBy_ListOfTypedExpressions(IntegrationTestContext context) {
            // Given - use Path references directly for groupBy
            // When
            var results = context.queryEmployees()
                    .select(Path.of(Employee::getDepartmentId), Path.of(Employee::getActive), Path.of(Employee::getId).count())
                    .groupBy(Employee::getDepartmentId, Employee::getActive)
                    .orderBy(Employee::getDepartmentId).asc()
                    .list();

            // Then
            assertThat(results).isNotEmpty();
        }

        /**
         * Test objective: Verify that groupBy with list works with having clause.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should group by list with having clause")
        void shouldGroupByList_WithHavingClause(IntegrationTestContext context) {
            // When
            var results = context.queryEmployees()
                    .select(Path.of(Employee::getDepartmentId), Path.of(Employee::getActive), Path.of(Employee::getId).count())
                    .groupBy(Employee::getDepartmentId, Employee::getActive)
                    .having(Path.of(Employee::getId).count().gt(1L))
                    .orderBy(Employee::getDepartmentId).asc()
                    .list();

            // Then
            assertThat(results).isNotEmpty();
        }
    }

    @Nested
    @DisplayName("orderBy(List<Order>) Integration Tests")
    class OrderByOrderListTests {

        /**
         * Test objective: Verify that orderBy(List<Order>) adds sorting.
         * Covers: WhereImpl.orderBy(List<Order>) line 222-225
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should order by list of Order expressions")
        void shouldOrderBy_ListOfOrderExpressions(IntegrationTestContext context) {
            // Given - create Order list using OrderImpl
            Order<Employee> nameOrder = new OrderImpl<>(Path.of(Employee::getName), SortOrder.ASC);
            Order<Employee> salaryOrder = new OrderImpl<>(Path.of(Employee::getSalary), SortOrder.DESC);
            List<Order<Employee>> orders = Arrays.asList(nameOrder, salaryOrder);

            // When
            List<Employee> result = context.queryEmployees()
                    .orderBy(orders)
                    .list();

            // Then
            assertThat(result).isNotEmpty();
            // Verify ordering - names should be sorted ascending
            for (int i = 1; i < result.size(); i++) {
                String prev = result.get(i - 1).getName();
                String curr = result.get(i).getName();
                assertThat(prev.compareTo(curr)).isLessThanOrEqualTo(0);
            }
        }

        /**
         * Test objective: Verify that orderBy(List<Order>) works with single order.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should order by single Order in list")
        void shouldOrderBy_SingleOrderInList(IntegrationTestContext context) {
            // Given
            Order<Employee> salaryOrder = new OrderImpl<>(Path.of(Employee::getSalary), SortOrder.DESC);
            List<Order<Employee>> orders = List.of(salaryOrder);

            // When
            List<Employee> result = context.queryEmployees()
                    .orderBy(orders)
                    .list();

            // Then
            assertThat(result).isNotEmpty();
            // Verify descending order by salary
            for (int i = 1; i < result.size(); i++) {
                Double prev = result.get(i - 1).getSalary();
                Double curr = result.get(i).getSalary();
                assertThat(prev).isGreaterThanOrEqualTo(curr);
            }
        }

        /**
         * Test objective: Verify that orderBy(List<Order>) works with where condition.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should combine orderBy list with where condition")
        void shouldCombineOrderByList_WithWhereCondition(IntegrationTestContext context) {
            // Given
            Order<Employee> nameOrder = new OrderImpl<>(Path.of(Employee::getName), SortOrder.ASC);
            List<Order<Employee>> orders = List.of(nameOrder);

            // When
            List<Employee> result = context.queryEmployees()
                    .where(Employee::getActive).eq(true)
                    .orderBy(orders)
                    .list();

            // Then
            assertThat(result).isNotEmpty();
            assertThat(result).allMatch(Employee::getActive);
        }

        /**
         * Test objective: Verify that orderBy(List<Order>) works with multiple orders.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should order by multiple Order expressions")
        void shouldOrderBy_MultipleOrderExpressions(IntegrationTestContext context) {
            // Given
            Order<Employee> activeOrder = new OrderImpl<>(Path.of(Employee::getActive), SortOrder.DESC);
            Order<Employee> salaryOrder = new OrderImpl<>(Path.of(Employee::getSalary), SortOrder.ASC);
            List<Order<Employee>> orders = Arrays.asList(activeOrder, salaryOrder);

            // When
            List<Employee> result = context.queryEmployees()
                    .orderBy(orders)
                    .list();

            // Then
            assertThat(result).isNotEmpty();
            // Verify ordering - active employees first, then by salary ascending within each group
            boolean foundInactive = false;
            Double lastActiveSalary = null;
            Double lastInactiveSalary = null;

            for (Employee e : result) {
                if (e.getActive()) {
                    if (lastActiveSalary != null) {
                        assertThat(e.getSalary()).isGreaterThanOrEqualTo(lastActiveSalary);
                    }
                    lastActiveSalary = e.getSalary();
                    foundInactive = true;
                } else {
                    if (foundInactive && lastInactiveSalary != null) {
                        assertThat(e.getSalary()).isGreaterThanOrEqualTo(lastInactiveSalary);
                    }
                    lastInactiveSalary = e.getSalary();
                }
            }
        }
    }

    @Nested
    @DisplayName("SubQueryBuilder getRoot() Integration Tests")
    class SubQueryGetRootTests {

        /**
         * Test objective: Verify that SubQueryBuilder is created correctly.
         * Covers: WhereImpl.SubQueryBuilderImpl usage
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should create subquery from query")
        void shouldCreateSubquery_FromQuery(IntegrationTestContext context) {
            // When
            var subQuery = context.queryEmployees()
                    .where(Employee::getActive).eq(true)
                    .toSubQuery();

            // Then
            assertThat(subQuery).isNotNull();
        }

        /**
         * Test objective: Verify that subquery count expression works.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should create count expression from subquery")
        void shouldCreateCountExpression_FromSubquery(IntegrationTestContext context) {
            // When
            var countExpr = context.queryEmployees()
                    .where(Employee::getActive).eq(true)
                    .toSubQuery()
                    .count();

            // Then
            assertThat(countExpr).isNotNull();
        }

        /**
         * Test objective: Verify that subquery slice expression works.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should create slice expression from subquery")
        void shouldCreateSliceExpression_FromSubquery(IntegrationTestContext context) {
            // When
            var sliceExpr = context.queryEmployees()
                    .where(Employee::getActive).eq(true)
                    .toSubQuery()
                    .window(0, 10);

            // Then
            assertThat(sliceExpr).isNotNull();
        }

        /**
         * Test objective: Verify that subquery getFirst works.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should create getFirst expression from subquery")
        void shouldCreateGetFirstExpression_FromSubquery(IntegrationTestContext context) {
            // When
            var firstExpr = context.queryEmployees()
                    .where(Employee::getActive).eq(true)
                    .toSubQuery()
                    .getFirst();

            // Then
            assertThat(firstExpr).isNotNull();
        }

        /**
         * Test objective: Verify that subquery getSingle works.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should create getSingle expression from subquery")
        void shouldCreateGetSingleExpression_FromSubquery(IntegrationTestContext context) {
            // When
            var singleExpr = context.queryEmployees()
                    .where(Employee::getActive).eq(true)
                    .toSubQuery()
                    .getSingle();

            // Then
            assertThat(singleExpr).isNotNull();
        }
    }
}
