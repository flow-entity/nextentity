package io.github.nextentity.core.meta;

import io.github.nextentity.core.reflect.schema.Attributes;
import io.github.nextentity.core.reflect.schema.SimpleAttributes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

///
 /// 单元测试 SimpleProjection.
class SimpleProjectionTest {

    private SimpleProjection projection;
    private SimpleEntity entity;

    @BeforeEach
    void setUp() {
        entity = new SimpleEntity(TestEntity.class, "test_entity", (e, t) -> null);
        projection = new SimpleProjection(TestProjection.class, entity);
    }

    @Nested
    class ConstructorAndGetters {

///
         /// 测试目标: 验证y constructor sets type 正确.
         /// 测试场景: Create SimpleProjection and check type().
         /// 预期结果: Returns the correct type.
        @Test
        void type_ShouldReturnConstructorType() {
            assertThat(projection.type()).isEqualTo(TestProjection.class);
        }

///
         /// 测试目标: 验证y constructor sets source 正确.
         /// 测试场景: Create SimpleProjection and check source().
         /// 预期结果: Returns the source entity.
        @Test
        void source_ShouldReturnConstructorSource() {
            assertThat(projection.source()).isEqualTo(entity);
        }
    }

    @Nested
    class SetAttributes {

///
         /// 测试目标: 验证y setAttributes and attributes work 正确.
         /// 测试场景: Set attributes and retrieve them.
         /// 预期结果: Returns the set attributes.
        @Test
        void attributes_ShouldReturnSetAttributes() {
            // given
            SimpleAttributes attrs = new SimpleAttributes(new ArrayList<>());
            projection.setAttributes(attrs);

            // when
            Attributes result = projection.attributes();

            // then
            assertThat(result).isEqualTo(attrs);
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

///
     /// 测试 projection class.
    static class TestProjection {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
