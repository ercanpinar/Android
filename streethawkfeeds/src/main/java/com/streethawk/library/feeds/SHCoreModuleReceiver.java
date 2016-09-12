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

public class SHCoreModuleReceiver extends BroadcastReceiver implements Constants {
    private final String FEED = "feed";

    public SHCoreModuleReceiver() {
    }

    public static int mPaginationCnt = 0;


    private void unit_test_tour(Context context) {
        readFeedData(context, 0);
    }


    private void parseAndSaveTrigger(Context context,JSONObject setup,String feedid){
        if(null==setup)
            return;
        if(null==feedid)
            return;

        SHTriger obj =  new SHTriger();
        try{
            obj.setDisplay(setup.getString(SETUP_DISPLAY));
        } catch (JSONException e) {
            obj.setDisplay(null);
        }
        try{
            obj.setTriger(setup.getString(SETUP_TRIGGER));
        } catch (JSONException e) {
            obj.setTriger(null);
        }
        try{
            obj.setTarget(setup.getString(SETUP_TARGET));
        } catch (JSONException e) {
            obj.setTarget(null);
        }
        try{
            obj.setView(setup.getString(SETUP_VIEW));
        } catch (JSONException e) {
            obj.setView(null);
        }
        try{
            obj.setDelay(setup.getInt(SETUP_DELAY));
        } catch (JSONException e) {
            obj.setDelay(-1);
        }
        try{
            obj.setTool(setup.getString(SETUP_TOOL));
        } catch (JSONException e) {
            obj.setTool(null);
        }
        try{
            JSONObject widget = setup.getJSONObject(SETUP_WIDGET);
            try{
                obj.setWidgetType(widget.getString(SETUP_WIDGET_TYPE));
            } catch (JSONException e) {
                obj.setWidgetType(null);
            }
            try{
                obj.setWidgetLabel(widget.getString(SETUP_WIDGET_LABEL));
            } catch (JSONException e) {
                obj.setWidgetLabel(null);
            }
            try{
                obj.setWidgetCss(widget.getString(SETUP_WIDGET_CSS));
            } catch (JSONException e) {
                obj.setWidgetCss(null);
            }
            try{
                obj.setBGColor(widget.getString(SETUP_WIDGET_BGCOLOR));
            } catch (JSONException e) {
                obj.setBGColor(null);
            }
            try{
                obj.setBGColor(widget.getString(SETUP_WIDGET_PLACEMENT));
            } catch (JSONException e) {
                obj.setBGColor(null);
            }
        } catch (JSONException e) {

        }
        TrigerDB trigetDb = new TrigerDB(context);
        trigetDb.open();
        trigetDb.storeTriggerData(obj);
        trigetDb.close();
    }

    private void parseAndSaveResponse(Context context, String answer) {
        try {
            SharedPreferences sharedPreferences = context.getSharedPreferences(SHSHARED_PREF_FEEDLIST,
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(ALL_FEEDS, answer);                     // Storing all for local feeds
            JSONObject feedItem = new JSONObject(answer);
            Object val = feedItem.get(VALUE);
            if (val instanceof JSONArray) {
                JSONArray arr = (JSONArray) val;
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    String feedId = obj.getString(PAYLOAD_SHFEEDID);
                    JSONObject content = obj.getJSONObject(CONTENT);
                    if (null != content) {
                        JSONObject data = new JSONObject(content.getString(DATA));
                        if (null != data) {
                            JSONObject init = data.getJSONObject(SETUP);
                            if (null != init) {
                                parseAndSaveTrigger(context,init,feedId);
                                String type = init.getString(INIT_TOOL);
                                if (null == type)
                                    type = FEED;
                                switch (type) {
                                    case TOUR:
                                        try {
                                            String tour = data.getString(TOUR);
                                            editor.putString(feedId, tour.toString());
                                            editor.commit();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        editor.commit();
                                        break;
                                    case MODAL:
                                        String modal = data.getString(MODAL);
                                        editor.putString(feedId, modal.toString());
                                        editor.commit();
                                        break;
                                    case TIP:
                                        break;
                                    case NEWS:
                                        break;
                                    case FEED:
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
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
                        mPaginationCnt+=20;
                        readFeedData(context,mPaginationCnt);
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

        unit_test_tour(context);

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
