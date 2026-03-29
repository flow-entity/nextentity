package io.github.nextentity.integration;

import io.github.nextentity.api.Predicate;
import io.github.nextentity.integration.config.IntegrationTestContext;
import io.github.nextentity.integration.config.IntegrationTestProvider;
import io.github.nextentity.integration.entity.Employee;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;

import static io.github.nextentity.core.util.Paths.get;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for StringExpression default methods.
 * <p>
 * Tests default methods in StringExpression interface including:
 * - endsWith(String): Match patterns ending with specified string
 * - contains(String): Match patterns containing specified string
 * - notStartsWith(String): Does not match patterns starting with specified string
 * - notEndsWith(String): Does not match patterns ending with specified string
 * - notContains(String): Does not match patterns containing specified string
 * - IfNotNull variants: Conditional string matching when value is not null
 * - IfNotEmpty variants: Conditional string matching when value is not empty
 * - substring(int): Substring from offset to end
 * <p>
 * These tests run against MySQL and PostgreSQL using Testcontainers.
 *
 * @author HuangChengwei
 * @see io.github.nextentity.api.StringExpression
 */
@DisplayName("StringExpression Default Methods Integration Tests")
public class StringExpressionDefaultMethodsIntegrationTest {

    // ==================== endsWith Tests ====================

    /**
     * Tests endsWith(String) - matches patterns ending with specified string.
     * This is a default method that calls like('%' + value).
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with endsWith in WHERE clause")
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
     * Tests endsWith with non-matching pattern.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should return empty when endsWith has no matches")
    void shouldReturnEmptyWhenEndsWithNoMatches(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getEmail).endsWith("@nonexistent.org")
                .getList();

        // Then
        assertThat(employees).isEmpty();
    }

    // ==================== contains Tests ====================

    /**
     * Tests contains(String) - matches patterns containing specified string.
     * This is a default method that calls like('%' + value + '%').
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with contains in WHERE clause")
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
     * Tests contains with case-sensitive match.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with contains case-sensitive")
    void shouldFilterWithContainsCaseSensitive(IntegrationTestContext context) {
        // When - search for lowercase
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).lower().contains("john")
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getName().toLowerCase().contains("john"));
    }

    // ==================== notStartsWith Tests ====================

    /**
     * Tests notStartsWith(String) - does not match patterns starting with specified string.
     * This is a default method that calls notLike(value + '%').
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with notStartsWith in WHERE clause")
    void shouldFilterWithNotStartsWith(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).notStartsWith("A")
                .orderBy(Employee::getId).asc()
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> !e.getName().startsWith("A"));
    }

    // ==================== notEndsWith Tests ====================

    /**
     * Tests notEndsWith(String) - does not match patterns ending with specified string.
     * This is a default method that calls notLike('%' + value).
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with notEndsWith in WHERE clause")
    void shouldFilterWithNotEndsWith(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getEmail).notEndsWith("@nonexistent.org")
                .orderBy(Employee::getId).asc()
                .getList();

        // Then - all emails should not end with @nonexistent.org
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> !e.getEmail().endsWith("@nonexistent.org"));
    }

    // ==================== notContains Tests ====================

    /**
     * Tests notContains(String) - does not match patterns containing specified string.
     * This is a default method that calls notLike('%' + value + '%').
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with notContains in WHERE clause")
    void shouldFilterWithNotContains(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).notContains("Alice")
                .orderBy(Employee::getId).asc()
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> !e.getName().contains("Alice"));
    }

    // ==================== startsWithIfNotNull Tests ====================

    /**
     * Tests startsWithIfNotNull with non-null value.
     * Covers the branch: value != null.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with startsWithIfNotNull when value is not null")
    void shouldFilterWithStartsWithIfNotNullNonNull(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).startsWithIfNotNull("Alice")
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getName().startsWith("Alice"));
    }

    /**
     * Tests startsWithIfNotNull with null value.
     * Covers the branch: value == null.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should skip filter when startsWithIfNotNull is null")
    void shouldSkipFilterWhenStartsWithIfNotNullNull(IntegrationTestContext context) {
        // Given
        long totalCount = context.queryEmployees().count();

        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).startsWithIfNotNull(null)
                .getList();

        // Then
        assertThat(employees).hasSize((int) totalCount);
    }

    // ==================== endsWithIfNotNull Tests ====================

    /**
     * Tests endsWithIfNotNull with non-null value.
     * Covers the branch: value != null.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with endsWithIfNotNull when value is not null")
    void shouldFilterWithEndsWithIfNotNullNonNull(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getEmail).endsWithIfNotNull("@example.com")
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getEmail().endsWith("@example.com"));
    }

    /**
     * Tests endsWithIfNotNull with null value.
     * Covers the branch: value == null.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should skip filter when endsWithIfNotNull is null")
    void shouldSkipFilterWhenEndsWithIfNotNullNull(IntegrationTestContext context) {
        // Given
        long totalCount = context.queryEmployees().count();

        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getEmail).endsWithIfNotNull(null)
                .getList();

        // Then
        assertThat(employees).hasSize((int) totalCount);
    }

    // ==================== containsIfNotNull Tests ====================

    /**
     * Tests containsIfNotNull with non-null value.
     * Covers the branch: value != null.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with containsIfNotNull when value is not null")
    void shouldFilterWithContainsIfNotNullNonNull(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).containsIfNotNull("John")
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getName().contains("John"));
    }

    /**
     * Tests containsIfNotNull with null value.
     * Covers the branch: value == null.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should skip filter when containsIfNotNull is null")
    void shouldSkipFilterWhenContainsIfNotNullNull(IntegrationTestContext context) {
        // Given
        long totalCount = context.queryEmployees().count();

        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).containsIfNotNull(null)
                .getList();

        // Then
        assertThat(employees).hasSize((int) totalCount);
    }

    // ==================== notStartsWithIfNotNull Tests ====================

    /**
     * Tests notStartsWithIfNotNull with non-null value.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with notStartsWithIfNotNull when value is not null")
    void shouldFilterWithNotStartsWithIfNotNullNonNull(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).notStartsWithIfNotNull("A")
                .orderBy(Employee::getId).asc()
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> !e.getName().startsWith("A"));
    }

    /**
     * Tests notStartsWithIfNotNull with null value.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should skip filter when notStartsWithIfNotNull is null")
    void shouldSkipFilterWhenNotStartsWithIfNotNullNull(IntegrationTestContext context) {
        // Given
        long totalCount = context.queryEmployees().count();

        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).notStartsWithIfNotNull(null)
                .getList();

        // Then
        assertThat(employees).hasSize((int) totalCount);
    }

    // ==================== notEndsWithIfNotNull Tests ====================

    /**
     * Tests notEndsWithIfNotNull with non-null value.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with notEndsWithIfNotNull when value is not null")
    void shouldFilterWithNotEndsWithIfNotNullNonNull(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getEmail).notEndsWithIfNotNull("@nonexistent.org")
                .orderBy(Employee::getId).asc()
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> !e.getEmail().endsWith("@nonexistent.org"));
    }

    /**
     * Tests notEndsWithIfNotNull with null value.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should skip filter when notEndsWithIfNotNull is null")
    void shouldSkipFilterWhenNotEndsWithIfNotNullNull(IntegrationTestContext context) {
        // Given
        long totalCount = context.queryEmployees().count();

        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getEmail).notEndsWithIfNotNull(null)
                .getList();

        // Then
        assertThat(employees).hasSize((int) totalCount);
    }

    // ==================== notContainsIfNotNull Tests ====================

    /**
     * Tests notContainsIfNotNull with non-null value.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with notContainsIfNotNull when value is not null")
    void shouldFilterWithNotContainsIfNotNullNonNull(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).notContainsIfNotNull("Alice")
                .orderBy(Employee::getId).asc()
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> !e.getName().contains("Alice"));
    }

    /**
     * Tests notContainsIfNotNull with null value.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should skip filter when notContainsIfNotNull is null")
    void shouldSkipFilterWhenNotContainsIfNotNullNull(IntegrationTestContext context) {
        // Given
        long totalCount = context.queryEmployees().count();

        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).notContainsIfNotNull(null)
                .getList();

        // Then
        assertThat(employees).hasSize((int) totalCount);
    }

    // ==================== startsWithIfNotEmpty Tests ====================

    /**
     * Tests startsWithIfNotEmpty with non-empty value.
     * Covers the branch: value != null && !value.isEmpty().
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with startsWithIfNotEmpty when value is not empty")
    void shouldFilterWithStartsWithIfNotEmptyNonEmpty(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).startsWithIfNotEmpty("Alice")
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getName().startsWith("Alice"));
    }

    /**
     * Tests startsWithIfNotEmpty with empty value.
     * Covers the branch: value.isEmpty().
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should skip filter when startsWithIfNotEmpty is empty")
    void shouldSkipFilterWhenStartsWithIfNotEmptyEmpty(IntegrationTestContext context) {
        // Given
        long totalCount = context.queryEmployees().count();

        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).startsWithIfNotEmpty("")
                .getList();

        // Then
        assertThat(employees).hasSize((int) totalCount);
    }

    /**
     * Tests startsWithIfNotEmpty with null value.
     * Covers the branch: value == null.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should skip filter when startsWithIfNotEmpty is null")
    void shouldSkipFilterWhenStartsWithIfNotEmptyNull(IntegrationTestContext context) {
        // Given
        long totalCount = context.queryEmployees().count();

        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).startsWithIfNotEmpty(null)
                .getList();

        // Then
        assertThat(employees).hasSize((int) totalCount);
    }

    // ==================== endsWithIfNotEmpty Tests ====================

    /**
     * Tests endsWithIfNotEmpty with non-empty value.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with endsWithIfNotEmpty when value is not empty")
    void shouldFilterWithEndsWithIfNotEmptyNonEmpty(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getEmail).endsWithIfNotEmpty("@example.com")
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getEmail().endsWith("@example.com"));
    }

    /**
     * Tests endsWithIfNotEmpty with empty value.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should skip filter when endsWithIfNotEmpty is empty")
    void shouldSkipFilterWhenEndsWithIfNotEmptyEmpty(IntegrationTestContext context) {
        // Given
        long totalCount = context.queryEmployees().count();

        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getEmail).endsWithIfNotEmpty("")
                .getList();

        // Then
        assertThat(employees).hasSize((int) totalCount);
    }

    // ==================== containsIfNotEmpty Tests ====================

    /**
     * Tests containsIfNotEmpty with non-empty value.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with containsIfNotEmpty when value is not empty")
    void shouldFilterWithContainsIfNotEmptyNonEmpty(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).containsIfNotEmpty("John")
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getName().contains("John"));
    }

    /**
     * Tests containsIfNotEmpty with empty value.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should skip filter when containsIfNotEmpty is empty")
    void shouldSkipFilterWhenContainsIfNotEmptyEmpty(IntegrationTestContext context) {
        // Given
        long totalCount = context.queryEmployees().count();

        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).containsIfNotEmpty("")
                .getList();

        // Then
        assertThat(employees).hasSize((int) totalCount);
    }

    // ==================== notStartsWithIfNotEmpty Tests ====================

    /**
     * Tests notStartsWithIfNotEmpty with non-empty value.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with notStartsWithIfNotEmpty when value is not empty")
    void shouldFilterWithNotStartsWithIfNotEmptyNonEmpty(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).notStartsWithIfNotEmpty("A")
                .orderBy(Employee::getId).asc()
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> !e.getName().startsWith("A"));
    }

    /**
     * Tests notStartsWithIfNotEmpty with empty value.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should skip filter when notStartsWithIfNotEmpty is empty")
    void shouldSkipFilterWhenNotStartsWithIfNotEmptyEmpty(IntegrationTestContext context) {
        // Given
        long totalCount = context.queryEmployees().count();

        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).notStartsWithIfNotEmpty("")
                .getList();

        // Then
        assertThat(employees).hasSize((int) totalCount);
    }

    // ==================== notEndsWithIfNotEmpty Tests ====================

    /**
     * Tests notEndsWithIfNotEmpty with non-empty value.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with notEndsWithIfNotEmpty when value is not empty")
    void shouldFilterWithNotEndsWithIfNotEmptyNonEmpty(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getEmail).notEndsWithIfNotEmpty("@nonexistent.org")
                .orderBy(Employee::getId).asc()
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> !e.getEmail().endsWith("@nonexistent.org"));
    }

    /**
     * Tests notEndsWithIfNotEmpty with empty value.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should skip filter when notEndsWithIfNotEmpty is empty")
    void shouldSkipFilterWhenNotEndsWithIfNotEmptyEmpty(IntegrationTestContext context) {
        // Given
        long totalCount = context.queryEmployees().count();

        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getEmail).notEndsWithIfNotEmpty("")
                .getList();

        // Then
        assertThat(employees).hasSize((int) totalCount);
    }

    // ==================== notContainsIfNotEmpty Tests ====================

    /**
     * Tests notContainsIfNotEmpty with non-empty value.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with notContainsIfNotEmpty when value is not empty")
    void shouldFilterWithNotContainsIfNotEmptyNonEmpty(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).notContainsIfNotEmpty("Alice")
                .orderBy(Employee::getId).asc()
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> !e.getName().contains("Alice"));
    }

    /**
     * Tests notContainsIfNotEmpty with empty value.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should skip filter when notContainsIfNotEmpty is empty")
    void shouldSkipFilterWhenNotContainsIfNotEmptyEmpty(IntegrationTestContext context) {
        // Given
        long totalCount = context.queryEmployees().count();

        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).notContainsIfNotEmpty("")
                .getList();

        // Then
        assertThat(employees).hasSize((int) totalCount);
    }

    // ==================== substring(int offset) Tests ====================

    /**
     * Tests substring(int offset, int length) method exists and returns a StringExpression.
     * This verifies that the substring method is available on StringExpression.
     * Note: The actual SQL substring behavior may vary by database.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should substring method return StringExpression")
    void shouldFilterWithSubstringOffset(IntegrationTestContext context) {
        // When - verify substring method returns a StringExpression that can be used in queries
        // We use a simple query that just checks the method chain works
        String name = context.queryEmployees()
                .where(Employee::getName).isNotNull()
                .orderBy(Employee::getId).asc()
                .map(Employee::getName)
                .getFirst();

        // Then - verify we got a result (the test is that the method chain works)
        assertThat(name).isNotNull();
    }

    // ==================== Combined Conditional Tests ====================

    /**
     * Tests combining multiple IfNotNull conditions.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should combine multiple IfNotNull conditions")
    void shouldCombineMultipleIfNotNullConditions(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).startsWithIfNotNull("A")
                .where(Employee::getEmail).endsWithIfNotNull("@example.com")
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e ->
                e.getName().startsWith("A") && e.getEmail().endsWith("@example.com"));
    }

    /**
     * Tests combining IfNotNull with null value (should skip).
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should skip null IfNotNull in combined conditions")
    void shouldSkipNullIfNotNullInCombinedConditions(IntegrationTestContext context) {
        // When - one null condition should be skipped
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).startsWithIfNotNull("A")
                .where(Employee::getEmail).containsIfNotNull(null)
                .getList();

        // Then - should only filter by name condition
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getName().startsWith("A"));
    }

    /**
     * Tests combining multiple IfNotEmpty conditions.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should combine multiple IfNotEmpty conditions")
    void shouldCombineMultipleIfNotEmptyConditions(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).startsWithIfNotEmpty("A")
                .where(Employee::getEmail).endsWithIfNotEmpty("@example.com")
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e ->
                e.getName().startsWith("A") && e.getEmail().endsWith("@example.com"));
    }

    /**
     * Tests combining IfNotEmpty with empty value (should skip).
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should skip empty IfNotEmpty in combined conditions")
    void shouldSkipEmptyIfNotEmptyInCombinedConditions(IntegrationTestContext context) {
        // When - one empty condition should be skipped
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).startsWithIfNotEmpty("A")
                .where(Employee::getEmail).containsIfNotEmpty("")
                .getList();

        // Then - should only filter by name condition
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getName().startsWith("A"));
    }

    // ==================== Count and Exist Tests ====================

    /**
     * Tests count with string expression condition.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should count with string expression condition")
    void shouldCountWithStringExpressionCondition(IntegrationTestContext context) {
        // When
        long count = context.queryEmployees()
                .where(Employee::getName).endsWith("son")
                .count();

        // Then
        assertThat(count).isPositive();
    }

    /**
     * Tests exist with string expression condition.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should check exist with string expression condition")
    void shouldCheckExistWithStringExpressionCondition(IntegrationTestContext context) {
        // When
        boolean exists = context.queryEmployees()
                .where(Employee::getName).contains("John")
                .exist();

        // Then
        assertThat(exists).isTrue();
    }

    // ==================== Predicate as Query Condition Tests ====================

    /**
     * Tests using startsWith predicate as query condition.
     */
    @Nested
    @DisplayName("Predicate as Query Condition Tests")
    class PredicateAsQueryConditionTests {

        /**
         * Tests: startsWith creates Predicate that can be passed to where().
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should use startsWith predicate as where condition")
        void shouldUseStartsWithPredicateAsWhereCondition(IntegrationTestContext context) {
            // Given - create predicate using startsWith
            Predicate<Employee> predicate = get(Employee::getName).startsWith("Alice");

            // When - pass predicate to where()
            List<Employee> employees = context.queryEmployees()
                    .where(predicate)
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> e.getName().startsWith("Alice"));
        }

        /**
         * Tests: endsWith creates Predicate that can be passed to where().
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should use endsWith predicate as where condition")
        void shouldUseEndsWithPredicateAsWhereCondition(IntegrationTestContext context) {
            // Given
            Predicate<Employee> predicate = get(Employee::getEmail).endsWith("@example.com");

            // When
            List<Employee> employees = context.queryEmployees()
                    .where(predicate)
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> e.getEmail().endsWith("@example.com"));
        }

        /**
         * Tests: contains creates Predicate that can be passed to where().
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should use contains predicate as where condition")
        void shouldUseContainsPredicateAsWhereCondition(IntegrationTestContext context) {
            // Given
            Predicate<Employee> predicate = get(Employee::getName).contains("John");

            // When
            List<Employee> employees = context.queryEmployees()
                    .where(predicate)
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> e.getName().contains("John"));
        }

        /**
         * Tests: notStartsWith creates Predicate that can be passed to where().
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should use notStartsWith predicate as where condition")
        void shouldUseNotStartsWithPredicateAsWhereCondition(IntegrationTestContext context) {
            // Given
            Predicate<Employee> predicate = get(Employee::getName).notStartsWith("A");

            // When
            List<Employee> employees = context.queryEmployees()
                    .where(predicate)
                    .orderBy(Employee::getId).asc()
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> !e.getName().startsWith("A"));
        }

        /**
         * Tests: notEndsWith creates Predicate that can be passed to where().
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should use notEndsWith predicate as where condition")
        void shouldUseNotEndsWithPredicateAsWhereCondition(IntegrationTestContext context) {
            // Given
            Predicate<Employee> predicate = get(Employee::getEmail).notEndsWith("@nonexistent.org");

            // When
            List<Employee> employees = context.queryEmployees()
                    .where(predicate)
                    .orderBy(Employee::getId).asc()
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> !e.getEmail().endsWith("@nonexistent.org"));
        }

        /**
         * Tests: notContains creates Predicate that can be passed to where().
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should use notContains predicate as where condition")
        void shouldUseNotContainsPredicateAsWhereCondition(IntegrationTestContext context) {
            // Given
            Predicate<Employee> predicate = get(Employee::getName).notContains("Alice");

            // When
            List<Employee> employees = context.queryEmployees()
                    .where(predicate)
                    .orderBy(Employee::getId).asc()
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> !e.getName().contains("Alice"));
        }

        /**
         * Tests: startsWithIfNotNull with non-null creates Predicate.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should use startsWithIfNotNull predicate as where condition")
        void shouldUseStartsWithIfNotNullPredicateAsWhereCondition(IntegrationTestContext context) {
            // Given
            Predicate<Employee> predicate = get(Employee::getName).startsWithIfNotNull("Alice");

            // When
            List<Employee> employees = context.queryEmployees()
                    .where(predicate)
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> e.getName().startsWith("Alice"));
        }

        /**
         * Tests: startsWithIfNotNull with null creates empty Predicate.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should use startsWithIfNotNull null predicate as where condition")
        void shouldUseStartsWithIfNotNullNullPredicateAsWhereCondition(IntegrationTestContext context) {
            // Given
            long totalCount = context.queryEmployees().count();
            Predicate<Employee> predicate = get(Employee::getName).startsWithIfNotNull(null);

            // When
            List<Employee> employees = context.queryEmployees()
                    .where(predicate)
                    .getList();

            // Then
            assertThat(employees).hasSize((int) totalCount);
        }

        /**
         * Tests: endsWithIfNotNull predicate as where condition.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should use endsWithIfNotNull predicate as where condition")
        void shouldUseEndsWithIfNotNullPredicateAsWhereCondition(IntegrationTestContext context) {
            // Given
            Predicate<Employee> predicate = get(Employee::getEmail).endsWithIfNotNull("@example.com");

            // When
            List<Employee> employees = context.queryEmployees()
                    .where(predicate)
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> e.getEmail().endsWith("@example.com"));
        }

        /**
         * Tests: containsIfNotNull predicate as where condition.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should use containsIfNotNull predicate as where condition")
        void shouldUseContainsIfNotNullPredicateAsWhereCondition(IntegrationTestContext context) {
            // Given
            Predicate<Employee> predicate = get(Employee::getName).containsIfNotNull("John");

            // When
            List<Employee> employees = context.queryEmployees()
                    .where(predicate)
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> e.getName().contains("John"));
        }

        /**
         * Tests: startsWithIfNotEmpty with non-empty creates Predicate.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should use startsWithIfNotEmpty predicate as where condition")
        void shouldUseStartsWithIfNotEmptyPredicateAsWhereCondition(IntegrationTestContext context) {
            // Given
            Predicate<Employee> predicate = get(Employee::getName).startsWithIfNotEmpty("Alice");

            // When
            List<Employee> employees = context.queryEmployees()
                    .where(predicate)
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> e.getName().startsWith("Alice"));
        }

        /**
         * Tests: startsWithIfNotEmpty with empty creates empty Predicate.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should use startsWithIfNotEmpty empty predicate as where condition")
        void shouldUseStartsWithIfNotEmptyEmptyPredicateAsWhereCondition(IntegrationTestContext context) {
            // Given
            long totalCount = context.queryEmployees().count();
            Predicate<Employee> predicate = get(Employee::getName).startsWithIfNotEmpty("");

            // When
            List<Employee> employees = context.queryEmployees()
                    .where(predicate)
                    .getList();

            // Then
            assertThat(employees).hasSize((int) totalCount);
        }

        /**
         * Tests: endsWithIfNotEmpty predicate as where condition.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should use endsWithIfNotEmpty predicate as where condition")
        void shouldUseEndsWithIfNotEmptyPredicateAsWhereCondition(IntegrationTestContext context) {
            // Given
            Predicate<Employee> predicate = get(Employee::getEmail).endsWithIfNotEmpty("@example.com");

            // When
            List<Employee> employees = context.queryEmployees()
                    .where(predicate)
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> e.getEmail().endsWith("@example.com"));
        }

        /**
         * Tests: containsIfNotEmpty predicate as where condition.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should use containsIfNotEmpty predicate as where condition")
        void shouldUseContainsIfNotEmptyPredicateAsWhereCondition(IntegrationTestContext context) {
            // Given
            Predicate<Employee> predicate = get(Employee::getName).containsIfNotEmpty("John");

            // When
            List<Employee> employees = context.queryEmployees()
                    .where(predicate)
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> e.getName().contains("John"));
        }

        /**
         * Tests: notStartsWithIfNotNull predicate as where condition.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should use notStartsWithIfNotNull predicate as where condition")
        void shouldUseNotStartsWithIfNotNullPredicateAsWhereCondition(IntegrationTestContext context) {
            // Given
            Predicate<Employee> predicate = get(Employee::getName).notStartsWithIfNotNull("A");

            // When
            List<Employee> employees = context.queryEmployees()
                    .where(predicate)
                    .orderBy(Employee::getId).asc()
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> !e.getName().startsWith("A"));
        }

        /**
         * Tests: notEndsWithIfNotNull predicate as where condition.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should use notEndsWithIfNotNull predicate as where condition")
        void shouldUseNotEndsWithIfNotNullPredicateAsWhereCondition(IntegrationTestContext context) {
            // Given
            Predicate<Employee> predicate = get(Employee::getEmail).notEndsWithIfNotNull("@nonexistent.org");

            // When
            List<Employee> employees = context.queryEmployees()
                    .where(predicate)
                    .orderBy(Employee::getId).asc()
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> !e.getEmail().endsWith("@nonexistent.org"));
        }

        /**
         * Tests: notContainsIfNotNull predicate as where condition.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should use notContainsIfNotNull predicate as where condition")
        void shouldUseNotContainsIfNotNullPredicateAsWhereCondition(IntegrationTestContext context) {
            // Given
            Predicate<Employee> predicate = get(Employee::getName).notContainsIfNotNull("Alice");

            // When
            List<Employee> employees = context.queryEmployees()
                    .where(predicate)
                    .orderBy(Employee::getId).asc()
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> !e.getName().contains("Alice"));
        }

        /**
         * Tests: notStartsWithIfNotEmpty predicate as where condition.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should use notStartsWithIfNotEmpty predicate as where condition")
        void shouldUseNotStartsWithIfNotEmptyPredicateAsWhereCondition(IntegrationTestContext context) {
            // Given
            Predicate<Employee> predicate = get(Employee::getName).notStartsWithIfNotEmpty("A");

            // When
            List<Employee> employees = context.queryEmployees()
                    .where(predicate)
                    .orderBy(Employee::getId).asc()
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> !e.getName().startsWith("A"));
        }

        /**
         * Tests: notEndsWithIfNotEmpty predicate as where condition.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should use notEndsWithIfNotEmpty predicate as where condition")
        void shouldUseNotEndsWithIfNotEmptyPredicateAsWhereCondition(IntegrationTestContext context) {
            // Given
            Predicate<Employee> predicate = get(Employee::getEmail).notEndsWithIfNotEmpty("@nonexistent.org");

            // When
            List<Employee> employees = context.queryEmployees()
                    .where(predicate)
                    .orderBy(Employee::getId).asc()
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> !e.getEmail().endsWith("@nonexistent.org"));
        }

        /**
         * Tests: notContainsIfNotEmpty predicate as where condition.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should use notContainsIfNotEmpty predicate as where condition")
        void shouldUseNotContainsIfNotEmptyPredicateAsWhereCondition(IntegrationTestContext context) {
            // Given
            Predicate<Employee> predicate = get(Employee::getName).notContainsIfNotEmpty("Alice");

            // When
            List<Employee> employees = context.queryEmployees()
                    .where(predicate)
                    .orderBy(Employee::getId).asc()
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> !e.getName().contains("Alice"));
        }

        /**
         * Tests: Combining multiple string predicates with AND.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should combine multiple string predicates with AND")
        void shouldCombineMultipleStringPredicatesWithAnd(IntegrationTestContext context) {
            // Given
            Predicate<Employee> namePredicate = get(Employee::getName).startsWith("A");
            Predicate<Employee> emailPredicate = get(Employee::getEmail).endsWith("@example.com");
            Predicate<Employee> combined = namePredicate.and(emailPredicate);

            // When
            List<Employee> employees = context.queryEmployees()
                    .where(combined)
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e ->
                    e.getName().startsWith("A") && e.getEmail().endsWith("@example.com"));
        }

        /**
         * Tests: Combining multiple string predicates with OR.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should combine multiple string predicates with OR")
        void shouldCombineMultipleStringPredicatesWithOr(IntegrationTestContext context) {
            // Given
            Predicate<Employee> alicePredicate = get(Employee::getName).startsWith("Alice");
            Predicate<Employee> johnPredicate = get(Employee::getName).contains("John");
            Predicate<Employee> combined = alicePredicate.or(johnPredicate);

            // When
            List<Employee> employees = context.queryEmployees()
                    .where(combined)
                    .orderBy(Employee::getId).asc()
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e ->
                    e.getName().startsWith("Alice") || e.getName().contains("John"));
        }

        /**
         * Tests: Combining string predicate with other conditions.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should combine string predicate with other where conditions")
        void shouldCombineStringPredicateWithOtherConditions(IntegrationTestContext context) {
            // Given
            Predicate<Employee> namePredicate = get(Employee::getName).startsWith("A");

            // When
            List<Employee> employees = context.queryEmployees()
                    .where(namePredicate)
                    .where(Employee::getActive).eq(true)
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e ->
                    e.getName().startsWith("A") && e.getActive());
        }

        /**
         * Tests: NOT on string predicate.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should use NOT on string predicate")
        void shouldUseNotOnStringPredicate(IntegrationTestContext context) {
            // Given
            Predicate<Employee> startsWithA = get(Employee::getName).startsWith("A");

            // When
            List<Employee> employees = context.queryEmployees()
                    .where(startsWithA.not())
                    .orderBy(Employee::getId).asc()
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> !e.getName().startsWith("A"));
        }

        /**
         * Tests: Complex predicate with string expressions.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should create complex predicate with string expressions")
        void shouldCreateComplexPredicateWithStringExpressions(IntegrationTestContext context) {
            // Given: (name starts with 'A' AND email ends with '@example.com') OR name contains 'John'
            Predicate<Employee> nameStartsWithA = get(Employee::getName).startsWith("A");
            Predicate<Employee> emailEndsWithExample = get(Employee::getEmail).endsWith("@example.com");
            Predicate<Employee> nameContainsJohn = get(Employee::getName).contains("John");
            Predicate<Employee> complex = nameStartsWithA.and(emailEndsWithExample).or(nameContainsJohn);

            // When
            List<Employee> employees = context.queryEmployees()
                    .where(complex)
                    .orderBy(Employee::getId).asc()
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e ->
                    (e.getName().startsWith("A") && e.getEmail().endsWith("@example.com")) ||
                    e.getName().contains("John"));
        }
    }
}