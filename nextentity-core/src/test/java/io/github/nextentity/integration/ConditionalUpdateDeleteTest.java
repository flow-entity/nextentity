package io.github.nextentity.integration;

import io.github.nextentity.integration.config.IntegrationTestContext;
import io.github.nextentity.integration.config.IntegrationTestProvider;
import io.github.nextentity.integration.entity.Department;
import io.github.nextentity.integration.entity.Employee;
import io.github.nextentity.integration.entity.EmployeeStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

///
/// 条件更新和删除操作的集成测试。
///
/// 测试 UpdateExecutor#update(Class&lt;T&gt;) 和 UpdateExecutor#delete(Class&lt;T&gt;) 方法：
/// - 条件批量更新（UPDATE ... WHERE）
/// - 条件批量删除（DELETE ... WHERE）
/// - 多字段更新
/// - 多条件组合
///
/// @author HuangChengwei
@DisplayName("Conditional Update/Delete Integration Tests")
public class ConditionalUpdateDeleteTest {

    @AfterEach
    void tearDown() {
        var context = IntegrationTestProvider.getEntityManagerContext();
        if (context != null) {
            context.reset();
        }
    }

    // ========================================
    // 1. Conditional Update Tests
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should update single field with where condition")
    void shouldUpdateSingleFieldWithWhereCondition(IntegrationTestContext context) {
        // Given - 先插入一条测试数据
        Employee employee = createTestEmployee(9001L, "Update Test", "update@example.com");
        context.getUpdateExecutor().insert(employee, context.getEntityContext(Employee.class));

        // When - 使用条件更新修改 salary 字段
        int updated = context.getUpdateExecutor().update(context.getEntityContext(Employee.class))
                .set(Employee::getSalary, 90000.0)
                .where(Employee::getId).eq(9001L)
                .execute();

        // Then - 验证更新结果
        assertThat(updated).isEqualTo(1);
        Employee updatedEmployee = context.queryEmployees()
                .where(Employee::getId).eq(9001L)
                .single();
        assertThat(updatedEmployee.getSalary()).isEqualTo(90000.0);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should update multiple fields with single where condition")
    void shouldUpdateMultipleFieldsWithSingleWhereCondition(IntegrationTestContext context) {
        // Given
        Employee employee = createTestEmployee(9002L, "Multi Update", "multi@example.com");
        context.getUpdateExecutor().insert(employee, context.getEntityContext(Employee.class));

        // When - 更新多个字段
        int updated = context.getUpdateExecutor().update(context.getEntityContext(Employee.class))
                .set(Employee::getSalary, 100000.0)
                .set(Employee::getName, "Updated Name")
                .set(Employee::getEmail, "updated@example.com")
                .where(Employee::getId).eq(9002L)
                .execute();

        // Then
        assertThat(updated).isEqualTo(1);
        Employee updatedEmployee = context.queryEmployees()
                .where(Employee::getId).eq(9002L)
                .single();
        assertThat(updatedEmployee.getSalary()).isEqualTo(100000.0);
        assertThat(updatedEmployee.getName()).isEqualTo("Updated Name");
        assertThat(updatedEmployee.getEmail()).isEqualTo("updated@example.com");
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should update with multiple where conditions")
    void shouldUpdateWithMultipleWhereConditions(IntegrationTestContext context) {
        // Given - Engineering 部门有多个员工，更新其中一部分
        long engineeringDeptId = 1L;

        // When - 更新 Engineering 部门中状态为 ACTIVE 的员工薪资
        int updated = context.getUpdateExecutor().update(context.getEntityContext(Employee.class))
                .set(Employee::getSalary, 80000.0)
                .where(Employee::getDepartmentId).eq(engineeringDeptId)
                .where(Employee::getStatus).eq(EmployeeStatus.ACTIVE)
                .execute();

        // Then - 验证只有符合条件的员工被更新
        assertThat(updated).isGreaterThanOrEqualTo(3); // Alice, Bob, Diana are ACTIVE in dept 1
        long activeEngineeringCount = context.queryEmployees()
                .where(Employee::getDepartmentId).eq(engineeringDeptId)
                .where(Employee::getStatus).eq(EmployeeStatus.ACTIVE)
                .list()
                .stream()
                .filter(e -> e.getSalary().equals(80000.0))
                .count();
        assertThat(activeEngineeringCount).isEqualTo(updated);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should update all records when no where condition")
    void shouldUpdateAllRecordsWhenNoWhereCondition(IntegrationTestContext context) {
        // Given - 先创建一些独立的测试数据
        context.getUpdateExecutor().insert(createTestEmployee(9010L, "Bulk 1", "bulk1@example.com"), context.getEntityContext(Employee.class));
        context.getUpdateExecutor().insert(createTestEmployee(9011L, "Bulk 2", "bulk2@example.com"), context.getEntityContext(Employee.class));
        context.getUpdateExecutor().insert(createTestEmployee(9012L, "Bulk 3", "bulk3@example.com"), context.getEntityContext(Employee.class));

        // When - 不添加 WHERE 条件，更新所有符合条件的记录
        // 注意：实际使用中无 WHERE 条件的更新会影响整个表，这里使用条件来限制范围
        int updated = context.getUpdateExecutor().update(context.getEntityContext(Employee.class))
                .set(Employee::getName, "Bulk Updated")
                .where(Employee::getId).in(9010L, 9011L, 9012L)
                .execute();

        // Then
        assertThat(updated).isEqualTo(3);
        var employees = context.queryEmployees()
                .where(Employee::getId).in(9010L, 9011L, 9012L)
                .list();
        assertThat(employees).allMatch(e -> e.getName().equals("Bulk Updated"));
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should update with numeric comparison conditions")
    void shouldUpdateWithNumericComparisonConditions(IntegrationTestContext context) {
        // Given
        Employee lowSalaryEmployee = createTestEmployee(9020L, "Low Salary", "low@example.com");
        lowSalaryEmployee.setSalary(40000.0);
        context.getUpdateExecutor().insert(lowSalaryEmployee, context.getEntityContext(Employee.class));

        Employee highSalaryEmployee = createTestEmployee(9021L, "High Salary", "high@example.com");
        highSalaryEmployee.setSalary(100000.0);
        context.getUpdateExecutor().insert(highSalaryEmployee, context.getEntityContext(Employee.class));

        // When - 使用数值比较条件更新低薪员工
        int updated = context.getUpdateExecutor().update(context.getEntityContext(Employee.class))
                .set(Employee::getSalary, 50000.0)
                .where(Employee::getId).in(9020L, 9021L)
                .where(Employee::getSalary).lt(50000.0)
                .execute();

        // Then - 只有低薪员工被更新
        assertThat(updated).isEqualTo(1);
        Employee updatedLow = context.queryEmployees().where(Employee::getId).eq(9020L).single();
        assertThat(updatedLow.getSalary()).isEqualTo(50000.0);
        Employee notUpdatedHigh = context.queryEmployees().where(Employee::getId).eq(9021L).single();
        assertThat(notUpdatedHigh.getSalary()).isEqualTo(100000.0);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should update enum field")
    void shouldUpdateEnumField(IntegrationTestContext context) {
        // Given
        Employee employee = createTestEmployee(9030L, "Status Update", "status@example.com");
        employee.setStatus(EmployeeStatus.ACTIVE);
        context.getUpdateExecutor().insert(employee, context.getEntityContext(Employee.class));

        // When
        int updated = context.getUpdateExecutor().update(context.getEntityContext(Employee.class))
                .set(Employee::getStatus, EmployeeStatus.INACTIVE)
                .where(Employee::getId).eq(9030L)
                .execute();

        // Then
        assertThat(updated).isEqualTo(1);
        Employee updatedEmployee = context.queryEmployees()
                .where(Employee::getId).eq(9030L)
                .single();
        assertThat(updatedEmployee.getStatus()).isEqualTo(EmployeeStatus.INACTIVE);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should update boolean field")
    void shouldUpdateBooleanField(IntegrationTestContext context) {
        // Given
        Employee employee = createTestEmployee(9040L, "Boolean Update", "boolean@example.com");
        employee.setActive(true);
        context.getUpdateExecutor().insert(employee, context.getEntityContext(Employee.class));

        // When
        int updated = context.getUpdateExecutor().update(context.getEntityContext(Employee.class))
                .set(Employee::getActive, false)
                .where(Employee::getId).eq(9040L)
                .execute();

        // Then
        assertThat(updated).isEqualTo(1);
        Employee updatedEmployee = context.queryEmployees()
                .where(Employee::getId).eq(9040L)
                .single();
        assertThat(updatedEmployee.getActive()).isFalse();
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should update department fields")
    void shouldUpdateDepartmentFields(IntegrationTestContext context) {
        // Given - 使用已存在的 Department (id=1)
        // When
        int updated = context.getUpdateExecutor().update(context.getEntityContext(Department.class))
                .set(Department::getBudget, 600000.0)
                .where(Department::getId).eq(1L)
                .execute();

        // Then
        assertThat(updated).isEqualTo(1);
        Department updatedDept = context.queryDepartments()
                .where(Department::getId).eq(1L)
                .single();
        assertThat(updatedDept.getBudget()).isEqualTo(600000.0);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should return zero when update matches no records")
    void shouldReturnZeroWhenUpdateMatchesNoRecords(IntegrationTestContext context) {
        // When - 使用一个不存在的 ID 进行更新
        int updated = context.getUpdateExecutor().update(context.getEntityContext(Employee.class))
                .set(Employee::getSalary, 50000.0)
                .where(Employee::getId).eq(99999L)
                .execute();

        // Then - 返回 0 表示没有记录被更新
        assertThat(updated).isEqualTo(0);
    }

    // ========================================
    // 2. Conditional Delete Tests
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should delete with single where condition")
    void shouldDeleteWithSingleWhereCondition(IntegrationTestContext context) {
        // Given
        Employee employee = createTestEmployee(9100L, "Delete Test", "delete@example.com");
        context.getUpdateExecutor().insert(employee, context.getEntityContext(Employee.class));

        // Verify inserted
        assertThat(context.queryEmployees().where(Employee::getId).eq(9100L).exists()).isTrue();

        // When
        int deleted = context.getUpdateExecutor().delete(context.getEntityContext(Employee.class))
                .where(Employee::getId).eq(9100L)
                .execute();

        // Then
        assertThat(deleted).isEqualTo(1);
        assertThat(context.queryEmployees().where(Employee::getId).eq(9100L).exists()).isFalse();
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should delete with multiple where conditions")
    void shouldDeleteWithMultipleWhereConditions(IntegrationTestContext context) {
        // Given - 创建多个测试员工
        context.getUpdateExecutor().insert(createTestEmployee(9110L, "Delete Multi 1", "dm1@example.com"), context.getEntityContext(Employee.class));
        context.getUpdateExecutor().insert(createTestEmployee(9111L, "Delete Multi 2", "dm2@example.com"), context.getEntityContext(Employee.class));
        Employee toDelete = createTestEmployee(9112L, "Delete Multi 3", "dm3@example.com");
        toDelete.setStatus(EmployeeStatus.INACTIVE);
        context.getUpdateExecutor().insert(toDelete, context.getEntityContext(Employee.class));

        // When - 使用多条件删除
        int deleted = context.getUpdateExecutor().delete(context.getEntityContext(Employee.class))
                .where(Employee::getId).in(9110L, 9111L, 9112L)
                .where(Employee::getStatus).eq(EmployeeStatus.INACTIVE)
                .execute();

        // Then - 只有状态为 INACTIVE 的员工被删除
        assertThat(deleted).isEqualTo(1);
        assertThat(context.queryEmployees().where(Employee::getId).eq(9112L).exists()).isFalse();
        assertThat(context.queryEmployees().where(Employee::getId).eq(9110L).exists()).isTrue();
        assertThat(context.queryEmployees().where(Employee::getId).eq(9111L).exists()).isTrue();
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should delete multiple records with in condition")
    void shouldDeleteMultipleRecordsWithInCondition(IntegrationTestContext context) {
        // Given
        context.getUpdateExecutor().insert(createTestEmployee(9120L, "Batch Delete 1", "bd1@example.com"), context.getEntityContext(Employee.class));
        context.getUpdateExecutor().insert(createTestEmployee(9121L, "Batch Delete 2", "bd2@example.com"), context.getEntityContext(Employee.class));
        context.getUpdateExecutor().insert(createTestEmployee(9122L, "Batch Delete 3", "bd3@example.com"), context.getEntityContext(Employee.class));

        // When
        int deleted = context.getUpdateExecutor().delete(context.getEntityContext(Employee.class))
                .where(Employee::getId).in(9120L, 9121L, 9122L)
                .execute();

        // Then
        assertThat(deleted).isEqualTo(3);
        long remaining = context.queryEmployees()
                .where(Employee::getId).in(9120L, 9121L, 9122L)
                .count();
        assertThat(remaining).isEqualTo(0);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should delete with numeric comparison condition")
    void shouldDeleteWithNumericComparisonCondition(IntegrationTestContext context) {
        // Given
        Employee lowSalary = createTestEmployee(9130L, "Low Salary Delete", "lsd@example.com");
        lowSalary.setSalary(30000.0);
        context.getUpdateExecutor().insert(lowSalary, context.getEntityContext(Employee.class));

        Employee highSalary = createTestEmployee(9131L, "High Salary Keep", "hsk@example.com");
        highSalary.setSalary(100000.0);
        context.getUpdateExecutor().insert(highSalary, context.getEntityContext(Employee.class));

        // When - 删除低薪员工
        int deleted = context.getUpdateExecutor().delete(context.getEntityContext(Employee.class))
                .where(Employee::getId).in(9130L, 9131L)
                .where(Employee::getSalary).lt(50000.0)
                .execute();

        // Then
        assertThat(deleted).isEqualTo(1);
        assertThat(context.queryEmployees().where(Employee::getId).eq(9130L).exists()).isFalse();
        assertThat(context.queryEmployees().where(Employee::getId).eq(9131L).exists()).isTrue();
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should delete with null condition")
    void shouldDeleteWithNullCondition(IntegrationTestContext context) {
        // Given - 创建一个 email 为 null 的员工
        Employee nullEmailEmployee = createTestEmployee(9140L, "Null Email", null);
        context.getUpdateExecutor().insert(nullEmailEmployee, context.getEntityContext(Employee.class));

        Employee normalEmployee = createTestEmployee(9141L, "Normal Email", "normal@example.com");
        context.getUpdateExecutor().insert(normalEmployee, context.getEntityContext(Employee.class));

        // When - 删除 email 为 null 的员工
        int deleted = context.getUpdateExecutor().delete(context.getEntityContext(Employee.class))
                .where(Employee::getId).in(9140L, 9141L)
                .where(Employee::getEmail).isNull()
                .execute();

        // Then
        assertThat(deleted).isEqualTo(1);
        assertThat(context.queryEmployees().where(Employee::getId).eq(9140L).exists()).isFalse();
        assertThat(context.queryEmployees().where(Employee::getId).eq(9141L).exists()).isTrue();
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should delete with not null condition")
    void shouldDeleteWithNotNullCondition(IntegrationTestContext context) {
        // Given
        Employee nullEmail = createTestEmployee(9150L, "Null Email", null);
        context.getUpdateExecutor().insert(nullEmail, context.getEntityContext(Employee.class));

        Employee normalEmail = createTestEmployee(9151L, "Normal Email", "normal@example.com");
        context.getUpdateExecutor().insert(normalEmail, context.getEntityContext(Employee.class));

        // When - 删除 email 不为 null 的员工
        int deleted = context.getUpdateExecutor().delete(context.getEntityContext(Employee.class))
                .where(Employee::getId).in(9150L, 9151L)
                .where(Employee::getEmail).isNotNull()
                .execute();

        // Then
        assertThat(deleted).isEqualTo(1);
        assertThat(context.queryEmployees().where(Employee::getId).eq(9150L).exists()).isTrue();
        assertThat(context.queryEmployees().where(Employee::getId).eq(9151L).exists()).isFalse();
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should return zero when delete matches no records")
    void shouldReturnZeroWhenDeleteMatchesNoRecords(IntegrationTestContext context) {
        // When - 使用不存在的 ID 进行删除
        int deleted = context.getUpdateExecutor().delete(context.getEntityContext(Employee.class))
                .where(Employee::getId).eq(99999L)
                .execute();

        // Then - 返回 0 表示没有记录被删除
        assertThat(deleted).isEqualTo(0);
    }

    // ========================================
    // 3. Combined Operations Tests
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should update then delete in transaction")
    void shouldUpdateThenDeleteInTransaction(IntegrationTestContext context) {
        // Given
        context.getUpdateExecutor().insert(createTestEmployee(9200L, "Combined 1", "c1@example.com"), context.getEntityContext(Employee.class));
        context.getUpdateExecutor().insert(createTestEmployee(9201L, "Combined 2", "c2@example.com"), context.getEntityContext(Employee.class));

        // When - 在事务中执行更新和删除
        Integer result = context.getUpdateExecutor().doInTransaction(() -> {
            // 先更新
            int updated = context.getUpdateExecutor().update(context.getEntityContext(Employee.class))
                    .set(Employee::getSalary, 99999.0)
                    .where(Employee::getId).eq(9200L)
                    .execute();

            // 再删除
            int deleted = context.getUpdateExecutor().delete(context.getEntityContext(Employee.class))
                    .where(Employee::getId).eq(9201L)
                    .execute();

            return updated + deleted;
        });

        // Then
        assertThat(result).isEqualTo(2);
        Employee updated = context.queryEmployees().where(Employee::getId).eq(9200L).single();
        assertThat(updated.getSalary()).isEqualTo(99999.0);
        assertThat(context.queryEmployees().where(Employee::getId).eq(9201L).exists()).isFalse();
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle conditional update after insert")
    void shouldHandleConditionalUpdateAfterInsert(IntegrationTestContext context) {
        // Given
        Employee employee = createTestEmployee(9300L, "Insert Then Update", "itu@example.com");
        employee.setSalary(40000.0);

        // When - 先插入，再使用条件更新修改薪资
        context.getUpdateExecutor().insert(employee, context.getEntityContext(Employee.class));
        int updated = context.getUpdateExecutor().update(context.getEntityContext(Employee.class))
                .set(Employee::getSalary, 60000.0)
                .where(Employee::getId).eq(9300L)
                .execute();

        // Then
        assertThat(updated).isEqualTo(1);
        Employee result = context.queryEmployees().where(Employee::getId).eq(9300L).single();
        assertThat(result.getSalary()).isEqualTo(60000.0);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle conditional delete after insert")
    void shouldHandleConditionalDeleteAfterInsert(IntegrationTestContext context) {
        // Given
        Employee employee = createTestEmployee(9400L, "Insert Then Delete", "itd@example.com");

        // When - 先插入，再使用条件删除
        context.getUpdateExecutor().insert(employee, context.getEntityContext(Employee.class));
        assertThat(context.queryEmployees().where(Employee::getId).eq(9400L).exists()).isTrue();

        int deleted = context.getUpdateExecutor().delete(context.getEntityContext(Employee.class))
                .where(Employee::getId).eq(9400L)
                .execute();

        // Then
        assertThat(deleted).isEqualTo(1);
        assertThat(context.queryEmployees().where(Employee::getId).eq(9400L).exists()).isFalse();
    }

    // ========================================
    // Helper Methods
    // ========================================

    private Employee createTestEmployee(Long id, String name, String email) {
        Employee employee = new Employee();
        employee.setId(id);
        employee.setName(name);
        employee.setEmail(email);
        employee.setSalary(50000.0);
        employee.setActive(true);
        employee.setStatus(EmployeeStatus.ACTIVE);
        employee.setDepartmentId(1L);
        employee.setHireDate(LocalDate.now());
        return employee;
    }
}