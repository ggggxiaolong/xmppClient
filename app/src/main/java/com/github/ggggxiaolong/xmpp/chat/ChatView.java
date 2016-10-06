package com.github.ggggxiaolong.xmpp.chat;

import com.github.ggggxiaolong.xmpp.base.BaseView;
import com.github.ggggxiaolong.xmpp.datasource.model.Chat;

import java.util.List;

/**
 * @author mrtan
 * @version v1.0
 * date 10/6/16
 */

interface ChatView extends BaseView {
    void loadDate(List<Chat> chats);

    void setTitle(String title);

    void scrollToBottom();

    void addData(Chat chat);

    void addData(List<Chat> chats);

    void finish();

    void sendError();

    void sendSuccess();

    void cancelRefresh();

    void noMoreChat();
}
