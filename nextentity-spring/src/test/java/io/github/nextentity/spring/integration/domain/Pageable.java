package io.github.nextentity.spring.integration.domain;

import io.github.nextentity.api.model.Sliceable;

import java.util.List;

public class Pageable<T> implements Sliceable<T, Page<T>> {

    private int page;
    private int size;

    public Pageable() {
    }

    public Pageable(int page, int size) {
        this.page = page;
        this.size = size;
    }

    @Override
    public int offset() {
        return (page - 1) * size;
    }

    @Override
    public int limit() {
        return size;
    }

    @Override
    public Page<T> collect(List<T> list, long total) {
        return new Page<>(list, total, this);
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
