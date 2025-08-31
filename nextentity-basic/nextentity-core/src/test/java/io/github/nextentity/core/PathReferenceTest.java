package io.github.nextentity.core;

import io.github.nextentity.api.Path;
import org.junit.jupiter.api.Test;

class PathReferenceTest {


    @Test
    void of() {
        String name = Integer[].class.getName();
        try {
            Class.forName(name);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        test(TestType::getBooleanArray, boolean[].class);
        test(TestType::getByteArray, byte[].class);
        test(TestType::getShortArray, short[].class);
        test(TestType::getCharArray, char[].class);
        test(TestType::getIntArray, int[].class);
        test(TestType::getLongArray, long[].class);
        test(TestType::getFloatArray, float[].class);
        test(TestType::getDoubleArray, double[].class);
        test(TestType::getObjectArray, Object[].class);

        test(TestType::getBoolean, Boolean.class);
        test(TestType::getByte, Byte.class);
        test(TestType::getShort, Short.class);
        test(TestType::getChar, Character.class);
        test(TestType::getInt, Integer.class);
        test(TestType::getLong, Long.class);
        test(TestType::getFloat, Float.class);
        test(TestType::getDouble, Double.class);
        test(TestType::getObject, Object.class);
    }

    public <T, R> void test(Path<T, R> f, Class<R> rClass) {
        PathReference reference = PathReference.of(f);
        if (reference.getEntityType() != TestType.class) {
            throw new IllegalArgumentException();
        }
        if (reference.getReturnType() != rClass) {
            throw new IllegalArgumentException();
        }
    }

    interface TestType {
        boolean[] getBooleanArray();

        byte[] getByteArray();

        short[] getShortArray();

        char[] getCharArray();

        int[] getIntArray();

        long[] getLongArray();

        float[] getFloatArray();

        double[] getDoubleArray();

        boolean getBoolean();

        byte getByte();

        short getShort();

        char getChar();

        int getInt();

        long getLong();

        float getFloat();

        double getDouble();

        Object[] getObjectArray();

        Object getObject();

    }

}