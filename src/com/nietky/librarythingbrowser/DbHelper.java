package com.nietky.librarythingbrowser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper {
    private static String TAG = "DbHelper"; // Tag just for the LogCat
                                                  // window
    // destination path (location) of our database on device
    private static String DB_PATH = "";
    private static String DB_NAME = "example";// Database name
    private SQLiteDatabase mDb;
    private final Context mContext;

    public DbHelper(Context context) {
        super(context, DB_NAME, null, 1);// 1? its Database Version
        DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        this.mContext = context;
    }

    public void createDb() throws IOException {
        // If database not exists copy it from the assets

        boolean mDbExist = checkDb();
        if (!mDbExist) {
            this.getReadableDatabase();
            this.close();
            try {
                // Copy the database from assests
                copyDb();
                Log.e(TAG, "createDatabase database created");
            } catch (IOException mIOException) {
                throw new Error("ErrorCopyingDb");
            }
        }
    }

    // Check that the database exists here: /data/data/your package/databases/Da
    // Name
    private boolean checkDb() {
        File dbFile = new File(DB_PATH + DB_NAME);
        // Log.v("dbFile", dbFile + "   "+ dbFile.exists());
        return dbFile.exists();
    }

    // Copy the database from assets
    private void copyDb() throws IOException {
        InputStream mInput = mContext.getAssets().open(DB_NAME);
        String outFileName = DB_PATH + DB_NAME;
        OutputStream mOutput = new FileOutputStream(outFileName);
        byte[] mBuffer = new byte[1024];
        int mLength;
        while ((mLength = mInput.read(mBuffer)) > 0) {
            mOutput.write(mBuffer, 0, mLength);
        }
        mOutput.flush();
        mOutput.close();
        mInput.close();
    }

    // Open the database, so we can query it
    public boolean openDb() throws SQLException {
        String mPath = DB_PATH + DB_NAME;
        // Log.v("mPath", mPath);
        mDb = SQLiteDatabase.openDatabase(mPath, null,
                SQLiteDatabase.CREATE_IF_NECESSARY);
        // mDb = SQLiteDatabase.openDatabase(mPath, null,
        // SQLiteDatabase.NO_LOCALIZED_COLLATORS);
        return mDb != null;
    }

    @Override
    public synchronized void close() {
        if (mDb != null)
            mDb.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        
    }

}