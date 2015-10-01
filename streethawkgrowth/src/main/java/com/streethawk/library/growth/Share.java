/*
 * Copyright (c) StreetHawk, All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */
package com.streethawk.library.growth;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.streethawk.library.core.Util;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

class Share {
    private static final String SUBTAG = "Share ";
    private final String UTM_SOURCE     = "utm_source";
    private final String UTM_MEDIUM      = "utm_medium";
    private final String UTM_TERM    = "utm_term";
    private final String UTM_CONTENT = "utm_content";
    private final String DEFAULT_URL = "destination_url_default";
    private final String APP_KEY     = "app_key";
    private final String SCHEME = "scheme";
    private final String URI = "uri";
    private final String INSTALL_ID = "sh_cuid";
    private final String ID = "utm_campaign";


    private final String SHARE = "https://pointzi.streethawk.com/originate_viral_share/";
    private static Dialog pickerDialog = null;                          //TODO: Material design for picker dialog
    private Context mContext;
    private Activity mActivity;
    public Share(Activity activity){
        this.mActivity = activity;
        this.mContext = activity.getApplicationContext();
        if(null==mActivity){
            Log.e(Util.TAG,SUBTAG+"activity is null,returning..");
            return;
        }
    }

    class PickerModel {
        private String mAppName;
        private String mPackageName;
        private String mClassName;
        public PickerModel(String appName,String packageName,String className){
            this.mAppName=appName;
            this.mPackageName = packageName;
            this.mClassName = className;
        }
        public String getPackageName(){return mPackageName;}
        public String getAppName(){return mAppName;}
        public String getClassName(){return mClassName;}

    } //End of pickerModel class

    class PickerAdapter extends BaseAdapter {
        private ArrayList<PickerModel> mAppList;
        private Context mPickerAdapterContext;
        private PickerModel temp;
        public PickerAdapter(Context context, ArrayList<PickerModel> appList) {
            this.mPickerAdapterContext = context;
            this.mAppList = appList;
        }
        @Override
        public int getCount() {
            if (mAppList.size() <= 0)
                return 1;
            return mAppList.size();
        }
        @Override
        public Object getItem(int position) {
            return position;
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            PickerView holder = new PickerView(mPickerAdapterContext);
            if (convertView == null) {
                view = holder.getPickerView();
                view.setTag(holder);
            } else {
                holder = (PickerView) view.getTag();
            }
            if (mAppList.size() <= 0) {
                Log.w(Util.TAG,SUBTAG+"No application found to share url");
            } else {
                temp = (PickerModel) mAppList.get(position);
                String packageName = temp.getPackageName();
                try {
                    Drawable icon = mPickerAdapterContext.getPackageManager().getApplicationIcon(packageName);
                    holder.updateView(icon,temp.getAppName());
                }catch(PackageManager.NameNotFoundException e){

                }
            }
            return view;
        }
    } //End of PickerAdapter class

    class PickerView extends View {
        private Context mContext;
        private LinearLayout baseLayout;
        private ImageView iconView;
        private TextView appTitleView;
        public PickerView(Context context) {
            super(context);
            this.mContext = context;
        }
        public void updateView(Drawable appIcon,String appTitle){
            LinearLayout.LayoutParams params = new  LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
            params.setMargins(10,5,10,5);
            baseLayout.removeView(appTitleView);
            baseLayout.removeView(iconView);
            iconView = new ImageView(mContext);
            iconView.setLayoutParams(params);
            appTitleView = new TextView(mContext);
            appTitleView.setLayoutParams(params);
            appTitleView.setGravity(Gravity.CENTER);
            appTitleView.setTextAppearance(mContext,android.R.style.TextAppearance_DeviceDefault_Medium);
            appTitleView.setTextColor(Color.parseColor("#000000"));
            appTitleView.setText("\t\t"+appTitle);
            if(null!=appIcon)
                iconView.setImageDrawable(appIcon);
            else
                iconView.setImageDrawable(mContext.getApplicationInfo().loadIcon(mContext.getPackageManager()));
            baseLayout.addView(iconView);
            baseLayout.addView(appTitleView);
        }
        public View getPickerView(){
            baseLayout = new LinearLayout(mContext);
            baseLayout.setOrientation(LinearLayout.HORIZONTAL);
            iconView = new ImageView(mContext);
            appTitleView = new TextView(mContext);
            baseLayout.addView(iconView);
            baseLayout.addView(appTitleView);
            return baseLayout;
        }
    } //End of PickerView class


    private void displayPickerLayout(final String share_guid_url, final ArrayList<PickerModel> appOptions){
        // Do not delete below. This will be layout for backward compatible devices
        final String share_guid_url_tmp = share_guid_url;
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                final ListView appOptionsPicker = new ListView(mContext);
                PickerAdapter adapter = new PickerAdapter(mContext, appOptions);
                appOptionsPicker.setAdapter(adapter);
                builder.setView(appOptionsPicker);
                if (null == pickerDialog) {
                    pickerDialog = new Dialog(mContext);
                }
                pickerDialog.dismiss();
                pickerDialog = builder.create();
                appOptionsPicker.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> Parent, View view,
                                            final int position, long id) {
                        pickerDialog.dismiss();
                        String className = appOptions.get(position).getClassName();
                        String packageName = appOptions.get(position).getPackageName();
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setClassName(packageName, className);
                        intent.setType("text/plain");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(Intent.EXTRA_TEXT, share_guid_url_tmp);
                        mContext.startActivity(intent);
                    }
                });
                if (pickerDialog.isShowing())
                    pickerDialog.dismiss();
                pickerDialog.show();
            }
        });
    }

    /**
     * API to initiate sharing of url.
     *
     * @param ID     share id
     * @param scheme scheme registered for deeplinking
     * @param uri    deeplink url which will open at other end
     * @return
     */
    public boolean originateShare(String ID, String scheme, String uri,
                                  String utm_source, String utm_medium, String utm_term, String campaign_content, String default_url,
                                  final IGrowth object) {
        if (null == mContext)
            return false;
        if (!Util.isNetworkConnected(mContext)) {
            Log.w(Util.TAG,SUBTAG+ "Device is not connected to network");
            return false;
        }
        String app_key = Util.getAppKey(mContext);
        if (null == app_key) {
            Log.e(Util.TAG, "Appkey is not defined.. returning");
            return false;
        }
        String installId = Util.getInstallId(mContext);
        if (null == installId) {
            Log.w(Util.TAG,SUBTAG+ "App is not registered with StreetHawk server");
            return false;
        }
        ID = ID == null ? "" : ID.trim();
        scheme = scheme == null ? "" : scheme.trim();
        uri = uri == null ? "" : uri.trim();

        HashMap<String, String> logMap = new HashMap<String, String>();
        logMap.put(APP_KEY, app_key);
        logMap.put(this.ID, ID);
        logMap.put(INSTALL_ID, installId);
        logMap.put(URI, uri);
        logMap.put(SCHEME, scheme);
        logMap.put(UTM_SOURCE, utm_source);
        logMap.put(UTM_MEDIUM, utm_medium);
        logMap.put(UTM_TERM, utm_term);
        logMap.put(UTM_CONTENT, campaign_content);
        logMap.put(DEFAULT_URL, default_url);
        BufferedReader reader = null;
        try {
            URL url = new URL(SHARE);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("X-Installid", installId);
            connection.setRequestProperty("X-App-Key", app_key);
            connection.setRequestProperty("X-Version", Util.getLibraryVersion());
            OutputStream os = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            String logs = Util.getPostDataString(logMap);
            writer.write(logs);
            writer.flush();
            writer.close();
            os.close();
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String answer = reader.readLine();
            if (connection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                if (null == answer)
                    return false;
                if (answer.isEmpty())
                    return true;
                String share_guid_url;
                try {
                    JSONObject answerObject = new JSONObject(answer);
                    share_guid_url = answerObject.getString("share_guid_url");
                } catch (JSONException e) {
                    share_guid_url = "";
                    e.printStackTrace();
                }
                if (null == object) {
                    Log.w(Util.TAG,SUBTAG+"pass IGrowth object to handle share url");
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, share_guid_url);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    //mContext.startActivity(intent);       // Launch original picker

                    final PackageManager packageManager = mContext.getPackageManager();
                    final List<ResolveInfo> pkgAppsList = packageManager.queryIntentActivities(intent, 0);
                    final ArrayList<PickerModel> appOptions = new ArrayList<PickerModel>();

                    for (ResolveInfo res : pkgAppsList) {
                        String packageName = res.activityInfo.packageName;
                        ApplicationInfo applicationInfo;
                        try {
                            applicationInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
                        } catch (final PackageManager.NameNotFoundException e) {
                            applicationInfo = null;
                            return false;
                        }
                        final String applicationName = res.loadLabel(packageManager).toString();
                        final String className = res.activityInfo.name;
                        appOptions.add(new PickerModel(applicationName, packageName, className));
                    }
                    displayPickerLayout(share_guid_url, appOptions);
                } else {
                    object.onReceiveShareUrl(share_guid_url);
                }
            }else {
                if (null == object) {
                    Log.e(Util.TAG,SUBTAG+ "Error Share" + answer);
                } else {
                    JSONObject obj;
                    try {
                        obj = new JSONObject(answer);
                    } catch (JSONException e) {
                        obj = null;
                        e.printStackTrace();
                    }
                    object.onReceiveErrorForShareUrl(obj);
                }
            }
            connection.disconnect();
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
