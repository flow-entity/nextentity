package io.github.nextentity.core.meta;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for SubQueryEntity.
 */
class SubQueryEntityTest {

    @Nested
    class SubSelectSql {

        /**
         * Test objective: Verify subSelectSql() returns the SQL string.
         * Test scenario: Create SubQueryEntity with SQL and call subSelectSql().
         * Expected result: Returns the SQL string.
         */
        @Test
        void subSelectSql_ShouldReturnConstructorValue() {
            // given
            String sql = "SELECT * FROM view_entity";
            SubQueryEntity entity = new SubQueryEntity(TestEntity.class, "view_entity",
                    (e, t) -> null, sql);

            // when
            String result = entity.subSelectSql();

            // then
            assertThat(result).isEqualTo(sql);
        }
    }

    @Nested
    class Inheritance {

        /**
         * Test objective: Verify SubQueryEntity inherits from SimpleEntity.
         * Test scenario: Create SubQueryEntity and check type and tableName.
         * Expected result: Inherited methods work correctly.
         */
        @Test
        void shouldInheritFromSimpleEntity() {
            // given
            SubQueryEntity entity = new SubQueryEntity(TestEntity.class, "test_table",
                    (e, t) -> null, "SELECT * FROM test");

            // when & then
            assertThat(entity.type()).isEqualTo(TestEntity.class);
            assertThat(entity.tableName()).isEqualTo("test_table");
        }

        /**
         * Test objective: Verify SubQueryEntity implements SubQueryEntityType.
         * Test scenario: Check if instance is SubQueryEntityType.
         * Expected result: Is instance of SubQueryEntityType.
         */
        @Test
        void shouldImplementSubQueryEntityType() {
            // given
            SubQueryEntity entity = new SubQueryEntity(TestEntity.class, "test_table",
                    (e, t) -> null, "SELECT * FROM test");

            // when & then
            assertThat(entity).isInstanceOf(SubQueryEntityType.class);
        }
    }

    /**
     * Test entity class.
     */
    static class TestEntity {
        private Long id;
        private String name;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
