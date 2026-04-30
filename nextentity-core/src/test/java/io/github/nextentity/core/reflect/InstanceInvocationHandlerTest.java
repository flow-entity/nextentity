package io.github.nextentity.core.reflect;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("InstanceInvocationHandler")
class InstanceInvocationHandlerTest {

    interface TestEntity {
        String getName();

        int getAge();
    }

    interface EntityWithDefault {
        String getName();

        default String greet() {
            return "Hello, " + getName();
        }
    }

    private static Method getNameMethod() throws NoSuchMethodException {
        return TestEntity.class.getMethod("getName");
    }

    private static Method getAgeMethod() throws NoSuchMethodException {
        return TestEntity.class.getMethod("getAge");
    }

    private static Method getDefaultGetNameMethod() throws NoSuchMethodException {
        return EntityWithDefault.class.getMethod("getName");
    }

    private static Object createProxy(Class<?> interfaceType, LazyValueMap data) {
        InstanceInvocationHandler handler = new InstanceInvocationHandler(interfaceType, data);
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        return Proxy.newProxyInstance(cl, new Class<?>[]{interfaceType}, handler);
    }

    @Nested
    @DisplayName("invoke")
    class InvokeTests {

        @Test
        @DisplayName("匹配 data 中的方法返回对应值")
        void shouldReturnValueFromData() throws NoSuchMethodException {
            LazyValueMap data = new LazyValueMap();
            data.put(getNameMethod(), "Alice");
            TestEntity proxy = (TestEntity) createProxy(TestEntity.class, data);

            assertThat(proxy.getName()).isEqualTo("Alice");
        }

        @Test
        @DisplayName("data 中包含多个方法时分别返回对应值")
        void shouldReturnValuesForMultipleMethods() throws NoSuchMethodException {
            LazyValueMap data = new LazyValueMap();
            data.put(getNameMethod(), "Bob");
            data.put(getAgeMethod(), 25);
            TestEntity proxy = (TestEntity) createProxy(TestEntity.class, data);

            assertThat(proxy.getName()).isEqualTo("Bob");
            assertThat(proxy.getAge()).isEqualTo(25);
        }

        @Test
        @DisplayName("data 中值为 null 时返回 null")
        void shouldReturnNullWhenDataValueIsNull() throws NoSuchMethodException {
            LazyValueMap data = new LazyValueMap();
            data.put(getNameMethod(), null);
            TestEntity proxy = (TestEntity) createProxy(TestEntity.class, data);

            assertThat(proxy.getName()).isNull();
        }

        @Test
        @DisplayName("Object.toString() 委托给 handler 自身")
        void shouldDelegateToStringToHandler() {
            LazyValueMap data = new LazyValueMap();
            InstanceInvocationHandler handler = new InstanceInvocationHandler(TestEntity.class, data);
            Object proxy = Proxy.newProxyInstance(
                    Thread.currentThread().getContextClassLoader(),
                    new Class<?>[]{TestEntity.class}, handler);

            assertThat(proxy.toString()).isEqualTo(handler.toString());
        }

        @Test
        @DisplayName("Object.hashCode() 委托给 handler 自身")
        void shouldDelegateHashCodeToHandler() {
            LazyValueMap data = new LazyValueMap();
            InstanceInvocationHandler handler = new InstanceInvocationHandler(TestEntity.class, data);
            Object proxy = Proxy.newProxyInstance(
                    Thread.currentThread().getContextClassLoader(),
                    new Class<?>[]{TestEntity.class}, handler);

            assertThat(proxy.hashCode()).isEqualTo(handler.hashCode());
        }

        @Test
        @DisplayName("Object.equals() 委托给 handler 自身")
        void shouldDelegateEqualsToHandler() {
            LazyValueMap data = new LazyValueMap();
            InstanceInvocationHandler handler = new InstanceInvocationHandler(TestEntity.class, data);
            Object proxy = Proxy.newProxyInstance(
                    Thread.currentThread().getContextClassLoader(),
                    new Class<?>[]{TestEntity.class}, handler);

            assertThat(proxy.equals(handler)).isTrue();
            assertThat(proxy.equals("not-a-handler")).isFalse();
        }

        @Test
        @DisplayName("default 方法通过 ReflectUtil.invokeDefaultMethod 执行")
        void shouldInvokeDefaultMethod() throws NoSuchMethodException {
            LazyValueMap data = new LazyValueMap();
            data.put(getDefaultGetNameMethod(), "World");
            EntityWithDefault proxy = (EntityWithDefault) createProxy(EntityWithDefault.class, data);

            assertThat(proxy.greet()).isEqualTo("Hello, World");
        }

        @Test
        @DisplayName("data 中包含 Object 方法时优先返回 data 中的值")
        void shouldPrioritizeDataOverObjectMethods() throws NoSuchMethodException {
            LazyValueMap data = new LazyValueMap();
            data.put(Object.class.getMethod("toString"), "custom-to-string");
            Object proxy = createProxy(TestEntity.class, data);

            assertThat(proxy.toString()).isEqualTo("custom-to-string");
        }

        @Test
        @DisplayName("未匹配方法抛出 AbstractMethodError")
        void shouldThrowAbstractMethodErrorForUnmatchedMethod() throws NoSuchMethodException {
            LazyValueMap data = new LazyValueMap();
            data.put(getNameMethod(), "Alice");
            TestEntity proxy = (TestEntity) createProxy(TestEntity.class, data);

            assertThatThrownBy(proxy::getAge)
                    .isInstanceOf(AbstractMethodError.class);
        }

        @Test
        @DisplayName("AbstractMethodError 包含方法信息")
        void shouldIncludeMethodInfoInAbstractMethodError() throws NoSuchMethodException {
            LazyValueMap data = new LazyValueMap();
            TestEntity proxy = (TestEntity) createProxy(TestEntity.class, data);

            assertThatThrownBy(proxy::getAge)
                    .isInstanceOf(AbstractMethodError.class)
                    .hasMessageContaining("getAge");
        }
    }

    @Nested
    @DisplayName("equals")
    class EqualsTests {

        @Test
        @DisplayName("自身相等")
        void shouldBeEqualToItself() {
            LazyValueMap data = new LazyValueMap();
            InstanceInvocationHandler handler = new InstanceInvocationHandler(TestEntity.class, data);

            assertThat(handler.equals(handler)).isTrue();
        }

        @Test
        @DisplayName("相同 resultType 和 data 的两个 handler 相等")
        void shouldBeEqualWhenSameResultTypeAndData() throws NoSuchMethodException {
            LazyValueMap data1 = new LazyValueMap();
            data1.put(getNameMethod(), "Alice");
            LazyValueMap data2 = new LazyValueMap();
            data2.put(getNameMethod(), "Alice");

            InstanceInvocationHandler handler1 = new InstanceInvocationHandler(TestEntity.class, data1);
            InstanceInvocationHandler handler2 = new InstanceInvocationHandler(TestEntity.class, data2);

            assertThat(handler1).isEqualTo(handler2);
        }

        @Test
        @DisplayName("不同 resultType 的 handler 不相等")
        void shouldNotBeEqualWhenDifferentResultType() {
            LazyValueMap data = new LazyValueMap();
            InstanceInvocationHandler handler1 = new InstanceInvocationHandler(TestEntity.class, data);
            InstanceInvocationHandler handler2 = new InstanceInvocationHandler(EntityWithDefault.class, data);

            assertThat(handler1).isNotEqualTo(handler2);
        }

        @Test
        @DisplayName("不同 data 的 handler 不相等")
        void shouldNotBeEqualWhenDifferentData() throws NoSuchMethodException {
            LazyValueMap data1 = new LazyValueMap();
            data1.put(getNameMethod(), "Alice");
            LazyValueMap data2 = new LazyValueMap();
            data2.put(getNameMethod(), "Bob");

            InstanceInvocationHandler handler1 = new InstanceInvocationHandler(TestEntity.class, data1);
            InstanceInvocationHandler handler2 = new InstanceInvocationHandler(TestEntity.class, data2);

            assertThat(handler1).isNotEqualTo(handler2);
        }

        @Test
        @DisplayName("与 null 比较返回 false")
        void shouldNotBeEqualToNull() {
            LazyValueMap data = new LazyValueMap();
            InstanceInvocationHandler handler = new InstanceInvocationHandler(TestEntity.class, data);

            assertThat(handler).isNotEqualTo(null);
        }

        @Test
        @DisplayName("与不同类型比较返回 false")
        void shouldNotBeEqualToDifferentType() {
            LazyValueMap data = new LazyValueMap();
            InstanceInvocationHandler handler = new InstanceInvocationHandler(TestEntity.class, data);

            assertThat(handler).isNotEqualTo(new Object());
        }

        @Test
        @DisplayName("proxy 对象解包后与同实例 handler 相等")
        void shouldBeEqualToProxyWithSameHandler() {
            LazyValueMap data = new LazyValueMap();
            InstanceInvocationHandler handler = new InstanceInvocationHandler(TestEntity.class, data);
            Object proxy = Proxy.newProxyInstance(
                    Thread.currentThread().getContextClassLoader(),
                    new Class<?>[]{TestEntity.class}, handler);

            // handler.equals(proxy) -> 解包 proxy 得到 handler -> this == handler -> true
            assertThat(handler.equals(proxy)).isTrue();
        }

        @Test
        @DisplayName("proxy 对象解包后与相同内容 handler 相等")
        void shouldBeEqualToProxyWithEqualHandler() throws NoSuchMethodException {
            LazyValueMap data1 = new LazyValueMap();
            data1.put(getNameMethod(), "Alice");
            LazyValueMap data2 = new LazyValueMap();
            data2.put(getNameMethod(), "Alice");

            InstanceInvocationHandler handler1 = new InstanceInvocationHandler(TestEntity.class, data1);
            InstanceInvocationHandler handler2 = new InstanceInvocationHandler(TestEntity.class, data2);
            Object proxy = Proxy.newProxyInstance(
                    Thread.currentThread().getContextClassLoader(),
                    new Class<?>[]{TestEntity.class}, handler2);

            // handler1.equals(proxy) -> 解包 proxy 得到 handler2 -> 比较 resultType 和 data -> true
            assertThat(handler1.equals(proxy)).isTrue();
        }

        @Test
        @DisplayName("两个 proxy 通过 equals 比较时解包后比较 handler 内容")
        void proxyEqualsProxyShouldUnwrapAndCompareHandlers() throws NoSuchMethodException {
            LazyValueMap data1 = new LazyValueMap();
            data1.put(getNameMethod(), "Alice");
            LazyValueMap data2 = new LazyValueMap();
            data2.put(getNameMethod(), "Alice");

            Object proxy1 = createProxy(TestEntity.class, data1);
            Object proxy2 = createProxy(TestEntity.class, data2);

            assertThat(proxy1.equals(proxy2)).isTrue();
        }

        @Test
        @DisplayName("两个 proxy 内容不同时 equals 返回 false")
        void proxyEqualsProxyShouldReturnFalseWhenDifferent() throws NoSuchMethodException {
            LazyValueMap data1 = new LazyValueMap();
            data1.put(getNameMethod(), "Alice");
            LazyValueMap data2 = new LazyValueMap();
            data2.put(getNameMethod(), "Bob");

            Object proxy1 = createProxy(TestEntity.class, data1);
            Object proxy2 = createProxy(TestEntity.class, data2);

            assertThat(proxy1.equals(proxy2)).isFalse();
        }
    }

    @Nested
    @DisplayName("hashCode")
    class HashCodeTests {

        @Test
        @DisplayName("基于 data 和 resultType 计算")
        void shouldComputeBasedOnDataAndResultType() {
            LazyValueMap data = new LazyValueMap();
            InstanceInvocationHandler handler = new InstanceInvocationHandler(TestEntity.class, data);

            int expected = 31 * data.hashCode() + TestEntity.class.hashCode();
            assertThat(handler.hashCode()).isEqualTo(expected);
        }

        @Test
        @DisplayName("相同 data 和 resultType 的两个 handler 的 hashCode 相等")
        void shouldHaveSameHashCodeWhenSameDataAndResultType() throws NoSuchMethodException {
            LazyValueMap data1 = new LazyValueMap();
            data1.put(getNameMethod(), "Alice");
            LazyValueMap data2 = new LazyValueMap();
            data2.put(getNameMethod(), "Alice");

            InstanceInvocationHandler handler1 = new InstanceInvocationHandler(TestEntity.class, data1);
            InstanceInvocationHandler handler2 = new InstanceInvocationHandler(TestEntity.class, data2);

            assertThat(handler1.hashCode()).isEqualTo(handler2.hashCode());
        }

        @Test
        @DisplayName("不同 resultType 的 handler 的 hashCode 不同")
        void shouldHaveDifferentHashCodeWhenDifferentResultType() {
            LazyValueMap data = new LazyValueMap();
            InstanceInvocationHandler handler1 = new InstanceInvocationHandler(TestEntity.class, data);
            InstanceInvocationHandler handler2 = new InstanceInvocationHandler(EntityWithDefault.class, data);

            assertThat(handler1.hashCode()).isNotEqualTo(handler2.hashCode());
        }
    }

    @Nested
    @DisplayName("toString")
    class ToStringTests {

        @Test
        @DisplayName("返回 resultType 简名 + @ + identityHashCode")
        void shouldReturnResultTypeSimpleNameAndIdentityHashCode() {
            LazyValueMap data = new LazyValueMap();
            InstanceInvocationHandler handler = new InstanceInvocationHandler(TestEntity.class, data);

            String result = handler.toString();

            assertThat(result).isEqualTo("TestEntity@" + System.identityHashCode(data));
        }

        @Test
        @DisplayName("不同 data 实例的 toString 不同（identityHashCode 不同）")
        void shouldReturnDifferentToStringForDifferentDataInstances() {
            LazyValueMap data1 = new LazyValueMap();
            LazyValueMap data2 = new LazyValueMap();

            InstanceInvocationHandler handler1 = new InstanceInvocationHandler(TestEntity.class, data1);
            InstanceInvocationHandler handler2 = new InstanceInvocationHandler(TestEntity.class, data2);

            assertThat(handler1.toString()).isNotEqualTo(handler2.toString());
        }

        @Test
        @DisplayName("相同 data 实例的 toString 相同")
        void shouldReturnSameToStringForSameDataInstance() {
            LazyValueMap data = new LazyValueMap();
            InstanceInvocationHandler handler1 = new InstanceInvocationHandler(TestEntity.class, data);
            InstanceInvocationHandler handler2 = new InstanceInvocationHandler(TestEntity.class, data);

            assertThat(handler1.toString()).isEqualTo(handler2.toString());
        }
    }

    @Nested
    @DisplayName("resultType")
    class ResultTypeTests {

        @Test
        @DisplayName("返回构造时传入的 resultType")
        void shouldReturnResultType() {
            LazyValueMap data = new LazyValueMap();
            InstanceInvocationHandler handler = new InstanceInvocationHandler(TestEntity.class, data);

            assertThat(handler.resultType()).isEqualTo(TestEntity.class);
        }

        @Test
        @DisplayName("不同 resultType 返回不同类型")
        void shouldReturnDifferentResultTypeForDifferentConstructorArg() {
            LazyValueMap data = new LazyValueMap();
            InstanceInvocationHandler handler1 = new InstanceInvocationHandler(TestEntity.class, data);
            InstanceInvocationHandler handler2 = new InstanceInvocationHandler(EntityWithDefault.class, data);

            assertThat(handler1.resultType()).isEqualTo(TestEntity.class);
            assertThat(handler2.resultType()).isEqualTo(EntityWithDefault.class);
        }
    }
}
