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
    public SHCoreModuleReceiver() {}
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