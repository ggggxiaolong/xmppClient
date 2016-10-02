package com.github.ggggxiaolong.xmpp.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.github.ggggxiaolong.xmpp.service.XMPPService;
import com.github.ggggxiaolong.xmpp.utils.Common;
import com.github.ggggxiaolong.xmpp.utils.XMPPUtil;
import com.github.ggggxiaolong.xmpp.utils.ThreadUtil;

import timber.log.Timber;

/**
 * Created by mrtan on 10/2/16.
 */

public final class InternetReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        //连接服务器，唤醒服务
        if (Common.isNetworkAvailable(context) && !XMPPUtil.isConnected()) {
            Timber.i("internet is connected");
            ThreadUtil.runONWorkThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (XMPPUtil.connect(null)) {
                            XMPPUtil.login();
                            context.startService(new Intent(context, XMPPService.class));
                        }
                    } catch (Exception e) {
                        Timber.e(e);
                    }
                }
            });
        }
    }
}
