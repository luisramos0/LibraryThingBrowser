package com.nietky.librarythingbrowser;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.SimpleCursorAdapter;

public class BookListFragment extends ListFragment implements
        OnQueryTextListener {

    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    private Callbacks mCallbacks = sDummyCallbacks;
    private int mActivatedPosition = ListView.INVALID_POSITION;
    private Cursor cursor;
    private DbAdapter dbAdapter;
    private SimpleCursorAdapter adapter;
    private String mCurFilter = "%";

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
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        loadList();
    }

    public void loadList() {
        dbAdapter = new DbAdapter(getActivity());
        dbAdapter.createDb();
        dbAdapter.createDb();
        dbAdapter.openDb();
        String columnName = "title";
        cursor = dbAdapter.searchAllCols("books", mCurFilter, columnName);
        dbAdapter.close();

        adapter = new SimpleCursorAdapter(getActivity(),
                R.layout.book_list_item, cursor, new String[] { "title",
                        "author2" }, new int[] { R.id.book_list_item_title,
                        R.id.book_list_item_subtitle }, 1);
        setListAdapter(adapter);
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
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    public void setActivateOnItemClick(boolean activateOnItemClick) {
        getListView().setChoiceMode(
                activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
                        : ListView.CHOICE_MODE_NONE);
    }

    public void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
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
        case R.id.menu_preferences:
            goToSettings();
            return true;
        case R.id.menuSearch:
            getActivity().onSearchRequested();
        default:
            return false;
        }
    }

    public void goToSettings() {
        Log.d("blf", "opening application settings");
        Intent i = new Intent(getActivity(), SettingsActivity.class);
        startActivity(i);
    }
    
    public boolean onQueryTextChange(String newText) {
        // Called when the action bar search text has changed. Update
        // the search filter, and restart the loader to do a new query
        // with this filter.
        mCurFilter = !TextUtils.isEmpty(newText) ? newText : "%";
        loadList();
        // getLoaderManager().restartLoader(0, null, this);
        return true;
    }

    public boolean onQueryTextSubmit(String query) {
        // Don't care about this.
        return true;
    }
}
