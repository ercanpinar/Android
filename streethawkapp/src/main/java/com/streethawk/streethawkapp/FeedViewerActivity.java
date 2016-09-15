package com.streethawk.streethawkapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ActionBarOverlayLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.streethawk.library.feeds.ISHFeedItemObserver;
import com.streethawk.library.feeds.SHFeedItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

public class FeedViewerActivity extends Activity implements Constants {

    private FeedItem mFeedItem = null;
    private FrameLayout mBaseFrame;

    private int ACCEPTED = 1;
    private int DECLINED = -1;
    private int LATER   =  0;

    private static String  mFeedId = null;

    public void SendLike(View view){
        try {
            int id = Integer.parseInt(mFeedId);
            SHFeedItem.getInstance(getApplicationContext()).notifyFeedResult(id,"accepted",false,false);

            Toast.makeText(getApplicationContext(),"Sent feed result accepted",Toast.LENGTH_LONG).show();
        }catch(NumberFormatException e){
            Toast.makeText(getApplicationContext(),"FeedId is not a int",Toast.LENGTH_LONG).show();
        }
    }

    public void SendDislike(View view){
        try {
            int id = Integer.parseInt(mFeedId);
            SHFeedItem.getInstance(getApplicationContext()).notifyFeedResult(id,"rejected",true,true);
            Toast.makeText(getApplicationContext(),"Sent feed result rejected",Toast.LENGTH_LONG).show();
        }catch(NumberFormatException e){
            Toast.makeText(getApplicationContext(),"FeedId is not a int",Toast.LENGTH_LONG).show();
        }
    }

    public void SendLater(View view){
        try {
            int id = Integer.parseInt(mFeedId);
            SHFeedItem.getInstance(getApplicationContext()).notifyFeedResult(id,"postponed",true,true);
            Toast.makeText(getApplicationContext(),"Sent feed result postponed",Toast.LENGTH_LONG).show();
        }catch(NumberFormatException e){
            Toast.makeText(getApplicationContext(),"FeedId is not a int",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_viewer);
        Intent intent = getIntent();
        if (null != intent) {
            mFeedItem = intent.getParcelableExtra("FEEDITEM_PARCEL");
            mFeedItem.displayForDebugging("Anurag", "From FeedItemViewer");
        }
    }

    private void sendFeedAckLog(){
        if(mFeedId==null) {
            return;
        }
        try {
            int int_feed_id = Integer.parseInt(mFeedId);
            SHFeedItem.getInstance(this).sendFeedAck(int_feed_id);
        }catch (NumberFormatException e){
            e.printStackTrace();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mFeedItem != null) {
            mFeedId = mFeedItem.getFeedId();
            sendFeedAckLog();
            TextView feed = (TextView)findViewById(R.id.jsonText);
            feed.setText(mFeedItem.getObjectDetails());
            }
        }

    }
