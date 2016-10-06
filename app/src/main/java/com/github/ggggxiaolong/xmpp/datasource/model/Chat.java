package com.github.ggggxiaolong.xmpp.datasource.model;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.github.ggggxiaolong.xmpp.datasource.local.DataBase;
import com.squareup.sqldelight.RowMapper;

import org.immutables.value.Value;

import java.util.ArrayList;
import java.util.List;


@Value.Immutable
@Value.Style(allParameters = true)
public abstract class Chat implements ChatModel {

    private static final ChatModel.Factory<Chat> FACTORY = new Factory<>(ImmutableChat::of);

    private static final RowMapper<Session> FOR_CHAT_MAPPER = FACTORY.for_chatMapper(ImmutableSession::of, Contact.FACTORY);

    private static final int pageSize = 15;
    public static final String HAS_MORE = "SELECT 1 FROM "+ TABLE_NAME + " WHERE " + FROM_ID + "=? AND "+ TIME + " <?";

    public static long insert(ImmutableChat chat) {
        return DataBase.getWritableDB().insert(TABLE_NAME, null, FACTORY.marshal(chat).asContentValues());
    }

    //根据用户查询，与该用户的所有对话
    public static List<Chat> byFrom(@NonNull String from, int page, long time) {
        ArrayList<Chat> chats = new ArrayList<>();
        Cursor cursor = DataBase.getReadableDB().rawQuery(FOR_FROM_ID, new String[]{from, String.valueOf(time), "15", String.valueOf(page * pageSize)});
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

    //与改用户在改时间之前是否还有会话
    public static boolean hasMore(String from, long time){
        Cursor cursor = DataBase.getReadableDB().rawQuery(HAS_MORE, new String[]{from, String.valueOf(time)});
        boolean b = cursor.moveToNext();
        cursor.close();
        return b;
    }

    @Value.Immutable
    @Value.Style(allParameters = true)
    public interface Session extends For_chatModel<Chat, Contact> {
    }
}
