package io.github.nextentity.integration;

import io.github.nextentity.api.Predicate;
import io.github.nextentity.api.model.Tuple2;
import io.github.nextentity.api.model.Tuple3;
import io.github.nextentity.api.model.Tuple4;
import io.github.nextentity.api.model.Tuple5;
import io.github.nextentity.integration.config.IntegrationTestContext;
import io.github.nextentity.integration.config.IntegrationTestProvider;
import io.github.nextentity.integration.entity.Department;
import io.github.nextentity.integration.entity.Employee;
import io.github.nextentity.integration.entity.EmployeeStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;

import static io.github.nextentity.core.util.Paths.get;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for QueryBuilder.
 */
class QueryBuilderIntegrationTest {

    // Test data constants - Employees
    private static final int TOTAL_TEST_EMPLOYEES = 12;
    private static final double MAX_SALARY = 85000.0;
    private static final double MIN_SALARY = 48000.0;
    private static final String ALICE_NAME = "Alice Johnson";
    private static final String BOB_NAME = "Bob Smith";

    // Test data constants - Departments
    private static final int TOTAL_DEPARTMENTS = 5;

    // Pagination constants
    private static final int PAGE_SIZE = 5;
    private static final int OFFSET = 3;

    // Salary range for between tests
    private static final double MIN_SALARY_RANGE = 60000.0;
    private static final double MAX_SALARY_RANGE = 75000.0;

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    void getList_ShouldReturnAllEntities(IntegrationTestContext context) {
        // when
        List<Employee> employees = context.queryEmployees().getList();

        // then
        assertThat(employees).isNotEmpty();
        assertThat(employees).hasSize(TOTAL_TEST_EMPLOYEES);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    void where_WithEqualityCondition_ShouldFilterResults(IntegrationTestContext context) {
        // when
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).eq(ALICE_NAME)
                .getList();

        // then
        assertThat(employees).hasSize(1);
        assertThat(employees.get(0).getName()).isEqualTo(ALICE_NAME);
    }

    /**
     * Tests multiple WHERE conditions with AND logic using Predicate.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    void where_WithMultipleConditions_ShouldFilterResults(IntegrationTestContext context) {
        // given
        Predicate<Employee> isActive = get(Employee::getActive).eq(true);

        // when
        List<Employee> employees = context.queryEmployees()
                .where(isActive.and(Employee::getStatus).eq(EmployeeStatus.ACTIVE))
                .getList();

        // then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getActive() && e.getStatus() == EmployeeStatus.ACTIVE);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    void orderBy_WithAscendingOrder_ShouldSortResults(IntegrationTestContext context) {
        // when
        List<Employee> employees = context.queryEmployees()
                .orderBy(Employee::getSalary).asc()
                .getList();

        // then
        assertThat(employees).isNotEmpty();
        for (int i = 1; i < employees.size(); i++) {
            assertThat(employees.get(i).getSalary())
                    .isGreaterThanOrEqualTo(employees.get(i - 1).getSalary());
        }
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    void orderBy_WithDescendingOrder_ShouldSortResults(IntegrationTestContext context) {
        // when
        List<Employee> employees = context.queryEmployees()
                .orderBy(Employee::getSalary).desc()
                .getList();

        // then
        assertThat(employees).isNotEmpty();
        for (int i = 1; i < employees.size(); i++) {
            assertThat(employees.get(i).getSalary())
                    .isLessThanOrEqualTo(employees.get(i - 1).getSalary());
        }
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    void getFirst_ShouldReturnFirstResult(IntegrationTestContext context) {
        // when
        Employee employee = context.queryEmployees()
                .orderBy(Employee::getSalary).desc()
                .getFirst();

        // then
        assertThat(employee).isNotNull();
        assertThat(employee.getSalary()).isEqualTo(MAX_SALARY);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    void limit_ShouldRestrictResultCount(IntegrationTestContext context) {
        // when
        List<Employee> employees = context.queryEmployees()
                .limit(PAGE_SIZE);

        // then
        assertThat(employees).hasSize(PAGE_SIZE);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    void offset_ShouldSkipResults(IntegrationTestContext context) {
        // given
        List<Employee> allEmployees = context.queryEmployees()
                .orderBy(Employee::getId).asc()
                .getList();

        // when
        List<Employee> pagedEmployees = context.queryEmployees()
                .orderBy(Employee::getId).asc()
                .getList(OFFSET, PAGE_SIZE);

        // then
        assertThat(pagedEmployees).hasSize(PAGE_SIZE);
        assertThat(pagedEmployees.get(0).getId()).isEqualTo(allEmployees.get(OFFSET).getId());
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    void select_WithSingleField_ShouldReturnProjectedResults(IntegrationTestContext context) {
        // when
        List<String> names = context.queryEmployees()
                .select(Employee::getName)
                .getList();

        // then
        assertThat(names).isNotEmpty();
        assertThat(names).hasSize(TOTAL_TEST_EMPLOYEES);
        assertThat(names).contains(ALICE_NAME, BOB_NAME);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    void select_WithMultipleFields_ShouldReturnTupleResults(IntegrationTestContext context) {
        // when
        List<Tuple2<String, Double>> results = context.queryEmployees()
                .select(Employee::getName, Employee::getSalary)
                .getList();

        // then
        assertThat(results).isNotEmpty();
        assertThat(results).hasSize(TOTAL_TEST_EMPLOYEES);

        Tuple2<String, Double> first = results.get(0);
        assertThat(first.get0()).isNotNull();
        assertThat(first.get1()).isNotNull();
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    void selectDistinct_ShouldReturnUniqueResults(IntegrationTestContext context) {
        // when
        List<Long> deptIds = context.queryEmployees()
                .selectDistinct(Employee::getDepartmentId)
                .getList();

        // then
        assertThat(deptIds).isNotEmpty();
        assertThat(deptIds).hasSize(TOTAL_DEPARTMENTS);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    void where_WithInClause_ShouldFilterResults(IntegrationTestContext context) {
        // when
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getDepartmentId).in(1L, 2L)
                .getList();

        // then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getDepartmentId() == 1L || e.getDepartmentId() == 2L);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    void where_WithBetweenClause_ShouldFilterResults(IntegrationTestContext context) {
        // when
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getSalary).between(MIN_SALARY_RANGE, MAX_SALARY_RANGE)
                .getList();

        // then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getSalary() >= MIN_SALARY_RANGE && e.getSalary() <= MAX_SALARY_RANGE);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    void where_WithLikeClause_ShouldFilterResults(IntegrationTestContext context) {
        // when
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getEmail).like("%@example.com")
                .getList();

        // then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getEmail().endsWith("@example.com"));
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    void where_WithIsNotNull_ShouldFilterResults(IntegrationTestContext context) {
        // when
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getDepartmentId).isNotNull()
                .getList();

        // then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getDepartmentId() != null);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    void select_WithCount_ShouldReturnCount(IntegrationTestContext context) {
        // when
        Long count = context.queryEmployees()
                .select(get(Employee::getId).count())
                .getFirst();

        // then
        assertThat(count).isEqualTo(TOTAL_TEST_EMPLOYEES);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    void select_WithSum_ShouldReturnSum(IntegrationTestContext context) {
        // when
        Double sum = context.queryEmployees()
                .select(get(Employee::getSalary).sum())
                .getFirst();

        // then
        assertThat(sum).isPositive();
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    void select_WithAvg_ShouldReturnAverage(IntegrationTestContext context) {
        // when
        Double avg = context.queryEmployees()
                .select(get(Employee::getSalary).avg())
                .getFirst();

        // then
        assertThat(avg).isPositive();
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    void select_WithMax_ShouldReturnMax(IntegrationTestContext context) {
        // when
        Double max = context.queryEmployees()
                .select(get(Employee::getSalary).max())
                .getFirst();

        // then
        assertThat(max).isEqualTo(MAX_SALARY);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    void select_WithMin_ShouldReturnMin(IntegrationTestContext context) {
        // when
        Double min = context.queryEmployees()
                .select(get(Employee::getSalary).min())
                .getFirst();

        // then
        assertThat(min).isEqualTo(MIN_SALARY);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    void where_WithOrCondition_ShouldFilterResults(IntegrationTestContext context) {
        // given
        Predicate<Employee> isAlice = get(Employee::getName).eq(ALICE_NAME);

        // when
        List<Employee> employees = context.queryEmployees()
                .where(isAlice.or(Employee::getName).eq(BOB_NAME))
                .getList();

        // then
        assertThat(employees).hasSize(2);
        assertThat(employees).extracting(Employee::getName)
                .containsExactlyInAnyOrder(ALICE_NAME, BOB_NAME);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    void query_Departments_ShouldReturnAllDepartments(IntegrationTestContext context) {
        // when
        List<Department> departments = context.queryDepartments().getList();

        // then
        assertThat(departments).hasSize(TOTAL_DEPARTMENTS);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    void select_WithProjectionType_ShouldReturnProjectedResults(IntegrationTestContext context) {
        // when
        List<Employee> employees = context.queryEmployees()
                .select(Employee.class)
                .where(Employee::getActive).eq(true)
                .getList();

        // then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(Employee::getActive);
    }

    // ==================== Multi-field Select Tests ====================

    @Nested
    @DisplayName("Multi-field Select Tests")
    class MultiFieldSelectTests {

        /**
         * Tests select with 3 fields returning Tuple3.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should select 3 fields into Tuple3")
        void shouldSelectThreeFieldsIntoTuple3(IntegrationTestContext context) {
            // When
            List<Tuple3<String, Double, Long>> results = context.queryEmployees()
                    .select(Employee::getName, Employee::getSalary, Employee::getId)
                    .orderBy(Employee::getId).asc()
                    .getList();

            // Then
            assertThat(results).isNotEmpty();
            assertThat(results).hasSize(TOTAL_TEST_EMPLOYEES);
            Tuple3<String, Double, Long> first = results.get(0);
            assertThat(first.get0()).isNotNull();
            assertThat(first.get1()).isNotNull();
            assertThat(first.get2()).isNotNull();
        }

        /**
         * Tests select with 4 fields returning Tuple4.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should select 4 fields into Tuple4")
        void shouldSelectFourFieldsIntoTuple4(IntegrationTestContext context) {
            // When
            List<Tuple4<String, Double, Long, String>> results = context.queryEmployees()
                    .select(Employee::getName, Employee::getSalary, Employee::getId, Employee::getEmail)
                    .orderBy(Employee::getId).asc()
                    .getList();

            // Then
            assertThat(results).isNotEmpty();
            assertThat(results).hasSize(TOTAL_TEST_EMPLOYEES);
            Tuple4<String, Double, Long, String> first = results.get(0);
            assertThat(first.get0()).isNotNull();
            assertThat(first.get1()).isNotNull();
            assertThat(first.get2()).isNotNull();
            assertThat(first.get3()).isNotNull();
        }

        /**
         * Tests select with 5 fields returning Tuple5.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should select 5 fields into Tuple5")
        void shouldSelectFiveFieldsIntoTuple5(IntegrationTestContext context) {
            // When
            List<Tuple5<String, Double, Long, String, Boolean>> results = context.queryEmployees()
                    .select(Employee::getName, Employee::getSalary, Employee::getId, Employee::getEmail, Employee::getActive)
                    .orderBy(Employee::getId).asc()
                    .getList();

            // Then
            assertThat(results).isNotEmpty();
            assertThat(results).hasSize(TOTAL_TEST_EMPLOYEES);
            Tuple5<String, Double, Long, String, Boolean> first = results.get(0);
            assertThat(first.get0()).isNotNull();
            assertThat(first.get1()).isNotNull();
            assertThat(first.get2()).isNotNull();
            assertThat(first.get3()).isNotNull();
            assertThat(first.get4()).isNotNull();
        }
    }

    // ==================== SelectDistinct Multi-field Tests ====================

    @Nested
    @DisplayName("SelectDistinct Multi-field Tests")
    class SelectDistinctMultiFieldTests {

        /**
         * Tests selectDistinct with 2 fields.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should selectDistinct 2 fields")
        void shouldSelectDistinctTwoFields(IntegrationTestContext context) {
            // When
            List<Tuple2<Long, Boolean>> results = context.queryEmployees()
                    .selectDistinct(Employee::getDepartmentId, Employee::getActive)
                    .getList();

            // Then
            assertThat(results).isNotEmpty();
            // Verify distinctness by counting unique combinations
            long distinctCount = results.stream().distinct().count();
            assertThat(results.size()).isEqualTo(distinctCount);
        }

        /**
         * Tests selectDistinct with 3 fields.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should selectDistinct 3 fields")
        void shouldSelectDistinctThreeFields(IntegrationTestContext context) {
            // When
            var results = context.queryEmployees()
                    .selectDistinct(Employee::getDepartmentId, Employee::getActive, Employee::getStatus)
                    .getList();

            // Then
            assertThat(results).isNotEmpty();
            // Verify distinctness
            long distinctCount = results.stream().distinct().count();
            assertThat(results.size()).isEqualTo(distinctCount);
        }
    }

    // ==================== Select with Expressions Tests ====================

    @Nested
    @DisplayName("Select with Expressions Tests")
    class SelectWithExpressionsTests {

        /**
         * Tests select with aggregate expression.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should select with count expression")
        void shouldSelectWithCountExpression(IntegrationTestContext context) {
            // When
            Long count = context.queryEmployees()
                    .select(get(Employee::getId).count())
                    .getFirst();

            // Then
            assertThat(count).isEqualTo(TOTAL_TEST_EMPLOYEES);
        }

        /**
         * Tests select with sum expression.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should select with sum expression")
        void shouldSelectWithSumExpression(IntegrationTestContext context) {
            // When
            Double sum = context.queryEmployees()
                    .select(get(Employee::getSalary).sum())
                    .getFirst();

            // Then
            assertThat(sum).isPositive();
        }

        /**
         * Tests select with multiple expressions.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should select with multiple expressions")
        void shouldSelectWithMultipleExpressions(IntegrationTestContext context) {
            // When
            var result = context.queryEmployees()
                    .select(
                            get(Employee::getSalary).avg(),
                            get(Employee::getSalary).max(),
                            get(Employee::getSalary).min()
                    )
                    .getFirst();

            // Then
            assertThat(result).isNotNull();
            assertThat((Double) result.get0()).isPositive(); // avg
            assertThat((Double) result.get1()).isEqualTo(MAX_SALARY); // max
            assertThat((Double) result.get2()).isEqualTo(MIN_SALARY); // min
        }

        /**
         * Tests select with arithmetic expression.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should select with arithmetic expression")
        void shouldSelectWithArithmeticExpression(IntegrationTestContext context) {
            // When - select salary + 1000
            List<Double> salaries = context.queryEmployees()
                    .select(get(Employee::getSalary).add(1000.0))
                    .orderBy(Employee::getId).asc()
                    .getList();

            // Then
            assertThat(salaries).isNotEmpty();
            // Verify the arithmetic was applied
            List<Double> originalSalaries = context.queryEmployees()
                    .select(Employee::getSalary)
                    .orderBy(Employee::getId).asc()
                    .getList();
            for (int i = 0; i < salaries.size(); i++) {
                assertThat(salaries.get(i)).isEqualTo(originalSalaries.get(i) + 1000.0);
            }
        }
    }

    // ==================== Predicate as Query Condition Tests ====================

    @Nested
    @DisplayName("Predicate as Query Condition Tests")
    class PredicateAsQueryConditionTests {

        /**
         * Tests using eq predicate as query condition.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should use eq predicate as where condition")
        void shouldUseEqPredicateAsWhereCondition(IntegrationTestContext context) {
            // Given
            Predicate<Employee> predicate = get(Employee::getName).eq(ALICE_NAME);

            // When
            List<Employee> employees = context.queryEmployees()
                    .where(predicate)
                    .getList();

            // Then
            assertThat(employees).hasSize(1);
            assertThat(employees.get(0).getName()).isEqualTo(ALICE_NAME);
        }

        /**
         * Tests using gt predicate as query condition.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should use gt predicate as where condition")
        void shouldUseGtPredicateAsWhereCondition(IntegrationTestContext context) {
            // Given
            Predicate<Employee> predicate = get(Employee::getSalary).gt(70000.0);

            // When
            List<Employee> employees = context.queryEmployees()
                    .where(predicate)
                    .orderBy(Employee::getSalary).asc()
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> e.getSalary() > 70000.0);
        }

        /**
         * Tests using between predicate as query condition.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should use between predicate as where condition")
        void shouldUseBetweenPredicateAsWhereCondition(IntegrationTestContext context) {
            // Given
            Predicate<Employee> predicate = get(Employee::getSalary).between(MIN_SALARY_RANGE, MAX_SALARY_RANGE);

            // When
            List<Employee> employees = context.queryEmployees()
                    .where(predicate)
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> e.getSalary() >= MIN_SALARY_RANGE && e.getSalary() <= MAX_SALARY_RANGE);
        }

        /**
         * Tests combining multiple predicates with AND.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should combine predicates with AND")
        void shouldCombinePredicatesWithAnd(IntegrationTestContext context) {
            // Given
            Predicate<Employee> activePredicate = get(Employee::getActive).eq(true);
            Predicate<Employee> salaryPredicate = get(Employee::getSalary).gt(50000.0);
            Predicate<Employee> combined = activePredicate.and(salaryPredicate);

            // When
            List<Employee> employees = context.queryEmployees()
                    .where(combined)
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> e.getActive() && e.getSalary() > 50000.0);
        }

        /**
         * Tests combining multiple predicates with OR.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should combine predicates with OR")
        void shouldCombinePredicatesWithOr(IntegrationTestContext context) {
            // Given
            Predicate<Employee> isAlice = get(Employee::getName).eq(ALICE_NAME);
            Predicate<Employee> isBob = get(Employee::getName).eq(BOB_NAME);
            Predicate<Employee> combined = isAlice.or(isBob);

            // When
            List<Employee> employees = context.queryEmployees()
                    .where(combined)
                    .getList();

            // Then
            assertThat(employees).hasSize(2);
            assertThat(employees).extracting(Employee::getName)
                    .containsExactlyInAnyOrder(ALICE_NAME, BOB_NAME);
        }

        /**
         * Tests NOT on predicate.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should use NOT on predicate")
        void shouldUseNotOnPredicate(IntegrationTestContext context) {
            // Given
            Predicate<Employee> isActive = get(Employee::getActive).eq(true);

            // When
            List<Employee> employees = context.queryEmployees()
                    .where(isActive.not())
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> !e.getActive());
        }

        /**
         * Tests complex predicate expression.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should create complex predicate expression")
        void shouldCreateComplexPredicateExpression(IntegrationTestContext context) {
            // Given: (active AND salary > 60000) OR name = 'Alice Johnson'
            Predicate<Employee> activeHighSalary = get(Employee::getActive).eq(true)
                    .and(get(Employee::getSalary).gt(60000.0));
            Predicate<Employee> isAlice = get(Employee::getName).eq(ALICE_NAME);
            Predicate<Employee> complex = activeHighSalary.or(isAlice);

            // When
            List<Employee> employees = context.queryEmployees()
                    .where(complex)
                    .orderBy(Employee::getId).asc()
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e ->
                    (e.getActive() && e.getSalary() > 60000.0) || e.getName().equals(ALICE_NAME));
        }
    }
}
