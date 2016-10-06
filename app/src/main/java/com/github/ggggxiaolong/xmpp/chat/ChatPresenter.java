package com.github.ggggxiaolong.xmpp.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import com.github.ggggxiaolong.xmpp.base.BasePresenter;
import com.github.ggggxiaolong.xmpp.datasource.model.Chat;
import com.github.ggggxiaolong.xmpp.datasource.model.Contact;
import com.github.ggggxiaolong.xmpp.datasource.model.ImmutableChat;
import com.github.ggggxiaolong.xmpp.utils.CommonField;
import com.github.ggggxiaolong.xmpp.utils.ObjectHolder;
import com.github.ggggxiaolong.xmpp.utils.ThreadUtil;
import com.github.ggggxiaolong.xmpp.utils.XMPPUtil;

import org.jivesoftware.smack.SmackException;

import java.util.Date;
import java.util.List;

import timber.log.Timber;

import static android.text.TextUtils.isEmpty;
import static com.github.ggggxiaolong.xmpp.utils.CommonField.HOLDER_ME;
import static com.github.ggggxiaolong.xmpp.utils.CommonField.HOLDER_OTHER;
import static com.github.ggggxiaolong.xmpp.utils.CommonField.INTENT_CONTENT;
import static com.github.ggggxiaolong.xmpp.utils.CommonField.INTENT_FORM;
import static com.github.ggggxiaolong.xmpp.utils.CommonField.INTENT_HOLDER;
import static com.github.ggggxiaolong.xmpp.utils.CommonField.INTENT_TIME;

/**
 * @author mrtan
 * @version v1.0
 * date 10/6/16
 * 增加-----
 * 判断是否还有数据
 */

final class ChatPresenter extends BasePresenter<ChatView> {
    private String fromId;
    private LocalBroadcastManager mBroadcastManager;
    private Intent mIntent;
    private int pager = 0;
    private long lastTime;

    @Override
    public void onAttach(ChatView view) {
        super.onAttach(view);
        mBroadcastManager = ObjectHolder.getBroadcastManager();
        mBroadcastManager.registerReceiver(mChatReceiver, new IntentFilter(CommonField.RECEIVER_CHAT));
        mIntent = new Intent(CommonField.RECEIVER_CHAT);
    }

    void setFromId(String from){
        fromId = from;
        check();
    }

    void onLoad(){
        check();
        pager = 0;
        Contact mContact = Contact.query(fromId);
        mView.setTitle(mContact.user_name());
        List<Chat> chats = Chat.byFrom(fromId, pager++, new Date().getTime());
        mView.loadDate(chats);
        lastTime = chats.get(chats.size()-1).time();
    }

    void loadMore(){
        if (!Chat.hasMore(fromId, lastTime)) {
            mView.noMoreChat();
            mView.cancelRefresh();
            return;
        }
        List<Chat> chats = Chat.byFrom(fromId, pager++, lastTime);
        lastTime = chats.get(chats.size()-1).time();
        mView.addData(chats);
        mView.cancelRefresh();
    }

    private void check(){
        if (isEmpty(fromId)){
            throw new RuntimeException("set the fromId first!");
        }
    }

    void send(String content){
        check();
        org.jivesoftware.smack.chat.Chat chat = XMPPUtil.getChat(fromId);
        try {
            chat.sendMessage(content);
        } catch (SmackException.NotConnectedException e) {
            Timber.e(e);
            mView.sendError();
            return;
        }
        chat.getThreadID();
        ImmutableChat chat1 = ImmutableChat.builder()
                .from_id(fromId)
                .content(content)
                .holder(true)
                .time(new Date().getTime()).build();
        mView.addData(chat1);
        mView.sendSuccess();

        ThreadUtil.runONWorkThread(() -> Chat.insert(chat1));
        mIntent.putExtra(INTENT_FORM, chat1.from_id());
        mIntent.putExtra(INTENT_CONTENT, chat1.content());
        mIntent.putExtra(INTENT_TIME, chat1.time());
        mIntent.putExtra(INTENT_HOLDER, HOLDER_ME);
        mBroadcastManager.sendBroadcast(mIntent);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mBroadcastManager.unregisterReceiver(mChatReceiver);
    }

    private BroadcastReceiver mChatReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra(CommonField.INTENT_FORM) && intent.hasExtra(CommonField.INTENT_CONTENT)) {
                String from = intent.getStringExtra(CommonField.INTENT_FORM);
                String content = intent.getStringExtra(CommonField.INTENT_CONTENT);
                int holder = intent.getIntExtra(INTENT_HOLDER, 0);
                if (holder != HOLDER_OTHER){
                    mView.scrollToBottom();
                    return;
                }
                long time = intent.getLongExtra(CommonField.INTENT_TIME, -1);
                if (time == -1) {
                    time = new Date().getTime();
                }
                ImmutableChat chat1 = ImmutableChat.builder()
                        .from_id(from)
                        .content(content)
                        .holder(false)
                        .time(time).build();
                mView.addData(chat1);
            }
        }
    };
}
