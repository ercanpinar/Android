package com.streethawk.streethawkdev;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.streethawk.library.core.StreetHawk;
import com.streethawk.library.core.Util;
import com.streethawk.library.push.Push;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;

public class PushActivity extends AppCompatActivity implements Constants {

    private final int ACTION_SIMPLE_PUSH = 0;
    private final int ACTION_DEEPLINK = ACTION_SIMPLE_PUSH + 1;
    private final int ACTION_WEBPAGE = ACTION_DEEPLINK + 1;
    private final int ACTION_RATE = ACTION_WEBPAGE + 1;
    private final int ACTION_UPDATE = ACTION_RATE + 1;
    private final int ACTION_DIALNUMBER = ACTION_UPDATE + 1;
    private final int ACTION_FEEDBACK = ACTION_DIALNUMBER + 1;
    private final int ACTION_ENABLE_BLUETOOTH = ACTION_FEEDBACK + 1;
    private final int ACTION_ENABLE_LOCATION = ACTION_ENABLE_BLUETOOTH + 1;
    private final int ACTION_ENABLE_PUSH = ACTION_ENABLE_LOCATION + 1;
    private final int ACTION_INTERACTIVE_PUSH = ACTION_ENABLE_PUSH + 1;
    private final int ACTION_CUSTOM_JSON = ACTION_INTERACTIVE_PUSH + 1;





    private final String DEFAULT_TITLE      = "Test from AutoFill ";
    private final String DEFAULT_MESSAGE    = "Message: date time now is "+StreetHawk.getCurrentFormattedDateTime();

    private final String DEFAULT_TITLE_BT      = "Enable Bluetooth for beacons";
    private final String DEFAULT_MESSAGE_BT    = " "+StreetHawk.getCurrentFormattedDateTime();

    private final String DEFAULT_TITLE_LOC      = "Enable Locations ";
    private final String DEFAULT_MESSAGE_LOC    = " "+StreetHawk.getCurrentFormattedDateTime();

    private final String DEFAULT_URL = "http://www.streethawk.com";
    private final String DEFAULT_DEEPLINK   =   "streethawkapp://activity=MainActivity";
    private final String DEFAULT_OP1 = "Yes";
    private final String DEFAULT_OP2 = "No";
    private final String DEFAULT_OP3 = "May be";
    private final String DEFAULT_OP4 = "I don't know";

    private final String DEFAULT_PHONE_NUMBER = "+61469123456";
    private final String DEFAULT_CUSTOM_JSON  = "";  //TODO add a json list to string here



    private final String TAG = "PushActivity";

    private Spinner     mActionSpinner;
    private FrameLayout mFrameLayout;
    private Activity mActivity;

    View mSimpleActionView;
    View mWebActionView;
    View mFeedBackActionView;
    View mInteractivePushView;

    CheckBox mDisplayInBrowser = null;
    CheckBox mNoConfirm;
    CheckBox mAppBG;

    private Spinner mPortionSpinner;
    private Spinner mOrientationSpinner;
    private Spinner mSpeedSpinner;

    private TextView mDataTV;


    EditText mTitleET;
    EditText mMessageET;


    private void showDialogForPushParams() {
        SharedPreferences prefs = getSharedPreferences(APP_PREF, Context.MODE_PRIVATE);
        String authToken = prefs.getString(KEY_AUTH_TOKEN, null);
        String projectNumber = prefs.getString(KEY_SENDER_ID, null);

        Log.e("Anurag", "AUTH_TOKEN " + authToken);
        Log.e("Anurag", "projectNumber " + projectNumber);

        if ((null == authToken) || null == projectNumber) {
            Context context = getApplicationContext();

            String title = "Missing Push params";
            String message = "open app using deeplink url streethawkapp://pushactivity?auth_token=<AUTH_TOKEN>&projectNumber=<PROJECT_NUMBER>";
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity, R.style.StreetHawkDialogTheme);
            builder.setTitle(title);
            builder.setMessage(message);
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    NavUtils.navigateUpFromSameTask(mActivity);

                }
            });
            builder.create().show();
        } else {
            Push.getInstance(mActivity).registerForPushMessaging(projectNumber);
            return;
        }
    }

    private void savePushParams(Uri deeplink) {
        try {
            String authToken = deeplink.getQueryParameter("authtoken");
            String projectnumber = deeplink.getQueryParameter("projectnumber");
            SharedPreferences prefs = getSharedPreferences(APP_PREF, Context.MODE_PRIVATE);
            SharedPreferences.Editor e = prefs.edit();
            if (null != projectnumber)
                e.putString(KEY_SENDER_ID, projectnumber);
            if (null != authToken)
                e.putString(KEY_AUTH_TOKEN, projectnumber);
            e.commit();
            Toast.makeText(getApplicationContext(), "Push params saved", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        showDialogForPushParams();
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_autofill:
                int position = mActionSpinner.getSelectedItemPosition();
                switch (position) {
                    case ACTION_SIMPLE_PUSH:
                    case ACTION_RATE:
                    case ACTION_UPDATE:
                    case ACTION_ENABLE_BLUETOOTH:
                    case ACTION_ENABLE_LOCATION:
                    case ACTION_ENABLE_PUSH:
                        break;
                    case ACTION_DEEPLINK: {
                        EditText mDataET = (EditText) findViewById(R.id.data);
                        mDataET.setText(DEFAULT_DEEPLINK);
                    }
                        break;
                    case ACTION_DIALNUMBER: {
                        EditText mDataET = (EditText) findViewById(R.id.data);
                        mDataET.setText(DEFAULT_PHONE_NUMBER);
                    }
                        break;
                    case ACTION_CUSTOM_JSON:
                    {
                        EditText mDataET = (EditText) findViewById(R.id.data);
                        mDataET.setText(DEFAULT_CUSTOM_JSON);
                    }
                        break;
                    case ACTION_WEBPAGE:{
                        EditText mDataET = (EditText) findViewById(R.id.data);
                        mDataET.setText(DEFAULT_URL);
                    }
                        break;
                    case ACTION_FEEDBACK: {
                        EditText o1ET = (EditText) findViewById(R.id.option1);
                        o1ET.setText(DEFAULT_OP1);
                        EditText o2ET = (EditText) findViewById(R.id.option2);
                        o2ET.setText(DEFAULT_OP2);
                        EditText o3ET = (EditText) findViewById(R.id.option3);
                        o3ET.setText(DEFAULT_OP3);
                        EditText o4ET = (EditText) findViewById(R.id.option4);
                        o4ET.setText(DEFAULT_OP4);
                    }
                        break;
                    case ACTION_INTERACTIVE_PUSH:
                        break;
                    default:
                        break;
                }
                mTitleET.setText(DEFAULT_TITLE);
                mMessageET.setText(DEFAULT_MESSAGE);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(sendPushData());

        Intent intent = getIntent();
        Uri deeplink = intent.getData();
        if (null != deeplink) {
            savePushParams(deeplink);
        }
        mActionSpinner = (Spinner) findViewById(R.id.CodePicker);
        mAppBG = (CheckBox) findViewById(R.id.appbg);
        mTitleET = (EditText) findViewById(R.id.editTextTitle);
        mMessageET = (EditText) findViewById(R.id.editTextMsg);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.Actions, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mActionSpinner.setAdapter(adapter);
        mActionSpinner.setSelection(ACTION_SIMPLE_PUSH);
        mActionSpinner.setOnItemSelectedListener(actionSpinnerSelect());
        mFrameLayout = (FrameLayout) findViewById(R.id.pushFrame);
        mActivity = this;

        LayoutInflater inflater = getLayoutInflater();
        mSimpleActionView = inflater.inflate(R.layout.simpleaction, null);
        mWebActionView = inflater.inflate(R.layout.weblayout, null);
        mFeedBackActionView = inflater.inflate(R.layout.feedback, null);
        mInteractivePushView = inflater.inflate(R.layout.simpleaction, null);
    }

    private Spinner.OnItemSelectedListener actionSpinnerSelect() {
        return new Spinner.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                mTitleET.setText("");
                mMessageET.setText("");
                mFrameLayout.removeAllViews();
                switch (position) {
                    case ACTION_SIMPLE_PUSH:
                    case ACTION_RATE:
                    case ACTION_UPDATE:
                    case ACTION_ENABLE_BLUETOOTH:
                    case ACTION_ENABLE_LOCATION:
                    case ACTION_ENABLE_PUSH:
                        break;
                    case ACTION_DEEPLINK:
                        mFrameLayout.addView(mSimpleActionView);
                        mDataTV = (TextView) findViewById(R.id.simpledatatv);
                        mDataTV.setText("DeepLink Url");
                        EditText deep = (EditText) findViewById(R.id.data);
                        deep.setText("");
                        break;
                    case ACTION_DIALNUMBER:
                        mFrameLayout.addView(mSimpleActionView);
                        mDataTV = (TextView) findViewById(R.id.simpledatatv);
                        mDataTV.setText("Phone Number");
                        EditText phone = (EditText) findViewById(R.id.data);
                        phone.setText("");
                        break;
                    case ACTION_CUSTOM_JSON:
                        mFrameLayout.addView(mSimpleActionView);
                        mDataTV = (TextView) findViewById(R.id.simpledatatv);
                        EditText mDataET = (EditText) findViewById(R.id.data);
                        mDataET.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                        mDataET.setLines(5);
                        mDataET.setText("");
                        mDataTV.setText("Custom JSON");
                        break;
                    case ACTION_WEBPAGE:
                        mFrameLayout.addView(mWebActionView);

                        mPortionSpinner = (Spinner) findViewById(R.id.portionspinner);
                        mOrientationSpinner = (Spinner) findViewById(R.id.orientationspinner);
                        mSpeedSpinner = (Spinner) findViewById(R.id.speedspinner);
                        mNoConfirm = (CheckBox) findViewById(R.id.noconfirm);
                        ArrayAdapter<CharSequence> padapter = ArrayAdapter.createFromResource(mActivity,
                                R.array.Portion, android.R.layout.simple_spinner_item);
                        padapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        mPortionSpinner.setAdapter(padapter);

                        ArrayAdapter<CharSequence> oadapter = ArrayAdapter.createFromResource(mActivity,
                                R.array.Orientation, android.R.layout.simple_spinner_item);
                        oadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        mOrientationSpinner.setAdapter(oadapter);

                        ArrayAdapter<CharSequence> sadapter = ArrayAdapter.createFromResource(mActivity,
                                R.array.Speed, android.R.layout.simple_spinner_item);
                        sadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        mSpeedSpinner.setAdapter(sadapter);

                        mDisplayInBrowser = (CheckBox) findViewById(R.id.openinbrowser);
                        if (null != mDisplayInBrowser) {
                            mDisplayInBrowser.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                                    TextView ptv = (TextView) findViewById(R.id.ptv);
                                    TextView otv = (TextView) findViewById(R.id.otv);
                                    TextView stv = (TextView) findViewById(R.id.stv);
                                    if (isChecked) {
                                        mPortionSpinner.setEnabled(false);
                                        mOrientationSpinner.setEnabled(false);
                                        mSpeedSpinner.setEnabled(false);
                                        mNoConfirm.setEnabled(false);
                                        ptv.setEnabled(false);
                                        otv.setEnabled(false);
                                        stv.setEnabled(false);
                                    } else {
                                        mPortionSpinner.setEnabled(true);
                                        mOrientationSpinner.setEnabled(true);
                                        mSpeedSpinner.setEnabled(true);
                                        mNoConfirm.setEnabled(true);
                                        ptv.setEnabled(true);
                                        otv.setEnabled(true);
                                        stv.setEnabled(true);
                                    }
                                }
                            });
                        }
                        break;
                    case ACTION_FEEDBACK:
                        mFrameLayout.addView(mFeedBackActionView);
                        break;
                    case ACTION_INTERACTIVE_PUSH:
                        mFrameLayout.addView(mSimpleActionView);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_setup, menu);
        return true;
    }

    private void sendPushPayload(final URL url, final HashMap<String, String> logMap) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Context context = getApplicationContext();
                final Activity activity = StreetHawk.INSTANCE.getCurrentActivity();
                final String app_key = StreetHawk.INSTANCE.getAppKey(context);
                String installId = Util.getInstallId(context);
                BufferedReader reader = null;
                try {
                    HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                    connection.setReadTimeout(10000);
                    connection.setConnectTimeout(15000);
                    connection.setRequestMethod("POST");
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.setRequestProperty("X-Installid", installId);
                    connection.setRequestProperty("X-App-Key", app_key);
                    String libVersion = Util.getLibraryVersion();
                    connection.setRequestProperty("User-Agent", app_key + "(" + libVersion + ")");
                    OutputStream os = connection.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(os, "UTF-8"));
                    //String logs = Util.getPostDataString(logMap);
                    String logs = "";
                    boolean first = true;
                    for (Map.Entry<String, String> entry : logMap.entrySet()) {
                        StringBuilder result = new StringBuilder();
                        if (first)
                            first = false;
                        else
                            result.append("&");
                        String key = entry.getKey();
                        String value = entry.getValue();
                        if (null != key) {
                            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                            result.append("=");
                            if (null != value) {
                                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                            } else {
                                result.append(URLEncoder.encode("", "UTF-8"));
                            }
                        }
                        logs += result.toString();
                        result = null; //Force GC
                    }

                    writer.write(logs);
                    writer.flush();
                    writer.close();
                    os.close();
                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        Log.i("StreetHawkApp", "Push sent");
                    } else {
                        Log.e("Anurag", "Error sending push message " + connection.getResponseCode() + " " + connection.getResponseMessage());
                    }
                    connection.disconnect();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null == connectivityManager)
            return false;
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (null == networkInfo)
            return false;
        else
            return true;
    }

    private String getPublishUrl(Context context) {

        final String HOST = "shKeyHost";
        SharedPreferences prefs = getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
        String setServer = prefs.getString(HOST, "undefined");

        if(setServer.contains("staging.streethawk.com")){
            Log.e("Anurag","Staging publish");
            return "https://staging.streethawk.com/v1/installs/publish";
        }
        return "https://api.streethawk.com/v1/installs/publish";


    }

    private View.OnClickListener sendPushData() {
        return new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                final HashMap<String, String> logMap = new HashMap<String, String>();
                final String PUSH_INSTALLID = "installid";
                final String PUSH_AUTH_TOKEN = "auth_token";
                final String PUSH_CODE = "c";
                final String PUSH_MSG_ID = "i";
                final String PUSH_DATA = "d";
                final String PUSH_SHOW_DIALOG = "n";
                final String PUSH_MSG = "m";
                final String PUSH_TITLE = "t";
                final String PUSH_PORTION = "p";
                final String PUSH_ORIENTATION = "o";
                final String PUSH_SPEED = "s";
                final String PUSH_APS = "aps";
                final String PUSH_ALERT = "alert";
                final String PUSH_BADGE = "badge";
                final String PUSH_SOUND = "sound";
                final String PUSH_AUTHTOKEN = "auth_token";

                Context context = getApplicationContext();
                String title = mTitleET.getText().toString();
                String message = mMessageET.getText().toString();
                URL url = null;

                if ((title.isEmpty()) && message.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Give one of Title or Message", Toast.LENGTH_LONG).show();
                    return;
                }
                if (!isNetworkConnected(context)) {
                    Log.e(TAG, "Failed to Update install No network connection");
                    return;
                }
                try {
                    url = new URL(getPublishUrl(context));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String installId = StreetHawk.INSTANCE.getInstallId(context);
                if (null == installId) {
                    Log.e(TAG, "Installid is null");
                    return;
                }
                int item = mActionSpinner.getSelectedItemPosition();
                switch (item) {
                    case ACTION_SIMPLE_PUSH:
                        logMap.put(PUSH_CODE, "8010");
                        break;
                    case ACTION_DEEPLINK: {
                        EditText mDataEt = (EditText) findViewById(R.id.data);
                        String data = mDataEt.getText().toString();
                        if (!data.isEmpty()) {
                            logMap.put(PUSH_DATA, data);
                        }
                        logMap.put(PUSH_CODE, "8004");
                    }
                    break;
                    case ACTION_WEBPAGE: {
                        int orientation;
                        int speed;
                        EditText mDataEt = (EditText) findViewById(R.id.data);
                        String data = mDataEt.getText().toString();
                        if (!data.isEmpty()) {
                            logMap.put(PUSH_DATA, data);
                        }
                        if (!mDisplayInBrowser.isChecked()) {
                            int ppos = mPortionSpinner.getSelectedItemPosition();
                            switch (ppos) {
                                case 0:
                                    logMap.put(PUSH_PORTION, "0.5");
                                    break;
                                case 1:
                                    logMap.put(PUSH_PORTION, "0.75");
                                    break;
                                case 2:
                                    logMap.put(PUSH_PORTION, "1.0");
                                    break;
                            }
                            orientation = mOrientationSpinner.getSelectedItemPosition();
                            logMap.put(PUSH_ORIENTATION, Integer.toString(orientation));
                            speed = 3 - (mSpeedSpinner.getSelectedItemPosition());
                            logMap.put(PUSH_SPEED, Integer.toString(speed));
                            if (mNoConfirm.isChecked())
                                logMap.put(PUSH_SHOW_DIALOG, "8000");
                        }
                        logMap.put(PUSH_CODE, "8000");
                        break;
                    }
                    case ACTION_RATE: {
                        logMap.put(PUSH_CODE, "8005");
                    }
                    break;
                    case ACTION_UPDATE: {
                        logMap.put(PUSH_CODE, "8008");
                    }
                    break;
                    case ACTION_DIALNUMBER: {
                        EditText mDataEt = (EditText) findViewById(R.id.data);
                        String data = mDataEt.getText().toString();
                        if (!data.isEmpty()) {
                            try {
                                int num = Integer.parseInt(data);
                            } catch (NumberFormatException e) {
                                Toast.makeText(context, "Enter valid number", Toast.LENGTH_LONG).show();
                            }
                            logMap.put(PUSH_DATA, data);
                        }
                        logMap.put(PUSH_CODE, "8009");
                    }
                    break;
                    case ACTION_FEEDBACK:
                        logMap.put(PUSH_CODE, "8011");
                        break;
                    case ACTION_ENABLE_BLUETOOTH:
                        logMap.put(PUSH_CODE, "8012");
                        break;
                    case ACTION_ENABLE_LOCATION:
                        logMap.put(PUSH_CODE, "8014");
                        break;
                    case ACTION_ENABLE_PUSH:
                        logMap.put(PUSH_CODE, "8013");
                        break;
                    case ACTION_INTERACTIVE_PUSH:
                        logMap.put(PUSH_CODE, "8100");
                        break;
                    case ACTION_CUSTOM_JSON: {
                        EditText mDataEt = (EditText) findViewById(R.id.data);
                        String data = mDataEt.getText().toString();
                        if (!data.isEmpty()) {
                            logMap.put(PUSH_DATA, data);
                        }
                        logMap.put(PUSH_CODE, "8049");
                        break;
                    }
                    default:
                        logMap.put(PUSH_CODE, "8000");
                        break;
                }
                SharedPreferences prefs = getSharedPreferences(APP_PREF, Context.MODE_PRIVATE);
                String authToken = prefs.getString(KEY_AUTH_TOKEN, null);
                logMap.put(PUSH_AUTH_TOKEN, authToken);
                logMap.put(PUSH_INSTALLID, installId);
                logMap.put(PUSH_TITLE, title);
                logMap.put(PUSH_MSG, message);
                final URL finUrl = url;
                if (null != url) {
                    Runnable task = new Runnable() {
                        @Override
                        public void run() {
                            sendPushPayload(finUrl, logMap);
                        }
                    };
                    ScheduledExecutorService worker = Executors.newSingleThreadScheduledExecutor();
                    worker.schedule(task, 2, TimeUnit.SECONDS);
                }
                Toast.makeText(getApplicationContext(),"You will be receiving a push notification shortly",Toast.LENGTH_SHORT).show();
                if(mAppBG.isChecked()){
                    Intent startMain = new Intent(Intent.ACTION_MAIN);
                    startMain.addCategory(Intent.CATEGORY_HOME);
                    startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(startMain);
                }
            }
        };
    }
}
