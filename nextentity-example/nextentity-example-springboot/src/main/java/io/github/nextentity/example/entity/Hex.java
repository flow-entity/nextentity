package io.github.nextentity.example.entity;

import jakarta.persistence.Column;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HexFormat;

public class Hex implements Serializable, Comparable<Hex> {

    @Column(name = "id")
    final byte[] id = new byte[16];

    public Hex() {
    }

    public Hex(byte[] id) {
        if (id.length != 16) {
            throw new IllegalArgumentException();
        }
        System.arraycopy(id, 0, this.id, 0, 16);
    }

    @Override
    public String toString() {
        return HexFormat.of().formatHex(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Hex hex = (Hex) o;
        return Arrays.equals(id, hex.id);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(id);
    }

    @Override
    public int compareTo(Hex o) {
        return Arrays.compare(id, o.id);
    }


}
