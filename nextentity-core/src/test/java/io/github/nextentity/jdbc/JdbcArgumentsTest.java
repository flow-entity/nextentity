//package io.github.nextentity.jdbc;
//
//import io.github.nextentity.core.meta.IdentityValueConverter;
//import io.github.nextentity.core.meta.ValueConverter;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.sql.ResultSet;
//import java.sql.SQLException;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyInt;
//import static org.mockito.Mockito.*;
//
/////
// /// 单元测试 JdbcArguments.
//@ExtendWith(MockitoExtension.class)
//class JdbcArgumentsTest {
//
//    @Mock
//    private ResultSet resultSet;
//
//    private JdbcArguments arguments;
//
//    @BeforeEach
//    void setUp() {
//        arguments = new JdbcArguments(resultSet);
//    }
//
//    @Nested
//    class GetMethod {
//
/////
//         /// 测试目标: 验证y get() retrieves value from result set.
//         /// 测试场景: Call get() with index and converter.
//         /// 预期结果: Value is retrieved using 1-based index.
//        @Test
//        void get_ShouldRetrieveValueFromResultSet() throws SQLException {
//            // given
//            ValueConverter converter = new IdentityValueConverter(String.class);
//            when(resultSet.getObject(1)).thenReturn("test_value");
//
//            // when
//            Object result = arguments.get(0, converter);
//
//            // then
//            assertThat(result).isEqualTo("test_value");
//            verify(resultSet).getObject(1);
//        }
//
/////
//         /// 测试目标: 验证y get() uses 1-based indexing.
//         /// 测试场景: Call get(5, converter).
//         /// 预期结果: 结果Set is accessed with index 6.
//        @Test
//        void get_ShouldUseOneBasedIndex() throws SQLException {
//            // given
//            ValueConverter converter = new IdentityValueConverter(Long.class);
//            when(resultSet.getObject(anyInt())).thenReturn(42L);
//
//            // when
//            arguments.get(5, converter);
//
//            // then
//            verify(resultSet).getObject(6);
//        }
//    }
//
//    @Nested
//    class Inheritance {
//
/////
//         /// 测试目标: 验证y JdbcArguments extends AbstractArguments.
//         /// 测试场景: Check inheritance.
//         /// 预期结果: Is instance of AbstractArguments.
//        @Test
//        void shouldExtendAbstractArguments() {
//            assertThat(arguments).isInstanceOf(AbstractArguments.class);
//        }
//    }
//}
