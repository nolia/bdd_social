package com.bdd.social.di;

import com.bdd.social.model.User;

public class AccountManager {

    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
