package com.github.ggggxiaolong.xmpp.splash;

import android.os.SystemClock;

import com.github.ggggxiaolong.xmpp.base.BasePresenter;
import com.github.ggggxiaolong.xmpp.utils.ThreadUtil;

public class SplashPresenter extends BasePresenter <SplashView>{
    private boolean isBackPressed = false;
    public void onLoad(){
        ThreadUtil.runONWorkThread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(3000);
                if (isBackPressed){
                    mView.finish();
                } else {
                    mView.login();
                    mView.finish();
                }
            }
        });
    }

    public void onBackPressed(){
        isBackPressed = true;
    }
}
