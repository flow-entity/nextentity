package io.github.nextentity.jpa;

import io.github.nextentity.core.meta.ValueConverter;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

///
 /// 测试目标: 验证y JpaArguments 正确 retrieves values from array
 /// <p>
 /// 测试场景s:
 /// 1. Create JpaArguments with array
 /// 2. Get value by index
 /// 3. Use next() 方法
class JpaArgumentsTest {

    @Nested
    class GetValue {

        @Test
        void get_ReturnsValueAtIndex() {
            // given
            Object[] objects = {"a", "b", "c"};
            JpaArguments args = new JpaArguments(objects);

            // when
            Object result = args.get(0, null);

            // then
            assertThat(result).isEqualTo("a");
        }

        @Test
        void get_SecondElement_ReturnsSecondValue() {
            // given
            Object[] objects = {1, 2, 3};
            JpaArguments args = new JpaArguments(objects);

            // when
            Object result = args.get(1, null);

            // then
            assertThat(result).isEqualTo(2);
        }
    }

    @Nested
    class NextValue {

        @Test
        void next_ReturnsFirstElement() {
            // given
            Object[] objects = {"x", "y", "z"};
            JpaArguments args = new JpaArguments(objects);

            // when
            Object result = args.next(null);

            // then
            assertThat(result).isEqualTo("x");
        }

        @Test
        void next_CalledTwice_ReturnsSequentialValues() {
            // given
            Object[] objects = {1, 2, 3};
            JpaArguments args = new JpaArguments(objects);

            // when
            Object first = args.next(null);
            Object second = args.next(null);

            // then
            assertThat(first).isEqualTo(1);
            assertThat(second).isEqualTo(2);
        }
    }

    @Nested
    class WithConverter {

        @Test
        void get_WithConverter_ReturnsRawValue() {
            // given
            Object[] objects = {"test"};
            JpaArguments args = new JpaArguments(objects);
            ValueConverter<String, String> converter = new ValueConverter<>() {
                @Override
                public String convertToDatabaseColumn(String attribute) {
                    return attribute.toUpperCase();
                }

                @Override
                public String convertToEntityAttribute(String dbData) {
                    return dbData.toLowerCase();
                }
            };

            // when
            Object result = args.get(0, converter);

            // then
            assertThat(result).isEqualTo("test");
        }
    }
}
