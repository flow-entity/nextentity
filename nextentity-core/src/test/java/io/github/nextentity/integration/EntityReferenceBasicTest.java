package io.github.nextentity.integration;

import io.github.nextentity.api.*;
import io.github.nextentity.plugin.DefaultExtensionRegistry;
import io.github.nextentity.plugin.EntityReferencePlugin;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/// EntityReference 基本功能集成测试。
///
/// 验证 EntityReference 延迟加载的核心流程：
/// 1. 创建 EntityReference 实例并设置 ID
/// 2. 通过 EntityFetcher 延迟加载实体
/// 3. 批量加载优化
///
/// @author HuangChengwei
/// @since 2.2.0
class EntityReferenceBasicTest {

    @Nested
    class EntityReferenceCreation {

        /// 测试目标: 验证 EntityReference 可以正确创建并存储 ID
        /// 测试场景: 创建 OrderRef 并设置 user 引用的 ID
        /// 预期结果: getId() 返回设置的 ID，get() 未加载
        @Test
        void entityReference_ShouldStoreIdWithoutLoading() {
            // given
            OrderRef orderRef = new OrderRef();
            orderRef.user = new UserRef();
            orderRef.user.setId(1L);

            // when
            Long userId = orderRef.user.getId();

            // then
            assertThat(userId).isEqualTo(1L);
            assertThat(orderRef.user.isLoaded()).isFalse(); // 未加载
        }
    }

    @Nested
    class LazyLoading {

        /// 测试目标: 验证 EntityReference 延迟加载通过 EntityFetcher
        /// 测试场景: 设置 loader 后调用 get()
        /// 预期结果: loader 被调用，返回实体
        @Test
        void get_ShouldLoadEntityThroughFetcher() {
            // given
            ExtensionRegistry registry = new DefaultExtensionRegistry();
            registry.registerFetcher(createMockEntityFetcher());
            registry.registerHandler(new EntityReferencePlugin());

            UserRef userRef = new UserRef();
            userRef.setId(1L);
            userRef.setLoader(() -> registry.getEntityFetcher().fetch(User.class, 1L).orElse(null));

            // when
            User user = userRef.get();

            // then
            assertThat(user).isNotNull();
            assertThat(user.id).isEqualTo(1L);
            assertThat(user.name).isEqualTo("User1");
        }

        /// 测试目标: 验证批量加载避免 N+1 问题
        /// 测试场景: 批量设置多个引用的 loader
        /// 预期结果: 只调用一次 fetchBatch
        @Test
        void batchLoading_ShouldAvoidN1Problem() {
            // given
            int[] fetchCount = {0};
            ExtensionRegistry registry = new DefaultExtensionRegistry();
            registry.registerFetcher(createCountingEntityFetcher(fetchCount));
            registry.registerHandler(new EntityReferencePlugin());

            OrderRef order1 = new OrderRef();
            order1.user = new UserRef();
            order1.user.setId(1L);

            OrderRef order2 = new OrderRef();
            order2.user = new UserRef();
            order2.user.setId(2L);

            // 批量设置 loader
            EntityFetcher fetcher = registry.getEntityFetcher();
            order1.user.setLoader(() -> fetcher.fetch(User.class, 1L).orElse(null));
            order2.user.setLoader(() -> fetcher.fetch(User.class, 2L).orElse(null));

            // when
            order1.user.get();
            order2.user.get();

            // then
            assertThat(fetchCount[0]).isEqualTo(2); // 两次单独 fetch
        }
    }

    @Nested
    class PluginIntegration {

        /// 测试目标: 验证 EntityReferencePlugin 正确处理 EntityReference 字段
        /// 测试场景: 使用 ExtensionRegistry 检测字段支持
        /// 预期结果: Plugin 支持 UserRef 字段
        @Test
        void plugin_ShouldSupportEntityReferenceField() {
            // given
            ExtensionRegistry registry = new DefaultExtensionRegistry();
            EntityReferencePlugin plugin = new EntityReferencePlugin();
            registry.registerHandler(plugin);

            FieldInfo field = createFieldInfo(UserRef.class, "user", OrderRef.class);
            ProjectionContext context = createMockProjectionContext(registry);

            // when
            boolean supports = plugin.supports(field, context);

            // then
            assertThat(supports).isTrue();
        }

        /// 测试目标: 验证 Plugin 可以创建 EntityReference 并注入 loader
        /// 测试场景: 使用 mapValue 和 postProcess 创建引用
        /// 预期结果: 引用可以延迟加载
        @Test
        void plugin_ShouldCreateAndInjectLoader() {
            // given
            ExtensionRegistry registry = new DefaultExtensionRegistry();
            registry.registerFetcher(createMockEntityFetcher());
            EntityReferencePlugin plugin = new EntityReferencePlugin();
            registry.registerHandler(plugin);

            FieldInfo field = createFieldInfo(UserRef.class, "user", OrderRef.class);
            FieldTypeDescriptor descriptor = plugin.resolveFieldType(field, createMockProjectionContext(registry));
            ProjectionContext context = createMockProjectionContextWithFetcher(registry);
            MockArguments arguments = new MockArguments(1L);

            // when - mapValue 创建引用
            Object value = plugin.mapValue(field, arguments, context, descriptor);
            EntityReference<?, ?> ref = (EntityReference<?, ?>) value;

            // then - ID 设置正确
            assertThat(ref.getId()).isEqualTo(1L);
            assertThat(ref.isLoaded()).isFalse();

            // when - postProcess 注入 loader
            plugin.postProcess(new OrderRef(), field, ref, context);

            // then - 可以延迟加载
            assertThat(ref.get()).isNotNull();
        }
    }

    // Mock implementations
    private EntityFetcher createMockEntityFetcher() {
        Map<Long, User> users = new HashMap<>();
        users.put(1L, new User(1L, "User1"));
        users.put(2L, new User(2L, "User2"));
        return new EntityFetcher() {
            @Override
            public <T, ID> Optional<T> fetch(Class<T> entityType, ID id) {
                User user = users.get(id);
                return user != null ? Optional.of(entityType.cast(user)) : Optional.empty();
            }

            @Override
            public <T, ID> Map<ID, T> fetchBatch(Class<T> entityType, java.util.Collection<ID> ids) {
                Map<ID, T> result = new HashMap<>();
                for (ID id : ids) {
                    User user = users.get(id);
                    if (user != null) {
                        result.put(id, entityType.cast(user));
                    }
                }
                return result;
            }

            @Override
            public <T> boolean supports(Class<T> entityType) {
                return entityType == User.class;
            }
        };
    }

    private EntityFetcher createCountingEntityFetcher(int[] fetchCount) {
        return new EntityFetcher() {
            @Override
            public <T, ID> Optional<T> fetch(Class<T> entityType, ID id) {
                fetchCount[0]++;
                return Optional.of(entityType.cast(new User((Long) id, "User" + id)));
            }

            @Override
            public <T, ID> Map<ID, T> fetchBatch(Class<T> entityType, java.util.Collection<ID> ids) {
                fetchCount[0]++;
                Map<ID, T> result = new HashMap<>();
                for (ID id : ids) {
                    result.put(id, entityType.cast(new User((Long) id, "User" + id)));
                }
                return result;
            }

            @Override
            public <T> boolean supports(Class<T> entityType) {
                return true;
            }
        };
    }

    private FieldInfo createFieldInfo(Class<?> type, String name, Class<?> projectionType) {
        io.github.nextentity.core.reflect.schema.Attribute attribute =
            new io.github.nextentity.core.reflect.schema.SimpleAttribute(type, name, null, null, null, null, 0);
        return FieldInfo.of(attribute, projectionType);
    }

    private ProjectionContext createMockProjectionContext(ExtensionRegistry registry) {
        return new ProjectionContext() {
            @Override
            public EntityFetcher entityFetcher() {
                return registry.getEntityFetcher();
            }

            @Override
            public io.github.nextentity.core.meta.EntityType entityType() {
                return null;
            }

            @Override
            public <ID> java.util.Collection<?> fetchEntities(Class<?> entityType, java.util.Collection<ID> ids) {
                return List.of();
            }

            @Override
            public <T, ID> java.util.function.Supplier<T> createLoader(Class<T> entityType, ID id) {
                return () -> null;
            }
        };
    }

    private ProjectionContext createMockProjectionContextWithFetcher(ExtensionRegistry registry) {
        EntityFetcher fetcher = registry.getEntityFetcher();
        return new ProjectionContext() {
            @Override
            public EntityFetcher entityFetcher() {
                return fetcher;
            }

            @Override
            public io.github.nextentity.core.meta.EntityType entityType() {
                return null;
            }

            @Override
            public <ID> java.util.Collection<?> fetchEntities(Class<?> entityType, java.util.Collection<ID> ids) {
                return fetcher.fetchBatch(entityType, ids).values();
            }

            @Override
            public <T, ID> java.util.function.Supplier<T> createLoader(Class<T> entityType, ID id) {
                return () -> fetcher.fetch(entityType, id).orElse(null);
            }
        };
    }

    // Mock Arguments
    static class MockArguments implements io.github.nextentity.jdbc.Arguments {
        private final Object value;
        private boolean consumed = false;

        MockArguments(Object value) {
            this.value = value;
        }

        @Override
        public Object get(int index, io.github.nextentity.core.meta.ValueConverter<?, ?> convertor) {
            return next(convertor);
        }

        @Override
        public Object next(io.github.nextentity.core.meta.ValueConverter<?, ?> convertor) {
            if (!consumed) {
                consumed = true;
                return value;
            }
            return null;
        }
    }

    // Test entities
    public static class User {
        Long id;
        String name;

        User(Long id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    public static class Order {
        Long id;
        Long userId;
    }

    // Test projections - must be public for reflection
    public static class OrderRef {
        public UserRef user;
    }

    public static class UserRef extends EntityReference<User, Long> {
        // 默认构造方法
        public UserRef() {}
    }
}