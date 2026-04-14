package io.github.nextentity.api;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/// 测试目标: 验证 EntityFetcher 接口契约和实现类行为
///
/// 测试场景:
/// 1. fetch() 返回 Optional 结果
/// 2. fetchBatch() 返回 ID -> 实体映射
/// 3. supports() 判断实体类型支持
class EntityFetcherTest {

    @Nested
    class FetchSingleEntity {

        /// 测试目标: 验证 fetch() 返回 Optional.empty() 当实体不存在
        /// 测试场景: 调用 fetch() 查询不存在的 ID
        /// 预期结果: 返回 Optional.empty()
        @Test
        void fetch_ShouldReturnEmpty_WhenEntityNotFound() {
            // given
            EntityFetcher fetcher = createMockFetcher();

            // when
            Optional<Object> result = fetcher.fetch(Object.class, 999L);

            // then
            assertThat(result).isEmpty();
        }

        /// 测试目标: 验证 fetch() 返回 Optional.of() 当实体存在
        /// 测试场景: 调用 fetch() 查询存在的 ID
        /// 预期结果: 返回包含实体的 Optional
        @Test
        void fetch_ShouldReturnEntity_WhenEntityExists() {
            // given
            TestEntity entity = new TestEntity(1L, "test");
            EntityFetcher fetcher = createMockFetcherWithEntity(entity);

            // when
            Optional<TestEntity> result = fetcher.fetch(TestEntity.class, 1L);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().id).isEqualTo(1L);
        }
    }

    @Nested
    class FetchBatchEntities {

        /// 测试目标: 验证 fetchBatch() 返回 ID -> 实体映射
        /// 测试场景: 批量查询多个实体
        /// 预期结果: 返回包含所有实体的映射
        @Test
        void fetchBatch_ShouldReturnIdToEntityMap() {
            // given
            TestEntity entity1 = new TestEntity(1L, "test1");
            TestEntity entity2 = new TestEntity(2L, "test2");
            EntityFetcher fetcher = createMockFetcherWithEntities(entity1, entity2);

            // when
            Map<Long, TestEntity> result = fetcher.fetchBatch(TestEntity.class, java.util.List.of(1L, 2L));

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(1L).name).isEqualTo("test1");
            assertThat(result.get(2L).name).isEqualTo("test2");
        }

        /// 测试目标: 验证 fetchBatch() 处理空 ID 集合
        /// 测试场景: 批量查询空集合
        /// 预期结果: 返回空映射
        @Test
        void fetchBatch_ShouldReturnEmptyMap_WhenIdsEmpty() {
            // given
            EntityFetcher fetcher = createMockFetcher();

            // when
            Map<Long, TestEntity> result = fetcher.fetchBatch(TestEntity.class, java.util.List.of());

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    class SupportsEntityType {

        /// 测试目标: 验证 supports() 返回 true 当类型支持
        /// 测试场景: 检查支持的实体类型
        /// 预期结果: 返回 true
        @Test
        void supports_ShouldReturnTrue_WhenTypeSupported() {
            // given
            EntityFetcher fetcher = createMockFetcher();

            // when
            boolean result = fetcher.supports(TestEntity.class);

            // then
            assertThat(result).isTrue();
        }
    }

    // Mock 实现用于测试
    private EntityFetcher createMockFetcher() {
        return new EntityFetcher() {
            @Override
            public <T, ID> Optional<T> fetch(Class<T> entityType, ID id) {
                return Optional.empty();
            }

            @Override
            public <T, ID> Map<ID, T> fetchBatch(Class<T> entityType, Collection<ID> ids) {
                return Map.of();
            }

            @Override
            public <T> boolean supports(Class<T> entityType) {
                return true;
            }
        };
    }

    private EntityFetcher createMockFetcherWithEntity(TestEntity entity) {
        return new EntityFetcher() {
            @Override
            public <T, ID> Optional<T> fetch(Class<T> entityType, ID id) {
                if (id.equals(entity.id)) {
                    return Optional.of(entityType.cast(entity));
                }
                return Optional.empty();
            }

            @Override
            public <T, ID> Map<ID, T> fetchBatch(Class<T> entityType, Collection<ID> ids) {
                Map<ID, T> result = new java.util.HashMap<>();
                for (ID id : ids) {
                    if (id.equals(entity.id)) {
                        result.put(id, entityType.cast(entity));
                    }
                }
                return result;
            }

            @Override
            public <T> boolean supports(Class<T> entityType) {
                return entityType == TestEntity.class;
            }
        };
    }

    private EntityFetcher createMockFetcherWithEntities(TestEntity... entities) {
        Map<Long, TestEntity> entityMap = new java.util.HashMap<>();
        for (TestEntity e : entities) {
            entityMap.put(e.id, e);
        }
        return new EntityFetcher() {
            @Override
            public <T, ID> Optional<T> fetch(Class<T> entityType, ID id) {
                TestEntity entity = entityMap.get(id);
                if (entity != null) {
                    return Optional.of(entityType.cast(entity));
                }
                return Optional.empty();
            }

            @Override
            public <T, ID> Map<ID, T> fetchBatch(Class<T> entityType, Collection<ID> ids) {
                Map<ID, T> result = new java.util.HashMap<>();
                for (ID id : ids) {
                    TestEntity entity = entityMap.get(id);
                    if (entity != null) {
                        result.put(id, entityType.cast(entity));
                    }
                }
                return result;
            }

            @Override
            public <T> boolean supports(Class<T> entityType) {
                return entityType == TestEntity.class;
            }
        };
    }

    // 测试实体类
    static class TestEntity {
        Long id;
        String name;

        TestEntity(Long id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}