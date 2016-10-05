package com.github.ggggxiaolong.xmpp.setting;

import com.github.ggggxiaolong.xmpp.base.BasePresenter;
import com.github.ggggxiaolong.xmpp.utils.CommonField;
import com.github.ggggxiaolong.xmpp.utils.XMPPUtil;
import com.github.ggggxiaolong.xmpp.utils.PreferencesUtils;
import com.github.ggggxiaolong.xmpp.utils.ThreadUtil;

import timber.log.Timber;

public class ServerPresenter extends BasePresenter<ServerView> {

    void Connect(final String name, final String address, final int port) {
        ThreadUtil.runONWorkThread(() -> {
            try {
                XMPPUtil.connect(name, address, port, null);
                saveConfig(name, address, port);
                ThreadUtil.runOnUIThread(() -> mView.connectSuccess());
            } catch (Exception e) {
                Timber.e(e);
                ThreadUtil.runOnUIThread(() -> {
                    mView.connectFail();
                });

            }
        });
    }

    public void Connect() {
        ThreadUtil.runONWorkThread(() -> {
            try {
                XMPPUtil.connect(null);
                ThreadUtil.runOnUIThread(() -> mView.connectSuccess());
            } catch (Exception e) {
                Timber.e(e);
                ThreadUtil.runOnUIThread(() -> mView.connectFail());
            }
        });
    }

    private void saveConfig(String name, String address, int port) {
        PreferencesUtils.getEditor()
                .putString(CommonField.SERVER_NAME, name)
                .putString(CommonField.SERVER_ADDRESS, address)
                .putInt(CommonField.SERVER_PORT, port)
                .apply();
    }

}
