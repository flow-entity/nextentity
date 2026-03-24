package io.github.nextentity.integration;

import io.github.nextentity.integration.config.DbConfig;
import io.github.nextentity.integration.config.IntegrationTestProvider;
import io.github.nextentity.integration.dto.*;
import io.github.nextentity.integration.entity.Employee;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Projection query integration tests.
 * <p>
 * Tests projection to different target types:
 * - JavaBean (class with default constructor and setters)
 * - Interface (dynamic proxy)
 * - Record (immutable data carrier)
 * <p>
 * These tests run against MySQL and PostgreSQL using Testcontainers.
 *
 * @author HuangChengwei
 */
@DisplayName("Projection Query Integration Tests")
public class ProjectionQueryIntegrationTest {

    // ========================================
    // 1. JavaBean Projection Tests
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should project to JavaBean - EmployeeBasicInfo")
    void shouldProjectToJavaBeanBasicInfo(DbConfig config) {
        // When
        List<EmployeeBasicInfo> results = config.queryEmployees()
                .select(EmployeeBasicInfo.class)
                .where(Employee::getId).eq(1L)
                .getList();

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
    void shouldProjectToJavaBeanSummary(DbConfig config) {
        // When
        List<EmployeeSummary> results = config.queryEmployees()
                .select(EmployeeSummary.class)
                .where(Employee::getActive).eq(true)
                .orderBy(Employee::getId).asc()
                .getList();

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
    void shouldProjectToJavaBeanMultipleResults(DbConfig config) {
        // When
        List<EmployeeBasicInfo> results = config.queryEmployees()
                .select(EmployeeBasicInfo.class)
                .orderBy(Employee::getId).asc()
                .getList();

        // Then
        assertThat(results).hasSize(12);
        assertThat(results.get(0).getName()).isEqualTo("Alice Johnson");
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should project to JavaBean with where condition")
    void shouldProjectToJavaBeanWithWhere(DbConfig config) {
        // When
        List<EmployeeBasicInfo> results = config.queryEmployees()
                .select(EmployeeBasicInfo.class)
                .where(Employee::getSalary).gt(70000.0)
                .orderBy(Employee::getName).asc()
                .getList();

        // Then
        assertThat(results).isNotEmpty();
        assertThat(results).allMatch(info -> info.getSalary() > 70000.0);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should project to JavaBean with pagination")
    void shouldProjectToJavaBeanWithPagination(DbConfig config) {
        // When
        List<EmployeeBasicInfo> results = config.queryEmployees()
                .select(EmployeeBasicInfo.class)
                .orderBy(Employee::getId).asc()
                .getList(0, 5);

        // Then
        assertThat(results).hasSize(5);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should return empty list when no match for JavaBean projection")
    void shouldReturnEmptyForJavaBeanNoMatch(DbConfig config) {
        // When
        List<EmployeeBasicInfo> results = config.queryEmployees()
                .select(EmployeeBasicInfo.class)
                .where(Employee::getId).eq(99999L)
                .getList();

        // Then
        assertThat(results).isEmpty();
    }

    // ========================================
    // 2. Interface Projection Tests
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should project to interface - EmployeeInfo")
    void shouldProjectToInterfaceEmployeeInfo(DbConfig config) {
        // When
        List<EmployeeInfo> results = config.queryEmployees()
                .select(EmployeeInfo.class)
                .where(Employee::getId).eq(1L)
                .getList();

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
    void shouldProjectToInterfaceNameAndSalary(DbConfig config) {
        // When
        List<EmployeeNameAndSalary> results = config.queryEmployees()
                .select(EmployeeNameAndSalary.class)
                .where(Employee::getActive).eq(true)
                .orderBy(Employee::getId).asc()
                .getList();

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
    void shouldProjectToInterfaceDepartmentInfo(DbConfig config) {
        // When
        List<DepartmentInfo> results = config.queryDepartments()
                .select(DepartmentInfo.class)
                .orderBy(io.github.nextentity.integration.entity.Department::getId).asc()
                .getList();

        // Then
        assertThat(results).hasSize(5);
        DepartmentInfo first = results.get(0);
        assertThat(first.getId()).isEqualTo(1L);
        assertThat(first.getName()).isNotNull();
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should project to interface with default method - EmployeeWithStatus")
    void shouldProjectToInterfaceWithDefaultMethod(DbConfig config) {
        // When
        List<EmployeeWithStatus> results = config.queryEmployees()
                .select(EmployeeWithStatus.class)
                .where(Employee::getId).eq(1L)
                .getList();

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
    void shouldProjectToInterfaceWithCondition(DbConfig config) {
        // When
        List<EmployeeInfo> results = config.queryEmployees()
                .select(EmployeeInfo.class)
                .where(Employee::getSalary).gt(70000.0)
                .orderBy(Employee::getName).asc()
                .getList();

        // Then
        assertThat(results).isNotEmpty();
        assertThat(results).allMatch(info -> info.getSalary() > 70000.0);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should project to interface with pagination")
    void shouldProjectToInterfaceWithPagination(DbConfig config) {
        // When
        List<EmployeeInfo> results = config.queryEmployees()
                .select(EmployeeInfo.class)
                .orderBy(Employee::getId).asc()
                .getList(0, 3);

        // Then
        assertThat(results).hasSize(3);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should get single result for interface projection")
    void shouldGetSingleForInterface(DbConfig config) {
        // When
        EmployeeInfo result = config.queryEmployees()
                .select(EmployeeInfo.class)
                .where(Employee::getId).eq(1L)
                .getSingle();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should return empty optional for interface projection first")
    void shouldReturnEmptyFirstForInterfaceNoMatch(DbConfig config) {
        // When
        var result = config.queryEmployees()
                .select(EmployeeInfo.class)
                .where(Employee::getId).eq(99999L)
                .first();

        // Then
        assertThat(result).isEmpty();
    }

    // ========================================
    // 3. Record Projection Tests
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should project to record - EmployeeRecord")
    void shouldProjectToRecordEmployeeRecord(DbConfig config) {
        // When
        List<EmployeeRecord> results = config.queryEmployees()
                .select(EmployeeRecord.class)
                .where(Employee::getId).eq(1L)
                .getList();

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
    void shouldProjectToRecordNameSalary(DbConfig config) {
        // When
        List<EmployeeNameSalary> results = config.queryEmployees()
                .select(EmployeeNameSalary.class)
                .where(Employee::getActive).eq(true)
                .orderBy(Employee::getId).asc()
                .getList();

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
    void shouldProjectToRecordDepartmentRecord(DbConfig config) {
        // When
        List<DepartmentRecord> results = config.queryDepartments()
                .select(DepartmentRecord.class)
                .orderBy(io.github.nextentity.integration.entity.Department::getId).asc()
                .getList();

        // Then
        assertThat(results).hasSize(5);
        DepartmentRecord first = results.get(0);
        assertThat(first.id()).isEqualTo(1L);
        assertThat(first.name()).isNotNull();
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should project to record - EmployeeDetailRecord")
    void shouldProjectToRecordDetail(DbConfig config) {
        // When
        List<EmployeeDetailRecord> results = config.queryEmployees()
                .select(EmployeeDetailRecord.class)
                .where(Employee::getId).eq(1L)
                .getList();

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
    void shouldProjectToRecordMultipleResults(DbConfig config) {
        // When
        List<EmployeeRecord> results = config.queryEmployees()
                .select(EmployeeRecord.class)
                .orderBy(Employee::getId).asc()
                .getList();

        // Then
        assertThat(results).hasSize(12);
        assertThat(results.get(0).name()).isEqualTo("Alice Johnson");
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should project to record with where condition")
    void shouldProjectToRecordWithWhere(DbConfig config) {
        // When
        List<EmployeeRecord> results = config.queryEmployees()
                .select(EmployeeRecord.class)
                .where(Employee::getSalary).gt(70000.0)
                .orderBy(Employee::getName).asc()
                .getList();

        // Then
        assertThat(results).isNotEmpty();
        assertThat(results).allMatch(r -> r.salary() > 70000.0);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should project to record with pagination")
    void shouldProjectToRecordWithPagination(DbConfig config) {
        // When
        List<EmployeeRecord> results = config.queryEmployees()
                .select(EmployeeRecord.class)
                .orderBy(Employee::getId).asc()
                .getList(0, 5);

        // Then
        assertThat(results).hasSize(5);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should get single result for record projection")
    void shouldGetSingleForRecord(DbConfig config) {
        // When
        EmployeeRecord result = config.queryEmployees()
                .select(EmployeeRecord.class)
                .where(Employee::getId).eq(1L)
                .getSingle();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should return empty list for record projection no match")
    void shouldReturnEmptyForRecordNoMatch(DbConfig config) {
        // When
        List<EmployeeRecord> results = config.queryEmployees()
                .select(EmployeeRecord.class)
                .where(Employee::getId).eq(99999L)
                .getList();

        // Then
        assertThat(results).isEmpty();
    }

    // ========================================
    // 4. Distinct Projection Tests
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select distinct JavaBean projection")
    void shouldSelectDistinctJavaBean(DbConfig config) {
        // When
        List<EmployeeSummary> results = config.queryEmployees()
                .selectDistinct(EmployeeSummary.class)
                .orderBy(Employee::getName).asc()
                .getList();

        // Then
        assertThat(results).hasSize(12); // All names are distinct
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select distinct interface projection")
    void shouldSelectDistinctInterface(DbConfig config) {
        // When
        List<EmployeeNameAndSalary> results = config.queryEmployees()
                .selectDistinct(EmployeeNameAndSalary.class)
                .orderBy(Employee::getName).asc()
                .getList();

        // Then
        assertThat(results).hasSize(12);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select distinct record projection")
    void shouldSelectDistinctRecord(DbConfig config) {
        // When
        List<EmployeeNameSalary> results = config.queryEmployees()
                .selectDistinct(EmployeeNameSalary.class)
                .orderBy(Employee::getName).asc()
                .getList();

        // Then
        assertThat(results).hasSize(12);
    }

    // ========================================
    // 5. Edge Cases and Error Handling
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle null values in JavaBean projection")
    void shouldHandleNullInJavaBean(DbConfig config) {
        // When
        List<EmployeeBasicInfo> results = config.queryEmployees()
                .select(EmployeeBasicInfo.class)
                .where(Employee::getId).eq(1L)
                .getList();

        // Then
        assertThat(results).hasSize(1);
        // Name should not be null
        assertThat(results.get(0).getName()).isNotNull();
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle null values in interface projection")
    void shouldHandleNullInInterface(DbConfig config) {
        // When
        List<EmployeeInfo> results = config.queryEmployees()
                .select(EmployeeInfo.class)
                .where(Employee::getId).eq(1L)
                .getList();

        // Then
        assertThat(results).hasSize(1);
        // Name should not be null
        assertThat(results.get(0).getName()).isNotNull();
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle null values in record projection")
    void shouldHandleNullInRecord(DbConfig config) {
        // When
        List<EmployeeNullableRecord> results = config.queryEmployees()
                .select(EmployeeNullableRecord.class)
                .where(Employee::getId).eq(1L)
                .getList();

        // Then
        assertThat(results).hasSize(1);
        // Name should not be null
        assertThat(results.get(0).name()).isNotNull();
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should project to interface with limit")
    void shouldProjectToInterfaceWithLimit(DbConfig config) {
        // When
        List<EmployeeInfo> results = config.queryEmployees()
                .select(EmployeeInfo.class)
                .orderBy(Employee::getId).asc()
                .limit(3);

        // Then
        assertThat(results).hasSize(3);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should project to record with limit")
    void shouldProjectToRecordWithLimit(DbConfig config) {
        // When
        List<EmployeeRecord> results = config.queryEmployees()
                .select(EmployeeRecord.class)
                .orderBy(Employee::getId).asc()
                .limit(3);

        // Then
        assertThat(results).hasSize(3);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should project JavaBean with complex condition")
    void shouldProjectJavaBeanWithComplexCondition(DbConfig config) {
        // When
        List<EmployeeBasicInfo> results = config.queryEmployees()
                .select(EmployeeBasicInfo.class)
                .where(Employee::getSalary).gt(50000.0)
                .where(Employee::getActive).eq(true)
                .orderBy(Employee::getSalary).desc()
                .getList();

        // Then
        assertThat(results).isNotEmpty();
        assertThat(results).allMatch(info -> info.getSalary() > 50000.0);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should project interface with complex condition")
    void shouldProjectInterfaceWithComplexCondition(DbConfig config) {
        // When
        List<EmployeeInfo> results = config.queryEmployees()
                .select(EmployeeInfo.class)
                .where(Employee::getSalary).gt(50000.0)
                .where(Employee::getActive).eq(true)
                .orderBy(Employee::getSalary).desc()
                .getList();

        // Then
        assertThat(results).isNotEmpty();
        assertThat(results).allMatch(info -> info.getSalary() > 50000.0);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should project record with complex condition")
    void shouldProjectRecordWithComplexCondition(DbConfig config) {
        // When
        List<EmployeeRecord> results = config.queryEmployees()
                .select(EmployeeRecord.class)
                .where(Employee::getSalary).gt(50000.0)
                .where(Employee::getActive).eq(true)
                .orderBy(Employee::getSalary).desc()
                .getList();

        // Then
        assertThat(results).isNotEmpty();
        assertThat(results).allMatch(r -> r.salary() > 50000.0);
    }
}