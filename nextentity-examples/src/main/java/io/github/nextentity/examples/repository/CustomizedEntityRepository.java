package io.github.nextentity.examples.repository;

import io.github.nextentity.core.EntityOperationsFactory;
import io.github.nextentity.spring.GenericRepository;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import java.util.List;

/// 扩展的 Repository 基类，提供通用的便捷方法。
///
/// 用户可以继承此类获得额外的查询方法，
/// 而注入时只需使用 `Repository<T, ID>` 接口。
///
/// @param <T>  实体类型
/// @param <ID> 主键类型
/// @author HuangChengwei
/// @since 2.1.4
@Repository
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CustomizedEntityRepository<T, ID> extends GenericRepository<T, ID> implements IExtendedEntityRepository<T, ID> {

    public CustomizedEntityRepository(EntityOperationsFactory factory, InjectionPoint injectionPoint) {
        super(factory, injectionPoint);
    }

    /// 查询所有记录（便捷方法）。
    public List<T> findAll() {
        return query().list();
    }

    /// 查询前 N 条记录。
    public List<T> findTop(int limit) {
        return query().list(limit);
    }

    /// 统计总数。
    public long count() {
        return query().count();
    }

    /// 检查是否存在任何记录。
    public boolean hasAny() {
        return query().exists();
    }

    /// 根据主键批量删除（便捷方法）。
    public void deleteByIds(List<ID> ids) {
        if (ids != null && !ids.isEmpty()) {
            deleteAllById(ids);
        }
    }
}