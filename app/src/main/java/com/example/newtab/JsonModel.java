package com.example.newtab;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class JsonModel {

    @SerializedName("status")
    private int status;
    @SerializedName("message")
    private String message;
    @SerializedName("users")
    private List<PersonModel> users;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    List<PersonModel> getUsers() {
        return users;
    }

    public void setUsers(List<PersonModel> users) {
        this.users = users;
    }


}

