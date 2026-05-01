package io.github.nextentity.integration.entity;

import jakarta.persistence.Embeddable;

@Embeddable
public class ZipCode {

    private String code;

    public ZipCode() {
    }

    public ZipCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
