package io.github.nextentity.core.reflect.schema;

import io.github.nextentity.core.util.ImmutableList;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test objective: Verify SimpleAttribute provides correct attribute metadata
 * <p>
 * Test scenarios:
 * 1. Getter and setter methods work correctly
 * 2. Builder pattern setters return this
 * 3. Path calculation works for nested attributes
 * <p>
 * Expected result: Attribute metadata can be created and accessed correctly
 */
class SimpleAttributeTest {

    @Nested
    class BasicProperties {

        /**
         * Test objective: Verify all properties can be set and retrieved
         * Test scenario: Create attribute with all properties
         * Expected result: All properties accessible
         */
        @Test
        void allProperties_ShouldBeAccessible() throws Exception {
            // given
            Class<?> type = String.class;
            String name = "testName";
            Method getter = TestEntity.class.getMethod("getName");
            Method setter = TestEntity.class.getMethod("setName", String.class);
            Field field = TestEntity.class.getDeclaredField("name");
            Schema declareBy = new SimpleSchema().type(TestEntity.class);
            int ordinal = 5;

            // when
            SimpleAttribute attr = new SimpleAttribute(type, name, getter, setter, field, declareBy, ordinal);

            // then
            assertThat(attr.type()).isEqualTo(type);
            assertThat(attr.name()).isEqualTo(name);
            assertThat(attr.getter()).isEqualTo(getter);
            assertThat(attr.setter()).isEqualTo(setter);
            assertThat(attr.field()).isEqualTo(field);
            assertThat(attr.declareBy()).isEqualTo(declareBy);
            assertThat(attr.ordinal()).isEqualTo(ordinal);
        }

        /**
         * Test objective: Verify default constructor creates empty attribute
         * Test scenario: Create with default constructor
         * Expected result: All properties null/default
         */
        @Test
        void defaultConstructor_ShouldCreateEmptyAttribute() {
            // when
            SimpleAttribute attr = new SimpleAttribute();

            // then
            assertThat(attr.type()).isNull();
            assertThat(attr.name()).isNull();
            assertThat(attr.getter()).isNull();
            assertThat(attr.setter()).isNull();
            assertThat(attr.field()).isNull();
            assertThat(attr.declareBy()).isNull();
            assertThat(attr.ordinal()).isZero();
        }
    }

    @Nested
    class BuilderPattern {

        /**
         * Test objective: Verify setter methods return this for chaining
         * Test scenario: Chain multiple setters
         * Expected result: Same instance returned
         */
        @Test
        void setters_ShouldReturnThis() {
            // given
            SimpleAttribute attr = new SimpleAttribute();

            // when
            SimpleAttribute result = attr
                    .type(String.class)
                    .name("test")
                    .ordinal(1);

            // then
            assertThat(result).isSameAs(attr);
            assertThat(attr.type()).isEqualTo(String.class);
            assertThat(attr.name()).isEqualTo("test");
            assertThat(attr.ordinal()).isEqualTo(1);
        }
    }

    @Nested
    class PathCalculation {

        /**
         * Test objective: Verify path returns single element for root attribute
         * Test scenario: Get path for attribute without parent
         * Expected result: Path contains only attribute name
         */
        @Test
        void path_WithoutParent_ShouldReturnSingleElement() {
            // given
            SimpleAttribute attr = new SimpleAttribute()
                    .name("fieldName");

            // when
            ImmutableList<String> path = attr.path();

            // then
            assertThat(path).containsExactly("fieldName");
        }
    }

    @Nested
    class SetAttribute {

        /**
         * Test objective: Verify setAttribute copies all properties
         * Test scenario: Copy properties from one attribute to another
         * Expected result: All properties copied
         */
        @Test
        void setAttribute_ShouldCopyAllProperties() throws Exception {
            // given
            Method getter = TestEntity.class.getMethod("getName");
            Method setter = TestEntity.class.getMethod("setName", String.class);
            Field field = TestEntity.class.getDeclaredField("name");
            Schema schema = new SimpleSchema().type(TestEntity.class);

            SimpleAttribute source = new SimpleAttribute(String.class, "name", getter, setter, field, schema, 3);
            SimpleAttribute target = new SimpleAttribute();

            // when
            target.setAttribute(source);

            // then
            assertThat(target.type()).isEqualTo(String.class);
            assertThat(target.name()).isEqualTo("name");
            assertThat(target.getter()).isEqualTo(getter);
            assertThat(target.setter()).isEqualTo(setter);
            assertThat(target.field()).isEqualTo(field);
            assertThat(target.declareBy()).isEqualTo(schema);
            assertThat(target.ordinal()).isEqualTo(3);
        }
    }

    // Test entity class
    static class TestEntity {
        private String name;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }
}
