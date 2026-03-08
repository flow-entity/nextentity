package io.github.nextentity.spring.integration;

import io.github.nextentity.core.PathReference;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PathReferenceTest {
    @Test
    void test() {
        PathReference pathReference = PathReference.of(TestLastClass::getName);
        Assertions.assertEquals(pathReference.getFieldName(), "name");
        Assertions.assertEquals(pathReference.getReturnType(), String.class);
        Assertions.assertEquals(pathReference.getEntityType(), TestLastClass.class);
    }

    public static class TestLastClass {
        String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

}
