package io.github.nextentity.core.expression;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

///
 /// 测试目标: 验证y PredicateImpl 正确 implements predicate 操作s
 /// <p>
 /// 测试场景s:
 /// 1. Create with expression node
 /// 2. Operate null
 /// 3. Convert to predicate
class PredicateImplTest {

    @Nested
    class Creation {

        @Test
        void predicateImpl_CreatesWithNode() {
            // given
            LiteralNode node = new LiteralNode(true);

            // when
            PredicateImpl<Object> predicate = new PredicateImpl<>(node);

            // then
            assertThat(predicate).isNotNull();
        }
    }

    @Nested
    class EmptyPredicate {

        @Test
        void empty_IsNotNull() {
            // then
            assertThat(PredicateImpl.EMPTY).isNotNull();
        }

        @Test
        void empty_GetRootIsEmptyNode() {
            // given
            PredicateImpl<?> empty = (PredicateImpl<?>) PredicateImpl.EMPTY;

            // then
            assertThat(empty.getRoot()).isSameAs(EmptyNode.INSTANCE);
        }
    }

    @Nested
    class ToPredicate {

        @Test
        void toPredicate_ReturnsSameInstance() {
            // given
            PredicateImpl<Object> predicate = new PredicateImpl<>(LiteralNode.TRUE);

            // when
            var result = predicate.toPredicate();

            // then
            assertThat(result).isSameAs(predicate);
        }
    }
}
