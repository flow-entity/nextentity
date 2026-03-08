package io.github.nextentity.core.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for {@link OptimisticLockException}.
 */
@DisplayName("OptimisticLockException Tests")
class OptimisticLockExceptionTest {

    @Test
    @DisplayName("should create exception with message")
    void shouldCreateExceptionWithMessage() {
        OptimisticLockException exception = new OptimisticLockException("Version conflict detected");

        assertThat(exception.getMessage()).isEqualTo("Version conflict detected");
    }
}
