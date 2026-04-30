package io.github.nextentity.spring.integration.projection;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.nextentity.core.annotation.EntityPath;

import java.util.Map;

public interface UserInterface {

    ObjectMapper MAPPER = new ObjectMapper() {{
        setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL);
    }};

    Integer getId();

    int getRandomNumber();

    String getUsername();

    Integer getPid();

    boolean isValid();

    @EntityPath("parentUser.username")
    String getParentUsername();

    default Map<String, Object> asMap() {
        return MAPPER.convertValue(this, new TypeReference<>() {
                });
    }
}
