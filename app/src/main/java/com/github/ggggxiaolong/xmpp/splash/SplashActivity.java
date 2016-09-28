package com.github.ggggxiaolong.xmpp.splash;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.ggggxiaolong.xmpp.R;
import com.github.ggggxiaolong.xmpp.user.LoginActivity;

/**
 * 闪屏页3s后进入登录页
 */
public class SplashActivity extends AppCompatActivity implements SplashView {

    private SplashPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mPresenter = new SplashPresenter();
        mPresenter.onAttach(this);
        mPresenter.onLoad();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mPresenter.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDetach();
    }

    @Override
    public void login() {
        startActivity(new Intent(getApplication(), LoginActivity.class));
    }
}
