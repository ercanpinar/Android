package library.streethawk.com.appmarshmallow;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.streethawk.library.core.StreetHawk;

public class NewGrowthActivity extends Activity {

    private EditText mUtm_campaign;
    private EditText mDeepLinkUri;
    private EditText mUtm_source;
    private EditText mUtm_medium;
    private EditText mUtm_term;
    private EditText mCampaign_Content;
    private EditText mDefaultURI;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_growth);

        mUtm_campaign = (EditText)findViewById(R.id.utm_campaign);
        mUtm_source = (EditText)findViewById(R.id.utm_source);
        mUtm_medium = (EditText)findViewById(R.id.utm_medium);
        mUtm_term = (EditText)findViewById(R.id.utm_term);
        mCampaign_Content = (EditText)findViewById(R.id.campaign_content);
        mDefaultURI = (EditText)findViewById(R.id.defaulturi);
        mDeepLinkUri = (EditText)findViewById(R.id.deeplinkuri);


    }

    public void AutoFill(View v){
        mUtm_campaign.setText(StreetHawk.INSTANCE.getCurrentFormattedDateTime().toString());
        mUtm_source.setText("Slack");
        mUtm_medium.setText("Slack");
        mUtm_term.setText("Slack");
        mCampaign_Content.setText("Testing on slack");
        mDefaultURI.setText("https://www.streethawk.com");
        mDeepLinkUri .setText("shsample://setparams?param1=31");
    }

    public void onShare(View v){
        String Utm_campaign = mUtm_campaign.getText().toString();
        String DeepLinkUri = mDeepLinkUri.getText().toString();
        String Utm_source = mUtm_source.getText().toString();
        String Utm_medium = mUtm_medium.getText().toString();
        String Utm_term = mUtm_term.getText().toString();
        String Campaign_Content = mCampaign_Content.getText().toString();
        String DefaultURI = mDefaultURI.getText().toString();

        com.streethawk.library.growth.Growth.getInstance(this).originateShareWithCampaign(Utm_campaign,DeepLinkUri,Utm_source
                ,Utm_medium,Utm_term,Campaign_Content,DefaultURI, null);
    }
}

