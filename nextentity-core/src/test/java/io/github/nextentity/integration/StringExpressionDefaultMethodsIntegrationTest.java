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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

///
 /// Integration tests for StringExpression default 方法.
 /// <p>
 /// 测试s default 方法 in StringExpression interface including:
 /// - endsWith(String): Match patterns ending with specified string
 /// - contains(String): Match patterns containing specified string
 /// - notStartsWith(String): Does not match patterns starting with specified string
 /// - notEndsWith(String): Does not match patterns ending with specified string
 /// - notContains(String): Does not match patterns containing specified string
 /// - IfNotNull variants: Conditional string matching when value is not null
 /// - IfNotEmpty variants: Conditional string matching when value is not empty
 /// - substring(int): Substring from offset to end
 /// <p>
 /// These tests run against MySQL and PostgreSQL using 测试containers.
 /// 
 /// @author HuangChengwei
 /// @see io.github.nextentity.api.StringExpression
@DisplayName("StringExpression Default Methods Integration Tests")
public class StringExpressionDefaultMethodsIntegrationTest {

    // ==================== endsWith Tests ====================

///
     /// 测试s endsWith(String) - matches patterns ending with specified string.
     /// This is a default 方法 that calls like('%' + value).
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with endsWith in WHERE clause")
    void shouldFilterWithEndsWith(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getEmail).endsWith("@example.com")
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getEmail().endsWith("@example.com"));
    }

///
     /// 测试s endsWith with non-matching pattern.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should return empty when endsWith has no matches")
    void shouldReturnEmptyWhenEndsWithNoMatches(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getEmail).endsWith("@nonexistent.org")
                .list();

        // Then
        assertThat(employees).isEmpty();
    }

    // ==================== contains Tests ====================

///
     /// 测试s contains(String) - matches patterns containing specified string.
     /// This is a default 方法 that calls like('%' + value + '%').
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with contains in WHERE clause")
    void shouldFilterWithContains(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).contains("John")
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getName().contains("John"));
    }

///
     /// 测试s contains with case-sensitive match.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with contains case-sensitive")
    void shouldFilterWithContainsCaseSensitive(IntegrationTestContext context) {
        // When - search for lowercase
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).lower().contains("john")
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getName().toLowerCase().contains("john"));
    }

    // ==================== notStartsWith Tests ====================

///
     /// 测试s notStartsWith(String) - does not match patterns starting with specified string.
     /// This is a default 方法 that calls notLike(value + '%').
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with notStartsWith in WHERE clause")
    void shouldFilterWithNotStartsWith(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).notStartsWith("A")
                .orderBy(Employee::getId).asc()
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> !e.getName().startsWith("A"));
    }

    // ==================== notEndsWith Tests ====================

///
     /// 测试s notEndsWith(String) - does not match patterns ending with specified string.
     /// This is a default 方法 that calls notLike('%' + value).
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with notEndsWith in WHERE clause")
    void shouldFilterWithNotEndsWith(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getEmail).notEndsWith("@nonexistent.org")
                .orderBy(Employee::getId).asc()
                .list();

        // Then - all emails should not end with @nonexistent.org
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> !e.getEmail().endsWith("@nonexistent.org"));
    }

    // ==================== notContains Tests ====================

///
     /// 测试s notContains(String) - does not match patterns containing specified string.
     /// This is a default 方法 that calls notLike('%' + value + '%').
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with notContains in WHERE clause")
    void shouldFilterWithNotContains(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).notContains("Alice")
                .orderBy(Employee::getId).asc()
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> !e.getName().contains("Alice"));
    }

    // ==================== startsWithIfNotNull Tests ====================

///
     /// 测试s startsWithIfNotNull with non-null value.
     /// Covers the branch: value != null.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with startsWithIfNotNull when value is not null")
    void shouldFilterWithStartsWithIfNotNullNonNull(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).startsWithIfNotNull("Alice")
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getName().startsWith("Alice"));
    }

///
     /// 测试s startsWithIfNotNull with null value.
     /// Covers the branch: value == null.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should skip filter when startsWithIfNotNull is null")
    void shouldSkipFilterWhenStartsWithIfNotNullNull(IntegrationTestContext context) {
        // Given
        long totalCount = context.queryEmployees().count();

        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).startsWithIfNotNull(null)
                .list();

        // Then
        assertThat(employees).hasSize((int) totalCount);
    }

    // ==================== endsWithIfNotNull Tests ====================

///
     /// 测试s endsWithIfNotNull with non-null value.
     /// Covers the branch: value != null.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with endsWithIfNotNull when value is not null")
    void shouldFilterWithEndsWithIfNotNullNonNull(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getEmail).endsWithIfNotNull("@example.com")
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getEmail().endsWith("@example.com"));
    }

///
     /// 测试s endsWithIfNotNull with null value.
     /// Covers the branch: value == null.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should skip filter when endsWithIfNotNull is null")
    void shouldSkipFilterWhenEndsWithIfNotNullNull(IntegrationTestContext context) {
        // Given
        long totalCount = context.queryEmployees().count();

        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getEmail).endsWithIfNotNull(null)
                .list();

        // Then
        assertThat(employees).hasSize((int) totalCount);
    }

    // ==================== containsIfNotNull Tests ====================

///
     /// 测试s containsIfNotNull with non-null value.
     /// Covers the branch: value != null.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with containsIfNotNull when value is not null")
    void shouldFilterWithContainsIfNotNullNonNull(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).containsIfNotNull("John")
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getName().contains("John"));
    }

///
     /// 测试s containsIfNotNull with null value.
     /// Covers the branch: value == null.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should skip filter when containsIfNotNull is null")
    void shouldSkipFilterWhenContainsIfNotNullNull(IntegrationTestContext context) {
        // Given
        long totalCount = context.queryEmployees().count();

        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).containsIfNotNull(null)
                .list();

        // Then
        assertThat(employees).hasSize((int) totalCount);
    }

    // ==================== notStartsWithIfNotNull Tests ====================

///
     /// 测试s notStartsWithIfNotNull with non-null value.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with notStartsWithIfNotNull when value is not null")
    void shouldFilterWithNotStartsWithIfNotNullNonNull(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).notStartsWithIfNotNull("A")
                .orderBy(Employee::getId).asc()
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> !e.getName().startsWith("A"));
    }

///
     /// 测试s notStartsWithIfNotNull with null value.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should skip filter when notStartsWithIfNotNull is null")
    void shouldSkipFilterWhenNotStartsWithIfNotNullNull(IntegrationTestContext context) {
        // Given
        long totalCount = context.queryEmployees().count();

        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).notStartsWithIfNotNull(null)
                .list();

        // Then
        assertThat(employees).hasSize((int) totalCount);
    }

    // ==================== notEndsWithIfNotNull Tests ====================

///
     /// 测试s notEndsWithIfNotNull with non-null value.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with notEndsWithIfNotNull when value is not null")
    void shouldFilterWithNotEndsWithIfNotNullNonNull(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getEmail).notEndsWithIfNotNull("@nonexistent.org")
                .orderBy(Employee::getId).asc()
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> !e.getEmail().endsWith("@nonexistent.org"));
    }

///
     /// 测试s notEndsWithIfNotNull with null value.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should skip filter when notEndsWithIfNotNull is null")
    void shouldSkipFilterWhenNotEndsWithIfNotNullNull(IntegrationTestContext context) {
        // Given
        long totalCount = context.queryEmployees().count();

        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getEmail).notEndsWithIfNotNull(null)
                .list();

        // Then
        assertThat(employees).hasSize((int) totalCount);
    }

    // ==================== notContainsIfNotNull Tests ====================

///
     /// 测试s notContainsIfNotNull with non-null value.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with notContainsIfNotNull when value is not null")
    void shouldFilterWithNotContainsIfNotNullNonNull(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).notContainsIfNotNull("Alice")
                .orderBy(Employee::getId).asc()
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> !e.getName().contains("Alice"));
    }

///
     /// 测试s notContainsIfNotNull with null value.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should skip filter when notContainsIfNotNull is null")
    void shouldSkipFilterWhenNotContainsIfNotNullNull(IntegrationTestContext context) {
        // Given
        long totalCount = context.queryEmployees().count();

        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).notContainsIfNotNull(null)
                .list();

        // Then
        assertThat(employees).hasSize((int) totalCount);
    }

    // ==================== startsWithIfNotEmpty Tests ====================

///
     /// 测试s startsWithIfNotEmpty with non-empty value.
     /// Covers the branch: value != null && !value.isEmpty().
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with startsWithIfNotEmpty when value is not empty")
    void shouldFilterWithStartsWithIfNotEmptyNonEmpty(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).startsWithIfNotEmpty("Alice")
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getName().startsWith("Alice"));
    }

///
     /// 测试s startsWithIfNotEmpty with empty value.
     /// Covers the branch: value.isEmpty().
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should skip filter when startsWithIfNotEmpty is empty")
    void shouldSkipFilterWhenStartsWithIfNotEmptyEmpty(IntegrationTestContext context) {
        // Given
        long totalCount = context.queryEmployees().count();

        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).startsWithIfNotEmpty("")
                .list();

        // Then
        assertThat(employees).hasSize((int) totalCount);
    }

///
     /// 测试s startsWithIfNotEmpty with null value.
     /// Covers the branch: value == null.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should skip filter when startsWithIfNotEmpty is null")
    void shouldSkipFilterWhenStartsWithIfNotEmptyNull(IntegrationTestContext context) {
        // Given
        long totalCount = context.queryEmployees().count();

        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).startsWithIfNotEmpty(null)
                .list();

        // Then
        assertThat(employees).hasSize((int) totalCount);
    }

    // ==================== endsWithIfNotEmpty Tests ====================

///
     /// 测试s endsWithIfNotEmpty with non-empty value.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with endsWithIfNotEmpty when value is not empty")
    void shouldFilterWithEndsWithIfNotEmptyNonEmpty(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getEmail).endsWithIfNotEmpty("@example.com")
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getEmail().endsWith("@example.com"));
    }

///
     /// 测试s endsWithIfNotEmpty with empty value.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should skip filter when endsWithIfNotEmpty is empty")
    void shouldSkipFilterWhenEndsWithIfNotEmptyEmpty(IntegrationTestContext context) {
        // Given
        long totalCount = context.queryEmployees().count();

        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getEmail).endsWithIfNotEmpty("")
                .list();

        // Then
        assertThat(employees).hasSize((int) totalCount);
    }

    // ==================== containsIfNotEmpty Tests ====================

///
     /// 测试s containsIfNotEmpty with non-empty value.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with containsIfNotEmpty when value is not empty")
    void shouldFilterWithContainsIfNotEmptyNonEmpty(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).containsIfNotEmpty("John")
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getName().contains("John"));
    }

///
     /// 测试s containsIfNotEmpty with empty value.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should skip filter when containsIfNotEmpty is empty")
    void shouldSkipFilterWhenContainsIfNotEmptyEmpty(IntegrationTestContext context) {
        // Given
        long totalCount = context.queryEmployees().count();

        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).containsIfNotEmpty("")
                .list();

        // Then
        assertThat(employees).hasSize((int) totalCount);
    }

    // ==================== notStartsWithIfNotEmpty Tests ====================

///
     /// 测试s notStartsWithIfNotEmpty with non-empty value.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with notStartsWithIfNotEmpty when value is not empty")
    void shouldFilterWithNotStartsWithIfNotEmptyNonEmpty(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).notStartsWithIfNotEmpty("A")
                .orderBy(Employee::getId).asc()
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> !e.getName().startsWith("A"));
    }

///
     /// 测试s notStartsWithIfNotEmpty with empty value.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should skip filter when notStartsWithIfNotEmpty is empty")
    void shouldSkipFilterWhenNotStartsWithIfNotEmptyEmpty(IntegrationTestContext context) {
        // Given
        long totalCount = context.queryEmployees().count();

        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).notStartsWithIfNotEmpty("")
                .list();

        // Then
        assertThat(employees).hasSize((int) totalCount);
    }

    // ==================== notEndsWithIfNotEmpty Tests ====================

///
     /// 测试s notEndsWithIfNotEmpty with non-empty value.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with notEndsWithIfNotEmpty when value is not empty")
    void shouldFilterWithNotEndsWithIfNotEmptyNonEmpty(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getEmail).notEndsWithIfNotEmpty("@nonexistent.org")
                .orderBy(Employee::getId).asc()
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> !e.getEmail().endsWith("@nonexistent.org"));
    }

///
     /// 测试s notEndsWithIfNotEmpty with empty value.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should skip filter when notEndsWithIfNotEmpty is empty")
    void shouldSkipFilterWhenNotEndsWithIfNotEmptyEmpty(IntegrationTestContext context) {
        // Given
        long totalCount = context.queryEmployees().count();

        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getEmail).notEndsWithIfNotEmpty("")
                .list();

        // Then
        assertThat(employees).hasSize((int) totalCount);
    }

    // ==================== notContainsIfNotEmpty Tests ====================

///
     /// 测试s notContainsIfNotEmpty with non-empty value.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with notContainsIfNotEmpty when value is not empty")
    void shouldFilterWithNotContainsIfNotEmptyNonEmpty(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).notContainsIfNotEmpty("Alice")
                .orderBy(Employee::getId).asc()
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> !e.getName().contains("Alice"));
    }

///
     /// 测试s notContainsIfNotEmpty with empty value.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should skip filter when notContainsIfNotEmpty is empty")
    void shouldSkipFilterWhenNotContainsIfNotEmptyEmpty(IntegrationTestContext context) {
        // Given
        long totalCount = context.queryEmployees().count();

        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).notContainsIfNotEmpty("")
                .list();

        // Then
        assertThat(employees).hasSize((int) totalCount);
    }

    // ==================== Combined Conditional Tests ====================

///
     /// 测试s combining multiple IfNotNull conditions.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should combine multiple IfNotNull conditions")
    void shouldCombineMultipleIfNotNullConditions(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).startsWithIfNotNull("A")
                .where(Employee::getEmail).endsWithIfNotNull("@example.com")
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e ->
                e.getName().startsWith("A") && e.getEmail().endsWith("@example.com"));
    }

///
     /// 测试s combining IfNotNull with null value (should skip).
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should skip null IfNotNull in combined conditions")
    void shouldSkipNullIfNotNullInCombinedConditions(IntegrationTestContext context) {
        // When - one null condition should be skipped
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).startsWithIfNotNull("A")
                .where(Employee::getEmail).containsIfNotNull(null)
                .list();

        // Then - should only filter by name condition
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getName().startsWith("A"));
    }

///
     /// 测试s combining multiple IfNotEmpty conditions.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should combine multiple IfNotEmpty conditions")
    void shouldCombineMultipleIfNotEmptyConditions(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).startsWithIfNotEmpty("A")
                .where(Employee::getEmail).endsWithIfNotEmpty("@example.com")
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e ->
                e.getName().startsWith("A") && e.getEmail().endsWith("@example.com"));
    }

///
     /// 测试s combining IfNotEmpty with empty value (should skip).
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should skip empty IfNotEmpty in combined conditions")
    void shouldSkipEmptyIfNotEmptyInCombinedConditions(IntegrationTestContext context) {
        // When - one empty condition should be skipped
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).startsWithIfNotEmpty("A")
                .where(Employee::getEmail).containsIfNotEmpty("")
                .list();

        // Then - should only filter by name condition
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getName().startsWith("A"));
    }

    // ==================== Count and Exist Tests ====================

///
     /// 测试s count with string expression condition.
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

///
     /// 测试s exist with string expression condition.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should check exist with string expression condition")
    void shouldCheckExistWithStringExpressionCondition(IntegrationTestContext context) {
        // When
        boolean exists = context.queryEmployees()
                .where(Employee::getName).contains("John")
                .exists();

        // Then
        assertThat(exists).isTrue();
    }

    // ==================== Predicate as Query Condition Tests ====================

///
     /// 测试s using startsWith predicate as query condition.
    @Nested
    @DisplayName("Predicate as Query Condition Tests")
    class PredicateAsQueryConditionTests {

///
         /// 测试s: startsWith creates Predicate that can be passed to where().
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should use startsWith predicate as where condition")
        void shouldUseStartsWithPredicateAsWhereCondition(IntegrationTestContext context) {
            // Given - create predicate using startsWith
            Predicate<Employee> predicate = Path.of(Employee::getName).startsWith("Alice");

            // When - pass predicate to where()
            List<Employee> employees = context.queryEmployees()
                    .where(predicate)
                    .list();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> e.getName().startsWith("Alice"));
        }

///
         /// 测试s: endsWith creates Predicate that can be passed to where().
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should use endsWith predicate as where condition")
        void shouldUseEndsWithPredicateAsWhereCondition(IntegrationTestContext context) {
            // Given
            Predicate<Employee> predicate = Path.of(Employee::getEmail).endsWith("@example.com");

            // When
            List<Employee> employees = context.queryEmployees()
                    .where(predicate)
                    .list();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> e.getEmail().endsWith("@example.com"));
        }

///
         /// 测试s: contains creates Predicate that can be passed to where().
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should use contains predicate as where condition")
        void shouldUseContainsPredicateAsWhereCondition(IntegrationTestContext context) {
            // Given
            Predicate<Employee> predicate = Path.of(Employee::getName).contains("John");

            // When
            List<Employee> employees = context.queryEmployees()
                    .where(predicate)
                    .list();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> e.getName().contains("John"));
        }

///
         /// 测试s: notStartsWith creates Predicate that can be passed to where().
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should use notStartsWith predicate as where condition")
        void shouldUseNotStartsWithPredicateAsWhereCondition(IntegrationTestContext context) {
            // Given
            Predicate<Employee> predicate = Path.of(Employee::getName).notStartsWith("A");

            // When
            List<Employee> employees = context.queryEmployees()
                    .where(predicate)
                    .orderBy(Employee::getId).asc()
                    .list();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> !e.getName().startsWith("A"));
        }

///
         /// 测试s: notEndsWith creates Predicate that can be passed to where().
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should use notEndsWith predicate as where condition")
        void shouldUseNotEndsWithPredicateAsWhereCondition(IntegrationTestContext context) {
            // Given
            Predicate<Employee> predicate = Path.of(Employee::getEmail).notEndsWith("@nonexistent.org");

            // When
            List<Employee> employees = context.queryEmployees()
                    .where(predicate)
                    .orderBy(Employee::getId).asc()
                    .list();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> !e.getEmail().endsWith("@nonexistent.org"));
        }

///
         /// 测试s: notContains creates Predicate that can be passed to where().
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should use notContains predicate as where condition")
        void shouldUseNotContainsPredicateAsWhereCondition(IntegrationTestContext context) {
            // Given
            Predicate<Employee> predicate = Path.of(Employee::getName).notContains("Alice");

            // When
            List<Employee> employees = context.queryEmployees()
                    .where(predicate)
                    .orderBy(Employee::getId).asc()
                    .list();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> !e.getName().contains("Alice"));
        }

///
         /// 测试s: startsWithIfNotNull with non-null creates Predicate.
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should use startsWithIfNotNull predicate as where condition")
        void shouldUseStartsWithIfNotNullPredicateAsWhereCondition(IntegrationTestContext context) {
            // Given
            Predicate<Employee> predicate = Path.of(Employee::getName).startsWithIfNotNull("Alice");

            // When
            List<Employee> employees = context.queryEmployees()
                    .where(predicate)
                    .list();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> e.getName().startsWith("Alice"));
        }

///
         /// 测试s: startsWithIfNotNull with null creates empty Predicate.
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should use startsWithIfNotNull null predicate as where condition")
        void shouldUseStartsWithIfNotNullNullPredicateAsWhereCondition(IntegrationTestContext context) {
            // Given
            long totalCount = context.queryEmployees().count();
            Predicate<Employee> predicate = Path.of(Employee::getName).startsWithIfNotNull(null);

            // When
            List<Employee> employees = context.queryEmployees()
                    .where(predicate)
                    .list();

            // Then
            assertThat(employees).hasSize((int) totalCount);
        }

///
         /// 测试s: endsWithIfNotNull predicate as where condition.
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should use endsWithIfNotNull predicate as where condition")
        void shouldUseEndsWithIfNotNullPredicateAsWhereCondition(IntegrationTestContext context) {
            // Given
            Predicate<Employee> predicate = Path.of(Employee::getEmail).endsWithIfNotNull("@example.com");

            // When
            List<Employee> employees = context.queryEmployees()
                    .where(predicate)
                    .list();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> e.getEmail().endsWith("@example.com"));
        }

///
         /// 测试s: containsIfNotNull predicate as where condition.
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should use containsIfNotNull predicate as where condition")
        void shouldUseContainsIfNotNullPredicateAsWhereCondition(IntegrationTestContext context) {
            // Given
            Predicate<Employee> predicate = Path.of(Employee::getName).containsIfNotNull("John");

            // When
            List<Employee> employees = context.queryEmployees()
                    .where(predicate)
                    .list();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> e.getName().contains("John"));
        }

///
         /// 测试s: startsWithIfNotEmpty with non-empty creates Predicate.
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should use startsWithIfNotEmpty predicate as where condition")
        void shouldUseStartsWithIfNotEmptyPredicateAsWhereCondition(IntegrationTestContext context) {
            // Given
            Predicate<Employee> predicate = Path.of(Employee::getName).startsWithIfNotEmpty("Alice");

            // When
            List<Employee> employees = context.queryEmployees()
                    .where(predicate)
                    .list();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> e.getName().startsWith("Alice"));
        }

///
         /// 测试s: startsWithIfNotEmpty with empty creates empty Predicate.
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should use startsWithIfNotEmpty empty predicate as where condition")
        void shouldUseStartsWithIfNotEmptyEmptyPredicateAsWhereCondition(IntegrationTestContext context) {
            // Given
            long totalCount = context.queryEmployees().count();
            Predicate<Employee> predicate = Path.of(Employee::getName).startsWithIfNotEmpty("");

            // When
            List<Employee> employees = context.queryEmployees()
                    .where(predicate)
                    .list();

            // Then
            assertThat(employees).hasSize((int) totalCount);
        }

///
         /// 测试s: endsWithIfNotEmpty predicate as where condition.
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should use endsWithIfNotEmpty predicate as where condition")
        void shouldUseEndsWithIfNotEmptyPredicateAsWhereCondition(IntegrationTestContext context) {
            // Given
            Predicate<Employee> predicate = Path.of(Employee::getEmail).endsWithIfNotEmpty("@example.com");

            // When
            List<Employee> employees = context.queryEmployees()
                    .where(predicate)
                    .list();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> e.getEmail().endsWith("@example.com"));
        }

///
         /// 测试s: containsIfNotEmpty predicate as where condition.
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should use containsIfNotEmpty predicate as where condition")
        void shouldUseContainsIfNotEmptyPredicateAsWhereCondition(IntegrationTestContext context) {
            // Given
            Predicate<Employee> predicate = Path.of(Employee::getName).containsIfNotEmpty("John");

            // When
            List<Employee> employees = context.queryEmployees()
                    .where(predicate)
                    .list();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> e.getName().contains("John"));
        }

///
         /// 测试s: notStartsWithIfNotNull predicate as where condition.
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should use notStartsWithIfNotNull predicate as where condition")
        void shouldUseNotStartsWithIfNotNullPredicateAsWhereCondition(IntegrationTestContext context) {
            // Given
            Predicate<Employee> predicate = Path.of(Employee::getName).notStartsWithIfNotNull("A");

            // When
            List<Employee> employees = context.queryEmployees()
                    .where(predicate)
                    .orderBy(Employee::getId).asc()
                    .list();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> !e.getName().startsWith("A"));
        }

///
         /// 测试s: notEndsWithIfNotNull predicate as where condition.
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should use notEndsWithIfNotNull predicate as where condition")
        void shouldUseNotEndsWithIfNotNullPredicateAsWhereCondition(IntegrationTestContext context) {
            // Given
            Predicate<Employee> predicate = Path.of(Employee::getEmail).notEndsWithIfNotNull("@nonexistent.org");

            // When
            List<Employee> employees = context.queryEmployees()
                    .where(predicate)
                    .orderBy(Employee::getId).asc()
                    .list();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> !e.getEmail().endsWith("@nonexistent.org"));
        }

///
         /// 测试s: notContainsIfNotNull predicate as where condition.
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should use notContainsIfNotNull predicate as where condition")
        void shouldUseNotContainsIfNotNullPredicateAsWhereCondition(IntegrationTestContext context) {
            // Given
            Predicate<Employee> predicate = Path.of(Employee::getName).notContainsIfNotNull("Alice");

            // When
            List<Employee> employees = context.queryEmployees()
                    .where(predicate)
                    .orderBy(Employee::getId).asc()
                    .list();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> !e.getName().contains("Alice"));
        }

///
         /// 测试s: notStartsWithIfNotEmpty predicate as where condition.
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should use notStartsWithIfNotEmpty predicate as where condition")
        void shouldUseNotStartsWithIfNotEmptyPredicateAsWhereCondition(IntegrationTestContext context) {
            // Given
            Predicate<Employee> predicate = Path.of(Employee::getName).notStartsWithIfNotEmpty("A");

            // When
            List<Employee> employees = context.queryEmployees()
                    .where(predicate)
                    .orderBy(Employee::getId).asc()
                    .list();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> !e.getName().startsWith("A"));
        }

///
         /// 测试s: notEndsWithIfNotEmpty predicate as where condition.
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should use notEndsWithIfNotEmpty predicate as where condition")
        void shouldUseNotEndsWithIfNotEmptyPredicateAsWhereCondition(IntegrationTestContext context) {
            // Given
            Predicate<Employee> predicate = Path.of(Employee::getEmail).notEndsWithIfNotEmpty("@nonexistent.org");

            // When
            List<Employee> employees = context.queryEmployees()
                    .where(predicate)
                    .orderBy(Employee::getId).asc()
                    .list();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> !e.getEmail().endsWith("@nonexistent.org"));
        }

///
         /// 测试s: notContainsIfNotEmpty predicate as where condition.
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should use notContainsIfNotEmpty predicate as where condition")
        void shouldUseNotContainsIfNotEmptyPredicateAsWhereCondition(IntegrationTestContext context) {
            // Given
            Predicate<Employee> predicate = Path.of(Employee::getName).notContainsIfNotEmpty("Alice");

            // When
            List<Employee> employees = context.queryEmployees()
                    .where(predicate)
                    .orderBy(Employee::getId).asc()
                    .list();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> !e.getName().contains("Alice"));
        }

///
         /// 测试s: Combining multiple string predicates with AND.
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should combine multiple string predicates with AND")
        void shouldCombineMultipleStringPredicatesWithAnd(IntegrationTestContext context) {
            // Given
            Predicate<Employee> namePredicate = Path.of(Employee::getName).startsWith("A");
            Predicate<Employee> emailPredicate = Path.of(Employee::getEmail).endsWith("@example.com");
            Predicate<Employee> combined = namePredicate.and(emailPredicate);

            // When
            List<Employee> employees = context.queryEmployees()
                    .where(combined)
                    .list();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e ->
                    e.getName().startsWith("A") && e.getEmail().endsWith("@example.com"));
        }

///
         /// 测试s: Combining multiple string predicates with OR.
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should combine multiple string predicates with OR")
        void shouldCombineMultipleStringPredicatesWithOr(IntegrationTestContext context) {
            // Given
            Predicate<Employee> alicePredicate = Path.of(Employee::getName).startsWith("Alice");
            Predicate<Employee> johnPredicate = Path.of(Employee::getName).contains("John");
            Predicate<Employee> combined = alicePredicate.or(johnPredicate);

            // When
            List<Employee> employees = context.queryEmployees()
                    .where(combined)
                    .orderBy(Employee::getId).asc()
                    .list();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e ->
                    e.getName().startsWith("Alice") || e.getName().contains("John"));
        }

///
         /// 测试s: Combining string predicate with other conditions.
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should combine string predicate with other where conditions")
        void shouldCombineStringPredicateWithOtherConditions(IntegrationTestContext context) {
            // Given
            Predicate<Employee> namePredicate = Path.of(Employee::getName).startsWith("A");

            // When
            List<Employee> employees = context.queryEmployees()
                    .where(namePredicate)
                    .where(Employee::getActive).eq(true)
                    .list();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e ->
                    e.getName().startsWith("A") && e.getActive());
        }

///
         /// 测试s: NOT on string predicate.
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should use NOT on string predicate")
        void shouldUseNotOnStringPredicate(IntegrationTestContext context) {
            // Given
            Predicate<Employee> startsWithA = Path.of(Employee::getName).startsWith("A");

            // When
            List<Employee> employees = context.queryEmployees()
                    .where(startsWithA.not())
                    .orderBy(Employee::getId).asc()
                    .list();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> !e.getName().startsWith("A"));
        }

///
         /// 测试s: Complex predicate with string expressions.
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should create complex predicate with string expressions")
        void shouldCreateComplexPredicateWithStringExpressions(IntegrationTestContext context) {
            // Given: (name starts with 'A' AND email ends with '@example.com') OR name contains 'John'
            Predicate<Employee> nameStartsWithA = Path.of(Employee::getName).startsWith("A");
            Predicate<Employee> emailEndsWithExample = Path.of(Employee::getEmail).endsWith("@example.com");
            Predicate<Employee> nameContainsJohn = Path.of(Employee::getName).contains("John");
            Predicate<Employee> complex = nameStartsWithA.and(emailEndsWithExample).or(nameContainsJohn);

            // When
            List<Employee> employees = context.queryEmployees()
                    .where(complex)
                    .orderBy(Employee::getId).asc()
                    .list();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e ->
                    (e.getName().startsWith("A") && e.getEmail().endsWith("@example.com")) ||
                    e.getName().contains("John"));
        }
    }
}
