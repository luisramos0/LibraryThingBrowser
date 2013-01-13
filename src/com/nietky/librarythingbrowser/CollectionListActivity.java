package com.nietky.librarythingbrowser;

import java.util.ArrayList;
import java.util.Collections;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class CollectionListActivity extends Activity {
    ArrayList<String> collections;
    ListView listView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_list);
        
        listView = (ListView) findViewById(R.id.collectionListView);

        Intent intent = getIntent();
        String searchFilter = intent.getStringExtra("searchFilter");
        
        DbHelperNew dbHelper = new DbHelperNew(getApplicationContext());
        dbHelper.open();
        String columnName = "title";
        Cursor cursor = dbHelper.searchAllCols(searchFilter, columnName);
        
        collections = CursorTags.getCollections(cursor);
        Collections.sort(collections, String.CASE_INSENSITIVE_ORDER);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, collections);
        
        listView.setAdapter(adapter);
        setTitle("Collections (" + collections.size() + "):");
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                String collection = collections.get(position);
                Intent resultIntent = new Intent(parent.getContext(), BookListActivity.class);
                resultIntent.putExtra("collectionName", collection);
                startActivity(resultIntent);
            }
        });
    }
    
    protected void onListItemClick(ListView l, View v, int position, long id) {
        // String selection = l.getItemAtPosition(position).toString();
        String collection = collections.get(position);
        Intent resultIntent = new Intent(this, BookListActivity.class);
        resultIntent.putExtra("collectionName", collection);
        startActivity(resultIntent);
    }

}
