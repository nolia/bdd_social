package com.bdd.social.model;

import java.util.Date;

public class Message {

    public User author;
    public String text;
    public Date date;

    public Message() {
        // for Json.
    }

    public Message(User author, String text) {
        this.author = author;
        this.text = text;
        this.date = new Date();
    }
}
