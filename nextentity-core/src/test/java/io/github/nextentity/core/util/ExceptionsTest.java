package io.github.nextentity.core.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

///
 /// 测试目标: 验证y Exceptions utility class provides sneaky throw functionality
 /// <p>
 /// 测试场景s:
 /// 1. sneakyThrow re-throws checked exceptions without declaring them
 /// 2. sneakyThrow re-throws runtime exceptions
 /// 3. sneakyThrow throws NPE for null throwable
 /// <p>
 /// 预期结果: Exceptions can be thrown without being declared in 方法 signature
class ExceptionsTest {

///
     /// 测试目标: 验证y sneakyThrow re-throws RuntimeException without declaring it
     /// 测试场景: Use sneakyThrow to re-throw RuntimeException
     /// 预期结果: RuntimeException is thrown
    @Test
    void sneakyThrow_WithRuntimeException_ShouldReThrow() {
        // given
        RuntimeException exception = new RuntimeException("test");

        // then
        assertThatThrownBy(() -> Exceptions.sneakyThrow(exception))
                .isSameAs(exception);
    }

///
     /// 测试目标: 验证y sneakyThrow re-throws Error without declaring it
     /// 测试场景: Use sneakyThrow to re-throw Error
     /// 预期结果: Error is thrown
    @Test
    void sneakyThrow_WithError_ShouldReThrow() {
        // given
        Error error = new Error("test error");

        // then
        assertThatThrownBy(() -> Exceptions.sneakyThrow(error))
                .isSameAs(error);
    }

///
     /// 测试目标: 验证y sneakyThrow throws NPE for null throwable
     /// 测试场景: Pass null to sneakyThrow
     /// 预期结果: NullPointerException is thrown
    @Test
    void sneakyThrow_WithNull_ShouldThrowNPE() {
        // then
        assertThatThrownBy(() -> Exceptions.sneakyThrow(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("throwable");
    }

///
     /// 测试目标: 验证y sneakyThrow can re-throw custom RuntimeException
     /// 测试场景: Use sneakyThrow to re-throw custom RuntimeException subclass
     /// 预期结果: Custom exception is thrown
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

///
     /// 测试目标: 验证y sneakyThrow preserves exception stack trace
     /// 测试场景: Create exception and throw via sneakyThrow
     /// 预期结果: Original stack trace is preserved
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
