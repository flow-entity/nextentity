package io.github.nextentity.jdbc;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

///
/// 测试目标: 验证y JdbcUtil static utility 方法
/// <p>
/// 测试场景s:
/// 1. 验证y TypedParameter interface implementations
/// 2. 验证y setParameters 处理 different types
/// Note: Methods using 结果Set/PreparedStatement require integration tests
class JdbcUtilTest {

    @Nested
    class TypedParameterTest {

        @Test
        void typedParameter_AttributeParameter_HasCorrectType() {
            // given
            AttributeParameter param = new AttributeParameter("test", String.class);

            // when
            Class<?> type = param.type();
            Object value = param.value();

            // then
            assertThat(type).isEqualTo(String.class);
            assertThat(value).isEqualTo("test");
        }

        @Test
        void typedParameter_NullParameter_HasCorrectType() {
            // given
            NullParameter param = new NullParameter(Integer.class);

            // when
            Class<?> type = param.type();
            Object value = param.value();

            // then
            assertThat(type).isEqualTo(Integer.class);
            assertThat(value).isNull();
        }
    }

    @Nested
    class ParameterTypesTest {

        @Test
        void attributeParameter_WithInteger() {
            // given & when
            AttributeParameter param = new AttributeParameter(42, Integer.class);

            // then
            assertThat(param.value()).isEqualTo(42);
            assertThat(param.type()).isEqualTo(Integer.class);
        }

        @Test
        void attributeParameter_WithNull() {
            // given & when
            AttributeParameter param = new AttributeParameter(null, String.class);

            // then
            assertThat(param.value()).isNull();
            assertThat(param.type()).isEqualTo(String.class);
        }

        @Test
        void nullParameter_WithPrimitiveType() {
            // given & when
            NullParameter param = new NullParameter(int.class);

            // then
            assertThat(param.type()).isEqualTo(int.class);
            assertThat(param.value()).isNull();
        }
    }
}
