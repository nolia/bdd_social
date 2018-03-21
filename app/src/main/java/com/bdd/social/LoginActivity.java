package com.bdd.social;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bdd.social.login.LoginPresenter;
import com.bdd.social.login.LoginView;
import com.jakewharton.rxbinding2.view.RxView;

import javax.inject.Inject;

import io.reactivex.Observable;

public class LoginActivity extends AppCompatActivity implements LoginView {

    EditText loginEditText;
    EditText passwordEditText;
    View loginButton;
    ProgressBar progressBar;

    @Inject
    LoginPresenter loginPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        ((App) getApplication()).getFeather().injectFields(this);

        loginEditText = findViewById(R.id.loginEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        progressBar = findViewById(R.id.progressBar);

        loginPresenter.attachView(this);
    }

    @Override
    protected void onDestroy() {
        loginPresenter.detachView(this);
        super.onDestroy();
    }

    @Override
    public String getLogin() {
        return loginEditText.getText().toString().trim();
    }

    @Override
    public String getPassword() {
        return passwordEditText.getText().toString().trim();
    }

    @Override
    public void showProgress(boolean show) {
        loginEditText.setEnabled(!show);
        passwordEditText.setEnabled(!show);
        loginButton.setEnabled(!show);

        progressBar.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public Observable<View> getOnLoginClickObservable() {
        return RxView.clicks(loginButton)
                .map(o -> loginButton)
                .filter(v -> validateInputs())
                ;
    }

    private boolean validateInputs() {
        if (TextUtils.isEmpty(getLogin())) {
            Toast.makeText(this, "Please, input valid email", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(getPassword())) {
            Toast.makeText(this, "Please, input valid password", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void goToNextScreen() {
        Toast.makeText(this, "HOORRAY!!!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, FeedActivity.class));
        finish();
    }

    @Override
    public void showError(Throwable throwable) {
        Log.e("Login", "error!!!", throwable);
        Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
    }
}
