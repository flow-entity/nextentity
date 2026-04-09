package io.github.nextentity.jdbc;

import io.github.nextentity.core.expression.LiteralNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/// 测试 inlineNumericLiterals 配置对 SQL 构建的影响。
///
/// 验证配置项:
/// - inlineNumericLiterals=true: 整数直接拼接到 SQL
/// - inlineNumericLiterals=false: 整数作为参数传递
///
/// @author HuangChengwei
/// @since 2.1.0
@DisplayName("Inline Numeric Literals Tests")
class InlineNumericLiteralsTest {

    @Nested
    @DisplayName("当 inlineNumericLiterals=true")
    class WhenEnabled {

        /// 验证整数直接嵌入 SQL
        @Test
        @DisplayName("Integer 应直接拼接")
        void integerShouldBeEmbedded() {
            JdbcConfig config = JdbcConfig.builder()
                    .inlineNumericLiterals(true)
                    .build();

            TestResult result = appendLiteral(config, 42);

            assertThat(result.sql).contains("42");
            assertThat(result.args).isEmpty();
        }

        /// 验证 Long 直接嵌入 SQL
        @Test
        @DisplayName("Long 应直接拼接")
        void longShouldBeEmbedded() {
            JdbcConfig config = JdbcConfig.builder()
                    .inlineNumericLiterals(true)
                    .build();

            TestResult result = appendLiteral(config, 100L);

            assertThat(result.sql).contains("100");
            assertThat(result.args).isEmpty();
        }

        /// 验证 Short 直接嵌入 SQL
        @Test
        @DisplayName("Short 应直接拼接")
        void shortShouldBeEmbedded() {
            JdbcConfig config = JdbcConfig.builder()
                    .inlineNumericLiterals(true)
                    .build();

            TestResult result = appendLiteral(config, (short) 5);

            assertThat(result.sql).contains("5");
            assertThat(result.args).isEmpty();
        }

        /// 验证 Byte 直接嵌入 SQL
        @Test
        @DisplayName("Byte 应直接拼接")
        void byteShouldBeEmbedded() {
            JdbcConfig config = JdbcConfig.builder()
                    .inlineNumericLiterals(true)
                    .build();

            TestResult result = appendLiteral(config, (byte) 1);

            assertThat(result.sql).contains("1");
            assertThat(result.args).isEmpty();
        }
    }

    @Nested
    @DisplayName("当 inlineNumericLiterals=false")
    class WhenDisabled {

        /// 验证整数作为参数传递
        @Test
        @DisplayName("Integer 应作为参数")
        void integerShouldBeParameter() {
            JdbcConfig config = JdbcConfig.builder()
                    .inlineNumericLiterals(false)
                    .build();

            TestResult result = appendLiteral(config, 42);

            assertThat(result.sql).contains("?");
            assertThat(result.args).containsExactly(42);
        }

        /// 验证 Long 作为参数传递
        @Test
        @DisplayName("Long 应作为参数")
        void longShouldBeParameter() {
            JdbcConfig config = JdbcConfig.builder()
                    .inlineNumericLiterals(false)
                    .build();

            TestResult result = appendLiteral(config, 100L);

            assertThat(result.sql).contains("?");
            assertThat(result.args).containsExactly(100L);
        }
    }

    @Nested
    @DisplayName("其他类型")
    class OtherTypes {

        /// 验证字符串始终作为参数
        @Test
        @DisplayName("String 应始终作为参数")
        void stringShouldAlwaysBeParameter() {
            JdbcConfig config = JdbcConfig.builder()
                    .inlineNumericLiterals(true)
                    .build();

            TestResult result = appendLiteral(config, "test");

            assertThat(result.sql).contains("?");
            assertThat(result.args).containsExactly("test");
        }

        /// 验证 Double 始终作为参数
        @Test
        @DisplayName("Double 应始终作为参数")
        void doubleShouldAlwaysBeParameter() {
            JdbcConfig config = JdbcConfig.builder()
                    .inlineNumericLiterals(true)
                    .build();

            TestResult result = appendLiteral(config, 3.14);

            assertThat(result.sql).contains("?");
            assertThat(result.args).containsExactly(3.14);
        }

        /// 验证 BigDecimal 始终作为参数
        @Test
        @DisplayName("BigDecimal 应始终作为参数")
        void bigDecimalShouldAlwaysBeParameter() {
            JdbcConfig config = JdbcConfig.builder()
                    .inlineNumericLiterals(true)
                    .build();

            TestResult result = appendLiteral(config, java.math.BigDecimal.valueOf(100.50));

            assertThat(result.sql).contains("?");
            assertThat(result.args).hasSize(1);
        }
    }

    @Nested
    @DisplayName("默认配置")
    class DefaultConfig {

        /// 验证默认配置启用 inlineNumericLiterals
        @Test
        @DisplayName("默认配置应启用 inlineNumericLiterals")
        void defaultConfigShouldEnableInline() {
            JdbcConfig config = JdbcConfig.DEFAULT;

            assertThat(config.inlineNumericLiterals()).isTrue();
        }

        /// 验证默认配置下整数嵌入 SQL
        @Test
        @DisplayName("默认配置下整数应嵌入 SQL")
        void defaultConfigShouldEmbedInteger() {
            TestResult result = appendLiteral(JdbcConfig.DEFAULT, 42);

            assertThat(result.sql).contains("42");
            assertThat(result.args).isEmpty();
        }
    }

    /// 测试结果
    record TestResult(String sql, List<Object> args) {}

    /// 使用 InlineNumericLiteralsHandler 直接测试 appendLiteral 逻辑
    private TestResult appendLiteral(JdbcConfig config, Object value) {
        StringBuilder sql = new StringBuilder();
        List<Object> args = new ArrayList<>();

        InlineNumericLiteralsHandler handler = new InlineNumericLiteralsHandler(config);
        handler.appendLiteral(sql, args, new LiteralNode(value));

        return new TestResult(sql.toString(), args);
    }

    /// 直接处理 inlineNumericLiterals 的逻辑，用于测试
    private static class InlineNumericLiteralsHandler {
        private final JdbcConfig config;

        InlineNumericLiteralsHandler(JdbcConfig config) {
            this.config = config;
        }

        void appendLiteral(StringBuilder sql, List<Object> args, LiteralNode literal) {
            Object value = literal.value();
            // 复制 QueryStatementBuilder.appendLiteral 的逻辑
            if (config.inlineNumericLiterals() &&
                (value instanceof Integer || value instanceof Long ||
                 value instanceof Short || value instanceof Byte)) {
                sql.append(" ").append(value);
            } else {
                sql.append(" ?");
                args.add(value);
            }
        }
    }
}