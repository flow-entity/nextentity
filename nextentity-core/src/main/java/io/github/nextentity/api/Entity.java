package io.github.nextentity.api;

/// 实体类标记接口。
///
/// 实现此接口的实体类可以被框架识别，用于类型安全的数据库操作。
///
/// ## 主要作用
///
/// 1. **标记实体类型**：让框架能够识别实体类，用于类型安全的数据库操作
/// 2. **简化 EntityPath 构建**：实现此接口后，可以通过方法引用直接调用
///    {@link Path#of(PathRef.EntityPathRef)} 方法构建 {@link EntityPath}，
///    编译器会自动识别方法引用为 `PathRef.EntityPathRef` 类型
///
/// 对于需要暴露主键的实体，请使用 {@link Persistable} 接口。
///
/// ## 使用示例
///
/// ```java
/// // 定义实体类
/// public class Department implements Entity {
///     private Long id;
///     private String name;
///
///     // getters and setters...
/// }
///
/// public class User implements Persistable<Long> {
///     private Long id;
///     private String name;
///     private Department department;  // 关联实体
///
///     @Override
///     public Long getId() { return id; }
///
///     // other getters and setters...
/// }
///
/// // 使用 EntityPath 访问嵌套实体属性
/// // 因为 Department 实现了 Entity，可以直接使用方法引用
/// EntityPath<User, Department> deptPath = Path.of(User::getDepartment);
/// StringPath<User> deptName = deptPath.get(Department::getName);
///
/// // 在查询中使用嵌套属性
/// List<User> users = repository.query()
///     .where(User::getDepartment).get(Department::getName).eq("技术部")
///     .getList();
/// ```
///
/// @author HuangChengwei
/// @since 1.0.0
/// @see Path#of(PathRef.EntityPathRef)
/// @see EntityPath
public interface Entity {
}