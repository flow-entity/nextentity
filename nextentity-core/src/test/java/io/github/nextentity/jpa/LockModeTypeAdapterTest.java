package io.github.nextentity.jpa;

import io.github.nextentity.api.model.LockModeType;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test objective: Verify LockModeTypeAdapter correctly converts between lock mode types
 * <p>
 * Test scenarios:
 * 1. Convert null value
 * 2. Convert each LockModeType value
 * 3. Conversion preserves name
 */
class LockModeTypeAdapterTest {

    @Nested
    class NullHandling {

        @Test
        void of_Null_ReturnsNull() {
            // when
            jakarta.persistence.LockModeType result = LockModeTypeAdapter.of(null);

            // then
            assertThat(result).isNull();
        }
    }

    @Nested
    class LockModeTypeConversion {

        @Test
        void of_Optimistic_ReturnsJpaOptimistic() {
            // when
            jakarta.persistence.LockModeType result = LockModeTypeAdapter.of(LockModeType.OPTIMISTIC);

            // then
            assertThat(result).isEqualTo(jakarta.persistence.LockModeType.OPTIMISTIC);
        }

        @Test
        void of_OptimisticForceIncrement_ReturnsJpaOptimisticForceIncrement() {
            // when
            jakarta.persistence.LockModeType result = LockModeTypeAdapter.of(LockModeType.OPTIMISTIC_FORCE_INCREMENT);

            // then
            assertThat(result).isEqualTo(jakarta.persistence.LockModeType.OPTIMISTIC_FORCE_INCREMENT);
        }

        @Test
        void of_PessimisticRead_ReturnsJpaPessimisticRead() {
            // when
            jakarta.persistence.LockModeType result = LockModeTypeAdapter.of(LockModeType.PESSIMISTIC_READ);

            // then
            assertThat(result).isEqualTo(jakarta.persistence.LockModeType.PESSIMISTIC_READ);
        }

        @Test
        void of_PessimisticWrite_ReturnsJpaPessimisticWrite() {
            // when
            jakarta.persistence.LockModeType result = LockModeTypeAdapter.of(LockModeType.PESSIMISTIC_WRITE);

            // then
            assertThat(result).isEqualTo(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE);
        }

        @Test
        void of_PessimisticForceIncrement_ReturnsJpaPessimisticForceIncrement() {
            // when
            jakarta.persistence.LockModeType result = LockModeTypeAdapter.of(LockModeType.PESSIMISTIC_FORCE_INCREMENT);

            // then
            assertThat(result).isEqualTo(jakarta.persistence.LockModeType.PESSIMISTIC_FORCE_INCREMENT);
        }

        @Test
        void of_Read_ReturnsJpaRead() {
            // when
            jakarta.persistence.LockModeType result = LockModeTypeAdapter.of(LockModeType.READ);

            // then
            assertThat(result).isEqualTo(jakarta.persistence.LockModeType.READ);
        }

        @Test
        void of_Write_ReturnsJpaWrite() {
            // when
            jakarta.persistence.LockModeType result = LockModeTypeAdapter.of(LockModeType.WRITE);

            // then
            assertThat(result).isEqualTo(jakarta.persistence.LockModeType.WRITE);
        }

        @Test
        void of_None_ReturnsJpaNone() {
            // when
            jakarta.persistence.LockModeType result = LockModeTypeAdapter.of(LockModeType.NONE);

            // then
            assertThat(result).isEqualTo(jakarta.persistence.LockModeType.NONE);
        }
    }
}
