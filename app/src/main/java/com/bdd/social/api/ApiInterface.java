package com.bdd.social.api;


import com.bdd.social.model.Message;
import com.bdd.social.model.User;

import java.util.List;

import io.reactivex.Observable;

public interface ApiInterface {

    Observable<User> login(final String login, final String password);

    Observable<List<Message>> getMessages();

    Observable<Message> getNewMessageChannel();

    Observable<Message> sendMessage(User user, String text);
}
