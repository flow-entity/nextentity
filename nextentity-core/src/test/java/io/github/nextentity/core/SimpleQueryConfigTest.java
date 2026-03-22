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
 * 4. Null boundary conditions
 * 5. Default values
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

        /**
         * Test objective: Verify queryExecutor returns null by default
         * Test scenario: Create new config without setting executor
         * Expected result: null returned
         */
        @Test
        void queryExecutor_DefaultValue_IsNull() {
            // given
            SimpleQueryConfig config = new SimpleQueryConfig();

            // when
            QueryExecutor executor = config.queryExecutor();

            // then
            assertThat(executor).isNull();
        }

        /**
         * Test objective: Verify setting null executor is allowed
         * Test scenario: Set executor to null after setting a value
         * Expected result: null returned
         */
        @Test
        void queryExecutor_SetNull_IsAllowed() {
            // given
            SimpleQueryConfig config = new SimpleQueryConfig();
            QueryExecutor executor = new QueryExecutor() {
                @Override
                public <T> List<T> getList(io.github.nextentity.core.expression.QueryStructure queryStructure) {
                    return Collections.emptyList();
                }
            };
            config.queryExecutor(executor);

            // when
            config.queryExecutor(null);

            // then
            assertThat(config.queryExecutor()).isNull();
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

        /**
         * Test objective: Verify metamodel returns null by default
         * Test scenario: Create new config without setting metamodel
         * Expected result: null returned
         */
        @Test
        void metamodel_DefaultValue_IsNull() {
            // given
            SimpleQueryConfig config = new SimpleQueryConfig();

            // when
            Metamodel meta = config.metamodel();

            // then
            assertThat(meta).isNull();
        }

        /**
         * Test objective: Verify setting null metamodel is allowed
         * Test scenario: Set metamodel to null after setting a value
         * Expected result: null returned
         */
        @Test
        void metamodel_SetNull_IsAllowed() {
            // given
            SimpleQueryConfig config = new SimpleQueryConfig();
            Metamodel meta = type -> null;
            config.metamodel(meta);

            // when
            config.metamodel(null);

            // then
            assertThat(config.metamodel()).isNull();
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

        /**
         * Test objective: Verify fluent API returns same instance
         * Test scenario: Call each setter and verify return type
         * Expected result: Same config instance returned
         */
        @Test
        void fluent_ReturnsSameInstance() {
            // given
            SimpleQueryConfig config = new SimpleQueryConfig();

            // when
            SimpleQueryConfig result1 = config.queryExecutor(null);
            SimpleQueryConfig result2 = config.metamodel(null);

            // then
            assertThat(result1).isSameAs(config);
            assertThat(result2).isSameAs(config);
        }
    }

    @Nested
    class InterfaceImplementation {

        /**
         * Test objective: Verify SimpleQueryConfig implements QueryConfig
         * Test scenario: Assign to QueryConfig interface
         * Expected result: Methods accessible via interface
         */
        @Test
        void implementsQueryConfig_CanBeAssignedToInterface() {
            // given
            QueryExecutor executor = new QueryExecutor() {
                @Override
                public <T> List<T> getList(io.github.nextentity.core.expression.QueryStructure queryStructure) {
                    return Collections.emptyList();
                }
            };
            Metamodel meta = type -> null;

            // when
            QueryConfig config = new SimpleQueryConfig()
                    .queryExecutor(executor)
                    .metamodel(meta);

            // then
            assertThat(config.queryExecutor()).isSameAs(executor);
            assertThat(config.metamodel()).isSameAs(meta);
        }
    }
}
