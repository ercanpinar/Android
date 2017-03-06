package com.streethawk.sdkdebugger;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class StreetHawkDebugActivity extends Activity implements Constants {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_street_hawk_debug2);
    }


    private final String SHAPP_KEY = "app_key";
    private final String INSTALL_ID = "installid";
    private final String PUSH_ACCESS_DATA = "pushaccessData";
    private final String SHGCM_SENDER_KEY_APP = "shgcmsenderkeyapp";


    private final int AppKey = 0;               // 0
    private final int AppVersion = AppKey + 1;         // 1
    private final int InstallID = AppVersion + 1;     // 2
    private final int Modules = InstallID + 1;      // 3
    private final int PushToken = Modules + 1;        // 4
    private final int PROJNUM = PushToken + 1;      // 5
    private final int Server = PROJNUM + 1;        // 6
    private final int ENABLE_LOGS = Server + 1;         // 7
    private final int Logcat = ENABLE_LOGS + 1;    // 8
    private final int ReRegister = Logcat + 1;        // 9
    private final int ShareInfo = ReRegister + 1;    // 10
    private final int Support = ShareInfo + 1;    // 11


    String[] mOptions = {
            "App Key ",
            "App Version ",
            "Install ID ",
            "Modules ",
            "Push Token ",
            "Project Number ",
            "Server ",
            "Enable StreetHawk Logs",
            "Logcat ",
            "ReRegister ",
            "ShareInfo ",
            "Support ",
    };


    private class CustomArrayAdapter extends ArrayAdapter<String> {

        private String[] mValue;

        public CustomArrayAdapter(Context context, String[] values) {
            super(context, -1, values);
            mValue = values;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getApplicationContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.listviewlayout, parent, false);
            TextView key = (TextView) rowView.findViewById(R.id.shdebugkey);
            TextView value = (TextView) rowView.findViewById(R.id.shdebugvalue);
            key.setText(mValue[position]);
            switch (position) {
                case 0:
                    value.setText(getAppKey());
                    break;
                case 1:
                    value.setText(getAppVersion());
                    break;
                case 2:
                    value.setText(getInstallId());
                    break;
                case 3:
                    value.setText(getModuleList());
                    break;
                case 4:
                    value.setText(getPushToken());
                    break;
                case 5:
                    value.setText(getProjectNumber());
                    break;
                case 6:
                    value.setText(getServer());
                    break;
                default:
                    value.setText("");
            }
            return rowView;
        }
    }

    private String getAppKey() {
        SharedPreferences prefs = getSharedPreferences(SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
        return prefs.getString(SHAPP_KEY, null);
    }

    private String getInstallId() {
        SharedPreferences prefs = getSharedPreferences(SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
        return prefs.getString(INSTALL_ID, null);
    }

    private String getAppVersion() {
        try {
            Context context = getApplicationContext();
            String app_version = null;
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            app_version = packageInfo.versionName;
            if (null == app_version)
                Log.e(TAG, "Application's version name is missing in AndroidManifest.xml");
            if (app_version.isEmpty())
                Log.e(TAG, "Application's version name is empty in AndroidManifest.xml");
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Exception in getAppVersionName");
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private String getModuleList() {
        String NEWLINE = "\n";
        String ModuleList = "";
        try {
            Class.forName("com.streethawk.library.core.StreetHawk");
            ModuleList += "Core" + NEWLINE;
        } catch (ClassNotFoundException e1) {
        }
        try {
            Class.forName("com.streethawk.library.growth.Growth");
            ModuleList += "Growth" + NEWLINE;
        } catch (ClassNotFoundException e1) {
            Log.i(TAG, "Module not present Growth");
        }
        try {
            Class.forName("com.streethawk.library.push.Push");
            ModuleList += "Push" + NEWLINE;
        } catch (ClassNotFoundException e1) {
        }
        try {
            Class.forName("com.streethawk.library.feeds.SHFeedItem");
            ModuleList += "Feeds" + NEWLINE;
        } catch (ClassNotFoundException e1) {
        }

        try {
            Class.forName("com.streethawk.library.beacon.Beacons");
            ModuleList += "Beacons" + NEWLINE;
        } catch (ClassNotFoundException e1) {
        }

        try {
            Class.forName("com.streethawk.library.geofence.SHGeofence");
            ModuleList += "Geofence" + NEWLINE;
        } catch (ClassNotFoundException e1) {
        }

        try {
            Class.forName(" com.streethawk.library.locations.SHLocation");
            ModuleList += "Locations" + NEWLINE;
        } catch (ClassNotFoundException e1) {
        }
        return ModuleList;
    }

    private String getPushToken() {

        SharedPreferences prefs = getSharedPreferences(SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
        return prefs.getString(PUSH_ACCESS_DATA, null);
    }

    private String getServer() {
        SharedPreferences prefs = getSharedPreferences(SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
        return prefs.getString(KEY_HOST, null);
    }

    private String getProjectNumber() {

        SharedPreferences prefs = getSharedPreferences(SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
        return prefs.getString(SHGCM_SENDER_KEY_APP, null);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void copyTextToClipboard(String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("shssahre", text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getApplicationContext(), "Info copied to clipboard ", Toast.LENGTH_LONG).show();
    }

    private void showToastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public AdapterView.OnItemClickListener optionsOnclickListener() {
        return new AdapterView.OnItemClickListener() {

            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                switch (position) {
                    case 0:
                        copyTextToClipboard("App Key:" + getAppKey());
                        break;
                    case 1:
                        copyTextToClipboard("App Version:" + getAppVersion());
                        break;
                    case 2:
                        copyTextToClipboard("InstallId:" + getInstallId());
                        break;
                    case 3:
                        copyTextToClipboard("Modules:" + getModuleList());
                        break;
                    case 4:
                        copyTextToClipboard("Push Token:" + getPushToken());
                        break;
                    case 5:
                        copyTextToClipboard("Project Number:" + getProjectNumber());
                        break;
                    case 6:
                        copyTextToClipboard("Server:" + getServer());
                        break;
                    case 7:
                        SharedPreferences prefs = getSharedPreferences(SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
                        SharedPreferences.Editor e = prefs.edit();
                        e.putBoolean("shdebugflag", true);
                        e.commit();
                        showToastMessage("Logs Enabled Check Logcat with TAG StreetHawk");
                        break;
                    case 8: {
                        Intent intent = new Intent(getApplicationContext(), SHLogCat.class);
                        startActivity(intent);
                    }
                    break;
                    case 9: {
                        Intent intent = new Intent(getApplicationContext(), SHReRegister.class);
                        startActivity(intent);
                    }
                    break;
                    case 10: {
                        String NEWLINE = "\n";
                        String Body = "*** INSTALL INFO ***" + NEWLINE +
                                "App Key: " + getAppKey() + NEWLINE +
                                "App Version: " + getAppKey() + NEWLINE +
                                "InstallID: " + getInstallId() + NEWLINE +
                                "Modules: " + getModuleList() + NEWLINE +
                                "PushToken " + getPushToken() + NEWLINE +
                                "Project Number" + getProjectNumber() + NEWLINE +
                                "Server " + getServer() + NEWLINE +
                                "************** Write your comments below ***************";
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Android Install Info");
                        intent.putExtra(Intent.EXTRA_TEXT, Body);
                        startActivity(intent);
                    }
                    break;
                    case 11: {
                        String NEWLINE = "\n";
                        String Body = "*** INSTALL INFO ***" + NEWLINE +
                                "App Key: " + getAppKey() + NEWLINE +
                                "App Version: " + getAppKey() + NEWLINE +
                                "InstallID: " + getInstallId() + NEWLINE +
                                "Modules: " + getModuleList() + NEWLINE +
                                "PushToken: " + getPushToken() + NEWLINE +
                                "Project Number: " + getProjectNumber() + NEWLINE +
                                "OS: " + "Android " + NEWLINE +
                                "Server " + getServer() + NEWLINE +
                                "************** Write your comments below ***************";
                        Intent mailer = new Intent(Intent.ACTION_SEND);
                        mailer.setType("message/rfc822");
                        mailer.putExtra(Intent.EXTRA_EMAIL, new String[]{"support@streethawk.com"});
                        mailer.putExtra(Intent.EXTRA_SUBJECT, "Android Support");
                        mailer.putExtra(Intent.EXTRA_TEXT, Body);
                        startActivity(mailer);
                        ;
                    }
                    break;
                    default:
                        break;
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        ListView list = (ListView) findViewById(R.id.optionsLv);
        final CustomArrayAdapter adapter = new CustomArrayAdapter(this, mOptions);
        list.setAdapter(adapter);
        list.setOnItemClickListener(optionsOnclickListener());
    }

}
