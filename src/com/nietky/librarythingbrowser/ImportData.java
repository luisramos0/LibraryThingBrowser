package com.nietky.librarythingbrowser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Scanner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

import com.googlecode.jcsv.CSVStrategy;
import com.googlecode.jcsv.reader.CSVEntryParser;
import com.googlecode.jcsv.reader.CSVReader;
import com.googlecode.jcsv.reader.internal.CSVReaderBuilder;

public class ImportData extends Activity {

    private static final String TAG = "ImportData";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_data);
        Log.d(TAG, "not even got to the Intent");
        Intent intent = getIntent();
        String data = intent.getDataString();
        TextView text1 = (TextView) findViewById(R.id.import_data_text1);
        text1.setText(data);
        text1.append("\n" + intent.getScheme());
        String content = "";
        Log.d(TAG, "just about to retrieve the data");
        InputStream inst = null;
        try {
            inst = getContentResolver().openInputStream(getIntent().getData());
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
        InputStreamReader instr = new InputStreamReader(inst);
        Log.d(TAG, "created InputStreamReader instr");
        CSVReader<String[]> csvReader = new CSVReaderBuilder<String[]>(instr)
                .strategy(new CSVStrategy('\t', '\b', '#', true, true))
                .entryParser(new EntryParser()).build();
        List<String[]> csvData = null;
        try {
            csvData = csvReader.readAll();
        } catch (IOException e) {
            e.printStackTrace();
        }
        text1.append("\n\n" + csvData.toString());
    }

    public class EntryParser implements CSVEntryParser<String[]> {
        public String[] parseEntry(String... data) {
            return data;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_import_data, menu);
        return true;
    }

    public static String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
