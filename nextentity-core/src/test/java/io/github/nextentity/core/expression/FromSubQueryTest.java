package io.github.nextentity.core.expression;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

///
/// 测试目标：验证FromSubQuery正确表示子查询源
/// <p>
/// 测试场景：
/// 1. 使用QueryStructure创建
/// 2. 访问结构属性
///
class FromSubQueryTest {

    @Nested
    class Creation {

        @Test
        void fromSubQuery_CreatesWithStructure() {
            // given
            QueryStructure structure = QueryStructure.of(String.class);

            // when
            FromSubQuery fromSubQuery = new FromSubQuery(structure);

            // then
            assertThat(fromSubQuery.structure()).isSameAs(structure);
        }

        @Test
        void fromSubQuery_ImplementsFrom() {
            // given
            FromSubQuery fromSubQuery = new FromSubQuery(QueryStructure.of(String.class));

            // then
            assertThat(fromSubQuery).isInstanceOf(From.class);
        }
    }
}
