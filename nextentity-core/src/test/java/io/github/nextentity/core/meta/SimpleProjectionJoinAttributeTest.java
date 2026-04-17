//package io.github.nextentity.core.meta;
//
//import io.github.nextentity.core.reflect.schema.Attributes;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
/////
// /// 单元测试 SimpleProjectionJoinAttribute.
//@ExtendWith(MockitoExtension.class)
//class SimpleProjectionJoinAttributeTest {
//
//    @Mock
//    private EntitySchemaAttribute sourceJoinAttribute;
//
//    @Mock
//    private Attributes mockAttributes;
//
//    private SimpleProjectionJoinAttribute attribute;
//
//    @Nested
//    class SourceMethod {
//
/////
//         /// 测试目标: 验证y source() returns the source join attribute.
//         /// 测试场景: Create SimpleProjectionJoinAttribute with source and call source().
//         /// 预期结果: Returns the source attribute passed to constructor.
//        @Test
//        void source_ShouldReturnConstructorValue() {
//            // given
//            attribute = new SimpleProjectionJoinAttribute(sourceJoinAttribute, attr -> mockAttributes);
//
//            // when
//            EntitySchemaAttribute result = attribute.source();
//
//            // then
//            assertThat(result).isSameAs(sourceJoinAttribute);
//        }
//    }
//
//    @Nested
//    class BuildAttributesMethod {
//
/////
//         /// 测试目标: 验证y buildAttributes() is called via attributes().
//         /// 测试场景: Call attributes() which triggers buildAttributes().
//         /// 预期结果: Returns attributes from the attributeBuilder function.
//        @Test
//        void attributes_ShouldCallBuildAttributes() {
//            // given
//            attribute = new SimpleProjectionJoinAttribute(sourceJoinAttribute, attr -> mockAttributes);
//
//            // when
//            Attributes result = attribute.attributes();
//
//            // then
//            assertThat(result).isSameAs(mockAttributes);
//        }
//
/////
//         /// 测试目标: 验证y attributeBuilder receives the correct attribute.
//         /// 测试场景: Create attribute with builder that captures the argument.
//         /// 预期结果: Builder receives the SimpleProjectionJoinAttribute instance.
//        @Test
//        void buildAttributes_ShouldReceiveThisAttribute() {
//            // given
//            SimpleProjectionJoinAttribute[] capturedAttr = new SimpleProjectionJoinAttribute[1];
//            attribute = new SimpleProjectionJoinAttribute(sourceJoinAttribute, attr -> {
//                capturedAttr[0] = attr;
//                return mockAttributes;
//            });
//
//            // when
//            attribute.attributes();
//
//            // then
//            assertThat(capturedAttr[0]).isSameAs(attribute);
//        }
//    }
//
//    @Nested
//    class Inheritance {
//
/////
//         /// 测试目标: 验证y SimpleProjectionJoinAttribute implements ProjectionJoinAttribute.
//         /// 测试场景: Check if instance is ProjectionJoinAttribute.
//         /// 预期结果: Is instance of ProjectionJoinAttribute.
//        @Test
//        void shouldImplementProjectionJoinAttribute() {
//            // given
//            attribute = new SimpleProjectionJoinAttribute(sourceJoinAttribute, attr -> mockAttributes);
//
//            // when & then
//            assertThat(attribute).isInstanceOf(ProjectionJoinAttribute.class);
//        }
//    }
//}
