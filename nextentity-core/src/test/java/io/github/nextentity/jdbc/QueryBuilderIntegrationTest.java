package io.github.nextentity.jdbc;

import io.github.nextentity.api.model.Tuple2;
import io.github.nextentity.test.db.AbstractIntegrationTest;
import io.github.nextentity.test.entity.Department;
import io.github.nextentity.test.entity.Employee;
import io.github.nextentity.test.entity.EmployeeStatus;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.github.nextentity.core.util.Paths.get;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for QueryBuilder.
 */
class QueryBuilderIntegrationTest extends AbstractIntegrationTest {

    /**
     * Test objective: Verify that basic query returns all entities.
     * Test scenario: Execute getList() without any conditions.
     * Expected result: Returns all entities from the database.
     */
    @Test
    void getList_ShouldReturnAllEntities() {
        // when
        List<Employee> employees = query(Employee.class).getList();

        // then
        assertThat(employees).isNotEmpty();
        assertThat(employees).hasSize(12);
    }

    /**
     * Test objective: Verify that where clause filters results correctly.
     * Test scenario: Query with equality condition.
     * Expected result: Returns only matching entities.
     */
    @Test
    void where_WithEqualityCondition_ShouldFilterResults() {
        // when
        List<Employee> employees = query(Employee.class)
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
    @Test
    void where_WithMultipleConditions_ShouldFilterResults() {
        // given
        io.github.nextentity.api.Predicate<Employee> isActive = get(Employee::getActive).eq(true);

        // when
        List<Employee> employees = query(Employee.class)
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
    @Test
    void orderBy_WithAscendingOrder_ShouldSortResults() {
        // when
        List<Employee> employees = query(Employee.class)
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
    @Test
    void orderBy_WithDescendingOrder_ShouldSortResults() {
        // when
        List<Employee> employees = query(Employee.class)
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
    @Test
    void getFirst_ShouldReturnFirstResult() {
        // when
        Employee employee = query(Employee.class)
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
    @Test
    void limit_ShouldRestrictResultCount() {
        // when
        List<Employee> employees = query(Employee.class)
                .limit(5);

        // then
        assertThat(employees).hasSize(5);
    }

    /**
     * Test objective: Verify that offset skips results.
     * Test scenario: Query with offset and limit.
     * Expected result: Skips the first N results and returns the next M.
     */
    @Test
    void offset_ShouldSkipResults() {
        // given
        List<Employee> allEmployees = query(Employee.class)
                .orderBy(Employee::getId).asc()
                .getList();

        // when
        List<Employee> pagedEmployees = query(Employee.class)
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
    @Test
    void select_WithSingleField_ShouldReturnProjectedResults() {
        // when
        List<String> names = query(Employee.class)
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
    @Test
    void select_WithMultipleFields_ShouldReturnTupleResults() {
        // when
        List<Tuple2<String, Double>> results = query(Employee.class)
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
    @Test
    void selectDistinct_ShouldReturnUniqueResults() {
        // when
        List<Long> deptIds = query(Employee.class)
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
    @Test
    void where_WithInClause_ShouldFilterResults() {
        // when
        List<Employee> employees = query(Employee.class)
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
    @Test
    void where_WithBetweenClause_ShouldFilterResults() {
        // when
        List<Employee> employees = query(Employee.class)
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
    @Test
    void where_WithLikeClause_ShouldFilterResults() {
        // when
        List<Employee> employees = query(Employee.class)
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
    @Test
    void where_WithIsNotNull_ShouldFilterResults() {
        // when
        List<Employee> employees = query(Employee.class)
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
    @Test
    void select_WithCount_ShouldReturnCount() {
        // when
        Long count = query(Employee.class)
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
    @Test
    void select_WithSum_ShouldReturnSum() {
        // when
        Double sum = query(Employee.class)
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
    @Test
    void select_WithAvg_ShouldReturnAverage() {
        // when
        Double avg = query(Employee.class)
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
    @Test
    void select_WithMax_ShouldReturnMax() {
        // when
        Double max = query(Employee.class)
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
    @Test
    void select_WithMin_ShouldReturnMin() {
        // when
        Double min = query(Employee.class)
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
    @Test
    void where_WithOrCondition_ShouldFilterResults() {
        // given
        io.github.nextentity.api.Predicate<Employee> isAlice = get(Employee::getName).eq("Alice Johnson");

        // when
        List<Employee> employees = query(Employee.class)
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
    @Test
    void query_Departments_ShouldReturnAllDepartments() {
        // when
        List<Department> departments = query(Department.class).getList();

        // then
        assertThat(departments).hasSize(5);
    }

    /**
     * Test objective: Verify that select with projection type works correctly.
     * Test scenario: Query selecting into a different type.
     * Expected result: Returns projected results.
     */
    @Test
    void select_WithProjectionType_ShouldReturnProjectedResults() {
        // when
        List<Employee> employees = query(Employee.class)
                .select(Employee.class)
                .where(Employee::getActive).eq(true)
                .getList();

        // then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(Employee::getActive);
    }
}
