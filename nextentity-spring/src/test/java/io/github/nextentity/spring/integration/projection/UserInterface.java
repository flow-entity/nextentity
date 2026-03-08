package io.github.nextentity.spring.integration.projection;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.nextentity.core.annotation.EntityAttribute;
import io.github.nextentity.spring.integration.JsonSerializablePredicateValueTest;

import java.util.Map;

public interface UserInterface {

    int getId();

    int getRandomNumber();

    String getUsername();

    Integer getPid();

    boolean isValid();

    @EntityAttribute("parentUser.username")
    String getParentUsername();

    default Map<String, Object> asMap() {
        return JsonSerializablePredicateValueTest.mapper
                .convertValue(this, new TypeReference<>() {
                });
    }
}
