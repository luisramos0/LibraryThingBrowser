package com.nietky.librarythingbrowser;

import java.io.IOException;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DbAdapter {
    protected static final String TAG = "DbAdapter";

    private final Context mContext;
    private SQLiteDatabase mDb;
    private DbHelper mDbHelper;

    public DbAdapter(Context context) {
        this.mContext = context;
        mDbHelper = new DbHelper(mContext);
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

    public Cursor getAllData() {
        try {
            String sql = "SELECT * FROM books";

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
}