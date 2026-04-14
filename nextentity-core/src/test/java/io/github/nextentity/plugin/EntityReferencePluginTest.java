package io.github.nextentity.plugin;

import io.github.nextentity.api.*;
import io.github.nextentity.core.annotation.DirectReference;
import io.github.nextentity.core.reflect.schema.Attribute;
import io.github.nextentity.core.reflect.schema.SimpleAttribute;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/// 测试目标: 验证 EntityReferencePlugin 核心功能
///
/// 测试场景:
/// 1. supports() 判断 EntityReference 类型（排除 @DirectReference）
/// 2. resolveFieldType() 解析字段类型描述符
/// 3. mapValue() 从查询结果创建 EntityReference
/// 4. postProcess() 注入延迟加载器
class EntityReferencePluginTest {

    @Nested
    class SupportsFieldType {

        /// 测试目标: 验证 supports() 返回 true 对 EntityReference 类型
        /// 测试场景: 检查 EntityReference 子类类型，无 @DirectReference
        /// 预期结果: 返回 true
        @Test
        void supports_ShouldReturnTrue_ForEntityReferenceWithoutDirectReference() {
            // given
            EntityReferencePlugin plugin = createPlugin();
            FieldInfo field = createFieldInfo(TestUserRef.class, "user", TestProjection.class);
            ProjectionContext context = createMockContext();

            // when
            boolean result = plugin.supports(field, context);

            // then
            assertThat(result).isTrue();
        }

        /// 测试目标: 验证 supports() 返回 false 对非 EntityReference 类型
        /// 测试场景: 检查普通类型
        /// 预期结果: 返回 false
        @Test
        void supports_ShouldReturnFalse_ForNonEntityReferenceType() {
            // given
            EntityReferencePlugin plugin = createPlugin();
            FieldInfo field = createFieldInfo(String.class, "name", TestProjection.class);
            ProjectionContext context = createMockContext();

            // when
            boolean result = plugin.supports(field, context);

            // then
            assertThat(result).isFalse();
        }

        /// 测试目标: 验证 supports() 返回 false 对标注 @DirectReference 的字段
        /// 测试场景: EntityReference 字段标注了 @DirectReference
        /// 预期结果: 返回 false（不延迟加载，应立即加载）
        @Test
        void supports_ShouldReturnFalse_ForDirectReferenceAnnotatedField() {
            // given
            EntityReferencePlugin plugin = createPlugin();
            FieldInfo field = createFieldInfoWithAnnotation(
                    TestUserRef.class, "directUser", TestProjection.class, DirectReference.class);
            ProjectionContext context = createMockContext();

            // when
            boolean result = plugin.supports(field, context);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    class ResolveFieldType {

        /// 测试目标: 验证 resolveFieldType() 返回正确的描述符
        /// 测试场景: 解析 TestUserRef 字段
        /// 预期结果: targetType 为 User.class，idType 为 Long.class
        @Test
        void resolveFieldType_ShouldReturnCorrectDescriptor() {
            // given
            EntityReferencePlugin plugin = createPlugin();
            FieldInfo field = createFieldInfo(TestUserRef.class, "user", TestProjection.class);
            ProjectionContext context = createMockContext();

            // when
            FieldTypeDescriptor descriptor = plugin.resolveFieldType(field, context);

            // then
            assertThat(descriptor.fieldType()).isEqualTo(TestUserRef.class);
            assertThat(descriptor.targetType()).isEqualTo(User.class);
            assertThat(descriptor.idType()).isEqualTo(Long.class);
        }

        /// 测试目标: 验证 ID 来源路径默认为字段名 + "Id"
        /// 测试场景: 无 @ReferenceId 注解
        /// 预期结果: idSourcePath 为 "userId"
        @Test
        void resolveFieldType_ShouldUseDefaultIdPath() {
            // given
            EntityReferencePlugin plugin = createPlugin();
            FieldInfo field = createFieldInfo(TestUserRef.class, "user", TestProjection.class);
            ProjectionContext context = createMockContext();

            // when
            FieldTypeDescriptor descriptor = plugin.resolveFieldType(field, context);

            // then
            assertThat(descriptor.idSourcePath()).isEqualTo("userId");
        }
    }

    @Nested
    class MapValue {

        /// 测试目标: 验证 mapValue() 创建 EntityReference 并设置 ID
        /// 测试场景: 从 Arguments 提取 ID 创建引用
        /// 预期结果: 引用的 getId() 返回设置的 ID
        @Test
        void mapValue_ShouldCreateReferenceWithId() {
            // given
            EntityReferencePlugin plugin = createPlugin();
            FieldInfo field = createFieldInfo(TestUserRef.class, "user", TestProjection.class);
            FieldTypeDescriptor descriptor = createDescriptor(TestUserRef.class, User.class, Long.class);
            ProjectionContext context = createMockContext();
            MockArguments arguments = new MockArguments(1L);

            // when
            Object result = plugin.mapValue(field, arguments, context, descriptor);

            // then
            assertThat(result).isInstanceOf(TestUserRef.class);
            EntityReference<?, ?> ref = (EntityReference<?, ?>) result;
            assertThat(ref.getId()).isEqualTo(1L);
        }

        /// 测试目标: 验证 mapValue() 返回 null 当 ID 为 null
        /// 测试场景: Arguments 返回 null ID
        /// 预期结果: 返回 null
        @Test
        void mapValue_ShouldReturnNull_WhenIdIsNull() {
            // given
            EntityReferencePlugin plugin = createPlugin();
            FieldInfo field = createFieldInfo(TestUserRef.class, "user", TestProjection.class);
            FieldTypeDescriptor descriptor = createDescriptor(TestUserRef.class, User.class, Long.class);
            ProjectionContext context = createMockContext();
            MockArguments arguments = new MockArguments(null);

            // when
            Object result = plugin.mapValue(field, arguments, context, descriptor);

            // then
            assertThat(result).isNull();
        }
    }

    @Nested
    class PostProcess {

        /// 测试目标: 验证 postProcess() 注入延迟加载器
        /// 测试场景: 对单个 EntityReference 进行后处理
        /// 预期结果: 引用能通过 get() 加载实体
        @Test
        void postProcess_ShouldInjectLoader() {
            // given
            EntityReferencePlugin plugin = createPlugin();
            TestUserRef ref = new TestUserRef();
            ref.setId(1L);
            FieldInfo field = createFieldInfo(TestUserRef.class, "user", TestProjection.class);
            ProjectionContext context = createMockContextWithEntities(
                    new User(1L, "user1")
            );

            // when
            plugin.postProcess(null, field, ref, context);

            // then
            assertThat(ref.isLoaded()).isFalse(); // 未自动加载
            assertThat(ref.get().name).isEqualTo("user1"); // 延迟加载成功
        }

        /// 测试目标: 验证 postProcessBatch() 批量注入加载器
        /// 测试场景: 对多个 EntityReference 进行批量后处理
        /// 预期结果: 所有引用都能通过 get() 加载实体
        @Test
        void postProcessBatch_ShouldInjectLoader() {
            // given
            EntityReferencePlugin plugin = createPlugin();
            TestUserRef ref1 = new TestUserRef();
            ref1.setId(1L);
            TestUserRef ref2 = new TestUserRef();
            ref2.setId(2L);
            ProjectionContext context = createMockContextWithEntities(
                    new User(1L, "user1"),
                    new User(2L, "user2")
            );

            // when
            plugin.postProcessBatch(List.of(ref1, ref2), context);

            // then
            assertThat(ref1.get().name).isEqualTo("user1");
            assertThat(ref2.get().name).isEqualTo("user2");
        }
    }

    // Helper methods
    private EntityReferencePlugin createPlugin() {
        return new EntityReferencePlugin();
    }

    private FieldInfo createFieldInfo(Class<?> type, String name, Class<?> projectionType) {
        Attribute attribute = new SimpleAttribute(type, name, null, null, null, null, 0);
        return FieldInfo.of(attribute, projectionType);
    }

    private FieldInfo createFieldInfoWithAnnotation(Class<?> type, String name, Class<?> projectionType,
                                                      Class<? extends java.lang.annotation.Annotation> annotationClass) {
        // 创建带有模拟注解的 FieldInfo
        Attribute attribute = new SimpleAttribute(type, name, null, null, null, null, 0);
        return new FieldInfo() {
            @Override
            public Class<?> type() {
                return type;
            }

            @Override
            public String name() {
                return name;
            }

            @Override
            public Class<?> projectionType() {
                return projectionType;
            }

            @Override
            public <A extends java.lang.annotation.Annotation> A getAnnotation(Class<A> annotationClass) {
                // 模拟返回注解
                if (annotationClass == DirectReference.class) {
                    try {
                        return (A) DirectReference.class.getDeclaredConstructor().newInstance();
                    } catch (Exception e) {
                        return null;
                    }
                }
                return null;
            }

            @Override
            public boolean hasAnnotation(Class<? extends java.lang.annotation.Annotation> annotationClass) {
                return annotationClass == DirectReference.class;
            }

            @Override
            public java.lang.reflect.AnnotatedElement annotatedElement() {
                return null;
            }

            @Override
            public Field field() {
                return null;
            }

            @Override
            public java.lang.reflect.Method getter() {
                return null;
            }

            @Override
            public Attribute attribute() {
                return attribute;
            }
        };
    }

    private FieldTypeDescriptor createDescriptor(Class<?> fieldType, Class<?> targetType, Class<?> idType) {
        return FieldTypeDescriptor.builder()
                .fieldType(fieldType)
                .targetType(targetType)
                .idType(idType)
                .build();
    }

    private ProjectionContext createMockContext() {
        return new ProjectionContext() {
            @Override
            public EntityFetcher entityFetcher() {
                return new EntityFetcher() {
                    @Override
                    public <T, ID> Optional<T> fetch(Class<T> entityType, ID id) {
                        return Optional.empty();
                    }

                    @Override
                    public <T, ID> Map<ID, T> fetchBatch(Class<T> entityType, java.util.Collection<ID> ids) {
                        return Map.of();
                    }

                    @Override
                    public <T> boolean supports(Class<T> entityType) {
                        return true;
                    }
                };
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

    private ProjectionContext createMockContextWithEntities(User... users) {
        Map<Long, User> userMap = new java.util.HashMap<>();
        for (User u : users) {
            userMap.put(u.id, u);
        }
        return new ProjectionContext() {
            @Override
            public EntityFetcher entityFetcher() {
                return new EntityFetcher() {
                    @Override
                    public <T, ID> Optional<T> fetch(Class<T> entityType, ID id) {
                        User user = userMap.get(id);
                        return user != null ? Optional.of(entityType.cast(user)) : Optional.empty();
                    }

                    @Override
                    public <T, ID> Map<ID, T> fetchBatch(Class<T> entityType, java.util.Collection<ID> ids) {
                        Map<ID, T> result = new java.util.HashMap<>();
                        for (ID id : ids) {
                            User user = userMap.get(id);
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

            @Override
            public io.github.nextentity.core.meta.EntityType entityType() {
                return null;
            }

            @Override
            public <ID> java.util.Collection<?> fetchEntities(Class<?> entityType, java.util.Collection<ID> ids) {
                return ids.stream()
                        .map(userMap::get)
                        .filter(java.util.Objects::nonNull)
                        .toList();
            }

            @Override
            public <T, ID> java.util.function.Supplier<T> createLoader(Class<T> entityType, ID id) {
                User user = userMap.get(id);
                return () -> user != null ? entityType.cast(user) : null;
            }
        };
    }

    // Mock Arguments (implements Arguments interface)
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

    // Test classes
    static class User {
        Long id;
        String name;

        User(Long id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    static class TestProjection {
        TestUserRef user;
    }

    static class TestUserRef extends EntityReference<User, Long> {
    }
}