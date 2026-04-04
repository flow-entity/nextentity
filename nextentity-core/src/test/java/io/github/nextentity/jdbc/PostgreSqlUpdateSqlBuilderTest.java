package io.github.nextentity.jdbc;

import io.github.nextentity.core.meta.EntityAttribute;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

///
/// 测试目标: 验证 PostgreSQL 方言通过 AbstractUpdateSqlBuilder 正确提供 SQL 语法
/// <p>
/// 测试场景:
/// 1. PostgreSQL 方言使用双引号作为引用字符
/// 2. 类型化占位符对日期类型返回 "::timestamp"
class PostgreSqlUpdateSqlBuilderTest {

    private final DefaultUpdateSqlBuilder builder = new DefaultUpdateSqlBuilder(SqlDialect.POSTGRESQL) {};

    @Nested
    class TickCharacters {

        @Test
        void leftTicks_ReturnsDoubleQuote() {
            // when
            String leftTicks = builder.leftTicks();

            // then
            assertThat(leftTicks).isEqualTo("\"");
        }

        @Test
        void rightTicks_ReturnsDoubleQuote() {
            // when
            String rightTicks = builder.rightTicks();

            // then
            assertThat(rightTicks).isEqualTo("\"");
        }
    }

    @Nested
    class TypedPlaceholder {

        @Test
        void typedPlaceholder_DateType_ReturnsTimestampCast() {
            // given
            EntityAttribute attribute = mock(EntityAttribute.class);
            doReturn(Date.class).when(attribute).type();

            // when
            String placeholder = builder.typedPlaceholder(attribute);

            // then
            assertThat(placeholder).isEqualTo("?::timestamp");
        }

        @Test
        void typedPlaceholder_TimestampType_ReturnsTimestampCast() {
            // given
            EntityAttribute attribute = mock(EntityAttribute.class);
            doReturn(java.sql.Timestamp.class).when(attribute).type();

            // when
            String placeholder = builder.typedPlaceholder(attribute);

            // then
            assertThat(placeholder).isEqualTo("?::timestamp");
        }

        @Test
        void typedPlaceholder_StringType_ReturnsQuestionMark() {
            // given
            EntityAttribute attribute = mock(EntityAttribute.class);
            doReturn(String.class).when(attribute).type();

            // when
            String placeholder = builder.typedPlaceholder(attribute);

            // then
            assertThat(placeholder).isEqualTo("?");
        }

        @Test
        void typedPlaceholder_IntegerType_ReturnsQuestionMark() {
            // given
            EntityAttribute attribute = mock(EntityAttribute.class);
            doReturn(Integer.class).when(attribute).type();

            // when
            String placeholder = builder.typedPlaceholder(attribute);

            // then
            assertThat(placeholder).isEqualTo("?");
        }

        @Test
        void typedPlaceholder_LongType_ReturnsQuestionMark() {
            // given
            EntityAttribute attribute = mock(EntityAttribute.class);
            doReturn(Long.class).when(attribute).type();

            // when
            String placeholder = builder.typedPlaceholder(attribute);

            // then
            assertThat(placeholder).isEqualTo("?");
        }
    }
}