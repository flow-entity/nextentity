package io.github.nextentity.core.configuration;

import io.github.nextentity.core.PersistExecutor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/// 默认持久化配置实现类
///
/// 提供持久化配置的具体实现，包含后处理器列表。
/// 使用 Builder 模式构造，保证不可变性。
public class DefaultPersistConfiguration implements PersistConfiguration {

    private final List<PostProcessor<PersistExecutor>> postProcessors;

    private DefaultPersistConfiguration(List<PostProcessor<PersistExecutor>> postProcessors) {
        this.postProcessors = List.copyOf(postProcessors);
    }

    @Override
    public List<PostProcessor<PersistExecutor>> getPostProcessors() {
        return postProcessors;
    }

    /// 默认配置实例（无后处理器）
    public static final DefaultPersistConfiguration DEFAULT =
            new DefaultPersistConfiguration(Collections.emptyList());

    /// 创建构建器
    public static Builder builder() {
        return new Builder();
    }

    /// 配置构建器
    public static class Builder {
        private final List<PostProcessor<PersistExecutor>> postProcessors = new ArrayList<>();

        public Builder addPostProcessor(PostProcessor<PersistExecutor> processor) {
            postProcessors.add(processor);
            return this;
        }

        public DefaultPersistConfiguration build() {
            return new DefaultPersistConfiguration(postProcessors);
        }
    }
}