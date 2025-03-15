package io.github.nextentity.example.entity;


import lombok.Data;

import java.io.Serializable;
import java.util.Arrays;

@Data
public class BytesId implements Serializable, Comparable<BytesId> {

    private byte[] id;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BytesId bytesId = (BytesId) o;
        return Arrays.equals(id, bytesId.id);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(id);
    }

    @Override
    public int compareTo(BytesId o) {
        return Arrays.compare(id, o.id);
    }
}
