package io.github.nextentity.integration;

import io.github.nextentity.api.Update;
import io.github.nextentity.core.Updaters;
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

    @Nested
    @DisplayName("Updaters Factory Tests")
    class UpdatersFactoryTest {

        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should create Update instance for Employee")
        void shouldCreateUpdateInstanceForEmployee(DbConfig config) {
            // When
            Update<Employee> update = Updaters.create(config.getUpdateExecutor(), Employee.class);

            // Then
            assertThat(update).isNotNull();
        }

        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should create Update instance for Department")
        void shouldCreateUpdateInstanceForDepartment(DbConfig config) {
            // When
            Update<Department> update = Updaters.create(config.getUpdateExecutor(), Department.class);

            // Then
            assertThat(update).isNotNull();
        }

        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Update toString should contain entity type name")
        void updateToStringShouldContainEntityTypeName(DbConfig config) {
            // When
            Update<Employee> update = Updaters.create(config.getUpdateExecutor(), Employee.class);

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
        void shouldInsertSingleEntityViaUpdateInterface(DbConfig config) {
            // Given
            Update<Employee> update = Updaters.create(config.getUpdateExecutor(), Employee.class);
            Employee employee = createTestEmployee(8001L, "Update Test");

            // When
            update.insert(employee);

            // Then
            Employee found = config.queryEmployees()
                    .where(Employee::getId).eq(8001L)
                    .getSingle();
            assertThat(found).isNotNull();
            assertThat(found.getName()).isEqualTo("Update Test");
        }

        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should update single entity via Update interface")
        void shouldUpdateSingleEntityViaUpdateInterface(DbConfig config) {
            // Given
            Update<Employee> update = Updaters.create(config.getUpdateExecutor(), Employee.class);
            Employee employee = createTestEmployee(8002L, "Before Update");
            update.insert(employee);

            // When
            employee.setName("After Update");
            Employee updated = update.update(employee);

            // Then
            assertThat(updated).isNotNull();
            assertThat(updated.getName()).isEqualTo("After Update");

            Employee found = config.queryEmployees()
                    .where(Employee::getId).eq(8002L)
                    .getSingle();
            assertThat(found.getName()).isEqualTo("After Update");
        }

        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should delete single entity via Update interface")
        void shouldDeleteSingleEntityViaUpdateInterface(DbConfig config) {
            // Given
            Update<Employee> update = Updaters.create(config.getUpdateExecutor(), Employee.class);
            Employee employee = createTestEmployee(8003L, "To Delete");
            update.insert(employee);

            // Verify exists
            Employee before = config.queryEmployees()
                    .where(Employee::getId).eq(8003L)
                    .getSingle();
            assertThat(before).isNotNull();

            // When
            update.delete(employee);

            // Then
            Employee after = config.queryEmployees()
                    .where(Employee::getId).eq(8003L)
                    .getSingle();
            assertThat(after).isNull();
        }

        @Disabled("BUG: PostgreSQL cannot determine data type for null parameters in updateNonNullColumn")
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should update non-null columns via updateNonNullColumn")
        void shouldUpdateNonNullColumnsViaUpdateNonNullColumn(DbConfig config) {
            // Given
            Update<Employee> update = Updaters.create(config.getUpdateExecutor(), Employee.class);
            Employee employee = createTestEmployee(8004L, "Patch Test");
            update.insert(employee);

            // When - update only name (set other fields to null)
            Employee patch = new Employee();
            patch.setId(8004L);
            patch.setName("Patched Name");
            // Other fields are null

            Employee updated = update.updateNonNullColumn(patch);

            // Then
            assertThat(updated).isNotNull();
            assertThat(updated.getName()).isEqualTo("Patched Name");

            // Verify other fields are not changed
            Employee found = config.queryEmployees()
                    .where(Employee::getId).eq(8004L)
                    .getSingle();
            assertThat(found.getSalary()).isEqualTo(50000.0); // Original value
        }
    }

    @Nested
    @DisplayName("Batch Operations Tests")
    class BatchOperationsTest {

        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should insert multiple entities via Update interface")
        void shouldInsertMultipleEntitiesViaUpdateInterface(DbConfig config) {
            // Given
            Update<Employee> update = Updaters.create(config.getUpdateExecutor(), Employee.class);
            List<Employee> employees = new ArrayList<>();
            employees.add(createTestEmployee(8011L, "Batch 1"));
            employees.add(createTestEmployee(8012L, "Batch 2"));
            employees.add(createTestEmployee(8013L, "Batch 3"));

            // When
            update.insert(employees);

            // Then
            List<Employee> found = config.queryEmployees()
                    .where(Employee::getId).in(8011L, 8012L, 8013L)
                    .orderBy(Employee::getId).asc()
                    .getList();
            assertThat(found).hasSize(3);
        }

        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should update multiple entities via Update interface")
        void shouldUpdateMultipleEntitiesViaUpdateInterface(DbConfig config) {
            // Given
            Update<Employee> update = Updaters.create(config.getUpdateExecutor(), Employee.class);
            List<Employee> employees = new ArrayList<>();
            employees.add(createTestEmployee(8021L, "Update Batch 1"));
            employees.add(createTestEmployee(8022L, "Update Batch 2"));
            update.insert(employees);

            // When
            for (Employee emp : employees) {
                emp.setName(emp.getName() + " Updated");
            }
            List<Employee> updated = update.update(employees);

            // Then
            assertThat(updated).hasSize(2);
            assertThat(updated.get(0).getName()).contains("Updated");
            assertThat(updated.get(1).getName()).contains("Updated");
        }

        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should delete multiple entities via Update interface")
        void shouldDeleteMultipleEntitiesViaUpdateInterface(DbConfig config) {
            // Given
            Update<Employee> update = Updaters.create(config.getUpdateExecutor(), Employee.class);
            List<Employee> employees = new ArrayList<>();
            employees.add(createTestEmployee(8031L, "Delete Batch 1"));
            employees.add(createTestEmployee(8032L, "Delete Batch 2"));
            update.insert(employees);

            // Verify exists
            List<Employee> before = config.queryEmployees()
                    .where(Employee::getId).in(8031L, 8032L)
                    .getList();
            assertThat(before).hasSize(2);

            // When
            update.delete(employees);

            // Then
            List<Employee> after = config.queryEmployees()
                    .where(Employee::getId).in(8031L, 8032L)
                    .getList();
            assertThat(after).isEmpty();
        }

        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should handle empty list for batch insert")
        void shouldHandleEmptyListForBatchInsert(DbConfig config) {
            // Given
            Update<Employee> update = Updaters.create(config.getUpdateExecutor(), Employee.class);
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
        void shouldInsertDepartmentViaUpdateInterface(DbConfig config) {
            // Given
            Update<Department> update = Updaters.create(config.getUpdateExecutor(), Department.class);
            Department dept = new Department(9001L, "Test Dept", "Location A", 100000.0, true);

            // When
            update.insert(dept);

            // Then
            Department found = config.queryDepartments()
                    .where(Department::getId).eq(9001L)
                    .getSingle();
            assertThat(found).isNotNull();
            assertThat(found.getName()).isEqualTo("Test Dept");
        }

        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should update department via Update interface")
        void shouldUpdateDepartmentViaUpdateInterface(DbConfig config) {
            // Given
            Update<Department> update = Updaters.create(config.getUpdateExecutor(), Department.class);
            Department dept = new Department(9002L, "Original Name", "Location B", 100000.0, true);
            update.insert(dept);

            // When
            dept.setName("Updated Name");
            dept.setBudget(200000.0);
            Department updated = update.update(dept);

            // Then
            assertThat(updated.getName()).isEqualTo("Updated Name");
            assertThat(updated.getBudget()).isEqualTo(200000.0);
        }

        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should delete department via Update interface")
        void shouldDeleteDepartmentViaUpdateInterface(DbConfig config) {
            // Given
            Update<Department> update = Updaters.create(config.getUpdateExecutor(), Department.class);
            Department dept = new Department(9003L, "To Delete", "Location C", 100000.0, true);
            update.insert(dept);

            // When
            update.delete(dept);

            // Then
            Department found = config.queryDepartments()
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