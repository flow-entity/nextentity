package io.github.nextentity.jdbc;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

///
 /// 测试目标: 验证y MySqlUpdateSqlBuilder 正确 provides MySQL-specific SQL syntax
 /// <p>
 /// 测试场景s:
 /// 1. Left tick character is backtick
 /// 2. Right tick character is backtick
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
