package io.github.nextentity.integration;

import io.github.nextentity.integration.config.IntegrationTestContext;
import io.github.nextentity.integration.config.IntegrationTestProvider;
import io.github.nextentity.integration.entity.Department;
import io.github.nextentity.integration.entity.Employee;
import io.github.nextentity.integration.fast.FastIntegrationTestProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import static org.assertj.core.api.Assertions.assertThat;

///
/// 条件更新/删除操作中关联路径表达式的集成测试。
///
/// 测试场景：
/// - 单层嵌套路径：Employee::getDepartment).get(Department::getName)
/// - 组合条件：嵌套路径 + 直接属性条件
/// - DELETE 操作：带嵌套路径的删除
///
/// @author HuangChengwei
@DisplayName("Conditional Update/Delete with Join Path Tests")
public class ConditionalUpdateWithJoinTest {

    @AfterEach
    void tearDown() {
        var context = IntegrationTestProvider.getEntityManagerContext();
        if (context != null) {
            context.reset();
        }
    }

    // ========================================
    // UPDATE with Join Path Tests
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(FastIntegrationTestProvider.class)
    @DisplayName("Should update employees by department name using nested path")
    void shouldUpdateEmployeesByDepartmentName(IntegrationTestContext context) {
        // Given
        String departmentName = "Engineering";
        double newSalary = 100000.0;

        // Count employees before update
        long countBefore = context.queryEmployees()
                .where(Employee::getDepartment).get(Department::getName).eq(departmentName)
                .count();

        // When - Update employees in Engineering department
        int updated = context
                .update(context.getEntityContext(Employee.class))
                .set(Employee::getSalary, newSalary)
                .where(Employee::getDepartment).get(Department::getName).eq(departmentName)
                .execute();

        // Then
        assertThat(updated).isEqualTo(countBefore);

        // Verify all employees in Engineering department have updated salary
        var updatedEmployees = context.queryEmployees()
                .where(Employee::getDepartment).get(Department::getName).eq(departmentName)
                .list();

        assertThat(updatedEmployees).hasSize((int) countBefore);
        assertThat(updatedEmployees).allMatch(emp -> emp.getSalary().equals(newSalary));
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should update employees by department location using nested path")
    void shouldUpdateEmployeesByDepartmentLocation(IntegrationTestContext context) {
        // Given
        String location = "Building A";
        double newSalary = 95000.0;

        // Count employees before update
        long countBefore = context.queryEmployees()
                .where(Employee::getDepartment).get(Department::getLocation).eq(location)
                .count();

        // When - Update employees in Building A location
        int updated = context
                .update(context.getEntityContext(Employee.class))
                .set(Employee::getSalary, newSalary)
                .where(Employee::getDepartment).get(Department::getLocation).eq(location)
                .execute();

        // Then
        assertThat(updated).isEqualTo(countBefore);

        // Verify all employees in Building A have updated salary
        var updatedEmployees = context.queryEmployees()
                .where(Employee::getDepartment).get(Department::getLocation).eq(location)
                .list();

        assertThat(updatedEmployees).hasSize((int) countBefore);
        assertThat(updatedEmployees).allMatch(emp -> emp.getSalary().equals(newSalary));
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should update employees with combined conditions: nested path AND direct property")
    void shouldUpdateEmployeesWithCombinedConditions(IntegrationTestContext context) {
        // Given
        String departmentName = "Engineering";
        boolean activeStatus = true;
        double newSalary = 110000.0;

        // Count matching employees before update
        long countBefore = context.queryEmployees()
                .where(Employee::getDepartment).get(Department::getName).eq(departmentName)
                .where(Employee::getActive).eq(activeStatus)
                .count();

        // When - Update active employees in Engineering department
        int updated = context.update(context.getEntityContext(Employee.class))
                .set(Employee::getSalary, newSalary)
                .where(Employee::getDepartment).get(Department::getName).eq(departmentName)
                .where(Employee::getActive).eq(activeStatus)
                .execute();

        // Then
        assertThat(updated).isEqualTo(countBefore);

        // Verify all matching employees have updated salary
        var updatedEmployees = context.queryEmployees()
                .where(Employee::getDepartment).get(Department::getName).eq(departmentName)
                .where(Employee::getActive).eq(activeStatus)
                .list();

        assertThat(updatedEmployees).hasSize((int) countBefore);
        assertThat(updatedEmployees).allMatch(emp -> emp.getSalary().equals(newSalary));
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should update employees with nested path using IN operator")
    void shouldUpdateEmployeesWithNestedPathInOperator(IntegrationTestContext context) {
        // Given
        String[] departmentNames = {"Engineering", "Marketing"};
        double newSalary = 90000.0;

        // Count matching employees before update
        long countBefore = context.queryEmployees()
                .where(Employee::getDepartment).get(Department::getName).in(departmentNames)
                .count();

        // When
        int updated = context.update(context.getEntityContext(Employee.class))
                .set(Employee::getSalary, newSalary)
                .where(Employee::getDepartment).get(Department::getName).in(departmentNames)
                .execute();

        // Then
        assertThat(updated).isEqualTo(countBefore);
    }

    // ========================================
    // DELETE with Join Path Tests
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should delete employees by department name using nested path")
    void shouldDeleteEmployeesByDepartmentName(IntegrationTestContext context) {
        // Given - Create test employees to delete
        var engineeringDept = context.queryDepartments()
                .where(Department::getName).eq("Engineering")
                .single();

        long countBefore = context.queryEmployees()
                .where(Employee::getDepartmentId).eq(engineeringDept.getId())
                .count();

        // Create temporary employees for deletion test
        for (int i = 0; i < 3; i++) {
            Employee emp = new Employee();
            emp.setId(10000L + i);
            emp.setName("Temp Employee " + i);
            emp.setEmail("temp" + i + "@test.com");
            emp.setSalary(50000.0);
            emp.setActive(true);
            emp.setDepartmentId(engineeringDept.getId());
            context.getUpdateExecutor().insert(emp, context.getEntityContext(Employee.class));
        }

        // Count after insert
        long countAfterInsert = context.queryEmployees()
                .where(Employee::getDepartmentId).eq(engineeringDept.getId())
                .count();

        assertThat(countAfterInsert).isEqualTo(countBefore + 3);

        // When - Delete temporary employees by department name
        int deleted = context.delete(context.getEntityContext(Employee.class))
                .where(Employee::getDepartment).get(Department::getName).eq("Engineering")
                .where(Employee::getName).like("Temp Employee%")
                .execute();

        // Then
        assertThat(deleted).isEqualTo(3);

        // Verify employees were deleted
        long countAfterDelete = context.queryEmployees()
                .where(Employee::getDepartmentId).eq(engineeringDept.getId())
                .count();

        assertThat(countAfterDelete).isEqualTo(countBefore);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle update with no matching nested path")
    void shouldHandleUpdateWithNoMatchingNestedPath(IntegrationTestContext context) {
        // Given
        String nonExistentDept = "NonExistentDepartment";

        // When - Update with non-existent department
        int updated = context.update(context.getEntityContext(Employee.class))
                .set(Employee::getSalary, 200000.0)
                .where(Employee::getDepartment).get(Department::getName).eq(nonExistentDept)
                .execute();

        // Then
        assertThat(updated).isZero();
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle delete with no matching nested path")
    void shouldHandleDeleteWithNoMatchingNestedPath(IntegrationTestContext context) {
        // Given
        String nonExistentDept = "NonExistentDepartment";

        // When - Delete with non-existent department
        int deleted = context.delete(context.getEntityContext(Employee.class))
                .where(Employee::getDepartment)
                .get(Department::getName)
                .eq(nonExistentDept)
                .execute();

        // Then
        assertThat(deleted).isZero();
    }
}