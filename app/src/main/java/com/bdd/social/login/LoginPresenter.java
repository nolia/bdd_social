package com.bdd.social.login;

import com.bdd.social.api.ApiInterface;
import com.bdd.social.base.BasePresenter;
import com.bdd.social.model.User;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class LoginPresenter extends BasePresenter<LoginView> {

    public LoginPresenter(ApiInterface apiInterface) {
        super(apiInterface);
    }

    @Override
    protected void onViewAttached(LoginView loginView) {
        loginView.getOnLoginClickObservable()
                .subscribe(
                        view -> perform(loginView)
                );

    }

    private Disposable perform(LoginView loginView) {
        return apiInterface.login(loginView.getLogin(), loginView.getPassword())
                .takeUntil(detachSubject)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> loginView.showProgress(true))
                .doOnEach(notification -> loginView.showProgress(false))
                .subscribe(
                        user -> {
                            saveUser(user);
                            loginView.goToNextScreen();
                        },
                        loginView::showError
                );
    }

    private void saveUser(User user) {

    }


}
