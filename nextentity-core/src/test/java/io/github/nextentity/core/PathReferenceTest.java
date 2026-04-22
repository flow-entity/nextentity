package io.github.nextentity.core;

import io.github.nextentity.integration.entity.Employee;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

///
/// 测试目标: 验证y PathReference 正确 resolves field names
/// <p>
/// 测试场景s:
/// 1. getFieldName converts 方法 names 正确
/// 2. getFieldName 处理 edge cases
/// 3. of() creates PathReference for valid paths
/// 4. clearCache() clears the internal cache
class PathReferenceTest {

    @Nested
    class GetFieldName {

        ///
        /// 测试目标: 验证y getFieldName converts getter 方法
        /// 测试场景: Convert "getName" to "name"
        /// 预期结果: "name"
        @Test
        void getFieldName_Getter_ReturnsFieldName() {
            // when
            String result = PathReference.getFieldName("getName");

            // then
            assertThat(result).isEqualTo("name");
        }

        ///
        /// 测试目标: 验证y getFieldName converts is 方法
        /// 测试场景: Convert "isActive" to "active"
        /// 预期结果: "active"
        @Test
        void getFieldName_IsMethod_ReturnsFieldName() {
            // when
            String result = PathReference.getFieldName("isActive");

            // then
            assertThat(result).isEqualTo("active");
        }

        ///
        /// 测试目标: 验证y getFieldName 处理 single char after get
        /// 测试场景: Convert "getX" to "x"
        /// 预期结果: "x"
        @Test
        void getFieldName_SingleChar_ReturnsLowercase() {
            // when
            String result = PathReference.getFieldName("getX");

            // then
            assertThat(result).isEqualTo("x");
        }

        ///
        /// 测试目标: 验证y getFieldName preserves uppercase for acronyms
        /// 测试场景: Convert "getURL" to "URL"
        /// 预期结果: "URL"
        @Test
        void getFieldName_Acronym_PreservesUppercase() {
            // when
            String result = PathReference.getFieldName("getURL");

            // then
            assertThat(result).isEqualTo("URL");
        }

        ///
        /// 测试目标: 验证y getFieldName 处理 mixed case
        /// 测试场景: Convert "getHTTPResponse" to "HTTPResponse"
        /// 预期结果: "HTTPResponse"
        @Test
        void getFieldName_MixedCase_PreservesUppercase() {
            // when
            String result = PathReference.getFieldName("getHTTPResponse");

            // then
            assertThat(result).isEqualTo("HTTPResponse");
        }

        ///
        /// 测试目标: 验证y getFieldName returns unchanged for non-getter
        /// 测试场景: Pass "name" directly
        /// 预期结果: "name"
        @Test
        void getFieldName_NonGetter_ReturnsUnchanged() {
            // when
            String result = PathReference.getFieldName("name");

            // then
            assertThat(result).isEqualTo("name");
        }

        ///
        /// 测试目标: 验证y getFieldName 处理 short 方法 names
        /// 测试场景: Pass "ge" (shorter than "get")
        /// 预期结果: "ge"
        @Test
        void getFieldName_ShortName_ReturnsUnchanged() {
            // when
            String result = PathReference.getFieldName("ge");

            // then
            assertThat(result).isEqualTo("ge");
        }

        ///
        /// 测试目标: 验证y getFieldName 处理 null
        /// 测试场景: Pass null
        /// 预期结果: NullPointerException
        @Test
        void getFieldName_Null_ThrowsException() {
            // when & then
            assertThatThrownBy(() -> PathReference.getFieldName(null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    class OfMethod {

        ///
        /// 测试目标: 验证y of() creates PathReference with correct field name
        /// 测试场景: Create PathReference for Employee::getName
        /// 预期结果: Field name is "name"
        @Test
        void of_WithValidPath_ShouldReturnCorrectFieldName() {
            // when
            PathReference ref = PathReference.of(Employee::getName);

            // then
            assertThat(ref.getFieldName()).isEqualTo("name");
        }

        ///
        /// 测试目标: 验证y of() creates PathReference with correct return type
        /// 测试场景: Create PathReference for Employee::getSalary
        /// 预期结果: Return type is Double
        @Test
        void of_WithValidPath_ShouldReturnCorrectReturnType() {
            // when
            PathReference ref = PathReference.of(Employee::getSalary);

            // then
            assertThat(ref.getReturnType()).isEqualTo(Double.class);
        }

        ///
        /// 测试目标: 验证y of() creates PathReference with correct entity type
        /// 测试场景: Create PathReference for Employee path
        /// 预期结果: Entity type is Employee
        @Test
        void of_WithValidPath_ShouldReturnCorrectEntityType() {
            // when
            PathReference ref = PathReference.of(Employee::getName);

            // then
            assertThat(ref.getEntityType()).isEqualTo(Employee.class);
        }

        ///
        /// 测试目标: 验证y of() throws NPE for null path
        /// 测试场景: Pass null to of()
        /// 预期结果: NullPointerException thrown
        @Test
        void of_WithNullPath_ShouldThrowNPE() {
            // when & then
            assertThatThrownBy(() -> PathReference.of(null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    class ClearCache {

        ///
        /// 测试目标: 验证y clearCache() 方法 executes without error
        /// 测试场景: Call clearCache() after using the cache
        /// 预期结果: Method completes successfully
        /// <p>
        /// Note: This test verifies the 方法 is callable. The actual cache 行为
        /// is an implementation detail that may vary.
        @Test
        void clearCache_AfterCacheUsage_ShouldNotThrow() {
            // given - use cache
            PathReference.of(Employee::getName);

            // when & then - clearCache should not throw
            org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> {
                PathReference.clearCache();
            });

            // verify we can still create new PathReference after clear
            PathReference ref = PathReference.of(Employee::getName);
            assertThat(ref).isNotNull();
            assertThat(ref.getFieldName()).isEqualTo("name");
        }
    }
}
