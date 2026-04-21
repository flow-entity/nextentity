package io.github.nextentity.integration.dto;

import io.github.nextentity.core.annotation.Fetch;
import io.github.nextentity.integration.entity.Department;

import static jakarta.persistence.FetchType.LAZY;

/**
 * 带懒加载属性的员工投影接口。
 * <p>
 * 用于测试投影对象 LAZY 属性的批量加载功能。
 * department 属性标记为 LAZY，首次访问时触发批量 WHERE IN 查询。
 */
public interface EmployeeWithLazyDepartment {

    Long getId();

    String getName();

    Double getSalary();

    Long getDepartmentId();

    /**
     * 懒加载的部门信息。
     * <p>
     * 使用 @Fetch(LAZY) 标记为延迟加载，
     * 首次访问时通过批量 WHERE IN 查询加载所有关联对象。
     */
    @Fetch(LAZY)
    Department getDepartment();

}