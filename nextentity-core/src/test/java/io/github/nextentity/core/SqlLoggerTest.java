package io.github.nextentity.core;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test objective: Verify SqlLogger provides correct logger configuration
 * <p>
 * Test scenarios:
 * 1. Logger exists
 * 2. Debug methods exist
 */
class SqlLoggerTest {

    @Test
    void logger_Exists() {
        // then
        assertThat(SqlLogger.log).isNotNull();
    }

    @Test
    void debug_MethodsDoNotThrow() {
        // when & then - should not throw
        SqlLogger.debug("test message");
        SqlLogger.debug("test message with param: {}", "value");
    }
}
