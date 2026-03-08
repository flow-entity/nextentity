package io.github.nextentity.spring.integration.projection;

public interface IUser {

    int getId();

    int getRandomNumber();

    String getUsername();

    U getParentUser();

    record U(int id, int randomNumber, Object test, String username, U parentUser) {
    }

}
