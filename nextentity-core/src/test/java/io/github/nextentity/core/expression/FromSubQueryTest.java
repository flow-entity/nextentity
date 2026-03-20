package io.github.nextentity.core.expression;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test objective: Verify FromSubQuery correctly represents a subquery source
 * <p>
 * Test scenarios:
 * 1. Create with QueryStructure
 * 2. Access structure property
 */
class FromSubQueryTest {

    @Nested
    class Creation {

        @Test
        void fromSubQuery_CreatesWithStructure() {
            // given
            QueryStructure structure = QueryStructure.of(String.class);

            // when
            FromSubQuery fromSubQuery = new FromSubQuery(structure);

            // then
            assertThat(fromSubQuery.structure()).isSameAs(structure);
        }

        @Test
        void fromSubQuery_ImplementsFrom() {
            // given
            FromSubQuery fromSubQuery = new FromSubQuery(QueryStructure.of(String.class));

            // then
            assertThat(fromSubQuery).isInstanceOf(From.class);
        }
    }
}
