package io.github.nextentity.core.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test objective: Verify EmptyArrays interface constants are correct empty arrays
 * <p>
 * Test scenarios:
 * 1. STRING constant is an empty String array
 * 2. OBJECT constant is an empty Object array
 * 3. Constants are truly empty (length = 0)
 * 4. Constants are not null
 * <p>
 * Expected result: All constants should be non-null empty arrays of their respective types
 */
class EmptyArraysTest {

    /**
     * Test objective: Verify STRING constant is a non-null empty String array
     * Test scenario: Access STRING constant and check its properties
     * Expected result: STRING is non-null, empty String array
     */
    @Test
    void string_ShouldBeEmptyStringArray() {
        // given
        String[] emptyStringArray = EmptyArrays.STRING;

        // then
        assertThat(emptyStringArray)
                .isNotNull();
        assertThat(emptyStringArray).isEmpty();
        assertThat(emptyStringArray).hasSize(0);
        assertThat(emptyStringArray).isInstanceOf(String[].class);
    }

    /**
     * Test objective: Verify OBJECT constant is a non-null empty Object array
     * Test scenario: Access OBJECT constant and check its properties
     * Expected result: OBJECT is non-null, empty Object array
     */
    @Test
    void object_ShouldBeEmptyObjectArray() {
        // given
        Object[] emptyObjectArray = EmptyArrays.OBJECT;

        // then
        assertThat(emptyObjectArray)
                .isNotNull();
        assertThat(emptyObjectArray).isEmpty();
        assertThat(emptyObjectArray).hasSize(0);
        assertThat(emptyObjectArray).isInstanceOf(Object[].class);
    }

    /**
     * Test objective: Verify STRING and OBJECT are singletons (same instance on multiple access)
     * Test scenario: Access constants multiple times and compare references
     * Expected result: Same instance returned each time
     */
    @Test
    void constants_ShouldBeSingletons() {
        // given
        String[] string1 = EmptyArrays.STRING;
        String[] string2 = EmptyArrays.STRING;
        Object[] object1 = EmptyArrays.OBJECT;
        Object[] object2 = EmptyArrays.OBJECT;

        // then
        assertThat(string1).isSameAs(string2);
        assertThat(object1).isSameAs(object2);
    }

    /**
     * Test objective: Verify STRING array can be assigned to String[] variable
     * Test scenario: Use STRING constant in a context expecting String[]
     * Expected result: No ClassCastException, works as expected
     */
    @Test
    void string_CanBeUsedAsStringArray() {
        // given
        String[] array = EmptyArrays.STRING;

        // when
        int length = array.length;

        // then
        assertThat(length).isZero();
    }

    /**
     * Test objective: Verify OBJECT array can be assigned to Object[] variable
     * Test scenario: Use OBJECT constant in a context expecting Object[]
     * Expected result: No ClassCastException, works as expected
     */
    @Test
    void object_CanBeUsedAsObjectArray() {
        // given
        Object[] array = EmptyArrays.OBJECT;

        // when
        int length = array.length;

        // then
        assertThat(length).isZero();
    }
}
