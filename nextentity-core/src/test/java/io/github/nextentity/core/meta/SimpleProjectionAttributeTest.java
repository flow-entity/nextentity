//package io.github.nextentity.core.meta;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
/////
// /// 单元测试 SimpleProjectionAttribute.
//class SimpleProjectionAttributeTest {
//
//    private SimpleProjectionAttribute attribute;
//    private SimpleEntityAttribute sourceAttribute;
//
//    @BeforeEach
//    void setUp() {
//        sourceAttribute = new SimpleEntityAttribute();
//        sourceAttribute.setColumnName("test_column");
//        sourceAttribute.setUpdatable(true);
//        attribute = new SimpleProjectionAttribute(sourceAttribute);
//    }
//
//    @Nested
//    class Source {
//
/////
//         /// 测试目标: 验证y source() returns the source attribute.
//         /// 测试场景: Create SimpleProjectionAttribute with source and call source().
//         /// 预期结果: Returns the source attribute.
//        @Test
//        void source_ShouldReturnSourceAttribute() {
//            // when
//            EntityAttribute result = attribute.source();
//
//            // then
//            assertThat(result).isSameAs(sourceAttribute);
//        }
//    }
//
//    @Nested
//    class IsUpdatable {
//
/////
//         /// 测试目标: 验证y isUpdatable() delegates to source.
//         /// 测试场景: Check isUpdatable when source is updatable.
//         /// 预期结果: Returns the same value as source.
//        @Test
//        void isUpdatable_ShouldDelegateToSource() {
//            // when
//            boolean result = attribute.isUpdatable();
//
//            // then
//            assertThat(result).isTrue();
//            assertThat(result).isEqualTo(sourceAttribute.isUpdatable());
//        }
//
/////
//         /// 测试目标: 验证y isUpdatable() returns false when source is not updatable.
//         /// 测试场景: Set source as not updatable.
//         /// 预期结果: Returns false.
//        @Test
//        void isUpdatable_WhenSourceNotUpdatable_ShouldReturnFalse() {
//            // given
//            sourceAttribute.setUpdatable(false);
//
//            // when
//            boolean result = attribute.isUpdatable();
//
//            // then
//            assertThat(result).isFalse();
//        }
//    }
//}
