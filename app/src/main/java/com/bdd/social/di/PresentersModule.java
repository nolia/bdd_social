package com.bdd.social.di;

import com.bdd.social.api.ApiInterface;
import com.bdd.social.feed.FeedPresenter;
import com.bdd.social.login.LoginPresenter;

import org.codejargon.feather.Provides;

@SuppressWarnings("unused")
public class PresentersModule {

    @Provides
    public LoginPresenter loginPresenter(ApiInterface apiInterface) {
        return new LoginPresenter(apiInterface);
    }

    @Provides
    public FeedPresenter feedPresenter(ApiInterface apiInterface) {
        return new FeedPresenter(apiInterface);
    }
}
