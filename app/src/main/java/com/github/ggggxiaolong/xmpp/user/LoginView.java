package com.github.ggggxiaolong.xmpp.user;

import com.github.ggggxiaolong.xmpp.base.BaseView;

/**
 * Created by mrtan on 9/28/16.
 */

public interface LoginView extends BaseView {
    void loginSuccess();
    void loginFail();
    void serverAddressError();
}
