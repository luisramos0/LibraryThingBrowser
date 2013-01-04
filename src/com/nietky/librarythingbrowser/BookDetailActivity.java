package com.nietky.librarythingbrowser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class BookDetailActivity extends Activity {
    public static final String TAG = "BookDetailActivity";

    private String id;
    private Cursor cursor;
    private Context context;

    private HashMap<String, String> fields;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);
        
        context = this;
        
        Intent intent = getIntent();
        id = intent.getStringExtra("_id");
        DbHelperNew dbHelper = new DbHelperNew(getApplicationContext());
        dbHelper.open();
        cursor = dbHelper.getRow(id);
        cursor.moveToFirst();
        Log.d(TAG, "getting Row id=" + id);

        fields = new HashMap<String, String>();

        String fieldname;
        String[] fieldnames = { "_id", "book_id", "title", "author1",
                "author2", "author_other", "publication", "date", "ISBNs",
                "series", "source", "lang1", "lang2", "lang_orig", "LCC",
                "DDC", "bookcrossing", "date_entered", "date_acquired",
                "date_started", "date_ended", "stars", "collections", "tags",
                "review", "summary", "comments", "comments_private", "copies",
                "encoding" };
        for (int i = 0; i < fieldnames.length; i += 1) {
            fieldname = fieldnames[i];
            Log.d(TAG, "Getting content for fieldname=" + fieldname);
            int index = cursor.getColumnIndex(fieldname);
            String content = "";
            if (index > -1)
                content = cursor.getString(index);
            content = content.replace("[return]", "\n");
            fields.put(fieldname, content);
        }
        dbHelper.close();
        
        setTitle(fields.get("title"));

        TextView title_view = (TextView) findViewById(R.id.book_detail_title);
        title_view.setText(fields.get("title"));

        TextView author_view = (TextView) findViewById(R.id.book_detail_author);
        String author;
        String author1 = fields.get("author1");
        String author2 = fields.get("author2");
        if (author2 == "") {
            author = author1;
        } else {
            author = author2;
        }
        author_view.setText(author);
        author_view.setTag(author);
        author_view.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                TextView self = (TextView) v;
                String author = (String) self.getTag();
//                Toast.makeText(getActivity(), tagTagText, 10).show();
                Intent resultIntent = new Intent(context,
                        BookListActivity.class);
                resultIntent.putExtra("author1Name", author);
                startActivity(resultIntent);
            }
        });
        

        TextView publication_date_view = (TextView) findViewById(R.id.book_detail_publication_date);
        String publication_details = fields.get("publication");
        if (publication_details == "")
            publication_details = fields.get("date");
        publication_date_view.setText(publication_details);

        LinearLayout ll = (LinearLayout) findViewById(R.id.book_detail_tags_container);
        ArrayList<String> tagNames = new ArrayList<String>();
        Collections.addAll(tagNames, fields.get("tags").split(","));

        Display display = getWindowManager().getDefaultDisplay();
        int maxWidth = display.getWidth() - 10;

        if (tagNames.size() > 0) {
            LinearLayout llAlso = new LinearLayout(context);
            llAlso.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
                    LayoutParams.WRAP_CONTENT));
            llAlso.setOrientation(LinearLayout.HORIZONTAL);

            TextView tvIntro = new TextView(context);
            tvIntro.setText("Tags:");

            llAlso.addView(tvIntro);
            tvIntro.measure(0, 0);

            int widthSoFar = tvIntro.getMeasuredWidth();
            for (String tagText : tagNames) {
                TextView tvTag = new TextView(context, null,
                        android.R.attr.textColorLink);
                tvTag.setText(tagText);
                tvTag.setTextSize(14);
                tvTag.setTextColor(Color.BLACK);
                tvTag.setBackgroundResource(R.drawable.rounded_edges_tag);
                tvTag.setTag(tagText);
                tvTag.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        TextView self = (TextView) v;
                        String tagTagText = (String) self.getTag();
//                        Toast.makeText(getActivity(), tagTagText, 10).show();
                        Intent resultIntent = new Intent(context,
                                BookListActivity.class);
                        resultIntent.putExtra("tagName", tagTagText);
                        startActivity(resultIntent);
                    }
                });

                LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(
                        LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                int left_right = 5;
                int top_bottom = 3;
                llp.setMargins(left_right, top_bottom, left_right, top_bottom); // llp.setMargins(left,
                                                                                // top,
                                                                                // right,
                                                                                // bottom);
                tvTag.setLayoutParams(llp);

                tvTag.measure(0, 0);
                widthSoFar += tvTag.getMeasuredWidth() + left_right * 2;

                if (widthSoFar >= maxWidth) {
                    ll.addView(llAlso);

                    llAlso = new LinearLayout(context);
                    llAlso.setLayoutParams(new LayoutParams(
                            LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
                    llAlso.setOrientation(LinearLayout.HORIZONTAL);

                    llAlso.addView(tvTag);
                    widthSoFar = tvTag.getMeasuredWidth();
                } else {

                    llAlso.addView(tvTag);
                }
            }

            ll.addView(llAlso);
        }

        
        
        
        
        LinearLayout ll2 = (LinearLayout) findViewById(R.id.book_detail_collections_container);
        ArrayList<String> collectionNames = new ArrayList<String>();
        Collections.addAll(collectionNames, fields.get("collections").split(","));

        Display display2 = getWindowManager().getDefaultDisplay();
        int maxWidth2 = display2.getWidth() - 10;

        if (collectionNames.size() > 0) {
            LinearLayout llAlso = new LinearLayout(context);
            llAlso.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
                    LayoutParams.WRAP_CONTENT));
            llAlso.setOrientation(LinearLayout.HORIZONTAL);

            TextView tvIntro = new TextView(context);
            tvIntro.setText("Collections:");

            llAlso.addView(tvIntro);
            tvIntro.measure(0, 0);

            int widthSoFar = tvIntro.getMeasuredWidth();
            for (String collectionText : collectionNames) {
                TextView tvTag = new TextView(context, null,
                        android.R.attr.textColorLink);
                tvTag.setText(collectionText);
                tvTag.setTextSize(14);
                tvTag.setTextColor(Color.BLACK);
                tvTag.setBackgroundResource(R.drawable.rounded_edges_collection);
                tvTag.setTag(collectionText);
                tvTag.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        TextView self = (TextView) v;
                        String collectionTagText = (String) self.getTag();
//                        Toast.makeText(getActivity(), collectionTagText, 10).show();
                        Intent resultIntent = new Intent(context, BookListActivity.class);
                        resultIntent.putExtra("collectionName", collectionTagText);
                        startActivity(resultIntent);
                    }
                });

                LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(
                        LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                int left_right = 5;
                int top_bottom = 3;
                llp.setMargins(left_right, top_bottom, left_right, top_bottom); // llp.setMargins(left,
                                                                                // top,
                                                                                // right,
                                                                                // bottom);
                tvTag.setLayoutParams(llp);

                tvTag.measure(0, 0);
                widthSoFar += tvTag.getMeasuredWidth() + left_right * 2;

                if (widthSoFar >= maxWidth) {
                    ll.addView(llAlso);

                    llAlso = new LinearLayout(this);
                    llAlso.setLayoutParams(new LayoutParams(
                            LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
                    llAlso.setOrientation(LinearLayout.HORIZONTAL);

                    llAlso.addView(tvTag);
                    widthSoFar = tvTag.getMeasuredWidth();
                } else {

                    llAlso.addView(tvTag);
                }
            }

            ll2.addView(llAlso);
        }
        
        TextView review_view = (TextView) findViewById(R.id.book_detail_review);
        review_view.setText(Html.fromHtml("<p><b>Review:</b> "
                + fields.get("review").replace("\n", "</p><p>")));

    }

}
