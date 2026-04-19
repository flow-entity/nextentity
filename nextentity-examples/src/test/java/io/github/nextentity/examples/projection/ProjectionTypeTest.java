package io.github.nextentity.examples.projection;

import io.github.nextentity.examples.entity.Department;
import io.github.nextentity.examples.entity.Employee;
import io.github.nextentity.examples.integration.BaseIntegrationTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/// 投影类型测试 - Interface vs Class 投影对比
///
/// 测试覆盖：
/// - Interface 投影（JDK Proxy）
/// - Class 投影（CGLIB Proxy）
/// - 延迟加载功能
@DisplayName("Projection Type Tests - Interface vs Class")
class ProjectionTypeTest extends BaseIntegrationTest {

    // ==================== Interface 投影测试 ====================

    @Nested
    @DisplayName("Interface Projection Tests (JDK Proxy)")
    class InterfaceProjectionTests {

        @Test
        @DisplayName("Interface 投影使用 JDK Proxy 创建代理")
        void interfaceProjectionUsesJdkProxy() {
            // When
            List<EmployeeInfoInterface> results = employeeRepository.query()
                    .select(EmployeeInfoInterface.class)
                    .where(Employee::getActive).eq(true)
                    .list();

            // Then
            assertThat(results).isNotEmpty();

            // 验证代理类型
            Object firstResult = results.get(0);
            assertThat(Proxy.isProxyClass(firstResult.getClass())).isTrue();
            assertThat(firstResult).isInstanceOf(EmployeeInfoInterface.class);

            // 验证基本属性
            assertThat(firstResult).extracting("id", "name")
                    .doesNotContainNull();
        }

        @Test
        @DisplayName("Interface 投影延迟加载正常工作")
        void interfaceProjectionLazyLoadingWorks() {
            // When
            List<EmployeeInfoInterface> results = employeeRepository.query()
                    .select(EmployeeInfoInterface.class)
                    .where(Employee::getActive).eq(true)
                    .list();

            // Then - 验证基本属性
            assertThat(results).isNotEmpty();

            EmployeeInfoInterface first = results.get(0);
            assertThat(first.getId()).isNotNull();
            assertThat(first.getName()).isNotNull();

            // 验证延迟加载属性（首次访问触发批量加载）
            Department dept = first.getDepartment();
            if (dept != null) {
                assertThat(dept.getId()).isNotNull();
                assertThat(dept.getName()).isNotNull();
            }
        }
    }

    // ==================== Class 投影测试 ====================

    @Nested
    @DisplayName("Class Projection Tests (CGLIB Proxy)")
    class ClassProjectionTests {

        @Test
        @Disabled("BUG: JpaArguments 数组越界 - 框架 bug 待修复")
        @DisplayName("Class 投影使用 CGLIB 创建代理")
        void classProjectionUsesCglibProxy() {
            // When
            List<EmployeeInfoClass> results = employeeRepository.query()
                    .select(EmployeeInfoClass.class)
                    .where(Employee::getActive).eq(true)
                    .list();

            // Then
            assertThat(results).isNotEmpty();

            // 验证代理类型（类名包含 CGLIB 标识）
            Object firstResult = results.get(0);
            assertThat(firstResult.getClass().getName()).contains("$$EnhancerByCGLIB$$");
            assertThat(firstResult).isInstanceOf(EmployeeInfoClass.class);

            // 验证基本属性
            assertThat(firstResult).extracting("id", "name")
                    .doesNotContainNull();
        }

        @Test
        @Disabled("BUG: JpaArguments 数组越界 - 框架 bug 待修复")
        @DisplayName("Class 投影延迟加载正常工作")
        void classProjectionLazyLoadingWorks() {
            // When
            List<EmployeeInfoClass> results = employeeRepository.query()
                    .select(EmployeeInfoClass.class)
                    .where(Employee::getActive).eq(true)
                    .list();

            // Then - 验证基本属性
            assertThat(results).isNotEmpty();

            EmployeeInfoClass first = results.get(0);
            assertThat(first.getId()).isNotNull();
            assertThat(first.getName()).isNotNull();

            // 验证延迟加载属性
            Department dept = first.getDepartment();
            if (dept != null) {
                assertThat(dept.getId()).isNotNull();
                assertThat(dept.getName()).isNotNull();
            }
        }
    }
}