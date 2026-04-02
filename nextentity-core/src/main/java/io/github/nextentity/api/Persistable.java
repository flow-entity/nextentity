package io.github.nextentity.api;

/// 可持久化实体接口，带有主键类型参数。
///
/// 继承 {@link Entity}，增加了主键类型参数，
/// 并提供访问实体 ID 的方法。
///
/// ## 使用示例
///
/// ```java
/// public class User implements Persistable<Long> {
///     private Long id;
///     private String name;
///
///     @Override
///     public Long getId() { return id; }
///
///     public void setId(Long id) { this.id = id; }
///     public String getName() { return name; }
///     public void setName(String name) { this.name = name; }
/// }
///
/// // 使用示例
/// User user = new User();
/// user.setId(1L);
/// user.setName("张三");
///
/// Long userId = user.getId(); // 获取主键
/// ```
///
/// @param <ID> 实体主键的类型
/// @author HuangChengwei
/// @since 1.0.0
public interface Persistable<ID> extends Entity {

    /// 返回此实体的主键。
    ///
    /// @return 实体的主键，如果实体是新的则返回 null
    ID getId();

}