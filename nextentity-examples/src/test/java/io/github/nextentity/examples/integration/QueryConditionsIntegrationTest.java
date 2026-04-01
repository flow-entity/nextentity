package io.github.nextentity.examples.integration;

import io.github.nextentity.examples.entity.Employee;
import io.github.nextentity.examples.entity.EmployeeStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for query conditions in EmployeeRepository.
 * Tests all comparison operators (eq, ne, gt, lt, between, in, like, etc.)
 * and verifies results correctness.
 */
@DisplayName("Query Conditions Integration Tests")
class QueryConditionsIntegrationTest extends BaseIntegrationTest {

    // ==================== Equality Tests ====================

    @Nested
    @DisplayName("Equality Conditions")
    class EqualityTests {

        @Test
        @DisplayName("Should find employees by status using eq")
        void shouldFindByStatus() {
            // Given
            EmployeeStatus status = EmployeeStatus.ACTIVE;
            int expectedCount = (int) testEmployees.stream()
                    .filter(e -> e.getStatus() == status)
                    .count();

            // When
            List<Employee> employees = employeeRepository.findByStatus(status);

            // Then
            assertThat(employees).hasSize(expectedCount);
            assertThat(employees).allMatch(e -> e.getStatus() == status);
        }

        @Test
        @DisplayName("Should find employees not terminated using ne")
        void shouldFindNotTerminated() {
            // Given
            int expectedCount = (int) testEmployees.stream()
                    .filter(e -> e.getStatus() != EmployeeStatus.TERMINATED)
                    .count();

            // When
            List<Employee> employees = employeeRepository.findNotTerminated();

            // Then
            assertThat(employees).hasSize(expectedCount);
            assertThat(employees).allMatch(e -> e.getStatus() != EmployeeStatus.TERMINATED);
        }

        @Test
        @DisplayName("Should find employees by status if present (non-null)")
        void shouldFindByStatusIfPresent() {
            // Given
            EmployeeStatus status = EmployeeStatus.ACTIVE;
            int expectedCount = (int) testEmployees.stream()
                    .filter(e -> e.getStatus() == status)
                    .count();

            // When
            List<Employee> employees = employeeRepository.findByStatusIfPresent(status);

            // Then
            assertThat(employees).hasSize(expectedCount);
            assertThat(employees).allMatch(e -> e.getStatus() == status);
        }

        @Test
        @DisplayName("Should return all employees when status is null")
        void shouldReturnAllWhenStatusNull() {
            // When
            List<Employee> employees = employeeRepository.findByStatusIfPresent(null);

            // Then - should return all employees since condition is not applied
            assertThat(employees).hasSize(getTestEmployeeCount());
        }
    }

    // ==================== Numeric Comparison Tests ====================

    @Nested
    @DisplayName("Numeric Comparison Conditions")
    class NumericComparisonTests {

        @Test
        @DisplayName("Should find employees with salary greater than threshold")
        void shouldFindBySalaryGreaterThan() {
            // Given
            BigDecimal threshold = new BigDecimal("70000.00");

            // When
            List<Employee> employees = employeeRepository.findBySalaryGreaterThan(threshold);

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> e.getSalary().compareTo(threshold) > 0);
        }

        @Test
        @DisplayName("Should find employees with salary greater or equal")
        void shouldFindBySalaryGreaterOrEqual() {
            // Given
            BigDecimal threshold = new BigDecimal("60000.00");

            // When
            List<Employee> employees = employeeRepository.findBySalaryGreaterOrEqual(threshold);

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> e.getSalary().compareTo(threshold) >= 0);
        }

        @Test
        @DisplayName("Should find employees with salary less than threshold")
        void shouldFindBySalaryLessThan() {
            // Given
            BigDecimal threshold = new BigDecimal("60000.00");

            // When
            List<Employee> employees = employeeRepository.findBySalaryLessThan(threshold);

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> e.getSalary().compareTo(threshold) < 0);
        }

        @Test
        @DisplayName("Should find employees with salary less or equal")
        void shouldFindBySalaryLessOrEqual() {
            // Given
            BigDecimal threshold = new BigDecimal("65000.00");

            // When
            List<Employee> employees = employeeRepository.findBySalaryLessOrEqual(threshold);

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> e.getSalary().compareTo(threshold) <= 0);
        }

        @Test
        @DisplayName("Should find employees with salary between range")
        void shouldFindBySalaryBetween() {
            // Given
            BigDecimal min = new BigDecimal("55000.00");
            BigDecimal max = new BigDecimal("75000.00");

            // When
            List<Employee> employees = employeeRepository.findBySalaryBetween(min, max);

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e ->
                    e.getSalary().compareTo(min) >= 0 && e.getSalary().compareTo(max) <= 0);
        }

        @Test
        @DisplayName("Should find employees with salary not between range")
        void shouldFindBySalaryNotBetween() {
            // Given
            BigDecimal min = new BigDecimal("55000.00");
            BigDecimal max = new BigDecimal("75000.00");

            // When
            List<Employee> employees = employeeRepository.findBySalaryNotBetween(min, max);

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e ->
                    e.getSalary().compareTo(min) < 0 || e.getSalary().compareTo(max) > 0);
        }
    }

    // ==================== IN Condition Tests ====================

    @Nested
    @DisplayName("IN Condition Tests")
    class InConditionTests {

        @Test
        @DisplayName("Should find employees by IDs using IN with varargs")
        void shouldFindByIdsVarargs() {
            // Given
            Long[] ids = testEmployees.stream().limit(3).map(Employee::getId).toArray(Long[]::new);

            // When
            List<Employee> employees = employeeRepository.findByIds(ids);

            // Then
            assertThat(employees).hasSize(3);
            assertThat(employees).extracting(Employee::getId).containsExactlyInAnyOrder(ids);
        }

        @Test
        @DisplayName("Should find employees by IDs using IN with collection")
        void shouldFindByIdsCollection() {
            // Given
            List<Long> ids = testEmployees.stream().limit(4).map(Employee::getId).toList();

            // When
            List<Employee> employees = employeeRepository.findByIdsCollection(ids);

            // Then
            assertThat(employees).hasSize(4);
            assertThat(employees).extracting(Employee::getId).containsExactlyElementsOf(ids);
        }

        @Test
        @DisplayName("Should find employees by statuses using IN")
        void shouldFindByStatuses() {
            // Given
            EmployeeStatus[] statuses = {EmployeeStatus.ACTIVE, EmployeeStatus.ON_LEAVE};
            int expectedCount = (int) testEmployees.stream()
                    .filter(e -> e.getStatus() == EmployeeStatus.ACTIVE || e.getStatus() == EmployeeStatus.ON_LEAVE)
                    .count();

            // When
            List<Employee> employees = employeeRepository.findByStatuses(statuses);

            // Then
            assertThat(employees).hasSize(expectedCount);
            assertThat(employees).allMatch(e ->
                    e.getStatus() == EmployeeStatus.ACTIVE || e.getStatus() == EmployeeStatus.ON_LEAVE);
        }

        @Test
        @DisplayName("Should find employees by status NOT IN")
        void shouldFindByStatusNotIn() {
            // Given
            EmployeeStatus[] excludedStatuses = {EmployeeStatus.TERMINATED, EmployeeStatus.INACTIVE};
            int expectedCount = (int) testEmployees.stream()
                    .filter(e -> e.getStatus() != EmployeeStatus.TERMINATED && e.getStatus() != EmployeeStatus.INACTIVE)
                    .count();

            // When
            List<Employee> employees = employeeRepository.findByStatusNotIn(excludedStatuses);

            // Then
            assertThat(employees).hasSize(expectedCount);
            assertThat(employees).allMatch(e ->
                    e.getStatus() != EmployeeStatus.TERMINATED && e.getStatus() != EmployeeStatus.INACTIVE);
        }
    }

    // ==================== Null Check Tests ====================

    @Nested
    @DisplayName("Null Check Tests")
    class NullCheckTests {

        @Test
        @DisplayName("Should find employees without email (isNull)")
        @Transactional
        void shouldFindWithoutEmail() {
            // Given - set an employee's email to null
            Employee emp = testEmployees.get(0);
            emp.setEmail(null);
            employeeRepository.update(emp);

            // When
            List<Employee> employees = employeeRepository.findWithoutEmail();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> e.getEmail() == null);
        }

        @Test
        @DisplayName("Should find employees with email (isNotNull)")
        void shouldFindWithEmail() {
            // When
            List<Employee> employees = employeeRepository.findWithEmail();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> e.getEmail() != null);
        }
    }

    // ==================== Date Comparison Tests ====================

    @Nested
    @DisplayName("Date Comparison Tests")
    class DateComparisonTests {

        @Test
        @DisplayName("Should find employees hired after date")
        void shouldFindHiredAfter() {
            // Given
            LocalDate date = LocalDate.of(2021, 1, 1);
            int expectedCount = (int) testEmployees.stream()
                    .filter(e -> e.getHireDate() != null && e.getHireDate().isAfter(date))
                    .count();

            // When
            List<Employee> employees = employeeRepository.findHiredAfter(date);

            // Then
            assertThat(employees).hasSize(expectedCount);
            assertThat(employees).allMatch(e -> e.getHireDate().isAfter(date));
        }

        @Test
        @DisplayName("Should find employees hired between dates")
        void shouldFindHiredBetween() {
            // Given
            LocalDate start = LocalDate.of(2020, 1, 1);
            LocalDate end = LocalDate.of(2022, 12, 31);
            int expectedCount = (int) testEmployees.stream()
                    .filter(e -> e.getHireDate() != null &&
                            !e.getHireDate().isBefore(start) && !e.getHireDate().isAfter(end))
                    .count();

            // When
            List<Employee> employees = employeeRepository.findHiredBetween(start, end);

            // Then
            assertThat(employees).hasSize(expectedCount);
            assertThat(employees).allMatch(e ->
                    !e.getHireDate().isBefore(start) && !e.getHireDate().isAfter(end));
        }
    }

    // ==================== String Operation Tests ====================

    @Nested
    @DisplayName("String Operation Tests")
    class StringOperationTests {

        @Test
        @DisplayName("Should find employees by name using LIKE")
        void shouldFindByNameLike() {
            // Given - use a pattern that matches existing employees
            String pattern = "%son%";

            // When
            List<Employee> employees = employeeRepository.findByNameLike(pattern);

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> e.getName().toLowerCase().contains("son"));
        }

        @Test
        @DisplayName("Should find employees by name starting with")
        void shouldFindByNameStartingWith() {
            // Given
            String prefix = "A";

            // When
            List<Employee> employees = employeeRepository.findByNameStartingWith(prefix);

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> e.getName().startsWith(prefix));
        }

        @Test
        @DisplayName("Should find employees by email ending with")
        void shouldFindByEmailEndingWith() {
            // Given
            String suffix = "@example.com";

            // When
            List<Employee> employees = employeeRepository.findByEmailEndingWith(suffix);

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> e.getEmail() != null && e.getEmail().endsWith(suffix));
        }

        @Test
        @DisplayName("Should find employees by name containing text")
        void shouldFindByNameContaining() {
            // Given
            String text = "Alice";

            // When
            List<Employee> employees = employeeRepository.findByNameContaining(text);

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> e.getName().contains(text));
        }

        @Test
        @DisplayName("Should find employees by name NOT LIKE")
        void shouldFindByNameNotLike() {
            // Given
            String pattern = "%Z%"; // Pattern that shouldn't match any test employee

            // When
            List<Employee> employees = employeeRepository.findByNameNotLike(pattern);

            // Then
            assertThat(employees).hasSize(getTestEmployeeCount()); // All employees should match
            assertThat(employees).allMatch(e -> !e.getName().contains("Z"));
        }

        @Test
        @DisplayName("Should find employees by name not starting with")
        void shouldFindByNameNotStartingWith() {
            // Given
            String prefix = "Z"; // Prefix that shouldn't match any test employee

            // When
            List<Employee> employees = employeeRepository.findByNameNotStartingWith(prefix);

            // Then
            assertThat(employees).hasSize(getTestEmployeeCount());
            assertThat(employees).allMatch(e -> !e.getName().startsWith(prefix));
        }

        @Test
        @DisplayName("Should find employees by email not ending with")
        void shouldFindByEmailNotEndingWith() {
            // Given
            String suffix = "@gmail.com";

            // When
            List<Employee> employees = employeeRepository.findByEmailNotEndingWith(suffix);

            // Then
            assertThat(employees).hasSize(getTestEmployeeCount());
            assertThat(employees).allMatch(e -> e.getEmail() == null || !e.getEmail().endsWith(suffix));
        }

        @Test
        @DisplayName("Should find employees by name not containing")
        void shouldFindByNameNotContaining() {
            // Given
            String text = "Nonexistent";

            // When
            List<Employee> employees = employeeRepository.findByNameNotContaining(text);

            // Then
            assertThat(employees).hasSize(getTestEmployeeCount());
            assertThat(employees).allMatch(e -> !e.getName().contains(text));
        }

        @Test
        @DisplayName("Should handle LIKE with null pattern")
        void shouldHandleLikeWithNullPattern() {
            // When
            List<Employee> employees = employeeRepository.findByNameLikeIfPresent(null);

            // Then - should return all employees since condition not applied
            assertThat(employees).hasSize(getTestEmployeeCount());
        }

        @Test
        @DisplayName("Should handle contains with empty string")
        void shouldHandleContainsWithEmptyString() {
            // When
            List<Employee> employees = employeeRepository.findByNameContainingIfPresent("");

            // Then - should return all employees since condition not applied
            assertThat(employees).hasSize(getTestEmployeeCount());
        }

        @Test
        @DisplayName("Should find employees with case-insensitive name search")
        void shouldFindByNameContainingIgnoreCase() {
            // Given
            String text = "ALICE"; // uppercase search

            // When
            List<Employee> employees = employeeRepository.findByNameContainingIgnoreCase(text);

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> e.getName().toLowerCase().contains(text.toLowerCase()));
        }

        @Test
        @DisplayName("Should find employees with valid email (contains @)")
        void shouldFindWithValidEmail() {
            // When
            List<Employee> employees = employeeRepository.findWithValidEmail();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> e.getEmail() != null && e.getEmail().contains("@"));
        }
    }

    // ==================== Combined Conditions Tests ====================

    @Nested
    @DisplayName("Combined Conditions Tests")
    class CombinedConditionsTests {

        @Test
        @DisplayName("Should find active employees in department")
        void shouldFindActiveInDepartment() {
            // Given
            Long deptId = getFirstDepartmentId();
            int expectedCount = (int) testEmployees.stream()
                    .filter(e -> e.getActive() && e.getDepartmentId().equals(deptId))
                    .count();

            // When
            List<Employee> employees = employeeRepository.findActiveInDepartment(deptId);

            // Then
            assertThat(employees).hasSize(expectedCount);
            assertThat(employees).allMatch(e -> e.getActive() && e.getDepartmentId().equals(deptId));
        }

        @Test
        @DisplayName("Should find active high earners")
        void shouldFindActiveHighEarners() {
            // Given
            BigDecimal threshold = new BigDecimal("50000.00");
            int expectedCount = (int) testEmployees.stream()
                    .filter(e -> e.getActive() && e.getSalary().compareTo(threshold) > 0)
                    .count();

            // When
            List<Employee> employees = employeeRepository.findActiveHighEarners();

            // Then
            assertThat(employees).hasSize(expectedCount);
            assertThat(employees).allMatch(e -> e.getActive() && e.getSalary().compareTo(new BigDecimal("50000.00")) > 0);
        }

        @Test
        @DisplayName("Should find employees by multiple conditions")
        void shouldFindEmployeesByMultipleConditions() {
            // When
            List<Employee> employees = employeeRepository.findEmployeesByMultipleConditions();

            // Then - verify all conditions
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(Employee::getActive);
            assertThat(employees).allMatch(e ->
                    e.getStatus() == EmployeeStatus.ACTIVE || e.getStatus() == EmployeeStatus.ON_LEAVE);
            assertThat(employees).allMatch(e -> e.getDepartmentId() != null);
        }

        @Test
        @DisplayName("Should search employees with optional filters")
        void shouldSearchEmployeesWithOptionalFilters() {
            // Given
            String name = "Alice";
            Long departmentId = null; // not filtering by department
            BigDecimal minSalary = null; // not filtering by salary

            // When
            List<Employee> employees = employeeRepository.searchEmployees(name, departmentId, minSalary);

            // Then - should only filter by name
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> e.getName().contains(name));
        }

        @Test
        @DisplayName("Should advanced search with all parameters")
        void shouldAdvancedSearchWithAllParameters() {
            // Given
            String name = null;
            String email = null;
            Long departmentId = getFirstDepartmentId();
            EmployeeStatus status = EmployeeStatus.ACTIVE;
            Boolean active = true;
            BigDecimal minSalary = new BigDecimal("50000.00");
            BigDecimal maxSalary = new BigDecimal("100000.00");
            LocalDate hireAfter = LocalDate.of(2019, 1, 1);

            // When
            List<Employee> employees = employeeRepository.advancedSearch(
                    name, email, departmentId, status, active, minSalary, maxSalary, hireAfter);

            // Then - verify all applied filters
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> e.getDepartmentId().equals(departmentId));
            assertThat(employees).allMatch(e -> e.getStatus() == status);
            assertThat(employees).allMatch(e -> e.getActive());
            assertThat(employees).allMatch(e -> e.getSalary().compareTo(minSalary) >= 0);
            assertThat(employees).allMatch(e -> e.getSalary().compareTo(maxSalary) <= 0);
            assertThat(employees).allMatch(e -> !e.getHireDate().isBefore(hireAfter));
        }
    }

    // ==================== Numeric Expression Tests ====================

    @Nested
    @DisplayName("Numeric Expression Tests")
    class NumericExpressionTests {

        @Test
        @DisplayName("Should find employees by salary with bonus")
        void shouldFindBySalaryWithBonus() {
            // Given
            BigDecimal bonus = new BigDecimal("10000.00");
            BigDecimal threshold = new BigDecimal("80000.00");

            // When
            List<Employee> employees = employeeRepository.findBySalaryWithBonus(bonus, threshold);

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> e.getSalary().add(bonus).compareTo(threshold) > 0);
        }

        @Test
        @DisplayName("Should find employees by annual salary (monthly * 12)")
        void shouldFindByAnnualSalary() {
            // Given
            BigDecimal threshold = new BigDecimal("600000.00"); // 50000 * 12

            // When
            List<Employee> employees = employeeRepository.findByAnnualSalary(threshold);

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e ->
                    e.getSalary().multiply(new BigDecimal("12")).compareTo(threshold) > 0);
        }

        @Test
        @DisplayName("Should find employees by salary after deduction")
        void shouldFindBySalaryAfterDeduction() {
            // Given
            BigDecimal deduction = new BigDecimal("10000.00");
            BigDecimal minSalary = new BigDecimal("50000.00");

            // When
            List<Employee> employees = employeeRepository.findBySalaryAfterDeduction(deduction, minSalary);

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> e.getSalary().subtract(deduction).compareTo(minSalary) >= 0);
        }

        @Test
        @DisplayName("Should find employees by ID modulo")
        void shouldFindByIdMod() {
            // Given
            int divisor = 2;
            int remainder = 0;

            // When
            List<Employee> employees = employeeRepository.findByIdMod(divisor, remainder);

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e -> e.getId() % divisor == remainder);
        }
    }

    // ==================== OR Condition Tests ====================

    @Nested
    @DisplayName("OR Condition Tests")
    class OrConditionTests {

        @Test
        @DisplayName("Should find employees by status using OR")
        void shouldFindByStatusOrStatus() {
            // When
            List<Employee> employees = employeeRepository.findByStatusOrStatus();

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e ->
                    e.getStatus() == EmployeeStatus.ACTIVE || e.getStatus() == EmployeeStatus.ON_LEAVE);
        }

        @Test
        @DisplayName("Should find active employees with OR condition")
        void shouldFindActiveWithOrCondition() {
            // When
            List<Employee> employees = employeeRepository.findActiveWithOrCondition();

            // Then - verify the complex OR logic
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e ->
                    e.getActive() &&
                    (e.getSalary().compareTo(new BigDecimal("100000.00")) > 0 ||
                     e.getStatus() == EmployeeStatus.ACTIVE));
        }

        @Test
        @DisplayName("Should find employees by salary OR status")
        void shouldFindBySalaryOrStatus() {
            // Given
            BigDecimal minSalary = new BigDecimal("80000.00");
            EmployeeStatus status = EmployeeStatus.ON_LEAVE;

            // When
            List<Employee> employees = employeeRepository.findBySalaryOrStatus(minSalary, status);

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(e ->
                    e.getSalary().compareTo(minSalary) > 0 || e.getStatus() == status);
        }
    }
}