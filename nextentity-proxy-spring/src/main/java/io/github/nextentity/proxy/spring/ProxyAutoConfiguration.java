package io.github.nextentity.proxy.spring;

import io.github.nextentity.core.interceptor.ConstructInterceptor;
import io.github.nextentity.core.interceptor.JdkProxyInterceptor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/// NextEntity 代理自动配置
///
/// 自动注册 CGLIB 和 JDK 代理拦截器。
/// 需要启用 nextentity.proxy.enabled 配置。
///
/// @since 2.1.4
@AutoConfiguration
public class ProxyAutoConfiguration {

    /// 注册 CGLIB 代理拦截器
    ///
    /// @return CGLIB 代理拦截器实例
    @Bean
    public ConstructInterceptor cglibProxyInterceptor() {
        return new CglibProxyInterceptor();
    }

}