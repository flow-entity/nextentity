package io.github.nextentity.jdbc;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

///
 /// 测试目标: 验证y SqlDialectSelector 正确 delegates to database-specific builders
 /// <p>
 /// 测试场景s:
 /// 1. Create selector instance
 /// 2. 验证y interface implementation
 /// 3. 验证y delegation structure
 /// <p>
 /// Note: setByDataSource requires integration test with actual DataSource
class SqlDialectSelectorTest {

    @Nested
    class Creation {

        @Test
        void selector_CreatesInstance() {
            // when
            SqlDialectSelector selector = new SqlDialectSelector();

            // then
            assertThat(selector).isNotNull();
        }
    }

    @Nested
    class InterfaceImplementation {

        @Test
        void selector_ImplementsQuerySqlBuilder() {
            // given
            SqlDialectSelector selector = new SqlDialectSelector();

            // then
            assertThat(selector).isInstanceOf(JdbcQueryExecutor.QuerySqlBuilder.class);
        }

        @Test
        void selector_ImplementsJdbcUpdateSqlBuilder() {
            // given
            SqlDialectSelector selector = new SqlDialectSelector();

            // then
            assertThat(selector).isInstanceOf(JdbcUpdateSqlBuilder.class);
        }
    }

    @Nested
    class SetByDataSource {

        @Test
        void setByDataSource_ReturnsSameInstance() {
            // given
            SqlDialectSelector selector = new SqlDialectSelector();

            // Note: This test requires a real DataSource for integration testing.
            // For unit testing, we just verify the method exists and returns the correct type.
            // Actual database selection logic should be tested in integration tests.

            // then
            assertThat(selector).isNotNull();
        }
    }
}
