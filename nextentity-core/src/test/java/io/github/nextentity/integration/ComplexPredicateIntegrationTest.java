package io.github.nextentity.integration;

import io.github.nextentity.api.Path;
import io.github.nextentity.api.Predicate;
import io.github.nextentity.integration.config.IntegrationTestContext;
import io.github.nextentity.integration.config.IntegrationTestProvider;
import io.github.nextentity.integration.entity.Department;
import io.github.nextentity.integration.entity.Employee;
import io.github.nextentity.integration.entity.EmployeeStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Complex predicate integration tests.
 * <p>
 * Tests complex predicate operations including:
 * - Complex AND/OR combinations
 * - Nested conditions
 * - NOT conditions
 * - Predicate combinations
 * <p>
 * These tests run against MySQL and PostgreSQL using Testcontainers.
 *
 * @author HuangChengwei
 */
@DisplayName("Complex Predicate Integration Tests")
public class ComplexPredicateIntegrationTest {

    /**
     * Tests simple AND condition.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with simple AND")
    void shouldFilterWithSimpleAnd(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getActive).eq(true)
                .where(Employee::getDepartmentId).eq(1L)
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getActive() && e.getDepartmentId() == 1L);
    }

    /**
     * Tests multiple AND conditions.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with multiple AND conditions")
    void shouldFilterWithMultipleAnd(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getActive).eq(true)
                .where(Employee::getStatus).eq(EmployeeStatus.ACTIVE)
                .where(Employee::getDepartmentId).eq(1L)
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e ->
                e.getActive() &&
                e.getStatus() == EmployeeStatus.ACTIVE &&
                e.getDepartmentId() == 1L);
    }

    /**
     * Tests OR condition using Predicate.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with OR condition")
    void shouldFilterWithOr(IntegrationTestContext context) {
        // Given
        Predicate<Employee> isAlice = Path.of(Employee::getName).eq("Alice Johnson");
        Predicate<Employee> isBob = Path.of(Employee::getName).eq("Bob Smith");

        // When
        List<Employee> employees = context.queryEmployees()
                .where(isAlice.or(isBob))
                .orderBy(Employee::getId).asc()
                .getList();

        // Then
        assertThat(employees).hasSize(2);
        assertThat(employees).extracting(Employee::getName)
                .containsExactlyInAnyOrder("Alice Johnson", "Bob Smith");
    }

    /**
     * Tests OR with different fields.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with OR on different fields")
    void shouldFilterWithOrDifferentFields(IntegrationTestContext context) {
        // Given
        Predicate<Employee> isHighSalary = Path.of(Employee::getSalary).gt(80000.0);
        Predicate<Employee> isDepartment1 = Path.of(Employee::getDepartmentId).eq(1L);

        // When
        List<Employee> employees = context.queryEmployees()
                .where(isHighSalary.or(isDepartment1))
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getSalary() > 80000.0 || e.getDepartmentId() == 1L);
    }

    /**
     * Tests NOT condition.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with NOT condition")
    void shouldFilterWithNot(IntegrationTestContext context) {
        // Given
        Predicate<Employee> isActive = Path.of(Employee::getActive).eq(true);

        // When
        List<Employee> employees = context.queryEmployees()
                .where(isActive.not())
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> !e.getActive());
    }

    /**
     * Tests complex AND-OR combination.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with complex AND-OR combination")
    void shouldFilterWithComplexAndOr(IntegrationTestContext context) {
        // Given: (active AND departmentId = 1) OR (salary > 80000)
        Predicate<Employee> activeAndDept1 = Path.of(Employee::getActive).eq(true)
                .and(Path.of(Employee::getDepartmentId).eq(1L));
        Predicate<Employee> highSalary = Path.of(Employee::getSalary).gt(80000.0);

        // When
        List<Employee> employees = context.queryEmployees()
                .where(activeAndDept1.or(highSalary))
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e ->
                (e.getActive() && e.getDepartmentId() == 1L) || e.getSalary() > 80000.0);
    }

    /**
     * Tests nested OR conditions.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with nested OR conditions")
    void shouldFilterWithNestedOr(IntegrationTestContext context) {
        // Given: name = 'Alice' OR (name = 'Bob' AND active = true)
        Predicate<Employee> isAlice = Path.of(Employee::getName).eq("Alice Johnson");
        Predicate<Employee> isBobAndActive = Path.of(Employee::getName).eq("Bob Smith")
                .and(Path.of(Employee::getActive).eq(true));

        // When
        List<Employee> employees = context.queryEmployees()
                .where(isAlice.or(isBobAndActive))
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e ->
                e.getName().equals("Alice Johnson") ||
                (e.getName().equals("Bob Smith") && e.getActive()));
    }

    /**
     * Tests combining Predicate with where clause.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should combine Predicate with where clause")
    void shouldCombinePredicateWithWhere(IntegrationTestContext context) {
        // Given
        Predicate<Employee> isDepartment1 = Path.of(Employee::getDepartmentId).eq(1L);

        // When
        List<Employee> employees = context.queryEmployees()
                .where(isDepartment1)
                .where(Employee::getActive).eq(true)
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getDepartmentId() == 1L && e.getActive());
    }

    /**
     * Tests multiple OR conditions.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with multiple OR conditions")
    void shouldFilterWithMultipleOr(IntegrationTestContext context) {
        // Given: name = 'Alice' OR name = 'Bob' OR name = 'Charlie'
        Predicate<Employee> isAlice = Path.of(Employee::getName).eq("Alice Johnson");
        Predicate<Employee> isBob = Path.of(Employee::getName).eq("Bob Smith");
        Predicate<Employee> isCharlie = Path.of(Employee::getName).eq("Charlie Brown");

        // When
        List<Employee> employees = context.queryEmployees()
                .where(isAlice.or(isBob).or(isCharlie))
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e ->
                e.getName().equals("Alice Johnson") ||
                e.getName().equals("Bob Smith") ||
                e.getName().equals("Charlie Brown"));
    }

    /**
     * Tests NOT with OR.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with NOT and OR")
    void shouldFilterWithNotAndOr(IntegrationTestContext context) {
        // Given: NOT (departmentId = 1 OR departmentId = 2)
        Predicate<Employee> dept1Or2 = Path.of(Employee::getDepartmentId).eq(1L)
                .or(Path.of(Employee::getDepartmentId).eq(2L));

        // When
        List<Employee> employees = context.queryEmployees()
                .where(dept1Or2.not())
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getDepartmentId() != 1L && e.getDepartmentId() != 2L);
    }

    /**
     * Tests complex nested predicates.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with complex nested predicates")
    void shouldFilterWithComplexNested(IntegrationTestContext context) {
        // Given: (active = true AND status = ACTIVE) OR (salary > 75000 AND departmentId = 1)
        Predicate<Employee> activeAndStatusActive = Path.of(Employee::getActive).eq(true)
                .and(Path.of(Employee::getStatus).eq(EmployeeStatus.ACTIVE));
        Predicate<Employee> highSalaryDept1 = Path.of(Employee::getSalary).gt(75000.0)
                .and(Path.of(Employee::getDepartmentId).eq(1L));

        // When
        List<Employee> employees = context.queryEmployees()
                .where(activeAndStatusActive.or(highSalaryDept1))
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e ->
                (e.getActive() && e.getStatus() == EmployeeStatus.ACTIVE) ||
                (e.getSalary() > 75000.0 && e.getDepartmentId() == 1L));
    }

    /**
     * Tests department predicate with employee filter.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter departments with predicates")
    void shouldFilterDepartmentsWithPredicates(IntegrationTestContext context) {
        // Given
        Predicate<Department> isActive = Path.of(Department::getActive).eq(true);
        Predicate<Department> highBudget = Path.of(Department::getBudget).gt(400000.0);

        // When
        List<Department> departments = context.queryDepartments()
                .where(isActive.and(highBudget))
                .getList();

        // Then
        assertThat(departments).isNotEmpty();
        assertThat(departments).allMatch(d -> d.getActive() && d.getBudget() > 400000.0);
    }

    /**
     * Tests combining predicates from different sources.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should combine predicates from different sources")
    void shouldCombinePredicatesFromDifferentSources(IntegrationTestContext context) {
        // Given
        Predicate<Employee> activePredicate = Path.of(Employee::getActive).eq(true);
        Predicate<Employee> statusPredicate = Path.of(Employee::getStatus).eq(EmployeeStatus.ACTIVE);
        Predicate<Employee> combined = activePredicate.and(statusPredicate);

        // When
        List<Employee> employees = context.queryEmployees()
                .where(combined)
                .where(Employee::getDepartmentId).eq(1L)
                .orderBy(Employee::getId).asc()
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e ->
                e.getActive() &&
                e.getStatus() == EmployeeStatus.ACTIVE &&
                e.getDepartmentId() == 1L);
    }

    /**
     * Tests predicate with IN clause.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with predicate and IN clause")
    void shouldFilterWithPredicateAndIn(IntegrationTestContext context) {
        // Given
        Predicate<Employee> activePredicate = Path.of(Employee::getActive).eq(true);

        // When
        List<Employee> employees = context.queryEmployees()
                .where(activePredicate)
                .where(Employee::getDepartmentId).in(1L, 2L, 3L)
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e ->
                e.getActive() && (e.getDepartmentId() == 1L || e.getDepartmentId() == 2L || e.getDepartmentId() == 3L));
    }

    /**
     * Tests predicate with LIKE.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with predicate and LIKE")
    void shouldFilterWithPredicateAndLike(IntegrationTestContext context) {
        // Given
        Predicate<Employee> activePredicate = Path.of(Employee::getActive).eq(true);

        // When
        List<Employee> employees = context.queryEmployees()
                .where(activePredicate)
                .where(Employee::getName).like("A%")
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getActive() && e.getName().startsWith("A"));
    }

    /**
     * Tests predicate with null checks.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with predicate and null check")
    void shouldFilterWithPredicateAndNull(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getEmail).isNotNull()
                .where(Employee::getDepartmentId).isNotNull()
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getEmail() != null && e.getDepartmentId() != null);
    }

    /**
     * Tests triple AND combination.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with triple AND")
    void shouldFilterWithTripleAnd(IntegrationTestContext context) {
        // Given
        Predicate<Employee> p1 = Path.of(Employee::getActive).eq(true);
        Predicate<Employee> p2 = Path.of(Employee::getStatus).eq(EmployeeStatus.ACTIVE);
        Predicate<Employee> p3 = Path.of(Employee::getDepartmentId).eq(1L);
        Predicate<Employee> combined = p1.and(p2).and(p3);

        // When
        List<Employee> employees = context.queryEmployees()
                .where(combined)
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e ->
                e.getActive() &&
                e.getStatus() == EmployeeStatus.ACTIVE &&
                e.getDepartmentId() == 1L);
    }

    /**
     * Tests predicate negation.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with predicate negation")
    void shouldFilterWithPredicateNegation(IntegrationTestContext context) {
        // Given: NOT (active = true AND status = ACTIVE)
        Predicate<Employee> activeAndStatus = Path.of(Employee::getActive).eq(true)
                .and(Path.of(Employee::getStatus).eq(EmployeeStatus.ACTIVE));

        // When
        List<Employee> employees = context.queryEmployees()
                .where(activeAndStatus.not())
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> !e.getActive() || e.getStatus() != EmployeeStatus.ACTIVE);
    }

    /**
     * Tests equality with boolean.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with boolean equality")
    void shouldFilterWithBooleanEquality(IntegrationTestContext context) {
        // When
        List<Employee> activeEmployees = context.queryEmployees()
                .where(Employee::getActive).eq(true)
                .getList();

        List<Employee> inactiveEmployees = context.queryEmployees()
                .where(Employee::getActive).eq(false)
                .getList();

        // Then
        assertThat(activeEmployees).isNotEmpty();
        assertThat(activeEmployees).allMatch(Employee::getActive);

        // May or may not have inactive employees depending on test data
        assertThat(inactiveEmployees).allMatch(e -> !e.getActive());
    }
}