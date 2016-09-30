package com.github.ggggxiaolong.xmpp.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.github.ggggxiaolong.xmpp.MainActivity;
import com.github.ggggxiaolong.xmpp.R;
import com.github.ggggxiaolong.xmpp.customView.OwlView;
import com.sdsmdg.tastytoast.TastyToast;

import timber.log.Timber;

public class LoginActivity extends AppCompatActivity implements LoginView {

    private OwlView mOwlView;
    private EditText mServerEdit, mUsernameEdit, mPasswordEdit;
    private View mLoginView;
    private LoginPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mOwlView = (OwlView) findViewById(R.id.owl);
        mServerEdit = (EditText) findViewById(R.id.server);
        mUsernameEdit = (EditText) findViewById(R.id.username);
        mPasswordEdit = (EditText) findViewById(R.id.password);
        mLoginView = findViewById(R.id.login);

        mPasswordEdit.setOnFocusChangeListener(mFocusChangeListener);
        mLoginView.setOnClickListener(mOnClickListener);

        mPresenter = new LoginPresenter();
        mPresenter.onAttach(this);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String server = mServerEdit.getText().toString();
            String username = mUsernameEdit.getText().toString();
            String password = mPasswordEdit.getText().toString();
            Timber.i("server = %s, username = %s, password = %s", server, username, password);
            mPresenter.Login(server, username, password);
        }
    };

    private View.OnFocusChangeListener mFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                mOwlView.open();
            } else {
                mOwlView.close();
            }
        }
    };

    @Override
    public void loginSuccess() {
        TastyToast.makeText(getApplicationContext(), "Login Success!", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);
        startActivity(new Intent(getApplication(), MainActivity.class));
        finish();
    }

    @Override
    public void loginFail() {
        TastyToast.makeText(getApplicationContext(), "Login Fail!", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
    }

    @Override
    public void serverAddressError() {
        TastyToast.makeText(getApplicationContext(), "Server Error!", TastyToast.LENGTH_SHORT, TastyToast.WARNING);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDetach();
    }
}
