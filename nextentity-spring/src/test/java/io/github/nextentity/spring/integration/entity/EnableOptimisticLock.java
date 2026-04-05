package io.github.nextentity.spring.integration.entity;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;

@MappedSuperclass
public class EnableOptimisticLock {

    @Version
    private int optLock;

    private int getOptLock() {
        return optLock;
    }

    private void setOptLock(int optLock) {
        this.optLock = optLock;
    }
}
