package com.github.ggggxiaolong.xmpp.utils;

import android.content.Intent;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import java.io.IOException;

import timber.log.Timber;

import static android.text.TextUtils.isEmpty;

/**
 * Created by mrtan on 10/2/16.
 */

public final class XMPPUtil {

    public static XMPPTCPConnection connection;
    
    /*public static void connect(String name, String address, int port) throws IOException, XMPPException, SmackException {
        Timber.i("connect to service");
        if (!isConnected()) {
            Timber.i("connect not null");
            closeConnected();
        }
        XMPPTCPConnectionConfiguration.Builder builder = XMPPTCPConnectionConfiguration.builder();
        builder.setServiceName(name);
        builder.setHost(address);
        builder.setPort(port);
        builder.setConnectTimeout(2000);
        //todo 添加证书
        builder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        final XMPPTCPConnection connection = new XMPPTCPConnection(builder.build());
        connection.connect();
        connection = connection;
    }*/

    public static void connect(String name, String address, int port, ConnectionListener listener) throws IOException, XMPPException, SmackException {
        Timber.i("connect to service");
        if (isConnected()) {
            Timber.i("connect not null");
            connection.removeConnectionListener(listener);
            closeConnected();
        }
        XMPPTCPConnectionConfiguration.Builder builder = XMPPTCPConnectionConfiguration.builder();
        builder.setServiceName(name);
        builder.setHost(address);
        builder.setPort(port);
        builder.setConnectTimeout(2000);
        //todo 添加证书
        builder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        final XMPPTCPConnection conn = new XMPPTCPConnection(builder.build());
        conn.connect();
        if (listener != null) {
            conn.addConnectionListener(listener);
        }
        connection = conn;
    }

    public static void login( String userName, String password) throws IOException, XMPPException, SmackException {
        if (!isConnected()){
            throw new RuntimeException("call the connect first!");
        }
        connection.login(userName, password, "android");
    }

    public static boolean login(){
        String username = PreferencesUtils.getString(CommonField.USER_NAME);
        String password = PreferencesUtils.getString(CommonField.USER_PASSWORD);
        if (!isEmpty(username) && isEmpty(password)){
            try {
                connection.login(username, password, "android");
                return true;
            } catch (Exception e) {
                Timber.e(e);
                return false;
            }
        }
        return false;
    }

    public static boolean connect(ConnectionListener listener) throws IOException, XMPPException, SmackException {
        String name = PreferencesUtils.getString(CommonField.SERVER_NAME);
        String address = PreferencesUtils.getString(CommonField.SERVER_ADDRESS);
        int port = PreferencesUtils.getInt(CommonField.SERVER_PORT);
        if (isEmpty(address) || isEmpty(name) || port == 0) {
            return false;
        } else {
            connect(name, address, port, listener);
        }
        return true;
    }

    public static void addConnectionListener(ConnectionListener listener) {
        if (connection != null) {
            connection.addConnectionListener(listener);
        }
    }

    public static void removeConnectionListener(ConnectionListener listener) {
        if (connection != null) {
            connection.removeConnectionListener(listener);
        }
    }

    public static boolean isConnected() {
        return connection != null && connection.isConnected();
    }

    public static boolean isLogin() {
        return connection != null && connection.isConnected() && connection.isAuthenticated();
    }

    public static void closeConnected() {
        if (isConnected()) {
            connection.disconnect();
            connection = null;
        }
    }

    public static class ConnectionListenerWrapper implements ConnectionListener {
        //连接到服务器成功
        @Override
        public void connected(XMPPConnection connection) {
            Timber.i("connected");
        }

        //登陆成功
        @Override
        public void authenticated(XMPPConnection connection, boolean resumed) {
            Timber.i("authenticated");
        }

        //连接断开
        @Override
        public void connectionClosed() {
            Timber.i("connectionClosed");
        }

        //异常断开
        @Override
        public void connectionClosedOnError(Exception e) {
            Timber.i("connectionClosedOnError");
        }

        //重连成功
        @Override
        public void reconnectionSuccessful() {
            Timber.i("reconnectionSuccessful");
        }

        @Override
        public void reconnectingIn(int seconds) {
            Timber.i("reconnectingIn");

        }
        //重连失败
        @Override
        public void reconnectionFailed(Exception e) {
            Timber.i("reconnectionFailed");
        }
    }
}
