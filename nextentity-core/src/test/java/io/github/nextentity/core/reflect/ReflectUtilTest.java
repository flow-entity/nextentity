package io.github.nextentity.core.reflect;

import io.github.nextentity.core.exception.ReflectiveException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/// ReflectUtil 单元测试。
///
/// 覆盖场景：
/// 1. getDeclaredField：本类字段 / 父类字段 / 不存在返回 null
/// 2. typeCheck：null+primitive / null+包装类 / 类型不匹配 / 类型匹配 / primitive 自动转包装
/// 3. getFieldValue / setFieldValue：private 字段访问
/// 4. getEnum：按序数 / 按名称 / 非枚举类型 / 越界序数
/// 5. newProxyInstance：创建代理并调用方法
/// 6. isAccessible：检查可访问性
///
/// @author HuangChengwei
@DisplayName("ReflectUtil")
class ReflectUtilTest {

    // ===== 测试辅助类型 =====

    static class Parent {
        private String parentField = "parent";
    }

    static class Child extends Parent {
        private String childField = "child";
    }

    enum TestEnum {
        ALPHA, BETA, GAMMA
    }

    interface HelloService {
        String sayHello();
        int getCount();
    }

    // ===== getDeclaredField =====

    @Nested
    @DisplayName("getDeclaredField")
    class GetDeclaredFieldTest {

        @Test
        @DisplayName("should find field declared in the class itself")
        void shouldFindFieldInOwnClass() {
            Field field = ReflectUtil.getDeclaredField(Child.class, "childField");

            assertThat(field).isNotNull();
            assertThat(field.getName()).isEqualTo("childField");
            assertThat(field.getDeclaringClass()).isEqualTo(Child.class);
        }

        @Test
        @DisplayName("should find field declared in superclass")
        void shouldFindFieldInSuperclass() {
            Field field = ReflectUtil.getDeclaredField(Child.class, "parentField");

            assertThat(field).isNotNull();
            assertThat(field.getName()).isEqualTo("parentField");
            assertThat(field.getDeclaringClass()).isEqualTo(Parent.class);
        }

        @Test
        @DisplayName("should return null when field does not exist")
        void shouldReturnNullWhenFieldNotFound() {
            Field field = ReflectUtil.getDeclaredField(Child.class, "nonExistentField");

            assertThat(field).isNull();
        }

        @Test
        @DisplayName("should find field when querying from declaring class directly")
        void shouldFindFieldFromDeclaringClass() {
            Field field = ReflectUtil.getDeclaredField(Parent.class, "parentField");

            assertThat(field).isNotNull();
            assertThat(field.getDeclaringClass()).isEqualTo(Parent.class);
        }
    }

    // ===== typeCheck =====

    @Nested
    @DisplayName("typeCheck")
    class TypeCheckTest {

        @Test
        @DisplayName("should throw when null value is assigned to primitive type")
        void shouldThrowWhenNullWithPrimitiveType() {
            assertThatThrownBy(() -> ReflectUtil.typeCheck(null, int.class))
                    .isInstanceOf(ReflectiveException.class)
                    .hasMessageContaining("primitive type value can not be null");
        }

        @Test
        @DisplayName("should pass when null value is assigned to wrapper type")
        void shouldPassWhenNullWithWrapperType() {
            assertThatNoException()
                    .isThrownBy(() -> ReflectUtil.typeCheck(null, Integer.class));
        }

        @Test
        @DisplayName("should pass when null value is assigned to reference type")
        void shouldPassWhenNullWithReferenceType() {
            assertThatNoException()
                    .isThrownBy(() -> ReflectUtil.typeCheck(null, String.class));
        }

        @Test
        @DisplayName("should throw when value type does not match expected type")
        void shouldThrowWhenTypeMismatch() {
            assertThatThrownBy(() -> ReflectUtil.typeCheck("hello", Integer.class))
                    .isInstanceOf(ReflectiveException.class)
                    .hasMessageContaining("can not cast to");
        }

        @Test
        @DisplayName("should pass when value type matches expected type")
        void shouldPassWhenTypeMatches() {
            assertThatNoException()
                    .isThrownBy(() -> ReflectUtil.typeCheck(42, Integer.class));
        }

        @Test
        @DisplayName("should pass when value is a subtype of expected type")
        void shouldPassWhenValueIsSubtype() {
            assertThatNoException()
                    .isThrownBy(() -> ReflectUtil.typeCheck("hello", CharSequence.class));
        }

        @Test
        @DisplayName("should convert primitive type to wrapper and pass when value matches")
        void shouldConvertPrimitiveToWrapperAndPass() {
            assertThatNoException()
                    .isThrownBy(() -> ReflectUtil.typeCheck(42, int.class));
        }

        @Test
        @DisplayName("should convert primitive type to wrapper and throw when value mismatches")
        void shouldConvertPrimitiveToWrapperAndThrow() {
            assertThatThrownBy(() -> ReflectUtil.typeCheck("not a number", int.class))
                    .isInstanceOf(ReflectiveException.class)
                    .hasMessageContaining("can not cast to");
        }

        @Test
        @DisplayName("should pass for boolean primitive with Boolean wrapper value")
        void shouldPassForBooleanPrimitiveWithWrapperValue() {
            assertThatNoException()
                    .isThrownBy(() -> ReflectUtil.typeCheck(Boolean.TRUE, boolean.class));
        }

        @Test
        @DisplayName("should throw for boolean primitive with non-Boolean value")
        void shouldThrowForBooleanPrimitiveWithNonBooleanValue() {
            assertThatThrownBy(() -> ReflectUtil.typeCheck("true", boolean.class))
                    .isInstanceOf(ReflectiveException.class);
        }
    }

    // ===== getFieldValue / setFieldValue =====

    @Nested
    @DisplayName("getFieldValue / setFieldValue")
    class FieldAccessTest {

        @Test
        @DisplayName("should get value of private field")
        void shouldGetPrivateFieldValue() throws IllegalAccessException, NoSuchFieldException {
            Parent parent = new Parent();
            Field field = Parent.class.getDeclaredField("parentField");

            Object value = ReflectUtil.getFieldValue(field, parent);

            assertThat(value).isEqualTo("parent");
        }

        @Test
        @DisplayName("should set value of private field")
        void shouldSetPrivateFieldValue() throws IllegalAccessException, NoSuchFieldException {
            Parent parent = new Parent();
            Field field = Parent.class.getDeclaredField("parentField");

            ReflectUtil.setFieldValue(field, parent, "updated");

            assertThat(ReflectUtil.getFieldValue(field, parent)).isEqualTo("updated");
        }

        @Test
        @DisplayName("should get value of private field in child class")
        void shouldGetPrivateFieldInChildClass() throws IllegalAccessException, NoSuchFieldException {
            Child child = new Child();
            Field childField = Child.class.getDeclaredField("childField");

            Object value = ReflectUtil.getFieldValue(childField, child);

            assertThat(value).isEqualTo("child");
        }

        @Test
        @DisplayName("should set value of private field inherited from parent")
        void shouldSetInheritedPrivateField() throws IllegalAccessException, NoSuchFieldException {
            Child child = new Child();
            Field parentField = Parent.class.getDeclaredField("parentField");

            ReflectUtil.setFieldValue(parentField, child, "updated via child");

            // Access through parent field on the child instance
            Object value = ReflectUtil.getFieldValue(parentField, child);
            assertThat(value).isEqualTo("updated via child");
        }
    }

    // ===== getEnum =====

    @Nested
    @DisplayName("getEnum")
    class GetEnumTest {

        @Test
        @DisplayName("should get enum by ordinal")
        void shouldGetEnumByOrdinal() {
            Object result = ReflectUtil.getEnum(TestEnum.class, 0);

            assertThat(result).isEqualTo(TestEnum.ALPHA);
        }

        @Test
        @DisplayName("should get enum by ordinal for subsequent values")
        void shouldGetEnumByOrdinalForSubsequentValues() {
            assertThat(ReflectUtil.getEnum(TestEnum.class, 1)).isEqualTo(TestEnum.BETA);
            assertThat(ReflectUtil.getEnum(TestEnum.class, 2)).isEqualTo(TestEnum.GAMMA);
        }

        @Test
        @DisplayName("should get enum by name")
        void shouldGetEnumByName() {
            Object result = ReflectUtil.getEnum(TestEnum.class, "BETA");

            assertThat(result).isEqualTo(TestEnum.BETA);
        }

        @Test
        @DisplayName("should throw IllegalArgumentException when class is not enum (by ordinal)")
        void shouldThrowWhenNotEnumByOrdinal() {
            assertThatThrownBy(() -> ReflectUtil.getEnum(String.class, 0))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("should throw IllegalArgumentException when class is not enum (by name)")
        void shouldThrowWhenNotEnumByName() {
            assertThatThrownBy(() -> ReflectUtil.getEnum(String.class, "VALUE"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("should throw ArrayIndexOutOfBoundsException for out-of-bounds ordinal")
        void shouldThrowForOutOfBoundsOrdinal() {
            assertThatThrownBy(() -> ReflectUtil.getEnum(TestEnum.class, 100))
                    .isInstanceOf(ArrayIndexOutOfBoundsException.class);
        }

        @Test
        @DisplayName("should throw ArrayIndexOutOfBoundsException for negative ordinal")
        void shouldThrowForNegativeOrdinal() {
            assertThatThrownBy(() -> ReflectUtil.getEnum(TestEnum.class, -1))
                    .isInstanceOf(ArrayIndexOutOfBoundsException.class);
        }

        @Test
        @DisplayName("should throw ReflectiveException for invalid enum name")
        void shouldThrowForInvalidEnumName() {
            assertThatThrownBy(() -> ReflectUtil.getEnum(TestEnum.class, "NON_EXISTENT"))
                    .isInstanceOf(ReflectiveException.class);
        }

        @Test
        @DisplayName("should return cached enum array on subsequent calls")
        void shouldReturnCachedEnumArrayOnSubsequentCalls() {
            // Multiple calls should return same values, verifying cache behavior
            Object first = ReflectUtil.getEnum(TestEnum.class, 0);
            Object second = ReflectUtil.getEnum(TestEnum.class, 0);

            assertThat(first).isSameAs(second);
            assertThat(first).isEqualTo(TestEnum.ALPHA);
        }
    }

    // ===== newProxyInstance =====

    @Nested
    @DisplayName("newProxyInstance")
    class NewProxyInstanceTest {

        @Test
        @DisplayName("should create proxy that delegates to LazyValueMap")
        void shouldCreateProxyDelegatingToLazyValueMap() throws NoSuchMethodException {
            LazyValueMap map = new LazyValueMap();
            Method sayHello = HelloService.class.getMethod("sayHello");
            map.put(sayHello, "Hello, World!");

            Object proxy = ReflectUtil.newProxyInstance(HelloService.class, map);

            assertThat(proxy).isInstanceOf(HelloService.class);
            assertThat(((HelloService) proxy).sayHello()).isEqualTo("Hello, World!");
        }

        @Test
        @DisplayName("should throw AbstractMethodError for method not in map and not default")
        void shouldThrowAbstractMethodErrorForMissingNonDefaultMethod() throws NoSuchMethodException {
            LazyValueMap map = new LazyValueMap();
            // Only put sayHello, not getCount

            Object proxy = ReflectUtil.newProxyInstance(HelloService.class, map);

            assertThatThrownBy(((HelloService) proxy)::getCount)
                    .isInstanceOf(AbstractMethodError.class);
        }

        @Test
        @DisplayName("should return proxy instance of correct type")
        void shouldReturnProxyOfCorrectType() {
            LazyValueMap map = new LazyValueMap();

            Object proxy = ReflectUtil.newProxyInstance(HelloService.class, map);

            assertThat(proxy).isInstanceOf(HelloService.class);
        }
    }

    // ===== isAccessible =====

    @Nested
    @DisplayName("isAccessible")
    class IsAccessibleTest {

        @Test
        @DisplayName("should return false for private field before setAccessible")
        void shouldReturnFalseForPrivateFieldBeforeSetAccessible() throws NoSuchFieldException, IllegalAccessException {
            Field field = Parent.class.getDeclaredField("parentField");
            Parent parent = new Parent();

            // A private field accessed from outside should not be accessible
            boolean accessible = ReflectUtil.isAccessible(field, parent);

            // Note: canAccess behavior depends on the context; a private field
            // from an external caller may report differently
            assertThat(accessible).isFalse();
        }

        @Test
        @DisplayName("should return true for public field")
        void shouldReturnTrueForPublicField() throws NoSuchFieldException {
            Field field = ClassWithPublicField.class.getField("publicField");
            ClassWithPublicField instance = new ClassWithPublicField();

            boolean accessible = ReflectUtil.isAccessible(field, instance);

            assertThat(accessible).isTrue();
        }

        @Test
        @DisplayName("should return true for private field after setAccessible")
        void shouldReturnTrueForPrivateFieldAfterSetAccessible() throws NoSuchFieldException {
            Field field = Parent.class.getDeclaredField("parentField");
            Parent parent = new Parent();
            field.setAccessible(true);

            assertThat(ReflectUtil.isAccessible(field, parent)).isTrue();
        }
    }

    // ===== 辅助类 =====

    @SuppressWarnings("unused")
    public static class ClassWithPublicField {
        public String publicField = "public";
    }
}
