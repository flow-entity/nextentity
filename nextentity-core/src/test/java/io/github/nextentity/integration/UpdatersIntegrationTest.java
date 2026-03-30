package io.github.nextentity.integration;

import io.github.nextentity.api.Update;
import io.github.nextentity.core.Updaters;
import io.github.nextentity.integration.config.IntegrationTestContext;
import io.github.nextentity.integration.config.IntegrationTestProvider;
import io.github.nextentity.integration.entity.Department;
import io.github.nextentity.integration.entity.Employee;
import io.github.nextentity.integration.entity.EmployeeStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for Updaters factory class.
 * <p>
 * Tests the Updaters factory and Update interface including:
 * - Creating Update instances
 * - Insert operations
 * - Update operations
 * - Delete operations
 * - Batch operations
 *
 * @author HuangChengwei
 */
@DisplayName("Updaters Integration Tests")
public class UpdatersIntegrationTest {

    @AfterEach
    void tearDown() {
        var context = IntegrationTestProvider.getEntityManagerContext();
        if (context != null) {
            context.reset();
        }
    }

    @Nested
    @DisplayName("Updaters Factory Tests")
    class UpdatersFactoryTest {

        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should create Update instance for Employee")
        void shouldCreateUpdateInstanceForEmployee(IntegrationTestContext context) {
            // When
            Update<Employee> update = Updaters.create(context.getUpdateExecutor(), Employee.class);

            // Then
            assertThat(update).isNotNull();
        }

        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should create Update instance for Department")
        void shouldCreateUpdateInstanceForDepartment(IntegrationTestContext context) {
            // When
            Update<Department> update = Updaters.create(context.getUpdateExecutor(), Department.class);

            // Then
            assertThat(update).isNotNull();
        }

        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Update toString should contain entity type name")
        void updateToStringShouldContainEntityTypeName(IntegrationTestContext context) {
            // When
            Update<Employee> update = Updaters.create(context.getUpdateExecutor(), Employee.class);

            // Then
            String str = update.toString();
            assertThat(str).contains("Employee");
        }
    }

    @Nested
    @DisplayName("Single Entity Operations Tests")
    class SingleEntityOperationsTest {

        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should insert single entity via Update interface")
        void shouldInsertSingleEntityViaUpdateInterface(IntegrationTestContext context) {
            // Given
            Update<Employee> update = Updaters.create(context.getUpdateExecutor(), Employee.class);
            Employee employee = createTestEmployee(8001L, "Update Test");

            // When
            update.insert(employee);

            // Then
            Employee found = context.queryEmployees()
                    .where(Employee::getId).eq(8001L)
                    .getSingle();
            assertThat(found).isNotNull();
            assertThat(found.getName()).isEqualTo("Update Test");
        }

        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should update single entity via Update interface")
        void shouldUpdateSingleEntityViaUpdateInterface(IntegrationTestContext context) {
            // Given
            Update<Employee> update = Updaters.create(context.getUpdateExecutor(), Employee.class);
            Employee employee = createTestEmployee(8002L, "Before Update");
            update.insert(employee);

            // When
            employee.setName("After Update");
            update.update(employee);
            Employee updated = context.queryEmployees()
                    .where(Employee::getId).eq(employee.getId())
                    .getSingle();

            // Then
            assertThat(updated).isNotNull();
            assertThat(updated.getName()).isEqualTo("After Update");

            Employee found = context.queryEmployees()
                    .where(Employee::getId).eq(8002L)
                    .getSingle();
            assertThat(found.getName()).isEqualTo("After Update");
        }

        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should delete single entity via Update interface")
        void shouldDeleteSingleEntityViaUpdateInterface(IntegrationTestContext context) {
            // Given
            Update<Employee> update = Updaters.create(context.getUpdateExecutor(), Employee.class);
            Employee employee = createTestEmployee(8003L, "To Delete");
            update.insert(employee);

            // Verify exists
            Employee before = context.queryEmployees()
                    .where(Employee::getId).eq(8003L)
                    .getSingle();
            assertThat(before).isNotNull();

            // When
            update.delete(employee);

            // Then
            Employee after = context.queryEmployees()
                    .where(Employee::getId).eq(8003L)
                    .getSingle();
            assertThat(after).isNull();
        }

    }

    @Nested
    @DisplayName("Batch Operations Tests")
    class BatchOperationsTest {

        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should insert multiple entities via Update interface")
        void shouldInsertMultipleEntitiesViaUpdateInterface(IntegrationTestContext context) {
            // Given
            Update<Employee> update = Updaters.create(context.getUpdateExecutor(), Employee.class);
            List<Employee> employees = new ArrayList<>();
            employees.add(createTestEmployee(8011L, "Batch 1"));
            employees.add(createTestEmployee(8012L, "Batch 2"));
            employees.add(createTestEmployee(8013L, "Batch 3"));

            // When
            update.insert(employees);

            // Then
            List<Employee> found = context.queryEmployees()
                    .where(Employee::getId).in(8011L, 8012L, 8013L)
                    .orderBy(Employee::getId).asc()
                    .getList();
            assertThat(found).hasSize(3);
        }

        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should update multiple entities via Update interface")
        void shouldUpdateMultipleEntitiesViaUpdateInterface(IntegrationTestContext context) {
            // Given
            Update<Employee> update = Updaters.create(context.getUpdateExecutor(), Employee.class);
            List<Employee> employees = new ArrayList<>();
            employees.add(createTestEmployee(8021L, "Update Batch 1"));
            employees.add(createTestEmployee(8022L, "Update Batch 2"));
            update.insert(employees);

            // When
            for (Employee emp : employees) {
                emp.setName(emp.getName() + " Updated");
            }
            update.update(employees);
            List<Employee> updated = context.queryEmployees()
                    .where(Employee::getId).in(8021L, 8022L)
                    .getList();

            // Then
            assertThat(updated).hasSize(2);
            assertThat(updated.get(0).getName()).contains("Updated");
            assertThat(updated.get(1).getName()).contains("Updated");
        }

        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should delete multiple entities via Update interface")
        void shouldDeleteMultipleEntitiesViaUpdateInterface(IntegrationTestContext context) {
            // Given
            Update<Employee> update = Updaters.create(context.getUpdateExecutor(), Employee.class);
            List<Employee> employees = new ArrayList<>();
            employees.add(createTestEmployee(8031L, "Delete Batch 1"));
            employees.add(createTestEmployee(8032L, "Delete Batch 2"));
            update.insert(employees);

            // Verify exists
            List<Employee> before = context.queryEmployees()
                    .where(Employee::getId).in(8031L, 8032L)
                    .getList();
            assertThat(before).hasSize(2);

            // When
            update.delete(employees);

            // Then
            List<Employee> after = context.queryEmployees()
                    .where(Employee::getId).in(8031L, 8032L)
                    .getList();
            assertThat(after).isEmpty();
        }

        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should handle empty list for batch insert")
        void shouldHandleEmptyListForBatchInsert(IntegrationTestContext context) {
            // Given
            Update<Employee> update = Updaters.create(context.getUpdateExecutor(), Employee.class);
            List<Employee> emptyList = new ArrayList<>();

            // When/Then - should not throw exception
            update.insert(emptyList);
        }
    }

    @Nested
    @DisplayName("Department Operations Tests")
    class DepartmentOperationsTest {

        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should insert department via Update interface")
        void shouldInsertDepartmentViaUpdateInterface(IntegrationTestContext context) {
            // Given
            Update<Department> update = Updaters.create(context.getUpdateExecutor(), Department.class);
            Department dept = new Department(9001L, "Test Dept", "Location A", 100000.0, true);

            // When
            update.insert(dept);

            // Then
            Department found = context.queryDepartments()
                    .where(Department::getId).eq(9001L)
                    .getSingle();
            assertThat(found).isNotNull();
            assertThat(found.getName()).isEqualTo("Test Dept");
        }

        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should update department via Update interface")
        void shouldUpdateDepartmentViaUpdateInterface(IntegrationTestContext context) {
            // Given
            Update<Department> update = Updaters.create(context.getUpdateExecutor(), Department.class);
            Department dept = new Department(9002L, "Original Name", "Location B", 100000.0, true);
            update.insert(dept);

            // When
            dept.setName("Updated Name");
            dept.setBudget(200000.0);
            update.update(dept);
            Department updated = context.queryDepartments()
                    .where(Department::getId).eq(dept.getId())
                    .getSingle();

            // Then
            assertThat(updated.getName()).isEqualTo("Updated Name");
            assertThat(updated.getBudget()).isEqualTo(200000.0);
        }

        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should delete department via Update interface")
        void shouldDeleteDepartmentViaUpdateInterface(IntegrationTestContext context) {
            // Given
            Update<Department> update = Updaters.create(context.getUpdateExecutor(), Department.class);
            Department dept = new Department(9003L, "To Delete", "Location C", 100000.0, true);
            update.insert(dept);

            // When
            update.delete(dept);

            // Then
            Department found = context.queryDepartments()
                    .where(Department::getId).eq(9003L)
                    .getSingle();
            assertThat(found).isNull();
        }
    }

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