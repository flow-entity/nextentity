package io.github.nextentity.core.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/// Specifies the entity attribute path for a projection class field.
///
/// This annotation is used in projection classes (DTOs, view models, etc.)
/// to define the mapping between a projection field and its corresponding
/// entity attribute path. It enables automatic population of projection fields
/// from entity data during query execution.
///
/// The path supports nested attribute traversal using dot notation.
/// For example, `"department.name"` maps to the name of the department association.
///
/// Example usage:
/// ```java
/// /// DTO combining employee and department info
/// public class EmployeeWithDept {
///     @EntityPath("name")
///     private String employeeName;
///
///     @EntityPath("department.name")
///     private String departmentName;
/// }
///
/// // Query with projection
/// List<EmployeeWithDept> results = query()
///     .select(EmployeeWithDept.class)
///     .getList();
/// ```
///
/// @see io.github.nextentity.core.meta.ProjectionAttribute
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface EntityPath {

    /// The entity attribute path for this projection field.
    ///
    /// Supports dot notation for nested attribute access.
    /// An empty string (default) indicates the field name matches
    /// the entity attribute name exactly.
    ///
    /// @return the attribute path, e.g., "name" or "department.name"
    String value() default "";

}