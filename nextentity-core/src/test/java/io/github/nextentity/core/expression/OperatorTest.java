package io.github.nextentity.core.expression;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test objective: Verify Operator enum defines all operators correctly
 * <p>
 * Test scenarios:
 * 1. Sign (symbol) values
 * 2. Priority values
 * 3. Multivalued flag
 * 4. Aggregate flag
 */
class OperatorTest {

    @Nested
    class SignValues {

        /**
         * Test objective: Verify comparison operator signs
         * Test scenario: Get sign of comparison operators
         * Expected result: Correct symbol
         */
        @Test
        void sign_ComparisonOperators() {
            assertThat(Operator.EQ.sign()).isEqualTo("=");
            assertThat(Operator.NE.sign()).isEqualTo("!=");
            assertThat(Operator.GT.sign()).isEqualTo(">");
            assertThat(Operator.GE.sign()).isEqualTo(">=");
            assertThat(Operator.LT.sign()).isEqualTo("<");
            assertThat(Operator.LE.sign()).isEqualTo("<=");
        }

        /**
         * Test objective: Verify logical operator signs
         * Test scenario: Get sign of logical operators
         * Expected result: Correct symbol
         */
        @Test
        void sign_LogicalOperators() {
            assertThat(Operator.NOT.sign()).isEqualTo("not");
            assertThat(Operator.AND.sign()).isEqualTo("and");
            assertThat(Operator.OR.sign()).isEqualTo("or");
        }

        /**
         * Test objective: Verify arithmetic operator signs
         * Test scenario: Get sign of arithmetic operators
         * Expected result: Correct symbol
         */
        @Test
        void sign_ArithmeticOperators() {
            assertThat(Operator.ADD.sign()).isEqualTo("+");
            assertThat(Operator.SUBTRACT.sign()).isEqualTo("-");
            assertThat(Operator.MULTIPLY.sign()).isEqualTo("*");
            assertThat(Operator.DIVIDE.sign()).isEqualTo("/");
            assertThat(Operator.MOD.sign()).isEqualTo("%");
        }

        /**
         * Test objective: Verify aggregate function signs
         * Test scenario: Get sign of aggregate operators
         * Expected result: Correct function name
         */
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

        /**
         * Test objective: Verify NOT has highest logical priority
         * Test scenario: Compare NOT priority
         * Expected result: NOT has higher priority than AND
         */
        @Test
        void priority_NotHigherThanAnd() {
            assertThat(Operator.NOT.priority()).isLessThan(Operator.AND.priority());
        }

        /**
         * Test objective: Verify AND has higher priority than OR
         * Test scenario: Compare AND and OR priorities
         * Expected result: AND has higher priority
         */
        @Test
        void priority_AndHigherThanOr() {
            assertThat(Operator.AND.priority()).isLessThan(Operator.OR.priority());
        }

        /**
         * Test objective: Verify arithmetic priorities
         * Test scenario: Compare arithmetic operator priorities
         * Expected result: Multiply/Divide/Mod have higher priority than Add/Subtract
         */
        @Test
        void priority_MultiplyHigherThanAdd() {
            assertThat(Operator.MULTIPLY.priority()).isLessThan(Operator.ADD.priority());
            assertThat(Operator.DIVIDE.priority()).isLessThan(Operator.ADD.priority());
            assertThat(Operator.MOD.priority()).isLessThan(Operator.ADD.priority());
        }
    }

    @Nested
    class MultivaluedFlag {

        /**
         * Test objective: Verify AND is multivalued
         * Test scenario: Check isMultivalued() for AND
         * Expected result: true
         */
        @Test
        void isMultivalued_And_ReturnsTrue() {
            assertThat(Operator.AND.isMultivalued()).isTrue();
        }

        /**
         * Test objective: Verify OR is multivalued
         * Test scenario: Check isMultivalued() for OR
         * Expected result: true
         */
        @Test
        void isMultivalued_Or_ReturnsTrue() {
            assertThat(Operator.OR.isMultivalued()).isTrue();
        }

        /**
         * Test objective: Verify arithmetic operators are multivalued
         * Test scenario: Check isMultivalued() for arithmetic operators
         * Expected result: true
         */
        @Test
        void isMultivalued_Arithmetic_ReturnsTrue() {
            assertThat(Operator.ADD.isMultivalued()).isTrue();
            assertThat(Operator.SUBTRACT.isMultivalued()).isTrue();
            assertThat(Operator.MULTIPLY.isMultivalued()).isTrue();
            assertThat(Operator.DIVIDE.isMultivalued()).isTrue();
            assertThat(Operator.MOD.isMultivalued()).isTrue();
        }

        /**
         * Test objective: Verify EQ is not multivalued
         * Test scenario: Check isMultivalued() for EQ
         * Expected result: false
         */
        @Test
        void isMultivalued_Eq_ReturnsFalse() {
            assertThat(Operator.EQ.isMultivalued()).isFalse();
        }

        /**
         * Test objective: Verify NOT is not multivalued
         * Test scenario: Check isMultivalued() for NOT
         * Expected result: false
         */
        @Test
        void isMultivalued_Not_ReturnsFalse() {
            assertThat(Operator.NOT.isMultivalued()).isFalse();
        }
    }

    @Nested
    class AggregateFlag {

        /**
         * Test objective: Verify aggregate operators have agg flag
         * Test scenario: Check isAgg() for aggregate operators
         * Expected result: true
         */
        @Test
        void isAgg_AggregateOperators_ReturnsTrue() {
            assertThat(Operator.MIN.isAgg()).isTrue();
            assertThat(Operator.MAX.isAgg()).isTrue();
            assertThat(Operator.COUNT.isAgg()).isTrue();
            assertThat(Operator.AVG.isAgg()).isTrue();
            assertThat(Operator.SUM.isAgg()).isTrue();
        }

        /**
         * Test objective: Verify non-aggregate operators have no agg flag
         * Test scenario: Check isAgg() for non-aggregate operators
         * Expected result: false
         */
        @Test
        void isAgg_NonAggregateOperators_ReturnsFalse() {
            assertThat(Operator.EQ.isAgg()).isFalse();
            assertThat(Operator.AND.isAgg()).isFalse();
            assertThat(Operator.ADD.isAgg()).isFalse();
        }
    }

    @Nested
    class ToStringMethod {

        /**
         * Test objective: Verify toString returns sign
         * Test scenario: Call toString() on operators
         * Expected result: Returns sign
         */
        @ParameterizedTest
        @EnumSource(Operator.class)
        void toString_ReturnsSign(Operator operator) {
            assertThat(operator.toString()).isEqualTo(operator.sign());
        }
    }
}
