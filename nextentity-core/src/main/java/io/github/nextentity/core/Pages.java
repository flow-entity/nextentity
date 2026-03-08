package io.github.nextentity.core;

import io.github.nextentity.api.model.Page;
import io.github.nextentity.api.model.Pageable;

import java.util.List;
import java.util.Objects;

/**
 * @author HuangChengwei
 * @since 1.0.0
 */
public class Pages {

    public static <T> Page<T> page(List<T> items, long total) {
        return new PageImpl<>(items, total);
    }

    public static Pageable pageable(int page, int size) {
        return new PageableImpl(page, size);
    }

    public static class PageImpl<T> implements Page<T> {
        private List<T> items;
        private long total;

        public PageImpl(List<T> items, long total) {
            this.items = items;
            this.total = total;
        }

        public PageImpl() {
        }

        public List<T> getItems() {
            return this.items;
        }

        public long getTotal() {
            return this.total;
        }

        public void setItems(List<T> items) {
            this.items = items;
        }

        public void setTotal(long total) {
            this.total = total;
        }

        public boolean equals(final Object o) {
            if (o == this) return true;
            if (!(o instanceof PageImpl<?> other)) return false;
            if (!other.canEqual(this)) return false;
            final Object this$items = this.getItems();
            final Object other$items = other.getItems();
            if (!Objects.equals(this$items, other$items)) return false;
            return this.getTotal() == other.getTotal();
        }

        protected boolean canEqual(final Object other) {
            return other instanceof PageImpl;
        }

        public int hashCode() {
            final int PRIME = 59;
            int result = 1;
            final Object $items = this.getItems();
            result = result * PRIME + ($items == null ? 43 : $items.hashCode());
            final long $total = this.getTotal();
            result = result * PRIME + Long.hashCode($total);
            return result;
        }

        public String toString() {
            return "Pages.PageImpl(items=" + this.getItems() + ", total=" + this.getTotal() + ")";
        }
    }

    public static class PageableImpl implements Pageable {
        private int page, size;

        public PageableImpl(int page, int size) {
            this.page = page;
            this.size = size;
        }

        public PageableImpl() {
        }

        public int page() {
            return this.page;
        }

        public int size() {
            return this.size;
        }

        public PageableImpl page(int page) {
            this.page = page;
            return this;
        }

        public PageableImpl size(int size) {
            this.size = size;
            return this;
        }

        public boolean equals(final Object o) {
            if (o == this) return true;
            if (!(o instanceof PageableImpl other)) return false;
            if (!other.canEqual(this)) return false;
            if (this.page() != other.page()) return false;
            return this.size() == other.size();
        }

        protected boolean canEqual(final Object other) {
            return other instanceof PageableImpl;
        }

        public int hashCode() {
            final int PRIME = 59;
            int result = 1;
            result = result * PRIME + this.page();
            result = result * PRIME + this.size();
            return result;
        }

        public String toString() {
            return "Pages.PageableImpl(page=" + this.page() + ", size=" + this.size() + ")";
        }
    }

}
