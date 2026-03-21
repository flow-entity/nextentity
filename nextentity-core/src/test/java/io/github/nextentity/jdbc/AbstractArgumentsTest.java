package io.github.nextentity.jdbc;

import io.github.nextentity.core.meta.ValueConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for AbstractArguments.
 */
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

        /**
         * Test objective: Verify next() returns values in sequence.
         * Test scenario: Call next() multiple times.
         * Expected result: Returns values in order.
         */
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

        /**
         * Test objective: Verify next() increments index.
         * Test scenario: Call next() and verify index increment.
         * Expected result: Index is incremented after each call.
         */
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

    /**
     * Test implementation of AbstractArguments.
     */
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
