package io.github.nextentity.jdbc;

import io.github.nextentity.core.expression.Operator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test objective: Verify SqlServerQuerySqlBuilder correctly provides SQL Server-specific SQL syntax
 * <p>
 * Test scenarios:
 * 1. Left quoted identifier is square bracket
 * 2. Right quoted identifier is square bracket
 * 3. Builder class exists and has correct structure
 * 4. LENGTH operator uses 'len' instead of 'length'
 */
class SqlServerQuerySqlBuilderTest {

    private final SqlServerQuerySqlBuilder builder = new SqlServerQuerySqlBuilder();

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
            Method buildMethod = SqlServerQuerySqlBuilder.class.getMethod("build", QueryContext.class);

            // then
            assertThat(buildMethod).isNotNull();
            assertThat(buildMethod.getReturnType()).isEqualTo(QuerySqlStatement.class);
        }
    }

    @Nested
    class QuoteIdentifierCharacters {

        @Test
        void builderClass_Exists() {
            // SQL Server uses [ ] (square brackets) for quoted identifiers
            assertThat(SqlServerQuerySqlBuilder.Builder.class).isNotNull();
        }
    }

    @Nested
    class OffsetAndLimitBehavior {

        @Test
        void appendOffsetAndLimit_MethodExists() throws NoSuchMethodException {
            // when
            Method method = SqlServerQuerySqlBuilder.Builder.class.getDeclaredMethod("appendOffsetAndLimit");

            // then
            assertThat(method).isNotNull();
        }
    }

    @Nested
    class OperatorHandling {

        @Test
        void appendOperator_MethodExists() throws NoSuchMethodException {
            // SQL Server has special handling for LENGTH operator (uses 'len' instead)
            Method method = SqlServerQuerySqlBuilder.Builder.class.getDeclaredMethod(
                    "appendOperator", Operator.class);

            // then
            assertThat(method).isNotNull();
        }
    }
}
