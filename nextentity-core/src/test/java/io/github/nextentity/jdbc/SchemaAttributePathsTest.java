package io.github.nextentity.jdbc;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

///
/// 测试目标: 验证y SchemaAttributePaths interface and static 方法
/// <p>
/// 测试场景s:
/// 1. 验证y empty() returns empty paths
class SchemaAttributePathsTest {

    @Test
    void schemaAttributePaths_Empty_ReturnsEmptyInstance() {
        // when
        SchemaAttributePaths paths = SchemaAttributePaths.empty();

        // then
        assertThat(paths).isNotNull();
        assertThat(paths.isEmpty()).isTrue();
    }

    @Test
    void schemaAttributePaths_Empty_GetReturnsNull() {
        // given
        SchemaAttributePaths paths = SchemaAttributePaths.empty();

        // when
        SchemaAttributePaths result = paths.get("any");

        // then
        assertThat(result).isNull();
    }
}
