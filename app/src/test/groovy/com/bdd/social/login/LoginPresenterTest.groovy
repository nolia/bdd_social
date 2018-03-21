package com.bdd.social.login

import android.view.View
import com.bdd.social.api.ApiInterface
import com.bdd.social.di.AccountManager
import com.bdd.social.model.User
import hkhc.electricspock.ElectricSpecification
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class LoginPresenterTest extends ElectricSpecification {

    ApiInterface mockApi
    AccountManager mockAccountManager
    private LoginPresenter loginPresenter

    void setup() {
        mockApi = Mock(ApiInterface)
        mockAccountManager = Mock(AccountManager)
        loginPresenter = new LoginPresenter(mockApi, mockAccountManager)
    }

    def "should subscribe to view login events"() {
        given:
        def view = Mock(LoginView)
        Observable<View> mockObservable = Mock(Observable)

        when:
        loginPresenter.onViewAttached(view)

        then:
        1 * view.getOnLoginClickObservable() >> mockObservable
        1 * mockObservable./subscribe.*/(*_)
    }

    def "should call api on login click"() {
        given:
        def loginView = Mock(LoginView)
        Observable<View> observable = PublishSubject.create()
        loginView.getOnLoginClickObservable() >> observable

        when: "View login click occurs"
        loginPresenter.attachView(loginView)
        observable.onNext(Mock(View))

        then:
        1 * loginView.getLogin() >> "login"
        1 * loginView.getPassword() >> "pass"
        1 * mockApi.login(_, _) >> Observable.just(new User())
        1 * loginView.showProgress(true)
        1 * loginView.showProgress(false)

        1 * loginView.goToNextScreen()
        1 * mockAccountManager.setUser(_)
    }
}