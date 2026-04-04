package io.github.nextentity.spring;

import io.github.nextentity.api.Path;
import io.github.nextentity.api.PathRef;
import io.github.nextentity.api.Persistable;

import java.util.function.Function;

/// 支持 Persistable 实体的抽象 Repository，提供基于主键的查询方法。
///
/// 继承 {@link AbstractRepository}，针对实现了 {@link Persistable} 接口的实体
/// 提供更高效的 ID 查询实现。Persistable 实体通过 {@code getId()} 方法
/// 暴露其主键，因此可以直接使用方法引用获取 ID，无需反射。
///
/// 使用示例：
/// ```java
/// @Component
/// public class UserRepository extends PersistableRepository<User, Long> {
///     // ID 相关方法自动可用
///     public User findById(Long id) { return getById(id); }
///     public List<User> findByIds(Collection<Long> ids) { return getAllById(ids); }
/// }
/// ```
///
/// @param <T>  实体类型（必须实现 Persistable 接口）
/// @param <ID> 主键类型
/// @author HuangChengwei
/// @since 1.0.0
public abstract class PersistableRepository<T extends Persistable<ID>, ID> extends AbstractRepository<T, ID> {

    /// 获取主键路径表达式。
    ///
    /// 重写父类方法，使用 Persistable::getId 方法引用构建路径，
    /// 比反射方式更高效。
    ///
    /// @return 主键路径表达式
    @Override
    protected Path<T, ID> newTidPath() {
        PathRef<T, ID> getId = Persistable::getId;
        return Path.of(getId);
    }

    /// 获取 ID 提取函数。
    ///
    /// 重写父类方法，直接使用 Persistable::getId 方法引用，
    /// 无需通过反射获取 ID 值。
    ///
    /// @return 从实体中提取 ID 的函数
    @Override
    protected Function<? super T, ? extends ID> newIdExtractor() {
        return Persistable::getId;
    }

}
