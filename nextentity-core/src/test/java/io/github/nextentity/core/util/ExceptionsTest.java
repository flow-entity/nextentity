package io.github.nextentity.core.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test objective: Verify Exceptions utility class provides sneaky throw functionality
 * <p>
 * Test scenarios:
 * 1. sneakyThrow re-throws checked exceptions without declaring them
 * 2. sneakyThrow re-throws runtime exceptions
 * 3. sneakyThrow throws NPE for null throwable
 * <p>
 * Expected result: Exceptions can be thrown without being declared in method signature
 */
class ExceptionsTest {

    /**
     * Test objective: Verify sneakyThrow re-throws RuntimeException without declaring it
     * Test scenario: Use sneakyThrow to re-throw RuntimeException
     * Expected result: RuntimeException is thrown
     */
    @Test
    void sneakyThrow_WithRuntimeException_ShouldReThrow() {
        // given
        RuntimeException exception = new RuntimeException("test");

        // then
        assertThatThrownBy(() -> Exceptions.sneakyThrow(exception))
                .isSameAs(exception);
    }

    /**
     * Test objective: Verify sneakyThrow re-throws Error without declaring it
     * Test scenario: Use sneakyThrow to re-throw Error
     * Expected result: Error is thrown
     */
    @Test
    void sneakyThrow_WithError_ShouldReThrow() {
        // given
        Error error = new Error("test error");

        // then
        assertThatThrownBy(() -> Exceptions.sneakyThrow(error))
                .isSameAs(error);
    }

    /**
     * Test objective: Verify sneakyThrow throws NPE for null throwable
     * Test scenario: Pass null to sneakyThrow
     * Expected result: NullPointerException is thrown
     */
    @Test
    void sneakyThrow_WithNull_ShouldThrowNPE() {
        // then
        assertThatThrownBy(() -> Exceptions.sneakyThrow(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("throwable");
    }

    /**
     * Test objective: Verify sneakyThrow can re-throw custom RuntimeException
     * Test scenario: Use sneakyThrow to re-throw custom RuntimeException subclass
     * Expected result: Custom exception is thrown
     */
    @Test
    void sneakyThrow_WithCustomRuntimeException_ShouldReThrow() {
        // given
        class CustomRuntimeException extends RuntimeException {
            public CustomRuntimeException(String message) {
                super(message);
            }
        }
        CustomRuntimeException exception = new CustomRuntimeException("custom message");

        // then
        assertThatThrownBy(() -> Exceptions.sneakyThrow(exception))
                .isInstanceOf(CustomRuntimeException.class)
                .hasMessage("custom message");
    }

    /**
     * Test objective: Verify sneakyThrow preserves exception stack trace
     * Test scenario: Create exception and throw via sneakyThrow
     * Expected result: Original stack trace is preserved
     */
    @Test
    void sneakyThrow_ShouldPreserveStackTrace() {
        // given
        RuntimeException exception = new RuntimeException("test");
        StackTraceElement[] originalStackTrace = exception.getStackTrace();

        // when
        try {
            Exceptions.sneakyThrow(exception);
        } catch (RuntimeException e) {
            // then
            assertThat(e.getStackTrace()).isEqualTo(originalStackTrace);
        }
    }
}
