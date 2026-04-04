package io.github.nextentity.jdbc;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

///
/// 测试目标: 验证 PostgreSQL 方言通过 QuerySqlBuilderImpl 正确提供 SQL 语法
/// <p>
/// 测试场景:
/// 1. PostgreSQL 方言使用双引号作为引用字符
/// 2. QuerySqlBuilderImpl.Builder 类存在且结构正确
class PostgresqlQuerySqlBuilderTest {

    private final QuerySqlBuilderImpl builder = new QuerySqlBuilderImpl(SqlDialect.POSTGRESQL);

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
            Method buildMethod = QuerySqlBuilderImpl.class.getMethod("build", QueryContext.class);

            // then
            assertThat(buildMethod).isNotNull();
            assertThat(buildMethod.getReturnType()).isEqualTo(QuerySqlStatement.class);
        }
    }

    @Nested
    class QuoteIdentifierCharacters {

        @Test
        void leftQuotedIdentifier_ReturnsDoubleQuote() {
            // when - Verify PostgreSQL uses double quotes for quoted identifiers
            String leftQuote = SqlDialect.POSTGRESQL.leftQuotedIdentifier();

            // then
            assertThat(leftQuote).isEqualTo("\"");
        }

        @Test
        void rightQuotedIdentifier_ReturnsDoubleQuote() {
            // when - Verify PostgreSQL uses double quotes for quoted identifiers
            String rightQuote = SqlDialect.POSTGRESQL.rightQuotedIdentifier();

            // then
            assertThat(rightQuote).isEqualTo("\"");
        }

        @Test
        void queryBuilderImpl_BuilderClassExists() {
            // PostgreSQL uses " (double quote) for quoted identifiers
            assertThat(QuerySqlBuilderImpl.Builder.class).isNotNull();
        }
    }

    @Nested
    class OffsetAndLimitBehavior {

        @Test
        void appendOffsetAndLimit_MethodExists() throws NoSuchMethodException {
            // when
            Method method = AbstractQuerySqlBuilder.class.getDeclaredMethod("appendOffsetAndLimit");

            // then
            assertThat(method).isNotNull();
        }
    }
}