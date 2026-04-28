package io.github.nextentity.core.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SizeableTest {

    @Test
    void isEmpty_WhenSizeIsZero_ShouldReturnTrue() {
        Sizeable sizeable = () -> 0;
        assertThat(sizeable.isEmpty()).isTrue();
        assertThat(sizeable.isNotEmpty()).isFalse();
    }

    @Test
    void isEmpty_WhenSizeIsPositive_ShouldReturnFalse() {
        Sizeable sizeable = () -> 5;
        assertThat(sizeable.isEmpty()).isFalse();
        assertThat(sizeable.isNotEmpty()).isTrue();
    }

    @Test
    void isNotEmpty_ShouldBeNegationOfIsEmpty() {
        Sizeable emptySizeable = () -> 0;
        Sizeable nonEmptySizeable = () -> 10;
        assertThat(emptySizeable.isNotEmpty()).isEqualTo(!emptySizeable.isEmpty());
        assertThat(nonEmptySizeable.isNotEmpty()).isEqualTo(!nonEmptySizeable.isEmpty());
    }
}
