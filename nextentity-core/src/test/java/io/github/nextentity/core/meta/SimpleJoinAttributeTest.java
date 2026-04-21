//package io.github.nextentity.core.meta;
//
//import io.github.nextentity.core.reflect.schema.Attributes;
//import io.github.nextentity.core.reflect.schema.SimpleAttributes;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
/////
// /// 单元测试 SimpleJoinAttribute.
//class SimpleJoinAttributeTest {
//
//    private SimpleJoinAttribute attribute;
//
//    @BeforeEach
//    void setUp() {
//        attribute = new SimpleJoinAttribute(attr -> new SimpleAttributes(new ArrayList<>()));
//    }
//
//    @Nested
//    class JoinName {
//
/////
//         /// 测试目标: 验证y joinName getter and setter.
//         /// 测试场景: Set join name and retrieve it.
//         /// 预期结果: Returns the set join name.
//        @Test
//        void joinName_ShouldReturnSetValue() {
//            // given
//            attribute.setJoinName("department_id");
//
//            // when
//            String result = attribute.joinName();
//
//            // then
//            assertThat(result).isEqualTo("department_id");
//        }
//    }
//
//    @Nested
//    class ReferencedColumnName {
//
/////
//         /// 测试目标: 验证y referencedColumnName getter and setter.
//         /// 测试场景: Set referenced column name and retrieve it.
//         /// 预期结果: Returns the set referenced column name.
//        @Test
//        void referencedColumnName_ShouldReturnSetValue() {
//            // given
//            attribute.setReferencedColumnName("id");
//
//            // when
//            String result = attribute.referencedColumnName();
//
//            // then
//            assertThat(result).isEqualTo("id");
//        }
//    }
//
//    @Nested
//    class TableName {
//
/////
//         /// 测试目标: 验证y tableName getter and setter.
//         /// 测试场景: Set table name and retrieve it.
//         /// 预期结果: Returns the set table name.
//        @Test
//        void tableName_ShouldReturnSetValue() {
//            // given
//            attribute.setTableName("department");
//
//            // when
//            String result = attribute.tableName();
//
//            // then
//            assertThat(result).isEqualTo("department");
//        }
//    }
//
//    @Nested
//    class IdAttribute {
//
/////
//         /// 测试目标: 验证y id() extracts ID from attributes.
//         /// 测试场景: Build attributes with ID and retrieve id().
//         /// 预期结果: Returns the ID attribute.
//        @Test
//        void id_ShouldReturnIdAttribute() {
//            // given
//            SimpleJoinAttribute customAttr = new SimpleJoinAttribute(attr -> {
//                List<SimpleEntityAttribute> attrs = new ArrayList<>();
//                SimpleEntityAttribute idAttr = new SimpleEntityAttribute();
//                idAttr.setId(true);
//                attrs.add(idAttr);
//                return new SimpleAttributes(attrs);
//            });
//
//            // when - trigger attribute building by accessing attributes
//            customAttr.attributes();
//            EntityAttribute id = customAttr.id();
//
//            // then
//            assertThat(id).isNotNull();
//            assertThat(id.isId()).isTrue();
//        }
//    }
//
//    @Nested
//    class VersionAttribute {
//
/////
//         /// 测试目标: 验证y version() extracts version from attributes.
//         /// 测试场景: Build attributes with version and retrieve version().
//         /// 预期结果: Returns the version attribute.
//        @Test
//        void version_ShouldReturnVersionAttribute() {
//            // given
//            SimpleJoinAttribute customAttr = new SimpleJoinAttribute(attr -> {
//                List<SimpleEntityAttribute> attrs = new ArrayList<>();
//                SimpleEntityAttribute versionAttr = new SimpleEntityAttribute();
//                versionAttr.setVersion(true);
//                attrs.add(versionAttr);
//                return new SimpleAttributes(attrs);
//            });
//
//            // when - trigger attribute building by accessing attributes
//            customAttr.attributes();
//            EntityAttribute version = customAttr.version();
//
//            // then
//            assertThat(version).isNotNull();
//            assertThat(version.isVersion()).isTrue();
//        }
//    }
//
//    @Nested
//    class AttributesBuilding {
//
/////
//         /// 测试目标: 验证y attributes are built lazily.
//         /// 测试场景: Create attribute and check attributes is null initially.
//         /// 预期结果: attributes() triggers building.
//        @Test
//        void attributes_ShouldBuildLazily() {
//            // when
//            Attributes result = attribute.attributes();
//
//            // then
//            assertThat(result).isNotNull();
//        }
//    }
//}
