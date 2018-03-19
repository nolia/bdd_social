package com.bdd.social.base;

import com.bdd.social.api.ApiInterface;

import javax.inject.Inject;

import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;

public abstract class BasePresenter<V> {

    protected final ApiInterface apiInterface;

    protected final BehaviorSubject<V> viewSubject = BehaviorSubject.create();
    protected final PublishSubject<V> detachSubject = PublishSubject.create();

    @Inject
    public BasePresenter(ApiInterface apiInterface) {
        this.apiInterface = apiInterface;
        viewSubject.subscribe(this::onViewAttached);
    }

    protected abstract void onViewAttached(V view);

    public final void attachView(V view) {
        viewSubject.onNext(view);
    }

    public final void detachView(V view) {
        detachSubject.onNext(view);
    }
}
