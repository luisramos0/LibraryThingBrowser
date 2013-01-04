package com.nietky.librarythingbrowser;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class ImportActivity extends Activity {
    TextView textView;
    private static final String TAG = "ImportActivity";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);
        // Show the Up button in the action bar.
        // getActionBar().setDisplayHomeAsUpEnabled(true);

        textView = (TextView) findViewById(R.id.text1);
        Log.d(TAG, "textView loaded");
        
        // declare the dialog as a member field of your activity
        ProgressDialog mProgressDialog;

        // instantiate it within the onCreate method
        mProgressDialog = new ProgressDialog(ImportActivity.this);
        mProgressDialog.setMessage("A message");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setMax(100);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        // execute this when the downloader must be fired
        DownloadFile downloadFile = new DownloadFile();
        downloadFile.execute("http://www.librarything.com/export-tab");

    }

    // usually, subclasses of AsyncTask are declared inside the activity class.
    // that way, you can easily modify the UI thread from here
    private class DownloadFile extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... sUrl) {
            try {
                Log.d(TAG, "checking out that url...");
                URL url = new URL(sUrl[0]);
                URLConnection connection = url.openConnection();
                connection.connect();
                Log.d(TAG, "connected");
                // this will be useful so that you can show a typical 0-100%
                // progress bar
                int fileLength = connection.getContentLength();

                // download the file
                InputStream input2 = new BufferedInputStream(url.openStream());
                InputStreamReader input = new InputStreamReader(input2, "utf-16");
                Log.d(TAG, "");
                Log.d(TAG, "inputstreamreader open");

                String str = convertStreamToString(input2);
                Log.d(TAG, str);
                Log.d(TAG, "OVVVEERRRRRRRRRRR");
                textView.setText(str);
//                char data[] = new char[1024];
//                long total = 0;
//                int count;
//                while ((count = input.read(data)) != -1) {
//                    total += count;
//                    // publishing the progress....
//                    textView.setText(data.toString());
//                    Log.d(TAG, "setText: " + data.toString());
//                }
                input.close();
                input2.close();
            } catch (Exception e) {
            }
            return null;
        }
    }
    
    public static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_import, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
