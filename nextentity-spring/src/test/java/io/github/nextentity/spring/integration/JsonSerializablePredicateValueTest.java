package io.github.nextentity.spring.integration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonSerializablePredicateValueTest {
    public static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL);
    }

}
