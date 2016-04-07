package com.streethawk.library.push;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.streethawk.library.core.Util;

public class SHGcmListenerService extends GcmListenerService implements Constants{

    private static ISHObserver mISHObserver=null;

    public static void setISHObserver(ISHObserver observer){
        mISHObserver = observer;
    }

    public static ISHObserver getISHObserver(){
        return mISHObserver;
    }


    private static final String SHGCM_SENDER_KEY_APP = "shgcmsenderkeyapp";
    @Override
    public void onMessageReceived(String from,Bundle data){
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
        String storedSenderId = sharedPreferences.getString(SHGCM_SENDER_KEY_APP,null);
        if(null==storedSenderId){
            Log.e(Util.TAG,"Project number is null returning..");
            return;
        }
        if(!from.endsWith(storedSenderId)){
            Log.e(Util.TAG,"Mismatched stored and from project number in push");
            return;
        }
        String msgId = data.getString(PUSH_MSG_ID);
        if(null==msgId){
            Log.e(Util.TAG,"Invalid messageId " + msgId);
            return;
        }
        GcmMessage message = new GcmMessage();
        if (!message.storePushMessageData(getApplicationContext(), data)) {
            return;
        }
        Intent pushNotificationIntent = new Intent();
        pushNotificationIntent.setAction(BROADCAST_SH_PUSH_NOTIFICATION);
        pushNotificationIntent.putExtra(MSGID, msgId);
        sendBroadcast(pushNotificationIntent);
    }
}