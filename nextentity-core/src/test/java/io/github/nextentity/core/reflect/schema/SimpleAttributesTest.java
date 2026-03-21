package io.github.nextentity.core.reflect.schema;

import io.github.nextentity.core.util.ImmutableArray;
import io.github.nextentity.core.util.ImmutableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for SimpleAttributes.
 */
class SimpleAttributesTest {

    private SimpleAttributes attributes;
    private TestAttribute idAttr;
    private TestAttribute nameAttr;
    private TestAttribute nestedAttr;

    @BeforeEach
    void setUp() {
        idAttr = new TestAttribute();
        idAttr.name("id").ordinal(0).declareBy(null).field(null).getter(null).setter(null);

        nameAttr = new TestAttribute();
        nameAttr.name("name").ordinal(1).declareBy(null).field(null).getter(null).setter(null);

        nestedAttr = new TestAttribute();
        nestedAttr.name("nested").ordinal(2).declareBy(null).field(null).getter(null).setter(null);

        List<Attribute> attrList = new ArrayList<>();
        attrList.add(idAttr);
        attrList.add(nameAttr);
        attrList.add(nestedAttr);

        attributes = new SimpleAttributes(attrList);
    }

    @Nested
    class GetByName {

        /**
         * Test objective: Verify get(String) returns attribute by name.
         * Test scenario: Call get() with existing attribute name.
         * Expected result: Returns the correct attribute.
         */
        @Test
        void get_WithExistingName_ShouldReturnAttribute() {
            // when
            Attribute result = attributes.get("id");

            // then
            assertThat(result).isSameAs(idAttr);
        }

        /**
         * Test objective: Verify get(String) returns null for non-existent name.
         * Test scenario: Call get() with non-existent attribute name.
         * Expected result: Returns null.
         */
        @Test
        void get_WithNonExistentName_ShouldReturnNull() {
            // when
            Attribute result = attributes.get("unknown");

            // then
            assertThat(result).isNull();
        }
    }

    @Nested
    class GetPrimitives {

        /**
         * Test objective: Verify getPrimitives() returns only primitive attributes.
         * Test scenario: Call getPrimitives() on attributes with mixed types.
         * Expected result: Returns only attributes where isPrimitive() returns true.
         */
        @Test
        void getPrimitives_ShouldReturnOnlyPrimitives() {
            // when
            ImmutableArray<Attribute> result = attributes.getPrimitives();

            // then
            assertThat(result).hasSize(2);
            assertThat(result).containsExactlyInAnyOrder(idAttr, nameAttr);
            assertThat(result).doesNotContain(nestedAttr);
        }
    }

    @Nested
    class Inheritance {

        /**
         * Test objective: Verify SimpleAttributes extends ImmutableList.
         * Test scenario: Check inheritance.
         * Expected result: Is instance of ImmutableList.
         */
        @Test
        void shouldExtendImmutableList() {
            assertThat(attributes).isInstanceOf(ImmutableList.class);
        }

        /**
         * Test objective: Verify SimpleAttributes implements Attributes.
         * Test scenario: Check interface implementation.
         * Expected result: Is instance of Attributes.
         */
        @Test
        void shouldImplementAttributes() {
            assertThat(attributes).isInstanceOf(Attributes.class);
        }
    }

    /**
     * Test implementation of Attribute.
     */
    static class TestAttribute extends SimpleAttribute {
        @Override
        public boolean isPrimitive() {
            // Return true for id and name, false for nested
            return !name().equals("nested");
        }
    }
}
