package com.bdd.social.feed;

import com.bdd.social.api.ApiInterface;
import com.bdd.social.base.BasePresenter;
import com.bdd.social.model.User;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class FeedPresenter extends BasePresenter<FeedView> {

    public FeedPresenter(ApiInterface apiInterface) {
        super(apiInterface);
    }

    @Override
    protected void onViewAttached(FeedView view) {
        // 1. Load messages from API.
        apiInterface.getMessages()
                .takeUntil(detachSubject)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(view::setFeed, view::showError);

        // 2. Subscribe to new incoming messages.
        apiInterface.getNewMessageChannel()
                .takeUntil(detachSubject)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(view::addNewMessage, view::showError);

        // 3. Subscribe to message sent event.
        view.messageSentObservable()
                .takeUntil(detachSubject)
                .subscribe(text -> apiInterface.sendMessage(getUser(), text)
                        .takeUntil(detachSubject)
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(disposable -> view.disableSend(true))
                        .doOnEach(n -> view.disableSend(false))
                        .subscribe(message -> view.resetInput(), view::showError)
                );

    }

    private User getUser() {
        return new User("meail@mail.com", "User Name");
    }
}
