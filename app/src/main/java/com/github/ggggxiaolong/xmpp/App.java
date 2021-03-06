package com.github.ggggxiaolong.xmpp;

import android.app.Application;

import com.github.ggggxiaolong.xmpp.utils.ObjectHolder;

import im.fir.sdk.FIR;
import timber.log.Timber;

/**
 * Created by mrtan on 9/27/16.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG){
            Timber.plant(new Timber.DebugTree());
        }
        FIR.init(this);
        ObjectHolder.context = getApplicationContext();
    }

}
