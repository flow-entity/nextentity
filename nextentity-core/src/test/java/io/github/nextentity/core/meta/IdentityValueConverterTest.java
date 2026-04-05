package io.github.nextentity.core.meta;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

///
 /// 测试目标: 验证y IdentityValueConverter returns values unchanged
 /// <p>
 /// 测试场景s:
 /// 1. Singleton INSTANCE
 /// 2. convertToDatabaseColumn returns value unchanged
 /// 3. convertToEntityAttribute returns value unchanged
 /// 4. getDatabaseColumnType returns configured type
class IdentityValueConverterTest {

    @Nested
    class SingletonInstance {

///
         /// 测试目标: 验证y INSTANCE is singleton
         /// 测试场景: Access INSTANCE multiple times
         /// 预期结果: Same instance
        @Test
        void instance_IsSingleton() {
            // when
            IdentityValueConverter instance1 = IdentityValueConverter.INSTANCE;
            IdentityValueConverter instance2 = IdentityValueConverter.of();

            // then
            assertThat(instance1).isSameAs(instance2);
        }
    }

    @Nested
    class ConvertToDatabaseColumn {

///
         /// 测试目标: 验证y convertToDatabaseColumn returns value unchanged
         /// 测试场景: Convert string value
         /// 预期结果: Same string
        @Test
        void convertToDatabaseColumn_ReturnsValue() {
            // given
            IdentityValueConverter converter = IdentityValueConverter.of();
            String value = "test";

            // when
            Object result = converter.convertToDatabaseColumn(value);

            // then
            assertThat(result).isSameAs(value);
        }

///
         /// 测试目标: 验证y convertToDatabaseColumn 处理 null
         /// 测试场景: Convert null
         /// 预期结果: null
        @Test
        void convertToDatabaseColumn_Null_ReturnsNull() {
            // given
            IdentityValueConverter converter = IdentityValueConverter.of();

            // when
            Object result = converter.convertToDatabaseColumn(null);

            // then
            assertThat(result).isNull();
        }

///
         /// 测试目标: 验证y convertToDatabaseColumn 处理 integer
         /// 测试场景: Convert integer
         /// 预期结果: Same integer
        @Test
        void convertToDatabaseColumn_Integer_ReturnsValue() {
            // given
            IdentityValueConverter converter = IdentityValueConverter.of();
            Integer value = Integer.valueOf(42);

            // when
            Object result = converter.convertToDatabaseColumn(value);

            // then
            assertThat(result).isSameAs(value);
        }
    }

    @Nested
    class ConvertToEntityAttribute {

///
         /// 测试目标: 验证y convertToEntityAttribute returns value unchanged
         /// 测试场景: Convert string value
         /// 预期结果: Same string
        @Test
        void convertToEntityAttribute_ReturnsValue() {
            // given
            IdentityValueConverter converter = IdentityValueConverter.of();
            String value = "test";

            // when
            Object result = converter.convertToEntityAttribute(value);

            // then
            assertThat(result).isSameAs(value);
        }

///
         /// 测试目标: 验证y convertToEntityAttribute 处理 null
         /// 测试场景: Convert null
         /// 预期结果: null
        @Test
        void convertToEntityAttribute_Null_ReturnsNull() {
            // given
            IdentityValueConverter converter = IdentityValueConverter.of();

            // when
            Object result = converter.convertToEntityAttribute(null);

            // then
            assertThat(result).isNull();
        }
    }

    @Nested
    class GetDatabaseColumnType {

///
         /// 测试目标: 验证y getDatabaseColumnType returns Object.class for default
         /// 测试场景: Get database column type from INSTANCE
         /// 预期结果: Object.class
        @Test
        void getDatabaseColumnType_Default_ReturnsObject() {
            // given
            IdentityValueConverter converter = IdentityValueConverter.of();

            // when
            Class<?> result = converter.getDatabaseColumnType();

            // then
            assertThat(result).isEqualTo(Object.class);
        }

///
         /// 测试目标: 验证y getDatabaseColumnType returns configured type
         /// 测试场景: Create converter with specific type
         /// 预期结果: Configured type
        @Test
        void getDatabaseColumnType_Custom_ReturnsConfiguredType() {
            // given
            IdentityValueConverter converter = new IdentityValueConverter(String.class);

            // when
            Class<?> result = converter.getDatabaseColumnType();

            // then
            assertThat(result).isEqualTo(String.class);
        }
    }
}
