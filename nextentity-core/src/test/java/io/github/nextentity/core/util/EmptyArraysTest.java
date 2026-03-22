package io.github.nextentity.core.util;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test objective: Verify EmptyArrays interface constants are correct empty arrays
 * <p>
 * Test scenarios:
 * 1. STRING constant is a non-null empty String array
 * 2. OBJECT constant is a non-null empty Object array
 * 3. Constants are singletons (same instance on multiple access)
 * <p>
 * Expected result: All constants should be non-null empty arrays of their respective types
 */
class EmptyArraysTest {

    @Nested
    class StringConstant {

        /**
         * Test objective: Verify STRING constant is a valid empty String array
         * Test scenario: Access STRING constant and verify all properties
         * Expected result: STRING is non-null, empty String array, and is singleton
         */
        @Test
        void string_ShouldBeValidEmptyStringArray() {
            // when
            String[] array = EmptyArrays.STRING;

            // then
            assertThat(array).isNotNull();
            assertThat(array).isEmpty();
            assertThat(array).isInstanceOf(String[].class);

            // verify singleton behavior
            assertThat(array).isSameAs(EmptyArrays.STRING);
        }
    }

    @Nested
    class ObjectConstant {

        /**
         * Test objective: Verify OBJECT constant is a valid empty Object array
         * Test scenario: Access OBJECT constant and verify all properties
         * Expected result: OBJECT is non-null, empty Object array, and is singleton
         */
        @Test
        void object_ShouldBeValidEmptyObjectArray() {
            // when
            Object[] array = EmptyArrays.OBJECT;

            // then
            assertThat(array).isNotNull();
            assertThat(array).isEmpty();
            assertThat(array).isInstanceOf(Object[].class);

            // verify singleton behavior
            assertThat(array).isSameAs(EmptyArrays.OBJECT);
        }
    }

    @Nested
    class SingletonBehavior {

        /**
         * Test objective: Verify constants are singletons across multiple access
         * Test scenario: Access constants multiple times and compare references
         * Expected result: Same instance returned each time
         */
        @Test
        void constants_ShouldBeSingletons() {
            // when
            String[] string1 = EmptyArrays.STRING;
            String[] string2 = EmptyArrays.STRING;
            Object[] object1 = EmptyArrays.OBJECT;
            Object[] object2 = EmptyArrays.OBJECT;

            // then
            assertThat(string1).isSameAs(string2);
            assertThat(object1).isSameAs(object2);
        }
    }
}