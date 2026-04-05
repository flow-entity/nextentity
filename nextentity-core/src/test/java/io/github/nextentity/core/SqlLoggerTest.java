package io.github.nextentity.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import static org.assertj.core.api.Assertions.assertThat;

///
 /// 测试目标: 验证y SqlLogger is 正确 configured
 /// <p>
 /// 测试场景s:
 /// 1. Logger instance is properly initialized
 /// 2. Logger name is correct
 /// <p>
 /// Note: We do not test debug() 方法 because they are simple delegations
 /// to SLF4J Logger which is already tested by the SLF4J framework.
class SqlLoggerTest {

    @Nested
    class LoggerConfiguration {

///
         /// 测试目标: 验证y logger instance is initialized
         /// 测试场景: Access the static log field
         /// 预期结果: Logger is not null
        @Test
        void log_ShouldBeInitialized() {
            // when
            Logger logger = SqlLogger.log;

            // then
            assertThat(logger).isNotNull();
        }

///
         /// 测试目标: 验证y logger has correct name
         /// 测试场景: Get logger name
         /// 预期结果: Logger name matches expected SQL logger name
        @Test
        void log_ShouldHaveCorrectName() {
            // when
            String loggerName = SqlLogger.log.getName();

            // then
            assertThat(loggerName).isEqualTo("io.github.nextentity.sql");
        }
    }

    @Nested
    class DebugMethods {

///
         /// 测试目标: 验证y debug 方法 are callable without exceptions
         /// 测试场景: Call debug 方法 with valid parameters
         /// 预期结果: No exceptions thrown
         /// <p>
         /// Note: This test verifies the 方法 can be called safely.
         /// Actual log output depends on logging configuration and is not verified here.
        @Test
        void debug_WithValidParameters_ShouldNotThrow() {
            // when & then - methods should complete without exceptions
            Assertions.assertDoesNotThrow(() -> {
                SqlLogger.debug("test message");
                SqlLogger.debug("test message with param: {}", "value");
                SqlLogger.debug("null param: {}", null);
            });
        }
    }
}
