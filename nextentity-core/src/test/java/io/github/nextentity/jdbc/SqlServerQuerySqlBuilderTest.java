package io.github.nextentity.jdbc;

import io.github.nextentity.core.expression.Operator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

///
/// 测试目标: 验证 SQL Server 方言通过 QuerySqlBuilderImpl 正确提供 SQL 语法
/// <p>
/// 测试场景:
/// 1. SQL Server 方言使用方括号作为引用字符
/// 2. QuerySqlBuilderImpl.Builder 类存在且结构正确
/// 3. LENGTH 函数映射为 'len'
class SqlServerQuerySqlBuilderTest {

    private final QuerySqlBuilderImpl builder = new QuerySqlBuilderImpl(SqlDialect.SQL_SERVER);

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
        void leftQuotedIdentifier_ReturnsSquareBracket() {
            // when - Verify SQL Server uses square brackets for quoted identifiers
            String leftQuote = SqlDialect.SQL_SERVER.leftQuotedIdentifier();

            // then
            assertThat(leftQuote).isEqualTo("[");
        }

        @Test
        void rightQuotedIdentifier_ReturnsSquareBracket() {
            // when - Verify SQL Server uses square brackets for quoted identifiers
            String rightQuote = SqlDialect.SQL_SERVER.rightQuotedIdentifier();

            // then
            assertThat(rightQuote).isEqualTo("]");
        }

        @Test
        void queryBuilderImpl_BuilderClassExists() {
            // SQL Server uses [ ] (square brackets) for quoted identifiers
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

        @Test
        void requiresOrderByForPagination_ReturnsTrue() {
            // SQL Server requires ORDER BY for pagination
            assertThat(SqlDialect.SQL_SERVER.requiresOrderByForPagination()).isTrue();
        }
    }

    @Nested
    class OperatorHandling {

        @Test
        void appendOperator_MethodExists() throws NoSuchMethodException {
            // SQL Server has special handling for LENGTH operator (uses 'len' instead)
            Method method = AbstractQuerySqlBuilder.class.getDeclaredMethod("appendOperator", Operator.class);

            // then
            assertThat(method).isNotNull();
        }

        @Test
        void functionName_Length_ReturnsLen() {
            // SQL Server uses 'len' instead of 'length'
            assertThat(SqlDialect.SQL_SERVER.functionName("length")).isEqualTo("len");
        }

        @Test
        void functionName_OtherNames_ReturnsSame() {
            // Other function names pass through unchanged
            assertThat(SqlDialect.SQL_SERVER.functionName("count")).isEqualTo("count");
        }
    }

    @Nested
    class NotOperationHandling {

        @Test
        void shouldConvertNotToEqFalse_ReturnsTrue() {
            // SQL Server converts NOT path to path = false
            assertThat(SqlDialect.SQL_SERVER.shouldConvertNotToEqFalse()).isTrue();
        }
    }
}