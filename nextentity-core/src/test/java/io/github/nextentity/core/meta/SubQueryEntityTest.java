//package io.github.nextentity.core.meta;
//
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
/////
// /// 单元测试 SubQueryEntity.
//class SubQueryEntityTest {
//
//    @Nested
//    class SubSelectSql {
//
/////
//         /// 测试目标: 验证y subSelectSql() returns the SQL string.
//         /// 测试场景: Create SubQueryEntity with SQL and call subSelectSql().
//         /// 预期结果: Returns the SQL string.
//        @Test
//        void subSelectSql_ShouldReturnConstructorValue() {
//            // given
//            String sql = "select * from view_entity";
//            SubQueryEntity entity = new SubQueryEntity(TestEntity.class, "view_entity",
//                    (e, t) -> null, sql);
//
//            // when
//            String result = entity.subSelectSql();
//
//            // then
//            assertThat(result).isEqualTo(sql);
//        }
//    }
//
//    @Nested
//    class Inheritance {
//
/////
//         /// 测试目标: 验证y SubQueryEntity inherits from SimpleEntity.
//         /// 测试场景: Create SubQueryEntity and check type and tableName.
//         /// 预期结果: Inherited 方法 work 正确.
//        @Test
//        void shouldInheritFromSimpleEntity() {
//            // given
//            SubQueryEntity entity = new SubQueryEntity(TestEntity.class, "test_table",
//                    (e, t) -> null, "select * from test");
//
//            // when & then
//            assertThat(entity.type()).isEqualTo(TestEntity.class);
//            assertThat(entity.tableName()).isEqualTo("test_table");
//        }
//
/////
//         /// 测试目标: 验证y SubQueryEntity implements SubQueryEntityType.
//         /// 测试场景: Check if instance is SubQueryEntityType.
//         /// 预期结果: Is instance of SubQueryEntityType.
//        @Test
//        void shouldImplementSubQueryEntityType() {
//            // given
//            SubQueryEntity entity = new SubQueryEntity(TestEntity.class, "test_table",
//                    (e, t) -> null, "select * from test");
//
//            // when & then
//            assertThat(entity).isInstanceOf(SubQueryEntityType.class);
//        }
//    }
//
/////
//     /// 测试 entity class.
//    static class TestEntity {
//        private Long id;
//        private String name;
//
//        public Long getId() {
//            return id;
//        }
//
//        public void setId(Long id) {
//            this.id = id;
//        }
//
//        public String getName() {
//            return name;
//        }
//
//        public void setName(String name) {
//            this.name = name;
//        }
//    }
//}
