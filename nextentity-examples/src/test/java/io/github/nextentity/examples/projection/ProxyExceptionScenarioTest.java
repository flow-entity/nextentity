package io.github.nextentity.examples.projection;

import io.github.nextentity.core.interceptor.ConstructInterceptor;
import io.github.nextentity.proxy.spring.CglibProxyInterceptor;
import io.github.nextentity.proxy.spring.ProxyException;
import io.github.nextentity.core.reflect.schema.Schema;
import io.github.nextentity.jdbc.Arguments;
import io.github.nextentity.jdbc.QueryContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

/// 代理异常场景测试
///
/// 测试覆盖：
/// - final 类无法代理
/// - 无默认构造函数的类无法代理
/// - record 类型不支持 CGLIB
/// - interface 类型不支持 CGLIB（使用 JDK Proxy）
@DisplayName("Proxy Exception Scenario Tests")
@ExtendWith(MockitoExtension.class)
class ProxyExceptionScenarioTest {

    private CglibProxyInterceptor interceptor;

    @Mock
    private QueryContext context;

    @Mock
    private Arguments arguments;

    @BeforeEach
    void setUp() {
        interceptor = new CglibProxyInterceptor();
    }

    // ==================== supports() 测试 ====================

    @Nested
    @DisplayName("supports() Method Tests")
    class SupportsMethodTests {

        @Test
        @DisplayName("普通类返回 true")
        void supportsOrdinaryClassReturnsTrue() {
            Schema schema = Schema.of(ValidPojo.class);
            when(context.getSchema()).thenReturn(schema);

            assertThat(interceptor.supports(context)).isTrue();
        }

        @Test
        @DisplayName("interface 类型返回 false")
        void supportsInterfaceReturnsFalse() {
            Schema schema = Schema.of(TestInterface.class);
            when(context.getSchema()).thenReturn(schema);

            assertThat(interceptor.supports(context)).isFalse();
        }

        @Test
        @DisplayName("record 类型返回 false")
        void supportsRecordReturnsFalse() {
            Schema schema = Schema.of(TestRecord.class);
            when(context.getSchema()).thenReturn(schema);

            assertThat(interceptor.supports(context)).isFalse();
        }

        @Test
        @DisplayName("final 类返回 false")
        void supportsFinalClassReturnsFalse() {
            Schema schema = Schema.of(FinalClass.class);
            when(context.getSchema()).thenReturn(schema);

            assertThat(interceptor.supports(context)).isFalse();
        }

        @Test
        @DisplayName("schema 为 null 返回 false")
        void supportsNullSchemaReturnsFalse() {
            when(context.getSchema()).thenReturn(null);

            assertThat(interceptor.supports(context)).isFalse();
        }
    }

    // ==================== intercept() 异常测试 ====================

    @Nested
    @DisplayName("intercept() Exception Tests")
    class InterceptExceptionTests {

        @Test
        @DisplayName("final 类抛出 ProxyException")
        void interceptFinalClassThrowsProxyException() {
            Schema schema = Schema.of(FinalClass.class);
            when(context.getSchema()).thenReturn(schema);

            assertThatThrownBy(() -> interceptor.intercept(context, arguments))
                    .isInstanceOf(ProxyException.class)
                    .hasMessageContaining("Cannot proxy final class");
        }

        @Test
        @DisplayName("无默认构造函数的类抛出 ProxyException")
        void interceptClassWithoutDefaultConstructorThrowsProxyException() {
            Schema schema = Schema.of(NoDefaultConstructor.class);
            when(context.getSchema()).thenReturn(schema);

            assertThatThrownBy(() -> interceptor.intercept(context, arguments))
                    .isInstanceOf(ProxyException.class)
                    .hasMessageContaining("Cannot proxy class without default constructor");
        }

        @Test
        @DisplayName("schema 为 null 返回 null")
        void interceptNullSchemaReturnsNull() {
            when(context.getSchema()).thenReturn(null);

            assertThat(interceptor.intercept(context, arguments)).isNull();
        }
    }

    // ==================== 基本属性测试 ====================

    @Nested
    @DisplayName("Basic Properties Tests")
    class BasicPropertiesTests {

        @Test
        @DisplayName("拦截器名称正确")
        void interceptorNameIsCorrect() {
            assertThat(interceptor.name()).isEqualTo("cglib-proxy");
        }

        @Test
        @DisplayName("默认优先级为 0")
        void defaultOrderIsZero() {
            assertThat(interceptor.order()).isEqualTo(0);
        }

        @Test
        @DisplayName("自定义优先级正确设置")
        void customOrderIsCorrect() {
            CglibProxyInterceptor customInterceptor = new CglibProxyInterceptor(50);
            assertThat(customInterceptor.order()).isEqualTo(50);
        }
    }

    // ==================== 测试数据类型 ====================

    /// 有效 POJO 类（可代理）
    public static class ValidPojo {
        private String name;

        public ValidPojo() {}

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    /// 测试接口
    public interface TestInterface {
        String getName();
    }

    /// 测试 record
    public record TestRecord(String name) {}

    /// final 类（不可代理）
    public static final class FinalClass {
        private String name;

        public FinalClass() {}

        public String getName() { return name; }
    }

    /// 无默认构造函数的类
    public static class NoDefaultConstructor {
        private String name;

        public NoDefaultConstructor(String name) {
            this.name = name;
        }

        public String getName() { return name; }
    }
}