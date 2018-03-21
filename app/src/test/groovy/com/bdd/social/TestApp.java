package com.bdd.social;

import android.content.Context;

import com.bdd.social.api.ApiInterface;
import com.bdd.social.di.AccountManager;
import com.bdd.social.di.DiModule;
import com.bdd.social.feed.FeedPresenter;
import com.bdd.social.login.LoginPresenter;

import org.codejargon.feather.Feather;

/**
 * Test application.
 */
public class TestApp extends App {

    public Feather feather;

    // Api interface.
    public ApiInterface apiInterface;

    @Override
    public Feather getFeather() {
        if (feather == null) {
            feather = Feather.with(new TestDiModule(this));
        }
        return feather;
    }

    class TestDiModule extends DiModule {

        public TestDiModule(Context context) {
            super(context);
        }

        @Override
        public ApiInterface provideApiInterface() {
            return apiInterface;
        }

        @Override
        public LoginPresenter loginPresenter(ApiInterface apiInterface, AccountManager accountManager) {
            return super.loginPresenter(apiInterface, accountManager);
        }

        @Override
        public FeedPresenter feedPresenter(ApiInterface apiInterface) {
            return super.feedPresenter(apiInterface);
        }
    }
}
