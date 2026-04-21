package io.github.nextentity.examples.projection;

import io.github.nextentity.core.annotation.Fetch;
import io.github.nextentity.core.interceptor.ConstructInterceptor;
import io.github.nextentity.core.interceptor.JdkProxyInterceptor;
import io.github.nextentity.examples.entity.Department;
import io.github.nextentity.proxy.spring.CglibProxyInterceptor;
import jakarta.persistence.FetchType;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/// CGLIB 代理拦截器使用示例
///
/// 本文档展示 nextentity-proxy-spring 模块的完整使用方法。
///
/// ## 模块功能
///
/// `nextentity-proxy-spring` 提供 CGLIB 代理支持，让普通类（POJO）投影也能支持延迟加载。
///
/// ## 核心概念
///
/// | 投影类型 | 代理方式 | 配置需求 | 适用场景 |
/// |---------|---------|---------|---------|
/// | Interface | JDK Proxy | 无需配置 | 轻量投影 |
/// | Class (POJO) | CGLIB | 需注册拦截器 | 可实例化 DTO |
///
/// ## 快速开始
///
/// ### 1. 添加依赖
/// ```xml
/// <dependency>
///     <groupId>io.github.flow-entity</groupId>
///     <artifactId>nextentity-proxy-spring</artifactId>
///     <version>2.1.4</version>
/// </dependency>
/// ```
///
/// ### 2. 自动配置（推荐）
///
/// 引入 `nextentity-proxy-spring` 后，自动配置生效：
/// - 注册 `CglibProxyInterceptor`（处理普通类）
/// - 注册 `JdkProxyInterceptor`（处理 interface）
///
/// 无需任何手动配置即可使用。
///
/// ### 3. 手动配置（可选）
///
/// 如需自定义拦截器行为，可手动配置：
/// ```java
/// @Configuration
/// public class ProxyInterceptorConfig {
///
///     @Bean
///     public ConstructInterceptor cglibProxyInterceptor() {
///         return new CglibProxyInterceptor();
///     }
///
///     @Bean
///     public ConstructInterceptor jdkProxyInterceptor() {
///         return new JdkProxyInterceptor();
///     }
/// }
/// ```
///
/// ## 投影类定义
///
/// ### Interface 投影
/// ```java
/// public interface EmployeeInfo {
///     Long getId();
///     String getName();
///
///     @Fetch(FetchType.LAZY)  // 延迟加载
///     Department getDepartment();
/// }
/// ```
///
/// ### Class 投影（POJO）
/// ```java
/// public class EmployeeDto {
///     private Long id;
///     private String name;
///     private Department department;
///
///     // 必须有无参构造函数
///     public EmployeeDto() {}
///
///     public Long getId() { return id; }
///     public void setId(Long id) { this.id = id; }
///
///     public String getName() { return name; }
///     public void setName(String name) { this.name = name; }
///
///     @Fetch(FetchType.LAZY)  // 延迟加载
///     public Department getDepartment() { return department; }
///     public void setDepartment(Department department) { this.department = department; }
/// }
/// ```
///
/// ## Class 投影要求
///
/// 1. **无参构造函数**：public，用于 CGLIB 创建代理实例
/// 2. **标准 getter 方法**：用于代理拦截
/// 3. **非 final 类**：CGLIB 无法代理 final 类
/// 4. **非 final 方法**：getter 方法不能是 final
///
/// ## 延迟加载行为
///
/// 首次访问任意对象的 LAZY 属性时触发批量加载：
/// - 执行查询：`WHERE foreign_key IN (所有外键值)`
/// - 结果缓存：外键 → 实体映射
/// - 后续访问：从缓存直接返回
///
/// **优势**：避免 N+1 查询问题。
///
/// ## 查询使用示例
/// ```java
/// // Interface 投影
/// List<EmployeeInfo> results = repository.query()
///     .select(EmployeeInfo.class)
///     .where(Employee::getActive).eq(true)
///     .list();
///
/// // Class 投影
/// List<EmployeeDto> results = repository.query()
///     .select(EmployeeDto.class)
///     .where(Employee::getActive).eq(true)
///     .list();
///
/// // 首次访问触发批量加载
/// Department dept = results.get(0).getDepartment();
/// // 后续访问从缓存返回
/// Department dept2 = results.get(1).getDepartment();
/// ```
///
/// ## 异常处理
///
/// ### 无法代理 final 类
/// ```java
/// public final class FinalDto { ... }  // ❌ 抛出 ProxyException
/// ```
///
/// ### 无法代理无构造函数类
/// ```java
/// public class NoConstructorDto {
///     public NoConstructorDto(String name) { ... }  // ❌ 抛出 ProxyException
/// }
/// ```
///
/// ## 自定义拦截器优先级
/// ```java
/// @Bean
/// public ConstructInterceptor customCglibInterceptor() {
///     return new CglibProxyInterceptor(100);  // 自定义优先级
/// }
/// ```
///
/// ## 禁用自动配置
/// ```properties
/// nextentity.proxy.enabled=false
/// ```
///
/// @see CglibProxyInterceptor
/// @see JdkProxyInterceptor
/// @see EmployeeInfoInterface Interface 投影示例
/// @see EmployeeInfoClass Class 投影示例
/// @see EmployeeDetailDto 多延迟加载属性示例
///
// 以下为示例配置类（非活跃配置）
@SuppressWarnings("unused")
class CglibProxyInterceptorUsageExamples {

    /// 示例：手动配置拦截器
    ///
    /// **场景**：需要自定义拦截器行为或覆盖自动配置
    @Configuration
    static class ManualProxyInterceptorConfig {

        /// CGLIB 代理拦截器（处理普通类投影）
        @Bean
        @ConditionalOnMissingBean(ConstructInterceptor.class)
        ConstructInterceptor cglibProxyInterceptor() {
            return new CglibProxyInterceptor();
        }

        /// JDK 代理拦截器（处理 interface 投影）
        @Bean
        @ConditionalOnMissingBean(name = "jdkProxyInterceptor")
        ConstructInterceptor jdkProxyInterceptor() {
            return new JdkProxyInterceptor();
        }
    }

    /// 示例：多延迟加载属性的投影类
    ///
    /// **场景**：需要多个关联实体延迟加载
    static class MultiLazyAttributeDto {

        private Long id;
        private String name;
        private Department department;
        private Department managerDepartment;

        public MultiLazyAttributeDto() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        /// 部门信息（延迟加载）
        @Fetch(FetchType.LAZY)
        public Department getDepartment() { return department; }
        public void setDepartment(Department department) { this.department = department; }

        /// 管理者部门（延迟加载）
        @Fetch(FetchType.LAZY)
        public Department getManagerDepartment() { return managerDepartment; }
        public void setManagerDepartment(Department managerDepartment) {
            this.managerDepartment = managerDepartment;
        }
    }

    /// 示例：自定义优先级拦截器配置
    @Configuration
    static class CustomOrderInterceptorConfig {

        /// 高优先级 CGLIB 拦截器
        @Bean
        ConstructInterceptor highPriorityCglibInterceptor() {
            return new CglibProxyInterceptor(-10);  // 更高优先级
        }

        /// 低优先级 JDK 拦截器
        @Bean
        ConstructInterceptor lowPriorityJdkInterceptor() {
            return new JdkProxyInterceptor(100);  // 更低优先级
        }
    }

    /// 示例：不可代理的类（用于测试异常场景）
    ///
    /// **警告**：以下类无法被 CGLIB 代理，会抛出 ProxyException
    static final class InvalidFinalClass {

        private String name;

        public InvalidFinalClass() {}

        public String getName() { return name; }
    }

    /// 示例：无默认构造函数的类
    ///
    /// **警告**：以下类无法被 CGLIB 代理，会抛出 ProxyException
    static class InvalidNoConstructorClass {

        private String name;

        // 只有带参数的构造函数
        public InvalidNoConstructorClass(String name) {
            this.name = name;
        }

        public String getName() { return name; }
    }
}