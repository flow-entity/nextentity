package io.github.nextentity.jdbc;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

///
 /// 测试目标: 验证y DefaultSchemaAttributePaths 正确 manages paths
 /// <p>
 /// 测试场景s:
 /// 1. Add paths
 /// 2. Get paths
 /// 3. Check inheritance
class DefaultSchemaAttributePathsTest {

    @Nested
    class AddPaths {

        @Test
        void add_SinglePathElement_CreatesNestedPath() {
            // given
            DefaultSchemaAttributePaths paths = new DefaultSchemaAttributePaths();

            // when
            paths.add(Arrays.asList("user"));

            // then
            assertThat(paths.get("user")).isNotNull();
        }

        @Test
        void add_MultiplePathElements_CreatesNestedPath() {
            // given
            DefaultSchemaAttributePaths paths = new DefaultSchemaAttributePaths();

            // when
            paths.add(Arrays.asList("user", "address", "city"));

            // then
            SchemaAttributePaths user = paths.get("user");
            assertThat(user).isNotNull();
            SchemaAttributePaths address = user.get("address");
            assertThat(address).isNotNull();
        }

        @Test
        void add_MultiplePaths_BuildsCorrectTree() {
            // given
            DefaultSchemaAttributePaths paths = new DefaultSchemaAttributePaths();

            // when
            paths.add(Arrays.asList("a", "b"));
            paths.add(Arrays.asList("a", "c"));

            // then
            SchemaAttributePaths a = paths.get("a");
            assertThat(a.get("b")).isNotNull();
            assertThat(a.get("c")).isNotNull();
        }
    }

    @Nested
    class GetPaths {

        @Test
        void get_NonExistentPath_ReturnsNull() {
            // given
            DefaultSchemaAttributePaths paths = new DefaultSchemaAttributePaths();

            // when
            SchemaAttributePaths result = paths.get("nonexistent");

            // then
            assertThat(result).isNull();
        }

        @Test
        void get_AfterAdding_ReturnsCorrectPath() {
            // given
            DefaultSchemaAttributePaths paths = new DefaultSchemaAttributePaths();
            paths.add(Arrays.asList("x", "y"));

            // when
            SchemaAttributePaths result = paths.get("x");

            // then
            assertThat(result).isNotNull();
        }
    }

    @Nested
    class InheritanceTest {

        @Test
        void defaultSchemaAttributePaths_ExtendsHashMap() {
            // given
            DefaultSchemaAttributePaths paths = new DefaultSchemaAttributePaths();

            // when
            paths.put("key", new DefaultSchemaAttributePaths());

            // then
            assertThat(paths.get("key")).isNotNull();
        }
    }
}
