package io.github.nextentity.jdbc;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test objective: Verify MySqlUpdateSqlBuilder correctly provides MySQL-specific SQL syntax
 * <p>
 * Test scenarios:
 * 1. Left tick character is backtick
 * 2. Right tick character is backtick
 */
class MySqlUpdateSqlBuilderTest {

    private final MySqlUpdateSqlBuilder builder = new MySqlUpdateSqlBuilder();

    @Nested
    class TickCharacters {

        @Test
        void leftTicks_ReturnsBacktick() {
            // when
            String leftTicks = builder.leftTicks();

            // then
            assertThat(leftTicks).isEqualTo("`");
        }

        @Test
        void rightTicks_ReturnsBacktick() {
            // when
            String rightTicks = builder.rightTicks();

            // then
            assertThat(rightTicks).isEqualTo("`");
        }
    }
}
