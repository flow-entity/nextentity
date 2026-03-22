package io.github.nextentity.core;

import io.github.nextentity.api.Path;
import io.github.nextentity.api.PathExpression;
import io.github.nextentity.api.TypedExpression;
import io.github.nextentity.api.model.LockModeType;
import io.github.nextentity.core.expression.*;
import io.github.nextentity.core.meta.Metamodel;
import io.github.nextentity.core.util.ImmutableList;
import io.github.nextentity.core.util.Paths;
import io.github.nextentity.integration.entity.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for QueryBuilder - Core query builder implementation.
 */
@ExtendWith(MockitoExtension.class)
class QueryBuilderTest {

    @Mock
    protected Metamodel metamodel;

    @Mock
    protected QueryExecutor queryExecutor;

    private QueryBuilder<Employee> queryBuilder;

    @BeforeEach
    void setUp() {
        queryBuilder = new QueryBuilder<>(metamodel, queryExecutor, Employee.class);
    }

    @Nested
    class SelectOperations {

        /**
         * Test objective: Verify that select with Class returns correct WhereStep.
         * Test scenario: Call select with a projection class.
         * Expected result: Returns WhereStep with SelectProjection.
         */
        @Test
        void select_WithClass_ShouldReturnWhereStep() {
            // when
            var result = queryBuilder.select(String.class);

            // then
            assertThat(result).isNotNull();
            QueryStructure structure = ((WhereImpl<?, ?>) result).getQueryStructure();
            assertThat(structure.select()).isInstanceOf(SelectProjection.class);
        }

        /**
         * Test objective: Verify that selectDistinct with Class sets distinct flag.
         * Test scenario: Call selectDistinct with a projection class.
         * Expected result: Returns WhereStep with distinct SelectProjection.
         */
        @Test
        void selectDistinct_WithClass_ShouldSetDistinctFlag() {
            // when
            var result = queryBuilder.selectDistinct(String.class);

            // then
            assertThat(result).isNotNull();
            QueryStructure structure = ((WhereImpl<?, ?>) result).getQueryStructure();
            SelectProjection select = (SelectProjection) structure.select();
            assertThat(select.distinct()).isTrue();
        }

        /**
         * Test objective: Verify that selectDistinct with Path sets distinct flag.
         * Test scenario: Call selectDistinct with a single Path expression.
         * Expected result: Returns WhereStep with distinct SelectExpression.
         */
        @Test
        void selectDistinct_WithPath_ShouldSetDistinctFlag() {
            // when
            var result = queryBuilder.selectDistinct(Employee::getName);

            // then
            assertThat(result).isNotNull();
            QueryStructure structure = ((WhereImpl<?, ?>) result).getQueryStructure();
            SelectExpression select = (SelectExpression) structure.select();
            assertThat(select.distinct()).isTrue();
        }

        /**
         * Test objective: Verify that select with Path creates SelectExpression.
         * Test scenario: Call select with a Path expression.
         * Expected result: Returns WhereStep with SelectExpression.
         */
        @Test
        void select_WithPath_ShouldCreateSelectExpression() {
            // when
            var result = queryBuilder.select(Employee::getName);

            // then
            assertThat(result).isNotNull();
            QueryStructure structure = ((WhereImpl<?, ?>) result).getQueryStructure();
            assertThat(structure.select()).isInstanceOf(SelectExpression.class);
        }

        /**
         * Test objective: Verify that select with collection of paths creates Tuple.
         * Test scenario: Call select with multiple paths.
         * Expected result: Returns WhereStep with SelectExpressions for Tuple.
         */
        @Test
        void select_WithPathCollection_ShouldCreateTuple() {
            // given
            List<Path<Employee, ?>> paths = Arrays.asList(
                    Employee::getName,
                    Employee::getSalary
            );

            // when
            var result = queryBuilder.select(paths);

            // then
            assertThat(result).isNotNull();
            QueryStructure structure = ((WhereImpl<?, ?>) result).getQueryStructure();
            assertThat(structure.select()).isInstanceOf(SelectExpressions.class);
            SelectExpressions select = (SelectExpressions) structure.select();
            assertThat(select.distinct()).isFalse();
            assertThat(select.items()).hasSize(2);
        }

        /**
         * Test objective: Verify that select with multiple paths creates typed Tuple.
         * Test scenario: Call select with 2 paths.
         * Expected result: Returns WhereStep with Tuple2.
         */
        @Test
        void select_WithTwoPaths_ShouldCreateTuple2() {
            // when
            var result = queryBuilder.select(Employee::getName, Employee::getSalary);

            // then
            assertThat(result).isNotNull();
            QueryStructure structure = ((WhereImpl<?, ?>) result).getQueryStructure();
            assertThat(structure.select()).isInstanceOf(SelectExpressions.class);
            SelectExpressions select = (SelectExpressions) structure.select();
            assertThat(select.distinct()).isFalse();
            assertThat(select.items()).hasSize(2);
        }

        /**
         * Test objective: Verify that select with three paths creates Tuple3.
         * Test scenario: Call select with 3 paths.
         * Expected result: Returns WhereStep with Tuple3.
         */
        @Test
        void select_WithThreePaths_ShouldCreateTuple3() {
            // when
            var result = queryBuilder.select(
                    Employee::getName,
                    Employee::getSalary,
                    Employee::getId
            );

            // then
            assertThat(result).isNotNull();
            QueryStructure structure = ((WhereImpl<?, ?>) result).getQueryStructure();
            SelectExpressions select = (SelectExpressions) structure.select();
            assertThat(select.distinct()).isFalse();
            assertThat(select.items()).hasSize(3);
        }

        /**
         * Test objective: Verify that selectDistinct with two paths creates distinct Tuple2.
         * Test scenario: Call selectDistinct with 2 paths.
         * Expected result: Returns WhereStep with distinct SelectExpressions.
         */
        @Test
        void selectDistinct_WithTwoPaths_ShouldCreateDistinctTuple2() {
            // when
            var result = queryBuilder.selectDistinct(Employee::getName, Employee::getSalary);

            // then
            QueryStructure structure = ((WhereImpl<?, ?>) result).getQueryStructure();
            SelectExpressions select = (SelectExpressions) structure.select();
            assertThat(select.distinct()).isTrue();
            assertThat(select.items()).hasSize(2);
        }

        /**
         * Test objective: Verify that select with four paths creates Tuple4.
         * Test scenario: Call select with 4 paths.
         * Expected result: Returns WhereStep with SelectExpressions.
         */
        @Test
        void select_WithFourPaths_ShouldCreateTuple4() {
            // when
            var result = queryBuilder.select(
                    Employee::getName,
                    Employee::getSalary,
                    Employee::getId,
                    Employee::getEmail
            );

            // then
            assertThat(result).isNotNull();
            QueryStructure structure = ((WhereImpl<?, ?>) result).getQueryStructure();
            SelectExpressions select = (SelectExpressions) structure.select();
            assertThat(select.distinct()).isFalse();
            assertThat(select.items()).hasSize(4);
        }

        /**
         * Test objective: Verify that select with five paths creates Tuple5.
         * Test scenario: Call select with 5 paths.
         * Expected result: Returns WhereStep with SelectExpressions.
         */
        @Test
        void select_WithFivePaths_ShouldCreateTuple5() {
            // when
            var result = queryBuilder.select(
                    Employee::getName,
                    Employee::getSalary,
                    Employee::getId,
                    Employee::getEmail,
                    Employee::getActive
            );

            // then
            assertThat(result).isNotNull();
            QueryStructure structure = ((WhereImpl<?, ?>) result).getQueryStructure();
            SelectExpressions select = (SelectExpressions) structure.select();
            assertThat(select.distinct()).isFalse();
            assertThat(select.items()).hasSize(5);
        }

        /**
         * Test objective: Verify that select with six paths creates Tuple6.
         * Test scenario: Call select with 6 paths.
         * Expected result: Returns WhereStep with SelectExpressions.
         */
        @Test
        void select_WithSixPaths_ShouldCreateTuple6() {
            // when
            var result = queryBuilder.select(
                    Employee::getName,
                    Employee::getSalary,
                    Employee::getId,
                    Employee::getEmail,
                    Employee::getActive,
                    Employee::getStatus
            );

            // then
            assertThat(result).isNotNull();
            QueryStructure structure = ((WhereImpl<?, ?>) result).getQueryStructure();
            SelectExpressions select = (SelectExpressions) structure.select();
            assertThat(select.distinct()).isFalse();
            assertThat(select.items()).hasSize(6);
        }

        /**
         * Test objective: Verify that select with seven paths creates Tuple7.
         * Test scenario: Call select with 7 paths.
         * Expected result: Returns WhereStep with SelectExpressions.
         */
        @Test
        void select_WithSevenPaths_ShouldCreateTuple7() {
            // when
            var result = queryBuilder.select(
                    Employee::getName,
                    Employee::getSalary,
                    Employee::getId,
                    Employee::getEmail,
                    Employee::getActive,
                    Employee::getStatus,
                    Employee::getDepartmentId
            );

            // then
            assertThat(result).isNotNull();
            QueryStructure structure = ((WhereImpl<?, ?>) result).getQueryStructure();
            SelectExpressions select = (SelectExpressions) structure.select();
            assertThat(select.distinct()).isFalse();
            assertThat(select.items()).hasSize(7);
        }

        /**
         * Test objective: Verify that select with eight paths creates Tuple8.
         * Test scenario: Call select with 8 paths.
         * Expected result: Returns WhereStep with SelectExpressions.
         */
        @Test
        void select_WithEightPaths_ShouldCreateTuple8() {
            // when
            var result = queryBuilder.select(
                    Employee::getName,
                    Employee::getSalary,
                    Employee::getId,
                    Employee::getEmail,
                    Employee::getActive,
                    Employee::getStatus,
                    Employee::getDepartmentId,
                    Employee::getHireDate
            );

            // then
            assertThat(result).isNotNull();
            QueryStructure structure = ((WhereImpl<?, ?>) result).getQueryStructure();
            SelectExpressions select = (SelectExpressions) structure.select();
            assertThat(select.distinct()).isFalse();
            assertThat(select.items()).hasSize(8);
        }

        /**
         * Test objective: Verify that select with nine paths creates Tuple9.
         * Test scenario: Call select with 9 paths.
         * Expected result: Returns WhereStep with SelectExpressions.
         */
        @Test
        void select_WithNinePaths_ShouldCreateTuple9() {
            // when
            var result = queryBuilder.select(
                    Employee::getName,
                    Employee::getSalary,
                    Employee::getId,
                    Employee::getEmail,
                    Employee::getActive,
                    Employee::getStatus,
                    Employee::getDepartmentId,
                    Employee::getHireDate,
                    Employee::getCreatedAt
            );

            // then
            assertThat(result).isNotNull();
            QueryStructure structure = ((WhereImpl<?, ?>) result).getQueryStructure();
            SelectExpressions select = (SelectExpressions) structure.select();
            assertThat(select.distinct()).isFalse();
            assertThat(select.items()).hasSize(9);
        }

        /**
         * Test objective: Verify that select with ten paths creates Tuple10.
         * Test scenario: Call select with 10 paths (reusing some).
         * Expected result: Returns WhereStep with SelectExpressions.
         */
        @Test
        void select_WithTenPaths_ShouldCreateTuple10() {
            // when
            var result = queryBuilder.select(
                    Employee::getName,
                    Employee::getSalary,
                    Employee::getId,
                    Employee::getEmail,
                    Employee::getActive,
                    Employee::getStatus,
                    Employee::getDepartmentId,
                    Employee::getHireDate,
                    Employee::getCreatedAt,
                    Employee::getName // reuse name as 10th
            );

            // then
            assertThat(result).isNotNull();
            QueryStructure structure = ((WhereImpl<?, ?>) result).getQueryStructure();
            SelectExpressions select = (SelectExpressions) structure.select();
            assertThat(select.distinct()).isFalse();
            assertThat(select.items()).hasSize(10);
        }

        /**
         * Test objective: Verify that selectDistinct with Collection of paths sets distinct flag.
         * Test scenario: Call selectDistinct with Collection of paths.
         * Expected result: Returns WhereStep with distinct SelectExpressions.
         */
        @Test
        void selectDistinct_WithCollectionOfPaths_ShouldSetDistinctFlag() {
            // given
            List<Path<Employee, ?>> paths = Arrays.asList(
                    Employee::getName,
                    Employee::getSalary
            );

            // when
            var result = queryBuilder.selectDistinct(paths);

            // then
            assertThat(result).isNotNull();
            QueryStructure structure = ((WhereImpl<?, ?>) result).getQueryStructure();
            SelectExpressions select = (SelectExpressions) structure.select();
            assertThat(select.distinct()).isTrue();
            assertThat(select.items()).hasSize(2);
        }

        /**
         * Test objective: Verify that selectDistinct with three paths creates distinct Tuple3.
         * Test scenario: Call selectDistinct with 3 paths.
         * Expected result: Returns WhereStep with distinct SelectExpressions.
         */
        @Test
        void selectDistinct_WithThreePaths_ShouldCreateDistinctTuple3() {
            // when
            var result = queryBuilder.selectDistinct(
                    Employee::getName,
                    Employee::getSalary,
                    Employee::getId
            );

            // then
            QueryStructure structure = ((WhereImpl<?, ?>) result).getQueryStructure();
            SelectExpressions select = (SelectExpressions) structure.select();
            assertThat(select.distinct()).isTrue();
            assertThat(select.items()).hasSize(3);
        }

        /**
         * Test objective: Verify that selectDistinct with four paths creates distinct Tuple4.
         * Test scenario: Call selectDistinct with 4 paths.
         * Expected result: Returns WhereStep with distinct SelectExpressions.
         */
        @Test
        void selectDistinct_WithFourPaths_ShouldCreateDistinctTuple4() {
            // when
            var result = queryBuilder.selectDistinct(
                    Employee::getName,
                    Employee::getSalary,
                    Employee::getId,
                    Employee::getEmail
            );

            // then
            QueryStructure structure = ((WhereImpl<?, ?>) result).getQueryStructure();
            SelectExpressions select = (SelectExpressions) structure.select();
            assertThat(select.distinct()).isTrue();
            assertThat(select.items()).hasSize(4);
        }

        /**
         * Test objective: Verify that selectDistinct with five paths creates distinct Tuple5.
         * Test scenario: Call selectDistinct with 5 paths.
         * Expected result: Returns WhereStep with distinct SelectExpressions.
         */
        @Test
        void selectDistinct_WithFivePaths_ShouldCreateDistinctTuple5() {
            // when
            var result = queryBuilder.selectDistinct(
                    Employee::getName,
                    Employee::getSalary,
                    Employee::getId,
                    Employee::getEmail,
                    Employee::getActive
            );

            // then
            QueryStructure structure = ((WhereImpl<?, ?>) result).getQueryStructure();
            SelectExpressions select = (SelectExpressions) structure.select();
            assertThat(select.distinct()).isTrue();
            assertThat(select.items()).hasSize(5);
        }

        /**
         * Test objective: Verify that selectDistinct with six paths creates distinct Tuple6.
         * Test scenario: Call selectDistinct with 6 paths.
         * Expected result: Returns WhereStep with distinct SelectExpressions.
         */
        @Test
        void selectDistinct_WithSixPaths_ShouldCreateDistinctTuple6() {
            // when
            var result = queryBuilder.selectDistinct(
                    Employee::getName,
                    Employee::getSalary,
                    Employee::getId,
                    Employee::getEmail,
                    Employee::getActive,
                    Employee::getStatus
            );

            // then
            QueryStructure structure = ((WhereImpl<?, ?>) result).getQueryStructure();
            SelectExpressions select = (SelectExpressions) structure.select();
            assertThat(select.distinct()).isTrue();
            assertThat(select.items()).hasSize(6);
        }

        /**
         * Test objective: Verify that selectDistinct with seven paths creates distinct Tuple7.
         * Test scenario: Call selectDistinct with 7 paths.
         * Expected result: Returns WhereStep with distinct SelectExpressions.
         */
        @Test
        void selectDistinct_WithSevenPaths_ShouldCreateDistinctTuple7() {
            // when
            var result = queryBuilder.selectDistinct(
                    Employee::getName,
                    Employee::getSalary,
                    Employee::getId,
                    Employee::getEmail,
                    Employee::getActive,
                    Employee::getStatus,
                    Employee::getDepartmentId
            );

            // then
            QueryStructure structure = ((WhereImpl<?, ?>) result).getQueryStructure();
            SelectExpressions select = (SelectExpressions) structure.select();
            assertThat(select.distinct()).isTrue();
            assertThat(select.items()).hasSize(7);
        }

        /**
         * Test objective: Verify that selectDistinct with eight paths creates distinct Tuple8.
         * Test scenario: Call selectDistinct with 8 paths.
         * Expected result: Returns WhereStep with distinct SelectExpressions.
         */
        @Test
        void selectDistinct_WithEightPaths_ShouldCreateDistinctTuple8() {
            // when
            var result = queryBuilder.selectDistinct(
                    Employee::getName,
                    Employee::getSalary,
                    Employee::getId,
                    Employee::getEmail,
                    Employee::getActive,
                    Employee::getStatus,
                    Employee::getDepartmentId,
                    Employee::getHireDate
            );

            // then
            QueryStructure structure = ((WhereImpl<?, ?>) result).getQueryStructure();
            SelectExpressions select = (SelectExpressions) structure.select();
            assertThat(select.distinct()).isTrue();
            assertThat(select.items()).hasSize(8);
        }

        /**
         * Test objective: Verify that selectDistinct with nine paths creates distinct Tuple9.
         * Test scenario: Call selectDistinct with 9 paths.
         * Expected result: Returns WhereStep with distinct SelectExpressions.
         */
        @Test
        void selectDistinct_WithNinePaths_ShouldCreateDistinctTuple9() {
            // when
            var result = queryBuilder.selectDistinct(
                    Employee::getName,
                    Employee::getSalary,
                    Employee::getId,
                    Employee::getEmail,
                    Employee::getActive,
                    Employee::getStatus,
                    Employee::getDepartmentId,
                    Employee::getHireDate,
                    Employee::getCreatedAt
            );

            // then
            QueryStructure structure = ((WhereImpl<?, ?>) result).getQueryStructure();
            SelectExpressions select = (SelectExpressions) structure.select();
            assertThat(select.distinct()).isTrue();
            assertThat(select.items()).hasSize(9);
        }

        /**
         * Test objective: Verify that selectDistinct with ten paths creates distinct Tuple10.
         * Test scenario: Call selectDistinct with 10 paths.
         * Expected result: Returns WhereStep with distinct SelectExpressions.
         */
        @Test
        void selectDistinct_WithTenPaths_ShouldCreateDistinctTuple10() {
            // when
            var result = queryBuilder.selectDistinct(
                    Employee::getName,
                    Employee::getSalary,
                    Employee::getId,
                    Employee::getEmail,
                    Employee::getActive,
                    Employee::getStatus,
                    Employee::getDepartmentId,
                    Employee::getHireDate,
                    Employee::getCreatedAt,
                    Employee::getName
            );

            // then
            QueryStructure structure = ((WhereImpl<?, ?>) result).getQueryStructure();
            SelectExpressions select = (SelectExpressions) structure.select();
            assertThat(select.distinct()).isTrue();
            assertThat(select.items()).hasSize(10);
        }

        /**
         * Test objective: Verify that selectDistinct with TypedExpression sets distinct flag.
         * Test scenario: Call selectDistinct with a single TypedExpression.
         * Expected result: Returns WhereStep with distinct SelectExpression.
         */
        @Test
        void selectDistinct_WithTypedExpression_ShouldSetDistinctFlag() {
            // when
            var result = queryBuilder.selectDistinct(Paths.get(Employee::getName));

            // then
            assertThat(result).isNotNull();
            QueryStructure structure = ((WhereImpl<?, ?>) result).getQueryStructure();
            SelectExpression select = (SelectExpression) structure.select();
            assertThat(select.distinct()).isTrue();
        }

        /**
         * Test objective: Verify that selectDistinct with List of TypedExpressions sets distinct flag.
         * Test scenario: Call selectDistinct with List of TypedExpressions.
         * Expected result: Returns WhereStep with distinct SelectExpressions.
         */
        @Test
        void selectDistinct_WithTypedExpressionList_ShouldSetDistinctFlag() {
            // given
            List<TypedExpression<Employee, ?>> expressions = Arrays.asList(
                    Paths.get(Employee::getName),
                    Paths.get(Employee::getSalary)
            );

            // when
            var result = queryBuilder.selectDistinct(expressions);

            // then
            assertThat(result).isNotNull();
            QueryStructure structure = ((WhereImpl<?, ?>) result).getQueryStructure();
            SelectExpressions select = (SelectExpressions) structure.select();
            assertThat(select.distinct()).isTrue();
            assertThat(select.items()).hasSize(2);
        }

        /**
         * Test objective: Verify that selectDistinct with two TypedExpressions creates distinct Tuple2.
         * Test scenario: Call selectDistinct with 2 TypedExpressions.
         * Expected result: Returns WhereStep with distinct SelectExpressions.
         */
        @Test
        void selectDistinct_WithTwoTypedExpressions_ShouldCreateDistinctTuple2() {
            // when
            var result = queryBuilder.selectDistinct(
                    Paths.get(Employee::getName),
                    Paths.get(Employee::getSalary)
            );

            // then
            QueryStructure structure = ((WhereImpl<?, ?>) result).getQueryStructure();
            SelectExpressions select = (SelectExpressions) structure.select();
            assertThat(select.distinct()).isTrue();
            assertThat(select.items()).hasSize(2);
        }

        /**
         * Test objective: Verify that selectDistinct with three TypedExpressions creates distinct Tuple3.
         * Test scenario: Call selectDistinct with 3 TypedExpressions.
         * Expected result: Returns WhereStep with distinct SelectExpressions.
         */
        @Test
        void selectDistinct_WithThreeTypedExpressions_ShouldCreateDistinctTuple3() {
            // when
            var result = queryBuilder.selectDistinct(
                    Paths.get(Employee::getName),
                    Paths.get(Employee::getSalary),
                    Paths.get(Employee::getId)
            );

            // then
            QueryStructure structure = ((WhereImpl<?, ?>) result).getQueryStructure();
            SelectExpressions select = (SelectExpressions) structure.select();
            assertThat(select.distinct()).isTrue();
            assertThat(select.items()).hasSize(3);
        }

        /**
         * Test objective: Verify that selectDistinct with four TypedExpressions creates distinct Tuple4.
         * Test scenario: Call selectDistinct with 4 TypedExpressions.
         * Expected result: Returns WhereStep with distinct SelectExpressions.
         */
        @Test
        void selectDistinct_WithFourTypedExpressions_ShouldCreateDistinctTuple4() {
            // when
            var result = queryBuilder.selectDistinct(
                    Paths.get(Employee::getName),
                    Paths.get(Employee::getSalary),
                    Paths.get(Employee::getId),
                    Paths.get(Employee::getEmail)
            );

            // then
            QueryStructure structure = ((WhereImpl<?, ?>) result).getQueryStructure();
            SelectExpressions select = (SelectExpressions) structure.select();
            assertThat(select.distinct()).isTrue();
            assertThat(select.items()).hasSize(4);
        }

        /**
         * Test objective: Verify that selectDistinct with five TypedExpressions creates distinct Tuple5.
         * Test scenario: Call selectDistinct with 5 TypedExpressions.
         * Expected result: Returns WhereStep with distinct SelectExpressions.
         */
        @Test
        void selectDistinct_WithFiveTypedExpressions_ShouldCreateDistinctTuple5() {
            // when
            var result = queryBuilder.selectDistinct(
                    Paths.get(Employee::getName),
                    Paths.get(Employee::getSalary),
                    Paths.get(Employee::getId),
                    Paths.get(Employee::getEmail),
                    Paths.get(Employee::getActive)
            );

            // then
            QueryStructure structure = ((WhereImpl<?, ?>) result).getQueryStructure();
            SelectExpressions select = (SelectExpressions) structure.select();
            assertThat(select.distinct()).isTrue();
            assertThat(select.items()).hasSize(5);
        }

        /**
         * Test objective: Verify that selectDistinct with six TypedExpressions creates distinct Tuple6.
         * Test scenario: Call selectDistinct with 6 TypedExpressions.
         * Expected result: Returns WhereStep with distinct SelectExpressions.
         */
        @Test
        void selectDistinct_WithSixTypedExpressions_ShouldCreateDistinctTuple6() {
            // when
            var result = queryBuilder.selectDistinct(
                    Paths.get(Employee::getName),
                    Paths.get(Employee::getSalary),
                    Paths.get(Employee::getId),
                    Paths.get(Employee::getEmail),
                    Paths.get(Employee::getActive),
                    Paths.get(Employee::getStatus)
            );

            // then
            QueryStructure structure = ((WhereImpl<?, ?>) result).getQueryStructure();
            SelectExpressions select = (SelectExpressions) structure.select();
            assertThat(select.distinct()).isTrue();
            assertThat(select.items()).hasSize(6);
        }

        /**
         * Test objective: Verify that selectDistinct with seven TypedExpressions creates distinct Tuple7.
         * Test scenario: Call selectDistinct with 7 TypedExpressions.
         * Expected result: Returns WhereStep with distinct SelectExpressions.
         */
        @Test
        void selectDistinct_WithSevenTypedExpressions_ShouldCreateDistinctTuple7() {
            // when
            var result = queryBuilder.selectDistinct(
                    Paths.get(Employee::getName),
                    Paths.get(Employee::getSalary),
                    Paths.get(Employee::getId),
                    Paths.get(Employee::getEmail),
                    Paths.get(Employee::getActive),
                    Paths.get(Employee::getStatus),
                    Paths.get(Employee::getDepartmentId)
            );

            // then
            QueryStructure structure = ((WhereImpl<?, ?>) result).getQueryStructure();
            SelectExpressions select = (SelectExpressions) structure.select();
            assertThat(select.distinct()).isTrue();
            assertThat(select.items()).hasSize(7);
        }

        /**
         * Test objective: Verify that selectDistinct with eight TypedExpressions creates distinct Tuple8.
         * Test scenario: Call selectDistinct with 8 TypedExpressions.
         * Expected result: Returns WhereStep with distinct SelectExpressions.
         */
        @Test
        void selectDistinct_WithEightTypedExpressions_ShouldCreateDistinctTuple8() {
            // when
            var result = queryBuilder.selectDistinct(
                    Paths.get(Employee::getName),
                    Paths.get(Employee::getSalary),
                    Paths.get(Employee::getId),
                    Paths.get(Employee::getEmail),
                    Paths.get(Employee::getActive),
                    Paths.get(Employee::getStatus),
                    Paths.get(Employee::getDepartmentId),
                    Paths.get(Employee::getHireDate)
            );

            // then
            QueryStructure structure = ((WhereImpl<?, ?>) result).getQueryStructure();
            SelectExpressions select = (SelectExpressions) structure.select();
            assertThat(select.distinct()).isTrue();
            assertThat(select.items()).hasSize(8);
        }

        /**
         * Test objective: Verify that selectDistinct with nine TypedExpressions creates distinct Tuple9.
         * Test scenario: Call selectDistinct with 9 TypedExpressions.
         * Expected result: Returns WhereStep with distinct SelectExpressions.
         */
        @Test
        void selectDistinct_WithNineTypedExpressions_ShouldCreateDistinctTuple9() {
            // when
            var result = queryBuilder.selectDistinct(
                    Paths.get(Employee::getName),
                    Paths.get(Employee::getSalary),
                    Paths.get(Employee::getId),
                    Paths.get(Employee::getEmail),
                    Paths.get(Employee::getActive),
                    Paths.get(Employee::getStatus),
                    Paths.get(Employee::getDepartmentId),
                    Paths.get(Employee::getHireDate),
                    Paths.get(Employee::getCreatedAt)
            );

            // then
            QueryStructure structure = ((WhereImpl<?, ?>) result).getQueryStructure();
            SelectExpressions select = (SelectExpressions) structure.select();
            assertThat(select.distinct()).isTrue();
            assertThat(select.items()).hasSize(9);
        }

        /**
         * Test objective: Verify that selectDistinct with ten TypedExpressions creates distinct Tuple10.
         * Test scenario: Call selectDistinct with 10 TypedExpressions.
         * Expected result: Returns WhereStep with distinct SelectExpressions.
         */
        @Test
        void selectDistinct_WithTenTypedExpressions_ShouldCreateDistinctTuple10() {

            // when
            var result = queryBuilder.selectDistinct(
                    Paths.get(Employee::getName),
                    Paths.get(Employee::getSalary),
                    Paths.get(Employee::getId),
                    Paths.get(Employee::getEmail),
                    Paths.get(Employee::getActive),
                    Paths.get(Employee::getStatus),
                    Paths.get(Employee::getDepartmentId),
                    Paths.get(Employee::getHireDate),
                    Paths.get(Employee::getCreatedAt),
                    Paths.get(Employee::getName)
            );

            // then
            QueryStructure structure = ((WhereImpl<?, ?>) result).getQueryStructure();
            SelectExpressions select = (SelectExpressions) structure.select();
            assertThat(select.distinct()).isTrue();
            assertThat(select.items()).hasSize(10);
        }
    }

    @Nested
    class FetchOperations {

        /**
         * Test objective: Verify that fetch with null list returns same instance.
         * Test scenario: Call fetch with null.
         * Expected result: Returns the same QueryBuilder instance.
         */
        @Test
        void fetch_WithNullList_ShouldReturnSameInstance() {
            // when
            var result = queryBuilder.fetch((List<PathExpression<Employee, ?>>) null);

            // then
            assertThat(result).isSameAs(queryBuilder);
        }
    }

    @Nested
    class FromTypeValidation {

        /**
         * Test objective: Verify that operations work with FromEntity type.
         * Test scenario: Perform select operation on entity query.
         * Expected result: Operations complete successfully.
         */
        @Test
        void select_WithFromEntity_ShouldWork() {
            // when
            var result = queryBuilder.select(Employee::getName);

            // then
            assertThat(result).isNotNull();
        }

        /**
         * Test objective: Verify that fromType throws exception for unsupported From types.
         * Test scenario: Create QueryBuilder with FromSubQuery and call select with projection class.
         * Expected result: Throws UnsupportedOperationException.
         */
        @Test
        void fromType_WithFromSubQuery_SelectProjectionShouldThrowException() {
            // given
            QueryStructure subQueryStructure = new QueryStructure(
                    new SelectEntity(ImmutableList.of(), false),
                    new FromSubQuery(QueryStructure.of(Employee.class)),
                    EmptyNode.INSTANCE,
                    ImmutableList.of(),
                    ImmutableList.of(),
                    EmptyNode.INSTANCE,
                    null,
                    null,
                    LockModeType.NONE
            );
            QueryBuilder<Employee> subQueryBuilder = new QueryBuilder<>(subQueryStructure, metamodel, queryExecutor);

            // when/then - select with projection class requires fromType()
            assertThatThrownBy(() -> subQueryBuilder.select(String.class))
                    .isInstanceOf(UnsupportedOperationException.class)
                    .hasMessageContaining("FromSubQuery");
        }

        /**
         * Test objective: Verify that fetch on subquery builder works without calling fromType.
         * Test scenario: Call fetch with null on FromSubQuery builder.
         * Expected result: Returns same instance without exception.
         */
        @Test
        void fromType_WithFromSubQuery_FetchNullShouldWork() {
            // given
            QueryStructure subQueryStructure = new QueryStructure(
                    new SelectEntity(ImmutableList.of(), false),
                    new FromSubQuery(QueryStructure.of(Employee.class)),
                    EmptyNode.INSTANCE,
                    ImmutableList.of(),
                    ImmutableList.of(),
                    EmptyNode.INSTANCE,
                    null,
                    null,
                    LockModeType.NONE
            );
            QueryBuilder<Employee> subQueryBuilder = new QueryBuilder<>(subQueryStructure, metamodel, queryExecutor);

            // when - fetch with null doesn't require fromType()
            var result = subQueryBuilder.fetch((List<PathExpression<Employee, ?>>) null);

            // then
            assertThat(result).isSameAs(subQueryBuilder);
        }
    }

    @Nested
    class SelectTupleByExpr {

        /**
         * Test objective: Verify that selectTupleByExpr with expressions creates SelectExpressions.
         * Test scenario: Call select with TypedExpression arguments.
         * Expected result: Returns WhereStep with SelectExpressions.
         */
        @Test
        void selectTupleByExpr_WithExpressions_ShouldCreateSelectExpressions() {
            // given
            TypedExpression<Employee, String> nameExpr = Expressions.of("test");

            // when
            var result = queryBuilder.select(nameExpr, Expressions.of(123.0));

            // then
            assertThat(result).isNotNull();
            QueryStructure structure = ((WhereImpl<?, ?>) result).getQueryStructure();
            assertThat(structure.select()).isInstanceOf(SelectExpressions.class);
            SelectExpressions select = (SelectExpressions) structure.select();
            assertThat(select.distinct()).isFalse();
            assertThat(select.items()).hasSize(2);
        }

        /**
         * Test objective: Verify that select with List of TypedExpression creates SelectExpressions.
         * Test scenario: Call select with List of TypedExpression.
         * Expected result: Returns WhereStep with SelectExpressions.
         */
        @Test
        void select_WithListOfTypedExpression_ShouldCreateSelectExpressions() {
            // given
            List<TypedExpression<Employee, ?>> expressions = Arrays.asList(
                    Expressions.of("name"),
                    Expressions.of(100.0)
            );

            // when
            var result = queryBuilder.select(expressions);

            // then
            assertThat(result).isNotNull();
            QueryStructure structure = ((WhereImpl<?, ?>) result).getQueryStructure();
            assertThat(structure.select()).isInstanceOf(SelectExpressions.class);
            SelectExpressions select = (SelectExpressions) structure.select();
            assertThat(select.distinct()).isFalse();
            assertThat(select.items()).hasSize(2);
        }

        /**
         * Test objective: Verify that selectDistinct with List of TypedExpression sets distinct flag.
         * Test scenario: Call selectDistinct with List of TypedExpression.
         * Expected result: Returns WhereStep with distinct SelectExpressions.
         */
        @Test
        void selectDistinct_WithListOfTypedExpression_ShouldSetDistinctFlag() {
            // given
            List<TypedExpression<Employee, ?>> expressions = Arrays.asList(
                    Expressions.of("name"),
                    Expressions.of(100.0)
            );

            // when
            var result = queryBuilder.selectDistinct(expressions);

            // then
            assertThat(result).isNotNull();
            QueryStructure structure = ((WhereImpl<?, ?>) result).getQueryStructure();
            SelectExpressions select = (SelectExpressions) structure.select();
            assertThat(select.distinct()).isTrue();
            assertThat(select.items()).hasSize(2);
        }

        /**
         * Test objective: Verify that select with three TypedExpressions creates Tuple3.
         * Test scenario: Call select with 3 TypedExpressions.
         * Expected result: Returns WhereStep with SelectExpressions.
         */
        @Test
        void select_WithThreeTypedExpressions_ShouldCreateTuple3() {
            // when
            var result = queryBuilder.select(
                    Expressions.of("name"),
                    Expressions.of(100.0),
                    Expressions.of(1L)
            );

            // then
            assertThat(result).isNotNull();
            QueryStructure structure = ((WhereImpl<?, ?>) result).getQueryStructure();
            assertThat(structure.select()).isInstanceOf(SelectExpressions.class);
            SelectExpressions select = (SelectExpressions) structure.select();
            assertThat(select.distinct()).isFalse();
            assertThat(select.items()).hasSize(3);
        }

        /**
         * Test objective: Verify that select with four TypedExpressions creates Tuple4.
         * Test scenario: Call select with 4 TypedExpressions.
         * Expected result: Returns WhereStep with SelectExpressions.
         */
        @Test
        void select_WithFourTypedExpressions_ShouldCreateTuple4() {
            // when
            var result = queryBuilder.select(
                    Expressions.of("name"),
                    Expressions.of(100.0),
                    Expressions.of(1L),
                    Expressions.of("email")
            );

            // then
            assertThat(result).isNotNull();
            QueryStructure structure = ((WhereImpl<?, ?>) result).getQueryStructure();
            assertThat(structure.select()).isInstanceOf(SelectExpressions.class);
            SelectExpressions select = (SelectExpressions) structure.select();
            assertThat(select.distinct()).isFalse();
            assertThat(select.items()).hasSize(4);
        }

        /**
         * Test objective: Verify that select with five TypedExpressions creates Tuple5.
         * Test scenario: Call select with 5 TypedExpressions.
         * Expected result: Returns WhereStep with SelectExpressions.
         */
        @Test
        void select_WithFiveTypedExpressions_ShouldCreateTuple5() {
            // when
            var result = queryBuilder.select(
                    Expressions.of("name"),
                    Expressions.of(100.0),
                    Expressions.of(1L),
                    Expressions.of("email"),
                    Expressions.of(true)
            );

            // then
            assertThat(result).isNotNull();
            QueryStructure structure = ((WhereImpl<?, ?>) result).getQueryStructure();
            assertThat(structure.select()).isInstanceOf(SelectExpressions.class);
            SelectExpressions select = (SelectExpressions) structure.select();
            assertThat(select.distinct()).isFalse();
            assertThat(select.items()).hasSize(5);
        }

        /**
         * Test objective: Verify that select with six TypedExpressions creates Tuple6.
         * Test scenario: Call select with 6 TypedExpressions.
         * Expected result: Returns WhereStep with SelectExpressions.
         */
        @Test
        void select_WithSixTypedExpressions_ShouldCreateTuple6() {
            // when
            var result = queryBuilder.select(
                    Expressions.of("name"),
                    Expressions.of(100.0),
                    Expressions.of(1L),
                    Expressions.of("email"),
                    Expressions.of(true),
                    Expressions.of(2)
            );

            // then
            assertThat(result).isNotNull();
            QueryStructure structure = ((WhereImpl<?, ?>) result).getQueryStructure();
            assertThat(structure.select()).isInstanceOf(SelectExpressions.class);
            SelectExpressions select = (SelectExpressions) structure.select();
            assertThat(select.distinct()).isFalse();
            assertThat(select.items()).hasSize(6);
        }

        /**
         * Test objective: Verify that select with seven TypedExpressions creates Tuple7.
         * Test scenario: Call select with 7 TypedExpressions.
         * Expected result: Returns WhereStep with SelectExpressions.
         */
        @Test
        void select_WithSevenTypedExpressions_ShouldCreateTuple7() {
            // when
            var result = queryBuilder.select(
                    Expressions.of("name"),
                    Expressions.of(100.0),
                    Expressions.of(1L),
                    Expressions.of("email"),
                    Expressions.of(true),
                    Expressions.of(2),
                    Expressions.of(3L)
            );

            // then
            assertThat(result).isNotNull();
            QueryStructure structure = ((WhereImpl<?, ?>) result).getQueryStructure();
            assertThat(structure.select()).isInstanceOf(SelectExpressions.class);
            SelectExpressions select = (SelectExpressions) structure.select();
            assertThat(select.distinct()).isFalse();
            assertThat(select.items()).hasSize(7);
        }

        /**
         * Test objective: Verify that select with eight TypedExpressions creates Tuple8.
         * Test scenario: Call select with 8 TypedExpressions.
         * Expected result: Returns WhereStep with SelectExpressions.
         */
        @Test
        void select_WithEightTypedExpressions_ShouldCreateTuple8() {
            // when
            var result = queryBuilder.select(
                    Expressions.of("name"),
                    Expressions.of(100.0),
                    Expressions.of(1L),
                    Expressions.of("email"),
                    Expressions.of(true),
                    Expressions.of(2),
                    Expressions.of(3L),
                    Expressions.of("date")
            );

            // then
            assertThat(result).isNotNull();
            QueryStructure structure = ((WhereImpl<?, ?>) result).getQueryStructure();
            assertThat(structure.select()).isInstanceOf(SelectExpressions.class);
            SelectExpressions select = (SelectExpressions) structure.select();
            assertThat(select.distinct()).isFalse();
            assertThat(select.items()).hasSize(8);
        }

        /**
         * Test objective: Verify that select with nine TypedExpressions creates Tuple9.
         * Test scenario: Call select with 9 TypedExpressions.
         * Expected result: Returns WhereStep with SelectExpressions.
         */
        @Test
        void select_WithNineTypedExpressions_ShouldCreateTuple9() {
            // when
            var result = queryBuilder.select(
                    Expressions.of("name"),
                    Expressions.of(100.0),
                    Expressions.of(1L),
                    Expressions.of("email"),
                    Expressions.of(true),
                    Expressions.of(2),
                    Expressions.of(3L),
                    Expressions.of("date"),
                    Expressions.of("timestamp")
            );

            // then
            assertThat(result).isNotNull();
            QueryStructure structure = ((WhereImpl<?, ?>) result).getQueryStructure();
            assertThat(structure.select()).isInstanceOf(SelectExpressions.class);
            SelectExpressions select = (SelectExpressions) structure.select();
            assertThat(select.distinct()).isFalse();
            assertThat(select.items()).hasSize(9);
        }

        /**
         * Test objective: Verify that select with ten TypedExpressions creates Tuple10.
         * Test scenario: Call select with 10 TypedExpressions.
         * Expected result: Returns WhereStep with SelectExpressions.
         */
        @Test
        void select_WithTenTypedExpressions_ShouldCreateTuple10() {
            // when
            var result = queryBuilder.select(
                    Expressions.of("name"),
                    Expressions.of(100.0),
                    Expressions.of(1L),
                    Expressions.of("email"),
                    Expressions.of(true),
                    Expressions.of(2),
                    Expressions.of(3L),
                    Expressions.of("date"),
                    Expressions.of("timestamp"),
                    Expressions.of("extra")
            );

            // then
            assertThat(result).isNotNull();
            QueryStructure structure = ((WhereImpl<?, ?>) result).getQueryStructure();
            assertThat(structure.select()).isInstanceOf(SelectExpressions.class);
            SelectExpressions select = (SelectExpressions) structure.select();
            assertThat(select.distinct()).isFalse();
            assertThat(select.items()).hasSize(10);
        }
    }

    @Nested
    class SelectExpressionOperations {

        /**
         * Test objective: Verify that select with TypedExpression creates SelectExpression.
         * Test scenario: Call select with a single TypedExpression.
         * Expected result: Returns WhereStep with SelectExpression.
         */
        @Test
        void select_WithTypedExpression_ShouldCreateSelectExpression() {
            // when
            var result = queryBuilder.select(Expressions.of("test"));

            // then
            assertThat(result).isNotNull();
            QueryStructure structure = ((WhereImpl<?, ?>) result).getQueryStructure();
            assertThat(structure.select()).isInstanceOf(SelectExpression.class);
        }

        /**
         * Test objective: Verify that selectDistinct with TypedExpression sets distinct flag.
         * Test scenario: Call selectDistinct with a TypedExpression.
         * Expected result: Returns WhereStep with distinct SelectExpression.
         */
        @Test
        void selectDistinct_WithTypedExpression_ShouldSetDistinctFlag() {
            // when
            var result = queryBuilder.selectDistinct(Expressions.of(123));

            // then
            QueryStructure structure = ((WhereImpl<?, ?>) result).getQueryStructure();
            SelectExpression select = (SelectExpression) structure.select();
            assertThat(select.distinct()).isTrue();
        }

        /**
         * Test objective: Verify that selectDistinct with two TypedExpressions creates distinct Tuple2.
         * Test scenario: Call selectDistinct with 2 TypedExpressions.
         * Expected result: Returns WhereStep with distinct SelectExpressions.
         */
        @Test
        void selectDistinct_WithTwoTypedExpressions_ShouldCreateDistinctTuple2() {
            // when
            var result = queryBuilder.selectDistinct(
                    Expressions.of("name"),
                    Expressions.of(100.0)
            );

            // then
            QueryStructure structure = ((WhereImpl<?, ?>) result).getQueryStructure();
            SelectExpressions select = (SelectExpressions) structure.select();
            assertThat(select.distinct()).isTrue();
            assertThat(select.items()).hasSize(2);
        }

        /**
         * Test objective: Verify that selectDistinct with three TypedExpressions creates distinct Tuple3.
         * Test scenario: Call selectDistinct with 3 TypedExpressions.
         * Expected result: Returns WhereStep with distinct SelectExpressions.
         */
        @Test
        void selectDistinct_WithThreeTypedExpressions_ShouldCreateDistinctTuple3() {
            // when
            var result = queryBuilder.selectDistinct(
                    Expressions.of("name"),
                    Expressions.of(100.0),
                    Expressions.of(1L)
            );

            // then
            QueryStructure structure = ((WhereImpl<?, ?>) result).getQueryStructure();
            SelectExpressions select = (SelectExpressions) structure.select();
            assertThat(select.distinct()).isTrue();
            assertThat(select.items()).hasSize(3);
        }

        /**
         * Test objective: Verify that selectDistinct with four TypedExpressions creates distinct Tuple4.
         * Test scenario: Call selectDistinct with 4 TypedExpressions.
         * Expected result: Returns WhereStep with distinct SelectExpressions.
         */
        @Test
        void selectDistinct_WithFourTypedExpressions_ShouldCreateDistinctTuple4() {
            // when
            var result = queryBuilder.selectDistinct(
                    Expressions.of("name"),
                    Expressions.of(100.0),
                    Expressions.of(1L),
                    Expressions.of("email")
            );

            // then
            QueryStructure structure = ((WhereImpl<?, ?>) result).getQueryStructure();
            SelectExpressions select = (SelectExpressions) structure.select();
            assertThat(select.distinct()).isTrue();
            assertThat(select.items()).hasSize(4);
        }

        /**
         * Test objective: Verify that selectDistinct with five TypedExpressions creates distinct Tuple5.
         * Test scenario: Call selectDistinct with 5 TypedExpressions.
         * Expected result: Returns WhereStep with distinct SelectExpressions.
         */
        @Test
        void selectDistinct_WithFiveTypedExpressions_ShouldCreateDistinctTuple5() {
            // when
            var result = queryBuilder.selectDistinct(
                    Expressions.of("name"),
                    Expressions.of(100.0),
                    Expressions.of(1L),
                    Expressions.of("email"),
                    Expressions.of(true)
            );

            // then
            QueryStructure structure = ((WhereImpl<?, ?>) result).getQueryStructure();
            SelectExpressions select = (SelectExpressions) structure.select();
            assertThat(select.distinct()).isTrue();
            assertThat(select.items()).hasSize(5);
        }

        /**
         * Test objective: Verify that selectDistinct with six TypedExpressions creates distinct Tuple6.
         * Test scenario: Call selectDistinct with 6 TypedExpressions.
         * Expected result: Returns WhereStep with distinct SelectExpressions.
         */
        @Test
        void selectDistinct_WithSixTypedExpressions_ShouldCreateDistinctTuple6() {
            // when
            var result = queryBuilder.selectDistinct(
                    Expressions.of("name"),
                    Expressions.of(100.0),
                    Expressions.of(1L),
                    Expressions.of("email"),
                    Expressions.of(true),
                    Expressions.of(2)
            );

            // then
            QueryStructure structure = ((WhereImpl<?, ?>) result).getQueryStructure();
            SelectExpressions select = (SelectExpressions) structure.select();
            assertThat(select.distinct()).isTrue();
            assertThat(select.items()).hasSize(6);
        }

        /**
         * Test objective: Verify that selectDistinct with seven TypedExpressions creates distinct Tuple7.
         * Test scenario: Call selectDistinct with 7 TypedExpressions.
         * Expected result: Returns WhereStep with distinct SelectExpressions.
         */
        @Test
        void selectDistinct_WithSevenTypedExpressions_ShouldCreateDistinctTuple7() {
            // when
            var result = queryBuilder.selectDistinct(
                    Expressions.of("name"),
                    Expressions.of(100.0),
                    Expressions.of(1L),
                    Expressions.of("email"),
                    Expressions.of(true),
                    Expressions.of(2),
                    Expressions.of(3L)
            );

            // then
            QueryStructure structure = ((WhereImpl<?, ?>) result).getQueryStructure();
            SelectExpressions select = (SelectExpressions) structure.select();
            assertThat(select.distinct()).isTrue();
            assertThat(select.items()).hasSize(7);
        }

        /**
         * Test objective: Verify that selectDistinct with eight TypedExpressions creates distinct Tuple8.
         * Test scenario: Call selectDistinct with 8 TypedExpressions.
         * Expected result: Returns WhereStep with distinct SelectExpressions.
         */
        @Test
        void selectDistinct_WithEightTypedExpressions_ShouldCreateDistinctTuple8() {
            // when
            var result = queryBuilder.selectDistinct(
                    Expressions.of("name"),
                    Expressions.of(100.0),
                    Expressions.of(1L),
                    Expressions.of("email"),
                    Expressions.of(true),
                    Expressions.of(2),
                    Expressions.of(3L),
                    Expressions.of("date")
            );

            // then
            QueryStructure structure = ((WhereImpl<?, ?>) result).getQueryStructure();
            SelectExpressions select = (SelectExpressions) structure.select();
            assertThat(select.distinct()).isTrue();
            assertThat(select.items()).hasSize(8);
        }

        /**
         * Test objective: Verify that selectDistinct with nine TypedExpressions creates distinct Tuple9.
         * Test scenario: Call selectDistinct with 9 TypedExpressions.
         * Expected result: Returns WhereStep with distinct SelectExpressions.
         */
        @Test
        void selectDistinct_WithNineTypedExpressions_ShouldCreateDistinctTuple9() {
            // when
            var result = queryBuilder.selectDistinct(
                    Expressions.of("name"),
                    Expressions.of(100.0),
                    Expressions.of(1L),
                    Expressions.of("email"),
                    Expressions.of(true),
                    Expressions.of(2),
                    Expressions.of(3L),
                    Expressions.of("date"),
                    Expressions.of("timestamp")
            );

            // then
            QueryStructure structure = ((WhereImpl<?, ?>) result).getQueryStructure();
            SelectExpressions select = (SelectExpressions) structure.select();
            assertThat(select.distinct()).isTrue();
            assertThat(select.items()).hasSize(9);
        }

        /**
         * Test objective: Verify that selectDistinct with ten TypedExpressions creates distinct Tuple10.
         * Test scenario: Call selectDistinct with 10 TypedExpressions.
         * Expected result: Returns WhereStep with distinct SelectExpressions.
         */
        @Test
        void selectDistinct_WithTenTypedExpressions_ShouldCreateDistinctTuple10() {
            // when
            var result = queryBuilder.selectDistinct(
                    Expressions.of("name"),
                    Expressions.of(100.0),
                    Expressions.of(1L),
                    Expressions.of("email"),
                    Expressions.of(true),
                    Expressions.of(2),
                    Expressions.of(3L),
                    Expressions.of("date"),
                    Expressions.of("timestamp"),
                    Expressions.of("extra")
            );

            // then
            QueryStructure structure = ((WhereImpl<?, ?>) result).getQueryStructure();
            SelectExpressions select = (SelectExpressions) structure.select();
            assertThat(select.distinct()).isTrue();
            assertThat(select.items()).hasSize(10);
        }
    }

    @Nested
    class SameTypeSelect {

        /**
         * Test objective: Verify that select with same entity type returns updated query.
         * Test scenario: Call select with the same entity class.
         * Expected result: Returns WhereStep without changing select type.
         */
        @Test
        void select_WithSameEntityType_ShouldReturnUpdatedQuery() {
            // when
            var result = queryBuilder.select(Employee.class);

            // then
            assertThat(result).isNotNull();
            QueryStructure structure = ((WhereImpl<?, ?>) result).getQueryStructure();
            assertThat(structure.select()).isInstanceOf(SelectEntity.class);
        }
    }
}
