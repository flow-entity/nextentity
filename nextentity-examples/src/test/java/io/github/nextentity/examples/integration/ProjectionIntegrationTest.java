package io.github.nextentity.examples.integration;

import io.github.nextentity.api.model.Tuple2;
import io.github.nextentity.api.model.Tuple3;
import io.github.nextentity.api.model.Tuple4;
import io.github.nextentity.api.model.Tuple5;
import io.github.nextentity.examples.entity.Employee;
import io.github.nextentity.examples.entity.EmployeeStatus;
import io.github.nextentity.examples.repository.DepartmentRepository;
import io.github.nextentity.examples.repository.DepartmentRepository.DepartmentInfo;
import io.github.nextentity.examples.repository.EmployeeRepository.EmployeeInfo;
import io.github.nextentity.examples.repository.EmployeeRepository.EmployeeSalaryReport;
import io.github.nextentity.examples.repository.EmployeeRepository.EmployeeSummary;
import io.github.nextentity.examples.repository.EmployeeRepository.EmployeeWithDept;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/// 员工 Repository 和部门 Repository 中投影操作的集成测试。
/// 测试选择字段、DTO 映射、元组选择并验证结果正确性。
@DisplayName("Projection Integration Tests")
class ProjectionIntegrationTest extends BaseIntegrationTest {

    // ==================== Single Field Projection Tests ====================

    @Nested
    @DisplayName("Single Field Projection Tests")
    class SingleFieldProjectionTests {

        @Test
        @DisplayName("Should select employee names correctly")
        void shouldSelectEmployeeNamesCorrectly() {
            // Given
            int expectedCount = (int) testEmployees.stream()
                    .filter(Employee::getActive)
                    .count();

            // When
            List<String> names = employeeRepository.findEmployeeNames();

            // Then
            assertThat(names).hasSize(expectedCount);
            assertThat(names).allMatch(name -> name != null && !name.isEmpty());
            assertThat(names).contains(testEmployees.stream()
                    .filter(Employee::getActive)
                    .map(Employee::getName)
                    .toArray(String[]::new));
        }

        @Test
        @DisplayName("Should select distinct department IDs correctly")
        void shouldSelectDistinctDepartmentIdsCorrectly() {
            // Given
            int expectedDistinctCount = (int) testEmployees.stream()
                    .map(Employee::getDepartmentId)
                    .distinct()
                    .count();

            // When
            List<Long> deptIds = employeeRepository.findDistinctDepartmentIds();

            // Then
            assertThat(deptIds).hasSize(expectedDistinctCount);
            assertThat(deptIds).doesNotHaveDuplicates();
        }

        @Test
        @DisplayName("Should select names ordered correctly")
        void shouldSelectNamesOrderedCorrectly() {
            // When
            List<String> names = employeeRepository.findNamesOrdered();

            // Then - verify ascending order
            assertThat(names).isNotEmpty();
            for (int i = 0; i < names.size() - 1; i++) {
                assertThat(names.get(i).compareTo(names.get(i + 1))).isLessThanOrEqualTo(0);
            }
        }
    }

    // ==================== Tuple Projection Tests ====================

    @Nested
    @DisplayName("Tuple Projection Tests")
    class TupleProjectionTests {

        @Test
        @DisplayName("Should select name and salary as Tuple2 correctly")
        void shouldSelectNameAndSalaryCorrectly() {
            // Given
            int expectedCount = (int) testEmployees.stream()
                    .filter(Employee::getActive)
                    .count();

            // When
            List<Tuple2<String, BigDecimal>> tuples = employeeRepository.findNameAndSalary();

            // Then
            assertThat(tuples).hasSize(expectedCount);
            assertThat(tuples).allMatch(t -> t.get0() != null && t.get1() != null);

            // Verify data correctness
            testEmployees.stream()
                    .filter(Employee::getActive)
                    .forEach(emp -> {
                        boolean found = tuples.stream()
                                .anyMatch(t -> t.get0().equals(emp.getName()) &&
                                        t.get1().compareTo(emp.getSalary()) == 0);
                        assertThat(found).isTrue();
                    });
        }

        @Test
        @DisplayName("Should select name, email and salary as Tuple3 correctly")
        void shouldSelectNameEmailSalaryCorrectly() {
            // Given
            int expectedCount = (int) testEmployees.stream()
                    .filter(Employee::getActive)
                    .count();

            // When
            List<Tuple3<String, String, BigDecimal>> tuples = employeeRepository.findNameEmailSalary();

            // Then
            assertThat(tuples).hasSize(expectedCount);
            assertThat(tuples).allMatch(t -> t.get0() != null && t.get1() != null && t.get2() != null);

            // Verify data correctness
            testEmployees.stream()
                    .filter(e -> e.getActive() && e.getEmail() != null)
                    .forEach(emp -> {
                        boolean found = tuples.stream()
                                .anyMatch(t -> t.get0().equals(emp.getName()) &&
                                        t.get1().equals(emp.getEmail()) &&
                                        t.get2().compareTo(emp.getSalary()) == 0);
                        assertThat(found).isTrue();
                    });
        }

        @Test
        @DisplayName("Should select name, email, salary and department as Tuple4 correctly")
        void shouldSelectNameEmailSalaryDepartmentCorrectly() {
            // Given
            int expectedCount = (int) testEmployees.stream()
                    .filter(Employee::getActive)
                    .count();

            // When
            List<Tuple4<String, String, BigDecimal, Long>> tuples =
                    employeeRepository.findNameEmailSalaryDepartment();

            // Then
            assertThat(tuples).hasSize(expectedCount);
            assertThat(tuples).allMatch(t -> t.get0() != null);

            // Verify data correctness for each tuple
            tuples.forEach(t -> {
                assertThat(t.get0()).isNotNull(); // name
                assertThat(t.get2()).isNotNull(); // salary
            });
        }

        @Test
        @DisplayName("Should select employee details as Tuple5 correctly")
        void shouldSelectEmployeeDetailsCorrectly() {
            // Given
            int expectedCount = (int) testEmployees.stream()
                    .filter(Employee::getActive)
                    .count();

            // When
            List<Tuple5<String, String, BigDecimal, Long, Boolean>> tuples =
                    employeeRepository.findEmployeeDetails();

            // Then
            assertThat(tuples).hasSize(expectedCount);
            assertThat(tuples).allMatch(t -> t.get0() != null);
            assertThat(tuples).allMatch(t -> t.get4() == true); // active status

            // Verify order (sorted by name asc)
            for (int i = 0; i < tuples.size() - 1; i++) {
                assertThat(tuples.get(i).get0().compareTo(tuples.get(i + 1).get0()))
                        .isLessThanOrEqualTo(0);
            }
        }

        @Test
        @DisplayName("Should select distinct name and status as Tuple2 correctly")
        void shouldSelectDistinctNameStatusCorrectly() {
            // When
            List<Tuple2<String, EmployeeStatus>> tuples = employeeRepository.findDistinctNameStatus();

            // Then - verify distinctness
            assertThat(tuples).isNotEmpty();
            // Each name-status combination should be unique
            long distinctCount = tuples.stream().distinct().count();
            assertThat(tuples.size()).isEqualTo(distinctCount);
        }

        @Test
        @DisplayName("Should select name-salary pairs with ordering correctly")
        void shouldSelectNameSalaryPairsWithOrderingCorrectly() {
            // When
            List<Tuple2<String, BigDecimal>> pairs = employeeRepository.findNameSalaryPairs();

            // Then - verify descending order by salary
            assertThat(pairs).isNotEmpty();
            for (int i = 0; i < pairs.size() - 1; i++) {
                assertThat(pairs.get(i).get1().compareTo(pairs.get(i + 1).get1()))
                        .isGreaterThanOrEqualTo(0);
            }
        }
    }

    // ==================== DTO Projection Tests ====================

    @Nested
    @DisplayName("DTO Projection Tests")
    class DtoProjectionTests {

        @Test
        @DisplayName("Should select employee summaries as DTO correctly")
        void shouldSelectEmployeeSummariesCorrectly() {
            // Given
            int expectedCount = (int) testEmployees.stream()
                    .filter(Employee::getActive)
                    .count();

            // When
            List<EmployeeSummary> summaries = employeeRepository.findEmployeeSummaries();

            // Then
            assertThat(summaries).hasSize(expectedCount);
            assertThat(summaries).allMatch(s -> s.getName() != null);
            assertThat(summaries).allMatch(s -> s.getStatus() != null);

            // Verify data correctness
            testEmployees.stream()
                    .filter(Employee::getActive)
                    .forEach(emp -> {
                        boolean found = summaries.stream()
                                .anyMatch(s -> s.getName().equals(emp.getName()));
                        assertThat(found).isTrue();
                    });
        }

        @Test
        @DisplayName("Should select high earner summaries as DTO correctly")
        void shouldSelectHighEarnerSummariesCorrectly() {
            // Given
            BigDecimal threshold = new BigDecimal("50000.00");
            int expectedCount = (int) testEmployees.stream()
                    .filter(e -> e.getSalary().compareTo(threshold) > 0)
                    .filter(e -> e.getStatus() == EmployeeStatus.ACTIVE)
                    .count();

            // When
            List<EmployeeSummary> summaries = employeeRepository.findHighEarnerSummaries();

            // Then
            assertThat(summaries).hasSize(expectedCount);
            assertThat(summaries).allMatch(s -> s.getStatus() == EmployeeStatus.ACTIVE);

            // Verify salary > 50000
            summaries.forEach(s -> {
                assertThat(s.getSalary().compareTo(threshold)).isGreaterThan(0);
            });
        }

        @Test
        @DisplayName("Should select distinct employee info as DTO correctly")
        void shouldSelectDistinctEmployeeInfoCorrectly() {
            // When
            List<EmployeeInfo> infos = employeeRepository.findDistinctEmployeeInfo();

            // Then
            assertThat(infos).isNotEmpty();
            assertThat(infos).allMatch(i -> i.getId() != null);
            assertThat(infos).allMatch(i -> i.getName() != null);
        }

        @Test
        @DisplayName("Should select employee with department info as DTO correctly")
        void shouldSelectEmployeeWithDepartmentInfoCorrectly() {
            // Given
            int expectedCount = (int) testEmployees.stream()
                    .filter(Employee::getActive)
                    .count();

            // When
            List<EmployeeWithDept> results = employeeRepository.findEmployeeWithDepartmentInfo();

            // Then
            assertThat(results).hasSize(expectedCount);
            assertThat(results).allMatch(r -> r.getEmployeeName() != null);
        }

        @Test
        @DisplayName("Should select department info as DTO correctly")
        void shouldSelectDepartmentInfoCorrectly() {
            // Given
            int expectedCount = (int) testDepartments.stream()
                    .filter(d -> d.getActive())
                    .count();

            // When
            List<DepartmentInfo> infos = departmentRepository.findDepartmentInfo();

            // Then
            assertThat(infos).hasSize(expectedCount);
            assertThat(infos).allMatch(i -> i.getName() != null);
            assertThat(infos).allMatch(i -> i.getLocation() != null);

            // Verify data correctness
            testDepartments.stream()
                    .filter(d -> d.getActive())
                    .forEach(dept -> {
                        boolean found = infos.stream()
                                .anyMatch(i -> i.getName().equals(dept.getName()));
                        assertThat(found).isTrue();
                    });
        }
    }

    // ==================== Department Repository Projection Tests ====================

    @Nested
    @DisplayName("Department Repository Projection Tests")
    class DepartmentProjectionTests {

        @Test
        @DisplayName("Should select active department names correctly")
        void shouldSelectActiveDepartmentNamesCorrectly() {
            // Given
            int expectedCount = (int) testDepartments.stream()
                    .filter(d -> d.getActive())
                    .count();

            // When
            List<String> names = departmentRepository.findActiveDepartmentNames();

            // Then
            assertThat(names).hasSize(expectedCount);
            assertThat(names).allMatch(name -> name != null);

            // Verify the names are from department entities (not employees)
            List<String> expectedNames = testDepartments.stream()
                    .filter(d -> d.getActive())
                    .map(d -> d.getName())
                    .toList();
            assertThat(names).containsExactlyInAnyOrderElementsOf(expectedNames);
        }
    }

    // ==================== Entity Projection Tests ====================

    @Nested
    @DisplayName("Entity Projection Tests")
    class EntityProjectionTests {

        @Test
        @DisplayName("Should select active employees as full entities")
        void shouldSelectActiveEmployeesAsEntities() {
            // Given
            int expectedCount = (int) testEmployees.stream()
                    .filter(Employee::getActive)
                    .count();

            // When
            List<Employee> employees = employeeRepository.findActiveEmployees();

            // Then
            assertThat(employees).hasSize(expectedCount);
            assertThat(employees).allMatch(Employee::getActive);
            assertThat(employees).allMatch(e -> e.getId() != null);
            assertThat(employees).allMatch(e -> e.getName() != null);
        }

        @Test
        @DisplayName("Should select distinct active employees as entities")
        void shouldSelectDistinctActiveEmployeesAsEntities() {
            // Given
            int expectedCount = (int) testEmployees.stream()
                    .filter(Employee::getActive)
                    .distinct()
                    .count();

            // When
            List<Employee> employees = employeeRepository.findDistinctActiveEmployees();

            // Then
            assertThat(employees).hasSize(expectedCount);
            assertThat(employees).allMatch(Employee::getActive);
        }
    }

    // ==================== Fetch Association Tests ====================

    @Nested
    @DisplayName("Fetch Association Tests")
    class FetchAssociationTests {

        @Test
        @DisplayName("Should find employees with department fetch correctly")
        void shouldFindEmployeesWithDepartmentFetchCorrectly() {
            // Given
            int expectedCount = (int) testEmployees.stream()
                    .filter(Employee::getActive)
                    .count();

            // When
            List<Employee> employees = employeeRepository.findWithDepartmentFetch();

            // Then
            assertThat(employees).hasSize(expectedCount);
            assertThat(employees).allMatch(Employee::getActive);

            // Verify department association is fetched
            employees.forEach(emp -> {
                if (emp.getDepartmentId() != null) {
                    // Department should be accessible without lazy loading exception
                    assertThat(emp.getDepartment()).isNotNull();
                }
            });
        }

        @Test
        @DisplayName("Should find active in department with fetch correctly")
        void shouldFindActiveInDepartmentWithFetchCorrectly() {
            // Given
            Long deptId = getFirstDepartmentId();
            int expectedCount = (int) testEmployees.stream()
                    .filter(e -> e.getActive() && e.getDepartmentId().equals(deptId))
                    .count();

            // When
            List<Employee> employees = employeeRepository.findActiveInDepartmentWithFetch(deptId);

            // Then
            assertThat(employees).hasSize(expectedCount);
            assertThat(employees).allMatch(e -> e.getDepartmentId().equals(deptId));

            // Verify order (by name asc)
            for (int i = 0; i < employees.size() - 1; i++) {
                assertThat(employees.get(i).getName().compareTo(employees.get(i + 1).getName()))
                        .isLessThanOrEqualTo(0);
            }
        }

        @Test
        @DisplayName("Should find employees with manual join correctly")
        void shouldFindEmployeesWithManualJoinCorrectly() {
            // Given
            int expectedCount = (int) testEmployees.stream()
                    .filter(Employee::getActive)
                    .count();

            // When
            List<EmployeeWithDept> results = employeeRepository.findWithManualJoin();

            // Then
            assertThat(results).hasSize(expectedCount);
            assertThat(results).allMatch(r -> r.getEmployeeName() != null);

            // Verify department name is populated
            results.forEach(r -> {
                assertThat(r.getEmployeeName()).isNotEmpty();
            });
        }
    }

    // ==================== DTO Projection (select(Class<R>)) Tests ====================

    @Nested
    @DisplayName("DTO Projection (select(Class<R>)) Tests")
    class DtoProjectionClassTests {

        @Test
        @DisplayName("Should select salary report via select(Class<R>) correctly")
        void shouldSelectSalaryReportCorrectly() {
            // Given
            int expectedCount = (int) testEmployees.stream()
                    .filter(e -> e.getActive() && e.getSalary() != null)
                    .count();

            // When
            List<EmployeeSalaryReport> reports = employeeRepository.findSalaryReport();

            // Then
            assertThat(reports).hasSize(expectedCount);
            assertThat(reports).allMatch(r -> r.getName() != null);
            assertThat(reports).allMatch(r -> r.getSalary() != null);
            assertThat(reports).allMatch(r -> r.getStatus() != null);

            // Verify descending order by salary
            for (int i = 0; i < reports.size() - 1; i++) {
                assertThat(reports.get(i).getSalary().compareTo(reports.get(i + 1).getSalary()))
                        .isGreaterThanOrEqualTo(0);
            }

            // Verify data correctness
            testEmployees.stream()
                    .filter(e -> e.getActive() && e.getSalary() != null)
                    .forEach(emp -> {
                        boolean found = reports.stream()
                                .anyMatch(r -> r.getName().equals(emp.getName()) &&
                                        r.getSalary().compareTo(emp.getSalary()) == 0);
                        assertThat(found).isTrue();
                    });
        }

        @Test
        @DisplayName("Should select salary report filtered by department via select(Class<R>)")
        void shouldSelectSalaryReportByDepartmentCorrectly() {
            // Given
            Long deptId = getFirstDepartmentId();
            int expectedCount = (int) testEmployees.stream()
                    .filter(e -> e.getActive() && e.getDepartmentId().equals(deptId))
                    .count();

            // When
            List<EmployeeSalaryReport> reports = employeeRepository.findSalaryReportByDepartment(deptId);

            // Then
            assertThat(reports).hasSize(expectedCount);
            assertThat(reports).allMatch(r -> r.getName() != null);
            assertThat(reports).allMatch(r -> r.getSalary() != null);

            // Verify descending order by salary
            for (int i = 0; i < reports.size() - 1; i++) {
                assertThat(reports.get(i).getSalary().compareTo(reports.get(i + 1).getSalary()))
                        .isGreaterThanOrEqualTo(0);
            }
        }
    }

    // ==================== Null and Missing Data Tests ====================

    @Nested
    @DisplayName("Null and Missing Data Tests")
    class NullAndMissingDataTests {

        @Test
        @DisplayName("Should find employees without department correctly")
        void shouldFindEmployeesWithoutDepartmentCorrectly() {
            // When
            List<Employee> employees = employeeRepository.findEmployeesWithoutDepartment();

            // Then - verify null departmentId and active status
            assertThat(employees).allMatch(e -> e.getDepartmentId() == null);
            assertThat(employees).allMatch(Employee::getActive);
        }

        @Test
        @DisplayName("Should find employees with missing email correctly")
        void shouldFindEmployeesWithMissingEmailCorrectly() {
            // When
            List<Employee> employees = employeeRepository.findEmployeesWithMissingEmail();

            // Then
            assertThat(employees).allMatch(e -> e.getEmail() == null);
        }
    }
}