package io.github.nextentity.proxy.spring;

import io.github.nextentity.core.interceptor.ConstructInterceptor;
import io.github.nextentity.core.interceptor.JdkProxyInterceptor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.*;

/**
 * ProxyAutoConfiguration Spring Boot 自动配置测试
 *
 * 测试覆盖：
 * - 默认配置（enabled=true）自动注册拦截器
 * - disabled 配置不注册拦截器
 * - ConditionalOnMissingBean 条件覆盖
 */
class ProxyAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(ProxyAutoConfiguration.class));

    @Nested
    @DisplayName("默认配置测试（enabled=true/matchIfMissing=true）")
    class DefaultConfigTests {

        @Test
        @DisplayName("默认注册 CglibProxyInterceptor 和 JdkProxyInterceptor")
        void registersBothInterceptorsByDefault() {
            contextRunner.run(context -> {
                // 验证 cglibProxyInterceptor 存在
                assertThat(context).hasBean("cglibProxyInterceptor");
                ConstructInterceptor cglibInterceptor = context.getBean("cglibProxyInterceptor", ConstructInterceptor.class);
                assertThat(cglibInterceptor).isInstanceOf(CglibProxyInterceptor.class);
                assertThat(cglibInterceptor.name()).isEqualTo("cglib-proxy");

                // 验证 jdkProxyInterceptor 存在
                assertThat(context).hasBean("jdkProxyInterceptor");
                ConstructInterceptor jdkInterceptor = context.getBean("jdkProxyInterceptor", ConstructInterceptor.class);
                assertThat(jdkInterceptor).isInstanceOf(JdkProxyInterceptor.class);
                assertThat(jdkInterceptor.name()).isEqualTo("jdk-proxy");
            });
        }

        @Test
        @DisplayName("默认注册 JdkProxyInterceptor（名为 jdkProxyInterceptor）")
        void registersJdkProxyInterceptorByDefault() {
            contextRunner.run(context -> {
                ConstructInterceptor jdkInterceptor = context.getBean("jdkProxyInterceptor", ConstructInterceptor.class);
                assertThat(jdkInterceptor).isInstanceOf(JdkProxyInterceptor.class);
                assertThat(jdkInterceptor.name()).isEqualTo("jdk-proxy");
            });
        }
    }

    @Nested
    @DisplayName("配置禁用测试")
    class DisabledConfigTests {

        @Test
        @DisplayName("nextentity.proxy.enabled=false 不注册拦截器")
        void doesNotRegisterWhenDisabled() {
            contextRunner
                    .withPropertyValues("nextentity.proxy.enabled=false")
                    .run(context -> {
                        assertThat(context).doesNotHaveBean(ConstructInterceptor.class);
                        assertThat(context).doesNotHaveBean("jdkProxyInterceptor");
                    });
        }
    }

    @Nested
    @DisplayName("ConditionalOnMissingBean 测试")
    class ConditionalOnMissingBeanTests {

        @Test
        @DisplayName("用户自定义 ConstructInterceptor Bean 不影响 jdkProxyInterceptor")
        void customInterceptorDoesNotAffectJdkInterceptor() {
            contextRunner
                    .withUserConfiguration(CustomInterceptorConfig.class)
                    .run(context -> {
                        // 用户自定义拦截器存在
                        assertThat(context).hasBean("customInterceptor");
                        ConstructInterceptor customInterceptor = context.getBean("customInterceptor", ConstructInterceptor.class);
                        assertThat(customInterceptor).isInstanceOf(CustomInterceptor.class);
                        assertThat(customInterceptor.name()).isEqualTo("custom-interceptor");

                        // jdkProxyInterceptor 仍然存在（有独立的 bean 名称条件）
                        assertThat(context).hasBean("jdkProxyInterceptor");
                        ConstructInterceptor jdkInterceptor = context.getBean("jdkProxyInterceptor", ConstructInterceptor.class);
                        assertThat(jdkInterceptor).isInstanceOf(JdkProxyInterceptor.class);
                    });
        }

        @Test
        @DisplayName("用户自定义 jdkProxyInterceptor Bean 覆盖默认配置")
        void customJdkInterceptorOverridesDefault() {
            contextRunner
                    .withUserConfiguration(CustomJdkInterceptorConfig.class)
                    .run(context -> {
                        ConstructInterceptor jdkInterceptor = context.getBean("jdkProxyInterceptor", ConstructInterceptor.class);
                        assertThat(jdkInterceptor).isInstanceOf(CustomJdkInterceptor.class);
                        assertThat(jdkInterceptor.name()).isEqualTo("custom-jdk");
                    });
        }
    }

    // ==================== 测试配置类 ====================

    @Configuration
    static class CustomInterceptorConfig {
        @Bean
        public ConstructInterceptor customInterceptor() {
            return new CustomInterceptor();
        }
    }

    @Configuration
    static class CustomJdkInterceptorConfig {
        @Bean
        public ConstructInterceptor jdkProxyInterceptor() {
            return new CustomJdkInterceptor();
        }
    }

    /// 自定义拦截器（用于测试覆盖）
    static class CustomInterceptor implements ConstructInterceptor {
        @Override
        public boolean supports(io.github.nextentity.jdbc.QueryContext context) { return false; }
        @Override
        public Object intercept(io.github.nextentity.jdbc.QueryContext context, io.github.nextentity.jdbc.Arguments arguments) { return null; }
        @Override
        public String name() { return "custom-interceptor"; }
        @Override
        public int order() { return 100; }
    }

    /// 自定义 JDK 拦截器（用于测试覆盖）
    static class CustomJdkInterceptor implements ConstructInterceptor {
        @Override
        public boolean supports(io.github.nextentity.jdbc.QueryContext context) { return false; }
        @Override
        public Object intercept(io.github.nextentity.jdbc.QueryContext context, io.github.nextentity.jdbc.Arguments arguments) { return null; }
        @Override
        public String name() { return "custom-jdk"; }
        @Override
        public int order() { return 200; }
    }
}