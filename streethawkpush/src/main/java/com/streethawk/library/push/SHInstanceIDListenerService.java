package com.streethawk.library.push;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.android.gms.iid.InstanceIDListenerService;
import com.streethawk.library.core.Util;

import java.io.IOException;

public class SHInstanceIDListenerService extends InstanceIDListenerService implements Constants{
    public SHInstanceIDListenerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onTokenRefresh() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Context context = getApplicationContext();
                SharedPreferences sharedPreferences = context.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
                String projectNumber = sharedPreferences.getString(SHGCM_SENDER_KEY_APP, null);
                if(null==projectNumber) {
                    Log.e(Util.TAG, "Project number is missing");
                    return;
                }
                if(projectNumber.isEmpty()){
                    Log.e(Util.TAG, "Project number is empty");
                    return;
                }
                // Get Registration id
                InstanceID instanceID = InstanceID.getInstance(context);
                String token=null;
                try {
                    token = instanceID.getToken(projectNumber,
                            GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
                // Save Registration id
                SharedPreferences prefs = context.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
                final SharedPreferences.Editor e = prefs.edit();
                e.putString(PUSH_ACCESS_DATA, token);
                e.commit();
                Push.getInstance(context).addPushModule();
                return;
            }
        }).start();

    }
}
