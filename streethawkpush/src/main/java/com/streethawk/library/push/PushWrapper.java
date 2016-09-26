package com.streethawk.library.push;

import android.content.Context;
import android.os.Bundle;

/**
 * Wrapper function for phonegap
 */
public class PushWrapper implements ISHObserver{

    private static PushWrapper instance;
    private static Context mContext;

    private PushWrapper(){}


    public static PushWrapper getInstance(Context context){
        mContext = context;
        if(null!=instance)
            instance = new PushWrapper();
        return instance;
    }


    public void registerSHObserver(){
        Push.getInstance(mContext).registerSHObserver(this);
    }

    public void setPushDataCallback(){

    }

    public void setPushResultCallback(){

    }

    public void setRawJsonCallback(){

    }

    public void registerNonSHPushPayloadObserver(){

    }

    public void setNotifyNewPageCallback(){

    }


    @Override
    public void shReceivedRawJSON(String title, String message, String json) {

    }

    @Override
    public void shNotifyAppPage(String pageName) {

    }

    @Override
    public void onReceivePushData(PushDataForApplication pushData) {

    }

    @Override
    public void onReceiveResult(PushDataForApplication resultData, int result) {

    }

    @Override
    public void onReceiveNonSHPushPayload(Bundle pushPayload) {

    }
}
