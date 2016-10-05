package com.github.ggggxiaolong.xmpp.datasource.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.github.ggggxiaolong.xmpp.datasource.local.DataBase;
import com.squareup.sqldelight.ColumnAdapter;
import com.squareup.sqldelight.EnumColumnAdapter;

import org.immutables.value.Value;
import org.jivesoftware.smack.packet.Presence;

import java.util.ArrayList;
import java.util.List;

@Value.Immutable(copy = false)
@Value.Style(allParameters = true)
public abstract class Contact implements ContactModel {

    private static final ColumnAdapter<Presence.Mode> MODE_ADAPTER = EnumColumnAdapter.create(Presence.Mode.class);
    private static final ColumnAdapter<Presence.Type> TYPE_ADAPTER = EnumColumnAdapter.create(Presence.Type.class);
    static final Factory<Contact> FACTORY = new Factory<Contact>(ImmutableContact::of, MODE_ADAPTER, TYPE_ADAPTER);

    public static long insertOrUpdate(Contact contact) {
        int i = update(contact);
        if (i > 0) {
            return i;
        }
        return DataBase.getWritableDB().insert(TABLE_NAME, null, FACTORY.marshal(contact).asContentValues());
    }

    //同步联系人
    public static long insertAll(List<Contact> contacts) {
        SQLiteDatabase db = DataBase.getWritableDB();
        db.beginTransaction();
        try {
            db.delete(TABLE_NAME, null, null);
            for (Contact contact : contacts) {
                db.insert(TABLE_NAME, null, FACTORY.marshal(contact).asContentValues());
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
        return contacts.size();
    }

    public static int delete(@NonNull String userId) {
        return DataBase.getWritableDB().delete(TABLE_NAME, USER_ID + " = ?", new String[]{userId});
    }

    public static int deleteAll() {
        return DataBase.getReadableDB().delete(TABLE_NAME, "", new String[0]);
    }

    public static int update(@NonNull Contact contact) {
        ContentValues values = FACTORY.marshal(contact).asContentValues();
        return DataBase.getWritableDB().update(TABLE_NAME, values, USER_ID + "= ?", new String[]{contact.user_id()});
    }

    public static int update(@NonNull String userId, String status, Presence.Mode mode, Presence.Type type) {
        ContentValues values = FACTORY.marshal().p_status(status).p_mode(mode).p_type(type).asContentValues();
        return DataBase.getWritableDB().update(TABLE_NAME, values, USER_ID + "= ?", new String[]{userId});
    }

    public static List<Contact> selectAll() {
        Cursor cursor = DataBase.getReadableDB().rawQuery(SELECT_ALL, new String[0]);
        ArrayList<Contact> contacts = new ArrayList<>();
        while (cursor.moveToNext()) {
            contacts.add(FACTORY.select_allMapper().map(cursor));
        }
        cursor.close();
        return contacts;
    }

    public static Contact query(@NonNull String userId) {
        Cursor cursor = DataBase.getReadableDB().rawQuery(SELECT_USER, new String[]{userId});
        Contact contact = null;
        if (cursor.moveToFirst()) {
            contact = FACTORY.select_userMapper().map(cursor);
        }
        cursor.close();
        return contact;
    }
}
