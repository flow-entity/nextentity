package io.github.nextentity.core;

import io.github.nextentity.core.meta.Metamodel;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test objective: Verify SimpleQueryConfig correctly manages configuration
 * <p>
 * Test scenarios:
 * 1. Set and get query executor
 * 2. Set and get metamodel
 * 3. Fluent API
 */
class SimpleQueryConfigTest {

    @Nested
    class QueryExecutorTest {

        /**
         * Test objective: Verify setQueryExecutor stores executor
         * Test scenario: Set query executor
         * Expected result: Same executor returned
         */
        @Test
        void queryExecutor_SetsExecutor() {
            // given
            SimpleQueryConfig config = new SimpleQueryConfig();
            QueryExecutor executor = new QueryExecutor() {
                @Override
                public <T> List<T> getList(io.github.nextentity.core.expression.QueryStructure queryStructure) {
                    return Collections.emptyList();
                }
            };

            // when
            SimpleQueryConfig result = config.queryExecutor(executor);

            // then
            assertThat(config.queryExecutor()).isSameAs(executor);
            assertThat(result).isSameAs(config);
        }
    }

    @Nested
    class MetamodelTest {

        /**
         * Test objective: Verify setMetamodel stores metamodel
         * Test scenario: Set metamodel
         * Expected result: Same metamodel returned
         */
        @Test
        void metamodel_SetsMetamodel() {
            // given
            SimpleQueryConfig config = new SimpleQueryConfig();
            Metamodel meta = type -> null;

            // when
            SimpleQueryConfig result = config.metamodel(meta);

            // then
            assertThat(config.metamodel()).isSameAs(meta);
            assertThat(result).isSameAs(config);
        }
    }

    @Nested
    class FluentAPI {

        /**
         * Test objective: Verify fluent API works
         * Test scenario: Chain multiple setters
         * Expected result: All values set
         */
        @Test
        void fluent_Chaining_Works() {
            // given
            QueryExecutor executor = new QueryExecutor() {
                @Override
                public <T> List<T> getList(io.github.nextentity.core.expression.QueryStructure queryStructure) {
                    return Collections.emptyList();
                }
            };
            Metamodel meta = type -> null;

            // when
            SimpleQueryConfig config = new SimpleQueryConfig()
                    .queryExecutor(executor)
                    .metamodel(meta);

            // then
            assertThat(config.queryExecutor()).isSameAs(executor);
            assertThat(config.metamodel()).isSameAs(meta);
        }
    }
}
