package io.github.nextentity.examples.config;

import io.github.nextentity.core.interceptor.ConstructInterceptor;
import io.github.nextentity.core.interceptor.JdkProxyInterceptor;
import io.github.nextentity.spring.CglibProxyInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/// 代理拦截器配置示例
///
/// **说明**：
/// - `nextentity-proxy-spring` 模块已包含自动配置
/// - 此配置类展示手动配置方式（供学习参考）
///
/// **自动配置行为**：
/// - 若引入 `nextentity-proxy-spring`，无需手动配置
/// - 自动注册 `CglibProxyInterceptor` 和 `JdkProxyInterceptor`
/// - 可通过 `nextentity.proxy.enabled=false` 禁用
///
/// **手动配置示例**：
/// ```java
/// @Configuration
/// public class ProxyInterceptorConfig {
///
///     /// CGLIB 代理拦截器（处理普通类）
///     @Bean
///     public ConstructInterceptor cglibProxyInterceptor() {
///         return new CglibProxyInterceptor();
///     }
///
///     /// JDK 代理拦截器（处理 interface）
///     @Bean
///     public ConstructInterceptor jdkProxyInterceptor() {
///         return new JdkProxyInterceptor();
///     }
/// }
/// ```
///
/// **自定义拦截器示例**：
/// ```java
/// @Configuration
/// public class CustomInterceptorConfig {
///
///     /// 自定义 CGLIB 拦截器（覆盖默认）
///     @Bean
///     @Primary
///     public ConstructInterceptor customCglibInterceptor() {
///         return new CglibProxyInterceptor(100);  // 自定义优先级
///     }
/// }
/// ```
///
/// @see CglibProxyInterceptor CGLIB 代理拦截器
/// @see JdkProxyInterceptor JDK 代理拦截器
@Configuration
public class ProxyInterceptorConfig {

    /// CGLIB 代理拦截器（处理普通类投影）
    ///
    /// **自动配置已包含**，此 Bean 供演示参考。
    /// 若未引入 `nextentity-proxy-spring`，可使用此配置。
    ///
    /// @return CGLIB 代理拦截器实例
    @Bean
    @ConditionalOnMissingBean(ConstructInterceptor.class)
    public ConstructInterceptor cglibProxyInterceptor() {
        return new CglibProxyInterceptor();
    }

    /// JDK 代理拦截器（处理 interface 投影）
    ///
    /// **自动配置已包含**，此 Bean 供演示参考。
    ///
    /// @return JDK 代理拦截器实例
    @Bean
    @ConditionalOnMissingBean(name = "jdkProxyInterceptor")
    public ConstructInterceptor jdkProxyInterceptor() {
        return new JdkProxyInterceptor();
    }
}