package com.nietky.librarythingbrowser;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class BookDetailFragment extends Fragment {

    public static final String ARG_ITEM_ID = "item_id";
    public static final String TAG = "BookDetailFragment";

    private String id; 
    private DbAdapter dbAdapter;
    private Cursor cursor;
    private TextView text;
    private String author;

    public BookDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(ARG_ITEM_ID)) {
            id = getArguments().getString(ARG_ITEM_ID);
        }
        dbAdapter = new DbAdapter(getActivity());
        dbAdapter.createDb();        
        dbAdapter.createDb();      
        dbAdapter.openDb();
        cursor = dbAdapter.getAllData();
        dbAdapter.close();
        
        int position = Integer.valueOf(id);
        Log.d(TAG, "position=" + position);
        cursor.moveToPosition(position);
        int index = cursor.getColumnIndex("author1");
        Log.d(TAG, "index=" + index);
        author = cursor.getString(index);
        Log.d(TAG, "author=" + author);
        
    }
    

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_book_detail, container, false);
        text = (TextView) rootView.findViewById(R.id.book_detail);
        text.setText(author);
        return rootView;
    }
}
