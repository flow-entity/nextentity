package io.github.nextentity.meta.jpa;

import io.github.nextentity.core.meta.ValueConverter;
import jakarta.persistence.AttributeConverter;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Test objective: Verify AttributeConverterWrapper correctly wraps JPA AttributeConverter
 * <p>
 * Test scenarios:
 * 1. Wrap non-ValueConverter AttributeConverter
 * 2. Return existing ValueConverter directly
 * 3. Delegate conversion methods
 */
class AttributeConverterWrapperTest {

    @Nested
    class FactoryMethod {

        @Test
        void of_ValueConverter_ReturnsSameInstance() {
            // given
            ValueConverter<String, String> converter = new TestValueConverter();

            // when
            ValueConverter<?, ?> result = AttributeConverterWrapper.of((AttributeConverter<?, ?>) converter);

            // then
            assertThat(result).isSameAs(converter);
        }

        @Test
        void of_NonValueConverter_ReturnsWrapper() {
            // given
            AttributeConverter<String, Integer> converter = mock(AttributeConverter.class);

            // when
            ValueConverter<?, ?> result = AttributeConverterWrapper.of(converter);

            // then
            assertThat(result).isInstanceOf(AttributeConverterWrapper.class);
        }
    }

    @Nested
    class ConversionDelegation {

        @SuppressWarnings("unchecked")
        @Test
        void convertToDatabaseColumn_DelegatesToWrappedConverter() {
            // given
            AttributeConverter<String, Integer> converter = mock(AttributeConverter.class);
            doReturn(42).when(converter).convertToDatabaseColumn("test");

            ValueConverter<String, Integer> wrapper = (ValueConverter<String, Integer>) AttributeConverterWrapper.of(converter);

            // when
            Integer result = wrapper.convertToDatabaseColumn("test");

            // then
            assertThat(result).isEqualTo(42);
            verify(converter).convertToDatabaseColumn("test");
        }

        @SuppressWarnings("unchecked")
        @Test
        void convertToEntityAttribute_DelegatesToWrappedConverter() {
            // given
            AttributeConverter<String, Integer> converter = mock(AttributeConverter.class);
            doReturn("test").when(converter).convertToEntityAttribute(42);

            ValueConverter<String, Integer> wrapper = (ValueConverter<String, Integer>) AttributeConverterWrapper.of(converter);

            // when
            String result = wrapper.convertToEntityAttribute(42);

            // then
            assertThat(result).isEqualTo("test");
            verify(converter).convertToEntityAttribute(42);
        }
    }

    /**
     * Test implementation that implements both interfaces
     */
    private static class TestValueConverter implements ValueConverter<String, String>, AttributeConverter<String, String> {
        @Override
        public String convertToDatabaseColumn(String attribute) {
            return attribute == null ? null : attribute.toUpperCase();
        }

        @Override
        public String convertToEntityAttribute(String dbData) {
            return dbData == null ? null : dbData.toLowerCase();
        }
    }
}
