package io.github.nextentity.core.reflect;

import io.github.nextentity.core.exception.BeanReflectiveException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test objective: Verify ReflectUtil utility class provides correct reflection operations
 * <p>
 * Test scenarios:
 * 1. getDeclaredField finds field in class hierarchy
 * 2. typeCheck validates type compatibility
 * 3. getEnum returns enum by ordinal
 * 4. isAccessible checks field accessibility
 * <p>
 * Expected result: Reflection operations work correctly
 */
class ReflectUtilTest {

    static class ParentClass {
        private String parentField;
    }

    static class ChildClass extends ParentClass {
        private Integer childField;
    }

    @Nested
    class GetDeclaredField {

        /**
         * Test objective: Verify getDeclaredField finds field in same class
         * Test scenario: Search for field declared in class
         * Expected result: Field found
         */
        @Test
        void getDeclaredField_InSameClass_ShouldFindField() throws Exception {
            // when
            Field field = ReflectUtil.getDeclaredField(ChildClass.class, "childField");

            // then
            assertThat(field).isNotNull();
            assertThat(field.getName()).isEqualTo("childField");
        }

        /**
         * Test objective: Verify getDeclaredField finds field in parent class
         * Test scenario: Search for field declared in parent
         * Expected result: Field found in parent
         */
        @Test
        void getDeclaredField_InParentClass_ShouldFindField() {
            // when
            Field field = ReflectUtil.getDeclaredField(ChildClass.class, "parentField");

            // then
            assertThat(field).isNotNull();
            assertThat(field.getName()).isEqualTo("parentField");
            assertThat(field.getDeclaringClass()).isEqualTo(ParentClass.class);
        }

        /**
         * Test objective: Verify getDeclaredField returns null for non-existent field
         * Test scenario: Search for non-existent field
         * Expected result: null returned
         */
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

        /**
         * Test objective: Verify typeCheck accepts null for non-primitive types
         * Test scenario: Pass null for String type
         * Expected result: No exception
         */
        @Test
        void typeCheck_NullForNonPrimitive_ShouldNotThrow() {
            // when/then - no exception
            ReflectUtil.typeCheck(null, String.class);
        }

        /**
         * Test objective: Verify typeCheck throws for null with primitive type
         * Test scenario: Pass null for int type
         * Expected result: BeanReflectiveException thrown
         */
        @Test
        void typeCheck_NullForPrimitive_ShouldThrow() {
            // then
            assertThatThrownBy(() -> ReflectUtil.typeCheck(null, int.class))
                    .isInstanceOf(BeanReflectiveException.class)
                    .hasMessageContaining("primitive type value can not be null");
        }

        /**
         * Test objective: Verify typeCheck accepts compatible type
         * Test scenario: Pass String for Object type
         * Expected result: No exception
         */
        @Test
        void typeCheck_CompatibleType_ShouldNotThrow() {
            // when/then - no exception
            ReflectUtil.typeCheck("test", Object.class);
        }

        /**
         * Test objective: Verify typeCheck throws for incompatible type
         * Test scenario: Pass String for Integer type
         * Expected result: BeanReflectiveException thrown
         */
        @Test
        void typeCheck_IncompatibleType_ShouldThrow() {
            // then
            assertThatThrownBy(() -> ReflectUtil.typeCheck("string", Integer.class))
                    .isInstanceOf(BeanReflectiveException.class)
                    .hasMessageContaining("can not cast to");
        }

        /**
         * Test objective: Verify typeCheck handles primitive wrapper conversion
         * Test scenario: Pass Integer for int primitive
         * Expected result: No exception after wrapper conversion
         */
        @Test
        void typeCheck_PrimitiveWrapper_ShouldNotThrow() {
            // when/then - no exception
            ReflectUtil.typeCheck(Integer.valueOf(42), int.class);
        }
    }

    @Nested
    class GetEnum {

        /**
         * Test objective: Verify getEnum returns correct enum by ordinal
         * Test scenario: Get enum by ordinal position
         * Expected result: Correct enum constant returned
         */
        @Test
        void getEnum_ByOrdinal_ShouldReturnCorrectEnum() {
            // when
            Object result = ReflectUtil.getEnum(TestEnum.class, 1);

            // then
            assertThat(result).isEqualTo(TestEnum.SECOND);
        }

        /**
         * Test objective: Verify getEnum throws for non-enum class
         * Test scenario: Pass non-enum class
         * Expected result: IllegalArgumentException thrown
         */
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

        /**
         * Test objective: Verify newProxyInstance creates working proxy
         * Test scenario: Create proxy for interface with method implementations
         * Expected result: Proxy delegates to provided implementations
         */
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
