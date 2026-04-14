package io.github.nextentity.integration;

import io.github.nextentity.api.ExtensionRegistry;
import io.github.nextentity.integration.config.IntegrationTestContext;
import io.github.nextentity.integration.config.IntegrationTestProvider;
import io.github.nextentity.integration.dto.EmployeeWithLazyReference;
import io.github.nextentity.integration.entity.Department;
import io.github.nextentity.integration.entity.Employee;
import io.github.nextentity.plugin.DefaultExtensionRegistry;
import io.github.nextentity.plugin.EntityReferencePlugin;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/// EntityReference 延迟加载完整集成测试。
///
/// 验证：
/// 1. 查询投影类中的 EntityReference 字段
/// 2. 查询时只加载 ID，不触发 SQL 查询完整实体
/// 3. 调用 get() 时触发延迟加载
/// 4. 批量加载优化
///
/// @author HuangChengwei
/// @since 2.2.0
@DisplayName("EntityReference Lazy Loading Integration Tests")
public class EntityReferenceLazyLoadingIntegrationTest {

    /// 测试目标: 验证 EntityReference 字段正确映射 ID
    /// 测试场景: 查询投影类包含 EntityReference 字段
    /// 预期结果: EntityReference.getId() 返回正确的 ID 值
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should load EntityReference with correct ID")
    void shouldLoadEntityReferenceWithCorrectId(IntegrationTestContext context) {
        // Given - 设置 ExtensionRegistry
        ExtensionRegistry registry = createExtensionRegistry(context);

        // When - 查询包含 EntityReference 的投影
        List<EmployeeWithLazyReference> results = context.queryEmployees()
                .select(EmployeeWithLazyReference.class)
                .where(Employee::getId).eq(1L)
                .list();

        // Then
        assertThat(results).hasSize(1);
        EmployeeWithLazyReference ref = results.get(0);

        // 验证基础字段
        assertThat(ref.getId()).isEqualTo(1L);
        assertThat(ref.getName()).isEqualTo("Alice Johnson");

        // 验证 EntityReference ID 已加载
        assertThat(ref.getDepartment()).isNotNull();
        assertThat(ref.getDepartment().getId()).isEqualTo(ref.getDepartmentId());

        // 验证延迟加载尚未触发
        assertThat(ref.getDepartment().isLoaded()).isFalse();
    }

    /// 测试目标: 验证延迟加载正确获取实体
    /// 测试场景: EntityReference.isLoaded() = false，调用 get() 后触发加载
    /// 预期结果: get() 返回完整实体，isLoaded() = true
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should lazy load entity on get() call")
    void shouldLazyLoadEntityOnGetCall(IntegrationTestContext context) {
        // Given
        ExtensionRegistry registry = createExtensionRegistry(context);

        // When - 查询
        List<EmployeeWithLazyReference> results = context.queryEmployees()
                .select(EmployeeWithLazyReference.class)
                .where(Employee::getId).eq(1L)
                .list();

        assertThat(results).hasSize(1);
        EmployeeWithLazyReference ref = results.get(0);

        // 验证延迟加载尚未触发
        assertThat(ref.getDepartment().isLoaded()).isFalse();

        // When - 触发延迟加载
        Department dept = ref.getDepartment().get();

        // Then - 验证实体已加载
        assertThat(dept).isNotNull();
        assertThat(ref.getDepartment().isLoaded()).isTrue();

        // 验证实体内容
        assertThat(dept.getId()).isEqualTo(ref.getDepartmentId());
        assertThat(dept.getName()).isNotNull();
    }

    /// 测试目标: 验证多个 EntityReference 的批量查询
    /// 测试场景: 查询多条记录，每条都有 EntityReference
    /// 预期结果: 所有 EntityReference 的 ID 正确设置
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle multiple EntityReferences in batch query")
    void shouldHandleMultipleEntityReferencesInBatch(IntegrationTestContext context) {
        // Given
        ExtensionRegistry registry = createExtensionRegistry(context);

        // When - 批量查询
        List<EmployeeWithLazyReference> results = context.queryEmployees()
                .select(EmployeeWithLazyReference.class)
                .where(Employee::getActive).eq(true)
                .orderBy(Employee::getId).asc()
                .list(0, 10);

        // Then - 验证所有记录的 EntityReference
        assertThat(results).isNotEmpty();
        for (EmployeeWithLazyReference emp : results) {
            // 验证 DepartmentRef
            if (emp.getDepartmentId() != null) {
                assertThat(emp.getDepartment()).isNotNull();
                assertThat(emp.getDepartment().getId()).isEqualTo(emp.getDepartmentId());
                assertThat(emp.getDepartment().isLoaded()).isFalse();
            }
        }
    }

    /// 测试目标: 验证 @ReferenceId 注解指定的 ID 来源
    /// 测试场景: manager 字段使用 @ReferenceId("managerId")
    /// 预期结果: manager.getId() 返回 managerId 的值
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should resolve ID from annotated field")
    void shouldResolveIdFromAnnotatedField(IntegrationTestContext context) {
        // Given
        ExtensionRegistry registry = createExtensionRegistry(context);

        // When - 查询有 managerId 的员工
        List<EmployeeWithLazyReference> results = context.queryEmployees()
                .select(EmployeeWithLazyReference.class)
                .where(Employee::getId).eq(3L) // Carol Davis has managerId = 1
                .list();

        // Then
        assertThat(results).hasSize(1);
        EmployeeWithLazyReference emp = results.get(0);

        // 验证 managerId 注解生效
        if (emp.getManagerId() != null) {
            assertThat(emp.getManager()).isNotNull();
            assertThat(emp.getManager().getId()).isEqualTo(emp.getManagerId());
        }
    }

    /// 测试目标: 验证 null ID 的 EntityReference 处理
    /// 测试场景: departmentId 为 null 时
    /// 预期结果: EntityReference 为 null 或 getId() = null
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle null ID correctly")
    void shouldHandleNullIdCorrectly(IntegrationTestContext context) {
        // Given
        ExtensionRegistry registry = createExtensionRegistry(context);

        // When - 查询可能没有 departmentId 的员工
        List<EmployeeWithLazyReference> results = context.queryEmployees()
                .select(EmployeeWithLazyReference.class)
                .orderBy(Employee::getId).asc()
                .list();

        // Then - 验证 null ID 处理
        for (EmployeeWithLazyReference emp : results) {
            if (emp.getDepartmentId() == null) {
                // null ID 时 EntityReference 应为 null 或 getId() = null
                assertThat(emp.getDepartment() == null || emp.getDepartment().getId() == null).isTrue();
            }
        }
    }

    // Helper methods
    private ExtensionRegistry createExtensionRegistry(IntegrationTestContext context) {
        DefaultExtensionRegistry registry = new DefaultExtensionRegistry();
        registry.registerHandler(new EntityReferencePlugin());
        // EntityFetcher 由 QueryContext 中的 ExtensionRegistry 提供
        return registry;
    }
}