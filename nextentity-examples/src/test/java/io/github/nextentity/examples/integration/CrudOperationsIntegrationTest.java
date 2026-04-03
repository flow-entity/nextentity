package io.github.nextentity.examples.integration;

import io.github.nextentity.examples.entity.Department;
import io.github.nextentity.examples.entity.Employee;
import io.github.nextentity.examples.entity.EmployeeStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/// 员工 Repository 中 CRUD 操作的集成测试。
/// 测试插入、更新、删除操作并验证结果正确性。
@DisplayName("CRUD Operations Integration Tests")
class CrudOperationsIntegrationTest extends BaseIntegrationTest {

    // ==================== Insert Tests ====================

    @Nested
    @DisplayName("Insert Operations")
    class InsertTests {

        @Test
        @DisplayName("Should insert single employee and verify all fields")
        @Transactional
        void shouldInsertSingleEmployeeAndVerify() {
            // Given
            Long newId = TestDataFactory.getEmpIdStart() + 1000L;
            Long deptId = getFirstDepartmentId();
            Employee newEmployee = createTestEmployee(newId, "New Employee", "new@example.com",
                    new BigDecimal("60000.00"), deptId);

            // When
            employeeRepository.insert(newEmployee);

            // Then - verify the employee was inserted correctly
            Employee inserted = employeeRepository.findEmployeeById(newId);
            assertThat(inserted).isNotNull();
            assertThat(inserted.getId()).isEqualTo(newId);
            assertThat(inserted.getName()).isEqualTo("New Employee");
            assertThat(inserted.getEmail()).isEqualTo("new@example.com");
            assertThat(inserted.getSalary()).isEqualByComparingTo("60000.00");
            assertThat(inserted.getActive()).isTrue();
            assertThat(inserted.getStatus()).isEqualTo(EmployeeStatus.ACTIVE);
            assertThat(inserted.getDepartmentId()).isEqualTo(deptId);
        }

        @Test
        @DisplayName("Should insert multiple employees and verify count")
        @Transactional
        void shouldInsertMultipleEmployeesAndVerify() {
            // Given
            Long deptId = getFirstDepartmentId();
            List<Employee> newEmployees = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                Long id = TestDataFactory.getEmpIdStart() + 2000L + i;
                newEmployees.add(createTestEmployee(id, "Batch Employee " + i,
                        "batch" + i + "@example.com", new BigDecimal("50000.00"), deptId));
            }

            // When
            employeeRepository.insertAll(newEmployees);

            // Then - verify all employees were inserted
            List<Employee> inserted = employeeRepository.findEmployeesByIds(
                    newEmployees.stream().map(Employee::getId).toList());
            assertThat(inserted).hasSize(5);
            assertThat(inserted).extracting(Employee::getName)
                    .containsExactlyInAnyOrder("Batch Employee 0", "Batch Employee 1",
                            "Batch Employee 2", "Batch Employee 3", "Batch Employee 4");
        }

        @Test
        @DisplayName("Should insert employee with null email")
        @Transactional
        void shouldInsertEmployeeWithNullEmail() {
            // Given
            Long newId = TestDataFactory.getEmpIdStart() + 3000L;
            Long deptId = getFirstDepartmentId();
            Employee employee = TestDataFactory.createEmployee(newId, "No Email Employee", null,
                    new BigDecimal("55000.00"), true, EmployeeStatus.ACTIVE, deptId, LocalDate.now());

            // When
            employeeRepository.insert(employee);

            // Then
            Employee inserted = employeeRepository.findEmployeeById(newId);
            assertThat(inserted).isNotNull();
            assertThat(inserted.getEmail()).isNull();
            assertThat(inserted.getName()).isEqualTo("No Email Employee");
        }

        @Test
        @DisplayName("Should fail when inserting duplicate ID")
        @Transactional
        void shouldFailOnDuplicateId() {
            // Given - use existing employee ID
            Long existingId = getFirstEmployeeId();
            Long deptId = getFirstDepartmentId();
            Employee duplicate = createTestEmployee(existingId, "Duplicate", "dup@example.com",
                    new BigDecimal("50000.00"), deptId);

            // When/Then - should throw exception
            assertThatThrownBy(() -> employeeRepository.insert(duplicate))
                    .isInstanceOf(RuntimeException.class);
        }
    }

    // ==================== Update Tests ====================

    @Nested
    @DisplayName("Update Operations")
    class UpdateTests {

        @Test
        @DisplayName("Should update employee salary and verify change")
        @Transactional
        void shouldUpdateSalaryAndVerify() {
            // Given
            Long empId = getFirstEmployeeId();
            BigDecimal newSalary = new BigDecimal("99999.99");

            // When
            employeeRepository.updateEmployeeSalary(empId, newSalary);

            // Then
            Employee updated = employeeRepository.findEmployeeById(empId);
            assertThat(updated).isNotNull();
            assertThat(updated.getSalary()).isEqualByComparingTo(newSalary);
        }

        @Test
        @DisplayName("Should update multiple employees and verify all changes")
        @Transactional
        void shouldUpdateMultipleEmployeesAndVerify() {
            // Given
            Long deptId = getFirstDepartmentId();
            BigDecimal raisePercentage = new BigDecimal("0.10"); // 10% raise

            // Get employees in department before update and store their original salaries
            List<Employee> beforeUpdate = employeeRepository.findByDepartmentId(deptId);
            assertThat(beforeUpdate).isNotEmpty();

            // Store original salaries for comparison
            java.util.Map<Long, BigDecimal> originalSalaries = beforeUpdate.stream()
                    .collect(java.util.stream.Collectors.toMap(Employee::getId, Employee::getSalary));

            // When
            List<Employee> updated = employeeRepository.giveRaiseToDepartment(deptId, raisePercentage);

            // Then - verify all salaries increased by 10%
            for (Employee emp : updated) {
                Employee afterUpdate = employeeRepository.findEmployeeById(emp.getId());
                assertThat(afterUpdate).isNotNull();
                // Verify salary was increased from original
                BigDecimal originalSalary = originalSalaries.get(emp.getId());
                BigDecimal expectedSalary = originalSalary.multiply(BigDecimal.ONE.add(raisePercentage));
                assertThat(afterUpdate.getSalary()).isEqualByComparingTo(expectedSalary);
            }
        }

        @Test
        @DisplayName("Should update employee status and verify")
        @Transactional
        void shouldUpdateStatusAndVerify() {
            // Given
            Long empId = getFirstEmployeeId();
            Employee employee = employeeRepository.findEmployeeById(empId);
            assertThat(employee).isNotNull();
            EmployeeStatus originalStatus = employee.getStatus();

            // When
            employee.setStatus(EmployeeStatus.INACTIVE);
            employeeRepository.update(employee);

            // Then
            Employee updated = employeeRepository.findEmployeeById(empId);
            assertThat(updated.getStatus()).isEqualTo(EmployeeStatus.INACTIVE);
            assertThat(updated.getStatus()).isNotEqualTo(originalStatus);
        }

        @Test
        @DisplayName("Should update employee department and verify")
        @Transactional
        void shouldUpdateDepartmentAndVerify() {
            // Given
            Long empId = getFirstEmployeeId();
            Long newDeptId = testDepartments.size() > 1 ? testDepartments.get(1).getId() : TestDataFactory.getDeptIdStart() + 2;

            // When
            Employee employee = employeeRepository.findEmployeeById(empId);
            employee.setDepartmentId(newDeptId);
            employeeRepository.update(employee);

            // Then
            Employee updated = employeeRepository.findEmployeeById(empId);
            assertThat(updated.getDepartmentId()).isEqualTo(newDeptId);
        }
    }

    // ==================== Delete Tests ====================

    @Nested
    @DisplayName("Delete Operations")
    class DeleteTests {

        @Test
        @DisplayName("Should delete single employee and verify removal")
        @Transactional
        void shouldDeleteSingleEmployeeAndVerify() {
            // Given - create an employee to delete
            Long deptId = getFirstDepartmentId();
            Long deleteId = TestDataFactory.getEmpIdStart() + 4000L;
            Employee toDelete = createTestEmployee(deleteId, "To Delete", "delete@example.com",
                    new BigDecimal("50000.00"), deptId);
            employeeRepository.insert(toDelete);

            // Verify it exists
            assertThat(employeeRepository.findEmployeeById(deleteId)).isNotNull();

            // When
            employeeRepository.deleteEmployee(deleteId);

            // Then
            Employee afterDelete = employeeRepository.findEmployeeById(deleteId);
            assertThat(afterDelete).isNull();
        }

        @Test
        @DisplayName("Should delete multiple employees and verify all removed")
        @Transactional
        void shouldDeleteMultipleEmployeesAndVerify() {
            // Given - create employees to delete
            Long deptId = getFirstDepartmentId();
            List<Employee> toDelete = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                Long id = TestDataFactory.getEmpIdStart() + 5000L + i;
                Employee emp = createTestEmployee(id, "Delete " + i, "del" + i + "@example.com",
                        new BigDecimal("50000.00"), deptId);
                employeeRepository.insert(emp);
                toDelete.add(emp);
            }

            // When
            employeeRepository.deleteAll(toDelete);

            // Then - verify all deleted
            for (Employee emp : toDelete) {
                assertThat(employeeRepository.findEmployeeById(emp.getId())).isNull();
            }
        }

        @Test
        @DisplayName("Should delete employees by department and verify")
        @Transactional
        void shouldDeleteByDepartmentAndVerify() {
            // Given - create a department with employees
            Long deptId = TestDataFactory.getDeptIdStart() + 100L;
            Department dept = createTestDepartment(deptId, "ToDeleteDept", "Building X", 100000.0);
            departmentRepository.insert(dept);

            for (int i = 0; i < 3; i++) {
                Long empId = TestDataFactory.getEmpIdStart() + 6000L + i;
                Employee emp = createTestEmployee(empId, "DeptEmp " + i, "deptemp" + i + "@example.com",
                        new BigDecimal("50000.00"), deptId);
                employeeRepository.insert(emp);
            }

            // Verify employees exist
            assertThat(employeeRepository.countByDepartment(deptId)).isEqualTo(3);

            // When
            employeeRepository.deleteEmployeesByDepartment(deptId);

            // Then
            assertThat(employeeRepository.countByDepartment(deptId)).isEqualTo(0);
        }
    }

    // ==================== Query Tests ====================

    @Nested
    @DisplayName("Basic Query Operations")
    class QueryTests {

        @Test
        @DisplayName("Should find all employees and verify count")
        void shouldFindAllEmployeesAndVerifyCount() {
            // When
            List<Employee> employees = employeeRepository.findAllEmployees();

            // Then
            assertThat(employees).hasSize(getTestEmployeeCount());
            assertThat(employees).allMatch(e -> e.getId() != null);
            assertThat(employees).allMatch(e -> e.getName() != null);
        }

        @Test
        @DisplayName("Should find employee by ID and verify fields")
        void shouldFindEmployeeByIdAndVerify() {
            // Given
            Long empId = getFirstEmployeeId();

            // When
            Employee employee = employeeRepository.findEmployeeById(empId);

            // Then
            assertThat(employee).isNotNull();
            assertThat(employee.getId()).isEqualTo(empId);
            assertThat(employee.getName()).isNotEmpty();
            assertThat(employee.getEmail()).isNotEmpty();
        }

        @Test
        @DisplayName("Should find employees by multiple IDs")
        void shouldFindEmployeesByIds() {
            // Given
            List<Long> ids = testEmployees.stream().limit(3).map(Employee::getId).toList();

            // When
            List<Employee> employees = employeeRepository.findEmployeesByIds(ids);

            // Then
            assertThat(employees).hasSize(3);
            assertThat(employees).extracting(Employee::getId).containsExactlyElementsOf(ids);
        }

        @Test
        @DisplayName("Should find employee by email")
        void shouldFindEmployeeByEmail() {
            // Given
            String email = testEmployees.get(0).getEmail();

            // When
            Employee employee = employeeRepository.findEmployeeByEmail(email);

            // Then
            assertThat(employee).isNotNull();
            assertThat(employee.getEmail()).isEqualTo(email);
        }

        @Test
        @DisplayName("Should return null for non-existent employee")
        void shouldReturnNullForNonExistentEmployee() {
            // When
            Employee employee = employeeRepository.findEmployeeById(999999L);

            // Then
            assertThat(employee).isNull();
        }
    }

    // ==================== Association Tests ====================

    @Nested
    @DisplayName("Association Operations")
    class AssociationTests {

        @Test
        @DisplayName("Should check if department has employees")
        void shouldCheckDepartmentHasEmployees() {
            // Given
            Long deptId = getFirstDepartmentId();

            // When
            boolean hasEmployees = employeeRepository.hasEmployeesInDepartment(deptId);

            // Then
            assertThat(hasEmployees).isTrue();
        }

        @Test
        @DisplayName("Should return false for empty department")
        @Transactional
        void shouldReturnFalseForEmptyDepartment() {
            // Given - create a new empty department
            Long emptyDeptId = TestDataFactory.getDeptIdStart() + 200L;
            Department emptyDept = createTestDepartment(emptyDeptId, "EmptyDept", "Building Z", 50000.0);
            departmentRepository.insert(emptyDept);

            // When
            boolean hasEmployees = employeeRepository.hasEmployeesInDepartment(emptyDeptId);

            // Then
            assertThat(hasEmployees).isFalse();
        }

        @Test
        @DisplayName("Should safely delete empty department")
        @Transactional
        void shouldSafelyDeleteEmptyDepartment() {
            // Given - create empty department
            Long emptyDeptId = TestDataFactory.getDeptIdStart() + 300L;
            Department emptyDept = createTestDepartment(emptyDeptId, "SafeDeleteDept", "Building Y", 50000.0);
            departmentRepository.insert(emptyDept);

            // When
            boolean canDelete = employeeRepository.deleteDepartmentIfEmpty(emptyDeptId);

            // Then
            assertThat(canDelete).isTrue();
        }

        @Test
        @DisplayName("Should not allow delete of department with employees")
        void shouldNotAllowDeleteDepartmentWithEmployees() {
            // Given
            Long deptId = getFirstDepartmentId();

            // When
            boolean canDelete = employeeRepository.deleteDepartmentIfEmpty(deptId);

            // Then
            assertThat(canDelete).isFalse();
        }
    }

    // ==================== Count Tests ====================

    @Nested
    @DisplayName("Count Operations")
    class CountTests {

        @Test
        @DisplayName("Should count all employees")
        void shouldCountAllEmployees() {
            // When
            long count = employeeRepository.countAllEmployees();

            // Then
            assertThat(count).isEqualTo(getTestEmployeeCount());
        }

        @Test
        @DisplayName("Should count active employees")
        void shouldCountActiveEmployees() {
            // When
            long count = employeeRepository.countActiveEmployees();

            // Then - count should match expected active count
            int expectedActive = (int) testEmployees.stream().filter(Employee::getActive).count();
            assertThat(count).isEqualTo(expectedActive);
        }

        @Test
        @DisplayName("Should count employees by department")
        void shouldCountByDepartment() {
            // Given
            Long deptId = getFirstDepartmentId();
            int expectedCount = (int) testEmployees.stream()
                    .filter(e -> e.getDepartmentId().equals(deptId))
                    .count();

            // When
            long count = employeeRepository.countByDepartment(deptId);

            // Then
            assertThat(count).isEqualTo(expectedCount);
        }
    }
}