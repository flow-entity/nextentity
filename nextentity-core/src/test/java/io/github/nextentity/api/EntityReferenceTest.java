package io.github.nextentity.api;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/// 测试目标: 验证 EntityReference 基类功能
///
/// 测试场景:
/// 1. getId() 返回存储的 ID
/// 2. get() 延迟加载实体
/// 3. isLoaded() 判断是否已加载
/// 4. setId() 设置 ID
/// 5. setLoader() 设置延迟加载器
class EntityReferenceTest {

    @Nested
    class IdManagement {

        /// 测试目标: 验证 getId() 返回存储的 ID
        /// 测试场景: 创建引用并设置 ID
        /// 预期结果: getId() 返回设置的 ID
        @Test
        void getId_ShouldReturnStoredId() {
            // given
            TestEntityReference ref = new TestEntityReference();
            ref.setId(1L);

            // when
            Long id = ref.getId();

            // then
            assertThat(id).isEqualTo(1L);
        }

        /// 测试目标: 验证 setId() 可以更新 ID
        /// 测试场景: 先设置 ID 为 1，再设置为 2
        /// 预期结果: getId() 返回更新后的 ID
        @Test
        void setId_ShouldUpdateId() {
            // given
            TestEntityReference ref = new TestEntityReference();
            ref.setId(1L);

            // when
            ref.setId(2L);

            // then
            assertThat(ref.getId()).isEqualTo(2L);
        }

        /// 测试目标: 验证 ID 为 null 时 getId() 返回 null
        /// 测试场景: 创建引用不设置 ID
        /// 预期结果: getId() 返回 null
        @Test
        void getId_ShouldReturnNull_WhenNotSet() {
            // given
            TestEntityReference ref = new TestEntityReference();

            // when
            Long id = ref.getId();

            // then
            assertThat(id).isNull();
        }
    }

    @Nested
    class LazyLoading {

        /// 测试目标: 验证 get() 延迟加载实体
        /// 测试场景: 设置 ID 和 loader，调用 get()
        /// 预期结果: loader 被调用，返回实体
        @Test
        void get_ShouldLoadEntity_WhenLoaderSet() {
            // given
            TestEntity entity = new TestEntity(1L, "test");
            TestEntityReference ref = new TestEntityReference();
            ref.setId(1L);
            ref.setLoader(() -> entity);

            // when
            TestEntity result = ref.get();

            // then
            assertThat(result).isEqualTo(entity);
        }

        /// 测试目标: 验证 get() 只加载一次（缓存）
        /// 测试场景: 多次调用 get()
        /// 预期结果: loader 只被调用一次，后续返回缓存
        @Test
        void get_ShouldCacheEntity() {
            // given
            int[] loadCount = {0};
            TestEntity entity = new TestEntity(1L, "test");
            TestEntityReference ref = new TestEntityReference();
            ref.setId(1L);
            ref.setLoader(() -> {
                loadCount[0]++;
                return entity;
            });

            // when
            ref.get();
            ref.get();
            ref.get();

            // then
            assertThat(loadCount[0]).isEqualTo(1); // loader 只调用一次
        }

        /// 测试目标: 验证 get() 抛出异常当 ID 为 null
        /// 测试场景: 不设置 ID，调用 get()
        /// 预期结果: 抛出 IllegalStateException
        @Test
        void get_ShouldThrowException_WhenIdIsNull() {
            // given
            TestEntityReference ref = new TestEntityReference();
            ref.setLoader(() -> new TestEntity(1L, "test"));

            // when & then
            assertThatThrownBy(ref::get)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("id is null");
        }

        /// 测试目标: 验证 get() 抛出异常当 loader 为 null
        /// 测试场景: 设置 ID 但不设置 loader
        /// 预期结果: 抛出 IllegalStateException
        @Test
        void get_ShouldThrowException_WhenLoaderIsNull() {
            // given
            TestEntityReference ref = new TestEntityReference();
            ref.setId(1L);

            // when & then
            assertThatThrownBy(ref::get)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("loader is null");
        }

        /// 测试目标: 验证 get() 返回 null 当 loader 返回 null
        /// 测试场景: loader 返回 null（实体不存在）
        /// 预期结果: get() 返回 null
        @Test
        void get_ShouldReturnNull_WhenLoaderReturnsNull() {
            // given
            TestEntityReference ref = new TestEntityReference();
            ref.setId(1L);
            ref.setLoader(() -> null);

            // when
            TestEntity result = ref.get();

            // then
            assertThat(result).isNull();
        }
    }

    @Nested
    class LoadStatus {

        /// 测试目标: 验证 isLoaded() 返回 false 初始状态
        /// 测试场景: 创建引用不调用 get()
        /// 预期结果: isLoaded() 返回 false
        @Test
        void isLoaded_ShouldReturnFalse_Initially() {
            // given
            TestEntityReference ref = new TestEntityReference();
            ref.setId(1L);
            ref.setLoader(() -> new TestEntity(1L, "test"));

            // when
            boolean loaded = ref.isLoaded();

            // then
            assertThat(loaded).isFalse();
        }

        /// 测试目标: 验证 isLoaded() 返回 true 加载后
        /// 测试场景: 调用 get() 后检查 isLoaded()
        /// 预期结果: isLoaded() 返回 true
        @Test
        void isLoaded_ShouldReturnTrue_AfterGet() {
            // given
            TestEntityReference ref = new TestEntityReference();
            ref.setId(1L);
            ref.setLoader(() -> new TestEntity(1L, "test"));

            // when
            ref.get();

            // then
            assertThat(ref.isLoaded()).isTrue();
        }

        /// 测试目标: 验证 isLoaded() 返回 false 当 loader 返回 null
        /// 测试场景: loader 返回 null
        /// 预期结果: isLoaded() 返回 false（因为 entity 为 null）
        @Test
        void isLoaded_ShouldReturnFalse_WhenLoaderReturnsNull() {
            // given
            TestEntityReference ref = new TestEntityReference();
            ref.setId(1L);
            ref.setLoader(() -> null);

            // when
            ref.get();

            // then
            assertThat(ref.isLoaded()).isFalse();
        }
    }

    @Nested
    class OrElseGet {

        /// 测试目标: 验证 orElse() 返回实体或默认值
        /// 测试场景: loader 返回 null，提供默认值
        /// 预期结果: 返回默认值
        @Test
        void orElse_ShouldReturnDefault_WhenEntityNull() {
            // given
            TestEntity defaultEntity = new TestEntity(0L, "default");
            TestEntityReference ref = new TestEntityReference();
            ref.setId(1L);
            ref.setLoader(() -> null);

            // when
            TestEntity result = ref.orElse(defaultEntity);

            // then
            assertThat(result).isEqualTo(defaultEntity);
        }

        /// 测试目标: 验证 orElse() 返回实体当已加载
        /// 测试场景: loader 返回实体
        /// 预期结果: 返回加载的实体
        @Test
        void orElse_ShouldReturnEntity_WhenLoaded() {
            // given
            TestEntity entity = new TestEntity(1L, "test");
            TestEntity defaultEntity = new TestEntity(0L, "default");
            TestEntityReference ref = new TestEntityReference();
            ref.setId(1L);
            ref.setLoader(() -> entity);

            // when
            TestEntity result = ref.orElse(defaultEntity);

            // then
            assertThat(result).isEqualTo(entity);
        }
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

    // 测试 EntityReference 实现
    static class TestEntityReference extends EntityReference<TestEntity, Long> {
        // 使用默认实现
    }
}