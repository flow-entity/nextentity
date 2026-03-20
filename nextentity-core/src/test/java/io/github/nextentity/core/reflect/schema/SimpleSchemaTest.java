package io.github.nextentity.core.reflect.schema;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test objective: Verify SimpleSchema provides correct schema metadata
 * <p>
 * Test scenarios:
 * 1. Type and attributes can be set and retrieved
 * 2. Builder pattern works correctly
 * <p>
 * Expected result: Schema metadata can be created and accessed correctly
 */
class SimpleSchemaTest {

    @Nested
    class BasicProperties {

        /**
         * Test objective: Verify type can be set and retrieved
         * Test scenario: Set type and get it back
         * Expected result: Correct type returned
         */
        @Test
        void type_ShouldBeSettable() {
            // given
            SimpleSchema schema = new SimpleSchema();

            // when
            schema.type(String.class);

            // then
            assertThat(schema.type()).isEqualTo(String.class);
        }

        /**
         * Test objective: Verify attributes can be set and retrieved
         * Test scenario: Set attributes and get them back
         * Expected result: Correct attributes returned
         */
        @Test
        void attributes_ShouldBeSettable() {
            // given
            SimpleSchema schema = new SimpleSchema();
            Attributes attrs = new SimpleAttributes(Collections.emptyList());

            // when
            schema.attributes(attrs);

            // then
            assertThat(schema.attributes()).isEqualTo(attrs);
        }
    }

    @Nested
    class BuilderPattern {

        /**
         * Test objective: Verify setter methods return this for chaining
         * Test scenario: Chain setters
         * Expected result: Same instance returned
         */
        @Test
        void setters_ShouldReturnThis() {
            // given
            SimpleSchema schema = new SimpleSchema();
            Attributes attrs = new SimpleAttributes(Collections.emptyList());

            // when
            SimpleSchema result = schema
                    .type(String.class)
                    .attributes(attrs);

            // then
            assertThat(result).isSameAs(schema);
        }
    }
}
