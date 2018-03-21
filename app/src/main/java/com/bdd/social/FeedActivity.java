package com.bdd.social;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bdd.social.feed.FeedPresenter;
import com.bdd.social.feed.FeedView;
import com.bdd.social.model.Message;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;

public class FeedActivity extends AppCompatActivity implements FeedView {

    private Toolbar toolbar;

    RecyclerView recyclerView;
    EditText inputText;
    View sendButton;

    @Inject
    FeedPresenter feedPresenter;
    private MessageAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feed_activity);
        ((App) getApplication()).getFeather().injectFields(this);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.chat);

        inputText = findViewById(R.id.inputText);
        sendButton = findViewById(R.id.sendButton);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MessageAdapter(LayoutInflater.from(this));
        recyclerView.setAdapter(adapter);

        feedPresenter.attachView(this);
    }

    @Override
    protected void onDestroy() {
        feedPresenter.detachView(this);
        super.onDestroy();
    }

    @Override
    public void setFeed(List<Message> messages) {
        adapter.setMessages(messages);
    }

    @Override
    public void addNewMessage(Message message) {
        adapter.addNewMessage(message);
        recyclerView.scrollToPosition(adapter.getItemCount() - 1);
    }

    @Override
    public void showProgress(boolean progress) {

    }

    @Override
    public void showError(Throwable throwable) {
        Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public Observable<String> messageSentObservable() {
        return RxView.clicks(sendButton)
                .map(o -> inputText.getText().toString());
    }

    static class MessageAdapter extends RecyclerView.Adapter<MessageViewHolder> {

        LayoutInflater layoutInflater;
        List<Message> messages = new ArrayList<>();

        public MessageAdapter(LayoutInflater layoutInflater) {
            this.layoutInflater = layoutInflater;
        }

        @Override
        public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MessageViewHolder(layoutInflater.inflate(R.layout.feed_item, parent, false));
        }

        @Override
        public void onBindViewHolder(MessageViewHolder holder, int position) {
            holder.setMessage(messages.get(position));
        }

        @Override
        public int getItemCount() {
            return messages.size();
        }

        public void setMessages(List<Message> messages) {
            this.messages.clear();
            this.messages.addAll(messages);
            notifyDataSetChanged();
        }

        public void addNewMessage(Message message) {
            messages.add(message);
            notifyDataSetChanged();
        }
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {

        private TextView author;
        private TextView text;
        private TextView date;

        public MessageViewHolder(View itemView) {
            super(itemView);
            author = itemView.findViewById(R.id.author);
            text = itemView.findViewById(R.id.text);
            date = itemView.findViewById(R.id.date);
        }


        public void setMessage(Message message) {
            author.setText(message.author.name);
            text.setText(message.text);
            final Date date = message.date;
            this.date.setText(date != null ? date.toString() : "");
        }
    }
}
