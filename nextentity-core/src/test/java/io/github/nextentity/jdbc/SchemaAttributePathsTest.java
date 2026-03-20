package io.github.nextentity.jdbc;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test objective: Verify SchemaAttributePaths interface and static methods
 * <p>
 * Test scenarios:
 * 1. Verify empty() returns empty paths
 */
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
