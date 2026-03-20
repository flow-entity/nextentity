package io.github.nextentity.core.util;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test objective: Verify Lazy class provides thread-safe lazy initialization
 * <p>
 * Test scenarios:
 * 1. Supplier is called only once on first get()
 * 2. Same instance is returned on subsequent get() calls
 * 3. Constructor throws NPE for null supplier
 * 4. Lazy initialization is thread-safe
 * <p>
 * Expected result: Supplier is called exactly once, and the result is cached
 */
class LazyTest {

    /**
     * Test objective: Verify supplier is called only once
     * Test scenario: Create Lazy with a supplier that increments a counter
     * Expected result: Supplier is called exactly once regardless of how many times get() is called
     */
    @Test
    void get_ShouldCallSupplierOnlyOnce() {
        // given
        AtomicInteger callCount = new AtomicInteger(0);
        Lazy<String> lazy = new Lazy<>(() -> {
            callCount.incrementAndGet();
            return "initialized";
        });

        // when
        lazy.get();
        lazy.get();
        lazy.get();

        // then
        assertThat(callCount.get()).isEqualTo(1);
    }

    /**
     * Test objective: Verify same instance is returned on subsequent get() calls
     * Test scenario: Create Lazy that returns new StringBuilder each time
     * Expected result: Same StringBuilder instance returned every time
     */
    @Test
    void get_ShouldReturnSameInstance() {
        // given
        Lazy<StringBuilder> lazy = new Lazy<>(StringBuilder::new);

        // when
        StringBuilder first = lazy.get();
        StringBuilder second = lazy.get();
        StringBuilder third = lazy.get();

        // then
        assertThat(first).isSameAs(second).isSameAs(third);
    }

    /**
     * Test objective: Verify constructor throws NPE for null supplier
     * Test scenario: Pass null to constructor
     * Expected result: NullPointerException is thrown
     */
    @Test
    void constructor_WithNullSupplier_ShouldThrowNPE() {
        // given
        Supplier<String> nullSupplier = null;

        // then
        assertThatThrownBy(() -> new Lazy<>(nullSupplier))
                .isInstanceOf(NullPointerException.class);
    }

    /**
     * Test objective: Verify lazy initialization works correctly
     * Test scenario: Create Lazy and verify supplier is not called until get()
     * Expected result: Supplier is not called during construction, only on get()
     */
    @Test
    void lazy_ShouldNotInitializeUntilGet() {
        // given
        AtomicReference<Boolean> initialized = new AtomicReference<>(false);
        Lazy<String> lazy = new Lazy<>(() -> {
            initialized.set(true);
            return "value";
        });

        // then - not initialized yet
        assertThat(initialized.get()).isFalse();

        // when
        lazy.get();

        // then - initialized after get()
        assertThat(initialized.get()).isTrue();
    }

    /**
     * Test objective: Verify get() returns the value from supplier
     * Test scenario: Create Lazy with a supplier that returns a specific value
     * Expected result: get() returns exactly what the supplier returns
     */
    @Test
    void get_ShouldReturnSupplierValue() {
        // given
        String expectedValue = "test-value";
        Lazy<String> lazy = new Lazy<>(() -> expectedValue);

        // when
        String result = lazy.get();

        // then
        assertThat(result).isEqualTo(expectedValue);
    }

    /**
     * Test objective: Verify Lazy can handle null values from supplier
     * Test scenario: Create Lazy with a supplier that returns null
     * Expected result: get() returns null without throwing exception
     */
    @Test
    void get_WithNullSupplierResult_ShouldReturnNull() {
        // given
        Lazy<String> lazy = new Lazy<>(() -> null);

        // when
        String result = lazy.get();

        // then
        assertThat(result).isNull();

        // and subsequent calls should still return null
        assertThat(lazy.get()).isNull();
    }

    /**
     * Test objective: Verify Lazy is thread-safe for concurrent access
     * Test scenario: Multiple threads call get() simultaneously
     * Expected result: Supplier is called exactly once, all threads get same value
     */
    @Test
    void get_WithConcurrentAccess_ShouldBeThreadSafe() throws InterruptedException {
        // given
        AtomicInteger callCount = new AtomicInteger(0);
        Lazy<String> lazy = new Lazy<>(() -> {
            callCount.incrementAndGet();
            // Simulate slow initialization
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return "initialized";
        });

        // when - multiple threads access simultaneously
        Thread[] threads = new Thread[10];
        String[] results = new String[10];
        for (int i = 0; i < threads.length; i++) {
            final int index = i;
            threads[i] = new Thread(() -> results[index] = lazy.get());
        }

        for (Thread thread : threads) {
            thread.start();
        }
        for (Thread thread : threads) {
            thread.join();
        }

        // then
        assertThat(callCount.get()).isEqualTo(1);
        for (String result : results) {
            assertThat(result).isEqualTo("initialized");
        }
    }

    /**
     * Test objective: Verify supplier can throw exception and it propagates
     * Test scenario: Create Lazy with a supplier that throws RuntimeException
     * Expected result: Exception is propagated on get()
     */
    @Test
    void get_WhenSupplierThrowsException_ShouldPropagate() {
        // given
        RuntimeException expectedException = new RuntimeException("Supplier error");
        Lazy<String> lazy = new Lazy<>(() -> {
            throw expectedException;
        });

        // then
        assertThatThrownBy(lazy::get)
                .isSameAs(expectedException);
    }
}
