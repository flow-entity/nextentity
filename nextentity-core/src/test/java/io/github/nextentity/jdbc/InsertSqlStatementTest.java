package io.github.nextentity.jdbc;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

///
/// 测试目标: 验证y InsertSqlStatement 正确 stores insert data
/// <p>
/// 测试场景s:
/// 1. Create insert statement with all fields
/// 2. 验证y returnGeneratedKeys flag
/// 3. 验证y entities getter
class InsertSqlStatementTest {

    @Nested
    class ConstructorTest {

        @Test
        void insertSqlStatement_CreatesWithAllFields() {
            // given
            List<Object> entities = Arrays.asList("entity1", "entity2");
            String sql = "insert into users (name) values (?)";
            List<List<Object>> params = Arrays.asList(
                    Collections.singletonList("name1"),
                    Collections.singletonList("name2")
            );

            // when
            InsertSqlStatement statement = new InsertSqlStatement(entities, sql, params, true);

            // then
            assertThat(statement.entities()).isSameAs(entities);
            assertThat(statement.sql()).isEqualTo(sql);
            assertThat(statement.returnGeneratedKeys()).isTrue();
        }

        @Test
        void insertSqlStatement_ReturnGeneratedKeys_False() {
            // given
            List<Object> entities = Collections.singletonList("entity");

            // when
            InsertSqlStatement statement = new InsertSqlStatement(
                    entities, "INSERT", Collections.emptyList(), false);

            // then
            assertThat(statement.returnGeneratedKeys()).isFalse();
        }
    }

    @Nested
    class EntitiesTest {

        @Test
        void entities_ReturnsEntities() {
            // given
            List<Object> entities = Arrays.asList(new Object(), new Object());
            InsertSqlStatement statement = new InsertSqlStatement(
                    entities, "INSERT", Collections.emptyList(), true);

            // when
            Iterable<?> result = statement.entities();

            // then
            assertThat(result).isSameAs(entities);
        }
    }

    @Nested
    class InheritanceTest {

        @Test
        void insertSqlStatement_ExtendsBatchSqlStatement() {
            // given
            List<List<Object>> params = Arrays.asList(
                    Collections.singletonList("param1"),
                    Collections.singletonList("param2")
            );

            // when
            InsertSqlStatement statement = new InsertSqlStatement(
                    Collections.emptyList(), "INSERT", params, false);

            // then
            assertThat(statement.parameters()).isSameAs(params);
        }
    }
}
