package io.github.nextentity.jdbc;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test objective: Verify SqlServerUpdateSqlBuilder correctly provides SQL Server-specific SQL syntax
 * <p>
 * Test scenarios:
 * 1. Left tick character is square bracket
 * 2. Right tick character is square bracket
 */
class SqlServerUpdateSqlBuilderTest {

    private final SqlServerUpdateSqlBuilder builder = new SqlServerUpdateSqlBuilder();

    @Nested
    class TickCharacters {

        @Test
        void leftTicks_ReturnsSquareBracket() {
            // when
            String leftTicks = builder.leftTicks();

            // then
            assertThat(leftTicks).isEqualTo("[");
        }

        @Test
        void rightTicks_ReturnsSquareBracket() {
            // when
            String rightTicks = builder.rightTicks();

            // then
            assertThat(rightTicks).isEqualTo("]");
        }
    }
}
