package io.github.nextentity.core.reflect;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/// LazyValue 单元测试
///
/// 覆盖场景：
/// 1. 首次调用 get() 触发 loader 并缓存结果
/// 2. 重复调用 get() 返回缓存值（不再调用 loader）
/// 3. loader 返回 null 值时正确处理
/// 4. 重入检测：loader 内部再次调用同一个 LazyValue 的 get() 时抛出 IllegalStateException
/// 5. equals/hashCode 基于 identifier
/// 6. 多线程并发访问时的线程安全性
@DisplayName("LazyValue")
class LazyValueTest {

    @Nested
    @DisplayName("get()")
    class GetTests {

        @Test
        @DisplayName("首次调用触发 loader 并缓存结果")
        void shouldInvokeLoaderOnFirstGet() {
            AtomicInteger callCount = new AtomicInteger(0);
            LazyValue lazyValue = new LazyValue(id -> {
                callCount.incrementAndGet();
                return "result-" + id;
            }, "key1");

            Object result = lazyValue.get();

            assertThat(result).isEqualTo("result-key1");
            assertThat(callCount.get()).isEqualTo(1);
        }

        @Test
        @DisplayName("重复调用返回缓存值，loader 仅执行一次")
        void shouldReturnCachedValueOnRepeatedGet() {
            AtomicInteger callCount = new AtomicInteger(0);
            LazyValue lazyValue = new LazyValue(id -> {
                callCount.incrementAndGet();
                return "computed";
            }, "key1");

            Object first = lazyValue.get();
            Object second = lazyValue.get();
            Object third = lazyValue.get();

            assertThat(first).isEqualTo("computed");
            assertThat(second).isSameAs(first);
            assertThat(third).isSameAs(first);
            assertThat(callCount.get()).isEqualTo(1);
        }

        @Test
        @DisplayName("loader 返回 null 时正确处理")
        void shouldHandleNullReturnValue() {
            AtomicInteger callCount = new AtomicInteger(0);
            LazyValue lazyValue = new LazyValue(id -> {
                callCount.incrementAndGet();
                return null;
            }, "key1");

            Object first = lazyValue.get();
            Object second = lazyValue.get();

            assertThat(first).isNull();
            assertThat(second).isNull();
            assertThat(callCount.get()).isEqualTo(1);
        }

        @Test
        @DisplayName("loader 接收 identifier 作为参数")
        void shouldPassIdentifierToLoader() {
            Object[] receivedId = {null};
            Object identifier = new Object();
            LazyValue lazyValue = new LazyValue(id -> {
                receivedId[0] = id;
                return "value";
            }, identifier);

            lazyValue.get();

            assertThat(receivedId[0]).isSameAs(identifier);
        }

        @Test
        @DisplayName("构造时不调用 loader")
        void shouldNotInvokeLoaderDuringConstruction() {
            AtomicBoolean initialized = new AtomicBoolean(false);
            new LazyValue(id -> {
                initialized.set(true);
                return "value";
            }, "key");

            assertThat(initialized.get()).isFalse();
        }

        @Test
        @DisplayName("loader 抛出异常时正确传播")
        void shouldPropagateExceptionFromLoader() {
            LazyValue lazyValue = new LazyValue(id -> {
                throw new RuntimeException("transient error");
            }, "key1");

            assertThatThrownBy(lazyValue::get)
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("transient error");
        }

        @Test
        @DisplayName("loader 抛出异常后重试而非缓存失败")
        void shouldRetryLoaderAfterException() {
            AtomicInteger callCount = new AtomicInteger(0);
            LazyValue lazyValue = new LazyValue(id -> {
                if (callCount.incrementAndGet() < 3) {
                    throw new RuntimeException("transient error");
                }
                return "recovered";
            }, "key");

            assertThatThrownBy(lazyValue::get).isInstanceOf(RuntimeException.class);
            assertThatThrownBy(lazyValue::get).isInstanceOf(RuntimeException.class);
            assertThat(lazyValue.get()).isEqualTo("recovered");
            assertThat(callCount.get()).isEqualTo(3);
        }
    }

    @Nested
    @DisplayName("重入检测")
    class ReentryTests {

        @Test
        @DisplayName("loader 内部递归调用 get() 时抛出 IllegalStateException")
        void shouldThrowOnRecursiveGet() {
            // 使用数组打破"可能尚未初始化"的编译期限制
            LazyValue[] holder = new LazyValue[1];
            holder[0] = new LazyValue(id -> {
                // 模拟递归调用：loader 内部再次调用 get()
                return holder[0].get();
            }, "key1");

            assertThatThrownBy(holder[0]::get)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Recursive invocation");
        }
    }

    @Nested
    @DisplayName("equals / hashCode")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("identifier 相同时 equals 返回 true")
        void shouldBeEqualWhenIdentifierIsSame() {
            LazyValue a = new LazyValue(id -> "a", "same-key");
            LazyValue b = new LazyValue(id -> "b", "same-key");

            assertThat(a).isEqualTo(b);
            assertThat(a.hashCode()).isEqualTo(b.hashCode());
        }

        @Test
        @DisplayName("identifier 不同时 equals 返回 false")
        void shouldNotBeEqualWhenIdentifierDiffers() {
            LazyValue a = new LazyValue(id -> "a", "key-1");
            LazyValue b = new LazyValue(id -> "b", "key-2");

            assertThat(a).isNotEqualTo(b);
        }

        @Test
        @DisplayName("与 null 比较返回 false")
        void shouldNotBeEqualToNull() {
            LazyValue lazyValue = new LazyValue(id -> "value", "key");

            assertThat(lazyValue).isNotEqualTo(null);
        }

        @Test
        @DisplayName("与非 LazyValue 类型比较返回 false")
        void shouldNotBeEqualToDifferentType() {
            LazyValue lazyValue = new LazyValue(id -> "value", "key");

            assertThat(lazyValue).isNotEqualTo("key");
            assertThat(lazyValue).isNotEqualTo(new Object());
        }

        @Test
        @DisplayName("identifier 为 null 时 equals 和 hashCode 正常工作")
        void shouldHandleNullIdentifier() {
            LazyValue a = new LazyValue(id -> "a", null);
            LazyValue b = new LazyValue(id -> "b", null);

            assertThat(a).isEqualTo(b);
            assertThat(a.hashCode()).isEqualTo(b.hashCode());
        }

        @Test
        @DisplayName("identifier 为 null 与非 null 不相等")
        void shouldNotBeEqualWhenOneIdentifierIsNull() {
            LazyValue withNull = new LazyValue(id -> "a", null);
            LazyValue withNonNull = new LazyValue(id -> "b", "key");

            assertThat(withNull).isNotEqualTo(withNonNull);
        }

        @Test
        @DisplayName("equals 满足自反性")
        void shouldBeReflexive() {
            LazyValue lazyValue = new LazyValue(id -> "a", "key");

            assertThat(lazyValue).isEqualTo(lazyValue);
        }

        @Test
        @DisplayName("equals 满足对称性")
        void shouldBeSymmetric() {
            LazyValue a = new LazyValue(id -> "a", "same");
            LazyValue b = new LazyValue(id -> "b", "same");

            assertThat(a.equals(b)).isEqualTo(b.equals(a));
        }
    }

    @Nested
    @DisplayName("线程安全性")
    class ConcurrencyTests {

        @Test
        @DisplayName("多线程并发调用 get() 时 loader 仅执行一次")
        void shouldInvokeLoaderOnlyOnceUnderConcurrency() throws Exception {
            AtomicInteger callCount = new AtomicInteger(0);
            Object uniqueResult = new Object();
            LazyValue lazyValue = new LazyValue(id -> {
                callCount.incrementAndGet();
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return uniqueResult;
            }, "key1");

            int threadCount = 16;
            CountDownLatch startLatch = new CountDownLatch(1);
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            List<Future<Object>> futures = new ArrayList<>();

            for (int i = 0; i < threadCount; i++) {
                futures.add(executor.submit(() -> {
                    startLatch.await();
                    return lazyValue.get();
                }));
            }

            startLatch.countDown();
            executor.shutdown();
            assertThat(executor.awaitTermination(5, TimeUnit.SECONDS)).isTrue();

            Object[] results = new Object[threadCount];
            for (int i = 0; i < threadCount; i++) {
                results[i] = futures.get(i).get();
            }

            assertThat(callCount.get()).isEqualTo(1);
            for (Object result : results) {
                assertThat(result).isSameAs(uniqueResult);
            }
        }

        @Test
        @DisplayName("多线程并发获取相同引用")
        void shouldReturnSameReferenceUnderConcurrency() throws Exception {
            AtomicInteger callCount = new AtomicInteger(0);
            LazyValue lazyValue = new LazyValue(id -> {
                callCount.incrementAndGet();
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return new StringBuilder("shared");
            }, "key1");

            int threadCount = 8;
            CountDownLatch startLatch = new CountDownLatch(1);
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            List<Future<Object>> futures = new ArrayList<>();

            for (int i = 0; i < threadCount; i++) {
                futures.add(executor.submit(() -> {
                    startLatch.await();
                    return lazyValue.get();
                }));
            }

            startLatch.countDown();
            executor.shutdown();
            assertThat(executor.awaitTermination(5, TimeUnit.SECONDS)).isTrue();

            Object first = futures.getFirst().get();
            for (Future<Object> future : futures) {
                assertThat(future.get()).isSameAs(first);
            }
            assertThat(callCount.get()).isEqualTo(1);
        }
    }
}
