package io.github.nextentity.core.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test objective: Verify Sizeable interface default methods work correctly
 * <p>
 * Test scenarios:
 * 1. isEmpty returns true when size is 0
 * 2. isEmpty returns false when size is positive
 * 3. isNotEmpty returns false when size is 0
 * 4. isNotEmpty returns true when size is positive
 * <p>
 * Expected result: Default methods correctly determine empty/non-empty state based on size
 */
class SizeableTest {

    /**
     * Test objective: Verify isEmpty returns true when size is 0
     * Test scenario: Create Sizeable with size 0
     * Expected result: isEmpty returns true
     */
    @Test
    void isEmpty_WhenSizeIsZero_ShouldReturnTrue() {
        // given
        Sizeable sizeable = () -> 0;

        // then
        assertThat(sizeable.isEmpty()).isTrue();
        assertThat(sizeable.isNotEmpty()).isFalse();
    }

    /**
     * Test objective: Verify isEmpty returns false when size is positive
     * Test scenario: Create Sizeable with positive size
     * Expected result: isEmpty returns false
     */
    @Test
    void isEmpty_WhenSizeIsPositive_ShouldReturnFalse() {
        // given
        Sizeable sizeable = () -> 5;

        // then
        assertThat(sizeable.isEmpty()).isFalse();
        assertThat(sizeable.isNotEmpty()).isTrue();
    }

    /**
     * Test objective: Verify isEmpty returns false when size is 1
     * Test scenario: Create Sizeable with size 1 (minimum positive size)
     * Expected result: isEmpty returns false
     */
    @Test
    void isEmpty_WhenSizeIsOne_ShouldReturnFalse() {
        // given
        Sizeable sizeable = () -> 1;

        // then
        assertThat(sizeable.isEmpty()).isFalse();
        assertThat(sizeable.isNotEmpty()).isTrue();
    }

    /**
     * Test objective: Verify isNotEmpty is the negation of isEmpty
     * Test scenario: Test both methods with various sizes
     * Expected result: isNotEmpty should always be the opposite of isEmpty
     */
    @Test
    void isNotEmpty_ShouldBeNegationOfIsEmpty() {
        // given
        Sizeable emptySizeable = () -> 0;
        Sizeable nonEmptySizeable = () -> 10;

        // then
        assertThat(emptySizeable.isNotEmpty()).isEqualTo(!emptySizeable.isEmpty());
        assertThat(nonEmptySizeable.isNotEmpty()).isEqualTo(!nonEmptySizeable.isEmpty());
    }

    /**
     * Test objective: Verify size method returns correct value
     * Test scenario: Create Sizeable implementations with different sizes
     * Expected result: size() returns the configured value
     */
    @Test
    void size_ShouldReturnConfiguredValue() {
        // given
        Sizeable sizeable0 = () -> 0;
        Sizeable sizeable1 = () -> 1;
        Sizeable sizeable100 = () -> 100;

        // then
        assertThat(sizeable0.size()).isEqualTo(0);
        assertThat(sizeable1.size()).isEqualTo(1);
        assertThat(sizeable100.size()).isEqualTo(100);
    }
}
