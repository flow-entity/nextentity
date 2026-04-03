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
 /// Integration tests for ExpressionBuilder.StringOperator default 方法.
 /// <p>
 /// 测试s default 方法 in ExpressionBuilder.StringOperator interface including:
 /// - IfNotNull series: startsWithIfNotNull, endsWithIfNotNull, containsIfNotNull, etc.
 /// - IfNotEmpty series: startsWithIfNotEmpty, endsWithIfNotEmpty, containsIfNotEmpty, etc.
 /// <p>
 /// These tests run against MySQL and PostgreSQL using 测试containers.
 /// 
 /// @author HuangChengwei
 /// @see io.github.nextentity.api.ExpressionBuilder.StringOperator
@DisplayName("StringOperator Default Methods Integration Tests")
public class StringOperatorDefaultMethodsIntegrationTest {

    // ==================== IfNotNull Default Methods Tests ====================

///
     /// 测试s startsWithIfNotNull with non-null value in WHERE clause.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with startsWithIfNotNull in WHERE clause")
    void shouldFilterWithStartsWithIfNotNullInWhere(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).startsWithIfNotNull("Alice")
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getName().startsWith("Alice"));
    }

///
     /// 测试s startsWithIfNotNull with null value in WHERE clause.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should skip filter when startsWithIfNotNull is null")
    void shouldSkipFilterWhenStartsWithIfNotNullIsNull(IntegrationTestContext context) {
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
     /// 测试s endsWithIfNotNull with non-null value in WHERE clause.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with endsWithIfNotNull in WHERE clause")
    void shouldFilterWithEndsWithIfNotNullInWhere(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getEmail).endsWithIfNotNull("@example.com")
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getEmail().endsWith("@example.com"));
    }

///
     /// 测试s endsWithIfNotNull with null value in WHERE clause.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should skip filter when endsWithIfNotNull is null")
    void shouldSkipFilterWhenEndsWithIfNotNullIsNull(IntegrationTestContext context) {
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
     /// 测试s containsIfNotNull with non-null value in WHERE clause.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with containsIfNotNull in WHERE clause")
    void shouldFilterWithContainsIfNotNullInWhere(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).containsIfNotNull("John")
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getName().contains("John"));
    }

///
     /// 测试s containsIfNotNull with null value in WHERE clause.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should skip filter when containsIfNotNull is null")
    void shouldSkipFilterWhenContainsIfNotNullIsNull(IntegrationTestContext context) {
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
     /// 测试s notStartsWithIfNotNull with non-null value in WHERE clause.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with notStartsWithIfNotNull in WHERE clause")
    void shouldFilterWithNotStartsWithIfNotNullInWhere(IntegrationTestContext context) {
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
     /// 测试s notStartsWithIfNotNull with null value in WHERE clause.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should skip filter when notStartsWithIfNotNull is null")
    void shouldSkipFilterWhenNotStartsWithIfNotNullIsNull(IntegrationTestContext context) {
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
     /// 测试s notEndsWithIfNotNull with non-null value in WHERE clause.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with notEndsWithIfNotNull in WHERE clause")
    void shouldFilterWithNotEndsWithIfNotNullInWhere(IntegrationTestContext context) {
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
     /// 测试s notEndsWithIfNotNull with null value in WHERE clause.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should skip filter when notEndsWithIfNotNull is null")
    void shouldSkipFilterWhenNotEndsWithIfNotNullIsNull(IntegrationTestContext context) {
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
     /// 测试s notContainsIfNotNull with non-null value in WHERE clause.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with notContainsIfNotNull in WHERE clause")
    void shouldFilterWithNotContainsIfNotNullInWhere(IntegrationTestContext context) {
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
     /// 测试s notContainsIfNotNull with null value in WHERE clause.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should skip filter when notContainsIfNotNull is null")
    void shouldSkipFilterWhenNotContainsIfNotNullIsNull(IntegrationTestContext context) {
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
     /// 测试s startsWithIfNotEmpty with non-empty value in WHERE clause.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with startsWithIfNotEmpty in WHERE clause")
    void shouldFilterWithStartsWithIfNotEmptyInWhere(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).startsWithIfNotEmpty("Alice")
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getName().startsWith("Alice"));
    }

///
     /// 测试s startsWithIfNotEmpty with empty value in WHERE clause.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should skip filter when startsWithIfNotEmpty is empty")
    void shouldSkipFilterWhenStartsWithIfNotEmptyIsEmpty(IntegrationTestContext context) {
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
     /// 测试s startsWithIfNotEmpty with null value in WHERE clause.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should skip filter when startsWithIfNotEmpty is null")
    void shouldSkipFilterWhenStartsWithIfNotEmptyIsNull(IntegrationTestContext context) {
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
     /// 测试s endsWithIfNotEmpty with non-empty value in WHERE clause.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with endsWithIfNotEmpty in WHERE clause")
    void shouldFilterWithEndsWithIfNotEmptyInWhere(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getEmail).endsWithIfNotEmpty("@example.com")
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getEmail().endsWith("@example.com"));
    }

///
     /// 测试s endsWithIfNotEmpty with empty value in WHERE clause.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should skip filter when endsWithIfNotEmpty is empty")
    void shouldSkipFilterWhenEndsWithIfNotEmptyIsEmpty(IntegrationTestContext context) {
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
     /// 测试s containsIfNotEmpty with non-empty value in WHERE clause.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with containsIfNotEmpty in WHERE clause")
    void shouldFilterWithContainsIfNotEmptyInWhere(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).containsIfNotEmpty("John")
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getName().contains("John"));
    }

///
     /// 测试s containsIfNotEmpty with empty value in WHERE clause.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should skip filter when containsIfNotEmpty is empty")
    void shouldSkipFilterWhenContainsIfNotEmptyIsEmpty(IntegrationTestContext context) {
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
     /// 测试s notStartsWithIfNotEmpty with non-empty value in WHERE clause.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with notStartsWithIfNotEmpty in WHERE clause")
    void shouldFilterWithNotStartsWithIfNotEmptyInWhere(IntegrationTestContext context) {
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
     /// 测试s notStartsWithIfNotEmpty with empty value in WHERE clause.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should skip filter when notStartsWithIfNotEmpty is empty")
    void shouldSkipFilterWhenNotStartsWithIfNotEmptyIsEmpty(IntegrationTestContext context) {
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
     /// 测试s notEndsWithIfNotEmpty with non-empty value in WHERE clause.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with notEndsWithIfNotEmpty in WHERE clause")
    void shouldFilterWithNotEndsWithIfNotEmptyInWhere(IntegrationTestContext context) {
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
     /// 测试s notEndsWithIfNotEmpty with empty value in WHERE clause.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should skip filter when notEndsWithIfNotEmpty is empty")
    void shouldSkipFilterWhenNotEndsWithIfNotEmptyIsEmpty(IntegrationTestContext context) {
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
     /// 测试s notContainsIfNotEmpty with non-empty value in WHERE clause.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with notContainsIfNotEmpty in WHERE clause")
    void shouldFilterWithNotContainsIfNotEmptyInWhere(IntegrationTestContext context) {
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
     /// 测试s notContainsIfNotEmpty with empty value in WHERE clause.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should skip filter when notContainsIfNotEmpty is empty")
    void shouldSkipFilterWhenNotContainsIfNotEmptyIsEmpty(IntegrationTestContext context) {
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
     /// 测试s combining multiple IfNotNull string conditions in WHERE clause.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should combine multiple IfNotNull conditions in WHERE clause")
    void shouldCombineMultipleIfNotNullConditionsInWhere(IntegrationTestContext context) {
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
     /// 测试s combining multiple IfNotEmpty string conditions in WHERE clause.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should combine multiple IfNotEmpty conditions in WHERE clause")
    void shouldCombineMultipleIfNotEmptyConditionsInWhere(IntegrationTestContext context) {
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
     /// 测试s combining IfNotNull and IfNotEmpty conditions.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should combine IfNotNull and IfNotEmpty conditions")
    void shouldCombineIfNotNullAndIfNotEmptyConditions(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).containsIfNotNull("John")
                .where(Employee::getEmail).endsWithIfNotEmpty("@example.com")
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e ->
                e.getName().contains("John") && e.getEmail().endsWith("@example.com"));
    }

///
     /// 测试s that null IfNotNull values are skipped in combined conditions.
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
     /// 测试s that empty IfNotEmpty values are skipped in combined conditions.
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

///
     /// 测试s count with IfNotNull string condition.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should count with IfNotNull string condition")
    void shouldCountWithIfNotNullStringCondition(IntegrationTestContext context) {
        // When
        long count = context.queryEmployees()
                .where(Employee::getName).startsWithIfNotNull("A")
                .count();

        // Then
        assertThat(count).isPositive();
    }

///
     /// 测试s count with IfNotEmpty string condition.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should count with IfNotEmpty string condition")
    void shouldCountWithIfNotEmptyStringCondition(IntegrationTestContext context) {
        // When
        long count = context.queryEmployees()
                .where(Employee::getEmail).endsWithIfNotEmpty("@example.com")
                .count();

        // Then
        assertThat(count).isPositive();
    }

///
     /// 测试s getFirst with IfNotNull string condition.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should getFirst with IfNotNull string condition")
    void shouldGetFirstWithIfNotNullStringCondition(IntegrationTestContext context) {
        // When
        Employee employee = context.queryEmployees()
                .where(Employee::getName).containsIfNotNull("Alice")
                .orderBy(Employee::getId).asc()
                .first();

        // Then
        assertThat(employee).isNotNull();
        assertThat(employee.getName()).contains("Alice");
    }

///
     /// 测试s exist with IfNotEmpty string condition.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should check exist with IfNotEmpty string condition")
    void shouldCheckExistWithIfNotEmptyStringCondition(IntegrationTestContext context) {
        // When
        boolean exists = context.queryEmployees()
                .where(Employee::getName).startsWithIfNotEmpty("Alice")
                .exists();

        // Then
        assertThat(exists).isTrue();
    }
}
