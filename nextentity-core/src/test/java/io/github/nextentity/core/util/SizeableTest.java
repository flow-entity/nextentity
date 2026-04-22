package io.github.nextentity.core.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

///
/// 测试目标: 验证y Sizeable interface default 方法 work 正确
/// <p>
/// 测试场景s:
/// 1. isEmpty returns true when size is 0
/// 2. isEmpty returns false when size is positive
/// 3. isNotEmpty returns false when size is 0
/// 4. isNotEmpty returns true when size is positive
/// <p>
/// 预期结果: Default 方法 正确 determine empty/non-empty state based on size
class SizeableTest {

    ///
    /// 测试目标: 验证y isEmpty returns true when size is 0
    /// 测试场景: Create Sizeable with size 0
    /// 预期结果: isEmpty returns true
    @Test
    void isEmpty_WhenSizeIsZero_ShouldReturnTrue() {
        // given
        Sizeable sizeable = () -> 0;

        // then
        assertThat(sizeable.isEmpty()).isTrue();
        assertThat(sizeable.isNotEmpty()).isFalse();
    }

    ///
    /// 测试目标: 验证y isEmpty returns false when size is positive
    /// 测试场景: Create Sizeable with positive size
    /// 预期结果: isEmpty returns false
    @Test
    void isEmpty_WhenSizeIsPositive_ShouldReturnFalse() {
        // given
        Sizeable sizeable = () -> 5;

        // then
        assertThat(sizeable.isEmpty()).isFalse();
        assertThat(sizeable.isNotEmpty()).isTrue();
    }

    ///
    /// 测试目标: 验证y isEmpty returns false when size is 1
    /// 测试场景: Create Sizeable with size 1 (minimum positive size)
    /// 预期结果: isEmpty returns false
    @Test
    void isEmpty_WhenSizeIsOne_ShouldReturnFalse() {
        // given
        Sizeable sizeable = () -> 1;

        // then
        assertThat(sizeable.isEmpty()).isFalse();
        assertThat(sizeable.isNotEmpty()).isTrue();
    }

    ///
    /// 测试目标: 验证y isNotEmpty is the negation of isEmpty
    /// 测试场景: 测试 both 方法 with various sizes
    /// 预期结果: isNotEmpty should always be the opposite of isEmpty
    @Test
    void isNotEmpty_ShouldBeNegationOfIsEmpty() {
        // given
        Sizeable emptySizeable = () -> 0;
        Sizeable nonEmptySizeable = () -> 10;

        // then
        assertThat(emptySizeable.isNotEmpty()).isEqualTo(!emptySizeable.isEmpty());
        assertThat(nonEmptySizeable.isNotEmpty()).isEqualTo(!nonEmptySizeable.isEmpty());
    }

    ///
    /// 测试目标: 验证y size 方法 returns correct value
    /// 测试场景: Create Sizeable implementations with different sizes
    /// 预期结果: size() returns the configured value
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
