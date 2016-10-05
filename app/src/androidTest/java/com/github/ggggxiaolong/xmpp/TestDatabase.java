package com.github.ggggxiaolong.xmpp;

import android.test.AndroidTestCase;

import com.github.ggggxiaolong.xmpp.datasource.local.DataBase;
import com.github.ggggxiaolong.xmpp.datasource.model.Chat;
import com.github.ggggxiaolong.xmpp.datasource.model.Contact;
import com.github.ggggxiaolong.xmpp.datasource.model.ImmutableChat;
import com.github.ggggxiaolong.xmpp.datasource.model.ImmutableContact;
import com.github.ggggxiaolong.xmpp.utils.ObjectHolder;

import org.jivesoftware.smack.packet.Presence;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TestDatabase extends AndroidTestCase {

    public void testAdd(){
        ImmutableContact tanxl3 = ImmutableContact.builder()
                .user_id("tanxl3@mrtan")
                .user_name("tanxl3")
                .pinyin("tanxl3@mrtan")
                .p_mode(Presence.Mode.available)
                .p_type(Presence.Type.unavailable)
                .build();
        ObjectHolder.context = getContext();
        Contact.insertOrUpdate(tanxl3);
    }

    public void testQuery(){
        ObjectHolder.context = getContext();
        List<Contact> contacts = Contact.selectAll();
        System.out.println(contacts.size());
        if (contacts.size()>0){
            System.out.println(contacts.get(0));
        }
    }

    public void testInstallAll(){
        ImmutableContact tanxl1 = ImmutableContact.builder()
                .user_id("tanxl1@mrtan")
                .user_name("tanxl1")
                .pinyin("tanxl1@mrtan")
                .p_mode(Presence.Mode.available)
                .p_type(Presence.Type.unavailable)
                .build();
        ImmutableContact tanxl2 = ImmutableContact.builder()
                .user_id("tanxl2@mrtan")
                .user_name("tanxl2")
                .pinyin("tanxl2@mrtan")
                .p_mode(Presence.Mode.available)
                .p_type(Presence.Type.unavailable)
                .build();
        ImmutableContact tanxl3 = ImmutableContact.builder()
                .user_id("tanxl3@mrtan")
                .user_name("tanxl3")
                .pinyin("tanxl3@mrtan")
                .p_mode(Presence.Mode.available)
                .p_type(Presence.Type.unavailable)
                .build();
        ArrayList<Contact> contacts = new ArrayList<>();
        contacts.add(tanxl1);
        contacts.add(tanxl2);
        contacts.add(tanxl3);
        ObjectHolder.context = getContext();
        Contact.insertAll(contacts);
        int size = Contact.selectAll().size();
        System.out.println(size);
        assertEquals(3,size);
    }

    public void testChatI(){
        String from_id = "tanxl3@mrtan";
        ImmutableChat chat = ImmutableChat.builder()
                .from_id(from_id)
                .content("你好啊")
                .holder(true)
                .time(new Date().getTime()).build();
        Chat.insert(chat);
        List<Chat> chats = Chat.byFrom(from_id);
        assertEquals(1, chats.size());
    }
}
