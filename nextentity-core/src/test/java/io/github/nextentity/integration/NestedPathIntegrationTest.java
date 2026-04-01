package io.github.nextentity.integration;

import io.github.nextentity.api.EntityPath;
import io.github.nextentity.integration.config.IntegrationTestContext;
import io.github.nextentity.integration.config.IntegrationTestProvider;
import io.github.nextentity.integration.entity.Department;
import io.github.nextentity.integration.entity.Employee;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for nested path expressions.
 * <p>
 * Tests association path queries including:
 * - Filtering by nested entity properties (e.g., Employee.department.name)
 * - Filtering by nested entity multiple properties
 * - Combined nested path with other conditions
 * <p>
 * These tests run against MySQL and PostgreSQL using Testcontainers.
 *
 * @author HuangChengwei
 */
@DisplayName("Nested Path Integration Tests")
public class NestedPathIntegrationTest {

    @Nested
    @DisplayName("Filter by Department Name Tests")
    class FilterByDepartmentNameTests {

        /**
         * Test objective: Verify filtering employees by department name using nested path.
         * Test scenario: Use Path.of(Employee::getDepartment).get(Department::getName).eq("Engineering")
         * Expected result: Returns all employees in Engineering department.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should filter employees by department name Engineering")
        void shouldFilterEmployees_ByDepartmentNameEngineering(IntegrationTestContext context) {
            // When - query employees in Engineering department using nested path
            List<Employee> employees = context.queryEmployees()
                    .fetch(Employee::getDepartment)
                    .where(EntityPath.of(Employee::getDepartment).get(Department::getName).eq("Engineering"))
                    .orderBy(Employee::getId).asc()
                    .getList();

            // Then - Engineering department has 5 employees
            assertThat(employees).hasSize(5);
            for (Employee emp : employees) {
                assertThat(emp.getDepartment()).isNotNull();
                assertThat(emp.getDepartment().getName()).isEqualTo("Engineering");
            }
        }

        /**
         * Test objective: Verify filtering employees by department name Marketing.
         * Test scenario: Use nested path to filter by Marketing department.
         * Expected result: Returns all employees in Marketing department.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should filter employees by department name Marketing")
        void shouldFilterEmployees_ByDepartmentNameMarketing(IntegrationTestContext context) {
            // When
            List<Employee> employees = context.queryEmployees()
                    .fetch(Employee::getDepartment)
                    .where(EntityPath.of(Employee::getDepartment).get(Department::getName).eq("Marketing"))
                    .orderBy(Employee::getId).asc()
                    .getList();

            // Then - Marketing department has 3 employees
            assertThat(employees).hasSize(3);
            for (Employee emp : employees) {
                assertThat(emp.getDepartment()).isNotNull();
                assertThat(emp.getDepartment().getName()).isEqualTo("Marketing");
            }
        }

        /**
         * Test objective: Verify filtering employees by department name Sales.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should filter employees by department name Sales")
        void shouldFilterEmployees_ByDepartmentNameSales(IntegrationTestContext context) {
            // When
            List<Employee> employees = context.queryEmployees()
                    .fetch(Employee::getDepartment)
                    .where(EntityPath.of(Employee::getDepartment).get(Department::getName).eq("Sales"))
                    .orderBy(Employee::getId).asc()
                    .getList();

            // Then - Sales department has 2 employees
            assertThat(employees).hasSize(2);
            for (Employee emp : employees) {
                assertThat(emp.getDepartment()).isNotNull();
                assertThat(emp.getDepartment().getName()).isEqualTo("Sales");
            }
        }

        /**
         * Test objective: Verify filtering employees by department name HR.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should filter employees by department name HR")
        void shouldFilterEmployees_ByDepartmentNameHR(IntegrationTestContext context) {
            // When
            List<Employee> employees = context.queryEmployees()
                    .fetch(Employee::getDepartment)
                    .where(EntityPath.of(Employee::getDepartment).get(Department::getName).eq("HR"))
                    .orderBy(Employee::getId).asc()
                    .getList();

            // Then - HR department has 1 employee
            assertThat(employees).hasSize(1);
            assertThat(employees.get(0).getDepartment()).isNotNull();
            assertThat(employees.get(0).getDepartment().getName()).isEqualTo("HR");
        }

        /**
         * Test objective: Verify filtering employees by department name Finance.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should filter employees by department name Finance")
        void shouldFilterEmployees_ByDepartmentNameFinance(IntegrationTestContext context) {
            // When
            List<Employee> employees = context.queryEmployees()
                    .fetch(Employee::getDepartment)
                    .where(EntityPath.of(Employee::getDepartment).get(Department::getName).eq("Finance"))
                    .orderBy(Employee::getId).asc()
                    .getList();

            // Then - Finance department has 1 employee
            assertThat(employees).hasSize(1);
            assertThat(employees.get(0).getDepartment()).isNotNull();
            assertThat(employees.get(0).getDepartment().getName()).isEqualTo("Finance");
        }

        /**
         * Test objective: Verify filtering by non-existent department name returns empty.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should return empty for non-existent department name")
        void shouldReturnEmpty_ForNonExistentDepartment(IntegrationTestContext context) {
            // When - query employees in non-existent department
            List<Employee> employees = context.queryEmployees()
                    .where(EntityPath.of(Employee::getDepartment).get(Department::getName).eq("IT"))
                    .getList();

            // Then
            assertThat(employees).isEmpty();
        }
    }

    @Nested
    @DisplayName("Filter by Nested Department Location Tests")
    class FilterByDepartmentLocationTests {

        /**
         * Test objective: Verify filtering employees by department location.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should filter employees by department location Building A")
        void shouldFilterEmployees_ByDepartmentLocationBuildingA(IntegrationTestContext context) {
            // When - Engineering (id=1) and HR (id=4) are in Building A
            List<Employee> employees = context.queryEmployees()
                    .fetch(Employee::getDepartment)
                    .where(EntityPath.of(Employee::getDepartment).get(Department::getLocation).eq("Building A"))
                    .orderBy(Employee::getId).asc()
                    .getList();

            // Then - Engineering has 5 employees + HR has 1 = 6 employees
            assertThat(employees).hasSize(6);
            for (Employee emp : employees) {
                assertThat(emp.getDepartment()).isNotNull();
                assertThat(emp.getDepartment().getLocation()).isEqualTo("Building A");
            }
        }

        /**
         * Test objective: Verify filtering employees by department location Building B.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should filter employees by department location Building B")
        void shouldFilterEmployees_ByDepartmentLocationBuildingB(IntegrationTestContext context) {
            // When - Marketing (id=2) is in Building B with 3 employees
            List<Employee> employees = context.queryEmployees()
                    .fetch(Employee::getDepartment)
                    .where(EntityPath.of(Employee::getDepartment).get(Department::getLocation).eq("Building B"))
                    .orderBy(Employee::getId).asc()
                    .getList();

            // Then
            assertThat(employees).hasSize(3);
            for (Employee emp : employees) {
                assertThat(emp.getDepartment()).isNotNull();
                assertThat(emp.getDepartment().getLocation()).isEqualTo("Building B");
            }
        }
    }

    @Nested
    @DisplayName("Filter by Nested Department Budget Tests")
    class FilterByDepartmentBudgetTests {

        /**
         * Test objective: Verify filtering employees by department budget using comparison operators.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should filter employees by department budget greater than")
        void shouldFilterEmployees_ByDepartmentBudgetGreaterThan(IntegrationTestContext context) {
            // When - departments with budget > 300000: Engineering(500000), Sales(400000)
            List<Employee> employees = context.queryEmployees()
                    .fetch(Employee::getDepartment)
                    .where(EntityPath.of(Employee::getDepartment).get(Department::getBudget).gt(300000.0))
                    .orderBy(Employee::getId).asc()
                    .getList();

            // Then - Engineering(5) + Sales(2) = 7 employees
            assertThat(employees).hasSize(7);
            for (Employee emp : employees) {
                assertThat(emp.getDepartment()).isNotNull();
                assertThat(emp.getDepartment().getBudget()).isGreaterThan(300000.0);
            }
        }

        /**
         * Test objective: Verify filtering employees by department budget using between.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should filter employees by department budget between")
        void shouldFilterEmployees_ByDepartmentBudgetBetween(IntegrationTestContext context) {
            // When - departments with budget between 200000 and 400000:
            // Marketing(300000), Sales(400000), HR(200000), Finance(250000)
            List<Employee> employees = context.queryEmployees()
                    .fetch(Employee::getDepartment)
                    .where(EntityPath.of(Employee::getDepartment).get(Department::getBudget).between(200000.0, 400000.0))
                    .orderBy(Employee::getId).asc()
                    .getList();

            // Then - Marketing(3) + Sales(2) + HR(1) + Finance(1) = 7 employees
            assertThat(employees).hasSize(7);
            for (Employee emp : employees) {
                assertThat(emp.getDepartment()).isNotNull();
                assertThat(emp.getDepartment().getBudget()).isBetween(200000.0, 400000.0);
            }
        }
    }

    @Nested
    @DisplayName("Filter by Nested Department Active Status Tests")
    class FilterByDepartmentActiveTests {

        /**
         * Test objective: Verify filtering employees by department active status.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should filter employees by active department")
        void shouldFilterEmployees_ByActiveDepartment(IntegrationTestContext context) {
            // When - active departments: Engineering, Marketing, Sales, HR (Finance is inactive)
            List<Employee> employees = context.queryEmployees()
                    .fetch(Employee::getDepartment)
                    .where(EntityPath.of(Employee::getDepartment).get(Department::getActive).eq(true))
                    .orderBy(Employee::getId).asc()
                    .getList();

            // Then - Engineering(5) + Marketing(3) + Sales(2) + HR(1) = 11 employees
            assertThat(employees).hasSize(11);
            for (Employee emp : employees) {
                assertThat(emp.getDepartment()).isNotNull();
                assertThat(emp.getDepartment().getActive()).isTrue();
            }
        }

        /**
         * Test objective: Verify filtering employees by inactive department.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should filter employees by inactive department")
        void shouldFilterEmployees_ByInactiveDepartment(IntegrationTestContext context) {
            // When - inactive department: Finance
            List<Employee> employees = context.queryEmployees()
                    .fetch(Employee::getDepartment)
                    .where(EntityPath.of(Employee::getDepartment).get(Department::getActive).eq(false))
                    .orderBy(Employee::getId).asc()
                    .getList();

            // Then - Finance has 1 employee
            assertThat(employees).hasSize(1);
            assertThat(employees.get(0).getDepartment()).isNotNull();
            assertThat(employees.get(0).getDepartment().getActive()).isFalse();
            assertThat(employees.get(0).getDepartment().getName()).isEqualTo("Finance");
        }
    }

    @Nested
    @DisplayName("Combined Nested Path Conditions Tests")
    class CombinedNestedPathConditionsTests {

        /**
         * Test objective: Verify combining nested path condition with direct property condition.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should combine nested path with employee active status")
        void shouldCombineNestedPath_WithEmployeeActive(IntegrationTestContext context) {
            // When - active employees in Engineering department
            List<Employee> employees = context.queryEmployees()
                    .fetch(Employee::getDepartment)
                    .where(EntityPath.of(Employee::getDepartment).get(Department::getName).eq("Engineering"))
                    .where(Employee::getActive).eq(true)
                    .orderBy(Employee::getId).asc()
                    .getList();

            // Then - Engineering has 4 active employees (Eve Adams is inactive)
            assertThat(employees).hasSize(4);
            for (Employee emp : employees) {
                assertThat(emp.getActive()).isTrue();
                assertThat(emp.getDepartment()).isNotNull();
                assertThat(emp.getDepartment().getName()).isEqualTo("Engineering");
            }
        }

        /**
         * Test objective: Verify combining multiple nested path conditions.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should combine multiple nested path conditions")
        void shouldCombine_MultipleNestedPathConditions(IntegrationTestContext context) {
            // When - employees in active departments with specific location
            List<Employee> employees = context.queryEmployees()
                    .fetch(Employee::getDepartment)
                    .where(EntityPath.of(Employee::getDepartment).get(Department::getActive).eq(true))
                    .where(EntityPath.of(Employee::getDepartment).get(Department::getLocation).eq("Building A"))
                    .orderBy(Employee::getId).asc()
                    .getList();

            // Then - Engineering and HR are active and in Building A
            // Engineering(5) + HR(1) = 6 employees
            assertThat(employees).hasSize(6);
            for (Employee emp : employees) {
                assertThat(emp.getDepartment()).isNotNull();
                assertThat(emp.getDepartment().getActive()).isTrue();
                assertThat(emp.getDepartment().getLocation()).isEqualTo("Building A");
            }
        }

        /**
         * Test objective: Verify combining nested path with salary condition.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should combine nested path with salary condition")
        void shouldCombineNestedPath_WithSalaryCondition(IntegrationTestContext context) {
            // When - employees in Engineering with salary > 70000
            List<Employee> employees = context.queryEmployees()
                    .fetch(Employee::getDepartment)
                    .where(EntityPath.of(Employee::getDepartment).get(Department::getName).eq("Engineering"))
                    .where(Employee::getSalary).gt(70000.0)
                    .orderBy(Employee::getSalary).desc()
                    .getList();

            // Then
            assertThat(employees).isNotEmpty();
            for (Employee emp : employees) {
                assertThat(emp.getSalary()).isGreaterThan(70000.0);
                assertThat(emp.getDepartment()).isNotNull();
                assertThat(emp.getDepartment().getName()).isEqualTo("Engineering");
            }
        }
    }

    @Nested
    @DisplayName("Nested Path with Order Tests")
    class NestedPathWithOrderTests {

        /**
         * Test objective: Verify ordering by nested property after filtering.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should order by employee salary after nested path filter")
        void shouldOrderBySalary_AfterNestedPathFilter(IntegrationTestContext context) {
            // When
            List<Employee> employees = context.queryEmployees()
                    .fetch(Employee::getDepartment)
                    .where(EntityPath.of(Employee::getDepartment).get(Department::getName).eq("Engineering"))
                    .orderBy(Employee::getSalary).desc()
                    .getList();

            // Then
            assertThat(employees).hasSize(5);
            // Verify descending order by salary
            for (int i = 1; i < employees.size(); i++) {
                assertThat(employees.get(i - 1).getSalary())
                        .isGreaterThanOrEqualTo(employees.get(i).getSalary());
            }
        }
    }

    @Nested
    @DisplayName("Nested Path Select Tests")
    class NestedPathSelectTests {

        /**
         * Test objective: Verify select with nested path expression.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should select department name from employees")
        void shouldSelectDepartmentName_FromEmployees(IntegrationTestContext context) {
            // When
            List<String> deptNames = context.queryEmployees()
                    .selectDistinct(EntityPath.of(Employee::getDepartment).get(Department::getName))
                    .getList();

            // Then - 5 distinct department names
            assertThat(deptNames).hasSize(5);
            assertThat(deptNames).containsExactlyInAnyOrder(
                    "Engineering", "Marketing", "Sales", "HR", "Finance");
        }

        /**
         * Test objective: Verify select department location from employees.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should select department location from employees")
        void shouldSelectDepartmentLocation_FromEmployees(IntegrationTestContext context) {
            // When
            List<String> locations = context.queryEmployees()
                    .selectDistinct(EntityPath.of(Employee::getDepartment).get(Department::getLocation))
                    .getList();

            // Then - 4 distinct locations (Building A, B, C, D)
            assertThat(locations).hasSize(4);
            assertThat(locations).containsExactlyInAnyOrder(
                    "Building A", "Building B", "Building C", "Building D");
        }
    }
}