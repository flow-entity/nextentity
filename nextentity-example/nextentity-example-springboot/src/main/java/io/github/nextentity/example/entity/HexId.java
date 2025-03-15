package io.github.nextentity.example.entity;

public class HexId implements CharSequence{
    String id;

    @Override
    public int length() {
        return id.length();
    }

    @Override
    public char charAt(int index) {
        return id.charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return id.subSequence(start, end);
    }
}
