package io.github.nextentity.spring.integration.projection;

import io.github.nextentity.core.annotation.EntityAttribute;
import io.github.nextentity.spring.integration.entity.User;

import java.util.Objects;

public class UserModel implements UserInterface {

    private int id;

    private int randomNumber;

    private String username;

    private Integer pid;

    private boolean valid;

    @EntityAttribute("parentUser.username")
    private String parentUsername;

    public UserModel() {
    }

    public UserModel(User user) {

        id = user.getId();
        randomNumber = user.getRandomNumber();
        username = user.getUsername();
        pid = user.getPid();
        valid = user.isValid();
        if (user.getParentUser() != null) {
            parentUsername = user.getParentUser().getUsername();
        }

    }

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int getRandomNumber() {
        return randomNumber;
    }

    public void setRandomNumber(int randomNumber) {
        this.randomNumber = randomNumber;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    @Override
    public String getParentUsername() {
        return parentUsername;
    }

    public void setParentUsername(String parentUsername) {
        this.parentUsername = parentUsername;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof UserModel userModel)) return false;

        return id == userModel.id
               && randomNumber == userModel.randomNumber
               && valid == userModel.valid
               && Objects.equals(username, userModel.username)
               && Objects.equals(pid, userModel.pid)
               && Objects.equals(parentUsername, userModel.parentUsername);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + randomNumber;
        result = 31 * result + Objects.hashCode(username);
        result = 31 * result + Objects.hashCode(pid);
        result = 31 * result + Boolean.hashCode(valid);
        result = 31 * result + Objects.hashCode(parentUsername);
        return result;
    }

    @Override
    public String toString() {
        return "UserModel{" +
               "id=" + id +
               ", username='" + username + '\'' +
               '}';
    }
}
