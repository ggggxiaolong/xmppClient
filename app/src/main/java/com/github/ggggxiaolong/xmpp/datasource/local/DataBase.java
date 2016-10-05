package com.github.ggggxiaolong.xmpp.datasource.local;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.github.ggggxiaolong.xmpp.datasource.model.Chat;
import com.github.ggggxiaolong.xmpp.datasource.model.Contact;
import com.github.ggggxiaolong.xmpp.utils.ObjectHolder;

/**
 * 本地数据库
 */

public final class DataBase extends SQLiteOpenHelper {

    private static DataBase instance;

    private DataBase() {
        super(ObjectHolder.context, "xmpp_db", null, 1);
    }

    private static DataBase getInstance() {
        if (instance == null) {
            synchronized (DataBase.class) {
                if (instance == null) {
                    instance = new DataBase();
                }
            }
        }
        return instance;
    }

    public static SQLiteDatabase getReadableDB(){
        return getInstance().getReadableDatabase();
    }

    public static SQLiteDatabase getWritableDB(){
        return getInstance().getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Contact.CREATE_TABLE);
        db.execSQL(Chat.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //
    }
}
