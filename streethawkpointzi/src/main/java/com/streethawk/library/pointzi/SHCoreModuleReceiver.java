package com.streethawk.library.pointzi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.streethawk.library.core.Util;

import org.json.JSONException;
import org.json.JSONObject;

public class SHCoreModuleReceiver extends BroadcastReceiver implements Constants {
    public SHCoreModuleReceiver() {
    }

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
                                        new Pointzi().fetchPointziPayload(context);

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
