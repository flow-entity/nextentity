package io.github.nextentity.core.meta;

import io.github.nextentity.core.reflect.schema.Attributes;
import io.github.nextentity.core.reflect.schema.SimpleAttributes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for SimpleEntity.
 */
class SimpleEntityTest {

    private SimpleEntity entity;

    @BeforeEach
    void setUp() {
        entity = new SimpleEntity(TestEntity.class, "test_entity", (e, t) -> null);
    }

    @Nested
    class ConstructorAndGetters {

        /**
         * Test objective: Verify constructor sets type correctly.
         * Test scenario: Create SimpleEntity and check type().
         * Expected result: Returns the correct type.
         */
        @Test
        void type_ShouldReturnConstructorType() {
            assertThat(entity.type()).isEqualTo(TestEntity.class);
        }

        /**
         * Test objective: Verify constructor sets table name correctly.
         * Test scenario: Create SimpleEntity and check tableName().
         * Expected result: Returns the correct table name.
         */
        @Test
        void tableName_ShouldReturnConstructorTableName() {
            assertThat(entity.tableName()).isEqualTo("test_entity");
        }
    }

    @Nested
    class SetAttributes {

        /**
         * Test objective: Verify setAttributes extracts ID attribute.
         * Test scenario: Set attributes with one marked as ID.
         * Expected result: id() returns the ID attribute.
         */
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

        /**
         * Test objective: Verify setAttributes extracts version attribute.
         * Test scenario: Set attributes with one marked as version.
         * Expected result: version() returns the version attribute.
         */
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

        /**
         * Test objective: Verify attributes() returns set attributes.
         * Test scenario: Set attributes and retrieve them.
         * Expected result: attributes() returns the same attributes.
         */
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

        /**
         * Test objective: Verify getProjection caches projection types.
         * Test scenario: Call getProjection twice with same type.
         * Expected result: Returns same instance on subsequent calls.
         */
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
