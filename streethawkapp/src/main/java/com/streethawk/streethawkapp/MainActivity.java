package com.streethawk.streethawkapp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.streethawk.library.beacon.INotifyBeaconTransition;
import com.streethawk.library.core.StreetHawk;
import com.streethawk.library.core.Util;
import com.streethawk.library.geofence.INotifyGeofenceTransition;
import com.streethawk.library.growth.Growth;
import com.streethawk.library.growth.IGrowth;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements Constants,IGrowth,INotifyGeofenceTransition,INotifyBeaconTransition{

    int ANALYTICS   = 0;
    int GROWTH      = ANALYTICS + 1;
    int PUSH        = GROWTH + 1;
    int BEACONS     = PUSH + 1;
    int GEOFENCE    = BEACONS + 1;
    int LOCATIONS   = GEOFENCE + 1;
    int FEEDS       = LOCATIONS + 1;
    int FEEDBACK    = FEEDS +1 ;
    int INSTALLINFO = FEEDBACK + 1;
    int SETTINGS    = INSTALLINFO + 1;


    String mAppKey = null;
    String mSenderKey = null;
    private Activity mActivity;

    @Override
    public void onReceiveShareUrl(String shareUrl) {

    }

    @Override
    public void onReceiveErrorForShareUrl(JSONObject errorResponse) {

    }

    @Override
    public void onReceiveDeepLinkUrl(String deeplinkUrl) {

    }

    @Override
    public void onDeviceEnteringGeofence() {
        
    }

    @Override
    public void onDeviceLeavingGeofence() {

    }

    @Override
    public void notifyBeaconDetected() {

    }


    private class StableArrayAdapter extends ArrayAdapter<String> {
        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();
        public StableArrayAdapter(Context context, int textViewResourceId,
                                  List<String> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }
        }

        @Override
        public long getItemId(int position) {
            String item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

    }

    private ListView mListView = null;
    private View mView = null;
    private final String[] mOptions = new String[]{
            "Analytics",
            "Growth",
            "Push",
            "Beacons",
            "Geofence",
            "Locations",
            "Feeds",
            "Feedback",
            "Install-Info",
            "Reset "
    };


    private View.OnClickListener share(){
        return new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Growth.getInstance(mActivity).getShareUrlForAppDownload("StreetHawkApp","streethawkapp://activity=MainActivity","StreetHawkApp","inApp",
                        "","","http://www.streethawk.com",null);
            }
        };
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*
        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                String value = getIntent().getExtras().getString(key);
                Log.d("FireBase", "Key: " + key + " Value: " + value);
            }
        }
        FirebaseMessaging.getInstance().subscribeToTopic("news");
        */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabmain);
        fab.setOnClickListener(share());
        mActivity = this;

        mListView = (ListView)findViewById(R.id.appOptions);
        final ArrayList<String> list = new ArrayList<String>();
        for(int i=0;i<mOptions.length;i++){
            list.add(mOptions[i]);
        }
        final StableArrayAdapter adapter = new StableArrayAdapter(this,
                R.layout.customlistview, list);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(optionsOnclickListener());

        Intent intent = getIntent();
        boolean isSetupRequired = false;
        if(null!=intent) {
            isSetupRequired= intent.getBooleanExtra(KEY_SETUP,false);
        }
        if(isSetupRequired){
            Log.e("Anurag","Starting setup");
            Intent setupintent  = new Intent(this,SetupActivity.class);
            setupintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(setupintent);
        }
        Log.e("Anurag","calling init 2");
        SharedPreferences prefs = getSharedPreferences(APP_PREF, Context.MODE_PRIVATE);
        mAppKey = prefs.getString(KEY_APP_KEY,null);
        if(null!=mAppKey) {
            Log.e("Anurag","Calling init");
            StreetHawk.INSTANCE.setAppKey(mAppKey);
            StreetHawk.INSTANCE.init(getApplication());
        }else{
            mAppKey = intent.getStringExtra(KEY_APP_KEY);
            if(null!=mAppKey){
                StreetHawk.INSTANCE.setAppKey(mAppKey);
                StreetHawk.INSTANCE.init(getApplication());
            }

        }
    }

    public AdapterView.OnItemClickListener optionsOnclickListener(){
        return new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent=null;
                if(ANALYTICS==position){
                    intent = new Intent(getApplicationContext(),Analytics.class);
                }
                if(GROWTH==position){
                    intent = new Intent(getApplicationContext(),GrowthActivity.class);
                }
                if(PUSH==position){
                    intent = new Intent(getApplicationContext(),PushActivity.class);
                }
                if(BEACONS==position){
                    intent = new Intent(getApplicationContext(),StartBGL.class);
                    intent.putExtra(ACTIVITY,ACTiVITY_BEACON);
                }
                if(GEOFENCE==position){
                    intent = new Intent(getApplicationContext(),StartBGL.class);
                    intent.putExtra(ACTIVITY,ACTiVITY_GEOFENCE);
                }
                if(LOCATIONS==position){
                    intent = new Intent(getApplicationContext(),StartBGL.class);
                    intent.putExtra(ACTIVITY,ACTiVITY_LOCATION);
                }
                if(FEEDS==position){
                    intent = new Intent(getApplicationContext(),Feeds.class);
                }
                if(FEEDBACK==position){
                    intent = new Intent(getApplicationContext(),Feedback.class);
                }
                if(INSTALLINFO==position){

                    Context context  = getApplicationContext();
                    final String HOST = "shKeyHost";
                    final String SHGCM_SENDER_KEY_APP = "shgcmsenderkeyapp";

                    SharedPreferences prefs = getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
                    String setServer = prefs.getString(HOST, "undefined");
                    String senderId = prefs.getString(SHGCM_SENDER_KEY_APP, "");
                    String NEW_LINE = "\n\n";
                    String installid = StreetHawk.INSTANCE.getInstallId(context);
                    String appKey = StreetHawk.INSTANCE.getAppKey(context);
                    String authtoken = prefs.getString(KEY_AUTH_TOKEN, "");

                    String message =    "Install id: "  + installid + NEW_LINE +
                                        "AppKey :"      + appKey + NEW_LINE +
                                        "Server :  "    + setServer + NEW_LINE +
                                        "GCM Sender ID: " + senderId + NEW_LINE +
                                        "Auth Token: "    + authtoken + NEW_LINE;

                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivity,R.style.StreetHawkDialogTheme);
                    builder.setTitle("Install Info");
                    builder.setMessage(message);
                    builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //dialog.dismiss();

                        }
                    });
                    builder.create().show();
                }
                if(SETTINGS==position){
                    intent = new Intent(getApplicationContext(),Settings.class);
                }
                if(null!=intent){
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        };
    }


    private void openUrlInBrowser(String url) {
        if(null==url)
            return;
        Intent docs = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(docs);
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
        if (id == R.id.action_docs) {
            openUrlInBrowser("https://streethawk.freshdesk.com/support/solutions/folders/5000273033");
            return true;
        }
        if (id == R.id.action_sourcecode) {
            openUrlInBrowser("https://github.com/StreetHawkSDK/Android");
            return true;
        }
        if (id == R.id.action_shdotcom) {
            openUrlInBrowser("http://streethawk.com");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
