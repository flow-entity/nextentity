package io.github.nextentity.spring;

import io.github.nextentity.core.reflect.LazyValueMap;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CglibProxyInterceptorTest {

    @Test
    void cglibProxyShouldDelegateUnmappedMethodToSuperclass() throws Exception {
        CglibProxyInterceptor.CglibProxyConstructor constructor =
                new CglibProxyInterceptor.CglibProxyConstructor(TestDto.class, List.of());

        Method getter = TestDto.class.getMethod("getValue");
        LazyValueMap map = new LazyValueMap();
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
