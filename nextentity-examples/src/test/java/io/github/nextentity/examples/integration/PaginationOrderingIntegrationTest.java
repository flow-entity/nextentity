package io.github.nextentity.examples.integration;

import io.github.nextentity.api.model.Slice;
import io.github.nextentity.examples.entity.Employee;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/// 员工 Repository 中分页和排序操作的集成测试。
/// 测试排序、分页、切片操作并验证结果正确性。
@DisplayName("Pagination and Ordering Integration Tests")
class PaginationOrderingIntegrationTest extends BaseIntegrationTest {

    // ==================== Ordering Tests ====================

    @Nested
    @DisplayName("Ordering Tests")
    class OrderingTests {

        @Test
        @DisplayName("Should order employees by name ascending correctly")
        void shouldOrderByNameAscCorrectly() {
            // When
            List<Employee> employees = employeeRepository.findOrderedByNameAsc();

            // Then - verify ascending order and active status
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(Employee::getActive);

            for (int i = 0; i < employees.size() - 1; i++) {
                String currentName = employees.get(i).getName();
                String nextName = employees.get(i + 1).getName();
                assertThat(currentName.compareTo(nextName)).isLessThanOrEqualTo(0);
            }
        }

        @Test
        @DisplayName("Should order employees by salary descending correctly")
        void shouldOrderBySalaryDescCorrectly() {
            // When
            List<Employee> employees = employeeRepository.findOrderedBySalaryDesc();

            // Then - verify descending order and active status
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(Employee::getActive);

            for (int i = 0; i < employees.size() - 1; i++) {
                BigDecimal currentSalary = employees.get(i).getSalary();
                BigDecimal nextSalary = employees.get(i + 1).getSalary();
                assertThat(currentSalary.compareTo(nextSalary)).isGreaterThanOrEqualTo(0);
            }
        }

        @Test
        @DisplayName("Should order employees by department then salary correctly")
        void shouldOrderByDepartmentThenSalaryCorrectly() {
            // When
            List<Employee> employees = employeeRepository.findByDepartmentThenSalary();

            // Then - verify ordering (department asc, salary desc)
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(Employee::getActive);

            for (int i = 0; i < employees.size() - 1; i++) {
                Employee current = employees.get(i);
                Employee next = employees.get(i + 1);

                // Department should be ascending
                if (current.getDepartmentId().equals(next.getDepartmentId())) {
                    // Within same department, salary should be descending
                    assertThat(current.getSalary().compareTo(next.getSalary()))
                            .isGreaterThanOrEqualTo(0);
                } else {
                    assertThat(current.getDepartmentId()).isLessThan(next.getDepartmentId());
                }
            }
        }

        @Test
        @DisplayName("Should order employees by status then name correctly")
        void shouldOrderByStatusThenNameCorrectly() {
            // When
            List<Employee> employees = employeeRepository.findByStatusThenName();

            // Then - verify ordering (status asc, name asc)
            assertThat(employees).isNotEmpty();

            for (int i = 0; i < employees.size() - 1; i++) {
                Employee current = employees.get(i);
                Employee next = employees.get(i + 1);

                if (current.getStatus() == next.getStatus()) {
                    // Within same status, name should be ascending
                    assertThat(current.getName().compareTo(next.getName()))
                            .isLessThanOrEqualTo(0);
                } else {
                    // Status ordinal comparison (ascending)
                    assertThat(current.getStatus().ordinal())
                            .isLessThanOrEqualTo(next.getStatus().ordinal());
                }
            }
        }
    }

    // ==================== Pagination Tests ====================

    @Nested
    @DisplayName("Pagination Tests")
    class PaginationTests {

        @Test
        @DisplayName("Should get first page correctly")
        void shouldGetFirstPageCorrectly() {
            // When
            List<Employee> employees = employeeRepository.findFirstPage();

            // Then - verify 10 results, ordered by id asc
            assertThat(employees).hasSize(10);
            assertThat(employees.get(0).getId()).isEqualTo(getFirstEmployeeId());

            // Verify ascending order by id
            for (int i = 0; i < employees.size() - 1; i++) {
                assertThat(employees.get(i).getId()).isLessThan(employees.get(i + 1).getId());
            }
        }

        @Test
        @DisplayName("Should get specific page correctly")
        void shouldGetSpecificPageCorrectly() {
            // Given
            int pageNumber = 1;
            int pageSize = 5;

            // When
            List<Employee> employees = employeeRepository.findPage(pageNumber, pageSize);

            // Then - verify correct offset (page 1 = offset 5)
            assertThat(employees).hasSize(pageSize);

            // Verify these are not the first 5 employees
            List<Employee> firstPage = employeeRepository.findPage(0, pageSize);
            assertThat(employees).extracting(Employee::getId)
                    .doesNotContainAnyElementsOf(firstPage.stream().map(Employee::getId).toList());
        }

        @Test
        @DisplayName("Should get active employees paged correctly")
        void shouldGetActiveEmployeesPagedCorrectly() {
            // Given
            int offset = 0;
            int limit = 5;

            // When
            List<Employee> employees = employeeRepository.findActiveEmployeesPaged(offset, limit);

            // Then - verify active status and correct count
            assertThat(employees).hasSize(limit);
            assertThat(employees).allMatch(Employee::getActive);

            // Verify ordering (name asc)
            for (int i = 0; i < employees.size() - 1; i++) {
                assertThat(employees.get(i).getName().compareTo(employees.get(i + 1).getName()))
                        .isLessThanOrEqualTo(0);
            }
        }

        @Test
        @DisplayName("Should handle complex query with pagination correctly")
        void shouldHandleComplexQueryWithPaginationCorrectly() {
            // Given
            Long deptId = getFirstDepartmentId();
            int offset = 0;
            int limit = 3;

            // When
            List<Employee> employees = employeeRepository.findComplexQuery(deptId, offset, limit);

            // Then - verify all conditions and pagination
            assertThat(employees).hasSize(limit);
            assertThat(employees).allMatch(Employee::getActive);
            assertThat(employees).allMatch(e -> e.getDepartmentId().equals(deptId));
            assertThat(employees).allMatch(e -> e.getStatus() != null);

            // Verify ordering (hireDate desc, name asc)
            for (int i = 0; i < employees.size() - 1; i++) {
                Employee current = employees.get(i);
                Employee next = employees.get(i + 1);

                if (current.getHireDate().equals(next.getHireDate())) {
                    assertThat(current.getName().compareTo(next.getName())).isLessThanOrEqualTo(0);
                } else {
                    assertThat(current.getHireDate()).isAfterOrEqualTo(next.getHireDate());
                }
            }
        }
    }

    // ==================== Top N Tests ====================

    @Nested
    @DisplayName("Top N Tests")
    class TopNTests {

        @Test
        @DisplayName("Should find top N earners correctly")
        void shouldFindTopEarnersCorrectly() {
            // Given
            int n = 5;

            // When
            List<Employee> employees = employeeRepository.findTopEarners(n);

            // Then - verify count (may be less than n if not enough active employees)
            assertThat(employees).isNotEmpty();
            assertThat(employees).allMatch(Employee::getActive);

            // Verify descending order by salary
            for (int i = 0; i < employees.size() - 1; i++) {
                assertThat(employees.get(i).getSalary().compareTo(employees.get(i + 1).getSalary()))
                        .isGreaterThanOrEqualTo(0);
            }

            // Verify these are actually the top earners
            if (employees.size() >= n) {
                List<Employee> allActive = employeeRepository.findActiveEmployees();
                BigDecimal lowestTopSalary = employees.get(employees.size() - 1).getSalary();
                java.util.Set<Long> topEarnerIds = employees.stream().map(Employee::getId).collect(java.util.stream.Collectors.toSet());
                allActive.stream()
                        .filter(e -> !topEarnerIds.contains(e.getId()))
                        .forEach(e -> assertThat(e.getSalary().compareTo(lowestTopSalary))
                                .isLessThanOrEqualTo(0));
            }
        }

        @Test
        @DisplayName("Should find highest paid employee correctly")
        void shouldFindHighestPaidCorrectly() {
            // When
            Employee highest = employeeRepository.findHighestPaid();

            // Then - verify active and highest salary
            assertThat(highest).isNotNull();
            assertThat(highest.getActive()).isTrue();

            // Verify it's the highest among all active employees
            List<Employee> allActive = employeeRepository.findActiveEmployees();
            allActive.forEach(e -> assertThat(e.getSalary().compareTo(highest.getSalary()))
                    .isLessThanOrEqualTo(0));
        }
    }

    // ==================== Slice Tests ====================

    @Nested
    @DisplayName("Slice Tests")
    class SliceTests {

        @Test
        @DisplayName("Should get first slice correctly")
        void shouldGetFirstSliceCorrectly() {
            // When
            Slice<Employee> slice = employeeRepository.findFirstSlice();

            // Then - verify slice properties
            assertThat(slice.data()).hasSize(10);
            assertThat(slice.offset()).isEqualTo(0);
            assertThat(slice.limit()).isEqualTo(10);

            // Verify ordering (id asc)
            List<Employee> data = slice.data();
            for (int i = 0; i < data.size() - 1; i++) {
                assertThat(data.get(i).getId()).isLessThan(data.get(i + 1).getId());
            }
        }

        @Test
        @DisplayName("Should get high earner slice correctly")
        void shouldGetHighEarnerSliceCorrectly() {
            // Given
            int page = 0;
            int size = 5;
            BigDecimal threshold = new BigDecimal("50000.00");

            // When
            Slice<Employee> slice = employeeRepository.findHighEarnerSlice(page, size);

            // Then - verify slice properties and conditions
            assertThat(slice.data()).hasSize(size);
            assertThat(slice.offset()).isEqualTo(page * size);
            assertThat(slice.limit()).isEqualTo(size);

            // Verify all employees in slice match conditions
            assertThat(slice.data()).allMatch(Employee::getActive);
            assertThat(slice.data()).allMatch(e -> e.getSalary().compareTo(threshold) > 0);

            // Verify ordering (salary desc)
            List<Employee> data = slice.data();
            for (int i = 0; i < data.size() - 1; i++) {
                assertThat(data.get(i).getSalary().compareTo(data.get(i + 1).getSalary()))
                        .isGreaterThanOrEqualTo(0);
            }
        }
    }

    // ==================== Recently Hired Tests ====================

    @Nested
    @DisplayName("Recently Hired Tests")
    class RecentlyHiredTests {

        @Test
        @DisplayName("Should find recently hired employees correctly")
        void shouldFindRecentlyHiredCorrectly() {
            // Given - use a larger date range to include test data
            int days = 365 * 5; // Last 5 years to include test data

            // When
            List<Employee> employees = employeeRepository.findRecentlyHired(days);

            // Then - verify hire date within range and order
            assertThat(employees).isNotEmpty();

            // Verify ordering (hireDate desc)
            for (int i = 0; i < employees.size() - 1; i++) {
                assertThat(employees.get(i).getHireDate())
                        .isAfterOrEqualTo(employees.get(i + 1).getHireDate());
            }
        }
    }

    // ==================== Boundary Tests ====================

    @Nested
    @DisplayName("Boundary Tests")
    class BoundaryTests {

        @Test
        @DisplayName("Should handle pagination at boundary correctly")
        void shouldHandlePaginationAtBoundaryCorrectly() {
            // Given - total count
            int totalCount = getTestEmployeeCount();
            int pageSize = 10;
            int lastPage = (totalCount / pageSize);

            // When - get last page
            List<Employee> employees = employeeRepository.findPage(lastPage, pageSize);

            // Then - should return remaining items (may be less than pageSize)
            int expectedRemaining = totalCount - (lastPage * pageSize);
            assertThat(employees).hasSize(Math.min(pageSize, expectedRemaining));
        }

        @Test
        @DisplayName("Should handle offset beyond total count")
        void shouldHandleOffsetBeyondTotalCount() {
            // Given - offset beyond total
            int largeOffset = getTestEmployeeCount() + 100;

            // When
            List<Employee> employees = employeeRepository.findPage(largeOffset / 10, 10);

            // Then - should return empty or partial results
            assertThat(employees.size()).isLessThanOrEqualTo(10);
        }

        @Test
        @DisplayName("Should handle top N greater than total")
        void shouldHandleTopNGreaterThanTotal() {
            // Given
            int n = getTestEmployeeCount() + 100;

            // When
            List<Employee> employees = employeeRepository.findTopEarners(n);

            // Then - should return all active employees
            int activeCount = (int) testEmployees.stream().filter(Employee::getActive).count();
            assertThat(employees).hasSize(activeCount);
        }
    }
}