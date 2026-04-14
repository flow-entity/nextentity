package io.github.nextentity.api;

import io.github.nextentity.plugin.DefaultExtensionRegistry;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/// 测试目标: 验证 ExtensionRegistry 接口契约和实现类行为
///
/// 测试场景:
/// 1. registerHandler() 注册处理器
/// 2. getHandlers() 按优先级排序返回
/// 3. getHandler() 返回支持的处理器（使用 FieldInfo）
/// 4. registerFetcher() 注册实体获取器
class ExtensionRegistryTest {

    @Nested
    class RegisterHandler {

        /// 测试目标: 验证 registerHandler() 注册处理器成功
        /// 测试场景: 注册一个处理器
        /// 预期结果: getHandlers() 返回包含该处理器
        @Test
        void registerHandler_ShouldAddHandler() {
            // given
            ExtensionRegistry registry = new DefaultExtensionRegistry();
            ProjectionFieldHandler<?> handler = createMockHandlerWithSupport(Object.class, 100);

            // when
            registry.registerHandler(handler);

            // then
            assertThat(registry.getHandlers()).contains(handler);
        }

        /// 测试目标: 验证处理器按优先级排序
        /// 测试场景: 注册多个处理器，不同优先级
        /// 预期结果: 低优先级数值的处理器排在前面
        @Test
        void getHandlers_ShouldSortByPriority() {
            // given
            ExtensionRegistry registry = new DefaultExtensionRegistry();
            ProjectionFieldHandler<?> handler1 = createMockHandler(200);
            ProjectionFieldHandler<?> handler2 = createMockHandler(100);

            // when
            registry.registerHandler(handler1);
            registry.registerHandler(handler2);

            // then
            List<ProjectionFieldHandler<?>> handlers = registry.getHandlers();
            assertThat(handlers).hasSize(2);
            assertThat(handlers.get(0)).isEqualTo(handler2); // order=100 在前
            assertThat(handlers.get(1)).isEqualTo(handler1); // order=200 在后
        }
    }

    @Nested
    class GetHandlerForField {

        /// 测试目标: 验证 getHandler() 返回支持的处理器
        /// 测试场景: 查询支持 String 类型的处理器
        /// 预期结果: 返回第一个支持的处理器
        @Test
        void getHandler_ShouldReturnSupportedHandler() {
            // given
            ExtensionRegistry registry = new DefaultExtensionRegistry();
            ProjectionFieldHandler<?> stringHandler = createMockHandlerWithSupport(String.class, 100);
            ProjectionFieldHandler<?> objectHandler = createMockHandlerWithSupport(Object.class, 200);
            registry.registerHandler(stringHandler);
            registry.registerHandler(objectHandler);
            FieldInfo field = createFieldInfo(String.class, "name");
            ProjectionContext context = createMockContext();

            // when
            Optional<ProjectionFieldHandler<?>> result = registry.getHandler(field, context);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(stringHandler);
        }

        /// 测试目标: 验证 getHandler() 返回 empty 当没有支持的处理器
        /// 测试场景: 查询不支持的类型
        /// 预期结果: 返回 Optional.empty()
        @Test
        void getHandler_ShouldReturnEmpty_WhenNoHandlerSupports() {
            // given
            ExtensionRegistry registry = new DefaultExtensionRegistry();
            ProjectionFieldHandler<?> stringHandler = createMockHandlerWithSupport(String.class, 100);
            registry.registerHandler(stringHandler);
            FieldInfo field = createFieldInfo(Integer.class, "count");
            ProjectionContext context = createMockContext();

            // when
            Optional<ProjectionFieldHandler<?>> result = registry.getHandler(field, context);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    class RegisterFetcher {

        /// 测试目标: 验证 registerFetcher() 注册获取器成功
        /// 测试场景: 注册 EntityFetcher
        /// 预期结果: getEntityFetcher() 返回该获取器
        @Test
        void registerFetcher_ShouldSetEntityFetcher() {
            // given
            ExtensionRegistry registry = new DefaultExtensionRegistry();
            EntityFetcher fetcher = createMockFetcher();

            // when
            registry.registerFetcher(fetcher);

            // then
            assertThat(registry.getEntityFetcher()).isEqualTo(fetcher);
        }
    }

    // Mock implementations
    private ProjectionFieldHandler<?> createMockHandler(int order) {
        return createMockHandlerWithSupport(Object.class, order);
    }

    private ProjectionFieldHandler<?> createMockHandlerWithSupport(Class<?> supportedType, int order) {
        return new ProjectionFieldHandler<Object>() {
            @Override
            public boolean supports(FieldInfo field, ProjectionContext context) {
                return field.type() == supportedType;
            }

            @Override
            public FieldTypeDescriptor resolveFieldType(FieldInfo field, ProjectionContext context) {
                return FieldTypeDescriptor.builder().build();
            }

            @Override
            public int order() {
                return order;
            }
        };
    }

    private EntityFetcher createMockFetcher() {
        return new EntityFetcher() {
            @Override
            public <T, ID> java.util.Optional<T> fetch(Class<T> entityType, ID id) {
                return java.util.Optional.empty();
            }

            @Override
            public <T, ID> java.util.Map<ID, T> fetchBatch(Class<T> entityType, java.util.Collection<ID> ids) {
                return java.util.Map.of();
            }

            @Override
            public <T> boolean supports(Class<T> entityType) {
                return true;
            }
        };
    }

    private FieldInfo createFieldInfo(Class<?> type, String name) {
        io.github.nextentity.core.reflect.schema.Attribute attribute =
            new io.github.nextentity.core.reflect.schema.SimpleAttribute(type, name, null, null, null, null, 0);
        return FieldInfo.of(attribute, Object.class);
    }

    private ProjectionContext createMockContext() {
        return new ProjectionContext() {
            @Override
            public EntityFetcher entityFetcher() {
                return createMockFetcher();
            }

            @Override
            public io.github.nextentity.core.meta.EntityType entityType() {
                return null;
            }

            @Override
            public <ID> java.util.Collection<?> fetchEntities(Class<?> entityType, java.util.Collection<ID> ids) {
                return java.util.List.of();
            }

            @Override
            public <T, ID> java.util.function.Supplier<T> createLoader(Class<T> entityType, ID id) {
                return () -> null;
            }
        };
    }
}