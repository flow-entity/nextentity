package io.github.nextentity.core.expression;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

///
/// 测试目标: 验证y Operator enum defines all operators 正确
/// <p>
/// 测试场景s:
/// 1. Sign (symbol) values
/// 2. Priority values
/// 3. Multivalued flag
/// 4. Aggregate flag
class OperatorTest {

    @Nested
    class SignValues {

        ///
        /// 测试目标: 验证y comparison operator signs
        /// 测试场景: Get sign of comparison operators
        /// 预期结果: Correct symbol
        @Test
        void sign_ComparisonOperators() {
            assertThat(Operator.EQ.sign()).isEqualTo("=");
            assertThat(Operator.NE.sign()).isEqualTo("!=");
            assertThat(Operator.GT.sign()).isEqualTo(">");
            assertThat(Operator.GE.sign()).isEqualTo(">=");
            assertThat(Operator.LT.sign()).isEqualTo("<");
            assertThat(Operator.LE.sign()).isEqualTo("<=");
        }

        ///
        /// 测试目标: 验证y logical operator signs
        /// 测试场景: Get sign of logical operators
        /// 预期结果: Correct symbol
        @Test
        void sign_LogicalOperators() {
            assertThat(Operator.NOT.sign()).isEqualTo("not");
            assertThat(Operator.AND.sign()).isEqualTo("and");
            assertThat(Operator.OR.sign()).isEqualTo("or");
        }

        ///
        /// 测试目标: 验证y arithmetic operator signs
        /// 测试场景: Get sign of arithmetic operators
        /// 预期结果: Correct symbol
        @Test
        void sign_ArithmeticOperators() {
            assertThat(Operator.ADD.sign()).isEqualTo("+");
            assertThat(Operator.SUBTRACT.sign()).isEqualTo("-");
            assertThat(Operator.MULTIPLY.sign()).isEqualTo("*");
            assertThat(Operator.DIVIDE.sign()).isEqualTo("/");
            assertThat(Operator.MOD.sign()).isEqualTo("%");
        }

        ///
        /// 测试目标: 验证y aggregate function signs
        /// 测试场景: Get sign of aggregate operators
        /// 预期结果: Correct function name
        @Test
        void sign_AggregateOperators() {
            assertThat(Operator.MIN.sign()).isEqualTo("min");
            assertThat(Operator.MAX.sign()).isEqualTo("max");
            assertThat(Operator.COUNT.sign()).isEqualTo("count");
            assertThat(Operator.AVG.sign()).isEqualTo("avg");
            assertThat(Operator.SUM.sign()).isEqualTo("sum");
        }
    }

    @Nested
    class PriorityValues {

        ///
        /// 测试目标: 验证y NOT has highest logical priority
        /// 测试场景: Compare NOT priority
        /// 预期结果: NOT has higher priority than AND
        @Test
        void priority_NotHigherThanAnd() {
            assertThat(Operator.NOT.priority()).isLessThan(Operator.AND.priority());
        }

        ///
        /// 测试目标: 验证y AND has higher priority than OR
        /// 测试场景: Compare AND and OR priorities
        /// 预期结果: AND has higher priority
        @Test
        void priority_AndHigherThanOr() {
            assertThat(Operator.AND.priority()).isLessThan(Operator.OR.priority());
        }

        ///
        /// 测试目标: 验证y arithmetic priorities
        /// 测试场景: Compare arithmetic operator priorities
        /// 预期结果: Multiply/Divide/Mod have higher priority than Add/Subtract
        @Test
        void priority_MultiplyHigherThanAdd() {
            assertThat(Operator.MULTIPLY.priority()).isLessThan(Operator.ADD.priority());
            assertThat(Operator.DIVIDE.priority()).isLessThan(Operator.ADD.priority());
            assertThat(Operator.MOD.priority()).isLessThan(Operator.ADD.priority());
        }
    }

    @Nested
    class MultivaluedFlag {

        ///
        /// 测试目标: 验证y AND is multivalued
        /// 测试场景: Check isMultivalued() for AND
        /// 预期结果: true
        @Test
        void isMultivalued_And_ReturnsTrue() {
            assertThat(Operator.AND.isMultivalued()).isTrue();
        }

        ///
        /// 测试目标: 验证y OR is multivalued
        /// 测试场景: Check isMultivalued() for OR
        /// 预期结果: true
        @Test
        void isMultivalued_Or_ReturnsTrue() {
            assertThat(Operator.OR.isMultivalued()).isTrue();
        }

        ///
        /// 测试目标: 验证y arithmetic operators are multivalued
        /// 测试场景: Check isMultivalued() for arithmetic operators
        /// 预期结果: true
        @Test
        void isMultivalued_Arithmetic_ReturnsTrue() {
            assertThat(Operator.ADD.isMultivalued()).isTrue();
            assertThat(Operator.SUBTRACT.isMultivalued()).isTrue();
            assertThat(Operator.MULTIPLY.isMultivalued()).isTrue();
            assertThat(Operator.DIVIDE.isMultivalued()).isTrue();
            assertThat(Operator.MOD.isMultivalued()).isTrue();
        }

        ///
        /// 测试目标: 验证y EQ is not multivalued
        /// 测试场景: Check isMultivalued() for EQ
        /// 预期结果: false
        @Test
        void isMultivalued_Eq_ReturnsFalse() {
            assertThat(Operator.EQ.isMultivalued()).isFalse();
        }

        ///
        /// 测试目标: 验证y NOT is not multivalued
        /// 测试场景: Check isMultivalued() for NOT
        /// 预期结果: false
        @Test
        void isMultivalued_Not_ReturnsFalse() {
            assertThat(Operator.NOT.isMultivalued()).isFalse();
        }
    }

    @Nested
    class AggregateFlag {

        ///
        /// 测试目标: 验证y aggregate operators have agg flag
        /// 测试场景: Check isAgg() for aggregate operators
        /// 预期结果: true
        @Test
        void isAgg_AggregateOperators_ReturnsTrue() {
            assertThat(Operator.MIN.isAgg()).isTrue();
            assertThat(Operator.MAX.isAgg()).isTrue();
            assertThat(Operator.COUNT.isAgg()).isTrue();
            assertThat(Operator.AVG.isAgg()).isTrue();
            assertThat(Operator.SUM.isAgg()).isTrue();
        }

        ///
        /// 测试目标: 验证y non-aggregate operators have no agg flag
        /// 测试场景: Check isAgg() for non-aggregate operators
        /// 预期结果: false
        @Test
        void isAgg_NonAggregateOperators_ReturnsFalse() {
            assertThat(Operator.EQ.isAgg()).isFalse();
            assertThat(Operator.AND.isAgg()).isFalse();
            assertThat(Operator.ADD.isAgg()).isFalse();
        }
    }

    @Nested
    class ToStringMethod {

        ///
        /// 测试目标: 验证y toString returns sign
        /// 测试场景: Call toString() on operators
        /// 预期结果: Returns sign
        @ParameterizedTest
        @EnumSource(Operator.class)
        void toString_ReturnsSign(Operator operator) {
            assertThat(operator.toString()).isEqualTo(operator.sign());
        }
    }
}
