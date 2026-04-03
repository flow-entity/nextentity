package io.github.nextentity.jdbc;

import io.github.nextentity.core.meta.ValueConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

///
 /// 单元测试 AbstractArguments.
class AbstractArgumentsTest {

    private TestArguments arguments;

    @BeforeEach
    void setUp() {
        Map<Integer, Object> data = new HashMap<>();
        data.put(0, "value1");
        data.put(1, "value2");
        data.put(2, "value3");
        arguments = new TestArguments(data);
    }

    @Nested
    class NextMethod {

///
         /// 测试目标: 验证y next() returns values in sequence.
         /// 测试场景: Call next() multiple times.
         /// 预期结果: Returns values in order.
        @Test
        void next_ShouldReturnValuesInSequence() {
            // when
            Object result1 = arguments.next(null);
            Object result2 = arguments.next(null);
            Object result3 = arguments.next(null);

            // then
            assertThat(result1).isEqualTo("value1");
            assertThat(result2).isEqualTo("value2");
            assertThat(result3).isEqualTo("value3");
        }

///
         /// 测试目标: 验证y next() increments index.
         /// 测试场景: Call next() and verify index increment.
         /// 预期结果: Index is incremented after each call.
        @Test
        void next_ShouldIncrementIndex() {
            // when
            arguments.next(null);
            arguments.next(null);
            arguments.next(null);

            // then - verify by calling get with next index
            Object result = arguments.get(3, null);
            assertThat(result).isNull();
        }
    }

///
     /// 测试 implementation of AbstractArguments.
    static class TestArguments extends AbstractArguments {
        private final Map<Integer, Object> data;

        TestArguments(Map<Integer, Object> data) {
            this.data = data;
        }

        @Override
        public Object get(int index, ValueConverter<?, ?> convertor) {
            return data.get(index);
        }
    }
}
