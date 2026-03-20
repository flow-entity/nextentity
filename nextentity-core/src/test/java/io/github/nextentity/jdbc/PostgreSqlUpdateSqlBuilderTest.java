package io.github.nextentity.jdbc;

import io.github.nextentity.core.meta.EntityAttribute;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Test objective: Verify PostgreSqlUpdateSqlBuilder correctly provides PostgreSQL-specific SQL syntax
 * <p>
 * Test scenarios:
 * 1. Left tick character is double quote
 * 2. Right tick character is double quote
 * 3. Typed placeholder for Date types
 * 4. Typed placeholder for non-Date types
 */
class PostgreSqlUpdateSqlBuilderTest {

    private final PostgreSqlUpdateSqlBuilder builder = new PostgreSqlUpdateSqlBuilder();

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
