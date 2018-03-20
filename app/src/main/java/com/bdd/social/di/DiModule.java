package com.bdd.social.di;

import android.content.Context;

import com.bdd.social.R;
import com.bdd.social.api.ApiInterface;
import com.bdd.social.api.LazyServer;
import com.bdd.social.feed.FeedPresenter;
import com.bdd.social.login.LoginPresenter;
import com.google.gson.Gson;

import org.codejargon.feather.Provides;

import java.io.InputStream;
import java.io.InputStreamReader;

import javax.inject.Singleton;

@SuppressWarnings("unused")
public class DiModule {

    private LazyServer lazyServer;

    public DiModule(Context context) {
        final InputStream stream = context.getResources().openRawResource(R.raw.server);
        final Gson gson = new Gson();
        lazyServer = gson.fromJson(new InputStreamReader(stream), LazyServer.class);
    }

    @Provides
    @Singleton
    public ApiInterface provideApiInterface() {
        // Use Retrofit, etc. in real project.
        return lazyServer;
    }

    @Provides
    public LoginPresenter loginPresenter(ApiInterface apiInterface) {
        return new LoginPresenter(apiInterface);
    }

    @Provides
    public FeedPresenter feedPresenter(ApiInterface apiInterface) {
        return new FeedPresenter(apiInterface);
    }
}
