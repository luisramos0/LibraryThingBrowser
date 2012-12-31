package com.nietky.librarythingbrowser;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class ImportData extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_import_data, menu);
        return true;
    }

}
