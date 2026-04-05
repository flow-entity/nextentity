package io.github.nextentity.integration;

import io.github.nextentity.integration.config.IntegrationTestContext;
import io.github.nextentity.integration.config.IntegrationTestProvider;
import io.github.nextentity.integration.entity.Employee;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

///
 /// String 操作s integration tests.
 /// <p>
 /// 测试s string 操作s including:
 /// - LIKE pattern matching
 /// - String functions (lower, upper, trim, substring, length)
 /// - String sorting
 /// <p>
 /// These tests run against MySQL and PostgreSQL using 测试containers.
 /// 
 /// @author HuangChengwei
@DisplayName("String Operations Integration Tests")
public class StringOperationsIntegrationTest {

///
     /// 测试s LIKE with prefix pattern.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with LIKE prefix pattern")
    void shouldFilterWithLikePrefix(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).like("A%")
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getName().startsWith("A"));
    }

///
     /// 测试s LIKE with suffix pattern.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with LIKE suffix pattern")
    void shouldFilterWithLikeSuffix(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getEmail).like("%@example.com")
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getEmail().endsWith("@example.com"));
    }

///
     /// 测试s LIKE with contains pattern.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with LIKE contains pattern")
    void shouldFilterWithLikeContains(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).like("%son%")
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getName().toLowerCase().contains("son"));
    }

///
     /// 测试s startsWith 方法.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with startsWith")
    void shouldFilterWithStartsWith(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).startsWith("Alice")
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getName().startsWith("Alice"));
    }

///
     /// 测试s endsWith 方法.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with endsWith")
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
     /// 测试s contains 方法.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with contains")
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
     /// 测试s NOT LIKE.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with NOT LIKE")
    void shouldFilterWithNotLike(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).notLike("A%")
                .orderBy(Employee::getId).asc()
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> !e.getName().startsWith("A"));
    }

///
     /// 测试s notStartsWith 方法.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with notStartsWith")
    void shouldFilterWithNotStartsWith(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).notStartsWith("A")
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> !e.getName().startsWith("A"));
    }

///
     /// 测试s notContains 方法.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with notContains")
    void shouldFilterWithNotContains(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).notContains("Alice")
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> !e.getName().contains("Alice"));
    }

///
     /// 测试s likeIfNotNull with non-null value.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with likeIfNotNull when value is not null")
    void shouldFilterWithLikeIfNotNull(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).likeIfNotNull("A%")
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getName().startsWith("A"));
    }

///
     /// 测试s likeIfNotNull with null value.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should return all when likeIfNotNull is null")
    void shouldReturnAllWhenLikeIfNotNullIsNull(IntegrationTestContext context) {
        // Given
        long totalCount = context.queryEmployees().count();

        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).likeIfNotNull(null)
                .list();

        // Then
        assertThat(employees).hasSize((int) totalCount);
    }

///
     /// 测试s likeIfNotEmpty with non-empty value.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with likeIfNotEmpty when value is not empty")
    void shouldFilterWithLikeIfNotEmpty(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).likeIfNotEmpty("A%")
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getName().startsWith("A"));
    }

///
     /// 测试s likeIfNotEmpty with empty value.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should return all when likeIfNotEmpty is empty")
    void shouldReturnAllWhenLikeIfNotEmptyIsEmpty(IntegrationTestContext context) {
        // Given
        long totalCount = context.queryEmployees().count();

        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).likeIfNotEmpty("")
                .list();

        // Then
        assertThat(employees).hasSize((int) totalCount);
    }

///
     /// 测试s multiple LIKE conditions.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with multiple LIKE conditions")
    void shouldFilterWithMultipleLikeConditions(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).like("A%")
                .where(Employee::getEmail).like("%@example.com")
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e ->
                e.getName().startsWith("A") && e.getEmail().endsWith("@example.com"));
    }

///
     /// 测试s LIKE with special characters.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with LIKE containing special characters")
    void shouldFilterWithLikeSpecialChars(IntegrationTestContext context) {
        // When - email contains @
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getEmail).like("%@%")
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getEmail().contains("@"));
    }

///
     /// 测试s case-insensitive LIKE pattern.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with case-insensitive pattern using lower")
    void shouldFilterWithCaseInsensitiveUsingLower(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).lower().like("alice%")
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getName().toLowerCase().startsWith("alice"));
    }

///
     /// 测试s string length function.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter by string length")
    void shouldFilterByStringLength(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).length().ge(10)
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getName().length() >= 10);
    }

///
     /// 测试s ordering by string field ascending.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should order by string field ascending")
    void shouldOrderByStringAsc(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .orderBy(Employee::getName).asc()
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        for (int i = 1; i < employees.size(); i++) {
            assertThat(employees.get(i).getName().compareTo(employees.get(i - 1).getName()))
                    .isGreaterThanOrEqualTo(0);
        }
    }

///
     /// 测试s ordering by string field descending.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should order by string field descending")
    void shouldOrderByStringDesc(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .orderBy(Employee::getName).desc()
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        for (int i = 1; i < employees.size(); i++) {
            assertThat(employees.get(i).getName().compareTo(employees.get(i - 1).getName()))
                    .isLessThanOrEqualTo(0);
        }
    }

///
     /// 测试s LIKE with underscore wildcard.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle LIKE with underscore wildcard")
    void shouldHandleLikeWithUnderscore(IntegrationTestContext context) {
        // When - any single character followed by 'lice'
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).like("_lice%")
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        // Alice should match
        assertThat(employees).anyMatch(e -> e.getName().contains("lice"));
    }

///
     /// 测试s combined string 操作s.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should combine multiple string operations")
    void shouldCombineStringOperations(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).like("A%")
                .where(Employee::getEmail).contains("@")
                .orderBy(Employee::getName).asc()
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e ->
                e.getName().startsWith("A") && e.getEmail().contains("@"));
    }

///
     /// 测试s LIKE with no matches.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should return empty when LIKE has no matches")
    void shouldReturnEmptyWhenNoMatches(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).like("ZZZZZZ%")
                .list();

        // Then
        assertThat(employees).isEmpty();
    }

///
     /// 测试s selecting string field.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select string field")
    void shouldSelectStringField(IntegrationTestContext context) {
        // When
        List<String> names = context.queryEmployees()
                .select(Employee::getName)
                .orderBy(Employee::getName).asc()
                .list();

        // Then
        assertThat(names).isNotEmpty();
        assertThat(names).doesNotContainNull();
    }

///
     /// 测试s selecting distinct string values.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select distinct string values")
    void shouldSelectDistinctString(IntegrationTestContext context) {
        // When
        List<String> emails = context.queryEmployees()
                .selectDistinct(Employee::getEmail)
                .list();

        // Then
        assertThat(emails).isNotEmpty();
        assertThat(emails).hasSize((int) context.queryEmployees().count());
    }

    // ==================== IfNotNull Default Methods Tests ====================

///
     /// 测试s startsWithIfNotNull with non-null value.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with startsWithIfNotNull when value is not null")
    void shouldFilterWithStartsWithIfNotNull(IntegrationTestContext context) {
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
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should return all when startsWithIfNotNull is null")
    void shouldReturnAllWhenStartsWithIfNotNullIsNull(IntegrationTestContext context) {
        // Given
        long totalCount = context.queryEmployees().count();

        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).startsWithIfNotNull(null)
                .list();

        // Then
        assertThat(employees).hasSize((int) totalCount);
    }

///
     /// 测试s endsWithIfNotNull with non-null value.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with endsWithIfNotNull when value is not null")
    void shouldFilterWithEndsWithIfNotNull(IntegrationTestContext context) {
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
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should return all when endsWithIfNotNull is null")
    void shouldReturnAllWhenEndsWithIfNotNullIsNull(IntegrationTestContext context) {
        // Given
        long totalCount = context.queryEmployees().count();

        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getEmail).endsWithIfNotNull(null)
                .list();

        // Then
        assertThat(employees).hasSize((int) totalCount);
    }

///
     /// 测试s containsIfNotNull with non-null value.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with containsIfNotNull when value is not null")
    void shouldFilterWithContainsIfNotNull(IntegrationTestContext context) {
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
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should return all when containsIfNotNull is null")
    void shouldReturnAllWhenContainsIfNotNullIsNull(IntegrationTestContext context) {
        // Given
        long totalCount = context.queryEmployees().count();

        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).containsIfNotNull(null)
                .list();

        // Then
        assertThat(employees).hasSize((int) totalCount);
    }

///
     /// 测试s notStartsWithIfNotNull with non-null value.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with notStartsWithIfNotNull when value is not null")
    void shouldFilterWithNotStartsWithIfNotNull(IntegrationTestContext context) {
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
    @DisplayName("Should return all when notStartsWithIfNotNull is null")
    void shouldReturnAllWhenNotStartsWithIfNotNullIsNull(IntegrationTestContext context) {
        // Given
        long totalCount = context.queryEmployees().count();

        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).notStartsWithIfNotNull(null)
                .list();

        // Then
        assertThat(employees).hasSize((int) totalCount);
    }

///
     /// 测试s notEndsWithIfNotNull with non-null value.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with notEndsWithIfNotNull when value is not null")
    void shouldFilterWithNotEndsWithIfNotNull(IntegrationTestContext context) {
        // When - filter emails not ending with a non-existent suffix
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getEmail).notEndsWithIfNotNull("@nonexistent.org")
                .orderBy(Employee::getId).asc()
                .list();

        // Then - all emails should not end with @nonexistent.org (which is all of them)
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> !e.getEmail().endsWith("@nonexistent.org"));
    }

///
     /// 测试s notEndsWithIfNotNull with null value.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should return all when notEndsWithIfNotNull is null")
    void shouldReturnAllWhenNotEndsWithIfNotNullIsNull(IntegrationTestContext context) {
        // Given
        long totalCount = context.queryEmployees().count();

        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getEmail).notEndsWithIfNotNull(null)
                .list();

        // Then
        assertThat(employees).hasSize((int) totalCount);
    }

///
     /// 测试s notContainsIfNotNull with non-null value.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with notContainsIfNotNull when value is not null")
    void shouldFilterWithNotContainsIfNotNull(IntegrationTestContext context) {
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
    @DisplayName("Should return all when notContainsIfNotNull is null")
    void shouldReturnAllWhenNotContainsIfNotNullIsNull(IntegrationTestContext context) {
        // Given
        long totalCount = context.queryEmployees().count();

        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).notContainsIfNotNull(null)
                .list();

        // Then
        assertThat(employees).hasSize((int) totalCount);
    }

    // ==================== IfNotEmpty Default Methods Tests ====================

///
     /// 测试s startsWithIfNotEmpty with non-empty value.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with startsWithIfNotEmpty when value is not empty")
    void shouldFilterWithStartsWithIfNotEmpty(IntegrationTestContext context) {
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
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should return all when startsWithIfNotEmpty is empty")
    void shouldReturnAllWhenStartsWithIfNotEmptyIsEmpty(IntegrationTestContext context) {
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
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should return all when startsWithIfNotEmpty is null")
    void shouldReturnAllWhenStartsWithIfNotEmptyIsNull(IntegrationTestContext context) {
        // Given
        long totalCount = context.queryEmployees().count();

        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).startsWithIfNotEmpty(null)
                .list();

        // Then
        assertThat(employees).hasSize((int) totalCount);
    }

///
     /// 测试s endsWithIfNotEmpty with non-empty value.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with endsWithIfNotEmpty when value is not empty")
    void shouldFilterWithEndsWithIfNotEmpty(IntegrationTestContext context) {
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
    @DisplayName("Should return all when endsWithIfNotEmpty is empty")
    void shouldReturnAllWhenEndsWithIfNotEmptyIsEmpty(IntegrationTestContext context) {
        // Given
        long totalCount = context.queryEmployees().count();

        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getEmail).endsWithIfNotEmpty("")
                .list();

        // Then
        assertThat(employees).hasSize((int) totalCount);
    }

///
     /// 测试s containsIfNotEmpty with non-empty value.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with containsIfNotEmpty when value is not empty")
    void shouldFilterWithContainsIfNotEmpty(IntegrationTestContext context) {
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
    @DisplayName("Should return all when containsIfNotEmpty is empty")
    void shouldReturnAllWhenContainsIfNotEmptyIsEmpty(IntegrationTestContext context) {
        // Given
        long totalCount = context.queryEmployees().count();

        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).containsIfNotEmpty("")
                .list();

        // Then
        assertThat(employees).hasSize((int) totalCount);
    }

///
     /// 测试s notStartsWithIfNotEmpty with non-empty value.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with notStartsWithIfNotEmpty when value is not empty")
    void shouldFilterWithNotStartsWithIfNotEmpty(IntegrationTestContext context) {
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
    @DisplayName("Should return all when notStartsWithIfNotEmpty is empty")
    void shouldReturnAllWhenNotStartsWithIfNotEmptyIsEmpty(IntegrationTestContext context) {
        // Given
        long totalCount = context.queryEmployees().count();

        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).notStartsWithIfNotEmpty("")
                .list();

        // Then
        assertThat(employees).hasSize((int) totalCount);
    }

///
     /// 测试s notEndsWithIfNotEmpty with non-empty value.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with notEndsWithIfNotEmpty when value is not empty")
    void shouldFilterWithNotEndsWithIfNotEmpty(IntegrationTestContext context) {
        // When - filter emails not ending with a non-existent suffix
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getEmail).notEndsWithIfNotEmpty("@nonexistent.org")
                .orderBy(Employee::getId).asc()
                .list();

        // Then - all emails should not end with @nonexistent.org (which is all of them)
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> !e.getEmail().endsWith("@nonexistent.org"));
    }

///
     /// 测试s notEndsWithIfNotEmpty with empty value.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should return all when notEndsWithIfNotEmpty is empty")
    void shouldReturnAllWhenNotEndsWithIfNotEmptyIsEmpty(IntegrationTestContext context) {
        // Given
        long totalCount = context.queryEmployees().count();

        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getEmail).notEndsWithIfNotEmpty("")
                .list();

        // Then
        assertThat(employees).hasSize((int) totalCount);
    }

///
     /// 测试s notContainsIfNotEmpty with non-empty value.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with notContainsIfNotEmpty when value is not empty")
    void shouldFilterWithNotContainsIfNotEmpty(IntegrationTestContext context) {
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
    @DisplayName("Should return all when notContainsIfNotEmpty is empty")
    void shouldReturnAllWhenNotContainsIfNotEmptyIsEmpty(IntegrationTestContext context) {
        // Given
        long totalCount = context.queryEmployees().count();

        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).notContainsIfNotEmpty("")
                .list();

        // Then
        assertThat(employees).hasSize((int) totalCount);
    }

    // ==================== Combined Conditional String Tests ====================

///
     /// 测试s combining multiple IfNotNull string conditions.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should combine multiple IfNotNull string conditions")
    void shouldCombineMultipleIfNotNullStringConditions(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).startsWithIfNotNull("A")
                .where(Employee::getEmail).containsIfNotNull("@")
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e ->
                e.getName().startsWith("A") && e.getEmail().contains("@"));
    }

///
     /// 测试s combining IfNotNull with null value (should skip condition).
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should skip condition when IfNotNull value is null")
    void shouldSkipConditionWhenIfNotNullIsNull(IntegrationTestContext context) {
        // Given
        long totalCount = context.queryEmployees().count();

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
     /// 测试s combining IfNotEmpty with empty value (should skip condition).
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should skip condition when IfNotEmpty value is empty")
    void shouldSkipConditionWhenIfNotEmptyIsEmpty(IntegrationTestContext context) {
        // Given
        long totalCount = context.queryEmployees().count();

        // When - one empty condition should be skipped
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).startsWithIfNotEmpty("A")
                .where(Employee::getEmail).containsIfNotEmpty("")
                .list();

        // Then - should only filter by name condition
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getName().startsWith("A"));
    }
}
