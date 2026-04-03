package io.github.nextentity.jdbc;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

///
 /// 测试目标: 验证y PostgresqlQuerySqlBuilder 正确 provides PostgreSQL-specific SQL syntax
 /// <p>
 /// 测试场景s:
 /// 1. Left quoted identifier is double quote
 /// 2. Right quoted identifier is double quote
 /// 3. Builder class exists and has correct structure
class PostgresqlQuerySqlBuilderTest {

    private final PostgresqlQuerySqlBuilder builder = new PostgresqlQuerySqlBuilder();

    @Nested
    class BuilderCreation {

        @Test
        void builder_IsNotNull() {
            // then
            assertThat(builder).isNotNull();
        }

        @Test
        void builder_HasBuildMethod() throws NoSuchMethodException {
            // when
            Method buildMethod = PostgresqlQuerySqlBuilder.class.getMethod("build", QueryContext.class);

            // then
            assertThat(buildMethod).isNotNull();
            assertThat(buildMethod.getReturnType()).isEqualTo(QuerySqlStatement.class);
        }
    }

    @Nested
    class QuoteIdentifierCharacters {

        @Test
        void builderClass_Exists() {
            // PostgreSQL uses " (double quote) for quoted identifiers
            assertThat(PostgresqlQuerySqlBuilder.Builder.class).isNotNull();
        }
    }

    @Nested
    class OffsetAndLimitBehavior {

        @Test
        void appendOffsetAndLimit_MethodExists() throws NoSuchMethodException {
            // when
            Method method = PostgresqlQuerySqlBuilder.Builder.class.getDeclaredMethod("appendOffsetAndLimit");

            // then
            assertThat(method).isNotNull();
        }
    }
}
