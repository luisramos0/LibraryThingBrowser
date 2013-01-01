package com.nietky.librarythingbrowser;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.util.Log;

public class DbHelperNew extends SQLiteOpenHelper {
    private static final String TAG = "DbHelperNew";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "LibraryThing";
    private static String DATABASE_PATH = "";
    public static SQLiteDatabase Db;
    private static String TABLE = "books";
    private static Context context;
    private static final String[] KEYS = { "book_id", "title", "author1",
            "author2", "author_other", "publication", "date", "ISBNs",
            "series", "source", "lang1", "lang2", "lang_orig", "LCC", "DDC",
            "bookcrossing", "date_entered", "date_acquired", "date_started",
            "date_ended", "stars", "collections", "tags", "review", "summary",
            "comments", "comments_private", "copies", "encoding" };
    private static String[] ALL_KEYS = { "_id", "book_id", "title", "author1",
            "author2", "author_other", "publication", "date", "ISBNs",
            "series", "source", "lang1", "lang2", "lang_orig", "LCC", "DDC",
            "bookcrossing", "date_entered", "date_acquired", "date_started",
            "date_ended", "stars", "collections", "tags", "review", "summary",
            "comments", "comments_private", "copies", "encoding" };
    private SharedPreferences sharedPref;

    public DbHelperNew(Context contextLocal) {
        super(contextLocal, DATABASE_NAME, null, DATABASE_VERSION);
        DATABASE_PATH = "/data/data/" + contextLocal.getPackageName()
                + "/databases/";
        context = contextLocal;
        sharedPref = PreferenceManager.getDefaultSharedPreferences(contextLocal
                .getApplicationContext());
        Log.d(TAG, "Helper ready, looking at " + DATABASE_PATH);
    }

    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE + "("
                + "_id INTEGER PRIMARY KEY" + ", book_id TEXT" + ", title TEXT"
                + ", author1 TEXT" + ", author2 TEXT" + ", author_other TEXT"
                + ", publication TEXT" + ", date TEXT" + ", ISBNs TEXT"
                + ", series TEXT" + ", source TEXT" + ", lang1 TEXT"
                + ", lang2 TEXT" + ", lang_orig TEXT" + ", LCC TEXT"
                + ", DDC TEXT" + ", bookcrossing TEXT" + ", date_entered TEXT"
                + ", date_acquired TEXT" + ", date_started TEXT"
                + ", date_ended TEXT" + ", stars TEXT" + ", collections TEXT"
                + ", tags TEXT" + ", review TEXT" + ", summary TEXT"
                + ", comments TEXT" + ", comments_private TEXT"
                + ", copies TEXT" + ", encoding TEXT" + ")";
        Log.d(TAG, "onCreate, running SQL: " + CREATE_CONTACTS_TABLE);
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
        Log.d(TAG, KEYS.length + " key labels; " + values.length + " values.");
        ContentValues cvalues = new ContentValues();
        for (int i = 0; i < values.length; i++)
            cvalues.put(KEYS[i], values[i]);
        Db.insert(TABLE, null, cvalues);
    }

    public void close() {
        Db.close();
        Log.d(TAG, "close method called.");
    }

    public void delete() {
        Db = this.getWritableDatabase();
        Db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(Db);
    }
    
    public Cursor searchAllCols(String search, String orderByColumn) {
        try {
            String sql = "SELECT * FROM " + TABLE + " WHERE ";
            for (int i = 0; i < ALL_KEYS.length; i++) {
                if (ALL_KEYS[i] == "publication"
                        && !sharedPref.getBoolean("search_publication", false))
                    continue;
                sql += ALL_KEYS[i] + " LIKE '%" + search + "%'";
                if (i == (ALL_KEYS.length - 1)) {
                    sql += "ORDER BY " + orderByColumn;
                } else {
                    sql += " OR ";
                }
            }
            Cursor cursor = Db.rawQuery(sql, null);
            if (cursor != null) {
                cursor.moveToNext();
            }
            return cursor;
        } catch (SQLException mSQLException) {
            Log.e(TAG, "getTestData >>" + mSQLException.toString());
            throw mSQLException;
        }
    }
    
    public Cursor searchTag(String search, String orderByColumn) {
        try {
            String sql = "SELECT * FROM " + TABLE + " WHERE ";
            sql += "tags LIKE '%" + search + "%'";
            sql += " ORDER BY " + orderByColumn;
            Cursor cursor = Db.rawQuery(sql, null);
            if (cursor != null) {
                cursor.moveToNext();
            }
            return cursor;
        } catch (SQLException mSQLException) {
            Log.e(TAG, "getTestData >>" + mSQLException.toString());
            throw mSQLException;
        }
    }
    
    public Cursor searchCollection(String search, String orderByColumn) {
        try {
            String sql = "SELECT * FROM " + TABLE + " WHERE ";
            sql += "collections LIKE '%" + search + "%'";
            sql += " ORDER BY " + orderByColumn;
            Cursor cursor = Db.rawQuery(sql, null);
            if (cursor != null) {
                cursor.moveToNext();
            }
            return cursor;
        } catch (SQLException mSQLException) {
            Log.e(TAG, "getTestData >>" + mSQLException.toString());
            throw mSQLException;
        }
    }
    
    public Cursor getRow(String id) {
        String sql = "SELECT * FROM " + TABLE + " WHERE _id='" + id + "'";
        Cursor cursor = Db.rawQuery(sql, null);
        return cursor;
    }

}
