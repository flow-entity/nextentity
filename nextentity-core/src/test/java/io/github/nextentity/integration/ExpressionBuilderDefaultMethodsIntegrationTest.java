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
/// Integration tests for ExpressionBuilder interface default 方法.
/// <p>
/// 测试s default 方法 in:
/// - ExpressionBuilder.StringOperator: likeIfNotEmpty, substring(int)
/// - ExpressionBuilder.NumberOperator: addIfNotNull, subtractIfNotNull, multiplyIfNotNull, divideIfNotNull, modIfNotNull
/// <p>
/// These 方法 are tested through the query builder API where they are used
/// as fluent 操作s on path expressions.
/// <p>
/// These tests run against MySQL and PostgreSQL using 测试containers.
///
/// @author HuangChengwei
/// @see io.github.nextentity.api.ExpressionBuilder.StringOperator
/// @see io.github.nextentity.api.ExpressionBuilder.NumberOperator
@DisplayName("ExpressionBuilder Default Methods Integration Tests")
public class ExpressionBuilderDefaultMethodsIntegrationTest {

    // ==================== StringOperator.likeIfNotEmpty Tests ====================

    ///
    /// 测试s likeIfNotEmpty(String) with a non-empty value.
    /// This should execute the LIKE 操作.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should execute likeIfNotEmpty with non-empty value")
    void shouldExecuteLikeIfNotEmptyWithNonEmptyValue(IntegrationTestContext context) {
        // When - likeIfNotEmpty with non-empty value
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).likeIfNotEmpty("Ali%")
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getName().startsWith("Ali"));
    }

    ///
    /// 测试s likeIfNotEmpty(String) with an empty string.
    /// This should skip the LIKE 操作 and return all records.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should skip likeIfNotEmpty with empty value")
    void shouldSkipLikeIfNotEmptyWithEmptyValue(IntegrationTestContext context) {
        // Given - get total count
        long totalCount = context.queryEmployees().count();

        // When - likeIfNotEmpty with empty value (should be skipped)
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).likeIfNotEmpty("")
                .list();

        // Then - should return all records (filter was skipped)
        assertThat(employees).hasSize((int) totalCount);
    }

    ///
    /// 测试s likeIfNotEmpty(String) with null value.
    /// This should skip the LIKE 操作 and return all records.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should skip likeIfNotEmpty with null value")
    void shouldSkipLikeIfNotEmptyWithNullValue(IntegrationTestContext context) {
        // Given - get total count
        long totalCount = context.queryEmployees().count();

        // When - likeIfNotEmpty with null value (should be skipped)
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).likeIfNotEmpty(null)
                .list();

        // Then - should return all records (filter was skipped)
        assertThat(employees).hasSize((int) totalCount);
    }

    // ==================== StringOperator.substring(int) Tests ====================

    ///
    /// 测试s substring(int) with an offset.
    /// This tests the default 方法 substring(int) which calls substring(int, Integer.MAX_VALUE).
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should use substring with offset for filtering")
    void shouldUseSubstringWithOffsetForFiltering(IntegrationTestContext context) {
        // When - use substring(offset) combined with length comparison
        // substring(1) returns characters starting from position 1 (0-indexed)
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).substring(1).length().gt(2)
                .list(5);

        // Then - should find employees with names longer than 3 characters
        assertThat(employees).isNotEmpty();
    }

    ///
    /// 测试s substring(int) with a larger offset combined with like.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle substring with like")
    void shouldHandleSubstringWithLike(IntegrationTestContext context) {
        // When - use substring(2) to skip first 2 characters and check length
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).substring(2).length().gt(1)
                .list(5);

        // Then
        assertThat(employees).isNotEmpty();
    }

    // ==================== NumberOperator.addIfNotNull Tests ====================

    ///
    /// 测试s addIfNotNull(Number) with a non-null value.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should execute addIfNotNull with non-null value")
    void shouldExecuteAddIfNotNullWithNonNullValue(IntegrationTestContext context) {
        // When - addIfNotNull with non-null value
        // Find employees where (salary + 10000) > 90000, i.e., salary > 80000
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getSalary).addIfNotNull(10000.0).gt(90000.0)
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getSalary() > 80000.0);
    }

    ///
    /// 测试s addIfNotNull(Number) with null value.
    /// This should skip the addition and just compare the original value.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should skip addIfNotNull with null value")
    void shouldSkipAddIfNotNullWithNullValue(IntegrationTestContext context) {
        // When - addIfNotNull with null value (should be skipped)
        // Find employees where salary > 50000 directly (addition was skipped)
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getSalary).addIfNotNull(null).gt(50000.0)
                .list();

        // Then - should compare salary > 50000 directly
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getSalary() > 50000.0);
    }

    // ==================== NumberOperator.subtractIfNotNull Tests ====================

    ///
    /// 测试s subtractIfNotNull(Number) with a non-null value.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should execute subtractIfNotNull with non-null value")
    void shouldExecuteSubtractIfNotNullWithNonNullValue(IntegrationTestContext context) {
        // When - subtractIfNotNull with non-null value
        // Find employees where (salary - 10000) > 60000, i.e., salary > 70000
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getSalary).subtractIfNotNull(10000.0).gt(60000.0)
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getSalary() > 70000.0);
    }

    ///
    /// 测试s subtractIfNotNull(Number) with null value.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should skip subtractIfNotNull with null value")
    void shouldSkipSubtractIfNotNullWithNullValue(IntegrationTestContext context) {
        // When - subtractIfNotNull with null value (should be skipped)
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getSalary).subtractIfNotNull(null).gt(60000.0)
                .list();

        // Then - should compare salary > 60000 directly
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getSalary() > 60000.0);
    }

    // ==================== NumberOperator.multiplyIfNotNull Tests ====================

    ///
    /// 测试s multiplyIfNotNull(Number) with a non-null value.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should execute multiplyIfNotNull with non-null value")
    void shouldExecuteMultiplyIfNotNullWithNonNullValue(IntegrationTestContext context) {
        // When - multiplyIfNotNull with non-null value
        // Find employees where (salary * 0.1) > 7000, i.e., salary > 70000
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getSalary).multiplyIfNotNull(0.1).gt(7000.0)
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getSalary() > 70000.0);
    }

    ///
    /// 测试s multiplyIfNotNull(Number) with null value.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should skip multiplyIfNotNull with null value")
    void shouldSkipMultiplyIfNotNullWithNullValue(IntegrationTestContext context) {
        // When - multiplyIfNotNull with null value (should be skipped)
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getSalary).multiplyIfNotNull(null).gt(50000.0)
                .list();

        // Then - should compare salary > 50000 directly
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getSalary() > 50000.0);
    }

    // ==================== NumberOperator.divideIfNotNull Tests ====================

    ///
    /// 测试s divideIfNotNull(Number) with a non-null value.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should execute divideIfNotNull with non-null value")
    void shouldExecuteDivideIfNotNullWithNonNullValue(IntegrationTestContext context) {
        // When - divideIfNotNull with non-null value
        // Find employees where (salary / 1000) > 75, i.e., salary > 75000
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getSalary).divideIfNotNull(1000.0).gt(75.0)
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getSalary() > 75000.0);
    }

    ///
    /// 测试s divideIfNotNull(Number) with null value.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should skip divideIfNotNull with null value")
    void shouldSkipDivideIfNotNullWithNullValue(IntegrationTestContext context) {
        // When - divideIfNotNull with null value (should be skipped)
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getSalary).divideIfNotNull(null).gt(50000.0)
                .list();

        // Then - should compare salary > 50000 directly
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getSalary() > 50000.0);
    }

    // ==================== NumberOperator.modIfNotNull Tests ====================

    ///
    /// 测试s modIfNotNull(Number) with a non-null value.
    /// Note: This test is primarily for coverage of the default 方法.
    /// The actual SQL execution may vary by database.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should execute modIfNotNull with non-null value")
    void shouldExecuteModIfNotNullWithNonNullValue(IntegrationTestContext context) {
        // When - modIfNotNull with non-null value using integer division
        // Use mod with integer value to find employees with even salary thousands
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getId).modIfNotNull(2L).eq(0L)
                .list(5);

        // Then - should find employees with even IDs
        assertThat(employees).isNotEmpty();
    }

    ///
    /// 测试s modIfNotNull(Number) with null value.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should skip modIfNotNull with null value")
    void shouldSkipModIfNotNullWithNullValue(IntegrationTestContext context) {
        // When - modIfNotNull with null value (should be skipped)
        // With null, mod is skipped, so we compare id > 0
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getId).modIfNotNull(null).gt(0L)
                .list(5);

        // Then - should find employees with id > 0
        assertThat(employees).isNotEmpty();
    }

    // ==================== Chaining Tests ====================

    ///
    /// 测试s chaining multiple numeric 操作s with IfNotNull variants.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should chain multiple numeric operations with IfNotNull")
    void shouldChainMultipleNumericOperations(IntegrationTestContext context) {
        // When - chain addIfNotNull and subtractIfNotNull
        // (salary + 5000 - 5000) > 70000, i.e., salary > 70000
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getSalary)
                .addIfNotNull(5000.0)
                .subtractIfNotNull(5000.0)
                .gt(70000.0)
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getSalary() > 70000.0);
    }

    ///
    /// 测试s chaining with null values in the middle.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle null values in chained operations")
    void shouldHandleNullValuesInChainedOperations(IntegrationTestContext context) {
        // When - chain with null (skipped operation)
        // salary + null (skipped) + 10000 > 85000, i.e., salary + 10000 > 85000, salary > 75000
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getSalary)
                .addIfNotNull(null)  // skipped
                .addIfNotNull(10000.0)  // applied
                .gt(85000.0)
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getSalary() > 75000.0);
    }

    // ==================== Combined Operations ====================

    ///
    /// 测试s combining numeric 操作s with like predicate.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should combine numeric operations with like predicate")
    void shouldCombineNumericOperationsWithLikePredicate(IntegrationTestContext context) {
        // When - use numeric operations with name filter
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).like("A%")
                .where(Employee::getSalary).addIfNotNull(10000.0).gt(80000.0)
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getName().startsWith("A") && e.getSalary() > 70000.0);
    }

    ///
    /// 测试s all IfNotNull variants in a single complex query.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should use all numeric IfNotNull variants")
    void shouldUseAllNumericIfNotNullVariants(IntegrationTestContext context) {
        // When - use all numeric IfNotNull variants
        // This tests all 5 methods: add, subtract, multiply, divide, mod
        List<Employee> addEmployees = context.queryEmployees()
                .where(Employee::getSalary).addIfNotNull(1000.0).gt(60000.0).list();

        List<Employee> subtractEmployees = context.queryEmployees()
                .where(Employee::getSalary).subtractIfNotNull(1000.0).gt(60000.0).list();

        List<Employee> multiplyEmployees = context.queryEmployees()
                .where(Employee::getSalary).multiplyIfNotNull(0.5).gt(30000.0).list();

        List<Employee> divideEmployees = context.queryEmployees()
                .where(Employee::getSalary).divideIfNotNull(2.0).gt(30000.0).list();

        // Use ID for mod test since salary mod may have issues with some databases
        List<Employee> modEmployees = context.queryEmployees()
                .where(Employee::getId).modIfNotNull(2L).eq(0L).list();

        // Then - all should return results
        assertThat(addEmployees).isNotEmpty();
        assertThat(subtractEmployees).isNotEmpty();
        assertThat(multiplyEmployees).isNotEmpty();
        assertThat(divideEmployees).isNotEmpty();
        assertThat(modEmployees).isNotEmpty();
    }
}

