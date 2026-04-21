package io.github.nextentity.examples.projection;

import io.github.nextentity.core.annotation.Fetch;
import io.github.nextentity.examples.entity.Department;
import jakarta.persistence.FetchType;

/// Class 投影示例 - 员工基本信息（POJO）
///
/// **特点**：
/// - 需要注册 `CglibProxyInterceptor` 才能支持延迟加载
/// - 使用 CGLIB 创建代理实例
/// - 需要无参构造函数和标准 getter 方法
/// - 类不能是 final
///
/// **使用示例**：
/// ```java
/// // 1. 配置拦截器（Spring Boot 自动配置已包含）
/// // 若未引入 nextentity-proxy-spring，需要手动配置：
/// @Configuration
/// public class ProxyConfig {
///     @Bean
///     public ConstructInterceptor cglibProxyInterceptor() {
///         return new CglibProxyInterceptor();
///     }
/// }
///
/// // 2. 查询使用
/// List<EmployeeInfoClass> results = repository.query()
///     .select(EmployeeInfoClass.class)
///     .where(Employee::getActive).eq(true)
///     .list();
///
/// // 3. 访问 LAZY 属性触发批量加载
/// Department dept = results.get(0).getDepartment();
/// ```
///
/// **对比 Interface 投影**：
/// | 特性 | Interface | Class |
/// |------|-----------|-------|
/// | 配置 | 无需配置 | 需注册拦截器 |
/// | 代理方式 | JDK Proxy | CGLIB |
/// | 可实例化 | ❌ | ✅ |
/// | 默认值 | ❌ | ✅（调用父类方法） |
///
/// @see EmployeeInfoInterface 接口投影对比示例
public class EmployeeInfoClass {

    /// 员工 ID
    private Long id;

    /// 员工姓名
    private String name;

    /// 员工邮箱
    private String email;

    /// 部门信息（延迟加载）
    private Department department;

    /// 无参构造函数（CGLIB 代理必需）
    public EmployeeInfoClass() {}

    // ==================== Getter 方法 ====================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    /// 部门信息（延迟加载）
    ///
    /// 访问此属性时触发批量查询：
    /// `WHERE department_id IN (所有外键值)`
    ///
    /// @return 部门实体
    @Fetch(FetchType.LAZY)
    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }
}