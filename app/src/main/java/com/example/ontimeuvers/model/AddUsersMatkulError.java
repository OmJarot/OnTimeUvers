package com.example.ontimeuvers.model;

import com.example.ontimeuvers.entity.User;

import java.util.List;

public class AddUsersMatkulError {

    private List<User> users;

    public AddUsersMatkulError() {
    }

    public AddUsersMatkulError(List<User> users) {
        this.users = users;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
