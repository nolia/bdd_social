package com.bdd.social.feed;

import com.bdd.social.api.ApiInterface;
import com.bdd.social.base.BasePresenter;

public class FeedPresenter extends BasePresenter<FeedView> {

    public FeedPresenter(ApiInterface apiInterface) {
        super(apiInterface);
    }

    @Override
    protected void onViewAttached(FeedView view) {
        // TODO
        // 1. Load messages from API.
        // 2. Subscribe to new incoming messages.
        // 3. Subscribe to message sent event.
    }
}
