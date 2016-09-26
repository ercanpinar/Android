package com.streethawk.streethawkapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmTaskReceiver extends BroadcastReceiver{
    private final String TAG = "PushTester";
    public AlarmTaskReceiver() {
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("com.streethawk.pushtester.alarmtask")){
            Log.e(TAG,"Received alarm task");
            new PushPingService().reportToServer();
        }
    }
}
