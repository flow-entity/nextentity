package io.github.nextentity.proxy.spring;

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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * CglibProxyInterceptor 单元测试
 *
 * 测试覆盖：
 * - supports() 方法：类型判断逻辑
 * - intercept() 方法：代理创建和异常处理
 * - name() 和 order() 方法：基本属性
 */
@ExtendWith(MockitoExtension.class)
class CglibProxyInterceptorTest {

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
    @DisplayName("supports() 方法测试")
    class SupportsTests {

        @Test
        @DisplayName("普通类（非 interface、非 record、非 final）返回 true")
        void supportsOrdinaryClassReturnsTrue() {
            // 普通类有默认构造函数
            Schema schema = Schema.of(TestPojo.class);
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

    // ==================== intercept() 测试 ====================

    @Nested
    @DisplayName("intercept() 方法测试")
    class InterceptTests {

        @Test
        @DisplayName("final 类抛 ProxyException")
        void interceptFinalClassThrowsException() {
            Schema schema = Schema.of(FinalClass.class);
            when(context.getSchema()).thenReturn(schema);

            assertThatThrownBy(() -> interceptor.intercept(context, arguments))
                    .isInstanceOf(ProxyException.class)
                    .hasMessageContaining("Cannot proxy final class");
        }

        @Test
        @DisplayName("无无参构造函数的类抛 ProxyException")
        void interceptClassWithoutDefaultConstructorThrowsException() {
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

        @Test
        @DisplayName("成功创建 CGLIB 代理实例")
        void interceptCreatesProxySuccessfully() {
            Schema schema = Schema.of(TestPojo.class);
            when(context.getSchema()).thenReturn(schema);
            when(context.getAttributeValue(any(), any())).thenReturn("test-value");

            Object result = interceptor.intercept(context, arguments);

            assertThat(result).isNotNull();
            // 验证代理结构（类名包含 CGLIB 标识）
            assertThat(result.getClass().getName()).contains("$$EnhancerByCGLIB$$");
            assertThat(result).isInstanceOf(TestPojo.class);
        }
    }

    // ==================== name() 和 order() 测试 ====================

    @Nested
    @DisplayName("基本属性测试")
    class BasicPropertyTests {

        @Test
        @DisplayName("name() 返回 'cglib-proxy'")
        void nameReturnsCglibProxy() {
            assertThat(interceptor.name()).isEqualTo("cglib-proxy");
        }

        @Test
        @DisplayName("默认 order() 返回 0")
        void defaultOrderReturnsZero() {
            assertThat(interceptor.order()).isEqualTo(0);
        }

        @Test
        @DisplayName("自定义 order() 返回指定值")
        void customOrderReturnsSpecifiedValue() {
            CglibProxyInterceptor customInterceptor = new CglibProxyInterceptor(100);
            assertThat(customInterceptor.order()).isEqualTo(100);
        }
    }

    // ==================== 测试数据类型 ====================

    /// 普通 POJO 类（可代理）
    public static class TestPojo {
        private String name;

        public TestPojo() {}

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    /// 测试接口（不可代理）
    public interface TestInterface {
        String getName();
    }

    /// 测试 record（不可代理）
    public record TestRecord(String name) {}

    /// final 类（不可代理）
    public static final class FinalClass {
        private String name;
        public FinalClass() {}
        public String getName() { return name; }
    }

    /// 无默认构造函数的类（不可代理）
    public static class NoDefaultConstructor {
        private String name;

        public NoDefaultConstructor(String name) {
            this.name = name;
        }

        public String getName() { return name; }
    }
}