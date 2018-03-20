package com.bdd.social.feed;

import com.bdd.social.model.Message;

import java.util.List;

import io.reactivex.Observable;

public interface FeedView {

    void setFeed(List<Message> messages);

    void addNewMessage(Message message);

    void showProgress(boolean progress);

    Observable<String> messageSentObservable();

    void showError(Throwable throwable);

    void disableSend(boolean disable);

    void resetInput();
}
