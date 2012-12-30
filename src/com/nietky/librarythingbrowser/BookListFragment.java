package com.nietky.librarythingbrowser;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.nietky.librarythingbrowser.dummy.DummyContent;

public class BookListFragment extends ListFragment {

    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    private Callbacks mCallbacks = sDummyCallbacks;
    private int mActivatedPosition = ListView.INVALID_POSITION;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TestAdapter mDbHelper = new TestAdapter(getActivity());        
        mDbHelper.createDatabase();      
        mDbHelper.open();

        Cursor testdata = mDbHelper.getTestData();
        
        
        mDbHelper.close();
//        
//        DataBaseHelper dbHelper = new DataBaseHelper(getActivity());
//        dbHelper.openDataBase();
//        SQLiteDatabase database = dbHelper.getWritableDatabase();
////        database.query(distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit, cancellationSignal)
////        database.query(table, columns, selection, selectionArgs, groupBy, having, orderBy)
////        database.query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limit)
//        Cursor c = database.query("books", new String[] {"_id", "title"}, 
//                                  null, null, null, null, "_id");
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                getActivity(), 
                android.R.layout.simple_list_item_1,
                testdata, 
                new String[] {"title"},
                new int[] {android.R.id.text1},
                1);
        setListAdapter(adapter);
//        setListAdapter(new ArrayAdapter<DummyContent.DummyItem>(getActivity(),
//                R.layout.simple_list_item_activated_1,
//                R.id.text1,
//                DummyContent.ITEMS));
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null && savedInstanceState
                .containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = sDummyCallbacks;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);
        mCallbacks.onItemSelected(DummyContent.ITEMS.get(position).id);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    public void setActivateOnItemClick(boolean activateOnItemClick) {
        getListView().setChoiceMode(activateOnItemClick
                ? ListView.CHOICE_MODE_SINGLE
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
}
