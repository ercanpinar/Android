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

import com.streethawk.library.core.Util;

import org.json.JSONException;
import org.json.JSONObject;

public class SHCoreModuleReceiver extends BroadcastReceiver implements Constants {
    private final String FEED = "feed";
    public SHCoreModuleReceiver() {}
    @Override
    public void onReceive(Context context, Intent intent){
        if (intent.getAction() == Util.BROADCAST_SH_APP_STATUS_NOTIFICATION) {
            String installId = intent.getStringExtra(Util.INSTALL_ID);
            if(null==installId)
                return;
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
                                        }if (null == storedFeedTime) {
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
                                    }
                                }
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
