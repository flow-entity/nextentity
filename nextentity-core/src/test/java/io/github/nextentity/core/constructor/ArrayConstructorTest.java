package io.github.nextentity.core.constructor;

import io.github.nextentity.api.model.Tuple;
import io.github.nextentity.core.expression.LiteralNode;
import io.github.nextentity.core.meta.ValueConverter;
import io.github.nextentity.jdbc.Arguments;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("ArrayConstructor")
class ArrayConstructorTest {

    private static SelectItem createSelectItem() {
        return SelectItem.of(LiteralNode.TRUE, mock(ValueConverter.class));
    }

    private static ValueConstructor mockValueConstructor(List<SelectItem> columns, Object constructResult) {
        ValueConstructor vc = mock(ValueConstructor.class);
        when(vc.columns()).thenReturn(columns);
        when(vc.construct(any(Arguments.class))).thenReturn(constructResult);
        return vc;
    }

    // ==================== Constructor ====================

    @Nested
    @DisplayName("Constructor")
    class Constructor {

        @Test
        @DisplayName("constructs with empty elements list")
        void constructsEmptyElements() {
            ArrayConstructor ac = new ArrayConstructor(List.of());

            assertThat(ac.columns()).isEmpty();
        }

        @Test
        @DisplayName("constructs with single element")
        void constructsSingleElement() {
            ValueConstructor element = mockValueConstructor(List.of(createSelectItem()), "value");

            ArrayConstructor ac = new ArrayConstructor(List.of(element));

            assertThat(ac.columns()).hasSize(1);
        }

        @Test
        @DisplayName("constructs with multiple elements")
        void constructsMultipleElements() {
            ValueConstructor e1 = mockValueConstructor(List.of(createSelectItem()), "a");
            ValueConstructor e2 = mockValueConstructor(List.of(createSelectItem()), "b");

            ArrayConstructor ac = new ArrayConstructor(List.of(e1, e2));

            assertThat(ac.columns()).hasSize(2);
        }
    }

    // ==================== columns() ====================

    @Nested
    @DisplayName("columns()")
    class Columns {

        @Test
        @DisplayName("returns empty list for empty elements")
        void returnsEmptyForEmptyElements() {
            ArrayConstructor ac = new ArrayConstructor(List.of());

            assertThat(ac.columns()).isEmpty();
        }

        @Test
        @DisplayName("returns flattened columns from all elements")
        void returnsFlattenedColumns() {
            SelectItem col1 = createSelectItem();
            SelectItem col2 = createSelectItem();
            SelectItem col3 = createSelectItem();
            ValueConstructor e1 = mockValueConstructor(List.of(col1, col2), "a");
            ValueConstructor e2 = mockValueConstructor(List.of(col3), "b");

            ArrayConstructor ac = new ArrayConstructor(List.of(e1, e2));

            assertThat(ac.columns()).containsExactly(col1, col2, col3);
        }

        @Test
        @DisplayName("each element's columns appear in order")
        void columnsAppearInOrder() {
            SelectItem col1 = createSelectItem();
            SelectItem col2 = createSelectItem();
            SelectItem col3 = createSelectItem();
            SelectItem col4 = createSelectItem();
            ValueConstructor e1 = mockValueConstructor(List.of(col1), "a");
            ValueConstructor e2 = mockValueConstructor(List.of(col2, col3), "b");
            ValueConstructor e3 = mockValueConstructor(List.of(col4), "c");

            ArrayConstructor ac = new ArrayConstructor(List.of(e1, e2, e3));

            assertThat(ac.columns()).containsExactly(col1, col2, col3, col4);
        }
    }

    // ==================== construct() ====================

    @Nested
    @DisplayName("construct()")
    class Construct {

        @Test
        @DisplayName("returns Tuple wrapping constructed values")
        void returnsTupleWrappingValues() {
            ValueConstructor element = mockValueConstructor(List.of(createSelectItem()), 42);
            ArrayConstructor ac = new ArrayConstructor(List.of(element));
            Arguments args = mock(Arguments.class);

            Object result = ac.construct(args);

            assertThat(result).isInstanceOf(Tuple.class);
        }

        @Test
        @DisplayName("elements are called in order")
        void elementsCalledInOrder() {
            List<String> callOrder = Collections.synchronizedList(new ArrayList<>());
            ValueConstructor e1 = mock(ValueConstructor.class);
            when(e1.columns()).thenReturn(List.of(createSelectItem()));
            when(e1.construct(any(Arguments.class))).thenAnswer(inv -> {
                callOrder.add("e1");
                return "first";
            });
            ValueConstructor e2 = mock(ValueConstructor.class);
            when(e2.columns()).thenReturn(List.of(createSelectItem()));
            when(e2.construct(any(Arguments.class))).thenAnswer(inv -> {
                callOrder.add("e2");
                return "second";
            });
            ArrayConstructor ac = new ArrayConstructor(List.of(e1, e2));
            Arguments args = mock(Arguments.class);

            ac.construct(args);

            assertThat(callOrder).containsExactly("e1", "e2");
        }

        @Test
        @DisplayName("null values from elements are preserved in the array")
        void nullValuesPreserved() {
            ValueConstructor e1 = mockValueConstructor(List.of(createSelectItem()), "value");
            ValueConstructor e2 = mockValueConstructor(List.of(createSelectItem()), null);
            ArrayConstructor ac = new ArrayConstructor(List.of(e1, e2));
            Arguments args = mock(Arguments.class);

            Tuple tuple = (Tuple) ac.construct(args);

            assertThat((Object) tuple.get(0)).isEqualTo("value");
            assertThat((Object) tuple.get(1)).isNull();
        }

        @Test
        @DisplayName("empty elements returns Tuple with size 0")
        void emptyElementsReturnsTupleSize0() {
            ArrayConstructor ac = new ArrayConstructor(List.of());
            Arguments args = mock(Arguments.class);

            Tuple tuple = (Tuple) ac.construct(args);

            assertThat(tuple.size()).isEqualTo(0);
        }

        @Test
        @DisplayName("single element returns Tuple with size 1")
        void singleElementReturnsTupleSize1() {
            ValueConstructor element = mockValueConstructor(List.of(createSelectItem()), "only");
            ArrayConstructor ac = new ArrayConstructor(List.of(element));
            Arguments args = mock(Arguments.class);

            Tuple tuple = (Tuple) ac.construct(args);

            assertThat(tuple.size()).isEqualTo(1);
            assertThat((Object) tuple.get(0)).isEqualTo("only");
        }

        @Test
        @DisplayName("multiple elements returns Tuple with correct size and values")
        void multipleElementsReturnsCorrectTuple() {
            ValueConstructor e1 = mockValueConstructor(List.of(createSelectItem()), 10);
            ValueConstructor e2 = mockValueConstructor(List.of(createSelectItem()), 20);
            ValueConstructor e3 = mockValueConstructor(List.of(createSelectItem()), 30);
            ArrayConstructor ac = new ArrayConstructor(List.of(e1, e2, e3));
            Arguments args = mock(Arguments.class);

            Tuple tuple = (Tuple) ac.construct(args);

            assertThat(tuple.size()).isEqualTo(3);
            assertThat((Object) tuple.get(0)).isEqualTo(10);
            assertThat((Object) tuple.get(1)).isEqualTo(20);
            assertThat((Object) tuple.get(2)).isEqualTo(30);
        }

        @Test
        @DisplayName("Tuple.get(i) returns the value from the i-th element's construct")
        void tupleGetReturnsCorrespondingValue() {
            ValueConstructor e1 = mockValueConstructor(List.of(createSelectItem()), "alpha");
            ValueConstructor e2 = mockValueConstructor(List.of(createSelectItem()), "beta");
            ArrayConstructor ac = new ArrayConstructor(List.of(e1, e2));
            Arguments args = mock(Arguments.class);

            Tuple tuple = (Tuple) ac.construct(args);

            assertThat((Object) tuple.get(0)).isEqualTo("alpha");
            assertThat((Object) tuple.get(1)).isEqualTo("beta");
            verify(e1).construct(args);
            verify(e2).construct(args);
        }
    }

    // ==================== Edge cases ====================

    @Nested
    @DisplayName("Edge cases")
    class EdgeCases {

        @Test
        @DisplayName("RuntimeException from element.construct() propagates directly")
        void runtimeExceptionPropagates() {
            ValueConstructor failing = mock(ValueConstructor.class);
            when(failing.columns()).thenReturn(List.of(createSelectItem()));
            when(failing.construct(any(Arguments.class))).thenThrow(new RuntimeException("construct failed"));
            ArrayConstructor ac = new ArrayConstructor(List.of(failing));
            Arguments args = mock(Arguments.class);

            assertThatThrownBy(() -> ac.construct(args))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("construct failed");
        }

        @Test
        @DisplayName("concurrent construct calls produce correct results")
        void concurrentConstructCalls() throws Exception {
            ValueConstructor e1 = mockValueConstructor(List.of(createSelectItem()), "a");
            ValueConstructor e2 = mockValueConstructor(List.of(createSelectItem()), "b");
            ArrayConstructor ac = new ArrayConstructor(List.of(e1, e2));

            int threadCount = 20;
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            CountDownLatch readyLatch = new CountDownLatch(threadCount);
            CountDownLatch startLatch = new CountDownLatch(1);
            AtomicInteger successCount = new AtomicInteger(0);

            for (int i = 0; i < threadCount; i++) {
                executor.submit(() -> {
                    readyLatch.countDown();
                    try {
                        startLatch.await();
                        Arguments args = mock(Arguments.class);
                        Tuple tuple = (Tuple) ac.construct(args);
                        if (tuple.size() == 2 && "a".equals(tuple.get(0)) && "b".equals(tuple.get(1))) {
                            successCount.incrementAndGet();
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }

            readyLatch.await(5, TimeUnit.SECONDS);
            startLatch.countDown();
            executor.shutdown();
            boolean terminated = executor.awaitTermination(10, TimeUnit.SECONDS);

            assertThat(terminated).isTrue();
            assertThat(successCount.get()).isEqualTo(threadCount);
        }

        @Test
        @DisplayName("null elements list causes NPE on construct")
        void nullElementsListCausesNPE() {
            ArrayConstructor ac = new ArrayConstructor(null);
            Arguments args = mock(Arguments.class);

            assertThatThrownBy(() -> ac.construct(args))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("multiple construct calls produce distinct Tuple instances")
        void multipleConstructsProduceDistinctTuples() {
            ValueConstructor element = mockValueConstructor(List.of(createSelectItem()), "value");
            ArrayConstructor ac = new ArrayConstructor(List.of(element));
            Arguments args = mock(Arguments.class);

            Tuple first = (Tuple) ac.construct(args);
            Tuple second = (Tuple) ac.construct(args);

            assertThat(first).isNotSameAs(second);
            assertThat(first).isEqualTo(second);
        }
    }
}
