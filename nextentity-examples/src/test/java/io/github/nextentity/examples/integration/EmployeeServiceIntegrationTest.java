package io.github.nextentity.examples.integration;

import io.github.nextentity.examples.entity.Department;
import io.github.nextentity.examples.entity.Employee;
import io.github.nextentity.examples.entity.EmployeeStatus;
import io.github.nextentity.examples.service.EmployeeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for EmployeeService.
 * Tests transactional operations and verifies results correctness.
 */
@DisplayName("Employee Service Integration Tests")
class EmployeeServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private EmployeeService employeeService;

    // ==================== Create with Transaction Tests ====================

    @Nested
    @DisplayName("Create with Transaction Tests")
    class CreateWithTransactionTests {

        @Test
        @DisplayName("Should create employee with department in single transaction")
        @Transactional
        void shouldCreateEmployeeWithDepartmentInSingleTransaction() {
            // Given
            Long newDeptId = TestDataFactory.getDeptIdStart() + 500L;
            Long newEmpId = TestDataFactory.getEmpIdStart() + 5000L;

            Department dept = new Department(newDeptId, "New Department", "Building X", 100000.0, true);
            Employee emp = new Employee(newEmpId, "New Employee", "new@transaction.com",
                    new BigDecimal("60000.00"), true, EmployeeStatus.ACTIVE, newDeptId, LocalDate.now());

            // When
            employeeService.createEmployeeWithDepartment(emp, dept);

            // Then - verify both were created
            Department createdDept = departmentRepository.findDepartmentById(newDeptId);
            Employee createdEmp = employeeRepository.findEmployeeById(newEmpId);

            assertThat(createdDept).isNotNull();
            assertThat(createdDept.getName()).isEqualTo("New Department");

            assertThat(createdEmp).isNotNull();
            assertThat(createdEmp.getName()).isEqualTo("New Employee");
            assertThat(createdEmp.getDepartmentId()).isEqualTo(newDeptId);
        }
    }

    // ==================== Transfer Tests ====================

    @Nested
    @DisplayName("Transfer Tests")
    class TransferTests {

        @Test
        @DisplayName("Should transfer employee between departments")
        @Transactional
        void shouldTransferEmployeeBetweenDepartments() {
            // Given
            Long empId = getFirstEmployeeId();
            Long oldDeptId = employeeRepository.findEmployeeById(empId).getDepartmentId();
            Long newDeptId = testDepartments.size() > 1 ? testDepartments.get(1).getId() : TestDataFactory.getDeptIdStart() + 2;

            // When
            employeeService.transferEmployee(empId, newDeptId);

            // Then
            Employee transferred = employeeRepository.findEmployeeById(empId);
            assertThat(transferred.getDepartmentId()).isEqualTo(newDeptId);
            assertThat(transferred.getDepartmentId()).isNotEqualTo(oldDeptId);
        }

        @Test
        @DisplayName("Should transfer multiple employees")
        @Transactional
        void shouldTransferMultipleEmployees() {
            // Given
            List<Long> empIds = testEmployees.stream().limit(3).map(Employee::getId).toList();
            Long newDeptId = testDepartments.size() > 1 ? testDepartments.get(1).getId() : TestDataFactory.getDeptIdStart() + 2;

            // When
            employeeService.transferEmployees(empIds, newDeptId);

            // Then - verify all transferred
            for (Long empId : empIds) {
                Employee emp = employeeRepository.findEmployeeById(empId);
                assertThat(emp.getDepartmentId()).isEqualTo(newDeptId);
            }
        }
    }

    // ==================== Batch Operations Tests ====================

    @Nested
    @DisplayName("Batch Operations Tests")
    class BatchOperationsTests {

        @Test
        @DisplayName("Should hire multiple employees in batch")
        @Transactional
        void shouldHireMultipleEmployeesInBatch() {
            // Given
            Long deptId = getFirstDepartmentId();
            List<Employee> newEmployees = List.of(
                    createTestEmployee(TestDataFactory.getEmpIdStart() + 6000L, "Hire1", "hire1@example.com",
                            new BigDecimal("50000.00"), deptId),
                    createTestEmployee(TestDataFactory.getEmpIdStart() + 6001L, "Hire2", "hire2@example.com",
                            new BigDecimal("55000.00"), deptId),
                    createTestEmployee(TestDataFactory.getEmpIdStart() + 6002L, "Hire3", "hire3@example.com",
                            new BigDecimal("52000.00"), deptId)
            );

            // When
            employeeService.hireEmployees(newEmployees, deptId);

            // Then - verify all were hired with correct attributes
            for (Employee emp : newEmployees) {
                Employee hired = employeeRepository.findEmployeeById(emp.getId());
                assertThat(hired).isNotNull();
                assertThat(hired.getDepartmentId()).isEqualTo(deptId);
                assertThat(hired.getStatus()).isEqualTo(EmployeeStatus.ACTIVE);
                assertThat(hired.getActive()).isTrue();
            }
        }

        @Test
        @DisplayName("Should terminate employee and update status")
        @Transactional
        void shouldTerminateEmployeeAndStatus() {
            // Given
            Long empId = getFirstEmployeeId();

            // When
            employeeService.terminateEmployee(empId);

            // Then
            Employee terminated = employeeRepository.findEmployeeById(empId);
            assertThat(terminated.getStatus()).isEqualTo(EmployeeStatus.TERMINATED);
            assertThat(terminated.getActive()).isFalse();
        }
    }

    // ==================== Raise Tests ====================

    @Nested
    @DisplayName("Raise Tests")
    class RaiseTests {

        @Test
        @DisplayName("Should give department raise correctly")
        @Transactional
        void shouldGiveDepartmentRaiseCorrectly() {
            // Given
            Long deptId = getFirstDepartmentId();
            BigDecimal raisePercentage = new BigDecimal("0.15"); // 15%

            // Get employees before raise and store original salaries
            List<Employee> beforeRaise = employeeRepository.findByDepartmentId(deptId);
            assertThat(beforeRaise).isNotEmpty();

            // Create map of employee ID to original salary
            java.util.Map<Long, BigDecimal> originalSalaries = beforeRaise.stream()
                    .filter(e -> e.getActive() && e.getSalary() != null)
                    .collect(java.util.stream.Collectors.toMap(Employee::getId, Employee::getSalary));

            // When
            employeeService.giveDepartmentRaise(deptId, raisePercentage);

            // Then - verify all active employees got raise
            List<Employee> afterRaise = employeeRepository.findByDepartmentId(deptId);
            for (Employee after : afterRaise) {
                BigDecimal originalSalary = originalSalaries.get(after.getId());
                if (originalSalary != null) {
                    BigDecimal expectedRaise = originalSalary.multiply(BigDecimal.ONE.add(raisePercentage));
                    assertThat(after.getSalary()).isEqualByComparingTo(expectedRaise);
                }
            }
        }
    }

    // ==================== Read-only Transaction Tests ====================

    @Nested
    @DisplayName("Read-only Transaction Tests")
    class ReadOnlyTransactionTests {

        @Test
        @DisplayName("Should find active employees with read-only transaction")
        void shouldFindActiveEmployeesWithReadOnlyTransaction() {
            // When
            List<Employee> employees = employeeService.findActiveEmployees();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(Employee::getActive);

            // Verify order (name asc)
            for (int i = 0; i < employees.size() - 1; i++) {
                assertThat(employees.get(i).getName().compareTo(employees.get(i + 1).getName()))
                        .isLessThanOrEqualTo(0);
            }
        }

        @Test
        @DisplayName("Should find employees by department with read-only transaction")
        void shouldFindEmployeesByDepartmentWithReadOnlyTransaction() {
            // Given
            Long deptId = getFirstDepartmentId();
            int expectedCount = (int) testEmployees.stream()
                    .filter(e -> e.getDepartmentId().equals(deptId))
                    .count();

            // When
            List<Employee> employees = employeeService.findByDepartment(deptId);

            // Then
            assertThat(employees).hasSize(expectedCount);
            assertThat(employees).allMatch(e -> e.getDepartmentId().equals(deptId));

            // Verify order (name asc)
            for (int i = 0; i < employees.size() - 1; i++) {
                assertThat(employees.get(i).getName().compareTo(employees.get(i + 1).getName()))
                        .isLessThanOrEqualTo(0);
            }
        }
    }
}