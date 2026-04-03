package io.github.nextentity.core.util;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/// 测试目标：验证 EmptyArrays 接口常量是正确的空数组
/// <p>
/// 测试场景：
/// 1. STRING 常量是非空的空字符串数组
/// 2. OBJECT 常量是非空的空对象数组
/// 3. 常量是单例的（多次访问时返回相同实例）
/// <p>
/// 预期结果：所有常量都应该是各自类型的非空空数组
class EmptyArraysTest {

    @Nested
    class StringConstant {

        /// 测试目标：验证 STRING 常量是有效的空字符串数组
        /// 测试场景：访问 STRING 常量并验证所有属性
        /// 预期结果：STRING 是非空的、空的字符串数组，并且是单例
        @Test
        void string_ShouldBeValidEmptyStringArray() {
            // when
            String[] array = EmptyArrays.STRING;

            // then
            assertThat(array).isNotNull();
            assertThat(array).isEmpty();
            assertThat(array).isInstanceOf(String[].class);

            // verify singleton behavior
            assertThat(array).isSameAs(EmptyArrays.STRING);
        }
    }

    @Nested
    class ObjectConstant {

        /// 测试目标：验证 OBJECT 常量是有效的空对象数组
        /// 测试场景：访问 OBJECT 常量并验证所有属性
        /// 预期结果：OBJECT 是非空的、空的对象数组，并且是单例
        @Test
        void object_ShouldBeValidEmptyObjectArray() {
            // when
            Object[] array = EmptyArrays.OBJECT;

            // then
            assertThat(array).isNotNull();
            assertThat(array).isEmpty();
            assertThat(array).isInstanceOf(Object[].class);

            // verify singleton behavior
            assertThat(array).isSameAs(EmptyArrays.OBJECT);
        }
    }

    @Nested
    class SingletonBehavior {

        /// 测试目标：验证常量在多次访问时是单例的
        /// 测试场景：多次访问常量并比较引用
        /// 预期结果：每次返回相同的实例
        @Test
        void constants_ShouldBeSingletons() {
            // when
            String[] string1 = EmptyArrays.STRING;
            String[] string2 = EmptyArrays.STRING;
            Object[] object1 = EmptyArrays.OBJECT;
            Object[] object2 = EmptyArrays.OBJECT;

            // then
            assertThat(string1).isSameAs(string2);
            assertThat(object1).isSameAs(object2);
        }
    }
}
