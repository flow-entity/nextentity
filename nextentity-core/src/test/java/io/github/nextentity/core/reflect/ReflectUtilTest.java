package io.github.nextentity.core.reflect;

import io.github.nextentity.core.exception.ReflectiveException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/// 测试目标：验证 ReflectUtil 工具类提供正确的反射操作
/// <p>
/// 测试场景：
/// 1. getDeclaredField 在类层次结构中找到字段
/// 2. typeCheck 验证类型兼容性
/// 3. getEnum 按序号返回枚举
/// 4. isAccessible 检查字段可访问性
/// <p>
/// 预期结果：反射操作正常工作
class ReflectUtilTest {

    static class ParentClass {
        private String parentField;
    }

    static class ChildClass extends ParentClass {
        private Integer childField;
    }

    @Nested
    class GetDeclaredField {

        /// 测试目标：验证 getDeclaredField 在同一类中找到字段
        /// 测试场景：搜索类中声明的字段
        /// 预期结果：找到字段
        @Test
        void getDeclaredField_InSameClass_ShouldFindField() throws Exception {
            // when
            Field field = ReflectUtil.getDeclaredField(ChildClass.class, "childField");

            // then
            assertThat(field).isNotNull();
            assertThat(field.getName()).isEqualTo("childField");
        }

        /// 测试目标：验证 getDeclaredField 在父类中找到字段
        /// 测试场景：搜索父类中声明的字段
        /// 预期结果：在父类中找到字段
        @Test
        void getDeclaredField_InParentClass_ShouldFindField() {
            // when
            Field field = ReflectUtil.getDeclaredField(ChildClass.class, "parentField");

            // then
            assertThat(field).isNotNull();
            assertThat(field.getName()).isEqualTo("parentField");
            assertThat(field.getDeclaringClass()).isEqualTo(ParentClass.class);
        }

        /// 测试目标：验证 getDeclaredField 为不存在的字段返回 null
        /// 测试场景：搜索不存在的字段
        /// 预期结果：返回 null
        @Test
        void getDeclaredField_NotExists_ShouldReturnNull() {
            // when
            Field field = ReflectUtil.getDeclaredField(ChildClass.class, "nonExistentField");

            // then
            assertThat(field).isNull();
        }
    }

    @Nested
    class TypeCheck {

        /// 测试目标：验证 typeCheck 接受非基本类型的 null 值
        /// 测试场景：为 String 类型传递 null
        /// 预期结果：不抛出异常
        @Test
        void typeCheck_NullForNonPrimitive_ShouldNotThrow() {
            // when/then - no exception
            ReflectUtil.typeCheck(null, String.class);
        }

        /// 测试目标：验证 typeCheck 对基本类型 null 值抛出异常
        /// 测试场景：为 int 类型传递 null
        /// 预期结果：BeanReflectiveException 抛出
        @Test
        void typeCheck_NullForPrimitive_ShouldThrow() {
            // then
            assertThatThrownBy(() -> ReflectUtil.typeCheck(null, int.class))
                    .isInstanceOf(ReflectiveException.class)
                    .hasMessageContaining("primitive type value can not be null");
        }

        /// 测试目标：验证 typeCheck 接受兼容类型
        /// 测试场景：为 Object 类型传递 String
        /// 预期结果：不抛出异常
        @Test
        void typeCheck_CompatibleType_ShouldNotThrow() {
            // when/then - no exception
            ReflectUtil.typeCheck("test", Object.class);
        }

        /// 测试目标：验证 typeCheck 对不兼容类型抛出异常
        /// 测试场景：为 Integer 类型传递 String
        /// 预期结果：BeanReflectiveException 抛出
        @Test
        void typeCheck_IncompatibleType_ShouldThrow() {
            // then
            assertThatThrownBy(() -> ReflectUtil.typeCheck("string", Integer.class))
                    .isInstanceOf(ReflectiveException.class)
                    .hasMessageContaining("can not cast to");
        }

        /// 测试目标：验证 typeCheck 处理基本类型包装器转换
        /// 测试场景：为 int 基本类型传递 Integer
        /// 预期结果：包装器转换后无异常
        @Test
        void typeCheck_PrimitiveWrapper_ShouldNotThrow() {
            // when/then - no exception
            ReflectUtil.typeCheck(Integer.valueOf(42), int.class);
        }
    }

    @Nested
    class GetEnum {

        /// 测试目标：验证 getEnum 按序号返回正确的枚举
        /// 测试场景：按序号位置获取枚举
        /// 预期结果：返回正确的枚举常量
        @Test
        void getEnum_ByOrdinal_ShouldReturnCorrectEnum() {
            // when
            Object result = ReflectUtil.getEnum(TestEnum.class, 1);

            // then
            assertThat(result).isEqualTo(TestEnum.SECOND);
        }

        /// 测试目标：验证 getEnum 对非枚举类抛出异常
        /// 测试场景：传递非枚举类
        /// 预期结果：IllegalArgumentException 抛出
        @Test
        void getEnum_NonEnumClass_ShouldThrow() {
            // then
            assertThatThrownBy(() -> ReflectUtil.getEnum(String.class, 0))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    enum TestEnum {
        FIRST, SECOND, THIRD
    }

    @Nested
    class NewProxyInstance {

        /// 测试目标：验证 newProxyInstance 创建工作代理
        /// 测试场景：为接口创建具有方法实现的代理
        /// 预期结果：代理委托给提供的实现
        @Test
        void newProxyInstance_ShouldCreateWorkingProxy() throws Exception {
            // given
            Method getName = TestInterface.class.getMethod("getName");
            Method getValue = TestInterface.class.getMethod("getValue");
            Map<Method, Object> map = Map.of(
                    getName, "test-name",
                    getValue, 42
            );

            // when
            Object proxy = ReflectUtil.newProxyInstance(TestInterface.class, map);

            // then
            assertThat(proxy).isInstanceOf(TestInterface.class);
            TestInterface testProxy = (TestInterface) proxy;
            assertThat(testProxy.getName()).isEqualTo("test-name");
            assertThat(testProxy.getValue()).isEqualTo(42);
        }
    }

    interface TestInterface {
        String getName();
        int getValue();
    }
}