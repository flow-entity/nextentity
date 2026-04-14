package io.github.nextentity.spring;

import io.github.nextentity.api.EntityFetcher;
import io.github.nextentity.api.Path;
import io.github.nextentity.core.EntityTemplateFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/// 基于 Repository 的 EntityFetcher 实现。
///
/// 使用 EntityTemplateFactory 提供实体获取能力，
/// 用于 EntityReference 延迟加载场景。
///
/// @author HuangChengwei
/// @since 2.2.0
public class RepositoryEntityFetcher implements EntityFetcher {

    private final EntityTemplateFactory factory;

    /// 创建 RepositoryEntityFetcher。
    ///
    /// @param factory EntityTemplateFactory 实例
    public RepositoryEntityFetcher(EntityTemplateFactory factory) {
        this.factory = factory;
    }

    @Override
    public <T, ID> Optional<T> fetch(Class<T> entityType, ID id) {
        try {
            // 使用 query() 查询单个实体
            Path<T, ID> idPath = getIdPath(entityType);
            T entity = factory.query(entityType)
                    .where(idPath).eq(id)
                    .first();
            return Optional.ofNullable(entity);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public <T, ID> Map<ID, T> fetchBatch(Class<T> entityType, Collection<ID> ids) {
        Map<ID, T> result = new HashMap<>();
        if (ids == null || ids.isEmpty()) {
            return result;
        }

        try {
            // 使用 query() 批量查询
            Path<T, ID> idPath = getIdPath(entityType);
            var query = factory.query(entityType)
                    .where(idPath).in(ids);

            for (T entity : query.list()) {
                ID entityId = extractId(entity);
                if (entityId != null) {
                    result.put(entityId, entity);
                }
            }
        } catch (Exception e) {
            // 单个查询作为备选
            for (ID id : ids) {
                Optional<T> entity = fetch(entityType, id);
                entity.ifPresent(t -> result.put(id, t));
            }
        }
        return result;
    }

    @Override
    public <T> boolean supports(Class<T> entityType) {
        // 支持所有实体类型
        return true;
    }

    /// 获取 ID 路径表达式。
    ///
    /// 假设实体有名为 "id" 的字段。
    private <T, ID> Path<T, ID> getIdPath(Class<T> entityType) {
        // 通过反射创建路径表达式
        // 这里简化处理，假设使用 "id" 字段
        return (Path<T, ID>) (Object) factory.metamodel().getEntity(entityType).id().expression();
    }

    /// 从实体提取 ID。
    ///
    /// 通过元模型获取 ID 值。
    private <T, ID> ID extractId(T entity) {
        if (entity == null) {
            return null;
        }
        try {
            var entityType = factory.metamodel().getEntity(entity.getClass());
            var idAttr = entityType.id();
            return (ID) idAttr.get(entity);
        } catch (Exception e) {
            return null;
        }
    }
}