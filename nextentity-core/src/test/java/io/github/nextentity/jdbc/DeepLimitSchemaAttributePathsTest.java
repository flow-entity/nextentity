package io.github.nextentity.jdbc;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

///
 /// 测试目标: 验证y DeepLimitSchemaAttributePaths factory 方法
 /// <p>
 /// 测试场景s:
 /// 1. Create paths with limit
 /// 2. 验证y isEmpty returns correct value
 /// 3. 验证y get 方法
class DeepLimitSchemaAttributePathsTest {

    @Nested
    class FactoryMethod {

        @Test
        void of_Zero_ReturnsInstance() {
            // when
            SchemaAttributePaths paths = DeepLimitSchemaAttributePaths.of(0);

            // then
            assertThat(paths).isNotNull();
        }

        @Test
        void of_PositiveLimit_ReturnsInstance() {
            // when
            SchemaAttributePaths paths = DeepLimitSchemaAttributePaths.of(5);

            // then
            assertThat(paths).isNotNull();
        }

        @Test
        void of_Negative_ReturnsEmpty() {
            // when
            SchemaAttributePaths paths = DeepLimitSchemaAttributePaths.of(-1);

            // then
            assertThat(paths).isSameAs(SchemaAttributePaths.empty());
        }
    }

    @Nested
    class IsEmptyTest {

        @Test
        void isEmpty_Zero_ReturnsFalse() {
            // given
            SchemaAttributePaths paths = DeepLimitSchemaAttributePaths.of(0);

            // then
            assertThat(paths.isEmpty()).isFalse();
        }

        @Test
        void isEmpty_Positive_ReturnsFalse() {
            // given
            SchemaAttributePaths paths = DeepLimitSchemaAttributePaths.of(1);

            // then
            assertThat(paths.isEmpty()).isFalse();
        }
    }

    @Nested
    class GetMethod {

        @Test
        void get_ZeroLimit_ReturnsEmptyPaths() {
            // given
            SchemaAttributePaths paths = DeepLimitSchemaAttributePaths.of(0);

            // when
            SchemaAttributePaths result = paths.get("any");

            // then
            assertThat(result).isSameAs(SchemaAttributePaths.empty());
        }

        @Test
        void get_PositiveLimit_ReturnsInstance() {
            // given
            SchemaAttributePaths paths = DeepLimitSchemaAttributePaths.of(1);

            // when
            SchemaAttributePaths result = paths.get("path");

            // then
            assertThat(result).isNotNull();
            assertThat(result.isEmpty()).isFalse();
        }
    }
}
