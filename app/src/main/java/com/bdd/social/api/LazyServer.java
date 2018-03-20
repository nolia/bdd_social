package com.bdd.social.api;

import com.bdd.social.model.Message;
import com.bdd.social.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class LazyServer implements ApiInterface {

    private List<UserWithPassword> users;
    private ArrayList<Message> messages; // To be able to modify.
    private List<Message> newMessages;

    private transient PublishSubject<Message> newMessageChannel = PublishSubject.create();

    private transient Random random = new Random();

    @Override
    public Observable<User> login(String login, String password) {
        return delay().map(time -> {
            for (UserWithPassword user : users) {
                if (user.email.equals(login) && user.password.equals(password)) {
                    startSendingNewMessages();
                    return user;
                }
            }
            throw new IllegalArgumentException("User not found!");
        });
    }

    private void startSendingNewMessages() {
        Observable.fromIterable(newMessages)
                .flatMap(message -> randomDelay().map(t -> message))
                .subscribe(newMessageChannel::onNext);
    }

    @Override
    public Observable<List<Message>> getMessages() {
        return delay().map(t -> messages);
    }

    @Override
    public Observable<Message> getNewMessageChannel() {
        return newMessageChannel;
    }

    @Override
    public Observable<Message> sendMessage(User user, String text) {
        return delay()
                .map(t -> {
                    final Message message = new Message(user, text);
                    newMessageChannel.onNext(message);
                    return message;
                });
    }

    private Observable<Long> randomDelay() {
        return Observable.timer(10 + random.nextInt(10), TimeUnit.SECONDS);
    }

    private Observable<Long> delay() {
        return Observable.timer(1500, TimeUnit.MILLISECONDS);
    }

    static class UserWithPassword extends User {

        String password;
    }
}
