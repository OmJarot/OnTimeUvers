package com.example.ontimeuvers.model;

import com.example.ontimeuvers.entity.User;

import java.util.List;
import java.util.Map;

public class AddUsersMatkul {

    private List<User> users;

    private Map<String, Map<String, String>> matkul;

    public AddUsersMatkul() {
    }

    public AddUsersMatkul(List<User> users, Map<String, Map<String, String>> matkul) {
        this.users = users;
        this.matkul = matkul;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public Map<String, Map<String, String>> getMatkul() {
        return matkul;
    }

    public void setMatkul(Map<String, Map<String, String>> matkul) {
        this.matkul = matkul;
    }
}
