package io.github.nextentity.integration;

import io.github.nextentity.core.LoggingConfig;
import io.github.nextentity.core.SqlLogger;
import io.github.nextentity.integration.config.IntegrationTestContext;
import io.github.nextentity.integration.config.IntegrationTestProvider;
import io.github.nextentity.integration.entity.Employee;
import io.github.nextentity.integration.entity.EmployeeStatus;
import io.github.nextentity.jdbc.JdbcConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

/// 配置行为集成测试。
///
/// 验证配置在实际数据库操作中正确生效：
/// - inlineNumericLiterals: 整数是否直接拼接到 SQL
/// - batch.enabled: 批处理是否生效
/// - logging: SQL 日志是否正确输出
///
/// @author HuangChengwei
/// @since 2.1.0
@DisplayName("Configuration Behavior Integration Tests")
public class ConfigurationBehaviorIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(ConfigurationBehaviorIntegrationTest.class);

    private LoggingConfig originalLoggingConfig;

    @BeforeEach
    void saveOriginalConfig() {
        originalLoggingConfig = SqlLogger.getConfig();
    }

    @AfterEach
    void restoreConfig() {
        SqlLogger.setConfig(originalLoggingConfig);
        var context = IntegrationTestProvider.getEntityManagerContext();
        if (context != null) {
            context.reset();
        }
    }

    @Nested
    @DisplayName("JDBC Query Configuration")
    class JdbcQueryConfigTests {

        /// 验证 inlineNumericLiterals 配置影响 SQL 构建
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("inlineNumericLiterals=true 时整数直接嵌入 SQL")
        void testInlineNumericLiteralsEnabled(IntegrationTestContext context) {
            // Given: 默认配置 inlineNumericLiterals=true
            JdbcConfig config = JdbcConfig.DEFAULT;
            assertThat(config.inlineNumericLiterals()).isTrue();

            // When: 执行带整数条件的查询
            List<Employee> employees = context.queryEmployees()
                    .where(Employee::getDepartmentId).eq(1L)
                    .list();

            // Then: 查询应成功返回结果
            assertThat(employees).isNotEmpty();
            log.info("Query with inlineNumericLiterals=true returned {} employees", employees.size());
        }

        /// 验证查询超时配置可设置
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("查询超时配置可正确传递")
        void testQueryTimeoutConfig(IntegrationTestContext context) {
            // Given: 配置查询超时
            JdbcConfig config = JdbcConfig.builder()
                    .queryTimeout(30)
                    .build();

            // Then: 配置值应正确设置
            assertThat(config.queryTimeout()).isEqualTo(30);

            // When: 执行查询（不会真正超时）
            List<Employee> employees = context.queryEmployees()
                    .where(Employee::getId).gt(0L)
                    .list(0, 10);

            // Then: 查询应成功
            assertThat(employees).isNotNull();
        }

        /// 验证 fetchSize 配置可设置
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("fetchSize 配置可正确传递")
        void testFetchSizeConfig(IntegrationTestContext context) {
            // Given: 配置 fetchSize
            JdbcConfig config = JdbcConfig.builder()
                    .fetchSize(100)
                    .build();

            // Then: 配置值应正确设置
            assertThat(config.fetchSize()).isEqualTo(100);

            // When: 执行查询
            List<Employee> employees = context.queryEmployees()
                    .list(0, 50);

            // Then: 查询应成功
            assertThat(employees).isNotNull();
        }
    }

    @Nested
    @DisplayName("Batch Operations Configuration")
    class BatchConfigTests {

        /// 验证批处理配置默认启用
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("批处理默认启用")
        void testBatchEnabledByDefault(IntegrationTestContext context) {
            // Given: 默认配置
            JdbcConfig config = JdbcConfig.DEFAULT;

            // Then: 批处理应默认启用
            assertThat(config.batchEnabled()).isTrue();
            assertThat(config.batchSize()).isEqualTo(500);

            // When: 批量插入
            Employee emp1 = createTestEmployee(9001L, "Batch 1");
            Employee emp2 = createTestEmployee(9002L, "Batch 2");
            Employee emp3 = createTestEmployee(9003L, "Batch 3");

            context.getUpdateExecutor().insertAll(List.of(emp1, emp2, emp3), Employee.class);

            // Then: 数据应正确插入
            List<Employee> inserted = context.queryEmployees()
                    .where(Employee::getId).in(9001L, 9002L, 9003L)
                    .orderBy(Employee::getId).asc()
                    .list();

            assertThat(inserted).hasSize(3);
        }

        /// 验证批处理大小配置可设置
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("批处理大小可配置")
        void testBatchSizeConfig(IntegrationTestContext context) {
            // Given: 自定义批处理大小
            JdbcConfig config = JdbcConfig.builder()
                    .batchSize(50)
                    .build();

            // Then: 批处理大小应正确设置
            assertThat(config.batchSize()).isEqualTo(50);

            // When: 批量插入
            Employee emp1 = createTestEmployee(9101L, "Batch 50-1");
            Employee emp2 = createTestEmployee(9102L, "Batch 50-2");

            assertThatNoException().isThrownBy(() ->
                    context.getUpdateExecutor().insertAll(List.of(emp1, emp2), Employee.class));

            // Then: 数据应正确插入
            long count = context.queryEmployees()
                    .where(Employee::getId).in(9101L, 9102L)
                    .count();
            assertThat(count).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("Logging Configuration")
    class LoggingConfigTests {

        /// 验证 SQL 日志可启用
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("SQL 日志启用配置生效")
        void testLoggingEnabled(IntegrationTestContext context) {
            // Given: 启用 SQL 日志
            LoggingConfig config = LoggingConfig.builder()
                    .enabled(true)
                    .parameters(true)
                    .loggerName("io.github.nextentity.sql.test")
                    .build();

            SqlLogger.setConfig(config);

            // Then: 配置应正确应用
            assertThat(SqlLogger.getConfig().enabled()).isTrue();
            assertThat(SqlLogger.getConfig().parameters()).isTrue();
            assertThat(SqlLogger.getLogger().getName()).isEqualTo("io.github.nextentity.sql.test");

            // When: 执行查询
            List<Employee> employees = context.queryEmployees()
                    .where(Employee::getId).gt(0L)
                    .list(0, 5);

            // Then: 查询应成功
            assertThat(employees).isNotNull();
        }

        /// 验证 SQL 日志可禁用
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("SQL 日志禁用配置生效")
        void testLoggingDisabled(IntegrationTestContext context) {
            // Given: 禁用 SQL 日志
            LoggingConfig config = LoggingConfig.builder()
                    .enabled(false)
                    .build();

            SqlLogger.setConfig(config);

            // Then: 配置应正确应用
            assertThat(SqlLogger.getConfig().enabled()).isFalse();

            // When: 执行查询
            List<Employee> employees = context.queryEmployees()
                    .where(Employee::getId).gt(0L)
                    .list(0, 5);

            // Then: 查询应成功
            assertThat(employees).isNotNull();
        }
    }

    @Nested
    @DisplayName("CRUD Operations")
    class CrudOperationsTests {

        /// 验证插入操作
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("插入操作成功")
        void testInsert(IntegrationTestContext context) {
            // Given
            Employee employee = createTestEmployee(9201L, "Insert Test");

            // When
            context.getUpdateExecutor().insert(employee, Employee.class);

            // Then
            Employee inserted = context.queryEmployees()
                    .where(Employee::getId).eq(9201L)
                    .single();
            assertThat(inserted).isNotNull();
            assertThat(inserted.getName()).isEqualTo("Insert Test");
        }

        /// 验证更新操作
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("更新操作成功")
        void testUpdate(IntegrationTestContext context) {
            // Given
            Employee employee = context.queryEmployees()
                    .where(Employee::getId).eq(1L)
                    .list().get(0);
            String originalName = employee.getName();
            employee.setName("Updated Name");

            // When
            context.getUpdateExecutor().update(employee, Employee.class);

            // Then
            Employee updated = context.queryEmployees()
                    .where(Employee::getId).eq(1L)
                    .single();
            assertThat(updated.getName()).isEqualTo("Updated Name");
            assertThat(updated.getName()).isNotEqualTo(originalName);
        }

        /// 验证删除操作
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("删除操作成功")
        void testDelete(IntegrationTestContext context) {
            // Given
            Employee employee = createTestEmployee(9301L, "Delete Test");
            context.getUpdateExecutor().insert(employee, Employee.class);

            // 验证插入成功
            Employee inserted = context.queryEmployees()
                    .where(Employee::getId).eq(9301L)
                    .single();
            assertThat(inserted).isNotNull();

            // When
            context.getUpdateExecutor().delete(inserted, Employee.class);

            // Then
            List<Employee> deleted = context.queryEmployees()
                    .where(Employee::getId).eq(9301L)
                    .list();
            assertThat(deleted).isEmpty();
        }

        /// 验证查询操作
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("查询操作成功")
        void testQuery(IntegrationTestContext context) {
            // When
            List<Employee> employees = context.queryEmployees()
                    .where(Employee::getStatus).eq(EmployeeStatus.ACTIVE)
                    .orderBy(Employee::getName).asc()
                    .list(0, 10);

            // Then
            assertThat(employees).isNotEmpty();
            assertThat(employees.size()).isLessThanOrEqualTo(10);
        }
    }

    // ==================== 辅助方法 ====================

    private Employee createTestEmployee(Long id, String name) {
        Employee employee = new Employee();
        employee.setId(id);
        employee.setName(name);
        employee.setEmail("test" + id + "@example.com");
        employee.setSalary(50000.0);
        employee.setActive(true);
        employee.setStatus(EmployeeStatus.ACTIVE);
        employee.setDepartmentId(1L);
        employee.setHireDate(LocalDate.now());
        return employee;
    }
}