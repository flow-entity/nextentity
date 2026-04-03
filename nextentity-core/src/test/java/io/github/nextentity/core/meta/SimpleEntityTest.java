package io.github.nextentity.core.meta;

import io.github.nextentity.core.reflect.schema.Attributes;
import io.github.nextentity.core.reflect.schema.SimpleAttributes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

///
 /// 单元测试 SimpleEntity.
class SimpleEntityTest {

    private SimpleEntity entity;

    @BeforeEach
    void setUp() {
        entity = new SimpleEntity(TestEntity.class, "test_entity", (e, t) -> null);
    }

    @Nested
    class ConstructorAndGetters {

///
         /// 测试目标: 验证y constructor sets type 正确.
         /// 测试场景: Create SimpleEntity and check type().
         /// 预期结果: Returns the correct type.
        @Test
        void type_ShouldReturnConstructorType() {
            assertThat(entity.type()).isEqualTo(TestEntity.class);
        }

///
         /// 测试目标: 验证y constructor sets table name 正确.
         /// 测试场景: Create SimpleEntity and check tableName().
         /// 预期结果: Returns the correct table name.
        @Test
        void tableName_ShouldReturnConstructorTableName() {
            assertThat(entity.tableName()).isEqualTo("test_entity");
        }
    }

    @Nested
    class SetAttributes {

///
         /// 测试目标: 验证y setAttributes extracts ID attribute.
         /// 测试场景: Set attributes with one marked as ID.
         /// 预期结果: id() returns the ID attribute.
        @Test
        void setAttributes_ShouldExtractIdAttribute() {
            // given
            List<SimpleEntityAttribute> attributes = new ArrayList<>();
            SimpleEntityAttribute idAttr = new SimpleEntityAttribute();
            idAttr.setId(true);
            attributes.add(idAttr);

            SimpleEntityAttribute normalAttr = new SimpleEntityAttribute();
            attributes.add(normalAttr);

            entity.setAttributes(new SimpleAttributes(attributes));

            // when
            EntityAttribute id = entity.id();

            // then
            assertThat(id).isNotNull();
            assertThat(id.isId()).isTrue();
        }

///
         /// 测试目标: 验证y setAttributes extracts version attribute.
         /// 测试场景: Set attributes with one marked as version.
         /// 预期结果: version() returns the version attribute.
        @Test
        void setAttributes_ShouldExtractVersionAttribute() {
            // given
            List<SimpleEntityAttribute> attributes = new ArrayList<>();
            SimpleEntityAttribute versionAttr = new SimpleEntityAttribute();
            versionAttr.setVersion(true);
            attributes.add(versionAttr);

            SimpleEntityAttribute normalAttr = new SimpleEntityAttribute();
            attributes.add(normalAttr);

            entity.setAttributes(new SimpleAttributes(attributes));

            // when
            EntityAttribute version = entity.version();

            // then
            assertThat(version).isNotNull();
            assertThat(version.isVersion()).isTrue();
        }

///
         /// 测试目标: 验证y attributes() returns set attributes.
         /// 测试场景: Set attributes and retrieve them.
         /// 预期结果: attributes() returns the same attributes.
        @Test
        void attributes_ShouldReturnSetAttributes() {
            // given
            List<SimpleEntityAttribute> attributes = new ArrayList<>();
            SimpleEntityAttribute attr = new SimpleEntityAttribute();
            attributes.add(attr);
            SimpleAttributes simpleAttributes = new SimpleAttributes(attributes);

            entity.setAttributes(simpleAttributes);

            // when
            Attributes result = entity.attributes();

            // then
            assertThat(result).isEqualTo(simpleAttributes);
        }
    }

    @Nested
    class GetProjection {

///
         /// 测试目标: 验证y getProjection caches projection types.
         /// 测试场景: Call getProjection twice with same type.
         /// 预期结果: Returns same instance on subsequent calls.
        @Test
        void getProjection_ShouldCacheResults() {
            // given
            SimpleProjection projection = new SimpleProjection(String.class, entity);
            SimpleEntity customEntity = new SimpleEntity(TestEntity.class, "test_entity",
                    (e, t) -> t == String.class ? projection : null);

            // when
            ProjectionType result1 = customEntity.getProjection(String.class);
            ProjectionType result2 = customEntity.getProjection(String.class);

            // then
            assertThat(result1).isSameAs(result2);
            assertThat(result1).isSameAs(projection);
        }
    }

///
     /// 测试 entity class.
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
