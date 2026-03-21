package io.github.nextentity.jdbc;

import io.github.nextentity.core.meta.IdentityValueConverter;
import io.github.nextentity.core.meta.ValueConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * Unit tests for JdbcArguments.
 */
@ExtendWith(MockitoExtension.class)
class JdbcArgumentsTest {

    @Mock
    private ResultSet resultSet;

    private JdbcArguments arguments;

    @BeforeEach
    void setUp() {
        arguments = new JdbcArguments(resultSet);
    }

    @Nested
    class GetMethod {

        /**
         * Test objective: Verify get() retrieves value from result set.
         * Test scenario: Call get() with index and converter.
         * Expected result: Value is retrieved using 1-based index.
         */
        @Test
        void get_ShouldRetrieveValueFromResultSet() throws SQLException {
            // given
            ValueConverter converter = new IdentityValueConverter(String.class);
            when(resultSet.getObject(1)).thenReturn("test_value");

            // when
            Object result = arguments.get(0, converter);

            // then
            assertThat(result).isEqualTo("test_value");
            verify(resultSet).getObject(1);
        }

        /**
         * Test objective: Verify get() uses 1-based indexing.
         * Test scenario: Call get(5, converter).
         * Expected result: ResultSet is accessed with index 6.
         */
        @Test
        void get_ShouldUseOneBasedIndex() throws SQLException {
            // given
            ValueConverter converter = new IdentityValueConverter(Long.class);
            when(resultSet.getObject(anyInt())).thenReturn(42L);

            // when
            arguments.get(5, converter);

            // then
            verify(resultSet).getObject(6);
        }
    }

    @Nested
    class Inheritance {

        /**
         * Test objective: Verify JdbcArguments extends AbstractArguments.
         * Test scenario: Check inheritance.
         * Expected result: Is instance of AbstractArguments.
         */
        @Test
        void shouldExtendAbstractArguments() {
            assertThat(arguments).isInstanceOf(AbstractArguments.class);
        }
    }
}
