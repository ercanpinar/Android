package com.streethawk.streethawkdev;

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
            new PushPingService().reportToServer();
        }
    }
}
