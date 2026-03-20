package io.github.nextentity.jdbc;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test objective: Verify NullParameter correctly represents null values
 * <p>
 * Test scenarios:
 * 1. Create null parameter with type
 * 2. Verify value returns null
 * 3. Verify toString output
 */
class NullParameterTest {

    @Test
    void nullParameter_CreatesWithType() {
        // given
        Class<String> type = String.class;

        // when
        NullParameter param = new NullParameter(type);

        // then
        assertThat(param.type()).isEqualTo(String.class);
    }

    @Test
    void nullParameter_Value_ReturnsNull() {
        // given
        NullParameter param = new NullParameter(Integer.class);

        // when
        Object result = param.value();

        // then
        assertThat(result).isNull();
    }

    @Test
    void nullParameter_ToString_ReturnsTypeName() {
        // given
        NullParameter param = new NullParameter(String.class);

        // when
        String result = param.toString();

        // then
        assertThat(result).isEqualTo("(String)null");
    }

    @Test
    void nullParameter_ToString_PrimitiveType() {
        // given
        NullParameter param = new NullParameter(int.class);

        // when
        String result = param.toString();

        // then
        assertThat(result).isEqualTo("(int)null");
    }
}
