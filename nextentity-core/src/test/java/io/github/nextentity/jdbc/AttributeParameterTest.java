package io.github.nextentity.jdbc;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test objective: Verify AttributeParameter correctly stores value and type
 * <p>
 * Test scenarios:
 * 1. Create parameter with value and type
 * 2. Verify toString output
 */
class AttributeParameterTest {

    @Test
    void attributeParameter_CreatesWithValueAndType() {
        // given
        Object value = "test";
        Class<String> type = String.class;

        // when
        AttributeParameter param = new AttributeParameter(value, type);

        // then
        assertThat(param.value()).isEqualTo("test");
        assertThat(param.type()).isEqualTo(String.class);
    }

    @Test
    void attributeParameter_ToString_ReturnsValueAsString() {
        // given
        AttributeParameter param = new AttributeParameter(42, Integer.class);

        // when
        String result = param.toString();

        // then
        assertThat(result).isEqualTo("42");
    }

    @Test
    void attributeParameter_NullValue_ToStringReturnsNull() {
        // given
        AttributeParameter param = new AttributeParameter(null, String.class);

        // when
        String result = param.toString();

        // then
        assertThat(result).isEqualTo("null");
    }
}
