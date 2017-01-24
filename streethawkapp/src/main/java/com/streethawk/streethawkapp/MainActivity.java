package com.streethawk.streethawkapp;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
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
import android.widget.Toast;

import com.streethawk.library.beacon.INotifyBeaconTransition;
import com.streethawk.library.core.ISHEventObserver;
import com.streethawk.library.core.StreetHawk;
import com.streethawk.library.core.Util;
import com.streethawk.library.feeds.ISHFeedItemObserver;
import com.streethawk.library.feeds.SHFeedItem;
import com.streethawk.library.geofence.INotifyGeofenceTransition;
import com.streethawk.library.growth.IGrowth;
import com.streethawk.library.push.ISHObserver;
import com.streethawk.library.push.Push;
import com.streethawk.library.push.PushDataForApplication;
import com.streethawk.library.pointzi.Pointzi;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        Constants, IGrowth, INotifyGeofenceTransition, INotifyBeaconTransition,
        ISHObserver, ISHFeedItemObserver,ISHEventObserver {
    int ANALYTICS = 0;
    int GROWTH = ANALYTICS + 1;
    int PUSH = GROWTH + 1;
    int BEACONS = PUSH + 1;
    int GEOFENCE = BEACONS + 1;
    int LOCATIONS = GEOFENCE + 1;
    int FEEDS = LOCATIONS + 1;
    int FEEDBACK = FEEDS + 1;
    int INSTALLINFO = FEEDBACK + 1;
    int SETTINGS = INSTALLINFO + 1;
    int WEBVIEW = SETTINGS + 1;
    int GRANT_PERMISSION = WEBVIEW + 1;
    int AUTHORING = GRANT_PERMISSION + 1;
    int COLORPICKER = AUTHORING + 1;
    int SERVER_LOGS = COLORPICKER + 1;
    int MODAL      = SERVER_LOGS + 1;
    int TEST_TIP    =   MODAL+1;

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

    @Override
    public void shReceivedRawJSON(String title, String message, String json) {

    }

    @Override
    public void shNotifyAppPage(String pageName) {

    }

    @Override
    public void onReceivePushData(PushDataForApplication pushData) {
        pushData.displayDataForDebugging("StreetHawk");
    }

    @Override
    public void onReceiveResult(PushDataForApplication resultData, int result) {

    }

    @Override
    public void onReceiveNonSHPushPayload(Bundle pushPayload) {

    }

    @Override
    public void shFeedReceived(JSONArray value) {

    }

    @Override
    public void onInstallRegistered(final String installId) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mActivity,"Install registered "+installId,Toast.LENGTH_LONG).show();
            }
        });
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
            "Reset ",
            "WebView",
            "Grant permission",
            "Authoring",
            "ColorPicker",
            "Logging Report",
            "Modal",
            "TestTip"
    };


    private View.OnClickListener share() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //Growth.getInstance(mActivity).getShareUrlForAppDownload("StreetHawkApp","streethawkapp://activity=MainActivity","StreetHawkApp","inApp",
                 //       "","","http://www.streethawk.com",null);


                /*
                SHTips tips = new SHTips();
                tips.unit_test_tooltip(mActivity,"fabmain");
                */

                //SHTours tours =new SHTours(mActivity);
                //tours.startTour("454186");


               /*
                Modal modal = new Modal();
                modal.unit_test_tooltip(mActivity);
                */



                Pointzi pointzi = new Pointzi(mActivity);
                pointzi.fetchPointziPayload(mActivity.getApplicationContext());
                Toast.makeText(getApplicationContext(), "Fetched pointiziPayload", Toast.LENGTH_SHORT).show();


                //new Authoring().enterAuthoringMode(mActivity);

            }

        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Util.setSHDebugFlag(this, true);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabmain);
        fab.setOnClickListener(share());
        mActivity = this;

        mListView = (ListView) findViewById(R.id.appOptions);
        final ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < mOptions.length; i++) {
            list.add(mOptions[i]);
        }
        final StableArrayAdapter adapter = new StableArrayAdapter(this,
                R.layout.customlistview, list);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(optionsOnclickListener());

        Intent intent = getIntent();
        boolean isSetupRequired = false;
        if (null != intent) {
            isSetupRequired = intent.getBooleanExtra(KEY_SETUP, false);
        }
        if (isSetupRequired) {
            Intent setupintent = new Intent(this, SetupActivity.class);
            setupintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(setupintent);
        }
        SharedPreferences prefs = getSharedPreferences(APP_PREF, Context.MODE_PRIVATE);
        mAppKey = prefs.getString(KEY_APP_KEY, null);
        if (null != mAppKey) {
            StreetHawk.INSTANCE.setAppKey(mAppKey);
            Push.getInstance(this).registerForPushMessaging("491295755890");
            Push.getInstance(this).setLargeIconResID("ic_launcher");
            Push.getInstance(this).setSmallIconResID("ic_stat_notification");
            Push.getInstance(this).registerSHObserver(this);
            StreetHawk.INSTANCE.init(getApplication());
        } else {
            mAppKey = intent.getStringExtra(KEY_APP_KEY);
            if (null != mAppKey) {
                StreetHawk.INSTANCE.setAppKey(mAppKey);
                StreetHawk.INSTANCE.init(getApplication());
            }
        }
        SHFeedItem.getInstance(this).registerFeedItemObserver(this);
    }

    /**
     * code to post/handler request for permission
     */
    public final static int REQUEST_CODE = 11;

    @TargetApi(Build.VERSION_CODES.M)
    public void checkDrawOverlayPermission() {
        /** check if we already  have permission to draw over other apps */
        if (!Settings.canDrawOverlays(getApplicationContext())) {
            /** if not construct intent to request permission */
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            /** request permission via start activity for result */
            startActivityForResult(intent, REQUEST_CODE);
        } else {
            Intent fabIntent = new Intent(this, SHFabService.class);
            startService(fabIntent);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /** check if received result code
         is equal our requested code for draw permission  */
        Log.e("Anurag", "onActivityresult" + requestCode);
        if (requestCode == REQUEST_CODE) {
            if (Settings.canDrawOverlays(this)) {
                Log.e("Anurag", "Startig service");
                Intent fabIntent = new Intent(this, SHFabService.class);
                startService(fabIntent);
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        Intent Sintent = new Intent(this, TestService.class);
        startService(Sintent);
    }


    public AdapterView.OnItemClickListener optionsOnclickListener() {
        return new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = null;
                if (ANALYTICS == position) {
                    intent = new Intent(getApplicationContext(), Analytics.class);
                }
                if (GROWTH == position) {
                    intent = new Intent(getApplicationContext(), GrowthActivity.class);
                }
                if (PUSH == position) {
                    intent = new Intent(getApplicationContext(), PushActivity.class);
                }
                if (BEACONS == position) {
                    intent = new Intent(getApplicationContext(), StartBGL.class);
                    intent.putExtra(ACTIVITY, ACTiVITY_BEACON);
                }
                if (GEOFENCE == position) {
                    intent = new Intent(getApplicationContext(), StartBGL.class);
                    intent.putExtra(ACTIVITY, ACTiVITY_GEOFENCE);
                }
                if (LOCATIONS == position) {
                    intent = new Intent(getApplicationContext(), StartBGL.class);
                    intent.putExtra(ACTIVITY, ACTiVITY_LOCATION);
                }
                if (FEEDS == position) {
                    intent = new Intent(getApplicationContext(), FeedList.class);
                }
                if (FEEDBACK == position) {
                    intent = new Intent(getApplicationContext(), Feedback.class);
                }
                if (INSTALLINFO == position) {

                    Context context = getApplicationContext();
                    final String HOST = "shKeyHost";
                    final String SHGCM_SENDER_KEY_APP = "shgcmsenderkeyapp";

                    SharedPreferences prefs = getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
                    String setServer = prefs.getString(HOST, "undefined");
                    String senderId = prefs.getString(SHGCM_SENDER_KEY_APP, "");
                    String NEW_LINE = "\n\n";
                    String installid = StreetHawk.INSTANCE.getInstallId(context);
                    String appKey = StreetHawk.INSTANCE.getAppKey(context);
                    String authtoken = prefs.getString(KEY_AUTH_TOKEN, "");


                    String TIME_LAST_PUSH = "timeLastPush";
                    String LAST_SERVER_MSG = "lastServerMessage";
                    String APP_SHARED_PREF = "LAST_SERVER_MSG";

                    final String PREF_NAME = "shsample_sharedPref";
                    SharedPreferences prefss = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
                    //String lastPingtime = prefss.getString(TIME_LAST_PUSH, null);
                    //String lastMessageToServer = prefss.getString(LAST_SERVER_MSG, null);


                    String message = "Install id: " + installid + NEW_LINE +
                            "AppKey :" + appKey + NEW_LINE +
                            "Server :  " + setServer + NEW_LINE +
                            "GCM Sender ID: " + senderId + NEW_LINE +
                            "Auth Token: " + authtoken + NEW_LINE ;
                            //"Last Ping Time" + lastPingtime + NEW_LINE +
                            //"Last message to server " + lastMessageToServer

                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivity, R.style.StreetHawkDialogTheme);
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
                if (SETTINGS == position) {
                    intent = new Intent(getApplicationContext(), Setting.class);
                }
                if (WEBVIEW == position) {
                    Intent webIntent = new Intent(getApplicationContext(), WebViewPOC.class);
                    startActivity(webIntent);
                }
                if (AUTHORING == position) {

                    /*
                    Intent authorIntent = new Intent(mActivity, AddButtonService.class);
                    mActivity.startService(authorIntent);

                    Intent authorIntent = new Intent(mActivity, AskPermission.class);
                    mActivity.startActivity(authorIntent);
                    */

                    /*
                    Authoring authoring = Authoring.getInstance(mActivity);
                    authoring.startAuthoring();
                    */

                }
                if (GRANT_PERMISSION == position) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        // Show alert dialog to the user saying a separate permission is needed
                        // Launch the settings activity if the user prefers
                        Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                        mActivity.startActivity(myIntent);
                    }
                }

                if (COLORPICKER == position) {
                    /*
                    ColorPicker picker = new ColorPicker();
                    picker.showColorPicker(mActivity, new IColorPickerObserver() {
                        @Override
                        public void onColorSelected(String color) {
                            Log.e("Anurag","Color selected "+color);
                        }
                    });
                    */
                }

                if (SERVER_LOGS == position) {
                    intent = new Intent(getApplicationContext(), Logreport.class);

                }
                if (MODAL == position) {
                    // new Modal().unit_test_tooltip(mActivity);
                }
                if(TEST_TIP==position){
                    intent = new Intent(getApplicationContext(), TestPointzi.class);
                }

                if (null != intent) {
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        };
    }

    private void openUrlInBrowser(String url) {
        if (null == url)
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