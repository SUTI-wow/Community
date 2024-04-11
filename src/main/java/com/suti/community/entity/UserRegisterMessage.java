package com.suti.community.entity;

public class UserRegisterMessage {
    private User user;

    public UserRegisterMessage(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
