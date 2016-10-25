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
package com.streethawk.library.feeds;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteConstraintException;
import android.util.Log;

import com.streethawk.library.core.Util;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;

public class SHCoreModuleReceiver extends BroadcastReceiver implements Constants {
    private final String FEED = "feed";

    public SHCoreModuleReceiver() {
    }

    public static int mPaginationCnt = 0;


    private void unit_test_tour(Context context) {
        readFeedData(context, 0);
    }

    private void parseAndSaveResponse(Context context,String answer){
        TrigerDB trigetDb = new TrigerDB(context);
        trigetDb.open();
        SHTriger trigger = new SHTriger();
        try{
            JSONObject feedItem = new JSONObject(answer);
            Object val = feedItem.get(VALUE);
            if (val instanceof JSONArray) {
                JSONArray arr = (JSONArray) val;
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    trigger.setFeedID(obj.getString(PAYLOAD_SHFEEDID));
                    JSONObject content = obj.getJSONObject(CONTENT);
                    if (null != content) {
                        JSONObject data = new JSONObject(content.getString(DATA));
                        JSONObject init = data.getJSONObject(SETUP);
                        trigger.setSetup(init.toString());
                        String tool = init.getString(SETUP_TOOL);
                        trigger.setTool(tool);
                        String trgr = init.getString(SETUP_TRIGGER);
                        trigger.setTrigger(trgr);
                        trigger.setView(init.getString(SETUP_VIEW));
                        if(null!=tool){
                            JSONArray toolObject = data.getJSONArray(tool);
                            String toolArary = toolObject.toString();
                            trigger.setJSON( toolArary);
                        }
                        trigger.setActioned(0);
                    }
                    trigetDb.storeTriggerData(trigger);
                }
            }
            trigetDb.close();
        }catch (JSONException e) {
            e.printStackTrace();
            trigetDb.close();
        }
    }

    public void readFeedData(final Context context, final int offset) {
        if (null == context)
            return;
        final String INSTALL_ID = "installid";
        final String APP_KEY = "app_key";
        final String EQUALS = "=";
        final String OFFSET = "offset";
        final String AND = "&";
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL baseurl = Util.getFeedUrl(context);
                    String urlParams = baseurl.toString();
                    urlParams += "?" + INSTALL_ID + EQUALS + Util.getInstallId(context) + AND +
                            APP_KEY + EQUALS + Util.getAppKey(context) + AND +
                            OFFSET + EQUALS + offset;
                    URL url = null;
                    try {
                        url = new URL(urlParams);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    BufferedReader input = new BufferedReader(
                            new InputStreamReader(connection.getInputStream()));
                    String answer = input.readLine();
                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        parseAndSaveResponse(context, answer);
                        /*
                        if(!answer.equals("[]")) {
                            mPaginationCnt += 20;
                            readFeedData(context, mPaginationCnt);
                        }
                        */
                    }
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    //Check why am i not receiving feeds from server in app status

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == Util.BROADCAST_SH_APP_STATUS_NOTIFICATION) {
            String installId = intent.getStringExtra(Util.INSTALL_ID);
            if (null == installId) {
                return;
            }
            if (installId.equals(Util.getInstallId(context))) {
                String answer = intent.getStringExtra(Util.APP_STATUS_ANSWER);
                try {
                    JSONObject object = new JSONObject(answer);
                    if (object.has(Util.APP_STATUS)) {
                        if (object.get(Util.APP_STATUS) instanceof JSONObject) {
                            JSONObject app_status = object.getJSONObject(Util.APP_STATUS);
                            if (app_status.has(FEED) && !app_status.isNull(FEED)) {
                                Object value_feed = app_status.get(FEED);
                                if (value_feed instanceof String) {
                                    String receivedTime = (String) value_feed;
                                    SharedPreferences sharedPreferences = context.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
                                    String storedFeedTime = sharedPreferences.getString(SHFEEDTIMESTAMP, null);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    if (null == receivedTime) {
                                        editor.putString(SHFEEDTIMESTAMP, null);
                                        editor.commit();
                                        return;
                                    } else {
                                        /*
                                        TrigerDB trigetDb = new TrigerDB(context);
                                        trigetDb.open();
                                        trigetDb.forceDeleteAllRecords();
                                        trigetDb.close();
                                        */
                                        if (receivedTime.isEmpty()) {
                                            editor.putString(SHFEEDTIMESTAMP, null);
                                            editor.commit();
                                            return;
                                        }
                                        if (null == storedFeedTime) {
                                            editor.putString(SHFEEDTIMESTAMP, receivedTime);
                                            editor.commit();
                                        } else {
                                            if (receivedTime.equals(storedFeedTime)) {
                                                return;
                                            } else {
                                                editor.putString(SHFEEDTIMESTAMP, receivedTime);
                                                editor.commit();
                                            }
                                        }
                                        readFeedData(context, mPaginationCnt);

                                        /* TODO pagination
                                        Intent pushNotificationIntent = new Intent();
                                        pushNotificationIntent.setAction(SHFeedItem.BROADCAST_NEW_FEED);
                                        pushNotificationIntent.putExtra(Util.INSTALL_ID, Util.getInstallId(context));
                                        context.sendBroadcast(pushNotificationIntent);
                                        if(mPaginationCnt==0) {
                                            SharedPreferences feeds = context.getSharedPreferences(SHSHARED_PREF_FEEDLIST,
                                                    Context.MODE_PRIVATE);
                                            SharedPreferences.Editor e = feeds.edit();
                                            e.clear();
                                            e.commit();
                                            Log.e("Anurag","Reading feed data");
                                            readFeedData(context, mPaginationCnt);
                                        }
                                        */
                                    }
                                }
                            }else{
                                //Clear saved tips if feed is null
                                SharedPreferences sharedPreferences = context.getSharedPreferences(SHSHARED_PREF_FEEDLIST,
                                        Context.MODE_PRIVATE);
                                SharedPreferences.Editor e = sharedPreferences.edit();
                                e.clear();
                                e.commit();
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}