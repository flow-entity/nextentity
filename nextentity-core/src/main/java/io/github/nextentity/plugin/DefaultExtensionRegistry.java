package io.github.nextentity.plugin;

import io.github.nextentity.api.EntityFetcher;
import io.github.nextentity.api.ExtensionRegistry;
import io.github.nextentity.api.FieldInfo;
import io.github.nextentity.api.ProjectionFieldHandler;
import io.github.nextentity.api.ProjectionContext;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/// 默认扩展点注册中心实现。
///
/// 管理 ProjectionFieldHandler 和 EntityFetcher 的注册和获取。
/// 处理器按 order() 优先级排序，数值越小优先级越高。
///
/// @author HuangChengwei
/// @since 2.2.0
public class DefaultExtensionRegistry implements ExtensionRegistry {

    private final List<ProjectionFieldHandler<?>> handlers = new ArrayList<>();
    private EntityFetcher entityFetcher;

    @Override
    public void registerHandler(@NonNull ProjectionFieldHandler<?> handler) {
        handlers.add(handler);
        // 每次注册后重新排序
        handlers.sort(Comparator.comparingInt(ProjectionFieldHandler::order));
    }

    @Override
    public void registerFetcher(@NonNull EntityFetcher fetcher) {
        this.entityFetcher = fetcher;
    }

    @Override
    public List<ProjectionFieldHandler<?>> getHandlers() {
        return new ArrayList<>(handlers);
    }

    @Override
    public Optional<ProjectionFieldHandler<?>> getHandler(FieldInfo field, ProjectionContext context) {
        for (ProjectionFieldHandler<?> handler : handlers) {
            if (handler.supports(field, context)) {
                return Optional.of(handler);
            }
        }
        return Optional.empty();
    }

    @Override
    public EntityFetcher getEntityFetcher() {
        if (entityFetcher == null) {
            throw new IllegalStateException("EntityFetcher not registered");
        }
        return entityFetcher;
    }

    /// 清除所有注册的处理器和获取器。
    ///
    /// 用于测试场景重置状态。
    public void clear() {
        handlers.clear();
        entityFetcher = null;
    }
}