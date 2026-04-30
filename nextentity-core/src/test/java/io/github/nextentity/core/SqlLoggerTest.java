package io.github.nextentity.core;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import static org.assertj.core.api.Assertions.assertThat;

/// 测试目标: 验证 SqlLogger 正确配置
///
/// 测试场景:
/// 1. Logger 实例正确初始化
/// 2. Logger 名称正确
/// 3. 配置可以动态更新
///
/// Note: 我们不测试 debug() 方法，因为它们是 SLF4J Logger 的简单代理，该框架已测试过。
class SqlLoggerTest {

    @Nested
    class LoggerConfiguration {

        /// 测试目标: 验证 logger 实例已初始化
        /// 测试场景: 访问 logger
        /// 预期结果: Logger 不为 null
        @Test
        void log_ShouldBeInitialized() {
            // when
            Logger logger = SqlLogger.getLogger();

            // then
            assertThat(logger).isNotNull();
        }

        /// 测试目标: 验证 logger 名称正确
        /// 测试场景: 获取 logger 名称
        /// 预期结果: Logger 名称匹配预期的 SQL logger 名称
        @Test
        void log_ShouldHaveCorrectName() {
            // when
            String loggerName = SqlLogger.getLogger().getName();

            // then
            assertThat(loggerName).isEqualTo("io.github.nextentity.sql");
        }
    }

    @Nested
    class ConfigTests {

        /// 测试目标: 验证默认配置
        /// 测试场景: 获取当前配置
        /// 预期结果: 配置值正确
        @Test
        void defaultConfig_ShouldBeCorrect() {
            // when
            LoggingConfig config = SqlLogger.getConfig();

            // then
            assertThat(config.enabled()).isTrue();
            assertThat(config.parameters()).isTrue();
            assertThat(config.loggerName()).isEqualTo("io.github.nextentity.sql");
        }

        /// 测试目标: 验证配置可以更新
        /// 测试场景: 设置新配置
        /// 预期结果: 配置更新成功
        @Test
        void setConfig_ShouldUpdateConfig() {
            // given
            LoggingConfig newConfig = LoggingConfig.builder()
                    .enabled(false)
                    .parameters(false)
                    .loggerName("custom.logger")
                    .build();

            // when
            SqlLogger.setConfig(newConfig);

            // then
            LoggingConfig config = SqlLogger.getConfig();
            assertThat(config.enabled()).isFalse();
            assertThat(config.parameters()).isFalse();
            assertThat(config.loggerName()).isEqualTo("custom.logger");

            // cleanup - 恢复默认配置
            SqlLogger.setConfig(LoggingConfig.DEFAULT);
        }

        /// 测试目标: 验证配置更新后 logger 名称也更新
        /// 测试场景: 设置新配置并获取 logger
        /// 预期结果: Logger 名称与新配置匹配
        @Test
        void setConfig_ShouldUpdateLoggerName() {
            // given
            LoggingConfig newConfig = LoggingConfig.builder()
                    .loggerName("test.logger")
                    .build();

            // when
            SqlLogger.setConfig(newConfig);

            // then
            assertThat(SqlLogger.getLogger().getName()).isEqualTo("test.logger");

            // cleanup
            SqlLogger.setConfig(LoggingConfig.DEFAULT);
        }
    }

}