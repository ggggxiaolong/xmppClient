package com.github.ggggxiaolong.xmpp.utils;

import android.content.Context;

import org.jivesoftware.smack.tcp.XMPPTCPConnection;

/**
 * 用于保存全局的Context，注意存入的Context为将Application的context，避免内存泄露
 * Application 可以启动一个 Activity，不过需要创建一个新的 task 任务队列。
 * 不可以创建Dialog
 */
public final class ObjectHolder {
    public static Context context;
    public static XMPPTCPConnection connection;
}