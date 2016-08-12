package com.streethawk.streethawkapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class WebViewPOC extends Activity {


    private FrameLayout baseFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_poc);

        baseFrame = (FrameLayout)findViewById(R.id.webframe);

    }

    @Override
    public void onResume(){
        super.onResume();

        LayoutInflater inflater = (LayoutInflater) getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        WebView webView = (WebView) inflater.inflate(R.layout.feed_web_view, baseFrame, false);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("http://www.facebook.com");
        baseFrame.addView(webView);
        baseFrame.invalidate();

    }


}
