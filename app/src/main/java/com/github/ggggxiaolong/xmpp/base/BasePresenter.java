package com.github.ggggxiaolong.xmpp.base;

import android.app.Fragment;

/**
 * Created by mrtan on 9/27/16.
 */

public abstract class BasePresenter<V extends BaseView> {
    protected V mView;

    public void onAttach(V view){
        mView = view;
    }

    public void onDetach(){
        mView = null;
    }

}
