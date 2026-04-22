package io.github.nextentity.jdbc;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

///
/// 测试目标: 验证y BatchSqlStatement 正确 stores SQL and batch parameters
/// <p>
/// 测试场景s:
/// 1. Create batch statement with SQL and parameters
/// 2. 验证y SQL getter
/// 3. 验证y parameters getter
class BatchSqlStatementTest {

    @Test
    void batchSqlStatement_CreatesWithSqlAndParameters() {
        // given
        String sql = "insert into users (name) values (?)";
        List<List<Object>> params = Arrays.asList(
                Arrays.asList("user1"),
                Arrays.asList("user2")
        );

        // when
        BatchSqlStatement statement = new BatchSqlStatement(sql, params);

        // then
        assertThat(statement.sql()).isEqualTo(sql);
    }

    @Test
    void batchSqlStatement_Sql_ReturnsExactSql() {
        // given
        String sql = "update users set name = ? where id = ?";

        // when
        BatchSqlStatement statement = new BatchSqlStatement(sql, Collections.emptyList());

        // then
        assertThat(statement.sql()).isSameAs(sql);
    }

    @Test
    void batchSqlStatement_Parameters_ReturnsIterable() {
        // given
        List<List<Object>> params = Arrays.asList(
                Arrays.asList("a", 1),
                Arrays.asList("b", 2)
        );

        // when
        BatchSqlStatement statement = new BatchSqlStatement("update ?", params);

        // then
        assertThat(statement.parameters()).hasSize(2);
    }

    @Test
    void batchSqlStatement_EmptyParameters() {
        // given
        String sql = "delete from users";

        // when
        BatchSqlStatement statement = new BatchSqlStatement(sql, Collections.emptyList());

        // then
        assertThat(statement.parameters()).isEmpty();
    }
}
