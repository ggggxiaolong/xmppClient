package com.github.ggggxiaolong.xmpp.datasource.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.ggggxiaolong.xmpp.datasource.local.DataBase;
import com.squareup.sqldelight.RowMapper;

import org.immutables.value.Value;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by mrtan on 10/3/16.
 */
@Value.Immutable
@Value.Style(allParameters = true)
public abstract class Chat implements ChatModel {

    public static final ChatModel.Factory<Chat> FACTORY = new Factory<>(ImmutableChat::of);
    public static final RowMapper<Session> FOR_CHAT_MAPPER = FACTORY.for_chatMapper(new For_chatCreator<Chat, Contact, Session>() {
        @Override
        public Session create(@NonNull Chat chat, @NonNull Contact contact, @Nullable Long max_time) {
            return ImmutableSession.of(chat, contact, max_time);
        }
    }, Contact.FACTORY);


    public static long insert(ImmutableChat chat) {
        return DataBase.getWritableDB().insert(TABLE_NAME, null, FACTORY.marshal(chat).asContentValues());
    }

    public static long insert(String from, String type) {
        long time = new Date().getTime();
        ContentValues values = FACTORY.marshal().from_id(from).type(type).time(time).asContentValues();
        return DataBase.getWritableDB().insert(TABLE_NAME, null, values);
    }

    //根据用户查询，与该用户的所有对话
    public static List<Chat> byFrom(@NonNull String from) {
        ArrayList<Chat> chats = new ArrayList<>();
        Cursor cursor = DataBase.getReadableDB().rawQuery(FOR_FROM_ID, new String[]{from});
        while (cursor.moveToNext()) {
            chats.add(FACTORY.for_from_idMapper().map(cursor));
        }
        cursor.close();
        return chats;
    }

    //查询和所有用户的最近对话
    public static List<Session> getContextChat() {
        ArrayList<Session> models = new ArrayList<>();
        Cursor cursor = DataBase.getReadableDB().rawQuery(FOR_CHAT, new String[0]);
        while (cursor.moveToNext()) {
            models.add(FOR_CHAT_MAPPER.map(cursor));
        }
        cursor.close();
        return models;
    }

    @Value.Immutable
    @Value.Style(allParameters = true)
    public interface Session extends For_chatModel<Chat, Contact> {}
}
