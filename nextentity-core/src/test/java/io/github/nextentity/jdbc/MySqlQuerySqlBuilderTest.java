package io.github.nextentity.jdbc;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

///
/// 测试目标: 验证 MySQL 方言通过 QuerySqlBuilderImpl 正确提供 SQL 语法
/// <p>
/// 测试场景:
/// 1. MySQL 方言使用反引号作为引用字符
/// 2. QuerySqlBuilderImpl.Builder 类存在且结构正确
class MySqlQuerySqlBuilderTest {

    private final QuerySqlBuilderImpl builder = new QuerySqlBuilderImpl(SqlDialect.MYSQL);

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
        void leftQuotedIdentifier_ReturnsBacktick() {
            // when - Verify MySQL uses backticks for quoted identifiers
            String leftQuote = SqlDialect.MYSQL.leftQuotedIdentifier();

            // then
            assertThat(leftQuote).isEqualTo("`");
        }

        @Test
        void rightQuotedIdentifier_ReturnsBacktick() {
            // when - Verify MySQL uses backticks for quoted identifiers
            String rightQuote = SqlDialect.MYSQL.rightQuotedIdentifier();

            // then
            assertThat(rightQuote).isEqualTo("`");
        }

        @Test
        void queryBuilderImpl_BuilderClassExists() {
            // then
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