package io.github.nextentity.spring.config;

import io.github.nextentity.core.LoggingConfig;
import io.github.nextentity.core.SqlLogger;
import io.github.nextentity.jdbc.JdbcConfig;
import io.github.nextentity.jdbc.SqlDialect;
import io.github.nextentity.jpa.JpaConfig;
import io.github.nextentity.spring.JdbcProperties;
import io.github.nextentity.spring.JpaProperties;
import io.github.nextentity.spring.LoggingProperties;
import io.github.nextentity.spring.NextEntityProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/// NextEntity 配置集成测试。
///
/// 验证配置属性从 Spring Boot 配置文件正确加载，
/// 并验证配置在实际行为中正确生效。
///
/// 测试分类:
/// 1. ConfigurationLoadingTests - 验证配置值正确加载
/// 2. ConfigurationConversionTests - 验证配置转换正确
/// 3. BehaviorVerificationTests - 验证配置行为生效
///
/// 注意: inlineNumericLiterals 的详细行为测试在
/// nextentity-core 的 InlineNumericLiteralsTest.java 中。
///
/// @author HuangChengwei
/// @since 2.1.0
@SpringBootTest(classes = ConfigTestApplication.class)
@ActiveProfiles("config-test")
class ConfigIntegrationTest {

    @Autowired
    private NextEntityProperties properties;

    @AfterEach
    void resetLoggingConfig() {
        SqlLogger.setConfig(LoggingConfig.DEFAULT);
    }

    // ==================== 配置加载测试 ====================

    @Nested
    class ConfigurationLoadingTests {

        @Test
        void testPropertiesLoaded() {
            assertNotNull(properties);
            assertNotNull(properties.getJdbc());
            assertNotNull(properties.getJpa());
            assertNotNull(properties.getLogging());
        }

        @Test
        void testJdbcPropertiesLoaded() {
            var jdbc = properties.getJdbc();

            assertEquals("io.github.nextentity.jdbc.MySqlDialect", jdbc.getDialect());
            assertEquals(30, jdbc.getQuery().getTimeout());
            assertEquals(100, jdbc.getQuery().getFetchSize());
            assertTrue(jdbc.getQuery().isInlineNumericLiterals());
            assertTrue(jdbc.getBatch().isEnabled());
            assertEquals(100, jdbc.getBatch().getSize());
            assertTrue(jdbc.getInsert().isReturnGeneratedKeys());
        }

        @Test
        void testJpaPropertiesLoaded() {
            var jpa = properties.getJpa();

            assertTrue(jpa.isStringParameterBinding());
            assertTrue(jpa.isNativeSubqueries());
        }

        @Test
        void testLoggingPropertiesLoaded() {
            var logging = properties.getLogging();

            assertTrue(logging.getSql().isEnabled());
            assertTrue(logging.getSql().isParameters());
            assertEquals("io.github.nextentity.sql", logging.getSql().getLoggerName());
        }
    }

    // ==================== 配置转换测试 ====================

    @Nested
    class ConfigurationConversionTests {

        @Test
        void testJdbcConfigConversion() {
            JdbcConfig jdbcConfig = toJdbcConfig(properties.getJdbc());

            assertEquals(30, jdbcConfig.queryTimeout());
            assertEquals(100, jdbcConfig.fetchSize());
            assertTrue(jdbcConfig.inlineNumericLiterals());
            assertTrue(jdbcConfig.batchEnabled());
            assertEquals(100, jdbcConfig.batchSize());
            assertTrue(jdbcConfig.returnGeneratedKeys());
        }

        @Test
        void testJpaConfigConversion() {
            JpaConfig jpaConfig = toJpaConfig(properties.getJpa());

            assertTrue(jpaConfig.stringParameterBinding());
            assertTrue(jpaConfig.nativeSubqueries());
        }

        @Test
        void testLoggingConfigConversion() {
            LoggingConfig loggingConfig = toLoggingConfig(properties.getLogging());

            assertTrue(loggingConfig.enabled());
            assertTrue(loggingConfig.parameters());
            assertEquals("io.github.nextentity.sql", loggingConfig.loggerName());
        }
    }

    // ==================== 行为验证测试 ====================

    @Nested
    class BehaviorVerificationTests {

        /// 验证 SqlLogger 配置正确应用
        @Test
        void testLoggingConfigApplied() {
            LoggingConfig customConfig = LoggingConfig.builder()
                    .enabled(false)
                    .parameters(false)
                    .loggerName("custom.test.logger")
                    .build();

            SqlLogger.setConfig(customConfig);

            assertEquals("custom.test.logger", SqlLogger.getLogger().getName());
            assertFalse(SqlLogger.getConfig().enabled());
            assertFalse(SqlLogger.getConfig().parameters());
        }

        /// 验证默认配置值
        @Test
        void testDefaultConfigValues() {
            JdbcConfig defaultConfig = JdbcConfig.DEFAULT;
            JpaConfig defaultJpaConfig = JpaConfig.DEFAULT;
            LoggingConfig defaultLoggingConfig = LoggingConfig.DEFAULT;

            // JDBC 默认值
            assertNull(defaultConfig.queryTimeout());
            assertEquals(0, defaultConfig.fetchSize());
            assertTrue(defaultConfig.inlineNumericLiterals());
            assertTrue(defaultConfig.batchEnabled());
            assertEquals(500, defaultConfig.batchSize());
            assertTrue(defaultConfig.returnGeneratedKeys());

            // JPA 默认值
            assertTrue(defaultJpaConfig.stringParameterBinding());
            assertTrue(defaultJpaConfig.nativeSubqueries());

            // Logging 默认值
            assertTrue(defaultLoggingConfig.enabled());
            assertTrue(defaultLoggingConfig.parameters());
            assertEquals("io.github.nextentity.sql", defaultLoggingConfig.loggerName());
        }

        /// 验证方言可以实例化
        @Test
        void testDialectInstantiation() {
            String dialectName = properties.getJdbc().getDialect();
            assertNotNull(dialectName);

            SqlDialect dialect = instantiateDialect(dialectName);
            assertNotNull(dialect);
            assertNotNull(dialect.leftQuotedIdentifier());
            assertNotNull(dialect.rightQuotedIdentifier());
        }

        /// 验证 Spring 配置转换为核心配置后行为一致
        @Test
        void testConfigConversionConsistency() {
            // 从 Spring 配置转换
            JdbcConfig fromSpring = toJdbcConfig(properties.getJdbc());

            // 直接创建相同配置
            JdbcConfig direct = JdbcConfig.builder()
                    .queryTimeout(30)
                    .fetchSize(100)
                    .inlineNumericLiterals(true)
                    .batchEnabled(true)
                    .batchSize(100)
                    .returnGeneratedKeys(true)
                    .build();

            // 验证转换结果一致
            assertEquals(fromSpring.queryTimeout(), direct.queryTimeout());
            assertEquals(fromSpring.fetchSize(), direct.fetchSize());
            assertEquals(fromSpring.inlineNumericLiterals(), direct.inlineNumericLiterals());
            assertEquals(fromSpring.batchEnabled(), direct.batchEnabled());
            assertEquals(fromSpring.batchSize(), direct.batchSize());
            assertEquals(fromSpring.returnGeneratedKeys(), direct.returnGeneratedKeys());
        }
    }

    // ==================== 辅助方法 ====================

    private SqlDialect instantiateDialect(String className) {
        try {
            Class<?> clazz = Class.forName(className);
            return (SqlDialect) clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to instantiate SqlDialect: " + className, e);
        }
    }

    private JdbcConfig toJdbcConfig(JdbcProperties props) {
        return JdbcConfig.builder()
                .queryTimeout(props.getQuery().getTimeout())
                .fetchSize(props.getQuery().getFetchSize())
                .inlineNumericLiterals(props.getQuery().isInlineNumericLiterals())
                .batchEnabled(props.getBatch().isEnabled())
                .batchSize(props.getBatch().getSize())
                .returnGeneratedKeys(props.getInsert().isReturnGeneratedKeys())
                .build();
    }

    private JpaConfig toJpaConfig(JpaProperties props) {
        return JpaConfig.builder()
                .stringParameterBinding(props.isStringParameterBinding())
                .nativeSubqueries(props.isNativeSubqueries())
                .build();
    }

    private LoggingConfig toLoggingConfig(LoggingProperties props) {
        return LoggingConfig.builder()
                .enabled(props.getSql().isEnabled())
                .parameters(props.getSql().isParameters())
                .loggerName(props.getSql().getLoggerName())
                .build();
    }
}