package com.github.ggggxiaolong.xmpp.user;

import android.content.Intent;

import com.github.ggggxiaolong.xmpp.base.BasePresenter;
import com.github.ggggxiaolong.xmpp.service.XMPPService;
import com.github.ggggxiaolong.xmpp.utils.ObjectHolder;
import com.github.ggggxiaolong.xmpp.utils.PreferencesUtils;
import com.github.ggggxiaolong.xmpp.utils.ThreadUtil;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import timber.log.Timber;

/**
 * 登陆的业务类
 */

public final class LoginPresenter extends BasePresenter<LoginView> {

    void Login(final String server, final String username, final String password) {
        if (ObjectHolder.connection != null) {
            closeConnect();
        }
        String[] split = server.split(":");
        if (split.length != 2) {
            mView.serverAddressError();
        }
        final String address = split[0];
        int port = 0;
        try {
            port = Integer.decode(split[1]);
        } catch (NumberFormatException e) {
            mView.serverAddressError();
        }
        XMPPTCPConnectionConfiguration.Builder builder = XMPPTCPConnectionConfiguration.builder();
        builder.setUsernameAndPassword(username, password);
        builder.setServiceName("mrtan");
        builder.setHost(address);
        builder.setPort(port);
        builder.setResource("android");
        //todo 添加证书
        builder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        final XMPPTCPConnection connection = new XMPPTCPConnection(builder.build());
        final int finalPort = port;
        ThreadUtil.runONWorkThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Timber.i("start connect");
                    connection.connect();
                    Timber.i("start login");
                    connection.login();
                    ObjectHolder.connection = connection;
                    save(address, finalPort, username, password);
                    //开启服务
                    ObjectHolder.context.startService(new Intent(ObjectHolder.context, XMPPService.class));
                    ThreadUtil.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            mView.loginSuccess();
                        }
                    });
                    return;
                } catch (Exception e) {
                    Timber.e(e);
                }
                ThreadUtil.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        mView.loginFail();
                    }
                });
            }
        });
    }

    private void save(final String server, final int port, final String username, final String password) {
        ThreadUtil.runONWorkThread(new Runnable() {
            @Override
            public void run() {
                PreferencesUtils.putString("im_server", server);
                PreferencesUtils.putString("im_password", password);
                PreferencesUtils.putString("im_username", username);
                PreferencesUtils.putInt("im_port", port);
            }
        });
    }

    //与服务器建立连接
    private void closeConnect() {
        ObjectHolder.connection.disconnect();
        ObjectHolder.connection = null;
    }
}
