package io.github.nextentity.core.meta;

import io.github.nextentity.core.reflect.schema.Attributes;
import io.github.nextentity.core.reflect.schema.SimpleAttributes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for SimpleProjection.
 */
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

        /**
         * Test objective: Verify constructor sets type correctly.
         * Test scenario: Create SimpleProjection and check type().
         * Expected result: Returns the correct type.
         */
        @Test
        void type_ShouldReturnConstructorType() {
            assertThat(projection.type()).isEqualTo(TestProjection.class);
        }

        /**
         * Test objective: Verify constructor sets source correctly.
         * Test scenario: Create SimpleProjection and check source().
         * Expected result: Returns the source entity.
         */
        @Test
        void source_ShouldReturnConstructorSource() {
            assertThat(projection.source()).isEqualTo(entity);
        }
    }

    @Nested
    class SetAttributes {

        /**
         * Test objective: Verify setAttributes and attributes work correctly.
         * Test scenario: Set attributes and retrieve them.
         * Expected result: Returns the set attributes.
         */
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

    /**
     * Test projection class.
     */
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
