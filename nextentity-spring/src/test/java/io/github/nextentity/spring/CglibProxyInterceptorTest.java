package io.github.nextentity.spring;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CglibProxyInterceptorTest {

    @Test
    void cglibProxyShouldDelegateUnmappedMethodToSuperclass() throws Exception {
        CglibProxyInterceptor.CglibProxyConstructor constructor =
                new CglibProxyInterceptor.CglibProxyConstructor(TestDto.class, java.util.List.of());

        Method getter = TestDto.class.getMethod("getValue");
        TestDto proxy = (TestDto) constructor.createProxy(Map.of(getter, "mapped"));

        assertEquals("mapped", proxy.getValue());
        assertDoesNotThrow(proxy::toString);
    }

    public static class TestDto {
        public String getValue() {
            return "default";
        }

        @Override
        public String toString() {
            return "TestDto";
        }
    }
}
