package io.github.nextentity.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test objective: Verify SqlLogger is correctly configured
 * <p>
 * Test scenarios:
 * 1. Logger instance is properly initialized
 * 2. Logger name is correct
 * <p>
 * Note: We do not test debug() methods because they are simple delegations
 * to SLF4J Logger which is already tested by the SLF4J framework.
 */
class SqlLoggerTest {

    @Nested
    class LoggerConfiguration {

        /**
         * Test objective: Verify logger instance is initialized
         * Test scenario: Access the static log field
         * Expected result: Logger is not null
         */
        @Test
        void log_ShouldBeInitialized() {
            // when
            Logger logger = SqlLogger.log;

            // then
            assertThat(logger).isNotNull();
        }

        /**
         * Test objective: Verify logger has correct name
         * Test scenario: Get logger name
         * Expected result: Logger name matches expected SQL logger name
         */
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

        /**
         * Test objective: Verify debug methods are callable without exceptions
         * Test scenario: Call debug methods with valid parameters
         * Expected result: No exceptions thrown
         * <p>
         * Note: This test verifies the methods can be called safely.
         * Actual log output depends on logging configuration and is not verified here.
         */
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