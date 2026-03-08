package io.github.nextentity.core.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for {@link UncheckedSQLException}.
 */
@DisplayName("UncheckedSQLException Tests")
class UncheckedSQLExceptionTest {

    @Test
    @DisplayName("should create exception with no arguments")
    void shouldCreateExceptionWithNoArguments() {
        UncheckedSQLException exception = new UncheckedSQLException();

        assertThat(exception.getMessage()).isNull();
        assertThat(exception.getCause()).isNull();
    }

    @Test
    @DisplayName("should create exception with message")
    void shouldCreateExceptionWithMessage() {
        UncheckedSQLException exception = new UncheckedSQLException("Test error message");

        assertThat(exception.getMessage()).isEqualTo("Test error message");
        assertThat(exception.getCause()).isNull();
    }

    @Test
    @DisplayName("should create exception with cause")
    void shouldCreateExceptionWithCause() {
        Throwable cause = new RuntimeException("Cause");
        UncheckedSQLException exception = new UncheckedSQLException(cause);

        assertThat(exception.getMessage()).isEqualTo("java.lang.RuntimeException: Cause");
        assertThat(exception.getCause()).isEqualTo(cause);
    }

    @Test
    @DisplayName("should create exception with message and cause")
    void shouldCreateExceptionWithMessageAndCause() {
        Throwable cause = new RuntimeException("Cause");
        UncheckedSQLException exception = new UncheckedSQLException("Test error", cause);

        assertThat(exception.getMessage()).isEqualTo("Test error");
        assertThat(exception.getCause()).isEqualTo(cause);
    }
}
