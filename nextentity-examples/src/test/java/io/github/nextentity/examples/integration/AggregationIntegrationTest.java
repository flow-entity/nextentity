package io.github.nextentity.examples.integration;

import io.github.nextentity.api.model.Tuple2;
import io.github.nextentity.api.model.Tuple6;
import io.github.nextentity.examples.entity.Employee;
import io.github.nextentity.examples.entity.EmployeeStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

/// 员工 Repository 中聚合操作的集成测试。
/// 测试计数、求和、平均值、最小值、最大值、分组操作并验证结果正确性。
@DisplayName("Aggregation Integration Tests")
class AggregationIntegrationTest extends BaseIntegrationTest {

    // ==================== Count Tests ====================

    @Nested
    @DisplayName("Count Operations")
    class CountTests {

        @Test
        @DisplayName("Should count all employees correctly")
        void shouldCountAllEmployeesCorrectly() {
            // Given
            int expectedCount = getTestEmployeeCount();

            // When
            long count = employeeRepository.countAllEmployees();

            // Then
            assertThat(count).isEqualTo(expectedCount);
        }

        @Test
        @DisplayName("Should count active employees correctly")
        void shouldCountActiveEmployeesCorrectly() {
            // Given
            int expectedCount = (int) testEmployees.stream()
                    .filter(Employee::getActive)
                    .count();

            // When
            long count = employeeRepository.countActiveEmployees();

            // Then
            assertThat(count).isEqualTo(expectedCount);
        }

        @Test
        @DisplayName("Should count employees by department correctly")
        void shouldCountByDepartmentCorrectly() {
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

        @Test
        @DisplayName("Should count distinct departments correctly")
        void shouldCountDistinctDepartmentsCorrectly() {
            // Given
            int expectedCount = (int) testEmployees.stream()
                    .map(Employee::getDepartmentId)
                    .distinct()
                    .count();

            // When
            long count = employeeRepository.countDistinctDepartments();

            // Then
            assertThat(count).isEqualTo(expectedCount);
        }
    }

    // ==================== Sum Tests ====================

    @Nested
    @DisplayName("Sum Operations")
    class SumTests {

        @Test
        @DisplayName("Should calculate total salary correctly")
        void shouldCalculateTotalSalaryCorrectly() {
            // Given
            BigDecimal expectedSum = testEmployees.stream()
                    .map(Employee::getSalary)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // When
            BigDecimal totalSalary = employeeRepository.calculateTotalSalary();

            // Then
            assertThat(totalSalary).isEqualByComparingTo(expectedSum);
        }

        @Test
        @DisplayName("Should calculate active employees total salary correctly")
        void shouldCalculateActiveTotalSalaryCorrectly() {
            // Given
            BigDecimal expectedSum = testEmployees.stream()
                    .filter(e -> e.getSalary() != null && e.getActive())
                    .map(Employee::getSalary)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // When
            BigDecimal totalSalary = employeeRepository.calculateActiveTotalSalary();

            // Then
            assertThat(totalSalary).isEqualByComparingTo(expectedSum);
        }
    }

    // ==================== Average Tests ====================

    @Nested
    @DisplayName("Average Operations")
    class AverageTests {

        @Test
        @DisplayName("Should calculate average salary correctly")
        void shouldCalculateAverageSalaryCorrectly() {
            // Given - calculate expected average
            List<Employee> activeWithSalary = testEmployees.stream()
                    .filter(e -> e.getSalary() != null && e.getActive())
                    .toList();
            double expectedAvg = activeWithSalary.stream()
                    .mapToDouble(e -> e.getSalary().doubleValue())
                    .average()
                    .orElse(0.0);

            // When
            Double avgSalary = employeeRepository.calculateAverageSalary();

            // Then
            assertThat(avgSalary).isNotNull();
            assertThat(avgSalary).isCloseTo(expectedAvg, within(0.01));
        }

        @Test
        @DisplayName("Should calculate average salary by department correctly")
        void shouldCalculateAverageSalaryByDepartmentCorrectly() {
            // Given
            Long deptId = getFirstDepartmentId();
            List<Employee> deptEmployees = testEmployees.stream()
                    .filter(e -> e.getSalary() != null && e.getDepartmentId().equals(deptId))
                    .toList();
            double expectedAvg = deptEmployees.stream()
                    .mapToDouble(e -> e.getSalary().doubleValue())
                    .average()
                    .orElse(0.0);

            // When
            Double avgSalary = employeeRepository.calculateAverageSalaryByDepartment(deptId);

            // Then
            assertThat(avgSalary).isNotNull();
            assertThat(avgSalary).isCloseTo(expectedAvg, within(0.01));
        }
    }

    // ==================== Min/Max Tests ====================

    @Nested
    @DisplayName("Min/Max Operations")
    class MinMaxTests {

        @Test
        @DisplayName("Should find max salary correctly")
        void shouldFindMaxSalaryCorrectly() {
            // Given
            BigDecimal expectedMax = testEmployees.stream()
                    .filter(e -> e.getSalary() != null && e.getActive())
                    .map(Employee::getSalary)
                    .max(BigDecimal::compareTo)
                    .orElse(BigDecimal.ZERO);

            // When
            BigDecimal maxSalary = employeeRepository.findMaxSalary();

            // Then
            assertThat(maxSalary).isEqualByComparingTo(expectedMax);
        }

        @Test
        @DisplayName("Should find min salary correctly")
        void shouldFindMinSalaryCorrectly() {
            // Given
            BigDecimal expectedMin = testEmployees.stream()
                    .filter(e -> e.getSalary() != null && e.getActive())
                    .map(Employee::getSalary)
                    .min(BigDecimal::compareTo)
                    .orElse(BigDecimal.ZERO);

            // When
            BigDecimal minSalary = employeeRepository.findMinSalary();

            // Then
            assertThat(minSalary).isEqualByComparingTo(expectedMin);
        }

        @Test
        @DisplayName("Should find highest paid employee correctly")
        void shouldFindHighestPaidEmployeeCorrectly() {
            // Given
            Employee expectedHighest = testEmployees.stream()
                    .filter(e -> e.getSalary() != null && e.getActive())
                    .max(Comparator.comparing(Employee::getSalary))
                    .orElse(null);

            // When
            Employee highestPaid = employeeRepository.findHighestPaidEmployee();

            // Then
            assertThat(highestPaid).isNotNull();
            assertThat(expectedHighest).isNotNull();
            assertThat(highestPaid.getSalary()).isEqualByComparingTo(expectedHighest.getSalary());
        }

        @Test
        @DisplayName("Should find lowest paid employee correctly")
        void shouldFindLowestPaidEmployeeCorrectly() {
            // Given
            Employee expectedLowest = testEmployees.stream()
                    .filter(e -> e.getSalary() != null && e.getActive())
                    .min(Comparator.comparing(Employee::getSalary))
                    .orElse(null);

            // When
            Employee lowestPaid = employeeRepository.findLowestPaidEmployee();

            // Then
            assertThat(lowestPaid).isNotNull();
            assertThat(expectedLowest).isNotNull();
            assertThat(lowestPaid.getSalary()).isEqualByComparingTo(expectedLowest.getSalary());
        }
    }

    // ==================== Group By Tests ====================

    @Nested
    @DisplayName("Group By Operations")
    class GroupByTests {

        @Test
        @DisplayName("Should group employees by department correctly")
        void shouldGroupByDepartmentCorrectly() {
            // Given - expected grouping
            Map<Long, List<Employee>> expectedGroups = testEmployees.stream()
                    .filter(Employee::getActive)
                    .collect(Collectors.groupingBy(Employee::getDepartmentId));

            // When
            Map<Long, List<Employee>> groups = employeeRepository.groupByDepartment();

            // Then
            assertThat(groups).hasSize(expectedGroups.size());
            for (Map.Entry<Long, List<Employee>> entry : expectedGroups.entrySet()) {
                assertThat(groups.get(entry.getKey())).hasSize(entry.getValue().size());
            }
        }

        @Test
        @DisplayName("Should count employees by status correctly")
        void shouldCountByStatusCorrectly() {
            // Given
            Map<EmployeeStatus, Long> expectedCounts = testEmployees.stream()
                    .filter(e -> e.getStatus() != null)
                    .collect(Collectors.groupingBy(
                            Employee::getStatus,
                            Collectors.counting()
                    ));

            // When
            Map<EmployeeStatus, Long> counts = employeeRepository.countByStatus();

            // Then
            assertThat(counts).hasSize(expectedCounts.size());
            for (Map.Entry<EmployeeStatus, Long> entry : expectedCounts.entrySet()) {
                assertThat(counts.get(entry.getKey())).isEqualTo(entry.getValue());
            }
        }

        @Test
        @DisplayName("Should calculate salary statistics by department correctly")
        void shouldCalculateSalaryStatsByDepartmentCorrectly() {
            // When
            List<Tuple6<Long, Long, BigDecimal, Double, BigDecimal, BigDecimal>> stats =
                    employeeRepository.salaryStatsByDepartment();

            // Then - verify each department's statistics
            assertThat(stats).isNotEmpty();

            for (Tuple6<Long, Long, BigDecimal, Double, BigDecimal, BigDecimal> stat : stats) {
                Long deptId = stat.get0();
                Long count = stat.get1();
                BigDecimal sum = stat.get2();
                Double avg = stat.get3();
                BigDecimal max = stat.get4();
                BigDecimal min = stat.get5();

                // Verify against expected values
                List<Employee> deptEmployees = testEmployees.stream()
                        .filter(e -> e.getActive() && e.getSalary() != null && e.getDepartmentId().equals(deptId))
                        .toList();

                if (!deptEmployees.isEmpty()) {
                    assertThat(count).isEqualTo(deptEmployees.size());

                    BigDecimal expectedSum = deptEmployees.stream()
                            .map(Employee::getSalary)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    assertThat(sum).isEqualByComparingTo(expectedSum);

                    double expectedAvg = deptEmployees.stream()
                            .mapToDouble(e -> e.getSalary().doubleValue())
                            .average()
                            .orElse(0.0);
                    assertThat(avg).isCloseTo(expectedAvg, within(0.01));

                    BigDecimal expectedMax = deptEmployees.stream()
                            .map(Employee::getSalary)
                            .max(BigDecimal::compareTo)
                            .orElse(BigDecimal.ZERO);
                    assertThat(max).isEqualByComparingTo(expectedMax);

                    BigDecimal expectedMin = deptEmployees.stream()
                            .map(Employee::getSalary)
                            .min(BigDecimal::compareTo)
                            .orElse(BigDecimal.ZERO);
                    assertThat(min).isEqualByComparingTo(expectedMin);
                }
            }
        }
    }

    // ==================== Tuple Result Tests ====================

    @Nested
    @DisplayName("Tuple Result Tests")
    class TupleResultTests {

        @Test
        @DisplayName("Should find name-salary pairs correctly")
        void shouldFindNameSalaryPairsCorrectly() {
            // Given
            int expectedCount = (int) testEmployees.stream()
                    .filter(Employee::getActive)
                    .count();

            // When
            List<Tuple2<String, BigDecimal>> pairs = employeeRepository.findNameSalaryPairs();

            // Then
            assertThat(pairs).hasSize(expectedCount);
            assertThat(pairs).allMatch(t -> t.get0() != null && t.get1() != null);

            // Verify order (sorted by salary desc)
            for (int i = 0; i < pairs.size() - 1; i++) {
                assertThat(pairs.get(i).get1().compareTo(pairs.get(i + 1).get1())).isGreaterThanOrEqualTo(0);
            }
        }

        @Test
        @DisplayName("Should find name and salary correctly")
        void shouldFindNameAndSalaryCorrectly() {
            // Given
            int expectedCount = (int) testEmployees.stream()
                    .filter(Employee::getActive)
                    .count();

            // When
            List<Tuple2<String, BigDecimal>> results = employeeRepository.findNameAndSalary();

            // Then
            assertThat(results).hasSize(expectedCount);
            assertThat(results).allMatch(t -> t.get0() != null);
        }
    }

    // ==================== Exist Tests ====================

    @Nested
    @DisplayName("Exist Operations")
    class ExistTests {

        @Test
        @DisplayName("Should check if active employees exist")
        void shouldCheckActiveEmployeesExist() {
            // When
            boolean exists = employeeRepository.hasActiveEmployees();

            // Then
            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("Should check if employee exists by email")
        void shouldCheckEmployeeExistsByEmail() {
            // Given
            String email = testEmployees.getFirst().getEmail();

            // When
            boolean exists = employeeRepository.existsByEmail(email);

            // Then
            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("Should return false for non-existent email")
        void shouldReturnFalseForNonExistentEmail() {
            // Given
            String email = "nonexistent@example.com";

            // When
            boolean exists = employeeRepository.existsByEmail(email);

            // Then
            assertThat(exists).isFalse();
        }
    }

    // ==================== First Result Tests ====================

    @Nested
    @DisplayName("First Result Tests")
    class FirstResultTests {

        @Test
        @DisplayName("Should get first active employee as Optional")
        void shouldGetFirstActiveEmployee() {
            // When
            var firstOpt = employeeRepository.findFirstActive();

            // Then
            assertThat(firstOpt).isPresent();
            assertThat(firstOpt.get().getActive()).isTrue();
        }

        @Test
        @DisplayName("Should get highest paid employee")
        void shouldGetHighestPaid() {
            // When
            Employee highest = employeeRepository.findHighestPaid();

            // Then
            assertThat(highest).isNotNull();
            assertThat(highest.getActive()).isTrue();

            // Verify it's actually the highest
            BigDecimal highestSalary = highest.getSalary();
            testEmployees.stream()
                    .filter(e -> e.getActive() && e.getSalary() != null)
                    .forEach(e -> assertThat(e.getSalary().compareTo(highestSalary)).isLessThanOrEqualTo(0));
        }
    }

    // ==================== Statistics Report Tests ====================

    @Nested
    @DisplayName("Statistics Report Tests")
    class StatisticsReportTests {

        @Test
        @DisplayName("Should generate department report correctly")
        void shouldGenerateDepartmentReportCorrectly() {
            // Given
            Map<Long, List<Employee>> expectedReport = testEmployees.stream()
                    .filter(Employee::getActive)
                    .collect(Collectors.groupingBy(Employee::getDepartmentId));

            // When
            List<Map.Entry<Long, List<Employee>>> report = employeeRepository.generateDepartmentReport();

            // Then
            assertThat(report).hasSize(expectedReport.size());
            assertThat(report).allMatch(entry -> entry.getValue().stream().allMatch(Employee::getActive));
        }

        @Test
        @DisplayName("Should find employees due for review correctly")
        void shouldFindEmployeesDueForReviewCorrectly() {
            // Given - employees hired > 1 year ago, active, ACTIVE status, salary < 50000
            LocalDate oneYearAgo = LocalDate.now().minusYears(1);
            int expectedCount = (int) testEmployees.stream()
                    .filter(Employee::getActive)
                    .filter(e -> e.getStatus() == EmployeeStatus.ACTIVE)
                    .filter(e -> e.getHireDate() != null && e.getHireDate().isBefore(oneYearAgo) || e.getHireDate().isEqual(oneYearAgo))
                    .filter(e -> e.getSalary() != null && e.getSalary().compareTo(new BigDecimal("50000.00")) < 0)
                    .count();

            // When
            List<Employee> employees = employeeRepository.findEmployeesDueForReview();

            // Then - verify all conditions
            assertThat(employees).hasSize(expectedCount);
            assertThat(employees).allMatch(Employee::getActive);
            assertThat(employees).allMatch(e -> e.getStatus() == EmployeeStatus.ACTIVE);
            assertThat(employees).allMatch(e -> e.getSalary().compareTo(new BigDecimal("50000.00")) < 0);
        }
    }
}
