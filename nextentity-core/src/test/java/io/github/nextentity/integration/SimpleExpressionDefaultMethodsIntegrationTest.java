package io.github.nextentity.integration;

import io.github.nextentity.api.Path;
import io.github.nextentity.api.Predicate;
import io.github.nextentity.integration.config.IntegrationTestContext;
import io.github.nextentity.integration.config.IntegrationTestProvider;
import io.github.nextentity.integration.entity.Employee;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for SimpleExpression default methods.
 * <p>
 * Tests default methods in SimpleExpression interface including:
 * - Comparison operators with values (ge, gt, le, lt)
 * - Between operations with values
 * - Mixed between operations (expression and value combinations)
 * <p>
 * These tests run against MySQL and PostgreSQL using Testcontainers.
 *
 * @author HuangChengwei
 * @see io.github.nextentity.api.SimpleExpression
 */
@DisplayName("SimpleExpression Default Methods Integration Tests")
public class SimpleExpressionDefaultMethodsIntegrationTest {

    private static final double SALARY_THRESHOLD = 60000.0;

    // ==================== ge(U value) Tests ====================

    /**
     * Tests ge(U value) - greater than or equal with value parameter.
     * This is a default method that uses root().literal(value).
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with ge(value) - greater than or equal")
    void shouldFilterWithGeValue(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getSalary).ge(SALARY_THRESHOLD)
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getSalary() >= SALARY_THRESHOLD);
    }

    /**
     * Tests ge(U value) with boundary value.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle ge(value) with boundary value")
    void shouldHandleGeValueBoundary(IntegrationTestContext context) {
        // Given - find a specific salary to use as boundary
        Employee first = context.queryEmployees().getFirst();
        Double boundarySalary = first.getSalary();

        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getSalary).ge(boundarySalary)
                .orderBy(Employee::getSalary).asc()
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getSalary() >= boundarySalary);
    }

    // ==================== gt(U value) Tests ====================

    /**
     * Tests gt(U value) - greater than with value parameter.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with gt(value) - greater than")
    void shouldFilterWithGtValue(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getSalary).gt(SALARY_THRESHOLD)
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getSalary() > SALARY_THRESHOLD);
    }

    /**
     * Tests gt(U value) with ID field.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with gt(value) on ID field")
    void shouldFilterWithGtValueOnId(IntegrationTestContext context) {
        // Given
        Long idThreshold = 5L;

        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getId).gt(idThreshold)
                .orderBy(Employee::getId).asc()
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getId() > idThreshold);
    }

    // ==================== le(U value) Tests ====================

    /**
     * Tests le(U value) - less than or equal with value parameter.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with le(value) - less than or equal")
    void shouldFilterWithLeValue(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getSalary).le(SALARY_THRESHOLD)
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getSalary() <= SALARY_THRESHOLD);
    }

    /**
     * Tests le(U value) with boundary value.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle le(value) with boundary value")
    void shouldHandleLeValueBoundary(IntegrationTestContext context) {
        // Given - find a specific salary to use as boundary
        Employee first = context.queryEmployees().getFirst();
        Double boundarySalary = first.getSalary();

        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getSalary).le(boundarySalary)
                .orderBy(Employee::getSalary).desc()
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getSalary() <= boundarySalary);
    }

    // ==================== lt(U value) Tests ====================

    /**
     * Tests lt(U value) - less than with value parameter.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with lt(value) - less than")
    void shouldFilterWithLtValue(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getSalary).lt(SALARY_THRESHOLD)
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getSalary() < SALARY_THRESHOLD);
    }

    /**
     * Tests lt(U value) with ID field.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with lt(value) on ID field")
    void shouldFilterWithLtValueOnId(IntegrationTestContext context) {
        // Given
        Long idThreshold = 10L;

        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getId).lt(idThreshold)
                .orderBy(Employee::getId).asc()
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getId() < idThreshold);
    }

    // ==================== between(U l, U r) Tests ====================

    /**
     * Tests between(U l, U r) - between two values.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with between(value, value)")
    void shouldFilterWithBetweenValues(IntegrationTestContext context) {
        // Given
        double minSalary = 55000.0;
        double maxSalary = 75000.0;

        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getSalary).between(minSalary, maxSalary)
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e ->
                e.getSalary() >= minSalary && e.getSalary() <= maxSalary);
    }

    /**
     * Tests between(U l, U r) with ID field.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with between(value, value) on ID field")
    void shouldFilterWithBetweenValuesOnId(IntegrationTestContext context) {
        // Given
        Long minId = 3L;
        Long maxId = 7L;

        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getId).between(minId, maxId)
                .orderBy(Employee::getId).asc()
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getId() >= minId && e.getId() <= maxId);
    }

    /**
     * Tests between(U l, U r) with hire date.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with between(value, value) on date field")
    void shouldFilterWithBetweenValuesOnDate(IntegrationTestContext context) {
        // Given
        LocalDate minDate = LocalDate.of(2020, 1, 1);
        LocalDate maxDate = LocalDate.of(2023, 12, 31);

        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getHireDate).between(minDate, maxDate)
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e ->
                e.getHireDate() != null &&
                !e.getHireDate().isBefore(minDate) &&
                !e.getHireDate().isAfter(maxDate));
    }

    // ==================== notBetween(U l, U r) Tests ====================

    /**
     * Tests notBetween(U l, U r) - not between two values.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with notBetween(value, value)")
    void shouldFilterWithNotBetweenValues(IntegrationTestContext context) {
        // Given
        double minSalary = 55000.0;
        double maxSalary = 75000.0;

        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getSalary).notBetween(minSalary, maxSalary)
                .orderBy(Employee::getSalary).asc()
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e ->
                e.getSalary() < minSalary || e.getSalary() > maxSalary);
    }

    /**
     * Tests notBetween(U l, U r) with ID field.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with notBetween(value, value) on ID field")
    void shouldFilterWithNotBetweenValuesOnId(IntegrationTestContext context) {
        // Given
        Long minId = 3L;
        Long maxId = 7L;

        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getId).notBetween(minId, maxId)
                .orderBy(Employee::getId).asc()
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getId() < minId || e.getId() > maxId);
    }

    // ==================== between(TypedExpression, U) Tests ====================

    /**
     * Tests between(TypedExpression, U) - between expression and value.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with between(expression, value)")
    void shouldFilterWithBetweenExpressionAndValue(IntegrationTestContext context) {
        // Given
        double maxSalary = 75000.0;
        Double employee1Salary = context.queryEmployees()
                .select(Employee::getSalary)
                .where(Employee::getId).eq(1L)
                .getSingle();

        // When - salary between (salary of employee 1) and maxSalary
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getSalary).between(employee1Salary, maxSalary)
                .getList();

        // Then - should return employees with salary >= employee 1's salary and <= maxSalary
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getSalary() >= employee1Salary && e.getSalary() <= maxSalary);
    }

    // ==================== between(U, TypedExpression) Tests ====================

    /**
     * Tests between(U, TypedExpression) - between value and expression.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with between(value, expression)")
    void shouldFilterWithBetweenValueAndExpression(IntegrationTestContext context) {
        // Given
        double minSalary = 50000.0;
        Double employee1Salary = context.queryEmployees()
                .select(Employee::getSalary)
                .where(Employee::getId).eq(1L)
                .getSingle();

        // When - salary between minSalary and (salary of employee 1)
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getSalary).between(minSalary, employee1Salary)
                .getList();

        // Then - should return employees with salary >= minSalary
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getSalary() >= minSalary && e.getSalary() <= employee1Salary);
    }

    // ==================== notBetween(TypedExpression, U) Tests ====================

    /**
     * Tests notBetween(TypedExpression, U) - not between expression and value.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with notBetween(expression, value)")
    void shouldFilterWithNotBetweenExpressionAndValue(IntegrationTestContext context) {
        // Given
        double maxSalary = 80000.0;
        Double employee1Salary = context.queryEmployees()
                .select(Employee::getSalary)
                .where(Employee::getId).eq(1L)
                .getSingle();

        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getSalary).notBetween(employee1Salary, maxSalary)
                .orderBy(Employee::getSalary).asc()
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getSalary() < employee1Salary || e.getSalary() > maxSalary);
    }

    // ==================== notBetween(U, TypedExpression) Tests ====================

    /**
     * Tests notBetween(U, TypedExpression) - not between value and expression.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with notBetween(value, expression)")
    void shouldFilterWithNotBetweenValueAndExpression(IntegrationTestContext context) {
        // Given
        double minSalary = 50000.0;
        Double employee1Salary = context.queryEmployees()
                .select(Employee::getSalary)
                .where(Employee::getId).eq(1L)
                .getSingle();

        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getSalary).notBetween(minSalary, employee1Salary)
                .orderBy(Employee::getSalary).asc()
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getSalary() < minSalary || e.getSalary() > employee1Salary);
    }

    // ==================== Combined Tests ====================

    /**
     * Tests combining multiple comparison operators.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should combine multiple comparison operators")
    void shouldCombineMultipleComparisons(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getSalary).ge(50000.0)
                .where(Employee::getSalary).lt(80000.0)
                .where(Employee::getId).gt(1L)
                .orderBy(Employee::getSalary).asc()
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e ->
                e.getSalary() >= 50000.0 && e.getSalary() < 80000.0 && e.getId() > 1L);
    }

    /**
     * Tests combining between with other conditions.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should combine between with other conditions")
    void shouldCombineBetweenWithOtherConditions(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getSalary).between(50000.0, 80000.0)
                .where(Employee::getName).like("A%")
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e ->
                e.getSalary() >= 50000.0 && e.getSalary() <= 80000.0 && e.getName().startsWith("A"));
    }

    /**
     * Tests that comparison operators return correct count.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should count correctly with comparison operators")
    void shouldCountCorrectlyWithComparisons(IntegrationTestContext context) {
        // When
        long geCount = context.queryEmployees()
                .where(Employee::getSalary).ge(SALARY_THRESHOLD)
                .count();

        long gtCount = context.queryEmployees()
                .where(Employee::getSalary).gt(SALARY_THRESHOLD)
                .count();

        long leCount = context.queryEmployees()
                .where(Employee::getSalary).le(SALARY_THRESHOLD)
                .count();

        long ltCount = context.queryEmployees()
                .where(Employee::getSalary).lt(SALARY_THRESHOLD)
                .count();

        // Then - ge count should be >= gt count (since ge is inclusive)
        assertThat(geCount).isGreaterThanOrEqualTo(gtCount);
        assertThat(leCount).isGreaterThanOrEqualTo(ltCount);
    }

    // ==================== geIfNotNull/gtIfNotNull/leIfNotNull/ltIfNotNull Tests ====================

    @Nested
    @DisplayName("IfNotNull Operations Tests")
    class IfNotNullOperationsTests {

        /**
         * Tests: geIfNotNull with non-null value.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should filter with geIfNotNull when value is not null")
        void shouldFilterWithGeIfNotNullNonNull(IntegrationTestContext context) {
            // When
            List<Employee> employees = context.queryEmployees()
                    .where(Employee::getSalary).geIfNotNull(SALARY_THRESHOLD)
                    .orderBy(Employee::getId).asc()
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> e.getSalary() >= SALARY_THRESHOLD);
        }

        /**
         * Tests: geIfNotNull with null value - should skip condition.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should skip filter when geIfNotNull is null")
        void shouldSkipFilterWhenGeIfNotNullNull(IntegrationTestContext context) {
            // Given
            long totalCount = context.queryEmployees().count();

            // When
            List<Employee> employees = context.queryEmployees()
                    .where(Employee::getSalary).geIfNotNull(null)
                    .getList();

            // Then
            assertThat(employees).hasSize((int) totalCount);
        }

        /**
         * Tests: gtIfNotNull with non-null value.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should filter with gtIfNotNull when value is not null")
        void shouldFilterWithGtIfNotNullNonNull(IntegrationTestContext context) {
            // When
            List<Employee> employees = context.queryEmployees()
                    .where(Employee::getSalary).gtIfNotNull(SALARY_THRESHOLD)
                    .orderBy(Employee::getId).asc()
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> e.getSalary() > SALARY_THRESHOLD);
        }

        /**
         * Tests: gtIfNotNull with null value - should skip condition.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should skip filter when gtIfNotNull is null")
        void shouldSkipFilterWhenGtIfNotNullNull(IntegrationTestContext context) {
            // Given
            long totalCount = context.queryEmployees().count();

            // When
            List<Employee> employees = context.queryEmployees()
                    .where(Employee::getSalary).gtIfNotNull(null)
                    .getList();

            // Then
            assertThat(employees).hasSize((int) totalCount);
        }

        /**
         * Tests: leIfNotNull with non-null value.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should filter with leIfNotNull when value is not null")
        void shouldFilterWithLeIfNotNullNonNull(IntegrationTestContext context) {
            // When
            List<Employee> employees = context.queryEmployees()
                    .where(Employee::getSalary).leIfNotNull(SALARY_THRESHOLD)
                    .orderBy(Employee::getId).asc()
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> e.getSalary() <= SALARY_THRESHOLD);
        }

        /**
         * Tests: leIfNotNull with null value - should skip condition.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should skip filter when leIfNotNull is null")
        void shouldSkipFilterWhenLeIfNotNullNull(IntegrationTestContext context) {
            // Given
            long totalCount = context.queryEmployees().count();

            // When
            List<Employee> employees = context.queryEmployees()
                    .where(Employee::getSalary).leIfNotNull(null)
                    .getList();

            // Then
            assertThat(employees).hasSize((int) totalCount);
        }

        /**
         * Tests: ltIfNotNull with non-null value.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should filter with ltIfNotNull when value is not null")
        void shouldFilterWithLtIfNotNullNonNull(IntegrationTestContext context) {
            // When
            List<Employee> employees = context.queryEmployees()
                    .where(Employee::getSalary).ltIfNotNull(SALARY_THRESHOLD)
                    .orderBy(Employee::getId).asc()
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> e.getSalary() < SALARY_THRESHOLD);
        }

        /**
         * Tests: ltIfNotNull with null value - should skip condition.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should skip filter when ltIfNotNull is null")
        void shouldSkipFilterWhenLtIfNotNullNull(IntegrationTestContext context) {
            // Given
            long totalCount = context.queryEmployees().count();

            // When
            List<Employee> employees = context.queryEmployees()
                    .where(Employee::getSalary).ltIfNotNull(null)
                    .getList();

            // Then
            assertThat(employees).hasSize((int) totalCount);
        }

        /**
         * Tests: Combining multiple IfNotNull conditions.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should combine multiple IfNotNull conditions")
        void shouldCombineMultipleIfNotNullConditions(IntegrationTestContext context) {
            // When
            List<Employee> employees = context.queryEmployees()
                    .where(Employee::getSalary).geIfNotNull(50000.0)
                    .where(Employee::getSalary).leIfNotNull(80000.0)
                    .orderBy(Employee::getId).asc()
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> e.getSalary() >= 50000.0 && e.getSalary() <= 80000.0);
        }

        /**
         * Tests: Combining IfNotNull with null value (should skip).
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should skip null IfNotNull in combined conditions")
        void shouldSkipNullIfNotNullInCombinedConditions(IntegrationTestContext context) {
            // When - one null condition should be skipped
            List<Employee> employees = context.queryEmployees()
                    .where(Employee::getSalary).geIfNotNull(50000.0)
                    .where(Employee::getId).gtIfNotNull(null)
                    .orderBy(Employee::getId).asc()
                    .getList();

            // Then - should only filter by salary condition
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> e.getSalary() >= 50000.0);
        }
    }

    // ==================== Predicate as Query Condition Tests ====================

    @Nested
    @DisplayName("Predicate as Query Condition Tests")
    class PredicateAsQueryConditionTests {

        /**
         * Tests: ge(value) creates Predicate that can be passed to where().
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should use ge predicate as where condition")
        void shouldUseGePredicateAsWhereCondition(IntegrationTestContext context) {
            // Given - create predicate using ge
            Predicate<Employee> predicate = Path.of(Employee::getSalary).ge(SALARY_THRESHOLD);

            // When - pass predicate to where()
            List<Employee> employees = context.queryEmployees()
                    .where(predicate)
                    .orderBy(Employee::getId).asc()
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> e.getSalary() >= SALARY_THRESHOLD);
        }

        /**
         * Tests: gt(value) creates Predicate that can be passed to where().
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should use gt predicate as where condition")
        void shouldUseGtPredicateAsWhereCondition(IntegrationTestContext context) {
            // Given
            Predicate<Employee> predicate = Path.of(Employee::getSalary).gt(SALARY_THRESHOLD);

            // When
            List<Employee> employees = context.queryEmployees()
                    .where(predicate)
                    .orderBy(Employee::getId).asc()
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> e.getSalary() > SALARY_THRESHOLD);
        }

        /**
         * Tests: le(value) creates Predicate that can be passed to where().
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should use le predicate as where condition")
        void shouldUseLePredicateAsWhereCondition(IntegrationTestContext context) {
            // Given
            Predicate<Employee> predicate = Path.of(Employee::getSalary).le(SALARY_THRESHOLD);

            // When
            List<Employee> employees = context.queryEmployees()
                    .where(predicate)
                    .orderBy(Employee::getId).asc()
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> e.getSalary() <= SALARY_THRESHOLD);
        }

        /**
         * Tests: lt(value) creates Predicate that can be passed to where().
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should use lt predicate as where condition")
        void shouldUseLtPredicateAsWhereCondition(IntegrationTestContext context) {
            // Given
            Predicate<Employee> predicate = Path.of(Employee::getSalary).lt(SALARY_THRESHOLD);

            // When
            List<Employee> employees = context.queryEmployees()
                    .where(predicate)
                    .orderBy(Employee::getId).asc()
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> e.getSalary() < SALARY_THRESHOLD);
        }

        /**
         * Tests: between(value, value) creates Predicate that can be passed to where().
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should use between predicate as where condition")
        void shouldUseBetweenPredicateAsWhereCondition(IntegrationTestContext context) {
            // Given
            double minSalary = 55000.0;
            double maxSalary = 75000.0;
            Predicate<Employee> predicate = Path.of(Employee::getSalary).between(minSalary, maxSalary);

            // When
            List<Employee> employees = context.queryEmployees()
                    .where(predicate)
                    .orderBy(Employee::getId).asc()
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> e.getSalary() >= minSalary && e.getSalary() <= maxSalary);
        }

        /**
         * Tests: notBetween(value, value) creates Predicate that can be passed to where().
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should use notBetween predicate as where condition")
        void shouldUseNotBetweenPredicateAsWhereCondition(IntegrationTestContext context) {
            // Given
            double minSalary = 55000.0;
            double maxSalary = 75000.0;
            Predicate<Employee> predicate = Path.of(Employee::getSalary).notBetween(minSalary, maxSalary);

            // When
            List<Employee> employees = context.queryEmployees()
                    .where(predicate)
                    .orderBy(Employee::getSalary).asc()
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> e.getSalary() < minSalary || e.getSalary() > maxSalary);
        }

        /**
         * Tests: geIfNotNull creates Predicate that can be passed to where().
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should use geIfNotNull predicate as where condition")
        void shouldUseGeIfNotNullPredicateAsWhereCondition(IntegrationTestContext context) {
            // Given
            Predicate<Employee> predicate = Path.of(Employee::getSalary).geIfNotNull(SALARY_THRESHOLD);

            // When
            List<Employee> employees = context.queryEmployees()
                    .where(predicate)
                    .orderBy(Employee::getId).asc()
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> e.getSalary() >= SALARY_THRESHOLD);
        }

        /**
         * Tests: geIfNotNull with null creates empty Predicate.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should use geIfNotNull null predicate as where condition")
        void shouldUseGeIfNotNullNullPredicateAsWhereCondition(IntegrationTestContext context) {
            // Given
            long totalCount = context.queryEmployees().count();
            Predicate<Employee> predicate = Path.of(Employee::getSalary).geIfNotNull(null);

            // When
            List<Employee> employees = context.queryEmployees()
                    .where(predicate)
                    .getList();

            // Then
            assertThat(employees).hasSize((int) totalCount);
        }

        /**
         * Tests: gtIfNotNull creates Predicate that can be passed to where().
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should use gtIfNotNull predicate as where condition")
        void shouldUseGtIfNotNullPredicateAsWhereCondition(IntegrationTestContext context) {
            // Given
            Predicate<Employee> predicate = Path.of(Employee::getSalary).gtIfNotNull(SALARY_THRESHOLD);

            // When
            List<Employee> employees = context.queryEmployees()
                    .where(predicate)
                    .orderBy(Employee::getId).asc()
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> e.getSalary() > SALARY_THRESHOLD);
        }

        /**
         * Tests: leIfNotNull creates Predicate that can be passed to where().
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should use leIfNotNull predicate as where condition")
        void shouldUseLeIfNotNullPredicateAsWhereCondition(IntegrationTestContext context) {
            // Given
            Predicate<Employee> predicate = Path.of(Employee::getSalary).leIfNotNull(SALARY_THRESHOLD);

            // When
            List<Employee> employees = context.queryEmployees()
                    .where(predicate)
                    .orderBy(Employee::getId).asc()
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> e.getSalary() <= SALARY_THRESHOLD);
        }

        /**
         * Tests: ltIfNotNull creates Predicate that can be passed to where().
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should use ltIfNotNull predicate as where condition")
        void shouldUseLtIfNotNullPredicateAsWhereCondition(IntegrationTestContext context) {
            // Given
            Predicate<Employee> predicate = Path.of(Employee::getSalary).ltIfNotNull(SALARY_THRESHOLD);

            // When
            List<Employee> employees = context.queryEmployees()
                    .where(predicate)
                    .orderBy(Employee::getId).asc()
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> e.getSalary() < SALARY_THRESHOLD);
        }

        /**
         * Tests: Combining multiple comparison predicates with AND.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should combine multiple comparison predicates with AND")
        void shouldCombineMultiplePredicatesWithAnd(IntegrationTestContext context) {
            // Given
            Predicate<Employee> minSalaryPredicate = Path.of(Employee::getSalary).ge(50000.0);
            Predicate<Employee> maxSalaryPredicate = Path.of(Employee::getSalary).le(80000.0);
            Predicate<Employee> combined = minSalaryPredicate.and(maxSalaryPredicate);

            // When
            List<Employee> employees = context.queryEmployees()
                    .where(combined)
                    .orderBy(Employee::getId).asc()
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> e.getSalary() >= 50000.0 && e.getSalary() <= 80000.0);
        }

        /**
         * Tests: Combining multiple comparison predicates with OR.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should combine multiple comparison predicates with OR")
        void shouldCombineMultiplePredicatesWithOr(IntegrationTestContext context) {
            // Given
            Predicate<Employee> lowSalaryPredicate = Path.of(Employee::getSalary).lt(55000.0);
            Predicate<Employee> highSalaryPredicate = Path.of(Employee::getSalary).gt(75000.0);
            Predicate<Employee> combined = lowSalaryPredicate.or(highSalaryPredicate);

            // When
            List<Employee> employees = context.queryEmployees()
                    .where(combined)
                    .orderBy(Employee::getSalary).asc()
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> e.getSalary() < 55000.0 || e.getSalary() > 75000.0);
        }

        /**
         * Tests: Combining comparison predicate with other where conditions.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should combine comparison predicate with other where conditions")
        void shouldCombinePredicateWithOtherConditions(IntegrationTestContext context) {
            // Given
            Predicate<Employee> salaryPredicate = Path.of(Employee::getSalary).ge(50000.0);

            // When
            List<Employee> employees = context.queryEmployees()
                    .where(salaryPredicate)
                    .where(Employee::getName).like("A%")
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> e.getSalary() >= 50000.0 && e.getName().startsWith("A"));
        }

        /**
         * Tests: NOT on comparison predicate.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should use NOT on comparison predicate")
        void shouldUseNotOnComparisonPredicate(IntegrationTestContext context) {
            // Given
            Predicate<Employee> highSalary = Path.of(Employee::getSalary).gt(80000.0);

            // When
            List<Employee> employees = context.queryEmployees()
                    .where(highSalary.not())
                    .orderBy(Employee::getId).asc()
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> e.getSalary() <= 80000.0);
        }

        /**
         * Tests: Complex predicate with comparison expressions.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should create complex predicate with comparison expressions")
        void shouldCreateComplexPredicateWithComparisonExpressions(IntegrationTestContext context) {
            // Given: (salary >= 50000 AND salary <= 80000) OR id < 3
            Predicate<Employee> salaryRange = Path.of(Employee::getSalary).ge(50000.0)
                    .and(Path.of(Employee::getSalary).le(80000.0));
            Predicate<Employee> lowId = Path.of(Employee::getId).lt(3L);
            Predicate<Employee> complex = salaryRange.or(lowId);

            // When
            List<Employee> employees = context.queryEmployees()
                    .where(complex)
                    .orderBy(Employee::getId).asc()
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e ->
                    (e.getSalary() >= 50000.0 && e.getSalary() <= 80000.0) || e.getId() < 3L);
        }

        /**
         * Tests: between predicate with expressions.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should use between with expression as predicate")
        void shouldUseBetweenWithExpressionAsPredicate(IntegrationTestContext context) {
            // Given
            Double employee1Salary = context.queryEmployees()
                    .select(Employee::getSalary)
                    .where(Employee::getId).eq(1L)
                    .getSingle();
            Predicate<Employee> predicate = Path.of(Employee::getSalary).between(employee1Salary, 80000.0);

            // When
            List<Employee> employees = context.queryEmployees()
                    .where(predicate)
                    .orderBy(Employee::getSalary).asc()
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> e.getSalary() >= employee1Salary && e.getSalary() <= 80000.0);
        }
    }
}