package io.github.nextentity.core.meta;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test objective: Verify IdentityValueConverter returns values unchanged
 * <p>
 * Test scenarios:
 * 1. Singleton INSTANCE
 * 2. convertToDatabaseColumn returns value unchanged
 * 3. convertToEntityAttribute returns value unchanged
 * 4. getDatabaseColumnType returns configured type
 */
class IdentityValueConverterTest {

    @Nested
    class SingletonInstance {

        /**
         * Test objective: Verify INSTANCE is singleton
         * Test scenario: Access INSTANCE multiple times
         * Expected result: Same instance
         */
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

        /**
         * Test objective: Verify convertToDatabaseColumn returns value unchanged
         * Test scenario: Convert string value
         * Expected result: Same string
         */
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

        /**
         * Test objective: Verify convertToDatabaseColumn handles null
         * Test scenario: Convert null
         * Expected result: null
         */
        @Test
        void convertToDatabaseColumn_Null_ReturnsNull() {
            // given
            IdentityValueConverter converter = IdentityValueConverter.of();

            // when
            Object result = converter.convertToDatabaseColumn(null);

            // then
            assertThat(result).isNull();
        }

        /**
         * Test objective: Verify convertToDatabaseColumn handles integer
         * Test scenario: Convert integer
         * Expected result: Same integer
         */
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

        /**
         * Test objective: Verify convertToEntityAttribute returns value unchanged
         * Test scenario: Convert string value
         * Expected result: Same string
         */
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

        /**
         * Test objective: Verify convertToEntityAttribute handles null
         * Test scenario: Convert null
         * Expected result: null
         */
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

        /**
         * Test objective: Verify getDatabaseColumnType returns Object.class for default
         * Test scenario: Get database column type from INSTANCE
         * Expected result: Object.class
         */
        @Test
        void getDatabaseColumnType_Default_ReturnsObject() {
            // given
            IdentityValueConverter converter = IdentityValueConverter.of();

            // when
            Class<?> result = converter.getDatabaseColumnType();

            // then
            assertThat(result).isEqualTo(Object.class);
        }

        /**
         * Test objective: Verify getDatabaseColumnType returns configured type
         * Test scenario: Create converter with specific type
         * Expected result: Configured type
         */
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
