package com.nietky.librarythingbrowser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toast;

import com.googlecode.jcsv.CSVStrategy;
import com.googlecode.jcsv.reader.CSVEntryParser;
import com.googlecode.jcsv.reader.CSVReader;
import com.googlecode.jcsv.reader.internal.CSVReaderBuilder;

public class BookListActivity extends ListActivity implements OnQueryTextListener {

    private static Intent intent;
    public static final String MESSAGE_TABLE_NAME = "com.nietky.librarythingbrowser.TABLE_NAME";
    private InputStreamReader inputStreamReader = null;
    private static final String TAG = "BookListActivity";

    public static final int RESULT_TAG_SELECT = 1;
    public static final int RESULT_COLLECTION_SELECT = 2;

    private Cursor cursor;
    private BookListCursorAdapter adapter;
    public String searchFilter = "%";
    public String tagFilter = "%";
    public String collectionFilter = "%";

    @SuppressWarnings("unused")
    private SharedPreferences sharedPref;
    @SuppressWarnings("unused")
    private SharedPreferences.Editor prefsEdit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this
                .getApplicationContext());

        intent = getIntent();
        if (intent.getAction() == Intent.ACTION_VIEW) {
            importData();
        } else if (intent.getAction() == Intent.ACTION_SEARCH) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            searchFilter = query;
            loadList();
        } else if (intent.hasExtra("tagName")) {
            loadTag(intent.getStringExtra("tagName"));
        } else if (intent.hasExtra("collectionName")) {
            loadCollection(intent.getStringExtra("collectionName"));
        } else if (intent.hasExtra("author1Name")) {
            loadAuthor(intent.getStringExtra("author1Name"));
        } else {
            loadList();
        }
    }

    public void loadList() {
        DbHelperNew dbHelper = new DbHelperNew(this.getApplicationContext());
        dbHelper.open();

        String columnName = "title";
        cursor = dbHelper.searchAllCols(searchFilter, columnName);
        if (searchFilter != "%")
            this.setTitle(searchFilter);
        else
            this.setTitle("All books");

        dbHelper.close();

        adapter = new BookListCursorAdapter(this, cursor);
        setListAdapter(adapter);
    }

    public void loadTag(String tag) {
        if (tag.contains("'")) {
            Toast.makeText(
                    this,
                    "filtering by terms including apostrophes is not supported yet.. sorry!",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        DbHelperNew dbHelper = new DbHelperNew(this.getApplicationContext());
        dbHelper.open();

        tagFilter = tag;
        String columnName = "title";
        cursor = dbHelper.searchTag(tag, columnName);
        this.setTitle(tag);
        dbHelper.close();

        adapter = new BookListCursorAdapter(this, cursor);
        setListAdapter(adapter);
    }

    public void loadCollection(String collection) {
        if (collection.contains("'")) {
            Toast.makeText(
                    this,
                    "filtering by terms including apostrophes is not supported yet.. sorry!",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        DbHelperNew dbHelper = new DbHelperNew(this.getApplicationContext());
        dbHelper.open();

        String columnName = "title";
        cursor = dbHelper.searchCollection(collection, columnName);
        this.setTitle(collection);
        dbHelper.close();

        adapter = new BookListCursorAdapter(this, cursor);
        setListAdapter(adapter);
    }

    public void loadAuthor(String author) {
        if (author.contains("'")) {
            Toast.makeText(
                    this,
                    "filtering by terms including apostrophes is not supported yet.. sorry!",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        DbHelperNew dbHelper = new DbHelperNew(this.getApplicationContext());
        dbHelper.open();

        String columnName = "author1";
        cursor = dbHelper.searchAuthor(author, columnName);
        this.setTitle(author);
        dbHelper.close();

        adapter = new BookListCursorAdapter(this, cursor);
        setListAdapter(adapter);
    }

    @SuppressWarnings("unchecked")
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

        @SuppressWarnings("static-access")
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
                    String[] csvRowShort = new String[csvRow.length - 1];
                    for (int j = 0; j < (csvRowShort.length); j++) {
                        csvRowShort[j] = csvRow[j];
                    }
                    dbHelper.addRow(csvRowShort);
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
            loadList();
        }
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position,
            long id) {
        super.onListItemClick(listView, view, position, id);
        cursor.moveToPosition(position);
        String _id = cursor.getString(cursor.getColumnIndex("_id"));
        Intent detailIntent = new Intent(this, BookDetailActivity.class);
        detailIntent.putExtra("_id", _id);
        startActivity(detailIntent);
    }

    @SuppressWarnings("unused")
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Place an action bar item for searching.
        this.getMenuInflater().inflate(R.menu.options, menu);
        // MenuItem item = menu.add("Search");
        MenuItem item = menu.findItem(R.id.menuSearch);
        // item.setIcon(android.R.drawable.ic_menu_search);
        // item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
//        SearchView sv = new SearchView(this);
//        sv.setOnQueryTextListener(this);
//        item.setActionView(sv);
        //
    }

    @SuppressWarnings("unused")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menuPreferences:
            goToPreferences();
            return true;
        case R.id.menuSearch:
            this.onSearchRequested();
            return true;
        case R.id.menuDelete:
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Delete books")
                    .setMessage(
                            "Are you sure you want to delete all your books?\n\n(This is only for data imported into LibraryThing Browser on your device, and cannot affect your LibraryThing account).")
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                        int which) {
                                    String tableName = "books";
                                    DbHelperNew dbHelper = new DbHelperNew(getApplicationContext());
                                    dbHelper.delete();
                                    loadList();
                                }

                            }).setNegativeButton("No", null).show();
            return true;
        case R.id.menuTags:
            Intent intentTags = new Intent(this, TagListActivity.class);
            intentTags.putExtra("searchFilter", searchFilter);
            startActivity(intentTags);
            return true;
        case R.id.menuCollections:
            Intent intentCollections = new Intent(this,
                    CollectionListActivity.class);
            intentCollections.putExtra("searchFilter", searchFilter);
            ;
            startActivity(intentCollections);
            return true;
        case R.id.menuAuthors:
            Intent intentAuthors = new Intent(this, AuthorListActivity.class);
            intentAuthors.putExtra("searchFilter", searchFilter);
            startActivity(intentAuthors);
            return true;
        case R.id.menuImport:
            Intent intent = new Intent(this, ImportActivity.class);
            startActivity(intent);
            return true;
        default:
            return false;
        }
    }

    public void goToPreferences() {
        Log.d("blf", "opening application settings");
        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);
    }

    public boolean onQueryTextChange(String newText) {
        // Called when the action bar search text has changed. Update
        // the search filter, and restart the loader to do a new query
        // with this filter.
        if (newText.contains("'")) {
            Toast.makeText(this,
                    "search terms cannot includes apostrophes yet.. sorry!", Toast.LENGTH_SHORT)
                    .show();
            newText = newText.replace("'", "");
        }
        searchFilter = !TextUtils.isEmpty(newText) ? newText : "%";
        loadList();
        // getLoaderManager().restartLoader(0, null, this);
        return true;
    }

    public boolean onQueryTextSubmit(String query) {
        // Don't care about this.
        return true;
    }

    public class BookListCursorAdapter extends CursorAdapter {
        LayoutInflater inflater;

        @SuppressWarnings("deprecation")
        public BookListCursorAdapter(Context context, Cursor c) {
            super(context, c);
            inflater = LayoutInflater.from(context);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView titleTV = (TextView) view
                    .findViewById(R.id.book_list_item_title);
            TextView subTitleTV = (TextView) view
                    .findViewById(R.id.book_list_item_subtitle);
            titleTV.setText(cursor.getString(cursor.getColumnIndex("title")));
            subTitleTV.setText(cursor.getString(cursor
                    .getColumnIndex("author2")));
            // if
            // (cursor.getString(cursor.getColumnIndex("tags")).contains("unread"))
            // {
            // view.setBackgroundColor(Color.GRAY);
            // } else
            // view.setBackgroundColor(Color.BLACK);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return inflater.inflate(R.layout.book_list_item, parent, false);
        }
    }
}
