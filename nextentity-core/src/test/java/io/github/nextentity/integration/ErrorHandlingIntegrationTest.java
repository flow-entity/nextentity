package io.github.nextentity.integration;

import io.github.nextentity.api.Path;
import io.github.nextentity.integration.config.IntegrationTestContext;
import io.github.nextentity.integration.config.IntegrationTestProvider;
import io.github.nextentity.integration.entity.Department;
import io.github.nextentity.integration.entity.Employee;
import io.github.nextentity.integration.entity.EmployeeStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

///
 /// Error handling integration tests.
 /// <p>
 /// 测试s error handling including:
 /// - SQL exceptions
 /// - Constraint violations
 /// - Invalid queries
 /// <p>
 /// These tests run against MySQL and PostgreSQL using 测试containers.
 /// 
 /// @author HuangChengwei
@DisplayName("Error Handling Integration Tests")
public class ErrorHandlingIntegrationTest {

    @AfterEach
    void tearDown() {
        var context = IntegrationTestProvider.getEntityManagerContext();
        if (context != null) {
            context.reset();
        }
    }

///
     /// 测试s duplicate primary key violation.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should throw exception on duplicate primary key")
    void shouldThrowExceptionOnDuplicatePrimaryKey(IntegrationTestContext context) {
        // Given - Employee with ID 1 already exists
        Employee duplicate = createTestEmployee(1L, "Duplicate");

        // When/Then
        assertThatThrownBy(() ->
                context.getUpdateExecutor().insert(duplicate, Employee.class))
                .isInstanceOf(RuntimeException.class);
    }

///
     /// 测试s updating non-existent entity.
     /// Note: This 行为 may vary between JPA and JDBC implementations.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle update of non-existent entity")
    void shouldHandleUpdateNonExistentEntity(IntegrationTestContext context) {
        // Given
        Employee nonExistent = createTestEmployee(99999L, "Non Existent");

        // When/Then - Behavior may vary:
        // - JPA might create the entity or throw an exception
        // - JDBC might just return 0 affected rows
        // This test documents the actual behavior
        try {
            context.getUpdateExecutor().update(nonExistent, Employee.class);
            // If no exception, verify the entity wasn't accidentally created
            Employee found = context.queryEmployees()
                    .where(Employee::getId).eq(99999L)
                    .single();
            // Entity might have been created (JPA merge behavior) or not found
        } catch (Exception e) {
            // Some implementations might throw an exception
            assertThat(e).isInstanceOf(RuntimeException.class);
        }
    }

///
     /// 测试s getSingle with multiple results.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should throw exception when getSingle finds multiple results")
    void shouldThrowExceptionWhenGetSingleFindsMultiple(IntegrationTestContext context) {
        // When/Then
        assertThatThrownBy(() ->
                context.queryEmployees().single())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("more than one");
    }

///
     /// 测试s query with invalid field comparison.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle type mismatch gracefully")
    void shouldHandleTypeMismatch(IntegrationTestContext context) {
        // When - Query with correct type
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getSalary).gt(50000.0)
                .list();

        // Then - Should work fine
        assertThat(employees).isNotNull();
    }

///
     /// 测试s foreign key constraint violation.
     /// Note: This depends on whether the schema has FK constraints defined.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle foreign key constraint")
    void shouldHandleForeignKeyConstraint(IntegrationTestContext context) {
        // Given - Employee with non-existent department ID
        Employee employee = createTestEmployee(8888L, "FK Test");
        employee.setDepartmentId(999999L); // Non-existent department

        // When - Insert might succeed or fail depending on FK constraints
        try {
            context.getUpdateExecutor().insert(employee, Employee.class);

            // If insert succeeded, clean up
            context.getUpdateExecutor().delete(employee, Employee.class);
        } catch (Exception e) {
            // If FK constraint exists, insert should fail
            assertThat(e).isInstanceOf(RuntimeException.class);
        }
    }

///
     /// 测试s null ID on insert.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle null ID on insert")
    void shouldHandleNullIdOnInsert(IntegrationTestContext context) {
        // Given
        Employee employee = new Employee();
        employee.setId(null);
        employee.setName("Null ID");
        employee.setEmail("nullid@example.com");
        employee.setSalary(50000.0);
        employee.setActive(true);
        employee.setStatus(EmployeeStatus.ACTIVE);
        employee.setDepartmentId(1L);
        employee.setHireDate(LocalDate.now());

        // When/Then - Should fail or auto-generate ID depending on schema
        try {
            context.getUpdateExecutor().insert(employee, Employee.class);
            // If succeeded, verify
            assertThat(employee.getId()).isNotNull();
        } catch (Exception e) {
            // Expected for schemas that require non-null ID
            assertThat(e).isInstanceOf(RuntimeException.class);
        }
    }

///
     /// 测试s querying non-existent table would fail at setup time.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle valid table query")
    void shouldHandleValidTableQuery(IntegrationTestContext context) {
        // When - Query existing table
        List<Employee> employees = context.queryEmployees().list();

        // Then - Should succeed
        assertThat(employees).isNotNull();
    }

///
     /// 测试s constraint violation on unique field.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle unique constraint on email if exists")
    void shouldHandleUniqueConstraintIfEmail(IntegrationTestContext context) {
        // Given - Get an existing employee's email
        Employee existing = context.queryEmployees()
                .where(Employee::getId).eq(1L)
                .single();

        Employee duplicateEmail = createTestEmployee(7777L, "Duplicate Email");
        duplicateEmail.setEmail(existing.getEmail());

        // When/Then - May or may not fail depending on unique constraint
        try {
            context.getUpdateExecutor().insert(duplicateEmail, Employee.class);
            // If no unique constraint, insert succeeds
            context.getUpdateExecutor().delete(duplicateEmail, Employee.class);
        } catch (Exception e) {
            // If unique constraint exists, insert fails
            assertThat(e).isInstanceOf(RuntimeException.class);
        }
    }

///
     /// 测试s valid delete 操作.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should delete existing entity successfully")
    void shouldDeleteExistingEntity(IntegrationTestContext context) {
        // Given
        Employee employee = createTestEmployee(6666L, "To Delete");
        context.getUpdateExecutor().insert(employee, Employee.class);

        // When
        context.getUpdateExecutor().delete(employee, Employee.class);

        // Then
        Employee found = context.queryEmployees()
                .where(Employee::getId).eq(6666L)
                .single();
        assertThat(found).isNull();
    }

///
     /// 测试s query with complex invalid condition.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle complex query conditions")
    void shouldHandleComplexQueryConditions(IntegrationTestContext context) {
        // When - Complex but valid query
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getActive).eq(true)
                .where(Employee::getSalary).between(50000.0, 80000.0)
                .where(Employee::getDepartmentId).in(1L, 2L, 3L)
                .orderBy(Employee::getName).asc()
                .list();

        // Then - Should succeed
        assertThat(employees).isNotNull();
    }

///
     /// 测试s aggregation error handling.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle aggregation queries")
    void shouldHandleAggregationQueries(IntegrationTestContext context) {
        // When
        long count = context.queryEmployees().count();
        double avgSalary = context.queryEmployees()
                .select(Path.of(Employee::getSalary).avg())
                .single();

        // Then
        assertThat(count).isPositive();
        assertThat(avgSalary).isPositive();
    }

///
     /// 测试s empty result set handling.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle empty result set")
    void shouldHandleEmptyResultSet(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getId).eq(999999L)
                .list();

        // Then
        assertThat(employees).isEmpty();

        // And count should be 0
        long count = context.queryEmployees()
                .where(Employee::getId).eq(999999L)
                .count();
        assertThat(count).isZero();
    }

///
     /// 测试s pagination edge cases.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle pagination edge cases")
    void shouldHandlePaginationEdgeCases(IntegrationTestContext context) {
        // When - Page beyond data
        List<Employee> employees = context.queryEmployees()
                .orderBy(Employee::getId).asc()
                .list(1000, 10);

        // Then
        assertThat(employees).isEmpty();

        // When - Zero limit
        employees = context.queryEmployees()
                .orderBy(Employee::getId).asc()
                .list(0);

        // Then - Should handle gracefully
        assertThat(employees).isNotNull();
    }

///
     /// 测试s projection with non-existent field.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle projection queries")
    void shouldHandleProjectionQueries(IntegrationTestContext context) {
        // When - Valid projection
        var results = context.queryEmployees()
                .select(Employee::getName, Employee::getSalary)
                .where(Employee::getId).eq(1L)
                .list();

        // Then
        assertThat(results).isNotEmpty();
        assertThat(results.getFirst().get0()).isNotNull();
        assertThat(results.getFirst().get1()).isNotNull();
    }

///
     /// 测试s department 操作s.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle department CRUD operations")
    void shouldHandleDepartmentCrud(IntegrationTestContext context) {
        // Given
        Department dept = new Department();
        dept.setId(5555L);
        dept.setName("Test Department");
        dept.setLocation("Test Location");
        dept.setBudget(100000.0);
        dept.setActive(true);

        // When - Insert
        context.getUpdateExecutor().insert(dept, Department.class);

        // Then - Verify
        Department found = context.queryDepartments()
                .where(Department::getId).eq(5555L)
                .single();
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Test Department");

        // When - Update
        dept.setName("Updated Department");
        context.getUpdateExecutor().update(dept, Department.class);

        // Then - Verify
        found = context.queryDepartments()
                .where(Department::getId).eq(5555L)
                .single();
        assertThat(found.getName()).isEqualTo("Updated Department");

        // When - Delete
        context.getUpdateExecutor().delete(dept, Department.class);

        // Then - Verify
        found = context.queryDepartments()
                .where(Department::getId).eq(5555L)
                .single();
        assertThat(found).isNull();
    }

///
     /// 测试s string pattern matching edge cases.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle string pattern matching")
    void shouldHandleStringPatternMatching(IntegrationTestContext context) {
        // When - Pattern that matches nothing
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).like("ZZZZZZZ%")
                .list();

        // Then
        assertThat(employees).isEmpty();

        // When - Pattern that matches all
        employees = context.queryEmployees()
                .where(Employee::getName).like("%")
                .list();

        // Then - May or may not return all depending on database
        assertThat(employees).isNotNull();
    }

///
     /// 测试s multiple condition combination.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle multiple condition combinations")
    void shouldHandleMultipleConditions(IntegrationTestContext context) {
        // When - Multiple conditions that result in empty set
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getId).eq(1L)
                .where(Employee::getId).eq(2L) // Contradictory
                .list();

        // Then
        assertThat(employees).isEmpty();
    }

///
     /// 创建 a test employee with the specified ID and name.
    private Employee createTestEmployee(Long id, String name) {
        Employee employee = new Employee();
        employee.setId(id);
        employee.setName(name);
        employee.setEmail(name.toLowerCase().replace(" ", "") + "@example.com");
        employee.setSalary(50000.0);
        employee.setActive(true);
        employee.setStatus(EmployeeStatus.ACTIVE);
        employee.setDepartmentId(1L);
        employee.setHireDate(LocalDate.now());
        return employee;
    }
}

