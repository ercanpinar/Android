package com.streethawk.streethawkapp.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.streethawk.streethawkapp.R;

public class SetupActivity extends AppCompatActivity implements ConstantsInterface {

    private Activity mActivity;

    private final String SHOW_DOC = "Show Doc";
    private final String DISMISS = "Dismiss";
    private final String OKAY = "Okay";

    private final String DEFAULT_AUTH_TOKEN = "iloEZKLS5IusDRJ0eQxN6HRHTwhAOD";
    private final String DEFAULT_APP_KEY = "SHTestPointzi";
    private final String DEFAULT_PROJECT_NUMBER = "491295755890";

    EditText app_keyET;
    EditText senderidET;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        mActivity = this;
        app_keyET = (EditText) findViewById(R.id.appkey);
        senderidET = (EditText) findViewById(R.id.senderid);
    }

    /**
     * Function opens webview to register with StreetHawk
     */
    public void registerAppKey(View view) {
        openUrlInBrowser("https://dashboard.streethawk.com/static/bb/#signup");
    }

    private void openUrlInBrowser(String url) {
        if (null == url)
            return;
        Intent docs = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(docs);
    }

    /**
     * Function stores setup params in database
     *
     * @param view
     */
    public void saveRegisterParams(View view) {
        String appKey = null;
        String senderid = null;
        String authToken = null;
        if (null != app_keyET)
            appKey = app_keyET.getText().toString();
        if (appKey.isEmpty()) {
            Toast.makeText(getApplicationContext(), getString(R.string.ERRMSG_EMPTY_APP_KEY), Toast.LENGTH_LONG).show();
            return;
        }
        if (appKey.startsWith("demo")) {
            Toast.makeText(getApplicationContext(), getString(R.string.ERRMSG_DEMO_APP_KEY), Toast.LENGTH_LONG).show();
            return;
        }


        if (null != senderidET) {
            senderid = senderidET.getText().toString();
        }
        if (senderid.isEmpty()) {
            Toast.makeText(getApplicationContext(), getString(R.string.WRNMSG_EMPTY_PROJECTNO), Toast.LENGTH_LONG).show();
        }
        SharedPreferences prefs = getSharedPreferences(APP_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = prefs.edit();
        e.putString(KEY_APP_KEY, appKey);
        e.putBoolean(KEY_SETUP, false);
        if (appKey.equals(DEFAULT_APP_KEY)) {
            e.putString(KEY_SENDER_ID, DEFAULT_PROJECT_NUMBER);
            e.putString(KEY_AUTH_TOKEN, DEFAULT_AUTH_TOKEN);
        }
        e.commit();
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(KEY_APP_KEY, appKey);
        startActivity(intent);
    }


    private void displayDialog(String title, String message, final String postitle, String negTitle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity, R.style.StreetHawkDialogTheme);
        if (null != title) {
            builder.setTitle(title);
        }
        if (null != message) {
            builder.setMessage(message);
        }
        if (null == title && null == message) {
            return;
        }

        if (null != postitle) {
            builder.setPositiveButton(postitle, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (postitle) {
                        case SHOW_DOC:
                            openUrlInBrowser("https://streethawk.freshdesk.com/solution/articles/5000608997-configure-push-messaging-android-");
                            break;
                        case OKAY:
                            break;
                        default:
                            Log.e("StreetHawkApp", "Not a valid case in displayDialog SetupActivity");
                            return;
                    }

                }
            });

            if (null != negTitle) {
                builder.setNegativeButton(negTitle, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // dismiss dialog
                    }
                });
            }


        } else {
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    // Dismiss dialog
                }
            });
        }
        builder.create().show();
    }


    public void displayTTAppKey(View view) {
        String title = "App Key";
        String description = "AppKey is your application's unique identifier registered with StreetHawk.\n" +
                "Use register button below to register an appKey for your application\n" +
                "This app doesn't accept the preassigned demo appkey (demoXXX) seen in StreetHawk dashboard";

        displayDialog(title, description, OKAY, null);
    }


    public void displayTTProjectNumber(View view) {
        String title = "Project Number";
        String description = "Project Number or Sender ID as obtained after registering the project with Google's developer console\n" +
                "Click Show Doc for steps to register your project on Google developer console and obtain the project number\n";


        displayDialog(title, description, SHOW_DOC, DISMISS);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_setup, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_autofill) {
            app_keyET.setText(DEFAULT_APP_KEY);
            senderidET.setText(DEFAULT_PROJECT_NUMBER);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
