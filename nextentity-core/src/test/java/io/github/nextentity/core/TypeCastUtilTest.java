package io.github.nextentity.core;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

///
/// 测试目标: 验证 TypeCastUtil 的 unsafeCast 行为
class TypeCastUtilTest {

    @Nested
    class UnsafeCast {

        @Test
        void unsafeCast_ReturnsSameObject() {
            String value = "test";
            String result = TypeCastUtil.unsafeCast(value);
            assertThat(result).isSameAs(value);
        }

        @Test
        void unsafeCast_Null_ReturnsNull() {
            Object result = TypeCastUtil.unsafeCast(null);
            assertThat(result).isNull();
        }
    }
}
