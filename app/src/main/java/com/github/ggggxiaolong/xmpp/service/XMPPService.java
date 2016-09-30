package com.github.ggggxiaolong.xmpp.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.github.ggggxiaolong.xmpp.utils.ObjectHolder;
import com.github.ggggxiaolong.xmpp.utils.ThreadUtil;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterListener;

import java.util.Collection;
import java.util.Set;

import timber.log.Timber;

public class XMPPService extends Service {

    private ChatManager mChatManager;
    private Roster mRoster;

    @Override
    public void onCreate() {
        Timber.i("tservice is create");
        Timber.i("service on create");
        if (ObjectHolder.connection == null) {
            Timber.e("XMPP connection was not connected!");
            return;
        }
        ThreadUtil.runONWorkThread(new Runnable() {
            @Override
            public void run() {
                Timber.i("同步花名册");
                mRoster = Roster.getInstanceFor(ObjectHolder.connection);
                Set<RosterEntry> entries = mRoster.getEntries();
                for (RosterEntry entry : entries) {
                    Timber.i("groups : %s ;", entry.getGroups().get(0).getName());
                    Timber.i("name : %s ;", entry.getName());
                    Timber.i("status : %s ;", entry.getStatus());
                    Timber.i("type : %s ;", entry.getType());
                    Timber.i("user : %s \n", entry.getUser());
                }
                //监听花名册的变更

                mRoster.addRosterListener(mRosterListener);
                //监听消息的改变
                mChatManager = ChatManager.getInstanceFor(ObjectHolder.connection);

                mChatManager.addChatListener(mChatManagerListener);

                ObjectHolder.connection.addAsyncStanzaListener(new StanzaListener() {
                    @Override
                    public void processPacket(Stanza packet) throws SmackException.NotConnectedException {
                        Timber.i(packet.toString());
                    }
                }, null);
            }
        });
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new Inner();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Timber.i("onStartCommand");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Timber.i("onDestroy");
        super.onDestroy();
    }

    public class Inner extends Binder {
    }

    //监听花名册的改变
    private RosterListener mRosterListener = new RosterListener() {
        @Override
        public void entriesAdded(Collection<String> addresses) {
            Timber.i("花名册增加了");
            for (String address : addresses) {
                Timber.i("address : %s", address);
            }
        }

        @Override
        public void entriesUpdated(Collection<String> addresses) {
            Timber.i("花名册更新了");
            for (String address : addresses) {
                Timber.i("address : %s", address);
            }
        }

        @Override
        public void entriesDeleted(Collection<String> addresses) {
            Timber.i("花名册减少了");
            for (String address : addresses) {
                Timber.i("address : %s", address);
            }
        }

        @Override
        public void presenceChanged(Presence presence) {
            Timber.i("状态改变了");
            Timber.i("目的地 : %s", presence.getTo());
            Timber.i("发起者 : %s", presence.getFrom());
            Timber.i("status : %s", presence.getStatus());
            Timber.i("mode : %s", presence.getMode());
        }
    };

    private ChatManagerListener mChatManagerListener = new ChatManagerListener() {
        @Override
        public void chatCreated(Chat chat, boolean createdLocally) {
            if (createdLocally) {
                Timber.i("主动创建了一个会话");
            } else {
                Timber.i("被动创建了一个会话");
            }
            Timber.i("会话ID : %s", chat.getThreadID());

            chat.addMessageListener(mChatMessageListener);
        }
    };
    private ChatMessageListener mChatMessageListener = new ChatMessageListener() {
        @Override
        public void processMessage(Chat chat, Message message) {
            Timber.i("会话ID：%s", chat.getThreadID());
            Timber.i("会话发起者：%s", message.getFrom());
            Timber.i("会话目标：%s", message.getTo());
            Timber.i("会话主题：%s", message.getSubject());
            Timber.i("会话类型：%s", message.getType());
            Timber.i("会话内容：%s", message.getBody());
        }
    };
}
