package com.nietky.librarythingbrowser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.googlecode.jcsv.CSVStrategy;
import com.googlecode.jcsv.reader.CSVEntryParser;
import com.googlecode.jcsv.reader.CSVReader;
import com.googlecode.jcsv.reader.internal.CSVReaderBuilder;

public class BookListActivity extends FragmentActivity implements
        BookListFragment.Callbacks {

    private boolean mTwoPane;
    private static Intent intent;
    public static final String MESSAGE_TABLE_NAME = "com.nietky.librarythingbrowser.TABLE_NAME";
    private InputStreamReader inputStreamReader = null;
    private static final String TAG = "BookListActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);

        if (findViewById(R.id.book_detail_container) != null) {
            mTwoPane = true;
            ((BookListFragment) getSupportFragmentManager().findFragmentById(
                    R.id.book_list)).setActivateOnItemClick(true);
        }

        intent = getIntent();
        if (intent.getAction() == Intent.ACTION_VIEW) {
            importData();
        } else if (intent.hasExtra("tagName")) {
            ((BookListFragment) getSupportFragmentManager().findFragmentById(
                    R.id.book_list)).loadTag(intent.getStringExtra("tagName"));
        } else if (intent.hasExtra("collectionName")) {
            ((BookListFragment) getSupportFragmentManager().findFragmentById(
                    R.id.book_list)).loadCollection(intent.getStringExtra("collectionName"));
        }
    }

    public void importData() {
        Uri uri = intent.getData();
        Log.d(TAG, "Intent contains uri=" + uri);

        // Date presentTime = Calendar.getInstance().getTime();
        // SimpleDateFormat dateFormatter = new SimpleDateFormat(
        // "yyyyMMddhhmmss");
        // String newTableName = "booksFrom" +
        // dateFormatter.format(presentTime);
        // Log.d(TAG, "Intended table name=" + newTableName);

        Log.d(TAG, "Opening InputStreamReader for " + uri + "...");
        InputStream inputStream = null;
        try {
            inputStream = getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }

        Log.d(TAG, "Creating CSVReader...");
        try {
            inputStreamReader = new InputStreamReader(inputStream, "utf-16");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        CSVReader<String[]> csvReader = new CSVReaderBuilder<String[]>(
                inputStreamReader)
                .strategy(new CSVStrategy('\t', '\b', '#', true, true))
                .entryParser(new EntryParser()).build();

        Log.d(TAG, "Reading csvData...");
        List<String[]> csvData = null;
        try {
            csvData = csvReader.readAll();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "Successfully read csvData");

        ImportBooksTask task = new ImportBooksTask();
        task.execute(csvData);
    }

    public class EntryParser implements CSVEntryParser<String[]> {
        public String[] parseEntry(String... data) {
            return data;
        }
    }

    private class ImportBooksTask extends
            AsyncTask<List<String[]>, Void, String> {
        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(BookListActivity.this);
            dialog.setTitle("Importing books...");
            dialog.setMessage("This could take a few minutes.");
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(List<String[]>... csvDatas) {
            List<String[]> csvData = csvDatas[0];
            String newTableName = "books";
            Log.d(TAG, "Opening " + newTableName + " in internal database...");
            DbHelperNew dbHelper = new DbHelperNew(getApplicationContext());
            dbHelper.open();
            dialog.setMax(csvData.size());
            dbHelper.Db.beginTransaction();
            try {
                for (int i = 0; i < csvData.size(); i++) {
                    String[] csvRow = csvData.get(i);
                    dbHelper.addRow(Arrays.copyOfRange(csvRow, 0,
                            csvRow.length - 1));
                    dbHelper.Db.yieldIfContendedSafely();
                    dialog.setProgress(i);
                }
                dbHelper.Db.setTransactionSuccessful();
            } finally {
                dbHelper.Db.endTransaction();
            }
            dbHelper.close();

            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();
            ((BookListFragment) getSupportFragmentManager().findFragmentById(
                    R.id.book_list)).loadList();
        }
    }

    public void onItemSelected(String id) {
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putString(BookDetailFragment.ARG_ITEM_ID, id);
            BookDetailFragment fragment = new BookDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.book_detail_container, fragment).commit();

        } else {
            Intent detailIntent = new Intent(this, BookDetailActivity.class);
            detailIntent.putExtra(BookDetailFragment.ARG_ITEM_ID, id);
            startActivityForResult(detailIntent, -1);
        }
    }
}
