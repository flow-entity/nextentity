package io.github.nextentity.core.reflect;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test objective: Verify InstanceInvocationHandler provides correct proxy invocation handling
 * <p>
 * Test scenarios:
 * 1. invoke returns data from map for known methods
 * 2. equals compares handlers correctly
 * 3. hashCode is consistent
 * 4. toString returns meaningful representation
 * <p>
 * Expected result: Invocation handler works correctly with dynamic proxies
 */
class InstanceInvocationHandlerTest {

    interface TestInterface {
        String getName();
        int getValue();
        default String getDefaultValue() {
            return "default";
        }
    }

    @Nested
    class Invoke {

        /**
         * Test objective: Verify invoke returns mapped value for method
         * Test scenario: Call proxied method
         * Expected result: Value from map returned
         */
        @Test
        void invoke_WithMappedMethod_ShouldReturnMappedValue() throws Throwable {
            // given
            Method getName = TestInterface.class.getMethod("getName");
            Map<Method, Object> data = Map.of(getName, "test-value");
            InstanceInvocationHandler handler = new InstanceInvocationHandler(TestInterface.class, data);
            Object proxy = Proxy.newProxyInstance(
                    TestInterface.class.getClassLoader(),
                    new Class<?>[]{TestInterface.class},
                    handler
            );

            // when
            Object result = handler.invoke(proxy, getName, null);

            // then
            assertThat(result).isEqualTo("test-value");
        }

        /**
         * Test objective: Verify invoke returns default method result
         * Test scenario: Call default method not in map
         * Expected result: Default method executed
         */
        @Test
        void invoke_WithDefaultMethod_ShouldExecuteDefault() throws Throwable {
            // given
            Method getDefaultValue = TestInterface.class.getMethod("getDefaultValue");
            Map<Method, Object> data = Map.of();
            InstanceInvocationHandler handler = new InstanceInvocationHandler(TestInterface.class, data);
            Object proxy = Proxy.newProxyInstance(
                    TestInterface.class.getClassLoader(),
                    new Class<?>[]{TestInterface.class},
                    handler
            );

            // when
            Object result = handler.invoke(proxy, getDefaultValue, null);

            // then
            assertThat(result).isEqualTo("default");
        }
    }

    @Nested
    class EqualsAndHashCode {

        /**
         * Test objective: Verify equals returns true for same handler
         * Test scenario: Compare handler to itself
         * Expected result: true returned
         */
        @Test
        void equals_SameHandler_ShouldReturnTrue() {
            // given
            Map<Method, Object> data = Map.of();
            InstanceInvocationHandler handler = new InstanceInvocationHandler(TestInterface.class, data);

            // then
            assertThat(handler.equals(handler)).isTrue();
        }

        /**
         * Test objective: Verify equals returns false for null
         * Test scenario: Compare handler to null
         * Expected result: false returned
         */
        @Test
        void equals_Null_ShouldReturnFalse() {
            // given
            Map<Method, Object> data = Map.of();
            InstanceInvocationHandler handler = new InstanceInvocationHandler(TestInterface.class, data);

            // then
            assertThat(handler.equals(null)).isFalse();
        }

        /**
         * Test objective: Verify equals compares proxies correctly
         * Test scenario: Compare proxy to another proxy with same handler
         * Expected result: Proxies considered equal
         */
        @Test
        void equals_ProxyWithSameHandler_ShouldBeEqual() throws Exception {
            // given
            Method getName = TestInterface.class.getMethod("getName");
            Map<Method, Object> data = Map.of(getName, "test");
            InstanceInvocationHandler handler1 = new InstanceInvocationHandler(TestInterface.class, data);
            InstanceInvocationHandler handler2 = new InstanceInvocationHandler(TestInterface.class, data);

            // then
            assertThat(handler1.equals(handler2)).isTrue();
            assertThat(handler1.hashCode()).isEqualTo(handler2.hashCode());
        }
    }

    @Nested
    class ToString {

        /**
         * Test objective: Verify toString contains type name
         * Test scenario: Call toString
         * Expected result: String contains interface name
         */
        @Test
        void toString_ShouldContainTypeName() {
            // given
            Map<Method, Object> data = Map.of();
            InstanceInvocationHandler handler = new InstanceInvocationHandler(TestInterface.class, data);

            // when
            String result = handler.toString();

            // then
            assertThat(result).contains("TestInterface");
        }
    }

    @Nested
    class AccessorMethods {

        /**
         * Test objective: Verify resultType returns correct type
         * Test scenario: Call resultType()
         * Expected result: Configured type returned
         */
        @Test
        void resultType_ShouldReturnConfiguredType() {
            // given
            Map<Method, Object> data = Map.of();
            InstanceInvocationHandler handler = new InstanceInvocationHandler(TestInterface.class, data);

            // when
            Class<?> result = handler.resultType();

            // then
            assertThat(result).isEqualTo(TestInterface.class);
        }

        /**
         * Test objective: Verify data returns correct map
         * Test scenario: Call data()
         * Expected result: Configured data map returned
         */
        @Test
        void data_ShouldReturnConfiguredData() throws Exception {
            // given
            Method getName = TestInterface.class.getMethod("getName");
            Map<Method, Object> data = Map.of(getName, "test");
            InstanceInvocationHandler handler = new InstanceInvocationHandler(TestInterface.class, data);

            // when
            Map<Method, Object> result = handler.data();

            // then
            assertThat(result).isEqualTo(data);
        }
    }
}
