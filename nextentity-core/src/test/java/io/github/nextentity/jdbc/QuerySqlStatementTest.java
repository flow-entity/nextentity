package io.github.nextentity.jdbc;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test objective: Verify QuerySqlStatement correctly stores SQL and parameters
 * <p>
 * Test scenarios:
 * 1. Create statement with SQL and parameters
 * 2. Verify SQL getter
 * 3. Verify parameters getter
 * 4. Verify toString returns SQL
 */
class QuerySqlStatementTest {

    @Test
    void querySqlStatement_CreatesWithSqlAndParameters() {
        // given
        String sql = "SELECT * FROM users WHERE id = ?";
        List<Object> params = Arrays.asList(1, "test");

        // when
        QuerySqlStatement statement = new QuerySqlStatement(sql, params);

        // then
        assertThat(statement.sql()).isEqualTo(sql);
        assertThat(statement.parameters()).hasSize(2);
    }

    @Test
    void querySqlStatement_Sql_ReturnsExactSql() {
        // given
        String sql = "INSERT INTO users (name) VALUES (?)";

        // when
        QuerySqlStatement statement = new QuerySqlStatement(sql, Collections.emptyList());

        // then
        assertThat(statement.sql()).isSameAs(sql);
    }

    @Test
    void querySqlStatement_Parameters_ReturnsIterable() {
        // given
        List<String> params = Arrays.asList("a", "b", "c");

        // when
        QuerySqlStatement statement = new QuerySqlStatement("SELECT ?", params);

        // then
        assertThat(statement.parameters()).hasSize(3);
    }

    @Test
    void querySqlStatement_ToString_ReturnsSql() {
        // given
        String sql = "SELECT * FROM users";

        // when
        QuerySqlStatement statement = new QuerySqlStatement(sql, Collections.emptyList());

        // then
        assertThat(statement.toString()).isEqualTo(sql);
    }

    @Test
    void querySqlStatement_EmptyParameters() {
        // given
        String sql = "SELECT 1";

        // when
        QuerySqlStatement statement = new QuerySqlStatement(sql, Collections.emptyList());

        // then
        assertThat(statement.parameters()).isEmpty();
    }
}
