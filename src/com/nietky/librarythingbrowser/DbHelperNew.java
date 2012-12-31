package com.nietky.librarythingbrowser;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelperNew extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "LibraryThing";
    private static String DATABASE_PATH = "";
    private static SQLiteDatabase Db;
    private static String TABLE;
    private static Context context;
    private static final String[] KEYS = { "book_id", "title", "author1",
            "author2", "author_other", "publication", "date", "ISBNs",
            "series", "source", "lang1", "lang2", "lang_orig", "LCC", "DDC",
            "bookcrossing", "date_entered", "date_acquired", "date_started",
            "date_ended", "stars", "collections", "tags", "review", "summary",
            "comments", "comments_private", "copies", "encoding" };

    public DbHelperNew(Context contextLocal, String tableName) {
        super(contextLocal, DATABASE_NAME, null, DATABASE_VERSION);
        TABLE = tableName;
        DATABASE_PATH = "/data/data/" + contextLocal.getPackageName()
                + "/databases/";
        context = contextLocal;
    }

    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE + "("
                + "_id INTEGER PRIMARY KEY" + ", book_id INTEGER"
                + ", title TEXT" + ", author1 TEXT" + ", author2 TEXT"
                + ", author_other TEXT" + ", publication TEXT" + ", date TEXT"
                + ", ISBNs TEXT" + ", series TEXT" + ", source TEXT"
                + ", lang1 TEXT" + ", lang2 TEXT" + ", lang_orig TEXT"
                + ", LCC TEXT" + ", DDC TEXT" + ", bookcrossing TEXT"
                + ", date_entered TEXT" + ", date_acquired TEXT"
                + ", date_started TEXT" + ", date_ended TEXT" + ", stars REAL"
                + ", collections TEXT" + ", tags TEXT" + ", review TEXT"
                + ", summary TEXT" + ", comments TEXT"
                + ", comments_private TEXT" + ", copies INTEGER"
                + ", encoding TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    public boolean open() throws SQLException {
        String path = DATABASE_PATH + DATABASE_NAME;
        Db = this.getWritableDatabase();
        return Db != null;
    }

    public void addRow(String[] values) {
        ContentValues cvalues = new ContentValues();
        for (int i = 0; i < values.length; i++)
            cvalues.put(KEYS[i], values[i]);
        Db.insert(TABLE, null, cvalues);
        Db.close();
    }

}
