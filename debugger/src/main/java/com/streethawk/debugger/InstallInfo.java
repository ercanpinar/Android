package com.streethawk.debugger;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class InstallInfo extends AppCompatActivity implements Constants{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_install_info);
    }

    public void onResume(){
        super.onResume();
        TextView appKey = (TextView)findViewById(R.id.appkey);
        TextView installId = (TextView)findViewById(R.id.installid);
        TextView modules = (TextView)findViewById(R.id.modules);
        TextView server = (TextView)findViewById(R.id.server);
        TextView lastlogline = (TextView)findViewById(R.id.lastloglinetime);
        TextView pushToken = (TextView)findViewById(R.id.pushtoken);

        appKey.setText(getAppKey());
        installId.setText(getInstallId());
        modules.setText(getModuleList());
        server.setText(getServer());
        lastlogline.setText(getLastLogline());
        pushToken.setText(getPushToken());
    }

    private String getAppKey(){
        final String SHAPP_KEY = "app_key";
        SharedPreferences prefs = getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        return prefs.getString(SHAPP_KEY, "");
    }

    private String getInstallId(){
        final String INSTALL_ID = "installid";
        SharedPreferences prefs = getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        return prefs.getString(INSTALL_ID, "");
    }

    private String getModuleList(){
        return null;
    }

    private String getServer(){
        final String HOST = "shKeyHost";
        SharedPreferences prefs = getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        return prefs.getString(HOST, "");
    }

    private String getLastLogline(){
        return null;
    }

    private String getPushToken(){
        final String ACCESS_DATA = "access_data";
        SharedPreferences prefs = getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        return prefs.getString(ACCESS_DATA, "");
    }
}
