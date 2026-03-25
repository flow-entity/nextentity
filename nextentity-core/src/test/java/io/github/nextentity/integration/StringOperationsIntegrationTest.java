package io.github.nextentity.integration;

import io.github.nextentity.integration.config.IntegrationTestContext;
import io.github.nextentity.integration.config.IntegrationTestProvider;
import io.github.nextentity.integration.entity.Employee;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * String operations integration tests.
 * <p>
 * Tests string operations including:
 * - LIKE pattern matching
 * - String functions (lower, upper, trim, substring, length)
 * - String sorting
 * <p>
 * These tests run against MySQL and PostgreSQL using Testcontainers.
 *
 * @author HuangChengwei
 */
@DisplayName("String Operations Integration Tests")
public class StringOperationsIntegrationTest {

    /**
     * Tests LIKE with prefix pattern.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with LIKE prefix pattern")
    void shouldFilterWithLikePrefix(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).like("A%")
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getName().startsWith("A"));
    }

    /**
     * Tests LIKE with suffix pattern.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with LIKE suffix pattern")
    void shouldFilterWithLikeSuffix(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getEmail).like("%@example.com")
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getEmail().endsWith("@example.com"));
    }

    /**
     * Tests LIKE with contains pattern.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with LIKE contains pattern")
    void shouldFilterWithLikeContains(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).like("%son%")
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getName().toLowerCase().contains("son"));
    }

    /**
     * Tests startsWith method.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with startsWith")
    void shouldFilterWithStartsWith(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).startsWith("Alice")
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getName().startsWith("Alice"));
    }

    /**
     * Tests endsWith method.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with endsWith")
    void shouldFilterWithEndsWith(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getEmail).endsWith("@example.com")
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getEmail().endsWith("@example.com"));
    }

    /**
     * Tests contains method.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with contains")
    void shouldFilterWithContains(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).contains("John")
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getName().contains("John"));
    }

    /**
     * Tests NOT LIKE.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with NOT LIKE")
    void shouldFilterWithNotLike(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).notLike("A%")
                .orderBy(Employee::getId).asc()
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> !e.getName().startsWith("A"));
    }

    /**
     * Tests notStartsWith method.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with notStartsWith")
    void shouldFilterWithNotStartsWith(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).notStartsWith("A")
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> !e.getName().startsWith("A"));
    }

    /**
     * Tests notContains method.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with notContains")
    void shouldFilterWithNotContains(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).notContains("Alice")
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> !e.getName().contains("Alice"));
    }

    /**
     * Tests likeIfNotNull with non-null value.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with likeIfNotNull when value is not null")
    void shouldFilterWithLikeIfNotNull(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).likeIfNotNull("A%")
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getName().startsWith("A"));
    }

    /**
     * Tests likeIfNotNull with null value.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should return all when likeIfNotNull is null")
    void shouldReturnAllWhenLikeIfNotNullIsNull(IntegrationTestContext context) {
        // Given
        long totalCount = context.queryEmployees().count();

        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).likeIfNotNull(null)
                .getList();

        // Then
        assertThat(employees).hasSize((int) totalCount);
    }

    /**
     * Tests likeIfNotEmpty with non-empty value.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with likeIfNotEmpty when value is not empty")
    void shouldFilterWithLikeIfNotEmpty(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).likeIfNotEmpty("A%")
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getName().startsWith("A"));
    }

    /**
     * Tests likeIfNotEmpty with empty value.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should return all when likeIfNotEmpty is empty")
    void shouldReturnAllWhenLikeIfNotEmptyIsEmpty(IntegrationTestContext context) {
        // Given
        long totalCount = context.queryEmployees().count();

        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).likeIfNotEmpty("")
                .getList();

        // Then
        assertThat(employees).hasSize((int) totalCount);
    }

    /**
     * Tests multiple LIKE conditions.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with multiple LIKE conditions")
    void shouldFilterWithMultipleLikeConditions(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).like("A%")
                .where(Employee::getEmail).like("%@example.com")
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e ->
                e.getName().startsWith("A") && e.getEmail().endsWith("@example.com"));
    }

    /**
     * Tests LIKE with special characters.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with LIKE containing special characters")
    void shouldFilterWithLikeSpecialChars(IntegrationTestContext context) {
        // When - email contains @
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getEmail).like("%@%")
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getEmail().contains("@"));
    }

    /**
     * Tests case-insensitive LIKE pattern.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with case-insensitive pattern using lower")
    void shouldFilterWithCaseInsensitiveUsingLower(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).lower().like("alice%")
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getName().toLowerCase().startsWith("alice"));
    }

    /**
     * Tests string length function.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter by string length")
    void shouldFilterByStringLength(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).length().ge(10)
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getName().length() >= 10);
    }

    /**
     * Tests ordering by string field ascending.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should order by string field ascending")
    void shouldOrderByStringAsc(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .orderBy(Employee::getName).asc()
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        for (int i = 1; i < employees.size(); i++) {
            assertThat(employees.get(i).getName().compareTo(employees.get(i - 1).getName()))
                    .isGreaterThanOrEqualTo(0);
        }
    }

    /**
     * Tests ordering by string field descending.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should order by string field descending")
    void shouldOrderByStringDesc(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .orderBy(Employee::getName).desc()
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        for (int i = 1; i < employees.size(); i++) {
            assertThat(employees.get(i).getName().compareTo(employees.get(i - 1).getName()))
                    .isLessThanOrEqualTo(0);
        }
    }

    /**
     * Tests LIKE with underscore wildcard.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle LIKE with underscore wildcard")
    void shouldHandleLikeWithUnderscore(IntegrationTestContext context) {
        // When - any single character followed by 'lice'
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).like("_lice%")
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        // Alice should match
        assertThat(employees).anyMatch(e -> e.getName().contains("lice"));
    }

    /**
     * Tests combined string operations.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should combine multiple string operations")
    void shouldCombineStringOperations(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).like("A%")
                .where(Employee::getEmail).contains("@")
                .orderBy(Employee::getName).asc()
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e ->
                e.getName().startsWith("A") && e.getEmail().contains("@"));
    }

    /**
     * Tests LIKE with no matches.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should return empty when LIKE has no matches")
    void shouldReturnEmptyWhenNoMatches(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).like("ZZZZZZ%")
                .getList();

        // Then
        assertThat(employees).isEmpty();
    }

    /**
     * Tests selecting string field.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select string field")
    void shouldSelectStringField(IntegrationTestContext context) {
        // When
        List<String> names = context.queryEmployees()
                .select(Employee::getName)
                .orderBy(Employee::getName).asc()
                .getList();

        // Then
        assertThat(names).isNotEmpty();
        assertThat(names).doesNotContainNull();
    }

    /**
     * Tests selecting distinct string values.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select distinct string values")
    void shouldSelectDistinctString(IntegrationTestContext context) {
        // When
        List<String> emails = context.queryEmployees()
                .selectDistinct(Employee::getEmail)
                .getList();

        // Then
        assertThat(emails).isNotEmpty();
        assertThat(emails).hasSize((int) context.queryEmployees().count());
    }
}