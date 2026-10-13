package io.github.nextentity.jpa;

import io.github.nextentity.core.expression.LiteralNode;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

///
/// 验证字面量的绑定/内联策略。
///
/// 字符串必须绑定，否则 SQL Server 的内联字面量丢失 Unicode 语义。
/// 时间类型必须绑定，否则 Hibernate 用 TimeZone.getDefault() 渲染内联字面量，
/// 与 Instant 默认的 TIMESTAMP_UTC 绑定口径不一致。
/// 其余类型保持内联，避免改变既有 SQL 形态。
///
/// @author HuangChengwei
@DisplayName("JPA 字面量绑定策略测试")
class JpaExpressionBuilderLiteralBindingTest {

    private final CriteriaBuilder cb = mock(CriteriaBuilder.class);

    private final Root<?> root = mock(Root.class);

    private JpaExpressionBuilder newBuilder() {
        return new JpaExpressionBuilder(root, cb, JpaConfig.DEFAULT);
    }

    @Nested
    @DisplayName("当 stringParameterBinding 开启（默认）")
    class WhenBindingEnabled {

        static Stream<Object> boundValues() {
            return Stream.of(
                    "张三",
                    Instant.parse("2026-10-12T21:27:07Z"),
                    LocalDateTime.of(2026, 10, 13, 5, 27, 7),
                    LocalDate.of(2026, 10, 13),
                    LocalTime.of(5, 27, 7),
                    OffsetDateTime.parse("2026-10-13T05:27:07+08:00"),
                    ZonedDateTime.parse("2026-10-13T05:27:07+08:00[Asia/Shanghai]"),
                    new Date(1_760_296_027_000L)
            );
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("boundValues")
        @DisplayName("字符串与时间类型应走参数绑定")
        void stringAndTemporalShouldBeBound(Object value) {
            JpaExpressionBuilder builder = newBuilder();

            builder.toExpression(new LiteralNode(value));

            verify(cb).parameter(eq(value.getClass()), eq("p0"));
            verify(cb, never()).literal(any());
            assertThat(builder.parameterValues).containsExactly(value);
        }

        @Test
        @DisplayName("多个字面量应按出现顺序生成参数名")
        void multipleLiteralsShouldUseSequentialNames() {
            JpaExpressionBuilder builder = newBuilder();
            Instant first = Instant.parse("2026-10-12T21:27:07Z");
            Instant second = Instant.parse("2026-10-13T21:27:07Z");

            builder.toExpression(new LiteralNode(first));
            builder.toExpression(new LiteralNode(second));

            verify(cb).parameter(eq(Instant.class), eq("p0"));
            verify(cb).parameter(eq(Instant.class), eq("p1"));
            assertThat(builder.parameterValues).containsExactly(first, second);
        }

        @Test
        @DisplayName("数值与布尔值应保持内联")
        void numericAndBooleanShouldStayInline() {
            JpaExpressionBuilder builder = newBuilder();

            builder.toExpression(new LiteralNode(42));
            builder.toExpression(new LiteralNode(new BigDecimal("12.50")));
            builder.toExpression(LiteralNode.FALSE);

            verify(cb).literal(42);
            verify(cb).literal(new BigDecimal("12.50"));
            verify(cb).literal(false);
            verify(cb, never()).parameter(any(), anyString());
            assertThat(builder.parameterValues).isEmpty();
        }

        @Test
        @DisplayName("null 字面量应保持内联")
        void nullLiteralShouldStayInline() {
            JpaExpressionBuilder builder = newBuilder();

            builder.toExpression(new LiteralNode(null));

            verify(cb).literal(null);
            verify(cb, never()).parameter(any(), anyString());
            assertThat(builder.parameterValues).isEmpty();
        }
    }

    @Nested
    @DisplayName("当 stringParameterBinding 关闭")
    class WhenBindingDisabled {

        @Test
        @DisplayName("时间字面量应退回内联")
        void temporalShouldFallBackToInline() {
            Instant value = Instant.parse("2026-10-12T21:27:07Z");
            JpaExpressionBuilder builder = new JpaExpressionBuilder(
                    root, cb, JpaConfig.builder().stringParameterBinding(false).build());

            builder.toExpression(new LiteralNode(value));

            verify(cb).literal(value);
            verify(cb, never()).parameter(any(), anyString());
            assertThat(builder.parameterValues).isEmpty();
        }
    }
}
