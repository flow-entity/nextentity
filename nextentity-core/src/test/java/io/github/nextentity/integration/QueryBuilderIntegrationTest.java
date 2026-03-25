package io.github.nextentity.integration;

import io.github.nextentity.api.Predicate;
import io.github.nextentity.api.model.Tuple2;
import io.github.nextentity.integration.config.IntegrationTestContext;
import io.github.nextentity.integration.config.IntegrationTestProvider;
import io.github.nextentity.integration.entity.Department;
import io.github.nextentity.integration.entity.Employee;
import io.github.nextentity.integration.entity.EmployeeStatus;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;

import static io.github.nextentity.core.util.Paths.get;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for QueryBuilder.
 */
class QueryBuilderIntegrationTest {

    // Test data constants - Employees
    private static final int TOTAL_TEST_EMPLOYEES = 12;
    private static final double MAX_SALARY = 85000.0;
    private static final double MIN_SALARY = 48000.0;
    private static final String ALICE_NAME = "Alice Johnson";
    private static final String BOB_NAME = "Bob Smith";

    // Test data constants - Departments
    private static final int TOTAL_DEPARTMENTS = 5;

    // Pagination constants
    private static final int PAGE_SIZE = 5;
    private static final int OFFSET = 3;

    // Salary range for between tests
    private static final double MIN_SALARY_RANGE = 60000.0;
    private static final double MAX_SALARY_RANGE = 75000.0;

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    void getList_ShouldReturnAllEntities(IntegrationTestContext context) {
        // when
        List<Employee> employees = context.queryEmployees().getList();

        // then
        assertThat(employees).isNotEmpty();
        assertThat(employees).hasSize(TOTAL_TEST_EMPLOYEES);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    void where_WithEqualityCondition_ShouldFilterResults(IntegrationTestContext context) {
        // when
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).eq(ALICE_NAME)
                .getList();

        // then
        assertThat(employees).hasSize(1);
        assertThat(employees.get(0).getName()).isEqualTo(ALICE_NAME);
    }

    /**
     * Tests multiple WHERE conditions with AND logic using Predicate.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    void where_WithMultipleConditions_ShouldFilterResults(IntegrationTestContext context) {
        // given
        Predicate<Employee> isActive = get(Employee::getActive).eq(true);

        // when
        List<Employee> employees = context.queryEmployees()
                .where(isActive.and(Employee::getStatus).eq(EmployeeStatus.ACTIVE))
                .getList();

        // then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getActive() && e.getStatus() == EmployeeStatus.ACTIVE);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    void orderBy_WithAscendingOrder_ShouldSortResults(IntegrationTestContext context) {
        // when
        List<Employee> employees = context.queryEmployees()
                .orderBy(Employee::getSalary).asc()
                .getList();

        // then
        assertThat(employees).isNotEmpty();
        for (int i = 1; i < employees.size(); i++) {
            assertThat(employees.get(i).getSalary())
                    .isGreaterThanOrEqualTo(employees.get(i - 1).getSalary());
        }
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    void orderBy_WithDescendingOrder_ShouldSortResults(IntegrationTestContext context) {
        // when
        List<Employee> employees = context.queryEmployees()
                .orderBy(Employee::getSalary).desc()
                .getList();

        // then
        assertThat(employees).isNotEmpty();
        for (int i = 1; i < employees.size(); i++) {
            assertThat(employees.get(i).getSalary())
                    .isLessThanOrEqualTo(employees.get(i - 1).getSalary());
        }
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    void getFirst_ShouldReturnFirstResult(IntegrationTestContext context) {
        // when
        Employee employee = context.queryEmployees()
                .orderBy(Employee::getSalary).desc()
                .getFirst();

        // then
        assertThat(employee).isNotNull();
        assertThat(employee.getSalary()).isEqualTo(MAX_SALARY);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    void limit_ShouldRestrictResultCount(IntegrationTestContext context) {
        // when
        List<Employee> employees = context.queryEmployees()
                .limit(PAGE_SIZE);

        // then
        assertThat(employees).hasSize(PAGE_SIZE);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    void offset_ShouldSkipResults(IntegrationTestContext context) {
        // given
        List<Employee> allEmployees = context.queryEmployees()
                .orderBy(Employee::getId).asc()
                .getList();

        // when
        List<Employee> pagedEmployees = context.queryEmployees()
                .orderBy(Employee::getId).asc()
                .getList(OFFSET, PAGE_SIZE);

        // then
        assertThat(pagedEmployees).hasSize(PAGE_SIZE);
        assertThat(pagedEmployees.get(0).getId()).isEqualTo(allEmployees.get(OFFSET).getId());
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    void select_WithSingleField_ShouldReturnProjectedResults(IntegrationTestContext context) {
        // when
        List<String> names = context.queryEmployees()
                .select(Employee::getName)
                .getList();

        // then
        assertThat(names).isNotEmpty();
        assertThat(names).hasSize(TOTAL_TEST_EMPLOYEES);
        assertThat(names).contains(ALICE_NAME, BOB_NAME);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    void select_WithMultipleFields_ShouldReturnTupleResults(IntegrationTestContext context) {
        // when
        List<Tuple2<String, Double>> results = context.queryEmployees()
                .select(Employee::getName, Employee::getSalary)
                .getList();

        // then
        assertThat(results).isNotEmpty();
        assertThat(results).hasSize(TOTAL_TEST_EMPLOYEES);

        Tuple2<String, Double> first = results.get(0);
        assertThat(first.get0()).isNotNull();
        assertThat(first.get1()).isNotNull();
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    void selectDistinct_ShouldReturnUniqueResults(IntegrationTestContext context) {
        // when
        List<Long> deptIds = context.queryEmployees()
                .selectDistinct(Employee::getDepartmentId)
                .getList();

        // then
        assertThat(deptIds).isNotEmpty();
        assertThat(deptIds).hasSize(TOTAL_DEPARTMENTS);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    void where_WithInClause_ShouldFilterResults(IntegrationTestContext context) {
        // when
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getDepartmentId).in(1L, 2L)
                .getList();

        // then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getDepartmentId() == 1L || e.getDepartmentId() == 2L);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    void where_WithBetweenClause_ShouldFilterResults(IntegrationTestContext context) {
        // when
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getSalary).between(MIN_SALARY_RANGE, MAX_SALARY_RANGE)
                .getList();

        // then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getSalary() >= MIN_SALARY_RANGE && e.getSalary() <= MAX_SALARY_RANGE);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    void where_WithLikeClause_ShouldFilterResults(IntegrationTestContext context) {
        // when
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getEmail).like("%@example.com")
                .getList();

        // then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getEmail().endsWith("@example.com"));
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    void where_WithIsNotNull_ShouldFilterResults(IntegrationTestContext context) {
        // when
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getDepartmentId).isNotNull()
                .getList();

        // then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getDepartmentId() != null);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    void select_WithCount_ShouldReturnCount(IntegrationTestContext context) {
        // when
        Long count = context.queryEmployees()
                .select(get(Employee::getId).count())
                .getFirst();

        // then
        assertThat(count).isEqualTo(TOTAL_TEST_EMPLOYEES);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    void select_WithSum_ShouldReturnSum(IntegrationTestContext context) {
        // when
        Double sum = context.queryEmployees()
                .select(get(Employee::getSalary).sum())
                .getFirst();

        // then
        assertThat(sum).isPositive();
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    void select_WithAvg_ShouldReturnAverage(IntegrationTestContext context) {
        // when
        Double avg = context.queryEmployees()
                .select(get(Employee::getSalary).avg())
                .getFirst();

        // then
        assertThat(avg).isPositive();
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    void select_WithMax_ShouldReturnMax(IntegrationTestContext context) {
        // when
        Double max = context.queryEmployees()
                .select(get(Employee::getSalary).max())
                .getFirst();

        // then
        assertThat(max).isEqualTo(MAX_SALARY);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    void select_WithMin_ShouldReturnMin(IntegrationTestContext context) {
        // when
        Double min = context.queryEmployees()
                .select(get(Employee::getSalary).min())
                .getFirst();

        // then
        assertThat(min).isEqualTo(MIN_SALARY);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    void where_WithOrCondition_ShouldFilterResults(IntegrationTestContext context) {
        // given
        Predicate<Employee> isAlice = get(Employee::getName).eq(ALICE_NAME);

        // when
        List<Employee> employees = context.queryEmployees()
                .where(isAlice.or(Employee::getName).eq(BOB_NAME))
                .getList();

        // then
        assertThat(employees).hasSize(2);
        assertThat(employees).extracting(Employee::getName)
                .containsExactlyInAnyOrder(ALICE_NAME, BOB_NAME);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    void query_Departments_ShouldReturnAllDepartments(IntegrationTestContext context) {
        // when
        List<Department> departments = context.queryDepartments().getList();

        // then
        assertThat(departments).hasSize(TOTAL_DEPARTMENTS);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    void select_WithProjectionType_ShouldReturnProjectedResults(IntegrationTestContext context) {
        // when
        List<Employee> employees = context.queryEmployees()
                .select(Employee.class)
                .where(Employee::getActive).eq(true)
                .getList();

        // then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(Employee::getActive);
    }
}
