package io.github.nextentity.integration;

import io.github.nextentity.api.Predicate;
import io.github.nextentity.api.model.Tuple2;
import io.github.nextentity.integration.config.DbConfig;
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

    /**
     * Test objective: Verify that basic query returns all entities.
     * Test scenario: Execute getList() without any conditions.
     * Expected result: Returns all entities from the database.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    void getList_ShouldReturnAllEntities(DbConfig config) {
        // when
        List<Employee> employees = config.queryEmployees().getList();

        // then
        assertThat(employees).isNotEmpty();
        assertThat(employees).hasSize(12);
    }

    /**
     * Test objective: Verify that where clause filters results correctly.
     * Test scenario: Query with equality condition.
     * Expected result: Returns only matching entities.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    void where_WithEqualityCondition_ShouldFilterResults(DbConfig config) {
        // when
        List<Employee> employees = config.queryEmployees()
                .where(Employee::getName).eq("Alice Johnson")
                .getList();

        // then
        assertThat(employees).hasSize(1);
        assertThat(employees.get(0).getName()).isEqualTo("Alice Johnson");
    }

    /**
     * Test objective: Verify that multiple where conditions work correctly.
     * Test scenario: Query with AND conditions using Predicate.
     * Expected result: Returns entities matching all conditions.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    void where_WithMultipleConditions_ShouldFilterResults(DbConfig config) {
        // given
        Predicate<Employee> isActive = get(Employee::getActive).eq(true);

        // when
        List<Employee> employees = config.queryEmployees()
                .where(isActive.and(Employee::getStatus).eq(EmployeeStatus.ACTIVE))
                .getList();

        // then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getActive() && e.getStatus() == EmployeeStatus.ACTIVE);
    }

    /**
     * Test objective: Verify that orderBy sorts results correctly.
     * Test scenario: Query with ascending order by salary.
     * Expected result: Returns entities sorted by salary in ascending order.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    void orderBy_WithAscendingOrder_ShouldSortResults(DbConfig config) {
        // when
        List<Employee> employees = config.queryEmployees()
                .orderBy(Employee::getSalary).asc()
                .getList();

        // then
        assertThat(employees).isNotEmpty();
        for (int i = 1; i < employees.size(); i++) {
            assertThat(employees.get(i).getSalary())
                    .isGreaterThanOrEqualTo(employees.get(i - 1).getSalary());
        }
    }

    /**
     * Test objective: Verify that orderBy with descending order works correctly.
     * Test scenario: Query with descending order by salary.
     * Expected result: Returns entities sorted by salary in descending order.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    void orderBy_WithDescendingOrder_ShouldSortResults(DbConfig config) {
        // when
        List<Employee> employees = config.queryEmployees()
                .orderBy(Employee::getSalary).desc()
                .getList();

        // then
        assertThat(employees).isNotEmpty();
        for (int i = 1; i < employees.size(); i++) {
            assertThat(employees.get(i).getSalary())
                    .isLessThanOrEqualTo(employees.get(i - 1).getSalary());
        }
    }

    /**
     * Test objective: Verify that getFirst returns the first result.
     * Test scenario: Query with orderBy and getFirst.
     * Expected result: Returns the first entity based on sort order.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    void getFirst_ShouldReturnFirstResult(DbConfig config) {
        // when
        Employee employee = config.queryEmployees()
                .orderBy(Employee::getSalary).desc()
                .getFirst();

        // then
        assertThat(employee).isNotNull();
        assertThat(employee.getSalary()).isEqualTo(85000.0);
    }

    /**
     * Test objective: Verify that limit restricts result count.
     * Test scenario: Query with limit.
     * Expected result: Returns at most the specified number of entities.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    void limit_ShouldRestrictResultCount(DbConfig config) {
        // when
        List<Employee> employees = config.queryEmployees()
                .limit(5);

        // then
        assertThat(employees).hasSize(5);
    }

    /**
     * Test objective: Verify that offset skips results.
     * Test scenario: Query with offset and limit.
     * Expected result: Skips the first N results and returns the next M.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    void offset_ShouldSkipResults(DbConfig config) {
        // given
        List<Employee> allEmployees = config.queryEmployees()
                .orderBy(Employee::getId).asc()
                .getList();

        // when
        List<Employee> pagedEmployees = config.queryEmployees()
                .orderBy(Employee::getId).asc()
                .getList(3, 5);

        // then
        assertThat(pagedEmployees).hasSize(5);
        assertThat(pagedEmployees.get(0).getId()).isEqualTo(allEmployees.get(3).getId());
    }

    /**
     * Test objective: Verify that select with single field returns projected results.
     * Test scenario: Query selecting only the name field.
     * Expected result: Returns list of names.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    void select_WithSingleField_ShouldReturnProjectedResults(DbConfig config) {
        // when
        List<String> names = config.queryEmployees()
                .select(Employee::getName)
                .getList();

        // then
        assertThat(names).isNotEmpty();
        assertThat(names).hasSize(12);
        assertThat(names).contains("Alice Johnson", "Bob Smith");
    }

    /**
     * Test objective: Verify that select with multiple fields returns tuple results.
     * Test scenario: Query selecting name and salary.
     * Expected result: Returns tuples of name and salary.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    void select_WithMultipleFields_ShouldReturnTupleResults(DbConfig config) {
        // when
        List<Tuple2<String, Double>> results = config.queryEmployees()
                .select(Employee::getName, Employee::getSalary)
                .getList();

        // then
        assertThat(results).isNotEmpty();
        assertThat(results).hasSize(12);

        Tuple2<String, Double> first = results.get(0);
        assertThat(first.get0()).isNotNull();
        assertThat(first.get1()).isNotNull();
    }

    /**
     * Test objective: Verify that select distinct returns unique results.
     * Test scenario: Query selecting distinct department IDs.
     * Expected result: Returns unique department IDs.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    void selectDistinct_ShouldReturnUniqueResults(DbConfig config) {
        // when
        List<Long> deptIds = config.queryEmployees()
                .selectDistinct(Employee::getDepartmentId)
                .getList();

        // then
        assertThat(deptIds).isNotEmpty();
        assertThat(deptIds).hasSize(5); // 5 distinct departments
    }

    /**
     * Test objective: Verify that in clause filters results correctly.
     * Test scenario: Query with IN condition.
     * Expected result: Returns entities matching any value in the list.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    void where_WithInClause_ShouldFilterResults(DbConfig config) {
        // when
        List<Employee> employees = config.queryEmployees()
                .where(Employee::getDepartmentId).in(1L, 2L)
                .getList();

        // then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getDepartmentId() == 1L || e.getDepartmentId() == 2L);
    }

    /**
     * Test objective: Verify that between clause filters results correctly.
     * Test scenario: Query with BETWEEN condition.
     * Expected result: Returns entities within the specified range.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    void where_WithBetweenClause_ShouldFilterResults(DbConfig config) {
        // when
        List<Employee> employees = config.queryEmployees()
                .where(Employee::getSalary).between(60000.0, 75000.0)
                .getList();

        // then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getSalary() >= 60000.0 && e.getSalary() <= 75000.0);
    }

    /**
     * Test objective: Verify that like clause filters results correctly.
     * Test scenario: Query with LIKE condition.
     * Expected result: Returns entities matching the pattern.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    void where_WithLikeClause_ShouldFilterResults(DbConfig config) {
        // when
        List<Employee> employees = config.queryEmployees()
                .where(Employee::getEmail).like("%@example.com")
                .getList();

        // then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getEmail().endsWith("@example.com"));
    }

    /**
     * Test objective: Verify that isNotNull filters results correctly.
     * Test scenario: Query with IS NOT NULL condition.
     * Expected result: Returns entities where the field is not null.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    void where_WithIsNotNull_ShouldFilterResults(DbConfig config) {
        // when
        List<Employee> employees = config.queryEmployees()
                .where(Employee::getDepartmentId).isNotNull()
                .getList();

        // then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getDepartmentId() != null);
    }

    /**
     * Test objective: Verify that count aggregation works correctly.
     * Test scenario: Query with count aggregation.
     * Expected result: Returns the count of matching entities.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    void select_WithCount_ShouldReturnCount(DbConfig config) {
        // when
        Long count = config.queryEmployees()
                .select(get(Employee::getId).count())
                .getFirst();

        // then
        assertThat(count).isEqualTo(12L);
    }

    /**
     * Test objective: Verify that sum aggregation works correctly.
     * Test scenario: Query with sum aggregation.
     * Expected result: Returns the sum of the specified field.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    void select_WithSum_ShouldReturnSum(DbConfig config) {
        // when
        Double sum = config.queryEmployees()
                .select(get(Employee::getSalary).sum())
                .getFirst();

        // then
        assertThat(sum).isPositive();
    }

    /**
     * Test objective: Verify that avg aggregation works correctly.
     * Test scenario: Query with avg aggregation.
     * Expected result: Returns the average of the specified field.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    void select_WithAvg_ShouldReturnAverage(DbConfig config) {
        // when
        Double avg = config.queryEmployees()
                .select(get(Employee::getSalary).avg())
                .getFirst();

        // then
        assertThat(avg).isPositive();
    }

    /**
     * Test objective: Verify that max aggregation works correctly.
     * Test scenario: Query with max aggregation.
     * Expected result: Returns the maximum value of the specified field.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    void select_WithMax_ShouldReturnMax(DbConfig config) {
        // when
        Double max = config.queryEmployees()
                .select(get(Employee::getSalary).max())
                .getFirst();

        // then
        assertThat(max).isEqualTo(85000.0);
    }

    /**
     * Test objective: Verify that min aggregation works correctly.
     * Test scenario: Query with min aggregation.
     * Expected result: Returns the minimum value of the specified field.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    void select_WithMin_ShouldReturnMin(DbConfig config) {
        // when
        Double min = config.queryEmployees()
                .select(get(Employee::getSalary).min())
                .getFirst();

        // then
        assertThat(min).isEqualTo(48000.0);
    }

    /**
     * Test objective: Verify that OR condition works correctly.
     * Test scenario: Query with OR condition using Predicate.
     * Expected result: Returns entities matching any of the conditions.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    void where_WithOrCondition_ShouldFilterResults(DbConfig config) {
        // given
        Predicate<Employee> isAlice = get(Employee::getName).eq("Alice Johnson");

        // when
        List<Employee> employees = config.queryEmployees()
                .where(isAlice.or(Employee::getName).eq("Bob Smith"))
                .getList();

        // then
        assertThat(employees).hasSize(2);
        assertThat(employees).extracting(Employee::getName)
                .containsExactlyInAnyOrder("Alice Johnson", "Bob Smith");
    }

    /**
     * Test objective: Verify that department query works correctly.
     * Test scenario: Query all departments.
     * Expected result: Returns all departments.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    void query_Departments_ShouldReturnAllDepartments(DbConfig config) {
        // when
        List<Department> departments = config.queryDepartments().getList();

        // then
        assertThat(departments).hasSize(5);
    }

    /**
     * Test objective: Verify that select with projection type works correctly.
     * Test scenario: Query selecting into a different type.
     * Expected result: Returns projected results.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    void select_WithProjectionType_ShouldReturnProjectedResults(DbConfig config) {
        // when
        List<Employee> employees = config.queryEmployees()
                .select(Employee.class)
                .where(Employee::getActive).eq(true)
                .getList();

        // then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(Employee::getActive);
    }
}
