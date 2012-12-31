package com.nietky.librarythingbrowser;

import java.io.IOException;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.util.Log;

public class DbAdapter {
    protected static final String TAG = "DbAdapter";
    protected static final String[] fieldnames = { "_id", "book_id", "title",
            "author1", "author2", "author_other", "publication", "date",
            "ISBNs", "series", "source", "lang1", "lang2", "lang_orig", "LCC",
            "DDC", "bookcrossing", "date_entered", "date_acquired",
            "date_started", "date_ended", "stars", "collections", "tags",
            "review", "summary", "comments", "comments_private", "copies",
            "encoding" };

    private final Context mContext;
    private SQLiteDatabase mDb;
    private DbHelper mDbHelper;
    
    private SharedPreferences sharedPref;

    public DbAdapter(Context context) {
        this.mContext = context;
        mDbHelper = new DbHelper(mContext);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    public DbAdapter createDb() throws SQLException {
        try {
            mDbHelper.createDb();
        } catch (IOException mIOException) {
            Log.e(TAG, mIOException.toString() + "  UnableToCreateDb");
            throw new Error("UnableToCreateDb");
        }
        return this;
    }

    public DbAdapter openDb() throws SQLException {
        try {
            mDbHelper.openDb();
            mDbHelper.close();
            mDb = mDbHelper.getReadableDatabase();
        } catch (SQLException mSQLException) {
            Log.e(TAG, "open >>" + mSQLException.toString());
            throw mSQLException;
        }
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    public Cursor searchOneCol(String tableName, String columnName,
            String search) {
        try {
            String sql = "SELECT * FROM " + tableName + " WHERE " + columnName
                    + " LIKE '%" + search + "%' ORDER BY " + columnName;

            Cursor mCur = mDb.rawQuery(sql, null);
            if (mCur != null) {
                mCur.moveToNext();
            }
            return mCur;
        } catch (SQLException mSQLException) {
            Log.e(TAG, "getTestData >>" + mSQLException.toString());
            throw mSQLException;
        }
    }

    public Cursor searchAllCols(String tableName, String search, String orderByColumn) {
        try {
            String sql = "SELECT * FROM " + tableName + " WHERE ";
            for (int i = 0; i < fieldnames.length; i++) {
                if (fieldnames[i] == "publication" && !sharedPref.getBoolean("search_publication", false))
                    continue;
                sql += fieldnames[i] + " LIKE '%" + search + "%'"; 
                if (i == (fieldnames.length - 1)) {
                    sql += "ORDER BY " + orderByColumn; 
                } else {
                    sql += " OR ";
                }
            }
            Cursor mCur = mDb.rawQuery(sql, null);
            if (mCur != null) {
                mCur.moveToNext();
            }
            return mCur;
        } catch (SQLException mSQLException) {
            Log.e(TAG, "getTestData >>" + mSQLException.toString());
            throw mSQLException;
        }
    }

    public Cursor getRow(String tableName, String id) {
        String sql = "SELECT * FROM " + tableName + " WHERE _id='" + id + "'";
        Cursor mCur = mDb.rawQuery(sql, null);
        return mCur;
    }
}