package io.github.nextentity.core;

import io.github.nextentity.api.Path;
import io.github.nextentity.api.PathExpression;
import io.github.nextentity.api.TypedExpression;
import io.github.nextentity.api.model.LockModeType;
import io.github.nextentity.core.expression.*;
import io.github.nextentity.core.meta.Metamodel;
import io.github.nextentity.core.util.ImmutableList;
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
import static org.mockito.ArgumentMatchers.any;

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
