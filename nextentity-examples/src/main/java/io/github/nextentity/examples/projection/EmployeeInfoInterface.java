package io.github.nextentity.examples.projection;

import io.github.nextentity.core.annotation.Fetch;
import io.github.nextentity.examples.entity.Department;
import jakarta.persistence.FetchType;

/// Interface 投影示例 - 员工基本信息
///
/// **特点**：
/// - 无需任何配置，默认支持延迟加载
/// - 使用 JDK Proxy 创建代理实例
/// - 只需定义 getter 方法
///
/// **使用示例**：
/// ```java
/// List<EmployeeInfoInterface> results = repository.query()
///     .select(EmployeeInfoInterface.class)
///     .where(Employee::getActive).eq(true)
///     .list();
///
/// // 访问 LAZY 属性触发批量加载
/// Department dept = results.get(0).getDepartment();
/// ```
///
/// @see EmployeeInfoClass 类投影对比示例
public interface EmployeeInfoInterface {

    /// 员工 ID
    Long getId();

    /// 员工姓名
    String getName();

    /// 员工邮箱
    String getEmail();

    /// 部门信息（延迟加载）
    ///
    /// 访问此属性时触发批量查询：
    /// `WHERE department_id IN (所有外键值)`
    @Fetch(FetchType.LAZY)
    Department getDepartment();
}