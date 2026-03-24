package io.github.nextentity.integration;

import io.github.nextentity.api.Predicate;
import io.github.nextentity.api.TypedExpression;
import io.github.nextentity.integration.config.DbConfig;
import io.github.nextentity.integration.config.IntegrationTestProvider;
import io.github.nextentity.integration.entity.Department;
import io.github.nextentity.integration.entity.Employee;
import io.github.nextentity.integration.entity.EmployeeStatus;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.Arrays;
import java.util.List;

import static io.github.nextentity.core.util.Paths.get;
import static io.github.nextentity.core.util.Predicates.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for AND/OR predicate chaining operations.
 * <p>
 * Tests cover:
 * - Simple AND/OR chaining using Predicates utility
 * - Multiple condition combinations
 * - Nested AND/OR predicates
 * - Complex boolean logic combinations
 * - Edge cases with NOT operations
 * <p>
 * These tests run against MySQL and PostgreSQL using Testcontainers.
 *
 * @author HuangChengwei
 */
@DisplayName("AND/OR Predicate Chaining Integration Tests")
public class AndOrPredicateIntegrationTest {

    // Test data constants
    private static final String ALICE_NAME = "Alice Johnson";
    private static final String BOB_NAME = "Bob Smith";
    private static final String CHARLIE_NAME = "Charlie Brown";
    private static final String DIANA_NAME = "Diana Prince";
    private static final double HIGH_SALARY_THRESHOLD = 80000.0;
    private static final double LOW_SALARY_THRESHOLD = 55000.0;

    @Nested
    @DisplayName("AND Predicate Tests")
    class AndPredicateTests {

        /**
         * Tests simple AND using Predicates.and().
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should combine conditions with AND using Predicates.and()")
        void shouldCombineWithAndUsingPredicates(DbConfig config) {
            // When: active AND departmentId = 1
            List<Employee> employees = config.queryEmployees()
                    .where(and(
                            get(Employee::getActive).eq(true),
                            get(Employee::getDepartmentId).eq(1L)
                    ))
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
        @DisplayName("Should combine multiple conditions with AND")
        void shouldCombineMultipleWithAnd(DbConfig config) {
            // When: active AND status = ACTIVE AND salary > 55000
            List<Employee> employees = config.queryEmployees()
                    .where(and(
                            get(Employee::getActive).eq(true),
                            get(Employee::getStatus).eq(EmployeeStatus.ACTIVE),
                            get(Employee::getSalary).gt(LOW_SALARY_THRESHOLD)
                    ))
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e ->
                    e.getActive() &&
                    e.getStatus() == EmployeeStatus.ACTIVE &&
                    e.getSalary() > LOW_SALARY_THRESHOLD);
        }

        /**
         * Tests AND with number range conditions.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should combine number range conditions with AND")
        void shouldCombineNumberRangesWithAnd(DbConfig config) {
            // When: salary > 60000 AND salary < 80000
            List<Employee> employees = config.queryEmployees()
                    .where(and(
                            get(Employee::getSalary).gt(60000.0),
                            get(Employee::getSalary).lt(HIGH_SALARY_THRESHOLD)
                    ))
                    .orderBy(Employee::getSalary).asc()
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> e.getSalary() > 60000.0 && e.getSalary() < 80000.0);
        }

        /**
         * Tests AND with string conditions.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should combine string conditions with AND")
        void shouldCombineStringConditionsWithAnd(DbConfig config) {
            // When: name starts with 'A' AND active = true
            List<Employee> employees = config.queryEmployees()
                    .where(and(
                            get(Employee::getName).startsWith("A"),
                            get(Employee::getActive).eq(true)
                    ))
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> e.getName().startsWith("A") && e.getActive());
        }

        /**
         * Tests AND with Department entity.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should combine Department conditions with AND")
        void shouldCombineDepartmentConditionsWithAnd(DbConfig config) {
            // When: active AND budget > 300000
            List<Department> departments = config.queryDepartments()
                    .where(and(
                            get(Department::getActive).eq(true),
                            get(Department::getBudget).gt(300000.0)
                    ))
                    .getList();

            // Then
            assertThat(departments).isNotEmpty();
            assertThat(departments).allMatch(d -> d.getActive() && d.getBudget() > 300000.0);
        }

        /**
         * Tests deep AND nesting with four conditions.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should handle deep AND nesting")
        void shouldHandleDeepAndNesting(DbConfig config) {
            // Given: active AND status AND dept AND salary > threshold
            List<Employee> employees = config.queryEmployees()
                    .where(and(
                            get(Employee::getActive).eq(true),
                            get(Employee::getStatus).eq(EmployeeStatus.ACTIVE),
                            get(Employee::getDepartmentId).eq(1L),
                            get(Employee::getSalary).gt(LOW_SALARY_THRESHOLD)
                    ))
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e ->
                    e.getActive() &&
                    e.getStatus() == EmployeeStatus.ACTIVE &&
                    e.getDepartmentId() == 1L &&
                    e.getSalary() > LOW_SALARY_THRESHOLD);
        }
    }

    @Nested
    @DisplayName("OR Predicate Tests")
    class OrPredicateTests {

        /**
         * Tests simple OR using Predicates.or().
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should combine conditions with OR using Predicates.or()")
        void shouldCombineWithOrUsingPredicates(DbConfig config) {
            // When: name = 'Alice' OR name = 'Bob'
            List<Employee> employees = config.queryEmployees()
                    .where(or(
                            get(Employee::getName).eq(ALICE_NAME),
                            get(Employee::getName).eq(BOB_NAME)
                    ))
                    .orderBy(Employee::getId).asc()
                    .getList();

            // Then
            assertThat(employees).hasSize(2);
            assertThat(employees).extracting(Employee::getName)
                    .containsExactlyInAnyOrder(ALICE_NAME, BOB_NAME);
        }

        /**
         * Tests multiple OR conditions.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should combine multiple conditions with OR")
        void shouldCombineMultipleWithOr(DbConfig config) {
            // When: name = 'Alice' OR name = 'Bob' OR name = 'Charlie'
            List<Employee> employees = config.queryEmployees()
                    .where(or(
                            get(Employee::getName).eq(ALICE_NAME),
                            get(Employee::getName).eq(BOB_NAME),
                            get(Employee::getName).eq(CHARLIE_NAME)
                    ))
                    .orderBy(Employee::getId).asc()
                    .getList();

            // Then
            assertThat(employees).hasSize(3);
            assertThat(employees).extracting(Employee::getName)
                    .containsExactlyInAnyOrder(ALICE_NAME, BOB_NAME, CHARLIE_NAME);
        }

        /**
         * Tests OR with different fields.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should combine different field conditions with OR")
        void shouldCombineDifferentFieldsWithOr(DbConfig config) {
            // When: salary > 80000 OR departmentId = 1
            List<Employee> employees = config.queryEmployees()
                    .where(or(
                            get(Employee::getSalary).gt(HIGH_SALARY_THRESHOLD),
                            get(Employee::getDepartmentId).eq(1L)
                    ))
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> e.getSalary() > HIGH_SALARY_THRESHOLD || e.getDepartmentId() == 1L);
        }

        /**
         * Tests OR with number range conditions.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should combine number range conditions with OR")
        void shouldCombineNumberRangesWithOr(DbConfig config) {
            // When: salary < 50000 OR salary > 80000
            List<Employee> employees = config.queryEmployees()
                    .where(or(
                            get(Employee::getSalary).lt(50000.0),
                            get(Employee::getSalary).gt(HIGH_SALARY_THRESHOLD)
                    ))
                    .orderBy(Employee::getSalary).asc()
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> e.getSalary() < 50000.0 || e.getSalary() > HIGH_SALARY_THRESHOLD);
        }

        /**
         * Tests OR with string conditions.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should combine string conditions with OR")
        void shouldCombineStringConditionsWithOr(DbConfig config) {
            // When: name starts with 'A' OR name starts with 'B'
            List<Employee> employees = config.queryEmployees()
                    .where(or(
                            get(Employee::getName).startsWith("A"),
                            get(Employee::getName).startsWith("B")
                    ))
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> e.getName().startsWith("A") || e.getName().startsWith("B"));
        }

        /**
         * Tests OR with Department entity.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should combine Department conditions with OR")
        void shouldCombineDepartmentConditionsWithOr(DbConfig config) {
            // When: id = 1 OR id = 2
            List<Department> departments = config.queryDepartments()
                    .where(or(
                            get(Department::getId).eq(1L),
                            get(Department::getId).eq(2L)
                    ))
                    .orderBy(Department::getId).asc()
                    .getList();

            // Then
            assertThat(departments).hasSize(2);
            assertThat(departments).extracting(Department::getId)
                    .containsExactlyInAnyOrder(1L, 2L);
        }

        /**
         * Tests deep OR nesting with four conditions.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should handle deep OR nesting")
        void shouldHandleDeepOrNesting(DbConfig config) {
            // Given: name = 'Alice' OR 'Bob' OR 'Charlie' OR 'Diana'
            List<Employee> employees = config.queryEmployees()
                    .where(or(
                            get(Employee::getName).eq(ALICE_NAME),
                            get(Employee::getName).eq(BOB_NAME),
                            get(Employee::getName).eq(CHARLIE_NAME),
                            get(Employee::getName).eq(DIANA_NAME)
                    ))
                    .orderBy(Employee::getId).asc()
                    .getList();

            // Then
            assertThat(employees).hasSize(4);
            assertThat(employees).extracting(Employee::getName)
                    .containsExactlyInAnyOrder(ALICE_NAME, BOB_NAME, CHARLIE_NAME, DIANA_NAME);
        }
    }

    @Nested
    @DisplayName("Mixed AND/OR Combination Tests")
    class MixedAndOrTests {

        /**
         * Tests OR nested inside AND (similar to testAndOr2 pattern).
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should handle OR nested inside AND")
        void shouldHandleOrNestedInAnd(DbConfig config) {
            // Given: active AND (departmentId = 1 OR departmentId = 2)
            List<Employee> employees = config.queryEmployees()
                    .where(and(
                            get(Employee::getActive).eq(true),
                            or(
                                    get(Employee::getDepartmentId).eq(1L),
                                    get(Employee::getDepartmentId).eq(2L)
                            )
                    ))
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e ->
                    e.getActive() && (e.getDepartmentId() == 1L || e.getDepartmentId() == 2L));
        }

        /**
         * Tests AND nested inside OR.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should handle AND nested inside OR")
        void shouldHandleAndNestedInOr(DbConfig config) {
            // Given: (active AND departmentId = 1) OR (salary > 80000)
            List<Employee> employees = config.queryEmployees()
                    .where(or(
                            and(
                                    get(Employee::getActive).eq(true),
                                    get(Employee::getDepartmentId).eq(1L)
                            ),
                            get(Employee::getSalary).gt(HIGH_SALARY_THRESHOLD)
                    ))
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e ->
                    (e.getActive() && e.getDepartmentId() == 1L) || e.getSalary() > HIGH_SALARY_THRESHOLD);
        }

        /**
         * Tests complex mixed AND/OR pattern similar to testAndOr2.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should handle complex mixed AND/OR pattern like testAndOr2")
        void shouldHandleComplexMixedAndOrPattern(DbConfig config) {
            // Given: active AND status = ACTIVE AND salary > 60000 AND
            //        (departmentId = 1 OR departmentId = 2 OR name = 'Alice')
            List<Employee> employees = config.queryEmployees()
                    .where(and(
                            get(Employee::getActive).eq(true),
                            get(Employee::getStatus).eq(EmployeeStatus.ACTIVE),
                            get(Employee::getSalary).gt(60000.0),
                            or(
                                    get(Employee::getDepartmentId).eq(1L),
                                    get(Employee::getDepartmentId).eq(2L),
                                    get(Employee::getName).eq(ALICE_NAME)
                            )
                    ))
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e ->
                    e.getActive() &&
                    e.getStatus() == EmployeeStatus.ACTIVE &&
                    e.getSalary() > 60000.0 &&
                    (e.getDepartmentId() == 1L || e.getDepartmentId() == 2L || e.getName().equals(ALICE_NAME)));
        }

        /**
         * Tests triple nested combination.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should handle triple nested combination")
        void shouldHandleTripleNestedCombination(DbConfig config) {
            // Given: (active AND departmentId = 1) OR (salary > 80000 AND status = ACTIVE)
            List<Employee> employees = config.queryEmployees()
                    .where(or(
                            and(
                                    get(Employee::getActive).eq(true),
                                    get(Employee::getDepartmentId).eq(1L)
                            ),
                            and(
                                    get(Employee::getSalary).gt(HIGH_SALARY_THRESHOLD),
                                    get(Employee::getStatus).eq(EmployeeStatus.ACTIVE)
                            )
                    ))
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e ->
                    (e.getActive() && e.getDepartmentId() == 1L) ||
                    (e.getSalary() > HIGH_SALARY_THRESHOLD && e.getStatus() == EmployeeStatus.ACTIVE));
        }

        /**
         * Tests complex business rule pattern.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should handle complex business rule pattern")
        void shouldHandleComplexBusinessRulePattern(DbConfig config) {
            // Given: Business rule - Find employees who are:
            // (active with high salary) OR (in department 1 or 2)
            List<Employee> employees = config.queryEmployees()
                    .where(or(
                            and(
                                    get(Employee::getActive).eq(true),
                                    get(Employee::getSalary).gt(HIGH_SALARY_THRESHOLD)
                            ),
                            or(
                                    get(Employee::getDepartmentId).eq(1L),
                                    get(Employee::getDepartmentId).eq(2L)
                            )
                    ))
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e ->
                    (e.getActive() && e.getSalary() > HIGH_SALARY_THRESHOLD) ||
                    e.getDepartmentId() == 1L ||
                    e.getDepartmentId() == 2L);
        }

        /**
         * Tests combining where clause with AND/OR predicates.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should combine where clause with AND/OR predicates")
        void shouldCombineWhereWithAndOrPredicates(DbConfig config) {
            // Given
            List<Employee> employees = config.queryEmployees()
                    .where(or(
                            get(Employee::getDepartmentId).eq(1L),
                            get(Employee::getDepartmentId).eq(2L)
                    ))
                    .where(Employee::getActive).eq(true)
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e ->
                    (e.getDepartmentId() == 1L || e.getDepartmentId() == 2L) && e.getActive());
        }

        /**
         * Tests full complex pattern matching testAndOr2 structure.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should handle full complex pattern like testAndOr2")
        void shouldHandleFullComplexPatternLikeTestAndOr2(DbConfig config) {
            // Given: Similar structure to testAndOr2
            // active AND status != null AND salary > 50000 AND
            // (departmentId = 1 OR departmentId = 2 OR name = 'Alice') AND
            // salary < 90000
            List<Employee> employees = config.queryEmployees()
                    .where(and(
                            get(Employee::getActive).eq(true),
                            get(Employee::getStatus).isNotNull(),
                            get(Employee::getSalary).gt(50000.0),
                            or(
                                    get(Employee::getDepartmentId).eq(1L),
                                    get(Employee::getDepartmentId).eq(2L),
                                    get(Employee::getName).eq(ALICE_NAME)
                            ),
                            get(Employee::getSalary).lt(90000.0)
                    ))
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e ->
                    e.getActive() &&
                    e.getStatus() != null &&
                    e.getSalary() > 50000.0 &&
                    (e.getDepartmentId() == 1L || e.getDepartmentId() == 2L || e.getName().equals(ALICE_NAME)) &&
                    e.getSalary() < 90000.0);
        }
    }

    @Nested
    @DisplayName("NOT with AND/OR Tests")
    class NotWithAndOrTests {

        /**
         * Tests NOT combined with AND.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should combine NOT with AND")
        void shouldCombineNotWithAnd(DbConfig config) {
            // Given: active AND NOT(departmentId = 1)
            List<Employee> employees = config.queryEmployees()
                    .where(and(
                            get(Employee::getActive).eq(true),
                            not(get(Employee::getDepartmentId).eq(1L))
                    ))
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> e.getActive() && e.getDepartmentId() != 1L);
        }

        /**
         * Tests NOT combined with OR.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should combine NOT with OR")
        void shouldCombineNotWithOr(DbConfig config) {
            // Given: active OR NOT(departmentId = 1)
            List<Employee> employees = config.queryEmployees()
                    .where(or(
                            get(Employee::getActive).eq(true),
                            not(get(Employee::getDepartmentId).eq(1L))
                    ))
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> e.getActive() || e.getDepartmentId() != 1L);
        }

        /**
         * Tests NOT of AND predicate.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should handle NOT of AND predicate")
        void shouldHandleNotOfAndPredicate(DbConfig config) {
            // Given: NOT(active AND departmentId = 1)
            List<Employee> employees = config.queryEmployees()
                    .where(not(and(
                            get(Employee::getActive).eq(true),
                            get(Employee::getDepartmentId).eq(1L)
                    )))
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> !e.getActive() || e.getDepartmentId() != 1L);
        }

        /**
         * Tests NOT of OR predicate.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should handle NOT of OR predicate")
        void shouldHandleNotOfOrPredicate(DbConfig config) {
            // Given: NOT(departmentId = 1 OR departmentId = 2)
            List<Employee> employees = config.queryEmployees()
                    .where(not(or(
                            get(Employee::getDepartmentId).eq(1L),
                            get(Employee::getDepartmentId).eq(2L)
                    )))
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> e.getDepartmentId() != 1L && e.getDepartmentId() != 2L);
        }

        /**
         * Tests chained NOT operations.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should handle chained NOT operations")
        void shouldHandleChainedNotOperations(DbConfig config) {
            // Given: NOT(NOT(active))
            List<Employee> employees = config.queryEmployees()
                    .where(not(not(get(Employee::getActive).eq(true))))
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(Employee::getActive);
        }

        /**
         * Tests complex NOT with AND/OR combination.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should handle complex NOT with AND/OR combination")
        void shouldHandleComplexNotWithAndOr(DbConfig config) {
            // Given: NOT(active AND (departmentId = 1 OR departmentId = 2))
            List<Employee> employees = config.queryEmployees()
                    .where(not(and(
                            get(Employee::getActive).eq(true),
                            or(
                                    get(Employee::getDepartmentId).eq(1L),
                                    get(Employee::getDepartmentId).eq(2L)
                            )
                    )))
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e ->
                    !e.getActive() || (e.getDepartmentId() != 1L && e.getDepartmentId() != 2L));
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        /**
         * Tests AND with NULL check.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should handle AND with NULL check")
        void shouldHandleAndWithNullCheck(DbConfig config) {
            // When: email IS NOT NULL AND active = true
            List<Employee> employees = config.queryEmployees()
                    .where(and(
                            get(Employee::getEmail).isNotNull(),
                            get(Employee::getActive).eq(true)
                    ))
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> e.getEmail() != null && e.getActive());
        }

        /**
         * Tests OR with NULL check.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should handle OR with NULL check")
        void shouldHandleOrWithNullCheck(DbConfig config) {
            // When: email IS NULL OR active = false
            List<Employee> employees = config.queryEmployees()
                    .where(or(
                            get(Employee::getEmail).isNull(),
                            get(Employee::getActive).eq(false)
                    ))
                    .getList();

            // Then
            assertThat(employees).allMatch(e -> e.getEmail() == null || !e.getActive());
        }

        /**
         * Tests AND with BETWEEN.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should handle AND with BETWEEN")
        void shouldHandleAndWithBetween(DbConfig config) {
            // When: salary BETWEEN 60000 AND 75000 AND active = true
            List<Employee> employees = config.queryEmployees()
                    .where(and(
                            get(Employee::getSalary).between(60000.0, 75000.0),
                            get(Employee::getActive).eq(true)
                    ))
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e ->
                    e.getSalary() >= 60000.0 && e.getSalary() <= 75000.0 && e.getActive());
        }

        /**
         * Tests OR with IN clause.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should handle OR with IN clause")
        void shouldHandleOrWithInClause(DbConfig config) {
            // When: departmentId IN (1, 2) OR salary > 80000
            List<Employee> employees = config.queryEmployees()
                    .where(or(
                            get(Employee::getDepartmentId).in(1L, 2L),
                            get(Employee::getSalary).gt(HIGH_SALARY_THRESHOLD)
                    ))
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e ->
                    (e.getDepartmentId() == 1L || e.getDepartmentId() == 2L) ||
                    e.getSalary() > HIGH_SALARY_THRESHOLD);
        }

        /**
         * Tests AND with LIKE.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should handle AND with LIKE")
        void shouldHandleAndWithLike(DbConfig config) {
            // When: email LIKE '%example.com' AND active = true
            List<Employee> employees = config.queryEmployees()
                    .where(and(
                            get(Employee::getEmail).like("%@example.com"),
                            get(Employee::getActive).eq(true)
                    ))
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e ->
                    e.getEmail().endsWith("@example.com") && e.getActive());
        }

        /**
         * Tests complex predicate with all operators.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should handle complex predicate with all operators")
        void shouldHandleComplexPredicateWithAllOperators(DbConfig config) {
            // Given: (active AND status = ACTIVE) OR
            //        (NOT(departmentId IN (1,2)) AND salary > 70000)
            List<Employee> employees = config.queryEmployees()
                    .where(or(
                            and(
                                    get(Employee::getActive).eq(true),
                                    get(Employee::getStatus).eq(EmployeeStatus.ACTIVE)
                            ),
                            and(
                                    not(get(Employee::getDepartmentId).in(1L, 2L)),
                                    get(Employee::getSalary).gt(70000.0)
                            )
                    ))
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e ->
                    (e.getActive() && e.getStatus() == EmployeeStatus.ACTIVE) ||
                    ((e.getDepartmentId() != 1L && e.getDepartmentId() != 2L) && e.getSalary() > 70000.0));
        }

        /**
         * Tests combining Predicate.and() method.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should combine using Predicate.and() method")
        void shouldCombineUsingPredicateAndMethod(DbConfig config) {
            // Given: using Predicate.and() to chain
            Predicate<Employee> p1 = get(Employee::getActive).eq(true);
            Predicate<Employee> p2 = get(Employee::getStatus).eq(EmployeeStatus.ACTIVE);
            Predicate<Employee> p3 = get(Employee::getDepartmentId).eq(1L);

            // When
            List<Employee> employees = config.queryEmployees()
                    .where(p1.and(p2).and(p3))
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e ->
                    e.getActive() &&
                    e.getStatus() == EmployeeStatus.ACTIVE &&
                    e.getDepartmentId() == 1L);
        }

        /**
         * Tests combining Predicate.or() method.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should combine using Predicate.or() method")
        void shouldCombineUsingPredicateOrMethod(DbConfig config) {
            // Given: using Predicate.or() to chain
            Predicate<Employee> p1 = get(Employee::getName).eq(ALICE_NAME);
            Predicate<Employee> p2 = get(Employee::getName).eq(BOB_NAME);
            Predicate<Employee> p3 = get(Employee::getName).eq(CHARLIE_NAME);

            // When
            List<Employee> employees = config.queryEmployees()
                    .where(p1.or(p2).or(p3))
                    .orderBy(Employee::getId).asc()
                    .getList();

            // Then
            assertThat(employees).hasSize(3);
            assertThat(employees).extracting(Employee::getName)
                    .containsExactlyInAnyOrder(ALICE_NAME, BOB_NAME, CHARLIE_NAME);
        }

        /**
         * Tests Predicate with Iterable.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should combine predicates with Iterable")
        void shouldCombinePredicatesWithIterable(DbConfig config) {
            // Given
            Predicate<Employee> isActive = get(Employee::getActive).eq(true);
            Predicate<Employee> isStatusActive = get(Employee::getStatus).eq(EmployeeStatus.ACTIVE);
            Predicate<Employee> isDept1 = get(Employee::getDepartmentId).eq(1L);

            // When
            List<Employee> employees = config.queryEmployees()
                    .where(isActive.and(Arrays.asList(isStatusActive, isDept1)))
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e ->
                    e.getActive() &&
                    e.getStatus() == EmployeeStatus.ACTIVE &&
                    e.getDepartmentId() == 1L);
        }

        /**
         * Tests Conjunction toPredicate.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should convert Conjunction to Predicate")
        void shouldConvertConjunctionToPredicate(DbConfig config) {

            // Given
            Predicate<Employee> predicate = get(Employee::getActive).eq(true)
                    .and(get(Employee::getDepartmentId).eq(1L))
                    .toPredicate();

            // When
            List<Employee> employees = config.queryEmployees()
                    .where(predicate)
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> e.getActive() && e.getDepartmentId() == 1L);
        }

        /**
         * Tests Disjunction toPredicate.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should convert Disjunction to Predicate")
        void shouldConvertDisjunctionToPredicate(DbConfig config) {
            // Given
            Predicate<Employee> predicate = get(Employee::getName).eq(ALICE_NAME)
                    .or(get(Employee::getName).eq(BOB_NAME))
                    .toPredicate();

            // When
            List<Employee> employees = config.queryEmployees()
                    .where(predicate)
                    .orderBy(Employee::getId).asc()
                    .getList();

            // Then
            assertThat(employees).hasSize(2);
        }

        /**
         * Tests Predicate.and() with Iterable for OR.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should combine predicates with Iterable for OR")
        void shouldCombinePredicatesWithIterableForOr(DbConfig config) {
            // Given
            Predicate<Employee> isAlice = get(Employee::getName).eq(ALICE_NAME);
            Predicate<Employee> isBob = get(Employee::getName).eq(BOB_NAME);
            Predicate<Employee> isCharlie = get(Employee::getName).eq(CHARLIE_NAME);

            // When
            List<Employee> employees = config.queryEmployees()
                    .where(isAlice.or(Arrays.asList(isBob, isCharlie)))
                    .orderBy(Employee::getId).asc()
                    .getList();

            // Then
            assertThat(employees).hasSize(3);
            assertThat(employees).extracting(Employee::getName)
                    .containsExactlyInAnyOrder(ALICE_NAME, BOB_NAME, CHARLIE_NAME);
        }
    }

    @Nested
    @DisplayName("Predicate Instance Methods Tests")
    class PredicateInstanceMethodsTests {

        /**
         * Tests Predicate.and() with varargs array.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should combine with varargs array using Predicate.and()")
        void shouldCombineWithVarargsArrayUsingPredicateAnd(DbConfig config) {
            // Given
            @SuppressWarnings("unchecked")
            TypedExpression<Employee, Boolean>[] predicates = new TypedExpression[]{
                    get(Employee::getStatus).eq(EmployeeStatus.ACTIVE),
                    get(Employee::getDepartmentId).eq(1L),
                    get(Employee::getSalary).gt(LOW_SALARY_THRESHOLD)
            };

            // When
            List<Employee> employees = config.queryEmployees()
                    .where(get(Employee::getActive).eq(true).and(predicates))
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e ->
                    e.getActive() &&
                    e.getStatus() == EmployeeStatus.ACTIVE &&
                    e.getDepartmentId() == 1L &&
                    e.getSalary() > LOW_SALARY_THRESHOLD);
        }

        /**
         * Tests Predicate.or() with varargs array.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should combine with varargs array using Predicate.or()")
        void shouldCombineWithVarargsArrayUsingPredicateOr(DbConfig config) {
            // Given
            @SuppressWarnings("unchecked")
            TypedExpression<Employee, Boolean>[] predicates = new TypedExpression[]{
                    get(Employee::getName).eq(BOB_NAME),
                    get(Employee::getName).eq(CHARLIE_NAME),
                    get(Employee::getName).eq(DIANA_NAME)
            };

            // When
            List<Employee> employees = config.queryEmployees()
                    .where(get(Employee::getName).eq(ALICE_NAME).or(predicates))
                    .orderBy(Employee::getId).asc()
                    .getList();

            // Then
            assertThat(employees).hasSize(4);
            assertThat(employees).extracting(Employee::getName)
                    .containsExactlyInAnyOrder(ALICE_NAME, BOB_NAME, CHARLIE_NAME, DIANA_NAME);
        }

        /**
         * Tests Conjunction.and(Path) returning PathOperator.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should chain and(Path) returning PathOperator")
        void shouldChainAndPathReturningPathOperator(DbConfig config) {
            // Given: active AND departmentId = 1 using fluent chaining
            List<Employee> employees = config.queryEmployees()
                    .where(get(Employee::getActive).eq(true)
                            .and(Employee::getDepartmentId).eq(1L))
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> e.getActive() && e.getDepartmentId() == 1L);
        }

        /**
         * Tests Conjunction.and(Path) with multiple chains.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should chain multiple and(Path) calls")
        void shouldChainMultipleAndPathCalls(DbConfig config) {
            // Given: active AND departmentId = 1 AND status = ACTIVE
            List<Employee> employees = config.queryEmployees()
                    .where(get(Employee::getActive).eq(true)
                            .and(Employee::getDepartmentId).eq(1L)
                            .and(Employee::getStatus).eq(EmployeeStatus.ACTIVE))
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e ->
                    e.getActive() &&
                    e.getDepartmentId() == 1L &&
                    e.getStatus() == EmployeeStatus.ACTIVE);
        }

        /**
         * Tests Disjunction.or(Path) returning PathOperator.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should chain or(Path) returning PathOperator")
        void shouldChainOrPathReturningPathOperator(DbConfig config) {
            // Given: name = 'Alice' OR departmentId = 1
            List<Employee> employees = config.queryEmployees()
                    .where(get(Employee::getName).eq(ALICE_NAME)
                            .or(Employee::getDepartmentId).eq(1L))
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e ->
                    e.getName().equals(ALICE_NAME) || e.getDepartmentId() == 1L);
        }

        /**
         * Tests Disjunction.or(Path) with multiple chains.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should chain multiple or(Path) calls")
        void shouldChainMultipleOrPathCalls(DbConfig config) {
            // Given: name = 'Alice' OR name = 'Bob' OR name = 'Charlie'
            List<Employee> employees = config.queryEmployees()
                    .where(get(Employee::getName).eq(ALICE_NAME)
                            .or(Employee::getName).eq(BOB_NAME)
                            .or(Employee::getName).eq(CHARLIE_NAME))
                    .orderBy(Employee::getId).asc()
                    .getList();

            // Then
            assertThat(employees).hasSize(3);
            assertThat(employees).extracting(Employee::getName)
                    .containsExactlyInAnyOrder(ALICE_NAME, BOB_NAME, CHARLIE_NAME);
        }

        /**
         * Tests Conjunction.and(NumberRef) returning NumberOperator.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should chain and(NumberRef) returning NumberOperator")
        void shouldChainAndNumberRefReturningNumberOperator(DbConfig config) {
            // Given: active AND salary > 60000
            List<Employee> employees = config.queryEmployees()
                    .where(get(Employee::getActive).eq(true)
                            .and(Employee::getSalary).gt(60000.0))
                    .orderBy(Employee::getSalary).asc()
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> e.getActive() && e.getSalary() > 60000.0);
        }

        /**
         * Tests Conjunction.and(NumberRef) with arithmetic operations.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should chain and(NumberRef) with arithmetic operations")
        void shouldChainAndNumberRefWithArithmeticOperations(DbConfig config) {
            // Given: active AND salary + 10000 > 80000
            List<Employee> employees = config.queryEmployees()
                    .where(get(Employee::getActive).eq(true)
                            .and(Employee::getSalary).add(10000.0).gt(80000.0))
                    .orderBy(Employee::getSalary).asc()
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> e.getActive() && e.getSalary() + 10000.0 > 80000.0);
        }

        /**
         * Tests Disjunction.or(NumberRef) returning NumberOperator.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should chain or(NumberRef) returning NumberOperator")
        void shouldChainOrNumberRefReturningNumberOperator(DbConfig config) {
            // Given: name = 'Alice' OR salary > 80000
            List<Employee> employees = config.queryEmployees()
                    .where(get(Employee::getName).eq(ALICE_NAME)
                            .or(Employee::getSalary).gt(HIGH_SALARY_THRESHOLD))
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e ->
                    e.getName().equals(ALICE_NAME) || e.getSalary() > HIGH_SALARY_THRESHOLD);
        }

        /**
         * Tests Conjunction.and(StringRef) returning StringOperator.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should chain and(StringRef) returning StringOperator")
        void shouldChainAndStringRefReturningStringOperator(DbConfig config) {
            // Given: active AND name starts with 'A'
            List<Employee> employees = config.queryEmployees()
                    .where(get(Employee::getActive).eq(true)
                            .and(Employee::getName).startsWith("A"))
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> e.getActive() && e.getName().startsWith("A"));
        }

        /**
         * Tests Conjunction.and(StringRef) with string functions.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should chain and(StringRef) with string functions")
        void shouldChainAndStringRefWithStringFunctions(DbConfig config) {
            // Given: active AND lower(name) starts with 'a'
            List<Employee> employees = config.queryEmployees()
                    .where(get(Employee::getActive).eq(true)
                            .and(Employee::getName).lower().startsWith("a"))
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e ->
                    e.getActive() && e.getName().toLowerCase().startsWith("a"));
        }

        /**
         * Tests Disjunction.or(StringRef) returning StringOperator.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should chain or(StringRef) returning StringOperator")
        void shouldChainOrStringRefReturningStringOperator(DbConfig config) {
            // Given: departmentId = 1 OR name contains 'Brown'
            List<Employee> employees = config.queryEmployees()
                    .where(get(Employee::getDepartmentId).eq(1L)
                            .or(Employee::getName).contains("Brown"))
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e ->
                    e.getDepartmentId() == 1L || e.getName().contains("Brown"));
        }

        /**
         * Tests mixed Conjunction and Disjunction using Predicate.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should handle mixed Conjunction and Disjunction using Predicate")
        void shouldHandleMixedConjunctionAndDisjunctionChains(DbConfig config) {
            // Given: (name = 'Alice' OR name = 'Bob') AND active = true
            // Using toPredicate() to convert Disjunction to Predicate, then chain and()
            List<Employee> employees = config.queryEmployees()
                    .where(get(Employee::getName).eq(ALICE_NAME)
                            .or(Employee::getName).eq(BOB_NAME)
                            .toPredicate()
                            .and(Employee::getActive).eq(true))
                    .orderBy(Employee::getId).asc()
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e ->
                    (e.getName().equals(ALICE_NAME) || e.getName().equals(BOB_NAME)) &&
                    e.getActive());
        }

        /**
         * Tests complex fluent chain similar to testPredicateAssembler.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should handle complex fluent chain like testPredicateAssembler")
        void shouldHandleComplexFluentChainLikeTestPredicateAssembler(DbConfig config) {
            // Given: active AND (status = ACTIVE OR salary > 70000)
            List<Employee> employees = config.queryEmployees()
                    .where(get(Employee::getActive).eq(true)
                            .and(get(Employee::getStatus).eq(EmployeeStatus.ACTIVE)
                                    .or(Employee::getSalary).gt(70000.0)))
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e ->
                    e.getActive() &&
                    (e.getStatus() == EmployeeStatus.ACTIVE || e.getSalary() > 70000.0));
        }

        /**
         * Tests Predicate.not() returns Predicate for chaining.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should chain after Predicate.not()")
        void shouldChainAfterPredicateNot(DbConfig config) {
            // Given: NOT(active) AND departmentId = 1
            List<Employee> employees = config.queryEmployees()
                    .where(get(Employee::getActive).eq(true).not()
                            .and(Employee::getDepartmentId).eq(1L))
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> !e.getActive() && e.getDepartmentId() == 1L);
        }

        /**
         * Tests Predicate.not().or() chain.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should chain or() after Predicate.not()")
        void shouldChainOrAfterPredicateNot(DbConfig config) {
            // Given: NOT(departmentId = 1) OR active = true
            List<Employee> employees = config.queryEmployees()
                    .where(get(Employee::getDepartmentId).eq(1L).not()
                            .or(Employee::getActive).eq(true))
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> e.getDepartmentId() != 1L || e.getActive());
        }

        /**
         * Tests complex chain with NOT, AND, OR.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should handle complex chain with NOT, AND, OR")
        void shouldHandleComplexChainWithNotAndOr(DbConfig config) {
            // Given: active AND NOT(departmentId = 1) AND (status = ACTIVE OR salary > 60000)
            List<Employee> employees = config.queryEmployees()
                    .where(get(Employee::getActive).eq(true)
                            .and(get(Employee::getDepartmentId).eq(1L).not())
                            .and(get(Employee::getStatus).eq(EmployeeStatus.ACTIVE)
                                    .or(Employee::getSalary).gt(60000.0)))
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e ->
                    e.getActive() &&
                    e.getDepartmentId() != 1L &&
                    (e.getStatus() == EmployeeStatus.ACTIVE || e.getSalary() > 60000.0));
        }

        /**
         * Tests Conjunction.and(Iterable) returning Conjunction for chaining.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should chain and(Iterable) for further operations")
        void shouldChainAndIterableForFurtherOperations(DbConfig config) {
            // Given
            List<TypedExpression<Employee, Boolean>> predicates = Arrays.asList(
                    get(Employee::getStatus).eq(EmployeeStatus.ACTIVE),
                    get(Employee::getDepartmentId).eq(1L)
            );

            // When: active AND (status = ACTIVE AND departmentId = 1) AND salary > 50000
            List<Employee> employees = config.queryEmployees()
                    .where(get(Employee::getActive).eq(true)
                            .and(predicates)
                            .and(Employee::getSalary).gt(50000.0))
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e ->
                    e.getActive() &&
                    e.getStatus() == EmployeeStatus.ACTIVE &&
                    e.getDepartmentId() == 1L &&
                    e.getSalary() > 50000.0);
        }

        /**
         * Tests Disjunction.or(Iterable) returning Disjunction for chaining.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should chain or(Iterable) for further operations")
        void shouldChainOrIterableForFurtherOperations(DbConfig config) {
            // Given
            List<TypedExpression<Employee, Boolean>> predicates = Arrays.asList(
                    get(Employee::getName).eq(BOB_NAME),
                    get(Employee::getName).eq(CHARLIE_NAME)
            );

            // When: name = 'Alice' OR (name = 'Bob' OR name = 'Charlie') OR name = 'Diana'
            List<Employee> employees = config.queryEmployees()
                    .where(get(Employee::getName).eq(ALICE_NAME)
                            .or(predicates)
                            .or(Employee::getName).eq(DIANA_NAME))
                    .orderBy(Employee::getId).asc()
                    .getList();

            // Then
            assertThat(employees).hasSize(4);
            assertThat(employees).extracting(Employee::getName)
                    .containsExactlyInAnyOrder(ALICE_NAME, BOB_NAME, CHARLIE_NAME, DIANA_NAME);
        }

        /**
         * Tests chained and().or() pattern using toPredicate().
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should handle chained and().or() pattern using toPredicate()")
        void shouldHandleChainedAndOrPattern(DbConfig config) {
            // Given: active AND departmentId = 1 OR salary > 80000
            // Note: This is (active AND departmentId = 1) OR salary > 80000
            // Using toPredicate() to convert Conjunction to Predicate, then chain or()
            List<Employee> employees = config.queryEmployees()
                    .where(get(Employee::getActive).eq(true)
                            .and(Employee::getDepartmentId).eq(1L)
                            .toPredicate()
                            .or(Employee::getSalary).gt(HIGH_SALARY_THRESHOLD))
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e ->
                    (e.getActive() && e.getDepartmentId() == 1L) ||
                    e.getSalary() > HIGH_SALARY_THRESHOLD);
        }

        /**
         * Tests chained or().and() pattern using toPredicate().
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should handle chained or().and() pattern using toPredicate()")
        void shouldHandleChainedOrAndPattern(DbConfig config) {
            // Given: name = 'Alice' OR departmentId = 1 AND active = true
            // Note: This is (name = 'Alice' OR departmentId = 1) AND active = true
            // Using toPredicate() to convert Disjunction to Predicate, then chain and()
            List<Employee> employees = config.queryEmployees()
                    .where(get(Employee::getName).eq(ALICE_NAME)
                            .or(Employee::getDepartmentId).eq(1L)
                            .toPredicate()
                            .and(Employee::getActive).eq(true))
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e ->
                    (e.getName().equals(ALICE_NAME) || e.getDepartmentId() == 1L) &&
                    e.getActive());
        }
    }
}