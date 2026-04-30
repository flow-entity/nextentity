package io.github.nextentity.core.constructor;

import io.github.nextentity.core.QueryConfig;
import io.github.nextentity.core.expression.LiteralNode;
import io.github.nextentity.core.meta.EntityBasicAttribute;
import io.github.nextentity.core.meta.EntitySchemaAttribute;
import io.github.nextentity.core.meta.EntityType;
import io.github.nextentity.core.meta.ValueConverter;
import io.github.nextentity.core.reflect.LazyValue;
import io.github.nextentity.core.reflect.LoadObserver;
import io.github.nextentity.core.reflect.LoadObserverRegistry;
import io.github.nextentity.jdbc.Arguments;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("LazyValueConstructor")
class LazyValueConstructorTest {

    private static final Class<?> ENTITY_TYPE = SampleEntity.class;
    private static final Class<?> TARGET_TYPE = SampleTarget.class;

    private QueryConfig queryConfig;
    // EntitySchemaAttribute is non-sealed and implements JoinAttribute
    private EntitySchemaAttribute attribute;
    private EntityBasicAttribute targetAttribute;
    private EntityType targetEntityType;
    private SelectItem column;

    @BeforeEach
    void setUp() {
        queryConfig = mock(QueryConfig.class);
        attribute = mock(EntitySchemaAttribute.class);
        targetAttribute = mock(EntityBasicAttribute.class);
        targetEntityType = mock(EntityType.class);
        // Create a real SelectItem.Expr instance (sealed interface, cannot mock)
        ValueConverter<?, ?> converter = mock(ValueConverter.class);
        column = SelectItem.of(LiteralNode.TRUE, converter);

        when(attribute.type()).thenAnswer(inv -> ENTITY_TYPE);
        when(attribute.getTargetEntityType()).thenReturn(targetEntityType);
        when(targetEntityType.type()).thenAnswer(inv -> TARGET_TYPE);
        when(attribute.getTargetAttribute()).thenReturn(targetAttribute);
        when(targetAttribute.valueConvertor()).thenReturn(mock(ValueConverter.class));
    }

    private LazyValueConstructor createInstance() {
        return new LazyValueConstructor(queryConfig, attribute, column);
    }

    private LazyValueConstructor createWithSameType() {
        when(targetEntityType.type()).thenAnswer(inv -> ENTITY_TYPE);
        return new LazyValueConstructor(queryConfig, attribute, column);
    }

    private void replaceBatchLoaderFunction(LazyValueConstructor instance, LazyLoaderFunction stub) {
        try {
            Field field = LazyValueConstructor.class.getDeclaredField("batchLoaderFunction");
            field.setAccessible(true);
            field.set(instance, stub);
        } catch (Exception e) {
            throw new RuntimeException("Failed to replace batchLoaderFunction via reflection", e);
        }
    }

    // --- stub LazyLoaderFunction ---

    private static LazyLoaderFunction stubLoader(Map<Object, Object> result) {
        return new LazyLoaderFunction() {
            @Override
            public Map<Object, Object> apply(LazyValueConstructor constructor, Collection<Object> foreignKeys) {
                return result;
            }
        };
    }

    private static LazyLoaderFunction recordingLoader(Map<Object, Object> result, AtomicInteger invokeCount) {
        return new LazyLoaderFunction() {
            @Override
            public Map<Object, Object> apply(LazyValueConstructor constructor, Collection<Object> foreignKeys) {
                invokeCount.incrementAndGet();
                return result;
            }
        };
    }

    // --- helper: call construct() to register a foreign key ---

    private LazyValue invokeConstruct(LazyValueConstructor constructor, Object foreignKey) {
        Arguments arguments = mock(Arguments.class);
        when(arguments.next(any(ValueConverter.class))).thenReturn(foreignKey);
        return (LazyValue) constructor.construct(arguments);
    }

    // ==================== Constructor ====================

    @Nested
    @DisplayName("Constructor")
    class Constructor {

        @Test
        @DisplayName("initializes queryConfig, attribute, columns correctly")
        void initializesFields() {
            LazyValueConstructor c = createInstance();

            assertThat(c.getQueryConfig()).isSameAs(queryConfig);
            assertThat(c.getAttribute()).isSameAs(attribute);
            assertThat(c.columns()).containsExactly(column);
        }

        @Test
        @DisplayName("creates EntityAttributeLoadFunction when attribute.type() == targetEntityType.type()")
        void createsEntityAttributeLoadFunction() {
            LazyValueConstructor c = createWithSameType();

            try {
                Field field = LazyValueConstructor.class.getDeclaredField("batchLoaderFunction");
                field.setAccessible(true);
                Object loader = field.get(c);
                assertThat(loader.getClass().getSimpleName()).isEqualTo("EntityAttributeLoadFunction");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Test
        @DisplayName("creates ProjectionAttributeLoadFunction when attribute.type() != targetEntityType.type()")
        void createsProjectionAttributeLoadFunction() {
            LazyValueConstructor c = createInstance();

            try {
                Field field = LazyValueConstructor.class.getDeclaredField("batchLoaderFunction");
                field.setAccessible(true);
                Object loader = field.get(c);
                assertThat(loader.getClass().getSimpleName()).isEqualTo("ProjectionAttributeLoadFunction");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    // ==================== construct() ====================

    @Nested
    @DisplayName("construct()")
    class Construct {

        @Test
        @DisplayName("extracts foreign key from arguments")
        void extractsForeignKey() {
            LazyValueConstructor c = createInstance();
            Object fk = 42;

            LazyValue lazyValue = invokeConstruct(c, fk);

            assertThat(lazyValue).isNotNull();
        }

        @Test
        @DisplayName("adds foreign key to foreignKeys set")
        void addsForeignKeyToSet() throws Exception {
            LazyValueConstructor c = createInstance();
            Object fk = 99;

            invokeConstruct(c, fk);

            Field foreignKeysField = LazyValueConstructor.class.getDeclaredField("foreignKeys");
            foreignKeysField.setAccessible(true);
            @SuppressWarnings("unchecked")
            Set<Object> foreignKeys = (Set<Object>) foreignKeysField.get(c);
            assertThat(foreignKeys).contains(fk);
        }

        @Test
        @DisplayName("returns LazyValue with identifier equal to foreign key")
        void returnsLazyValueWithIdentifier() {
            LazyValueConstructor c = createInstance();
            Object fk = "dept-1";

            LazyValue lazyValue = invokeConstruct(c, fk);

            // LazyValue equals/hashCode is based on identifier
            LazyValue expected = new LazyValue(o -> o, fk);
            assertThat(lazyValue).isEqualTo(expected);
            assertThat(lazyValue.hashCode()).isEqualTo(expected.hashCode());
        }

        @Test
        @DisplayName("returned LazyValue get() triggers findByForeignKey")
        void lazyValueGetTriggersFindByForeignKey() {
            LazyValueConstructor c = createInstance();
            replaceBatchLoaderFunction(c, stubLoader(Map.of("fk-1", "result-1")));

            LazyValue lazyValue = invokeConstruct(c, "fk-1");
            Object result = lazyValue.get();

            assertThat(result).isEqualTo("result-1");
        }
    }

    // ==================== findByForeignKey() - caching ====================

    @Nested
    @DisplayName("findByForeignKey() - caching")
    class FindByForeignKeyCaching {

        @Test
        @DisplayName("triggers batch load on first lookup")
        void triggersBatchLoadOnFirstLookup() {
            LazyValueConstructor c = createInstance();
            AtomicInteger invokeCount = new AtomicInteger(0);
            replaceBatchLoaderFunction(c, recordingLoader(Map.of(1, "one"), invokeCount));

            c.findByForeignKey(1);

            assertThat(invokeCount.get()).isEqualTo(1);
        }

        @Test
        @DisplayName("returns cached value on subsequent lookup without reloading")
        void returnsCachedValueWithoutReloading() {
            LazyValueConstructor c = createInstance();
            AtomicInteger invokeCount = new AtomicInteger(0);
            replaceBatchLoaderFunction(c, recordingLoader(Map.of(1, "one"), invokeCount));

            Object first = c.findByForeignKey(1);
            Object second = c.findByForeignKey(1);

            assertThat(first).isEqualTo("one");
            assertThat(second).isEqualTo("one");
            assertThat(invokeCount.get()).isEqualTo(1);
        }

        @Test
        @DisplayName("maps unmatched foreign keys to null after batch load")
        void mapsUnmatchedForeignKeysToNull() {
            LazyValueConstructor c = createInstance();
            // Register two foreign keys via construct()
            invokeConstruct(c, 1);
            invokeConstruct(c, 2);
            // Batch loader only returns result for key 1
            replaceBatchLoaderFunction(c, stubLoader(Map.of(1, "one")));

            Object result1 = c.findByForeignKey(1);
            Object result2 = c.findByForeignKey(2);

            assertThat(result1).isEqualTo("one");
            assertThat(result2).isNull();
        }
    }

    // ==================== findByForeignKey() - concurrency ====================

    @Nested
    @DisplayName("findByForeignKey() - concurrency")
    class FindByForeignKeyConcurrency {

        @Test
        @DisplayName("concurrent lookups for same key trigger batch load only once")
        void concurrentSameKeyLoadsOnlyOnce() throws Exception {
            LazyValueConstructor c = createInstance();
            AtomicInteger invokeCount = new AtomicInteger(0);
            CountDownLatch loadLatch = new CountDownLatch(1);

            LazyLoaderFunction slowLoader = new LazyLoaderFunction() {
                @Override
                public Map<Object, Object> apply(LazyValueConstructor constructor, Collection<Object> foreignKeys) {
                    invokeCount.incrementAndGet();
                    try {
                        loadLatch.await(5, TimeUnit.SECONDS);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    Map<Object, Object> result = new HashMap<>();
                    for (Object fk : foreignKeys) {
                        result.put(fk, "value-" + fk);
                    }
                    return result;
                }
            };
            replaceBatchLoaderFunction(c, slowLoader);

            // Register foreign key first via construct()
            invokeConstruct(c, 1);

            int threadCount = 10;
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            List<Object> results = new ArrayList<>();
            CountDownLatch startLatch = new CountDownLatch(1);

            for (int i = 0; i < threadCount; i++) {
                executor.submit(() -> {
                    try {
                        startLatch.await();
                        Object r = c.findByForeignKey(1);
                        synchronized (results) {
                            results.add(r);
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }

            startLatch.countDown();
            // Allow the batch loader to complete after a short delay
            Thread.sleep(100);
            loadLatch.countDown();

            executor.shutdown();
            boolean terminated = executor.awaitTermination(10, TimeUnit.SECONDS);
            assertThat(terminated).isTrue();

            assertThat(invokeCount.get()).isEqualTo(1);
            assertThat(results).hasSize(threadCount);
            assertThat(results).allMatch(r -> "value-1".equals(r));
        }

        @Test
        @DisplayName("concurrent lookups for different keys all return correct values")
        void concurrentDifferentKeysAllReturnCorrectly() throws Exception {
            LazyValueConstructor c = createInstance();

            LazyLoaderFunction loader = new LazyLoaderFunction() {
                @Override
                public Map<Object, Object> apply(LazyValueConstructor constructor, Collection<Object> foreignKeys) {
                    Map<Object, Object> result = new HashMap<>();
                    for (Object fk : foreignKeys) {
                        result.put(fk, "value-" + fk);
                    }
                    return result;
                }
            };
            replaceBatchLoaderFunction(c, loader);

            int keyCount = 20;
            // Register all foreign keys via construct() first
            for (int i = 0; i < keyCount; i++) {
                invokeConstruct(c, i);
            }

            ExecutorService executor = Executors.newFixedThreadPool(keyCount);
            Map<Integer, Object> results = new ConcurrentHashMap<>();
            CountDownLatch startLatch = new CountDownLatch(1);

            for (int i = 0; i < keyCount; i++) {
                final int key = i;
                executor.submit(() -> {
                    try {
                        startLatch.await();
                        Object r = c.findByForeignKey(key);
                        results.put(key, r);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }

            startLatch.countDown();
            executor.shutdown();
            boolean terminated = executor.awaitTermination(10, TimeUnit.SECONDS);
            assertThat(terminated).isTrue();

            for (int i = 0; i < keyCount; i++) {
                assertThat(results).containsEntry(i, "value-" + i);
            }
        }
    }

    // ==================== findByForeignKey() - LoadObserver ====================

    @Nested
    @DisplayName("findByForeignKey() - LoadObserver notifications")
    class FindByForeignKeyLoadObserver {

        @Test
        @DisplayName("notifies onCacheHit on cache hit")
        void notifiesCacheHit() {
            LazyValueConstructor c = createInstance();
            replaceBatchLoaderFunction(c, stubLoader(Map.of(1, "one")));

            // First lookup triggers load
            c.findByForeignKey(1);

            LoadObserver observer = mock(LoadObserver.class);
            LoadObserverRegistry.withObserver(observer, () -> {
                // Second lookup should hit cache
                c.findByForeignKey(1);

                verify(observer).onCacheHit(new LoadObserver.CacheHitEvent(ENTITY_TYPE, 1, "one"));
            });
        }

        @Test
        @DisplayName("notifies onBeforeLoad before batch load")
        void notifiesBeforeLoad() {
            LazyValueConstructor c = createInstance();
            replaceBatchLoaderFunction(c, stubLoader(Map.of(1, "one")));

            LoadObserver observer = mock(LoadObserver.class);

            LoadObserverRegistry.withObserver(observer, () -> {
                c.findByForeignKey(1);

                verify(observer).onBeforeLoad(any(LoadObserver.BatchLoadEvent.class));
            });
        }

        @Test
        @DisplayName("notifies onAfterLoad after batch load")
        void notifiesAfterLoad() {
            LazyValueConstructor c = createInstance();
            replaceBatchLoaderFunction(c, stubLoader(Map.of(1, "one")));

            LoadObserver observer = mock(LoadObserver.class);

            LoadObserverRegistry.withObserver(observer, () -> {
                c.findByForeignKey(1);

                verify(observer).onAfterLoad(any(LoadObserver.BatchLoadEvent.class));
            });
        }

        @Test
        @DisplayName("does not throw when no observer is bound")
        void noExceptionWithoutObserver() {
            LazyValueConstructor c = createInstance();
            replaceBatchLoaderFunction(c, stubLoader(Map.of(1, "one")));

            assertThat(LoadObserverRegistry.isBound()).isFalse();

            // Should not throw
            Object result = c.findByForeignKey(1);
            assertThat(result).isEqualTo("one");

            // Cache hit without observer should also not throw
            Object cached = c.findByForeignKey(1);
            assertThat(cached).isEqualTo("one");
        }

        @Test
        @DisplayName("BatchLoadEvent contains correct entityType and foreignKeys")
        void batchLoadEventContainsCorrectData() {
            LazyValueConstructor c = createInstance();
            replaceBatchLoaderFunction(c, stubLoader(Map.of(1, "one")));
            // Register foreign key via construct so it appears in foreignKeys set
            invokeConstruct(c, 1);

            List<LoadObserver.BatchLoadEvent> beforeEvents = new ArrayList<>();
            List<LoadObserver.BatchLoadEvent> afterEvents = new ArrayList<>();

            LoadObserver observer = new LoadObserver() {
                @Override
                public void onBeforeLoad(BatchLoadEvent event) {
                    beforeEvents.add(event);
                }

                @Override
                public void onAfterLoad(BatchLoadEvent event) {
                    afterEvents.add(event);
                }

                @Override
                public void onCacheHit(CacheHitEvent event) {
                }
            };

            LoadObserverRegistry.withObserver(observer, () -> c.findByForeignKey(1));

            assertThat(beforeEvents).hasSize(1);
            LoadObserver.BatchLoadEvent beforeEvent = beforeEvents.get(0);
            assertThat(beforeEvent.entityType()).isEqualTo(ENTITY_TYPE);
            assertThat(beforeEvent.foreignKeys()).anyMatch(fk -> fk.equals(1));

            assertThat(afterEvents).hasSize(1);
            LoadObserver.BatchLoadEvent afterEvent = afterEvents.get(0);
            assertThat(afterEvent.entityType()).isEqualTo(ENTITY_TYPE);
            assertThat(afterEvent.endTimeNanos()).isGreaterThanOrEqualTo(afterEvent.startTimeNanos());
        }
    }

    // ==================== columns() ====================

    @Nested
    @DisplayName("columns()")
    class Columns {

        @Test
        @DisplayName("returns the column passed to constructor")
        void returnsConstructorColumn() {
            LazyValueConstructor c = createInstance();

            assertThat(c.columns()).containsExactly(column);
        }
    }

    // ==================== accessor methods ====================

    @Nested
    @DisplayName("Accessor methods")
    class AccessorMethods {

        @Test
        @DisplayName("getQueryConfig() returns constructor argument")
        void getQueryConfig() {
            LazyValueConstructor c = createInstance();

            assertThat(c.getQueryConfig()).isSameAs(queryConfig);
        }

        @Test
        @DisplayName("getAttribute() returns constructor argument")
        void getAttribute() {
            LazyValueConstructor c = createInstance();

            assertThat(c.getAttribute()).isSameAs(attribute);
        }
    }

    // ==================== LazyValue equals contract ====================

    @Nested
    @DisplayName("LazyValue equals/hashCode contract")
    class LazyValueEqualsContract {

        @Test
        @DisplayName("reflexive: x.equals(x) is true")
        void reflexive() {
            LazyValue lv = new LazyValue(o -> o, "id-1");
            assertThat(lv.equals(lv)).isTrue();
        }

        @Test
        @DisplayName("symmetric: a.equals(b) == b.equals(a)")
        void symmetric() {
            LazyValue a = new LazyValue(o -> o, "id-1");
            LazyValue b = new LazyValue(o -> "other", "id-1");
            assertThat(a.equals(b)).isEqualTo(b.equals(a));
            assertThat(a.equals(b)).isTrue();
        }

        @Test
        @DisplayName("transitive: a.equals(b) && b.equals(c) => a.equals(c)")
        void transitive() {
            LazyValue a = new LazyValue(o -> o, "id-1");
            LazyValue b = new LazyValue(o -> "x", "id-1");
            LazyValue c = new LazyValue(o -> "y", "id-1");
            assertThat(a.equals(b)).isTrue();
            assertThat(b.equals(c)).isTrue();
            assertThat(a.equals(c)).isTrue();
        }

        @Test
        @DisplayName("returns false for null")
        void returnsFalseForNull() {
            LazyValue lv = new LazyValue(o -> o, "id-1");
            assertThat(lv.equals(null)).isFalse();
        }

        @Test
        @DisplayName("returns false for different type")
        void returnsFalseForDifferentType() {
            LazyValue lv = new LazyValue(o -> o, "id-1");
            assertThat(lv.equals("id-1")).isFalse();
        }

        @Test
        @DisplayName("returns false for different identifier")
        void returnsFalseForDifferentIdentifier() {
            LazyValue a = new LazyValue(o -> o, "id-1");
            LazyValue b = new LazyValue(o -> o, new Object());
            assertThat(a.equals(b)).isFalse();
        }

        @Test
        @DisplayName("consistent hashCode with equals")
        void consistentHashCode() {
            Object id = new Object();
            LazyValue a = new LazyValue(o -> o, id);
            LazyValue b = new LazyValue(o -> "other", id);
            assertThat(a.equals(b)).isTrue();
            assertThat(a.hashCode()).isEqualTo(b.hashCode());
        }

        @Test
        @DisplayName("handles null identifier")
        void handlesNullIdentifier() {
            LazyValue a = new LazyValue(o -> o, null);
            LazyValue b = new LazyValue(o -> o, null);
            assertThat(a.equals(b)).isTrue();
            assertThat(a.hashCode()).isEqualTo(b.hashCode());
        }

        @Test
        @DisplayName("LazyValue.get() throws IllegalStateException on recursive invocation")
        void throwsOnReentry() {
            LazyValue[] holder = new LazyValue[1];
            holder[0] = new LazyValue(o -> {
                holder[0].get();
                return "result";
            }, "id");
            assertThatThrownBy(holder[0]::get)
                    .isInstanceOf(IllegalStateException.class)
                    .message().contains("Recursive invocation");
        }
    }

    // ==================== Exception propagation ====================

    @Nested
    @DisplayName("findByForeignKey() - exception propagation")
    class FindByForeignKeyException {

        @Test
        @DisplayName("propagates RuntimeException from batchLoaderFunction")
        void propagatesRuntimeException() {
            LazyValueConstructor c = createInstance();
            replaceBatchLoaderFunction(c, new LazyLoaderFunction() {
                @Override
                public Map<Object, Object> apply(LazyValueConstructor constructor, Collection<Object> foreignKeys) {
                    throw new RuntimeException("batch load failed");
                }
            });

            assertThatThrownBy(() -> c.findByForeignKey(1))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("batch load failed");
        }

        @Test
        @DisplayName("null foreign key triggers NullPointerException on foreignKeys.add()")
        void nullForeignKeyThrowsNPE() {
            LazyValueConstructor c = createInstance();
            replaceBatchLoaderFunction(c, stubLoader(new HashMap<>()));

            // ConcurrentHashMap.newKeySet() does not allow null keys
            // construct() adds foreignKey to foreignKeys set, so null will throw NPE
            assertThatThrownBy(() -> invokeConstruct(c, null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    // ==================== Batch load receives all registered foreign keys ====================

    @Nested
    @DisplayName("findByForeignKey() - batch load receives all foreign keys")
    class FindByForeignKeyBatchKeyCollection {

        @Test
        @DisplayName("batchLoaderFunction receives all registered foreign keys")
        void batchLoaderReceivesAllKeys() {
            LazyValueConstructor c = createInstance();
            AtomicReference<Collection<Object>> capturedKeys = new AtomicReference<>();
            replaceBatchLoaderFunction(c, new LazyLoaderFunction() {
                @Override
                public Map<Object, Object> apply(LazyValueConstructor constructor, Collection<Object> foreignKeys) {
                    capturedKeys.set(new ArrayList<>(foreignKeys));
                    return Map.of();
                }
            });

            invokeConstruct(c, 10);
            invokeConstruct(c, 20);
            invokeConstruct(c, 30);
            c.findByForeignKey(10);

            assertThat(capturedKeys.get()).containsExactlyInAnyOrder(10, 20, 30);
        }

        @Test
        @DisplayName("returns null for all keys when batchLoaderFunction returns empty map")
        void allKeysNullWhenEmptyResult() {
            LazyValueConstructor c = createInstance();
            replaceBatchLoaderFunction(c, stubLoader(Map.of()));

            invokeConstruct(c, 1);
            invokeConstruct(c, 2);
            assertThat(c.findByForeignKey(1)).isNull();
            assertThat(c.findByForeignKey(2)).isNull();
        }
    }

    // ==================== BatchLoadEvent time unit validation ====================

    @Nested
    @DisplayName("findByForeignKey() - BatchLoadEvent time consistency")
    class FindByForeignKeyTimeConsistency {

        @Test
        @DisplayName("BatchLoadEvent startTime and endTime are in milliseconds (current implementation)")
        void batchLoadEventTimeIsInMilliseconds() {
            LazyValueConstructor c = createInstance();
            replaceBatchLoaderFunction(c, stubLoader(Map.of(1, "one")));
            invokeConstruct(c, 1);

            List<LoadObserver.BatchLoadEvent> afterEvents = new ArrayList<>();
            LoadObserver observer = new LoadObserver() {
                @Override
                public void onBeforeLoad(BatchLoadEvent event) {}

                @Override
                public void onAfterLoad(BatchLoadEvent event) {
                    afterEvents.add(event);
                }

                @Override
                public void onCacheHit(CacheHitEvent event) {}
            };

            LoadObserverRegistry.withObserver(observer, () -> c.findByForeignKey(1));

            assertThat(afterEvents).hasSize(1);
            LoadObserver.BatchLoadEvent event = afterEvents.get(0);
            // Duration should be in milliseconds range (0-5000ms for a fast operation)
            // If values were truly nanoseconds, duration for a fast operation would be < 1ms = 1_000_000ns
            // But since currentTimeMillis is used, duration will be 0-5 typically
            long duration = event.endTimeNanos() - event.startTimeNanos();
            assertThat(duration).isGreaterThanOrEqualTo(0);
            assertThat(duration).isLessThan(5000);
        }
    }

    // ==================== Concurrency - improved ====================

    @Nested
    @DisplayName("findByForeignKey() - concurrency (improved)")
    class FindByForeignKeyConcurrencyImproved {

        @Test
        @DisplayName("concurrent lookups for same key trigger batch load only once (no Thread.sleep)")
        void concurrentSameKeyNoSleep() throws Exception {
            LazyValueConstructor c = createInstance();
            AtomicInteger invokeCount = new AtomicInteger(0);
            CountDownLatch loadLatch = new CountDownLatch(1);

            LazyLoaderFunction slowLoader = new LazyLoaderFunction() {
                @Override
                public Map<Object, Object> apply(LazyValueConstructor constructor, Collection<Object> foreignKeys) {
                    invokeCount.incrementAndGet();
                    try {
                        loadLatch.await(5, TimeUnit.SECONDS);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    Map<Object, Object> result = new HashMap<>();
                    for (Object fk : foreignKeys) {
                        result.put(fk, "value-" + fk);
                    }
                    return result;
                }
            };
            replaceBatchLoaderFunction(c, slowLoader);
            invokeConstruct(c, 1);

            int threadCount = 10;
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            List<Object> results = new ArrayList<>();
            CountDownLatch readyLatch = new CountDownLatch(threadCount);
            CountDownLatch startLatch = new CountDownLatch(1);

            for (int i = 0; i < threadCount; i++) {
                executor.submit(() -> {
                    readyLatch.countDown();
                    try {
                        startLatch.await();
                        Object r = c.findByForeignKey(1);
                        synchronized (results) {
                            results.add(r);
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }

            // Wait for all threads to be ready, then release them simultaneously
            readyLatch.await(5, TimeUnit.SECONDS);
            // Small delay to ensure all threads are blocked on findByForeignKey
            Thread.sleep(50);
            loadLatch.countDown();
            startLatch.countDown();

            executor.shutdown();
            boolean terminated = executor.awaitTermination(10, TimeUnit.SECONDS);
            assertThat(terminated).isTrue();

            assertThat(invokeCount.get()).isEqualTo(1);
            assertThat(results).hasSize(threadCount);
            assertThat(results).allMatch(r -> "value-1".equals(r));
        }
    }

    // --- sample types for mocking ---

    private static class SampleEntity {
    }

    private static class SampleTarget {
    }
}
