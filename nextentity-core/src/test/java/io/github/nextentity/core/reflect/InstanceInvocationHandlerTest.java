package io.github.nextentity.core.reflect;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/// 测试目标：验证 InstanceInvocationHandler 提供正确的代理调用处理
/// <p>
/// 测试场景：
/// 1. invoke 为已知方法从映射中返回数据
/// 2. equals 正确比较处理器
/// 3. hashCode 保持一致
/// 4. toString 返回有意义的表示
/// <p>
/// 预期结果：调用处理器与动态代理正确配合工作
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

        /// 测试目标：验证 invoke 为方法返回映射的值
        /// 测试场景：调用代理的方法
        /// 预期结果：返回映射中的值
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

        /// 测试目标：验证 invoke 返回默认方法结果
        /// 测试场景：调用映射中不存在的默认方法
        /// 预期结果：执行默认方法
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

        /// 测试目标：验证 equals 对同一处理器返回 true
        /// 测试场景：将处理器与其自身比较
        /// 预期结果：返回 true
        @Test
        void equals_SameHandler_ShouldReturnTrue() {
            // given
            Map<Method, Object> data = Map.of();
            InstanceInvocationHandler handler = new InstanceInvocationHandler(TestInterface.class, data);

            // then
            assertThat(handler.equals(handler)).isTrue();
        }

        /// 测试目标：验证 equals 对 null 返回 false
        /// 测试场景：将处理器与 null 比较
        /// 预期结果：返回 false
        @Test
        void equals_Null_ShouldReturnFalse() {
            // given
            Map<Method, Object> data = Map.of();
            InstanceInvocationHandler handler = new InstanceInvocationHandler(TestInterface.class, data);

            // then
            assertThat(handler.equals(null)).isFalse();
        }

        /// 测试目标：验证 equals 正确比较代理
        /// 测试场景：将代理与具有相同处理器的另一个代理比较
        /// 预期结果：代理被视为相等
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

        /// 测试目标：验证 toString 包含类型名称
        /// 测试场景：调用 toString
        /// 预期结果：字符串包含接口名称
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

        /// 测试目标：验证 resultType 返回正确类型
        /// 测试场景：调用 resultType()
        /// 预期结果：返回配置的类型
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

        /// 测试目标：验证 data 返回正确的映射
        /// 测试场景：调用 data()
        /// 预期结果：返回配置的数据映射
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