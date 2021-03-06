package com.nietky.librarythingbrowser;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toast;

public class BookListFragment extends ListFragment implements
        OnQueryTextListener {

    private static final String STATE_ACTIVATED_POSITION = "activated_position";
    public final static String MESSAGE_TABLE_NAME = "com.nietky.librarythingbrowser.TABLE_NAME";
    public static final int RESULT_TAG_SELECT = 1;
    public static final int RESULT_COLLECTION_SELECT = 2;

    private Callbacks mCallbacks = sDummyCallbacks;
    private int mActivatedPosition = ListView.INVALID_POSITION;
    private Cursor cursor;
    private BookListCursorAdapter adapter;
    public String searchFilter = "%";
    public String tagFilter = "%";
    public String collectionFilter = "%";
    private String tableName = "books";

    private SharedPreferences sharedPref;
    private SharedPreferences.Editor prefsEdit;

    public interface Callbacks {

        public void onItemSelected(String id);
    }

    private static Callbacks sDummyCallbacks = new Callbacks() {
        public void onItemSelected(String id) {
        }
    };

    public BookListFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = PreferenceManager
                .getDefaultSharedPreferences(getActivity()
                        .getApplicationContext());
        loadList();
    }

    public void loadList() {
        DbHelperNew dbHelper = new DbHelperNew(getActivity()
                .getApplicationContext());
        dbHelper.open();

        String columnName = "title";
        cursor = dbHelper.searchAllCols(searchFilter, columnName);
        if (searchFilter != "%")
            getActivity().setTitle(searchFilter);
        else
            getActivity().setTitle("All books");

        dbHelper.close();

        adapter = new BookListCursorAdapter(getActivity(), cursor);
        setListAdapter(adapter);
    }

    public void loadTag(String tag) {
        if (tag.contains("'")) {
            Toast.makeText(
                    getActivity(),
                    "filtering by terms including apostrophes is not supported yet.. sorry!",
                    7).show();
            return;
        }
        DbHelperNew dbHelper = new DbHelperNew(getActivity()
                .getApplicationContext());
        dbHelper.open();

        tagFilter = tag;
        String columnName = "title";
        cursor = dbHelper.searchTag(tag, columnName);
        getActivity().setTitle(tag);
        dbHelper.close();

        adapter = new BookListCursorAdapter(getActivity(), cursor);
        setListAdapter(adapter);
    }

    public void loadCollection(String collection) {
        if (collection.contains("'")) {
            Toast.makeText(
                    getActivity(),
                    "filtering by terms including apostrophes is not supported yet.. sorry!",
                    7).show();
            return;
        }
        DbHelperNew dbHelper = new DbHelperNew(getActivity()
                .getApplicationContext());
        dbHelper.open();

        String columnName = "title";
        cursor = dbHelper.searchCollection(collection, columnName);
        getActivity().setTitle(collection);
        dbHelper.close();

        adapter = new BookListCursorAdapter(getActivity(), cursor);
        setListAdapter(adapter);
    }
    
    public void loadAuthor(String author) {
        if (author.contains("'")) {
            Toast.makeText(
                    getActivity(),
                    "filtering by terms including apostrophes is not supported yet.. sorry!",
                    7).show();
            return;
        }
        DbHelperNew dbHelper = new DbHelperNew(getActivity()
                .getApplicationContext());
        dbHelper.open();

        String columnName = "author1";
        cursor = dbHelper.searchAuthor(author, columnName);
        getActivity().setTitle(author);
        dbHelper.close();

        adapter = new BookListCursorAdapter(getActivity(), cursor);
        setListAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_book_list,
                container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState
                    .getInt(STATE_ACTIVATED_POSITION));
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException(
                    "Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = sDummyCallbacks;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position,
            long id) {
        super.onListItemClick(listView, view, position, id);
        cursor.moveToPosition(position);
        String _id = cursor.getString(cursor.getColumnIndex("_id"));
        mCallbacks.onItemSelected(_id);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != AdapterView.INVALID_POSITION) {
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    public void setActivateOnItemClick(boolean activateOnItemClick) {
        getListView().setChoiceMode(
                activateOnItemClick ? AbsListView.CHOICE_MODE_SINGLE
                        : AbsListView.CHOICE_MODE_NONE);
    }

    public void setActivatedPosition(int position) {
        if (position == AdapterView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Place an action bar item for searching.
        getActivity().getMenuInflater().inflate(R.menu.options, menu);
        // MenuItem item = menu.add("Search");
        MenuItem item = menu.findItem(R.id.menuSearch);
        // item.setIcon(android.R.drawable.ic_menu_search);
        // item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        SearchView sv = new SearchView(getActivity());
        sv.setOnQueryTextListener(this);
        item.setActionView(sv);
        //
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menuPreferences:
            goToPreferences();
            return true;
        case R.id.menuSearch:
            getActivity().onSearchRequested();
            return true;
        case R.id.menuDelete:
            AlertDialog show = new AlertDialog.Builder(getActivity())
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Delete books")
                    .setMessage(
                            "Are you sure you want to delete all your books?\n\n(This is only for data imported into LibraryThing Browser on your device, and cannot affect your LibraryThing account).")
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                        int which) {
                                    String tableName = "books";
                                    DbHelperNew dbHelper = new DbHelperNew(
                                            getActivity()
                                                    .getApplicationContext());
                                    dbHelper.delete();
                                    loadList();
                                }

                            }).setNegativeButton("No", null).show();
            return true;
        case R.id.menuTags:
            Intent intentTags = new Intent(getActivity(), TagListActivity.class);
            intentTags.putExtra("searchFilter", searchFilter);
            startActivity(intentTags);
            return true;
        case R.id.menuCollections:
            Intent intentCollections = new Intent(getActivity(),
                    CollectionListActivity.class);
            intentCollections.putExtra("searchFilter", searchFilter);;
            startActivity(intentCollections);
            return true;
        case R.id.menuAuthors:
            Intent intentAuthors = new Intent(getActivity(), AuthorListActivity.class);
            intentAuthors.putExtra("searchFilter", searchFilter);
            startActivity(intentAuthors);
        default:
            return false;
        }
    }

    public void goToPreferences() {
        Log.d("blf", "opening application settings");
        Intent i = new Intent(getActivity(), SettingsActivity.class);
        startActivity(i);
    }

    public boolean onQueryTextChange(String newText) {
        // Called when the action bar search text has changed. Update
        // the search filter, and restart the loader to do a new query
        // with this filter.
        if (newText.contains("'")) {
            Toast.makeText(getActivity(),
                    "search terms cannot includes apostrophes yet.. sorry!", 7)
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
        public BookListCursorAdapter(Context context, Cursor c) {
            super(context, c);
            inflater = LayoutInflater.from(context);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView titleTV = (TextView) view.findViewById(R.id.book_list_item_title);
            TextView subTitleTV = (TextView) view.findViewById(R.id.book_list_item_subtitle);
            titleTV.setText(cursor.getString(cursor.getColumnIndex("title")));
            subTitleTV.setText(cursor.getString(cursor.getColumnIndex("author2")));
//            if (cursor.getString(cursor.getColumnIndex("tags")).contains("unread")) {
//                view.setBackgroundColor(Color.GRAY);
//            } else 
//                view.setBackgroundColor(Color.BLACK);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return inflater.inflate(R.layout.book_list_item, parent, false);
        }
    }
}
