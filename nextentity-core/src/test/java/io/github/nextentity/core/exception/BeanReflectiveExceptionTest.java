package io.github.nextentity.core.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for {@link BeanReflectiveException}.
 */
@DisplayName("BeanReflectiveException Tests")
class BeanReflectiveExceptionTest {

    @Test
    @DisplayName("should create exception with message")
    void shouldCreateExceptionWithMessage() {
        BeanReflectiveException exception = new BeanReflectiveException("Test error message");

        assertThat(exception.getMessage()).isEqualTo("Test error message");
    }

    @Test
    @DisplayName("should create exception with cause")
    void shouldCreateExceptionWithCause() {
        Throwable cause = new RuntimeException("Cause");
        BeanReflectiveException exception = new BeanReflectiveException(cause);

        assertThat(exception.getMessage()).isEqualTo("java.lang.RuntimeException: Cause");
        assertThat(exception.getCause()).isEqualTo(cause);
    }
}
