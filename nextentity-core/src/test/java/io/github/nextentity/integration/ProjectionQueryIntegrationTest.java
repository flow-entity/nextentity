package io.github.nextentity.integration;

import io.github.nextentity.integration.config.IntegrationTestContext;
import io.github.nextentity.integration.config.IntegrationTestProvider;
import io.github.nextentity.integration.dto.*;
import io.github.nextentity.integration.entity.Department;
import io.github.nextentity.integration.entity.Employee;
import io.github.nextentity.integration.fast.FastIntegrationTestProvider;
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
public class ProjectionQueryIntegrationTest {

    // ========================================
    // 1. JavaBean Projection Tests
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should project to JavaBean - EmployeeBasicInfo")
    void shouldProjectToJavaBeanBasicInfo(IntegrationTestContext context) {
        // When
        List<EmployeeBasicInfo> results = context.queryEmployees()
                .select(EmployeeBasicInfo.class)
                .where(Employee::getId).eq(1L)
                .list();

        // Then
        assertThat(results).hasSize(1);
        EmployeeBasicInfo info = results.get(0);
        assertThat(info.getId()).isEqualTo(1L);
        assertThat(info.getName()).isEqualTo("Alice Johnson");
        assertThat(info.getEmail()).isEqualTo("alice@example.com");
        assertThat(info.getSalary()).isNotNull();
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should project to JavaBean - EmployeeSummary")
    void shouldProjectToJavaBeanSummary(IntegrationTestContext context) {
        // When
        List<EmployeeSummary> results = context.queryEmployees()
                .select(EmployeeSummary.class)
                .where(Employee::getActive).eq(true)
                .orderBy(Employee::getId).asc()
                .list();

        // Then
        assertThat(results).isNotEmpty();
        for (EmployeeSummary summary : results) {
            assertThat(summary.getName()).isNotNull();
            assertThat(summary.getSalary()).isNotNull();
            assertThat(summary.getActive()).isTrue();
        }
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should project to JavaBean with multiple results")
    void shouldProjectToJavaBeanMultipleResults(IntegrationTestContext context) {
        // When
        List<EmployeeBasicInfo> results = context.queryEmployees()
                .select(EmployeeBasicInfo.class)
                .orderBy(Employee::getId).asc()
                .list();

        // Then
        assertThat(results).hasSize(12);
        assertThat(results.get(0).getName()).isEqualTo("Alice Johnson");
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should project to JavaBean with where condition")
    void shouldProjectToJavaBeanWithWhere(IntegrationTestContext context) {
        // When
        List<EmployeeBasicInfo> results = context.queryEmployees()
                .select(EmployeeBasicInfo.class)
                .where(Employee::getSalary).gt(70000.0)
                .orderBy(Employee::getName).asc()
                .list();

        // Then
        assertThat(results).isNotEmpty();
        assertThat(results).allMatch(info -> info.getSalary() > 70000.0);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should project to JavaBean with pagination")
    void shouldProjectToJavaBeanWithPagination(IntegrationTestContext context) {
        // When
        List<EmployeeBasicInfo> results = context.queryEmployees()
                .select(EmployeeBasicInfo.class)
                .orderBy(Employee::getId).asc()
                .list(5);

        // Then
        assertThat(results).hasSize(5);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should return empty list when no match for JavaBean projection")
    void shouldReturnEmptyForJavaBeanNoMatch(IntegrationTestContext context) {
        // When
        List<EmployeeBasicInfo> results = context.queryEmployees()
                .select(EmployeeBasicInfo.class)
                .where(Employee::getId).eq(99999L)
                .list();

        // Then
        assertThat(results).isEmpty();
    }

    // ========================================
    // 2. Interface Projection Tests
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should project to interface - EmployeeInfo")
    void shouldProjectToInterfaceEmployeeInfo(IntegrationTestContext context) {
        // When
        List<EmployeeInfo> results = context.queryEmployees()
                .select(EmployeeInfo.class)
                .where(Employee::getId).eq(1L)
                .list();

        // Then
        assertThat(results).hasSize(1);
        EmployeeInfo info = results.get(0);
        assertThat(info.getId()).isEqualTo(1L);
        assertThat(info.getName()).isEqualTo("Alice Johnson");
        assertThat(info.getEmail()).isEqualTo("alice@example.com");
        assertThat(info.getSalary()).isNotNull();
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should project to interface - EmployeeNameAndSalary")
    void shouldProjectToInterfaceNameAndSalary(IntegrationTestContext context) {
        // When
        List<EmployeeNameAndSalary> results = context.queryEmployees()
                .select(EmployeeNameAndSalary.class)
                .where(Employee::getActive).eq(true)
                .orderBy(Employee::getId).asc()
                .list();

        // Then
        assertThat(results).isNotEmpty();
        for (EmployeeNameAndSalary item : results) {
            assertThat(item.getName()).isNotNull();
            assertThat(item.getSalary()).isNotNull();
        }
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should project to interface - DepartmentInfo")
    void shouldProjectToInterfaceDepartmentInfo(IntegrationTestContext context) {
        // When
        List<DepartmentInfo> results = context.queryDepartments()
                .select(DepartmentInfo.class)
                .orderBy(io.github.nextentity.integration.entity.Department::getId).asc()
                .list();

        // Then
        assertThat(results).hasSize(5);
        DepartmentInfo first = results.get(0);
        assertThat(first.getId()).isEqualTo(1L);
        assertThat(first.getName()).isNotNull();
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should project to interface with default method - EmployeeWithStatus")
    void shouldProjectToInterfaceWithDefaultMethod(IntegrationTestContext context) {
        // When
        List<EmployeeWithStatus> results = context.queryEmployees()
                .select(EmployeeWithStatus.class)
                .where(Employee::getId).eq(1L)
                .list();

        // Then
        assertThat(results).hasSize(1);
        EmployeeWithStatus status = results.get(0);
        assertThat(status.getName()).isEqualTo("Alice Johnson");
        assertThat(status.getActive()).isTrue();
        // Verify default method works
        assertThat(status.getStatus()).isEqualTo("Active");
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should project to interface with condition")
    void shouldProjectToInterfaceWithCondition(IntegrationTestContext context) {
        // When
        List<EmployeeInfo> results = context.queryEmployees()
                .select(EmployeeInfo.class)
                .where(Employee::getSalary).gt(70000.0)
                .orderBy(Employee::getName).asc()
                .list();

        // Then
        assertThat(results).isNotEmpty();
        assertThat(results).allMatch(info -> info.getSalary() > 70000.0);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should project to interface with pagination")
    void shouldProjectToInterfaceWithPagination(IntegrationTestContext context) {
        // When
        List<EmployeeInfo> results = context.queryEmployees()
                .select(EmployeeInfo.class)
                .orderBy(Employee::getId).asc()
                .list(3);

        // Then
        assertThat(results).hasSize(3);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should get single result for interface projection")
    void shouldGetSingleForInterface(IntegrationTestContext context) {
        // When
        EmployeeInfo result = context.queryEmployees()
                .select(EmployeeInfo.class)
                .where(Employee::getId).eq(1L)
                .single();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should return empty optional for interface projection first")
    void shouldReturnEmptyFirstForInterfaceNoMatch(IntegrationTestContext context) {
        // When
        var result = context.queryEmployees()
                .select(EmployeeInfo.class)
                .where(Employee::getId).eq(99999L)
                .first();

        // Then
        assertThat(result).isNull();
    }

    // ========================================
    // 3. Record Projection Tests
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should project to record - EmployeeRecord")
    void shouldProjectToRecordEmployeeRecord(IntegrationTestContext context) {
        // When
        List<EmployeeRecord> results = context.queryEmployees()
                .select(EmployeeRecord.class)
                .where(Employee::getId).eq(1L)
                .list();

        // Then
        assertThat(results).hasSize(1);
        EmployeeRecord record = results.get(0);
        assertThat(record.id()).isEqualTo(1L);
        assertThat(record.name()).isEqualTo("Alice Johnson");
        assertThat(record.email()).isEqualTo("alice@example.com");
        assertThat(record.salary()).isNotNull();
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should project to record - EmployeeNameSalary")
    void shouldProjectToRecordNameSalary(IntegrationTestContext context) {
        // When
        List<EmployeeNameSalary> results = context.queryEmployees()
                .select(EmployeeNameSalary.class)
                .where(Employee::getActive).eq(true)
                .orderBy(Employee::getId).asc()
                .list();

        // Then
        assertThat(results).isNotEmpty();
        for (EmployeeNameSalary item : results) {
            assertThat(item.name()).isNotNull();
            assertThat(item.salary()).isNotNull();
        }
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should project to record - DepartmentRecord")
    void shouldProjectToRecordDepartmentRecord(IntegrationTestContext context) {
        // When
        List<DepartmentRecord> results = context.queryDepartments()
                .select(DepartmentRecord.class)
                .orderBy(io.github.nextentity.integration.entity.Department::getId).asc()
                .list();

        // Then
        assertThat(results).hasSize(5);
        DepartmentRecord first = results.get(0);
        assertThat(first.id()).isEqualTo(1L);
        assertThat(first.name()).isNotNull();
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should project to record - EmployeeDetailRecord")
    void shouldProjectToRecordDetail(IntegrationTestContext context) {
        // When
        List<EmployeeDetailRecord> results = context.queryEmployees()
                .select(EmployeeDetailRecord.class)
                .where(Employee::getId).eq(1L)
                .list();

        // Then
        assertThat(results).hasSize(1);
        EmployeeDetailRecord detail = results.get(0);
        assertThat(detail.id()).isEqualTo(1L);
        assertThat(detail.name()).isEqualTo("Alice Johnson");
        assertThat(detail.salary()).isNotNull();
        assertThat(detail.active()).isTrue();
        assertThat(detail.departmentId()).isEqualTo(1L);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should project to record with multiple results")
    void shouldProjectToRecordMultipleResults(IntegrationTestContext context) {
        // When
        List<EmployeeRecord> results = context.queryEmployees()
                .select(EmployeeRecord.class)
                .orderBy(Employee::getId).asc()
                .list();

        // Then
        assertThat(results).hasSize(12);
        assertThat(results.get(0).name()).isEqualTo("Alice Johnson");
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should project to record with where condition")
    void shouldProjectToRecordWithWhere(IntegrationTestContext context) {
        // When
        List<EmployeeRecord> results = context.queryEmployees()
                .select(EmployeeRecord.class)
                .where(Employee::getSalary).gt(70000.0)
                .orderBy(Employee::getName).asc()
                .list();

        // Then
        assertThat(results).isNotEmpty();
        assertThat(results).allMatch(r -> r.salary() > 70000.0);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should project to record with pagination")
    void shouldProjectToRecordWithPagination(IntegrationTestContext context) {
        // When
        List<EmployeeRecord> results = context.queryEmployees()
                .select(EmployeeRecord.class)
                .orderBy(Employee::getId).asc()
                .list(5);

        // Then
        assertThat(results).hasSize(5);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should get single result for record projection")
    void shouldGetSingleForRecord(IntegrationTestContext context) {
        // When
        EmployeeRecord result = context.queryEmployees()
                .select(EmployeeRecord.class)
                .where(Employee::getId).eq(1L)
                .single();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should return empty list for record projection no match")
    void shouldReturnEmptyForRecordNoMatch(IntegrationTestContext context) {
        // When
        List<EmployeeRecord> results = context.queryEmployees()
                .select(EmployeeRecord.class)
                .where(Employee::getId).eq(99999L)
                .list();

        // Then
        assertThat(results).isEmpty();
    }

    // ========================================
    // 4. Distinct Projection Tests
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select distinct JavaBean projection")
    void shouldSelectDistinctJavaBean(IntegrationTestContext context) {
        // When
        List<EmployeeSummary> results = context.queryEmployees()
                .selectDistinct(EmployeeSummary.class)
                .orderBy(Employee::getName).asc()
                .list();

        // Then
        assertThat(results).hasSize(12); // All names are distinct
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select distinct interface projection")
    void shouldSelectDistinctInterface(IntegrationTestContext context) {
        // When
        List<EmployeeNameAndSalary> results = context.queryEmployees()
                .selectDistinct(EmployeeNameAndSalary.class)
                .orderBy(Employee::getName).asc()
                .list();

        // Then
        assertThat(results).hasSize(12);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select distinct record projection")
    void shouldSelectDistinctRecord(IntegrationTestContext context) {
        // When
        List<EmployeeNameSalary> results = context.queryEmployees()
                .selectDistinct(EmployeeNameSalary.class)
                .orderBy(Employee::getName).asc()
                .list();

        // Then
        assertThat(results).hasSize(12);
    }

    // ========================================
    // 5. Edge Cases and Error Handling
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle null values in JavaBean projection")
    void shouldHandleNullInJavaBean(IntegrationTestContext context) {
        // When
        List<EmployeeBasicInfo> results = context.queryEmployees()
                .select(EmployeeBasicInfo.class)
                .where(Employee::getId).eq(1L)
                .list();

        // Then
        assertThat(results).hasSize(1);
        // Name should not be null
        assertThat(results.get(0).getName()).isNotNull();
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle null values in interface projection")
    void shouldHandleNullInInterface(IntegrationTestContext context) {
        // When
        List<EmployeeInfo> results = context.queryEmployees()
                .select(EmployeeInfo.class)
                .where(Employee::getId).eq(1L)
                .list();

        // Then
        assertThat(results).hasSize(1);
        // Name should not be null
        assertThat(results.get(0).getName()).isNotNull();
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle null values in record projection")
    void shouldHandleNullInRecord(IntegrationTestContext context) {
        // When
        List<EmployeeNullableRecord> results = context.queryEmployees()
                .select(EmployeeNullableRecord.class)
                .where(Employee::getId).eq(1L)
                .list();

        // Then
        assertThat(results).hasSize(1);
        // Name should not be null
        assertThat(results.get(0).name()).isNotNull();
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should project to interface with limit")
    void shouldProjectToInterfaceWithLimit(IntegrationTestContext context) {
        // When
        List<EmployeeInfo> results = context.queryEmployees()
                .select(EmployeeInfo.class)
                .orderBy(Employee::getId).asc()
                .list(3);

        // Then
        assertThat(results).hasSize(3);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should project to record with limit")
    void shouldProjectToRecordWithLimit(IntegrationTestContext context) {
        // When
        List<EmployeeRecord> results = context.queryEmployees()
                .select(EmployeeRecord.class)
                .orderBy(Employee::getId).asc()
                .list(3);

        // Then
        assertThat(results).hasSize(3);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should project JavaBean with complex condition")
    void shouldProjectJavaBeanWithComplexCondition(IntegrationTestContext context) {
        // When
        List<EmployeeBasicInfo> results = context.queryEmployees()
                .select(EmployeeBasicInfo.class)
                .where(Employee::getSalary).gt(50000.0)
                .where(Employee::getActive).eq(true)
                .orderBy(Employee::getSalary).desc()
                .list();

        // Then
        assertThat(results).isNotEmpty();
        assertThat(results).allMatch(info -> info.getSalary() > 50000.0);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should project interface with complex condition")
    void shouldProjectInterfaceWithComplexCondition(IntegrationTestContext context) {
        // When
        List<EmployeeInfo> results = context.queryEmployees()
                .select(EmployeeInfo.class)
                .where(Employee::getSalary).gt(50000.0)
                .where(Employee::getActive).eq(true)
                .orderBy(Employee::getSalary).desc()
                .list();

        // Then
        assertThat(results).isNotEmpty();
        assertThat(results).allMatch(info -> info.getSalary() > 50000.0);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should project record with complex condition")
    void shouldProjectRecordWithComplexCondition(IntegrationTestContext context) {
        // When
        List<EmployeeRecord> results = context.queryEmployees()
                .select(EmployeeRecord.class)
                .where(Employee::getSalary).gt(50000.0)
                .where(Employee::getActive).eq(true)
                .orderBy(Employee::getSalary).desc()
                .list();

        // Then
        assertThat(results).isNotEmpty();
        assertThat(results).allMatch(r -> r.salary() > 50000.0);
    }

    // ========================================
    // 6. Lazy Loading Projection Tests
    // ========================================

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

