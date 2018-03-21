package com.bdd.social.login;

import com.bdd.social.api.ApiInterface;
import com.bdd.social.base.BasePresenter;
import com.bdd.social.di.AccountManager;
import com.bdd.social.model.User;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class LoginPresenter extends BasePresenter<LoginView> {

    private final AccountManager accountManager;

    public LoginPresenter(ApiInterface apiInterface, AccountManager accountManager) {
        super(apiInterface);
        this.accountManager = accountManager;
    }

    @Override
    protected void onViewAttached(LoginView loginView) {
        loginView.getOnLoginClickObservable()
                .subscribe(
                        view -> performLogin(loginView)
                );

    }

    private Disposable performLogin(LoginView loginView) {
        return apiInterface.login(loginView.getLogin(), loginView.getPassword())
                .takeUntil(detachSubject)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> loginView.showProgress(true))
                .doOnTerminate(() -> loginView.showProgress(false))
                .subscribe(
                        user -> {
                            saveUser(user);
                            loginView.goToNextScreen();
                        },
                        loginView::showError
                );
    }

    private void saveUser(User user) {
        accountManager.setUser(user);
    }


}
