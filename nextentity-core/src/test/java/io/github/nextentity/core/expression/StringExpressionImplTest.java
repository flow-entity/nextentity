package io.github.nextentity.core.expression;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test objective: Verify StringExpressionImpl correctly implements string operations
 * <p>
 * Test scenarios:
 * 1. lower() operation
 * 2. upper() operation
 * 3. substring() operation
 * 4. trim() operation
 * 5. length() operation
 */
class StringExpressionImplTest {

    @Nested
    class StringOperations {

        /**
         * Test objective: Verify lower() creates LOWER operator
         * Test scenario: Call lower() on expression
         * Expected result: Expression with LOWER operator
         */
        @Test
        void lower_CreatesLowerOperator() {
            // given
            PathNode pathNode = new PathNode("name");
            StringExpressionImpl<Object> expr = new StringExpressionImpl<>(pathNode);

            // when
            var result = expr.lower();

            // then
            assertThat(result).isNotNull();
            ExpressionTree tree = (ExpressionTree) result;
            ExpressionNode root = tree.getRoot();
            assertThat(root).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) root;
            assertThat(opNode.operator()).isEqualTo(Operator.LOWER);
        }

        /**
         * Test objective: Verify upper() creates UPPER operator
         * Test scenario: Call upper() on expression
         * Expected result: Expression with UPPER operator
         */
        @Test
        void upper_CreatesUpperOperator() {
            // given
            PathNode pathNode = new PathNode("name");
            StringExpressionImpl<Object> expr = new StringExpressionImpl<>(pathNode);

            // when
            var result = expr.upper();

            // then
            assertThat(result).isNotNull();
            ExpressionTree tree = (ExpressionTree) result;
            ExpressionNode root = tree.getRoot();
            assertThat(root).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) root;
            assertThat(opNode.operator()).isEqualTo(Operator.UPPER);
        }

        /**
         * Test objective: Verify trim() creates TRIM operator
         * Test scenario: Call trim() on expression
         * Expected result: Expression with TRIM operator
         */
        @Test
        void trim_CreatesTrimOperator() {
            // given
            PathNode pathNode = new PathNode("name");
            StringExpressionImpl<Object> expr = new StringExpressionImpl<>(pathNode);

            // when
            var result = expr.trim();

            // then
            assertThat(result).isNotNull();
            ExpressionTree tree = (ExpressionTree) result;
            ExpressionNode root = tree.getRoot();
            assertThat(root).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) root;
            assertThat(opNode.operator()).isEqualTo(Operator.TRIM);
        }

        /**
         * Test objective: Verify substring() creates SUBSTRING operator
         * Test scenario: Call substring() on expression
         * Expected result: Expression with SUBSTRING operator
         */
        @Test
        void substring_CreatesSubstringOperator() {
            // given
            PathNode pathNode = new PathNode("name");
            StringExpressionImpl<Object> expr = new StringExpressionImpl<>(pathNode);

            // when
            var result = expr.substring(0, 10);

            // then
            assertThat(result).isNotNull();
            ExpressionTree tree = (ExpressionTree) result;
            ExpressionNode root = tree.getRoot();
            assertThat(root).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) root;
            assertThat(opNode.operator()).isEqualTo(Operator.SUBSTRING);
        }

        /**
         * Test objective: Verify length() creates LENGTH operator
         * Test scenario: Call length() on expression
         * Expected result: Expression with LENGTH operator
         */
        @Test
        void length_CreatesLengthOperator() {
            // given
            PathNode pathNode = new PathNode("name");
            StringExpressionImpl<Object> expr = new StringExpressionImpl<>(pathNode);

            // when
            var result = expr.length();

            // then
            assertThat(result).isNotNull();
            ExpressionTree tree = (ExpressionTree) result;
            ExpressionNode root = tree.getRoot();
            assertThat(root).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) root;
            assertThat(opNode.operator()).isEqualTo(Operator.LENGTH);
        }
    }
}
