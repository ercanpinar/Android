package com.streethawk.streethawkapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.streethawk.library.feeds.ISHFeedItemObserver;
import com.streethawk.library.feeds.SHFeedItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Feeds extends AppCompatActivity implements ISHFeedItemObserver {
    TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feeds);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SHFeedItem.getInstance(this).registerFeedItemObserver(this);
        tv = (TextView) findViewById(R.id.feedview);
    }
    @Override
    public void onResume() {
        super.onResume();
        fetchFeedData(0);
        tv.setText("Reading feeeds...");

    }

    /**
     * Read feed data after the given offset number
     * @param offset
     */
    private void fetchFeedData(int offset){
        SHFeedItem.getInstance(this).readFeedData(offset);
    }
    private void updateTextView(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (null != tv) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv.setText(message);
                        }
                    });
                }
            }
        });
    }
    @Override
    public void shFeedReceived(final JSONArray value) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String NEWLINE = "\n";
                String texttmp = "Received Feeds " + NEWLINE;
                for (int i = 0; i < value.length(); i++) {
                    try {
                        JSONObject obj = value.getJSONObject(i);
                        texttmp += obj.toString() + NEWLINE;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                updateTextView(texttmp);
            }
        }).start();
    }
}
