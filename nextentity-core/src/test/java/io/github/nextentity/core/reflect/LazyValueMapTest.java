package io.github.nextentity.core.reflect;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/// LazyValueMap 单元测试
///
/// 覆盖场景：
/// 1. put 普通值后 get 返回该值
/// 2. put null 值后 get 返回 null（NullValue 机制）
/// 3. put LazyValue 后 get 触发延迟加载并返回结果
/// 4. containsKey 方法：存在/不存在的 key
/// 5. equals/hashCode 基于 values map
/// 6. 同一个 Method 对象的 put 会覆盖旧值
/// 7. 多线程并发 put/get 的线程安全性
@DisplayName("LazyValueMap")
class LazyValueMapTest {

    interface TestInterface {
        String getName();
        int getAge();
        Object getValue();
    }

    private static Method getNameMethod() throws NoSuchMethodException {
        return TestInterface.class.getMethod("getName");
    }

    private static Method getAgeMethod() throws NoSuchMethodException {
        return TestInterface.class.getMethod("getAge");
    }

    private static Method getValueMethod() throws NoSuchMethodException {
        return TestInterface.class.getMethod("getValue");
    }

    private LazyValueMap map;

    @BeforeEach
    void setUp() {
        map = new LazyValueMap();
    }

    @Nested
    @DisplayName("put + get")
    class PutAndGetTests {

        @Test
        @DisplayName("put 普通值后 get 返回该值")
        void shouldReturnPlainValue() throws NoSuchMethodException {
            Method method = getNameMethod();
            map.put(method, "hello");

            Object result = map.get(method);

            assertThat(result).isEqualTo("hello");
        }

        @Test
        @DisplayName("put 整数值后 get 返回该值")
        void shouldReturnIntegerValue() throws NoSuchMethodException {
            Method method = getAgeMethod();
            map.put(method, 42);

            Object result = map.get(method);

            assertThat(result).isEqualTo(42);
        }

        @Test
        @DisplayName("put null 值后 get 返回 null")
        void shouldReturnNullWhenNullPut() throws NoSuchMethodException {
            Method method = getNameMethod();
            map.put(method, null);

            Object result = map.get(method);

            assertThat(result).isNull();
        }

        @Test
        @DisplayName("put null 值后 containsKey 返回 true")
        void shouldContainKeyWhenNullPut() throws NoSuchMethodException {
            Method method = getNameMethod();
            map.put(method, null);

            assertThat(map.containsKey(method)).isTrue();
        }

        @Test
        @DisplayName("put 多个不同 Method 后分别 get 返回对应值")
        void shouldReturnCorrectValueForDifferentMethods() throws NoSuchMethodException {
            Method nameMethod = getNameMethod();
            Method ageMethod = getAgeMethod();

            map.put(nameMethod, "Alice");
            map.put(ageMethod, 30);

            assertThat(map.get(nameMethod)).isEqualTo("Alice");
            assertThat(map.get(ageMethod)).isEqualTo(30);
        }

        @Test
        @DisplayName("get 不存在的 Method 返回 null")
        void shouldReturnNullForMissingMethod() throws NoSuchMethodException {
            Method method = getNameMethod();

            Object result = map.get(method);

            assertThat(result).isNull();
        }

        @Test
        @DisplayName("同一个 Method 的 put 会覆盖旧值")
        void shouldOverwriteValueOnRepeatedPut() throws NoSuchMethodException {
            Method method = getNameMethod();
            map.put(method, "first");
            map.put(method, "second");

            assertThat(map.get(method)).isEqualTo("second");
        }

        @Test
        @DisplayName("覆盖 null 值后 get 返回新值")
        void shouldOverwriteNullWithNewValue() throws NoSuchMethodException {
            Method method = getNameMethod();
            map.put(method, null);
            map.put(method, "replaced");

            assertThat(map.get(method)).isEqualTo("replaced");
        }

        @Test
        @DisplayName("覆盖普通值为 null 后 get 返回 null")
        void shouldOverwriteValueWithNull() throws NoSuchMethodException {
            Method method = getNameMethod();
            map.put(method, "value");
            map.put(method, null);

            assertThat(map.get(method)).isNull();
        }
    }

    @Nested
    @DisplayName("LazyValue 延迟加载")
    class LazyValueTests {

        @Test
        @DisplayName("put LazyValue 后 get 触发延迟加载并返回结果")
        void shouldResolveLazyValueOnGet() throws NoSuchMethodException {
            Method method = getNameMethod();
            LazyValue lazyValue = new LazyValue(id -> "lazy-result", "key1");
            map.put(method, lazyValue);

            Object result = map.get(method);

            assertThat(result).isEqualTo("lazy-result");
        }

        @Test
        @DisplayName("LazyValue 的 loader 仅在首次 get 时调用")
        void shouldInvokeLoaderOnlyOnce() throws NoSuchMethodException {
            Method method = getNameMethod();
            AtomicInteger callCount = new AtomicInteger(0);
            LazyValue lazyValue = new LazyValue(id -> {
                callCount.incrementAndGet();
                return "computed";
            }, "key1");
            map.put(method, lazyValue);

            map.get(method);
            map.get(method);
            map.get(method);

            assertThat(callCount.get()).isEqualTo(1);
        }

        @Test
        @DisplayName("LazyValue 返回 null 时 get 返回 null")
        void shouldReturnNullWhenLazyValueLoadsNull() throws NoSuchMethodException {
            Method method = getValueMethod();
            LazyValue lazyValue = new LazyValue(id -> null, "key1");
            map.put(method, lazyValue);

            Object result = map.get(method);

            assertThat(result).isNull();
        }

        @Test
        @DisplayName("LazyValue 与普通值可共存于同一 map")
        void shouldMixLazyAndPlainValues() throws NoSuchMethodException {
            Method nameMethod = getNameMethod();
            Method ageMethod = getAgeMethod();

            map.put(nameMethod, new LazyValue(id -> "lazy-name", "key1"));
            map.put(ageMethod, 25);

            assertThat(map.get(nameMethod)).isEqualTo("lazy-name");
            assertThat(map.get(ageMethod)).isEqualTo(25);
        }

        @Test
        @DisplayName("覆盖 LazyValue 为普通值后 get 返回普通值")
        void shouldOverwriteLazyValueWithPlainValue() throws NoSuchMethodException {
            Method method = getNameMethod();
            map.put(method, new LazyValue(id -> "lazy", "key1"));
            map.put(method, "plain");

            assertThat(map.get(method)).isEqualTo("plain");
        }

        @Test
        @DisplayName("覆盖普通值为 LazyValue 后 get 触发延迟加载")
        void shouldOverwritePlainValueWithLazyValue() throws NoSuchMethodException {
            Method method = getNameMethod();
            map.put(method, "plain");
            map.put(method, new LazyValue(id -> "lazy", "key1"));

            assertThat(map.get(method)).isEqualTo("lazy");
        }

        @Test
        @DisplayName("LazyValue loader 抛异常时 get 传播异常")
        void shouldPropagateExceptionFromLazyValueLoader() throws NoSuchMethodException {
            Method method = getNameMethod();
            LazyValue failing = new LazyValue(id -> {
                throw new RuntimeException("fail");
            }, "key");
            map.put(method, failing);

            assertThatThrownBy(() -> map.get(method))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("fail");
        }

        @Test
        @DisplayName("LazyValue loader 抛异常后再次 get 会重试")
        void shouldRetryLazyValueAfterException() throws NoSuchMethodException {
            Method method = getNameMethod();
            AtomicInteger callCount = new AtomicInteger(0);
            LazyValue lazyValue = new LazyValue(id -> {
                if (callCount.incrementAndGet() < 2) {
                    throw new RuntimeException("transient");
                }
                return "recovered";
            }, "key");
            map.put(method, lazyValue);

            assertThatThrownBy(() -> map.get(method)).isInstanceOf(RuntimeException.class);
            assertThat(map.get(method)).isEqualTo("recovered");
            assertThat(callCount.get()).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("containsKey")
    class ContainsKeyTests {

        @Test
        @DisplayName("存在的 Method 返回 true")
        void shouldReturnTrueForExistingKey() throws NoSuchMethodException {
            Method method = getNameMethod();
            map.put(method, "value");

            assertThat(map.containsKey(method)).isTrue();
        }

        @Test
        @DisplayName("不存在的 Method 返回 false")
        void shouldReturnFalseForMissingKey() throws NoSuchMethodException {
            Method method = getNameMethod();

            assertThat(map.containsKey(method)).isFalse();
        }

        @Test
        @DisplayName("put null 值后 containsKey 返回 true")
        void shouldReturnTrueWhenNullWasPut() throws NoSuchMethodException {
            Method method = getNameMethod();
            map.put(method, null);

            assertThat(map.containsKey(method)).isTrue();
        }

        @Test
        @DisplayName("不同的 Method 互不影响")
        void shouldDistinguishDifferentMethods() throws NoSuchMethodException {
            Method nameMethod = getNameMethod();
            Method ageMethod = getAgeMethod();

            map.put(nameMethod, "value");

            assertThat(map.containsKey(nameMethod)).isTrue();
            assertThat(map.containsKey(ageMethod)).isFalse();
        }

        @Test
        @DisplayName("put null 与未 put 的 key 可通过 containsKey 区分")
        void shouldDistinguishNullPutFromMissingKeyViaContainsKey() throws NoSuchMethodException {
            Method nameMethod = getNameMethod();
            Method ageMethod = getAgeMethod();
            map.put(nameMethod, null);

            assertThat(map.containsKey(nameMethod)).isTrue();
            assertThat(map.containsKey(ageMethod)).isFalse();
            assertThat(map.get(nameMethod)).isNull();
            assertThat(map.get(ageMethod)).isNull();
        }
    }

    @Nested
    @DisplayName("equals / hashCode")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("相同内容的两个 LazyValueMap 相等")
        void shouldBeEqualWhenContentIsSame() throws NoSuchMethodException {
            Method method = getNameMethod();

            LazyValueMap other = new LazyValueMap();
            map.put(method, "value");
            other.put(method, "value");

            assertThat(map).isEqualTo(other);
            assertThat(map.hashCode()).isEqualTo(other.hashCode());
        }

        @Test
        @DisplayName("空 map 相等")
        void shouldBeEqualWhenBothEmpty() {
            LazyValueMap other = new LazyValueMap();

            assertThat(map).isEqualTo(other);
            assertThat(map.hashCode()).isEqualTo(other.hashCode());
        }

        @Test
        @DisplayName("内容不同时不相等")
        void shouldNotBeEqualWhenContentDiffers() throws NoSuchMethodException {
            Method method = getNameMethod();

            LazyValueMap other = new LazyValueMap();
            map.put(method, "value1");
            other.put(method, "value2");

            assertThat(map).isNotEqualTo(other);
        }

        @Test
        @DisplayName("一个为空一个不为空时不相等")
        void shouldNotBeEqualWhenOneIsEmpty() throws NoSuchMethodException {
            LazyValueMap other = new LazyValueMap();
            map.put(getNameMethod(), "value");

            assertThat(map).isNotEqualTo(other);
        }

        @Test
        @DisplayName("与 null 比较返回 false")
        void shouldNotBeEqualToNull() {
            assertThat(map).isNotEqualTo(null);
        }

        @Test
        @DisplayName("与非 LazyValueMap 类型比较返回 false")
        void shouldNotBeEqualToDifferentType() {
            assertThat(map).isNotEqualTo(new Object());
        }

        @Test
        @DisplayName("equals 满足自反性")
        void shouldBeReflexive() throws NoSuchMethodException {
            map.put(getNameMethod(), "value");

            assertThat(map).isEqualTo(map);
        }

        @Test
        @DisplayName("equals 满足对称性")
        void shouldBeSymmetric() throws NoSuchMethodException {
            LazyValueMap other = new LazyValueMap();

            assertThat(map.equals(other)).isEqualTo(other.equals(map));
        }

        @Test
        @DisplayName("equals 满足传递性")
        void shouldBeTransitive() throws NoSuchMethodException {
            Method method = getNameMethod();

            LazyValueMap a = new LazyValueMap();
            LazyValueMap b = new LazyValueMap();
            LazyValueMap c = new LazyValueMap();
            a.put(method, "value");
            b.put(method, "value");
            c.put(method, "value");

            assertThat(a.equals(b) && b.equals(c)).isTrue();
            assertThat(a.equals(c)).isTrue();
        }

        @Test
        @DisplayName("put null 值后的两个 map 相等")
        void shouldBeEqualWhenBothContainNullValue() throws NoSuchMethodException {
            Method method = getNameMethod();

            LazyValueMap other = new LazyValueMap();
            map.put(method, null);
            other.put(method, null);

            assertThat(map).isEqualTo(other);
            assertThat(map.hashCode()).isEqualTo(other.hashCode());
        }

        @Test
        @DisplayName("put 相同 LazyValue（相同 identifier）后的两个 map 相等")
        void shouldBeEqualWhenBothContainSameLazyValue() throws NoSuchMethodException {
            Method method = getNameMethod();

            LazyValueMap other = new LazyValueMap();
            map.put(method, new LazyValue(id -> "a", "same-id"));
            other.put(method, new LazyValue(id -> "b", "same-id"));

            assertThat(map).isEqualTo(other);
            assertThat(map.hashCode()).isEqualTo(other.hashCode());
        }
    }

    @Nested
    @DisplayName("线程安全性")
    class ConcurrencyTests {

        @Test
        @DisplayName("多线程并发 put 和 get 不抛异常且数据一致")
        void shouldBeThreadSafeUnderConcurrentPutAndGet() throws Exception {
            Method nameMethod = getNameMethod();
            Method ageMethod = getAgeMethod();
            int threadCount = 16;
            int iterations = 100;
            CountDownLatch startLatch = new CountDownLatch(1);
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            List<Future<?>> futures = new ArrayList<>();

            for (int i = 0; i < threadCount; i++) {
                final int index = i;
                futures.add(executor.submit(() -> {
                    try {
                        startLatch.await();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                    for (int j = 0; j < iterations; j++) {
                        if (index % 2 == 0) {
                            map.put(nameMethod, "name-" + index);
                            map.get(nameMethod);
                        } else {
                            map.put(ageMethod, index);
                            map.get(ageMethod);
                        }
                    }
                }));
            }

            startLatch.countDown();
            executor.shutdown();
            assertThat(executor.awaitTermination(5, TimeUnit.SECONDS)).isTrue();

            for (Future<?> future : futures) {
                future.get();
            }

            assertThat(map.containsKey(nameMethod)).isTrue();
            assertThat(map.containsKey(ageMethod)).isTrue();
        }

        @Test
        @DisplayName("多线程并发 get 同一个 LazyValue 仅加载一次")
        void shouldResolveLazyValueOnlyOnceUnderConcurrency() throws Exception {
            Method method = getNameMethod();
            AtomicInteger callCount = new AtomicInteger(0);
            LazyValue lazyValue = new LazyValue(id -> {
                callCount.incrementAndGet();
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return "resolved";
            }, "key1");
            map.put(method, lazyValue);

            int threadCount = 16;
            CountDownLatch startLatch = new CountDownLatch(1);
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            List<Future<Object>> futures = new ArrayList<>();

            for (int i = 0; i < threadCount; i++) {
                futures.add(executor.submit(() -> {
                    startLatch.await();
                    return map.get(method);
                }));
            }

            startLatch.countDown();
            executor.shutdown();
            assertThat(executor.awaitTermination(5, TimeUnit.SECONDS)).isTrue();

            for (Future<Object> future : futures) {
                assertThat(future.get()).isEqualTo("resolved");
            }
            assertThat(callCount.get()).isEqualTo(1);
        }

        @Test
        @DisplayName("多线程并发 put null 值后 get 返回 null")
        void shouldHandleConcurrentNullPut() throws Exception {
            Method method = getNameMethod();
            int threadCount = 8;
            CountDownLatch startLatch = new CountDownLatch(1);
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            List<Future<?>> futures = new ArrayList<>();

            for (int i = 0; i < threadCount; i++) {
                futures.add(executor.submit(() -> {
                    try {
                        startLatch.await();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                    map.put(method, null);
                }));
            }

            startLatch.countDown();
            executor.shutdown();
            assertThat(executor.awaitTermination(5, TimeUnit.SECONDS)).isTrue();

            for (Future<?> future : futures) {
                future.get();
            }

            assertThat(map.containsKey(method)).isTrue();
            assertThat(map.get(method)).isNull();
        }
    }
}
