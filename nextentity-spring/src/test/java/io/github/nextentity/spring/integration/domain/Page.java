package io.github.nextentity.spring.integration.domain;


import java.util.List;

public class Page<T> {

    private List<T> list;
    private long total;
    private int page;
    private int size;

    public Page(List<T> list, long total, Pageable<T> pageable) {
        this.list = list;
        this.total = total;
        this.page = pageable.getPage();
        this.size = pageable.getSize();
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
