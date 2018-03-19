package com.bdd.social.login;

import android.view.View;

import io.reactivex.Observable;

public interface LoginView {

    String getLogin();

    String getPassword();

    void showProgress(boolean show);

    Observable<View> getOnLoginClickObservable();

    void goToNextScreen();

    void showError(Throwable throwable);
}
