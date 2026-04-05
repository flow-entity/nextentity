package io.github.nextentity.core.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/// 为投影类字段指定实体属性路径。
///
/// 此注解用于投影类（DTO、视图模型等）中，定义投影字段与其对应实体属性路径之间的映射。
/// 它允许在查询执行期间自动从实体数据填充投影字段。
///
/// 路径支持使用点符号进行嵌套属性遍历。例如，"department.name" 映射到部门关联的名称。
///
/// 示例用法：
/// ```java
/// /// 组合员工和部门信息的DTO
/// public class EmployeeWithDept {
///     @EntityPath("name")
///     private String employeeName;
///
///     @EntityPath("department.name")
///     private String departmentName;
/// }
///
/// // 带投影的查询
/// List<EmployeeWithDept> results = query()
///     .select(EmployeeWithDept.class)
///     .getList();
/// ```
///
/// @see io.github.nextentity.core.meta.ProjectionAttribute
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface EntityPath {

    /// 此投影字段的实体属性路径。
    ///
    /// 支持点符号进行嵌套属性访问。
    /// 空字符串（默认值）表示字段名与实体属性名完全匹配。
    ///
    /// @return 属性路径，例如 "name" 或 "department.name"
    String value() default "";

}