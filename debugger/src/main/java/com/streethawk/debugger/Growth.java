package com.streethawk.debugger;

import android.app.Activity;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import org.json.JSONObject;

public class Growth extends AppCompatActivity {
    private EditText mUtm_campaign;
    private EditText mDeepLinkUri;
    private EditText mUtm_source;
    private EditText mUtm_medium;
    private EditText mUtm_term;
    private EditText mCampaign_Content;
    private EditText mDefaultURI;

    private Activity mActivity;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_growth);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(onShare());

        mUtm_campaign = (EditText)findViewById(R.id.utm_campaign);
        mUtm_source = (EditText)findViewById(R.id.utm_source);
        mUtm_medium = (EditText)findViewById(R.id.utm_medium);
        mUtm_term = (EditText)findViewById(R.id.utm_term);
        mCampaign_Content = (EditText)findViewById(R.id.campaign_content);
        mDefaultURI = (EditText)findViewById(R.id.defaulturi);
        mDeepLinkUri = (EditText)findViewById(R.id.deeplinkuri);
        mActivity = this;


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.help) {
            autoFill();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void autoFill(){
        mUtm_campaign.setText(StreetHawk.INSTANCE.getCurrentFormattedDateTime().toString());
        mUtm_source.setText("Slack");
        mUtm_medium.setText("Slack");
        mUtm_term.setText("Slack");
        mCampaign_Content.setText("Testing on slack");
        mDefaultURI.setText("https://www.streethawk.com");
        mDeepLinkUri .setText("shsample://setparams?param1=31");
    }

    public View.OnClickListener onShare(){
        return new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String Utm_campaign = mUtm_campaign.getText().toString();
                String DeepLinkUri = mDeepLinkUri.getText().toString();
                String Utm_source = mUtm_source.getText().toString();
                String Utm_medium = mUtm_medium.getText().toString();
                String Utm_term = mUtm_term.getText().toString();
                String Campaign_Content = mCampaign_Content.getText().toString();
                String DefaultURI = mDefaultURI.getText().toString();
                Growth.getInstance(mActivity).originateShareWithCampaign(Utm_campaign, DeepLinkUri, Utm_source
                        , Utm_medium, Utm_term, Campaign_Content, DefaultURI, new IGrowth() {
                            @Override
                            public void onReceiveShareUrl(String shareUrl) {

                            }

                            @Override
                            public void onReceiveErrorForShareUrl(JSONObject errorResponse) {

                            }

                            @Override
                            public void onReceiveDeepLinkUrl(String deeplinkUrl) {

                            }
                        });
            }
        };
    }
}
