package io.github.nextentity.core.meta;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

///
 /// 测试目标: 验证y OrdinalOfEnumType 正确 converts enum to ordinal
 /// <p>
 /// 测试场景s:
 /// 1. Constructor creates converter for enum type
 /// 2. toDatabaseType converts enum to ordinal
 /// 3. toAttributeType converts ordinal to enum
 /// 4. databaseType returns Integer.class
class OrdinalOfEnumTypeTest {

    enum TestStatus {
        ACTIVE, INACTIVE, PENDING
    }

    @Nested
    class Constructor {

///
         /// 测试目标: 验证y constructor creates converter
         /// 测试场景: Create converter for enum type
         /// 预期结果: Converter is created
        @Test
        void constructor_CreatesConverter() {
            // when
            OrdinalOfEnumType converter = new OrdinalOfEnumType(TestStatus.class);

            // then
            assertThat(converter).isNotNull();
        }
    }

    @Nested
    class DatabaseType {

///
         /// 测试目标: 验证y databaseType returns Integer.class
         /// 测试场景: Call databaseType()
         /// 预期结果: Integer.class
        @Test
        void databaseType_ReturnsInteger() {
            // given
            OrdinalOfEnumType converter = new OrdinalOfEnumType(TestStatus.class);

            // when
            Class<?> result = converter.databaseType();

            // then
            assertThat(result).isEqualTo(Integer.class);
        }
    }

    @Nested
    class ToDatabaseType {

///
         /// 测试目标: 验证y toDatabaseType converts enum to ordinal
         /// 测试场景: Convert ACTIVE enum
         /// 预期结果: 0 (ordinal of ACTIVE)
        @Test
        void toDatabaseType_ConvertsToOrdinal() {
            // given
            OrdinalOfEnumType converter = new OrdinalOfEnumType(TestStatus.class);

            // when
            Object result = converter.toDatabaseType(TestStatus.ACTIVE);

            // then
            assertThat(result).isEqualTo(0);
        }

///
         /// 测试目标: 验证y toDatabaseType converts INACTIVE to ordinal 1
         /// 测试场景: Convert INACTIVE enum
         /// 预期结果: 1 (ordinal of INACTIVE)
        @Test
        void toDatabaseType_ConvertsInActiveToOrdinalOne() {
            // given
            OrdinalOfEnumType converter = new OrdinalOfEnumType(TestStatus.class);

            // when
            Object result = converter.toDatabaseType(TestStatus.INACTIVE);

            // then
            assertThat(result).isEqualTo(1);
        }

///
         /// 测试目标: 验证y toDatabaseType 处理 null
         /// 测试场景: Convert null
         /// 预期结果: null
        @Test
        void toDatabaseType_Null_ReturnsNull() {
            // given
            OrdinalOfEnumType converter = new OrdinalOfEnumType(TestStatus.class);

            // when
            Object result = converter.toDatabaseType(null);

            // then
            assertThat(result).isNull();
        }
    }

    @Nested
    class ToAttributeType {

///
         /// 测试目标: 验证y toAttributeType converts ordinal to enum
         /// 测试场景: Convert ordinal 0
         /// 预期结果: ACTIVE enum
        @Test
        void toAttributeType_ConvertsToEnum() {
            // given
            OrdinalOfEnumType converter = new OrdinalOfEnumType(TestStatus.class);

            // when
            Object result = converter.toAttributeType(0);

            // then
            assertThat(result).isEqualTo(TestStatus.ACTIVE);
        }

///
         /// 测试目标: 验证y toAttributeType converts ordinal 1
         /// 测试场景: Convert ordinal 1
         /// 预期结果: INACTIVE enum
        @Test
        void toAttributeType_ConvertsOrdinalOneToEnum() {
            // given
            OrdinalOfEnumType converter = new OrdinalOfEnumType(TestStatus.class);

            // when
            Object result = converter.toAttributeType(1);

            // then
            assertThat(result).isEqualTo(TestStatus.INACTIVE);
        }

///
         /// 测试目标: 验证y toAttributeType returns non-Integer unchanged
         /// 测试场景: Convert string
         /// 预期结果: Same string
        @Test
        void toAttributeType_NonInteger_ReturnsValue() {
            // given
            OrdinalOfEnumType converter = new OrdinalOfEnumType(TestStatus.class);
            String value = "test";

            // when
            Object result = converter.toAttributeType(value);

            // then
            assertThat(result).isEqualTo(value);
        }
    }
}
