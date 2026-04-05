package io.github.nextentity.spring.integration.projection;

public interface IUser {

    Integer getId();

    int getRandomNumber();

    String getUsername();

    U getParentUser();

    record U(Integer id, int randomNumber, Object test, String username, U parentUser) {
    }

}
