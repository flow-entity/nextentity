package io.github.nextentity.jdbc;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

///
/// 测试目标: 验证 SQL Server 方言通过 AbstractUpdateSqlBuilder 正确提供 SQL 语法
/// <p>
/// 测试场景:
/// 1. SQL Server 方言使用方括号作为引用字符
class SqlServerUpdateSqlBuilderTest {

    private final DefaultUpdateSqlBuilder builder = new DefaultUpdateSqlBuilder(SqlDialect.SQL_SERVER) {};

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