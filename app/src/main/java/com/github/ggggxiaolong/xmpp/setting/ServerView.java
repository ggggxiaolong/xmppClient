package com.github.ggggxiaolong.xmpp.setting;

import com.github.ggggxiaolong.xmpp.base.BaseView;

/**
 * Created by mrtan on 10/1/16.
 */

public interface ServerView extends BaseView {
    /**
     * 连接失败
     */
    void connectFail();

    /**
     * 连接成功
     */
    void connectSuccess();

}
