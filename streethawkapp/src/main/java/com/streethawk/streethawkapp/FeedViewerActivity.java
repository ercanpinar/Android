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

import com.streethawk.library.feeds.ISHFeedItemObserver;

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

    @Override
    protected void onResume() {
        super.onResume();
        mBaseFrame = (FrameLayout) findViewById(R.id.baseframe);
        if (mFeedItem != null) {
            String title = mFeedItem.getFeedTitle();
            if (null != title) {
                if (!title.isEmpty()) {
                    TextView titleTV = new TextView(getApplicationContext());
                    titleTV.setGravity(Gravity.CENTER_HORIZONTAL);
                    titleTV.setBackgroundColor(Color.parseColor("#000000"));
                    titleTV.setTextColor(Color.parseColor("#000000"));
                    titleTV.setText(title);
                }
            }
            String message = mFeedItem.getFeedMessage();
            if (null != message) {
                if (!message.isEmpty()) {
                    TextView MessageTV = new TextView(getApplicationContext());
                    MessageTV.setTextColor(Color.parseColor("#000000"));
                    MessageTV.setText(message);
                }
            }
            String webUrl = mFeedItem.getURL();
            if (null != webUrl) {
                if (!webUrl.isEmpty()) {
                    LayoutInflater inflater = (LayoutInflater) getApplicationContext()
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    WebView webView = (WebView) inflater.inflate(R.layout.feed_web_view, mBaseFrame, false);
                    webView.setWebViewClient(new WebViewClient());
                    webView.loadUrl(webUrl);
                    mBaseFrame.addView(webView);
                }
            }
            String imageUrl = mFeedItem.getImage();
            if (null != imageUrl) {
                if (!imageUrl.isEmpty()) {
                    DisplayImageView(imageUrl);
                }
            }
            String videoUrl = mFeedItem.getVideo();
            if (null != videoUrl) {
                if (!videoUrl.isEmpty()) {
                    //TODO Display Video URL
                }
            }
        }
        mBaseFrame.invalidate();
    }

// TODO : Only for testing, This fetch is redundant
class fetchImageTask extends AsyncTask<String, Void, Bitmap> {
    @Override
    protected Bitmap doInBackground(String... urls) {
        String imageUrl = urls[0];
        try {
            Log.e("Anurag", "ImageView " + imageUrl);
            InputStream in = new java.net.URL(imageUrl).openStream();
            return BitmapFactory.decodeStream(in);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap image) {
        super.onPostExecute(image);
        FrameLayout frame = (FrameLayout) findViewById(R.id.baseframe);
        LayoutInflater inflater = (LayoutInflater) getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ImageView imageView = (ImageView) inflater.inflate(R.layout.feed_image_view, frame, false);
        imageView.setImageBitmap(image);
        frame.addView(imageView);
    }

}

    private void DisplayImageView(String url) {
        new fetchImageTask().execute(url);
    }

}
