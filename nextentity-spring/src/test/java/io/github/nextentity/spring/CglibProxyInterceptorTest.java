package io.github.nextentity.spring;

import io.github.nextentity.core.reflect.MethodValueMap;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CglibProxyInterceptorTest {

    @Test
    void cglibProxyShouldDelegateUnmappedMethodToSuperclass() throws Exception {
        CglibProxyInterceptor.CglibProxyConstructor constructor =
                new CglibProxyInterceptor.CglibProxyConstructor(TestDto.class, java.util.List.of());

        Method getter = TestDto.class.getMethod("getValue");
        MethodValueMap map = new MethodValueMap();
        map.put(getter, "mapped");
        TestDto proxy = (TestDto) constructor.createProxy(map);

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
