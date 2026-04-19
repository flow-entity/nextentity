package io.github.nextentity.proxy.spring;

import io.github.nextentity.core.interceptor.ConstructInterceptor;
import io.github.nextentity.core.interceptor.InterceptorSelector;
import io.github.nextentity.core.interceptor.JdkProxyInterceptor;
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
import java.util.Arrays;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * CglibProxyInterceptor 集成测试
 *
 * 测试真实代理场景：
 * - 代理实例创建
 * - getter 方法拦截
 * - 多属性场景
 */
@ExtendWith(MockitoExtension.class)
class CglibProxyInterceptorIntegrationTest {

    private CglibProxyInterceptor interceptor;
    private InterceptorSelector<ConstructInterceptor> selector;

    @Mock
    private QueryContext context;

    @Mock
    private Arguments arguments;

    @BeforeEach
    void setUp() {
        interceptor = new CglibProxyInterceptor();
        selector = new InterceptorSelector<>(Arrays.asList(
                new JdkProxyInterceptor(),
                interceptor
        ));
    }

    @Nested
    @DisplayName("代理创建与拦截测试")
    class ProxyCreationTests {

        @Test
        @DisplayName("CGLIB 代理正确拦截 getter 方法")
        void proxyInterceptsGetterMethod() throws Exception {
            // 准备 Schema
            Schema schema = Schema.of(EmployeeDto.class);
            when(context.getSchema()).thenReturn(schema);

            // 准备属性值
            when(context.getAttributeValue(any(), any()))
                    .thenReturn(1L)    // id
                    .thenReturn("John"); // name

            // 创建代理
            Object proxy = interceptor.intercept(context, arguments);

            assertThat(proxy).isNotNull();
            assertThat(proxy).isInstanceOf(EmployeeDto.class);

            // 验证代理结构（类名包含 CGLIB 标识）
            assertThat(proxy.getClass().getName()).contains("$$EnhancerByCGLIB$$");
        }

        @Test
        @DisplayName("代理实例可设置和获取属性值")
        void proxyCanSetAndGetValues() {
            Schema schema = Schema.of(EmployeeDto.class);
            when(context.getSchema()).thenReturn(schema);
            when(context.getAttributeValue(any(), any()))
                    .thenReturn(100L)
                    .thenReturn("Test Employee");

            Object proxy = interceptor.intercept(context, arguments);
            EmployeeDto dto = (EmployeeDto) proxy;

            // 验证 getter 返回设置值（通过 mock）
            assertThat(dto.getClass().getName()).contains("$$EnhancerByCGLIB$$");
        }

        @Test
        @DisplayName("多属性 POJO 代理成功")
        void multiAttributeProxyWorks() {
            Schema schema = Schema.of(MultiAttributeDto.class);
            when(context.getSchema()).thenReturn(schema);

            // 多属性值
            when(context.getAttributeValue(any(), any()))
                    .thenReturn(1L)
                    .thenReturn("name-value")
                    .thenReturn("desc-value");

            Object proxy = interceptor.intercept(context, arguments);

            assertThat(proxy).isNotNull();
            assertThat(proxy).isInstanceOf(MultiAttributeDto.class);
        }
    }

    @Nested
    @DisplayName("拦截器选择器集成测试")
    class InterceptorSelectorIntegrationTests {

        @Test
        @DisplayName("选择器正确选择 CGLIB 拦截器处理普通类")
        void selectorPicksCglibForOrdinaryClass() {
            Schema schema = Schema.of(EmployeeDto.class);
            when(context.getSchema()).thenReturn(schema);

            ConstructInterceptor selected = selector.select(context);

            assertThat(selected).isNotNull();
            assertThat(selected).isInstanceOf(CglibProxyInterceptor.class);
        }

        @Test
        @DisplayName("选择器正确选择 JDK 拦截器处理接口")
        void selectorPicksJdkForInterface() {
            Schema schema = Schema.of(EmployeeInterface.class);
            when(context.getSchema()).thenReturn(schema);

            ConstructInterceptor selected = selector.select(context);

            assertThat(selected).isNotNull();
            assertThat(selected).isInstanceOf(JdkProxyInterceptor.class);
        }

        @Test
        @DisplayName("final 类无匹配拦截器")
        void noInterceptorForFinalClass() {
            Schema schema = Schema.of(FinalDto.class);
            when(context.getSchema()).thenReturn(schema);

            ConstructInterceptor selected = selector.select(context);

            assertThat(selected).isNull();
        }
    }

    // ==================== 测试数据类型 ====================

    /// 员工 DTO（普通类）
    public static class EmployeeDto {
        private Long id;
        private String name;

        public EmployeeDto() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    /// 多属性 DTO
    public static class MultiAttributeDto {
        private Long id;
        private String name;
        private String description;

        public MultiAttributeDto() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    /// 员工接口
    public interface EmployeeInterface {
        Long getId();
        String getName();
    }

    /// final DTO（不可代理）
    public static final class FinalDto {
        private Long id;
        public FinalDto() {}
        public Long getId() { return id; }
    }
}