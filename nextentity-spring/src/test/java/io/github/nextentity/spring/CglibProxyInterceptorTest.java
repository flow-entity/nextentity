package io.github.nextentity.spring;

import io.github.nextentity.core.constructor.PropertyBinding;
import io.github.nextentity.core.constructor.QueryContext;
import io.github.nextentity.core.constructor.ValueConstructor;
import io.github.nextentity.core.expression.SelectEntity;
import io.github.nextentity.core.expression.SelectExpression;
import io.github.nextentity.core.expression.SelectProjection;
import io.github.nextentity.core.meta.EntityType;
import io.github.nextentity.core.meta.MetamodelAttribute;
import io.github.nextentity.core.meta.ProjectionSchema;
import io.github.nextentity.core.reflect.LazyValue;
import io.github.nextentity.spring.CglibProxyInterceptor.CglibProxyConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 验证 {@link CglibProxyInterceptor} 的行为。
 * <p>
 * <strong>背景：</strong>代码评审曾指出 {@code MethodProxy.invoke(target(), args)} 可能无效，
 * 因为 {@code target} 是父类实例而非 CGLIB 增强代理实例。本测试直接验证该模式是正确且有效的。
 * </p>
 *
 * @see CglibProxyInterceptor
 */
@DisplayName("CglibProxyInterceptor")
class CglibProxyInterceptorTest {

    // ==================== 测试用 POJO ====================

    /**
     * 普通 POJO — 可被 CGLIB 代理
     */
    public static class SimplePojo {
        private Long id;
        private String name;
        private String lazyValue;

        public SimplePojo() {
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLazyValue() {
            return lazyValue;
        }

        public void setLazyValue(String lazyValue) {
            this.lazyValue = lazyValue;
        }
    }

    /**
     * final 类 — 不可被 CGLIB 代理
     */
    public static final class FinalClass {
        public Long getId() {
            return 1L;
        }
    }

    /**
     * 没有默认构造函数的类
     */
    @SuppressWarnings("FieldCanBeLocal")
    public static class NoDefaultCtor {
        private final Long id;

        public NoDefaultCtor(Long id) {
            this.id = id;
        }
    }

    /**
     * 记录类型 — 不可被 CGLIB 代理
     */
    public record SampleRecord(Long id, String name) {
    }

    /**
     * 多懒加载属性的 POJO
     */
    public static class MultiLazyPojo {
        private Long id;
        private String lazy1;
        private String lazy2;

        public MultiLazyPojo() {
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getLazy1() {
            return lazy1;
        }

        public void setLazy1(String v) {
            lazy1 = v;
        }

        public String getLazy2() {
            return lazy2;
        }

        public void setLazy2(String v) {
            lazy2 = v;
        }
    }

    // ==================== Constructor / Name / Order ====================

    @Nested
    @DisplayName("Constructor, name() and order()")
    class ConstructorAndIdentity {

        @Test
        @DisplayName("default constructor creates instance with order 0")
        void defaultConstructorOrderIsZero() {
            assertThat(new CglibProxyInterceptor().order()).isEqualTo(0);
        }

        @Test
        @DisplayName("constructor with order creates instance with specified order")
        void customOrderConstructor() {
            assertThat(new CglibProxyInterceptor(10).order()).isEqualTo(10);
        }

        @Test
        @DisplayName("name() returns 'cglib-proxy'")
        void nameReturnsCglibProxy() {
            assertThat(new CglibProxyInterceptor().name()).isEqualTo("cglib-proxy");
        }
    }

    // ==================== supports() ====================

    @Nested
    @DisplayName("supports()")
    class SupportsTest {

        private final CglibProxyInterceptor interceptor = new CglibProxyInterceptor();

        @Test
        @DisplayName("returns true for non-final class with lazy attributes")
        void returnsTrueForNonFinalClassWithLazy() {
            assertThat(interceptor.supports(
                    mockContext(SimplePojo.class, true),
                    new SelectProjection(SimplePojo.class, false)
            )).isTrue();
        }

        @Test
        @DisplayName("returns false for interface type")
        void returnsFalseForInterface() {
            assertThat(interceptor.supports(
                    mockContext(Runnable.class, true),
                    new SelectProjection(Runnable.class, false)
            )).isFalse();
        }

        @Test
        @DisplayName("returns false for record type")
        void returnsFalseForRecord() {
            assertThat(interceptor.supports(
                    mockContext(SampleRecord.class, true),
                    new SelectProjection(SampleRecord.class, false)
            )).isFalse();
        }

        @Test
        @DisplayName("returns false for final class")
        void returnsFalseForFinalClass() {
            assertThat(interceptor.supports(
                    mockContext(FinalClass.class, true),
                    new SelectProjection(FinalClass.class, false)
            )).isFalse();
        }

        @Test
        @DisplayName("returns false for class without default constructor")
        void returnsFalseForNoDefaultCtor() {
            assertThat(interceptor.supports(
                    mockContext(NoDefaultCtor.class, true),
                    new SelectProjection(NoDefaultCtor.class, false)
            )).isFalse();
        }

        @Test
        @DisplayName("returns false when lazy loading is disabled")
        void returnsFalseWhenLazyDisabled() {
            QueryContext ctx = mockContext(SimplePojo.class, true);
            when(ctx.isEnableLazyLoading()).thenReturn(false);

            assertThat(interceptor.supports(
                    ctx,
                    new SelectProjection(SimplePojo.class, false)
            )).isFalse();
        }

        @Test
        @DisplayName("returns false for class without lazy attributes")
        void returnsFalseWithoutLazy() {
            assertThat(interceptor.supports(
                    mockContext(SimplePojo.class, false),
                    new SelectProjection(SimplePojo.class, false)
            )).isFalse();
        }

        @Test
        @DisplayName("returns false for SelectEntity")
        void returnsFalseForSelectEntity() {
            assertThat(interceptor.supports(
                    mockContext(SimplePojo.class, true),
                    new SelectEntity(io.github.nextentity.core.util.ImmutableList.of(), false)
            )).isFalse();
        }

        @Test
        @DisplayName("returns false for SelectExpression")
        void returnsFalseForSelectExpression() {
            assertThat(interceptor.supports(
                    mockContext(SimplePojo.class, true),
                    mock(SelectExpression.class)
            )).isFalse();
        }

        private QueryContext mockContext(Class<?> type, boolean hasLazy) {
            QueryContext ctx = mock(QueryContext.class);
            when(ctx.isEnableLazyLoading()).thenReturn(true);
            EntityType entityType = mock(EntityType.class);
            ProjectionSchema projection = mock(ProjectionSchema.class);
            when(ctx.getEntityType()).thenReturn(entityType);
            when(entityType.getProjection(type)).thenReturn(projection);
            when(projection.hasLazyAttribute()).thenReturn(hasLazy);
            doReturn(type).when(projection).type();
            return ctx;
        }
    }

    // ==================== CglibProxyConstructor ====================

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Nested
    @DisplayName("CglibProxyConstructor")
    class CglibProxyConstructorTest {

        @Test
        @DisplayName("construct creates CGLIB proxy instance")
        void constructCreatesCglibProxy() {
            var constructor = new CglibProxyConstructor(SimplePojo.class, List.of(), true);
            Object result = constructor.construct(null);

            assertThat(result).isNotNull();
            assertThat(result.getClass().getName()).contains("$$EnhancerByCGLIB$$");
            assertThat(result).isInstanceOf(SimplePojo.class);
        }

        @Test
        @DisplayName("construct returns null for non-root with empty properties")
        void constructReturnsNullForNonRootEmpty() {
            var constructor = new CglibProxyConstructor(SimplePojo.class, List.of(), false);
            Object result = constructor.construct(null);

            assertThat(result).isNull();
        }

        @Test
        @DisplayName("constructor stores result type correctly")
        void getResultType() {
            var constructor = new CglibProxyConstructor(SimplePojo.class, List.of(), true);

            assertThat(constructor.getResultType()).isEqualTo(SimplePojo.class);
        }

        @Test
        @DisplayName("non-lazy property value is accessible via proxy delegation")
        void nonLazyPropertyIsAccessibleViaProxy() {
            PropertyBinding binding = new PropertyBinding(
                    createAttribute(SimplePojo.class, "id"),
                    valueReturning(42L)
            );
            var constructor = new CglibProxyConstructor(SimplePojo.class, List.of(binding), true);
            SimplePojo proxy = (SimplePojo) constructor.construct(null);

            assertThat(proxy.getId()).isEqualTo(42L);
        }

        @Test
        @DisplayName("lazy property resolves on first getter call")
        void lazyPropertyResolvesOnFirstAccess() {
            PropertyBinding binding = new PropertyBinding(
                    createAttribute(SimplePojo.class, "lazyValue"),
                    valueReturning(new LazyValue(_ -> "resolved-data", "k"))
            );
            var constructor = new CglibProxyConstructor(SimplePojo.class, List.of(binding), true);
            SimplePojo proxy = (SimplePojo) constructor.construct(null);

            assertThat(proxy.getLazyValue()).isEqualTo("resolved-data");
        }

        @Test
        @DisplayName("lazy property resolves only once")
        void lazyPropertyResolvesOnce() {
            AtomicInteger count = new AtomicInteger(0);
            PropertyBinding binding = new PropertyBinding(
                    createAttribute(SimplePojo.class, "lazyValue"),
                    valueReturning(new LazyValue(_ -> {
                        count.incrementAndGet();
                        return "v" + count.get();
                    }, "k"))
            );
            var constructor = new CglibProxyConstructor(SimplePojo.class, List.of(binding), true);
            SimplePojo proxy = (SimplePojo) constructor.construct(null);

            assertThat(proxy.getLazyValue()).isEqualTo("v1");
            assertThat(proxy.getLazyValue()).isEqualTo("v1");
            assertThat(count.get()).isEqualTo(1);
        }

        @Test
        @DisplayName("setter cancels lazy loading for that property")
        void setterTriggersThenPreventsLazyLoading() {
            PropertyBinding binding = new PropertyBinding(
                    createAttribute(SimplePojo.class, "lazyValue"),
                    valueReturning(new LazyValue(_ -> "lazy loading should not be triggered after setter", "k"))
            );
            var constructor = new CglibProxyConstructor(SimplePojo.class, List.of(binding), true);
            SimplePojo proxy = (SimplePojo) constructor.construct(null);

            proxy.setLazyValue("explicit-value");

            assertThat(proxy.getLazyValue()).isEqualTo("explicit-value");
        }

        @Test
        @DisplayName("multiple lazy properties resolve independently")
        void multipleLazyProperties() {
            PropertyBinding lazy1 = new PropertyBinding(
                    createAttribute(MultiLazyPojo.class, "lazy1"),
                    valueReturning(new LazyValue(_ -> "lazy1-data", "k1"))
            );
            PropertyBinding lazy2 = new PropertyBinding(
                    createAttribute(MultiLazyPojo.class, "lazy2"),
                    valueReturning(new LazyValue(_ -> "lazy2-data", "k2"))
            );
            var constructor = new CglibProxyConstructor(MultiLazyPojo.class, List.of(lazy1, lazy2), true);
            MultiLazyPojo proxy = (MultiLazyPojo) constructor.construct(null);

            assertThat(proxy.getLazy1()).isEqualTo("lazy1-data");
            assertThat(proxy.getLazy2()).isEqualTo("lazy2-data");
        }

        @Test
        @DisplayName("setter on one lazy property does not affect other lazy properties")
        void setterCancelsOnlySpecificProperty() {
            PropertyBinding lazy1 = new PropertyBinding(
                    createAttribute(MultiLazyPojo.class, "lazy1"),
                    valueReturning(new LazyValue(_ -> "lazy1-data", "k1"))
            );
            PropertyBinding lazy2 = new PropertyBinding(
                    createAttribute(MultiLazyPojo.class, "lazy2"),
                    valueReturning(new LazyValue(_ -> "lazy2 should be cancelled by setter", "k2"))
            );
            var constructor = new CglibProxyConstructor(MultiLazyPojo.class, List.of(lazy1, lazy2), true);
            MultiLazyPojo proxy = (MultiLazyPojo) constructor.construct(null);

            proxy.setLazy2("explicit-value");

            assertThat(proxy.getLazy1()).isEqualTo("lazy1-data");
            assertThat(proxy.getLazy2()).isEqualTo("explicit-value");
        }

        @Test
        @DisplayName("setter cancels lazy loading when setting null")
        void setterTriggersThenPreventsLazyLoadingWithNull() {
            AtomicInteger loadCount = new AtomicInteger(0);
            PropertyBinding lazy1 = new PropertyBinding(
                    createAttribute(MultiLazyPojo.class, "lazy1"),
                    valueReturning(new LazyValue(_ -> {
                        loadCount.incrementAndGet();
                        return "lazy1-data";
                    }, "k1"))
            );
            PropertyBinding lazy2 = new PropertyBinding(
                    createAttribute(MultiLazyPojo.class, "lazy2"),
                    valueReturning(new LazyValue(_ -> "should-not-load", "k2"))
            );
            var constructor = new CglibProxyConstructor(MultiLazyPojo.class, List.of(lazy1, lazy2), true);
            MultiLazyPojo proxy = (MultiLazyPojo) constructor.construct(null);

            proxy.setLazy2(null);

            assertThat(proxy.getLazy1()).isEqualTo("lazy1-data");
            assertThat(proxy.getLazy2()).isNull();
            assertThat(loadCount.get()).isEqualTo(1);
        }

        @Test
        @DisplayName("read-only property (no setter) lazy loads normally")
        void readOnlyPropertyLazyLoadsNormally() {
            MetamodelAttribute attr = mock(MetamodelAttribute.class);
            Method getter = getMethod(SimplePojo.class, "getName");
            when(attr.getter()).thenReturn(getter);
            when(attr.setter()).thenReturn(null);
            doAnswer(invocation -> {
                Object instance = invocation.getArgument(0);
                Object value = invocation.getArgument(1);
                ((SimplePojo) instance).setName((String) value);
                return null;
            }).when(attr).set(any(), any());

            AtomicInteger loadCount = new AtomicInteger(0);
            PropertyBinding binding = new PropertyBinding(
                    attr,
                    valueReturning(new LazyValue(_ -> {
                        loadCount.incrementAndGet();
                        return "lazy-name";
                    }, "k"))
            );
            var constructor = new CglibProxyConstructor(SimplePojo.class, List.of(binding), true);
            SimplePojo proxy = (SimplePojo) constructor.construct(null);

            assertThat(proxy.getName()).isEqualTo("lazy-name");
            assertThat(loadCount.get()).isEqualTo(1);
        }

        @Test
        @DisplayName("lazy value exception propagates and allows retry")
        void lazyValueExceptionPropagatesAndAllowsRetry() {
            AtomicInteger loadCount = new AtomicInteger(0);
            PropertyBinding binding = new PropertyBinding(
                    createAttribute(SimplePojo.class, "lazyValue"),
                    valueReturning(new LazyValue(_ -> {
                        int count = loadCount.incrementAndGet();
                        if (count == 1) {
                            throw new RuntimeException("first attempt fails");
                        }
                        return "success-after-retry";
                    }, "k"))
            );
            var constructor = new CglibProxyConstructor(SimplePojo.class, List.of(binding), true);
            SimplePojo proxy = (SimplePojo) constructor.construct(null);

            //noinspection ResultOfMethodCallIgnored
            org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, proxy::getLazyValue);
            assertThat(loadCount.get()).isEqualTo(1);

            assertThat(proxy.getLazyValue()).isEqualTo("success-after-retry");
            assertThat(loadCount.get()).isEqualTo(2);
        }

        @Test
        @DisplayName("concurrent getter calls resolve only once")
        void concurrentGetterCallsResolveOnlyOnce() throws InterruptedException {
            AtomicInteger loadCount = new AtomicInteger(0);
            PropertyBinding binding = new PropertyBinding(
                    createAttribute(SimplePojo.class, "lazyValue"),
                    valueReturning(new LazyValue(_ -> {
                        loadCount.incrementAndGet();
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                        return "resolved";
                    }, "k"))
            );
            var constructor = new CglibProxyConstructor(SimplePojo.class, List.of(binding), true);
            SimplePojo proxy = (SimplePojo) constructor.construct(null);

            Thread t1 = new Thread(proxy::getLazyValue);
            Thread t2 = new Thread(proxy::getLazyValue);
            t1.start();
            t2.start();
            t1.join();
            t2.join();

            assertThat(loadCount.get()).isEqualTo(1);
            assertThat(proxy.getLazyValue()).isEqualTo("resolved");
        }

        // ==================== helper methods ====================

        private static ValueConstructor valueReturning(Object value) {
            ValueConstructor vc = mock(ValueConstructor.class);
            when(vc.construct(any())).thenReturn(value);
            return vc;
        }

        private static MetamodelAttribute createAttribute(Class<?> type, String property) {
            String capitalized = property.substring(0, 1).toUpperCase() + property.substring(1);
            Method getter = getMethod(type, "get" + capitalized);
            Method setter = getMethod(type, "set" + capitalized, getter.getReturnType());

            MetamodelAttribute attr = mock(MetamodelAttribute.class);
            when(attr.getter()).thenReturn(getter);
            when(attr.setter()).thenReturn(setter);
            doAnswer(invocation -> {
                Object instance = invocation.getArgument(0);
                Object value = invocation.getArgument(1);
                setter.invoke(instance, value);
                return null;
            }).when(attr).set(any(), any());
            return attr;
        }

        private static Method getMethod(Class<?> clazz, String name, Class<?>... paramTypes) {
            try {
                return clazz.getMethod(name, paramTypes);
            } catch (NoSuchMethodException e) {
                throw new AssertionError(e);
            }
        }
    }
}
