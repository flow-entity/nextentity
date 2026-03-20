package io.github.nextentity.core;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test objective: Verify PathReference correctly resolves field names
 * <p>
 * Test scenarios:
 * 1. getFieldName converts method names correctly
 * 2. getFieldName handles edge cases
 */
class PathReferenceTest {

    @Nested
    class GetFieldName {

        /**
         * Test objective: Verify getFieldName converts getter methods
         * Test scenario: Convert "getName" to "name"
         * Expected result: "name"
         */
        @Test
        void getFieldName_Getter_ReturnsFieldName() {
            // when
            String result = PathReference.getFieldName("getName");

            // then
            assertThat(result).isEqualTo("name");
        }

        /**
         * Test objective: Verify getFieldName converts is methods
         * Test scenario: Convert "isActive" to "active"
         * Expected result: "active"
         */
        @Test
        void getFieldName_IsMethod_ReturnsFieldName() {
            // when
            String result = PathReference.getFieldName("isActive");

            // then
            assertThat(result).isEqualTo("active");
        }

        /**
         * Test objective: Verify getFieldName handles single char after get
         * Test scenario: Convert "getX" to "x"
         * Expected result: "x"
         */
        @Test
        void getFieldName_SingleChar_ReturnsLowercase() {
            // when
            String result = PathReference.getFieldName("getX");

            // then
            assertThat(result).isEqualTo("x");
        }

        /**
         * Test objective: Verify getFieldName preserves uppercase for acronyms
         * Test scenario: Convert "getURL" to "URL"
         * Expected result: "URL"
         */
        @Test
        void getFieldName_Acronym_PreservesUppercase() {
            // when
            String result = PathReference.getFieldName("getURL");

            // then
            assertThat(result).isEqualTo("URL");
        }

        /**
         * Test objective: Verify getFieldName handles mixed case
         * Test scenario: Convert "getHTTPResponse" to "HTTPResponse"
         * Expected result: "HTTPResponse"
         */
        @Test
        void getFieldName_MixedCase_PreservesUppercase() {
            // when
            String result = PathReference.getFieldName("getHTTPResponse");

            // then
            assertThat(result).isEqualTo("HTTPResponse");
        }

        /**
         * Test objective: Verify getFieldName returns unchanged for non-getter
         * Test scenario: Pass "name" directly
         * Expected result: "name"
         */
        @Test
        void getFieldName_NonGetter_ReturnsUnchanged() {
            // when
            String result = PathReference.getFieldName("name");

            // then
            assertThat(result).isEqualTo("name");
        }

        /**
         * Test objective: Verify getFieldName handles short method names
         * Test scenario: Pass "ge" (shorter than "get")
         * Expected result: "ge"
         */
        @Test
        void getFieldName_ShortName_ReturnsUnchanged() {
            // when
            String result = PathReference.getFieldName("ge");

            // then
            assertThat(result).isEqualTo("ge");
        }

        /**
         * Test objective: Verify getFieldName handles null
         * Test scenario: Pass null
         * Expected result: NullPointerException
         */
        @Test
        void getFieldName_Null_ThrowsException() {
            // when & then
            assertThatThrownBy(() -> PathReference.getFieldName(null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    class ClearCache {

        /**
         * Test objective: Verify clearCache() works without error
         * Test scenario: Call clearCache()
         * Expected result: No exception
         */
        @Test
        void clearCache_NoError() {
            // when & then - should not throw
            PathReference.clearCache();
        }
    }
}
