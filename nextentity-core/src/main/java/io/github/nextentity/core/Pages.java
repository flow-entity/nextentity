package io.github.nextentity.core;

import io.github.nextentity.api.model.Page;
import io.github.nextentity.api.model.Pageable;

import java.util.List;
import java.util.Objects;

///
/// @author HuangChengwei
/// @since 1.0.0
///
public class Pages {

    public static <T> Page<T> page(List<T> items, long total) {
        return new DefaultPage<>(items, total);
    }

    public static <T> Pageable<T> pageable(int page, int total) {
        return new DefaultPageable<>(page, total);
    }

    public record DefaultPageable<T>(int page, int size) implements Pageable<T> {
        @Override
        public Page<T> collect(List<T> list, long total) {
            return Pages.page(list, total);
        }
    }


    public static class DefaultPage<T> implements Page<T> {
        private List<T> items;
        private long total;

        public DefaultPage(List<T> items, long total) {
            this.items = items;
            this.total = total;
        }

        public DefaultPage() {
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
            if (!(o instanceof DefaultPage<?> other)) return false;
            if (!other.canEqual(this)) return false;
            final Object this$items = this.getItems();
            final Object other$items = other.getItems();
            if (!Objects.equals(this$items, other$items)) return false;
            return this.getTotal() == other.getTotal();
        }

        protected boolean canEqual(final Object other) {
            return other instanceof DefaultPage;
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


}
