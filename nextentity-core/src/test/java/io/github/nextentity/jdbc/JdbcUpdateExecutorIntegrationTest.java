package io.github.nextentity.jdbc;

import io.github.nextentity.test.db.AbstractIntegrationTest;
import io.github.nextentity.test.entity.Department;
import io.github.nextentity.test.entity.Employee;
import io.github.nextentity.test.entity.EmployeeStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for JdbcUpdateExecutor.
 */
class JdbcUpdateExecutorIntegrationTest extends AbstractIntegrationTest {

    /**
     * Test objective: Verify that insert operation works correctly.
     * Test scenario: Insert a new employee.
     * Expected result: The employee is inserted into the database.
     */
    @Test
    void insert_NewEmployee_ShouldPersistToDatabase() {
        // given
        Employee newEmployee = new Employee(
                100L, "Test Employee", "test@example.com",
                50000.0, true, EmployeeStatus.ACTIVE, 1L,
                LocalDate.of(2024, 1, 1));

        // when
        doInTransaction(() -> updateExecutor.insert(newEmployee, Employee.class));

        // then
        List<Employee> employees = query(Employee.class)
                .where(Employee::getId).eq(100L)
                .getList();

        assertThat(employees).hasSize(1);
        assertThat(employees.get(0).getName()).isEqualTo("Test Employee");
    }

    /**
     * Test objective: Verify that update operation works correctly.
     * Test scenario: Update an existing employee's salary.
     * Expected result: The employee's salary is updated in the database.
     */
    @Test
    void update_ExistingEmployee_ShouldModifyInDatabase() {
        // given
        Employee employee = query(Employee.class)
                .where(Employee::getId).eq(1L)
                .getFirst();
        Double originalSalary = employee.getSalary();
        Double newSalary = originalSalary + 10000.0;
        employee.setSalary(newSalary);

        // when
        doInTransaction(() -> updateExecutor.update(employee, Employee.class));

        // then
        Employee updatedEmployee = query(Employee.class)
                .where(Employee::getId).eq(1L)
                .getFirst();

        assertThat(updatedEmployee.getSalary()).isEqualTo(newSalary);
    }

    /**
     * Test objective: Verify that delete operation works correctly.
     * Test scenario: Delete an existing employee.
     * Expected result: The employee is removed from the database.
     */
    @Test
    void delete_ExistingEmployee_ShouldRemoveFromDatabase() {
        // given
        Employee employee = query(Employee.class)
                .where(Employee::getId).eq(1L)
                .getFirst();

        // when
        doInTransaction(() -> updateExecutor.delete(employee, Employee.class));

        // then
        List<Employee> employees = query(Employee.class)
                .where(Employee::getId).eq(1L)
                .getList();

        assertThat(employees).isEmpty();
    }

    /**
     * Test objective: Verify that patch operation works correctly.
     * Test scenario: Patch an existing employee with only changed fields.
     * Expected result: Only the specified fields are updated.
     */
    @Test
    void patch_ExistingEmployee_ShouldUpdateOnlyChangedFields() {
        // given
        Employee employee = query(Employee.class)
                .where(Employee::getId).eq(2L)
                .getFirst();
        String originalEmail = employee.getEmail();
        String newEmail = "updated@example.com";
        employee.setEmail(newEmail);

        // when
        doInTransaction(() -> updateExecutor.patch(employee, Employee.class));

        // then
        Employee patchedEmployee = query(Employee.class)
                .where(Employee::getId).eq(2L)
                .getFirst();

        assertThat(patchedEmployee.getEmail()).isEqualTo(newEmail);
    }

    /**
     * Test objective: Verify that batch insert works correctly.
     * Test scenario: Insert multiple employees at once.
     * Expected result: All employees are inserted into the database.
     */
    @Test
    void insertAll_MultipleEmployees_ShouldPersistAllToDatabase() {
        // given
        List<Employee> newEmployees = List.of(
                new Employee(200L, "Employee A", "a@example.com", 60000.0, true, EmployeeStatus.ACTIVE, 2L, LocalDate.now()),
                new Employee(201L, "Employee B", "b@example.com", 65000.0, true, EmployeeStatus.ACTIVE, 2L, LocalDate.now()),
                new Employee(202L, "Employee C", "c@example.com", 70000.0, true, EmployeeStatus.ACTIVE, 2L, LocalDate.now())
        );

        // when
        doInTransaction(() -> updateExecutor.insertAll(newEmployees, Employee.class));

        // then
        List<Employee> employees = query(Employee.class)
                .where(Employee::getId).in(200L, 201L, 202L)
                .getList();

        assertThat(employees).hasSize(3);
    }

    /**
     * Test objective: Verify that batch update works correctly.
     * Test scenario: Update multiple employees at once.
     * Expected result: All employees are updated in the database.
     */
    @Test
    void updateAll_MultipleEmployees_ShouldModifyAllInDatabase() {
        // given
        List<Employee> employees = query(Employee.class)
                .where(Employee::getDepartmentId).eq(1L)
                .limit(3);
        Double originalSalary = employees.get(0).getSalary();
        Double newSalary = originalSalary + 5000.0;
        for (Employee emp : employees) {
            emp.setSalary(newSalary);
        }

        // when
        doInTransaction(() -> updateExecutor.updateAll(employees, Employee.class));

        // then
        List<Employee> updatedEmployees = query(Employee.class)
                .where(Employee::getDepartmentId).eq(1L)
                .limit(3);

        assertThat(updatedEmployees).allMatch(e -> e.getSalary().equals(newSalary));
    }

    /**
     * Test objective: Verify that batch delete works correctly.
     * Test scenario: Delete multiple employees at once.
     * Expected result: All employees are removed from the database.
     */
    @Test
    void deleteAll_MultipleEmployees_ShouldRemoveAllFromDatabase() {
        // given
        List<Employee> employees = query(Employee.class)
                .where(Employee::getDepartmentId).eq(5L)
                .getList();
        assertThat(employees).isNotEmpty();

        // when
        doInTransaction(() -> updateExecutor.deleteAll(employees, Employee.class));

        // then
        List<Employee> remaining = query(Employee.class)
                .where(Employee::getDepartmentId).eq(5L)
                .getList();

        assertThat(remaining).isEmpty();
    }

    /**
     * Test objective: Verify that department insert works correctly.
     * Test scenario: Insert a new department.
     * Expected result: The department is inserted into the database.
     */
    @Test
    void insert_NewDepartment_ShouldPersistToDatabase() {
        // given
        Department newDept = new Department(100L, "Test Department", "Building E", 100000.0, true);

        // when
        doInTransaction(() -> updateExecutor.insert(newDept, Department.class));

        // then
        List<Department> departments = query(Department.class)
                .where(Department::getId).eq(100L)
                .getList();

        assertThat(departments).hasSize(1);
        assertThat(departments.get(0).getName()).isEqualTo("Test Department");
    }

    /**
     * Test objective: Verify that department update works correctly.
     * Test scenario: Update an existing department's budget.
     * Expected result: The department's budget is updated in the database.
     */
    @Test
    void update_ExistingDepartment_ShouldModifyInDatabase() {
        // given
        Department dept = query(Department.class)
                .where(Department::getId).eq(1L)
                .getFirst();
        Double originalBudget = dept.getBudget();
        Double newBudget = originalBudget + 50000.0;
        dept.setBudget(newBudget);

        // when
        doInTransaction(() -> updateExecutor.update(dept, Department.class));

        // then
        Department updatedDept = query(Department.class)
                .where(Department::getId).eq(1L)
                .getFirst();

        assertThat(updatedDept.getBudget()).isEqualTo(newBudget);
    }

    /**
     * Test objective: Verify that department delete works correctly.
     * Test scenario: Delete an existing department.
     * Expected result: The department is removed from the database.
     */
    @Test
    void delete_ExistingDepartment_ShouldRemoveFromDatabase() {
        // given
        Department dept = query(Department.class)
                .where(Department::getId).eq(5L)
                .getFirst();

        // when
        doInTransaction(() -> updateExecutor.delete(dept, Department.class));

        // then
        List<Department> departments = query(Department.class)
                .where(Department::getId).eq(5L)
                .getList();

        assertThat(departments).isEmpty();
    }
}
