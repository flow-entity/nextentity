package io.github.nextentity.spring;

import io.github.nextentity.core.EntityOperationsFactory;
import io.github.nextentity.core.EntityTemplate;
import io.github.nextentity.core.TypeCastUtil;
import io.github.nextentity.core.exception.ConfigurationException;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.core.ResolvableType;

/// 通用的 Repository 实现，提供基本的数据库 CRUD 操作和查询构建功能。
///
/// 该类可以直接实例化，无需继承。通过 EntityTemplate 提供所有 Repository 功能。
///
/// 使用示例：
/// ```java
/// // 直接实例化
/// GenericRepository<User, Long> repository = new GenericRepository<>(template, Long.class);
///
/// // 或通过自动配置注入
/// @Autowired
/// Repository<User, Long> userRepository;
/// ```
///
/// @param <T>  实体类型
/// @param <ID> 主键类型
/// @author HuangChengwei
/// @since 2.1.3
public class GenericRepository<T, ID> extends AbstractRepository<T, ID> {

    /// 创建 Repository 实例。
    ///
    /// @param operations EntityTemplate 实例，提供实体操作能力
    public GenericRepository(EntityTemplate<T> operations, Class<ID> genericIdType) {
        super(operations, genericIdType);
    }

    /// 通过注入点创建 Repository 实例。
    ///
    /// 该构造方法用于 Spring 自动配置，根据注入点的泛型参数
    /// 自动解析实体类型和主键类型。
    ///
    /// @param factory      EntityOperationsFactory 实例
    /// @param injectionPoint Spring 注入点，用于解析泛型参数
    public GenericRepository(EntityOperationsFactory factory,
                             InjectionPoint injectionPoint) {
        if (!(injectionPoint instanceof DependencyDescriptor descriptor)) {
            throw new ConfigurationException("Unsupported injection point: " + injectionPoint);
        }
        ResolvableType resolvableType = descriptor.getResolvableType().as(Repository.class);
        Class<T> entityType = TypeCastUtil.cast(resolvableType.resolveGeneric(0));
        Class<ID> genericIdType = TypeCastUtil.cast(resolvableType.resolveGeneric(1));
        // operations() 返回 EntityOperations，实际实现是 EntityTemplate
        EntityTemplate<T> template = (EntityTemplate<T>) factory.operations(entityType);
        super(template, genericIdType);
    }

}