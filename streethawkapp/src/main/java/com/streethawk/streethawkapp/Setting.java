package com.streethawk.streethawkapp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.streethawk.library.core.Util;

public class Setting extends AppCompatActivity implements Constants {

    Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reregister();
            }
        });
        mActivity = this;
    }

    private void changeTargetUrl(String url) {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = sharedPreferences.edit();
        e.putString("shKeyHost", url);
        e.commit();
    }

    public void onRadioButtonClicked(View view) {
        final String PROD_URL = "https://api.streethawk.com";
        final String DEV_URL = "https://dev.streethawk.com";
        final String KFACTOR_URL = "https://staging.streethawk.com";
        boolean checked = ((RadioButton) view).isChecked();

        switch (view.getId()) {
            case R.id.prod:
                if (checked)
                    changeTargetUrl(PROD_URL);
                break;
            case R.id.kfactor:
                if (checked)
                    changeTargetUrl(KFACTOR_URL);
                break;
        }
    }

    public void clearPushSettings(View view){
        Context context = getApplicationContext();
        SharedPreferences prefs = context.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = prefs.edit();
        e.putString(KEY_AUTH_TOKEN, null);
        e.putString(KEY_SENDER_ID,null);
        e.commit();
        Toast.makeText(context,"Cleared AUTH_TOKEN and Porject Number",Toast.LENGTH_LONG).show();
    }

    public void testAlert(View view){
        new  TestService().SendAlertToServer();
    }


    public void reregister() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity, R.style.StreetHawkDialogTheme);
        builder.setTitle("Are you sure?");
        builder.setMessage("Do you want to reset this install");
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText customUrlET = (EditText) findViewById(R.id.customUrl);
                String customUrl = customUrlET.getText().toString();
                if (null != customUrl) {
                    if (!customUrl.isEmpty()) {
                        changeTargetUrl(customUrl);
                    }
                    Context context = getApplicationContext();
                    SharedPreferences prefs = context.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
                    SharedPreferences.Editor e = prefs.edit();
                    e.putBoolean("install_state", false);
                    e.putString("installid", null);
                    e.putString(KEY_ACCESS_DATA,null);
                    e.commit();
                    // kill the app and let user reopen it
                    finish();
                    System.exit(0);
                }
            }
        });
        builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.create().show();
    }
}
