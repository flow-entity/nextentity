package io.github.nextentity.core;

import io.github.nextentity.api.Collector;
import io.github.nextentity.api.SubQueryBuilder;
import io.github.nextentity.api.model.Page;
import io.github.nextentity.api.model.Pageable;
import io.github.nextentity.api.model.Slice;
import jakarta.persistence.LockModeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for AbstractCollector interface default methods.
 */
class AbstractCollectorTest {

    private TestCollectorImpl<String> collector;

    @BeforeEach
    void setUp() {
        collector = new TestCollectorImpl<>();
    }

    /**
     * Test objective: Verify that slice returns correct slice when count > offset.
     * Test scenario: Call slice with offset and limit when there are more items.
     * Expected result: Returns a Slice containing the items.
     */
    @Test
    void slice_WhenCountGreaterThanOffset_ShouldReturnSliceWithContent() {
        // given
        List<String> items = Arrays.asList("a", "b", "c");
        collector.setItems(items);
        collector.setCount(10L);

        // when
        Slice<String> slice = collector.slice(0, 3);

        // then
        assertThat(slice.data()).hasSize(3);
        assertThat(slice.total()).isEqualTo(10L);
        assertThat(slice.offset()).isEqualTo(0);
        assertThat(slice.limit()).isEqualTo(3);
    }

    /**
     * Test objective: Verify that slice returns empty slice when count <= offset.
     * Test scenario: Call slice with offset that exceeds total count.
     * Expected result: Returns an empty Slice.
     */
    @Test
    void slice_WhenCountLessThanOrEqualToOffset_ShouldReturnEmptySlice() {
        // given
        collector.setCount(5L);

        // when
        Slice<String> slice = collector.slice(10, 3);

        // then
        assertThat(slice.data()).isEmpty();
        assertThat(slice.total()).isEqualTo(5L);
        assertThat(slice.offset()).isEqualTo(10);
        assertThat(slice.limit()).isEqualTo(3);
    }

    /**
     * Test objective: Verify that getPage returns page with content when count > offset.
     * Test scenario: Call getPage with pageable when there are items.
     * Expected result: Returns a Page containing the items.
     */
    @Test
    void getPage_WhenCountGreaterThanOffset_ShouldReturnPageWithContent() {
        // given
        Pageable pageable = Pages.pageable(1, 5);
        List<String> items = Arrays.asList("a", "b", "c");
        collector.setItems(items);
        collector.setCount(10L);

        // when
        Page<String> page = collector.getPage(pageable);

        // then
        assertThat(page.getItems()).hasSize(3);
        assertThat(page.getTotal()).isEqualTo(10L);
    }

    /**
     * Test objective: Verify that getPage returns empty page when count <= offset.
     * Test scenario: Call getPage with pageable where offset exceeds count.
     * Expected result: Returns an empty Page.
     */
    @Test
    void getPage_WhenCountLessThanOrEqualToOffset_ShouldReturnEmptyPage() {
        // given
        Pageable pageable = Pages.pageable(3, 5); // offset = (3-1) * 5 = 10
        collector.setCount(5L);

        // when
        Page<String> page = collector.getPage(pageable);

        // then
        assertThat(page.getItems()).isEmpty();
        assertThat(page.getTotal()).isEqualTo(5L);
    }

    /**
     * Test objective: Verify that map creates a MappedCollector that transforms results.
     * Test scenario: Create a mapped collector and retrieve data.
     * Expected result: Results are transformed by the mapper function.
     */
    @Test
    void map_ShouldTransformResults() {
        // given
        List<String> items = Arrays.asList("abc", "de", "fghi");
        collector.setItems(items);
        collector.setCount(3L);

        Function<String, Integer> lengthMapper = String::length;
        Collector<Integer> mappedCollector = collector.map(lengthMapper);

        // when
        List<Integer> result = mappedCollector.getList(0, 10, LockModeType.NONE);

        // then
        assertThat(result).containsExactly(3, 2, 4);
    }

    /**
     * Test objective: Verify that mapped collector count delegates to original collector.
     * Test scenario: Call count on mapped collector.
     * Expected result: Returns count from original collector.
     */
    @Test
    void map_CountShouldDelegateToOriginalCollector() {
        // given
        collector.setCount(100L);

        Function<String, Integer> mapper = String::length;
        Collector<Integer> mappedCollector = collector.map(mapper);

        // when
        long count = mappedCollector.count();

        // then
        assertThat(count).isEqualTo(100L);
    }

    /**
     * Test objective: Verify that mapped collector exist delegates to original collector.
     * Test scenario: Call exist on mapped collector.
     * Expected result: Returns result from original collector.
     */
    @Test
    void map_ExistShouldDelegateToOriginalCollector() {
        // given
        collector.setExistResult(true);

        Function<String, Integer> mapper = String::length;
        Collector<Integer> mappedCollector = collector.map(mapper);

        // when
        boolean exists = mappedCollector.exist(5);

        // then
        assertThat(exists).isTrue();
    }

    /**
     * Test collector implementation for testing AbstractCollector default methods.
     */
    @SuppressWarnings("unchecked")
    static class TestCollectorImpl<T> implements AbstractCollector<T> {
        private List<T> items = Collections.emptyList();
        private long count = 0;
        private boolean existResult = false;

        public void setItems(List<T> items) {
            this.items = items;
        }

        public void setCount(long count) {
            this.count = count;
        }

        public void setExistResult(boolean existResult) {
            this.existResult = existResult;
        }

        @Override
        public long count() {
            return count;
        }

        @Override
        public List<T> getList(int offset, int maxResult, LockModeType lockModeType) {
            if (offset >= items.size()) {
                return Collections.emptyList();
            }
            int end = Math.min(offset + maxResult, items.size());
            return items.subList(offset, end);
        }

        @Override
        public boolean exist(int offset) {
            return existResult;
        }

        @Override
        public <X> SubQueryBuilder<X, T> asSubQuery() {
            throw new UnsupportedOperationException();
        }
    }
}
