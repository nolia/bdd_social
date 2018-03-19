package com.bdd.social;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.bdd.social.feed.FeedPresenter;
import com.bdd.social.feed.FeedView;
import com.bdd.social.model.Message;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;

public class FeedActivity extends AppCompatActivity implements FeedView {

    private Toolbar toolbar;

    private RecyclerView recyclerView;
    private EditText inputText;
    private View sendButton;

    @Inject
    FeedPresenter feedPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feed_activity);
        ((App) getApplication()).getFeather().injectFields(this);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.chat);

        recyclerView = findViewById(R.id.recyclerView);
        inputText = findViewById(R.id.inputText);
        sendButton = findViewById(R.id.sendButton);

        feedPresenter.attachView(this);
    }

    @Override
    protected void onDestroy() {
        feedPresenter.detachView(this);
        super.onDestroy();
    }

    @Override
    public void setFeed(List<Message> messages) {

    }

    @Override
    public void addNewMessage(Message message) {

    }

    @Override
    public void showProgress(boolean progress) {

    }

    @Override
    public Observable<String> messageSentObservable() {
        return null;
    }
}
