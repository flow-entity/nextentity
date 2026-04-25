package io.github.nextentity.integration.fast;

import io.github.nextentity.integration.config.IntegrationTestContext;
import io.github.nextentity.integration.dto.EmployeeWithLazyDepartment;
import io.github.nextentity.integration.dto.EmployeeWithLazyDepartmentNoId;
import io.github.nextentity.integration.dto.EmployeeWithLazyIDepartment;
import io.github.nextentity.integration.entity.Department;
import io.github.nextentity.integration.entity.Employee;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

///
/// Projection query integration tests.
/// <p>
/// 测试s projection to different target types:
/// - JavaBean (class with default constructor and setters)
/// - Interface (dynamic proxy)
/// - Record (immutable data carrier)
/// <p>
/// These tests run against MySQL and PostgreSQL using 测试containers.
///
/// @author HuangChengwei
@DisplayName("Projection Query Integration Tests")
public class FastProjectionQueryIntegrationTest {

    @ParameterizedTest
    @ArgumentsSource(FastIntegrationTestProvider.class)
    @DisplayName("Should project with LAZY attribute - EmployeeWithLazyDepartment")
    void shouldProjectWithLazyDepartment(IntegrationTestContext context) {
        // When - 查询带 LAZY 属性的投影对象
        List<EmployeeWithLazyDepartment> results = context.queryEmployees()
                .select(EmployeeWithLazyDepartment.class)
                .where(Employee::getDepartmentId).isNotNull()
                .orderBy(Employee::getId).asc()
                .list(5);

        // Then - EAGER 属性应已加载
        assertThat(results).isNotEmpty();
        for (EmployeeWithLazyDepartment emp : results) {
            assertThat(emp.getId()).isNotNull();
            assertThat(emp.getName()).isNotNull();
            assertThat(emp.getDepartmentId()).isNotNull();
        }

        // 首次访问 LAZY 属性 - 触发批量加载
        EmployeeWithLazyDepartment first = results.getFirst();
        Department dept = first.getDepartment();

        if (dept != null) {
            assertThat(dept.getId()).isNotNull();
            assertThat(dept.getName()).isNotNull();
        }

        // 后续访问应从缓存获取
        for (EmployeeWithLazyDepartment emp : results) {
            Department d = emp.getDepartment();
            if (d != null) {
                assertThat(d.getId()).isNotNull();
            }
        }
    }


    @ParameterizedTest
    @ArgumentsSource(FastIntegrationTestProvider.class)
    @DisplayName("Should project with LAZY attribute - EmployeeWithLazyDepartment")
    void shouldProjectWithLazyIDepartment(IntegrationTestContext context) {
        // When - 查询带 LAZY 属性的投影对象
        List<EmployeeWithLazyIDepartment> results = context.queryEmployees()
                .select(EmployeeWithLazyIDepartment.class)
                .where(Employee::getDepartmentId).isNotNull()
                .orderBy(Employee::getId).asc()
                .list(5);

        // Then - EAGER 属性应已加载
        assertThat(results).isNotEmpty();
        for (EmployeeWithLazyIDepartment emp : results) {
            assertThat(emp.getId()).isNotNull();
            assertThat(emp.getName()).isNotNull();
            assertThat(emp.getDepartmentId()).isNotNull();
        }

        // 首次访问 LAZY 属性 - 触发批量加载
        EmployeeWithLazyIDepartment first = results.getFirst();
        EmployeeWithLazyIDepartment.DepartmentInfoLazy dept = first.getDepartment();

        if (dept != null) {
            assertThat(dept.getId()).isNotNull();
            assertThat(dept.getName()).isNotNull();
        }

        // 后续访问应从缓存获取
        for (EmployeeWithLazyIDepartment emp : results) {
            EmployeeWithLazyIDepartment.DepartmentInfoLazy d = emp.getDepartment();
            if (d != null) {
                assertThat(d.getId()).isNotNull();
            }
        }
    }


    @ParameterizedTest
    @ArgumentsSource(FastIntegrationTestProvider.class)
    @DisplayName("Should project with LAZY attribute - EmployeeWithLazyDepartment")
    void shouldProjectWithLazyDepartmentNoId(IntegrationTestContext context) {
        // When - 查询带 LAZY 属性的投影对象
        List<EmployeeWithLazyDepartmentNoId> results = context.queryEmployees()
                .select(EmployeeWithLazyDepartmentNoId.class)
                .where(Employee::getDepartmentId).isNotNull()
                .orderBy(Employee::getId).asc()
                .list(5);

        // Then - EAGER 属性应已加载
        assertThat(results).isNotEmpty();
        for (EmployeeWithLazyDepartmentNoId emp : results) {
            assertThat(emp.getId()).isNotNull();
            assertThat(emp.getName()).isNotNull();
            assertThat(emp.getDepartmentId()).isNotNull();
        }

        // 首次访问 LAZY 属性 - 触发批量加载
        EmployeeWithLazyDepartmentNoId first = results.getFirst();
        EmployeeWithLazyDepartmentNoId.DepartmentInfoLazy dept = first.getDepartment();

        if (dept != null) {
            assertThat(dept.getName()).isNotNull();
        }

        // 后续访问应从缓存获取
        for (EmployeeWithLazyDepartmentNoId emp : results) {
            EmployeeWithLazyDepartmentNoId.DepartmentInfoLazy d = emp.getDepartment();
            if (d != null) {
                assertThat(d.getName()).isNotNull();
            }
        }
    }
}

