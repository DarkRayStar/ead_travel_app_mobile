package com.ead.train_management.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Data Base Helper class
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "train.db";
    private static final int DB_VERSION = 1;

    private static final String TABLE_USERS = "users";

    private static final String CREATE_TABLE_USERS = "CREATE TABLE users ( _id INTEGER PRIMARY KEY AUTOINCREMENT,nic TEXT,uid TEXT)";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }
}