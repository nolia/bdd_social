package com.bdd.social;

import android.app.Application;

import com.bdd.social.di.DiModule;

import org.codejargon.feather.Feather;

public class App extends Application {

    private Feather feather;

    @Override
    public void onCreate() {
        super.onCreate();

        feather = Feather.with(new DiModule(this));
    }

    public Feather getFeather() {
        return feather;
    }
}
