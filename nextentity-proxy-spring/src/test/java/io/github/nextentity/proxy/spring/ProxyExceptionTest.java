package io.github.nextentity.proxy.spring;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * ProxyException 异常测试
 *
 * 测试覆盖：
 * - 消息构造方法
 * - 消息+原因构造方法
 * - 异常继承关系
 */
class ProxyExceptionTest {

    @Test
    @DisplayName("仅消息构造方法正确设置消息")
    void messageConstructorSetsMessage() {
        String message = "Cannot proxy final class: MyClass";
        ProxyException exception = new ProxyException(message);

        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isNull();
    }

    @Test
    @DisplayName("消息+原因构造方法正确设置消息和原因")
    void messageAndCauseConstructorSetsBoth() {
        String message = "Proxy creation failed";
        Throwable cause = new RuntimeException("Underlying error");
        ProxyException exception = new ProxyException(message, cause);

        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isSameAs(cause);
    }

    @Test
    @DisplayName("ProxyException 是 RuntimeException 子类")
    void isRuntimeExceptionSubclass() {
        ProxyException exception = new ProxyException("test");

        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("异常可以被捕获并重新抛出")
    void canBeCaughtAndRethrown() {
        ProxyException original = new ProxyException("original");

        assertThatThrownBy(() -> {
            try {
                throw original;
            } catch (ProxyException e) {
                throw new IllegalStateException("wrapped", e);
            }
        })
                .isInstanceOf(IllegalStateException.class)
                .hasCauseInstanceOf(ProxyException.class);
    }
}