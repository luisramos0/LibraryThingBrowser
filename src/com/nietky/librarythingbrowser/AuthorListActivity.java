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

public class AuthorListActivity extends Activity {
    ArrayList<String> authors;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author_list);

        listView = (ListView) findViewById(R.id.authorListView);

        Intent intent = getIntent();
        String searchFilter = intent.getStringExtra("searchFilter");

        DbHelperNew dbHelper = new DbHelperNew(getApplicationContext());
        dbHelper.open();
        String columnName = "title";
        Cursor cursor = dbHelper.searchAllCols(searchFilter, columnName);

        authors = CursorTags.getAuthors1(cursor);
        Collections.sort(authors, String.CASE_INSENSITIVE_ORDER);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, authors);

        listView.setAdapter(adapter);
        setTitle("Authors (" + authors.size() + "):");

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                String author = authors.get(position);
                Intent resultIntent = new Intent(parent.getContext(), BookListActivity.class);
                resultIntent.putExtra("author1Name", author);
                startActivity(resultIntent);
            }
        });

    }
}
