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

package com.streethawk.library.push;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.streethawk.library.core.Util;

public class GCMReceiver extends WakefulBroadcastReceiver {

    /**
     *Display payload for debugging
     * @param bundle
     */
    public static void displayAllExtras(Bundle bundle){
        for (String key : bundle.keySet()) {
            Object value = bundle.get(key);
            Log.d(Util.TAG, String.format("%s %s (%s)", key,
                    value.toString(), value.getClass().getName()));
        }
    }


    private void dispatchThirdPartyPayload(Bundle extras){
        ISHObserver observer = SHGcmListenerService.getISHObserver();
        if(null==observer){
            Log.i(Util.TAG,"ISHObserver not registered for accepting third party push payload. returning");
            return;
        }else{
            observer.onReceiveNonSHPushPayload(extras);
        }
    }

    @Override
    public final void onReceive(final Context context, final Intent intent) {
        try{
        if ("com.google.android.c2dm.intent.RECEIVE".equals(intent.getAction())) {
            //displayAllExtras(intent.getExtras());
            Bundle extras = intent.getExtras();
            if (null == extras)
                return;
            String installId = extras.getString(Util.INSTALL_ID);
            String storedInstallId = Util.getInstallId(context);
            if (null == installId) {
                dispatchThirdPartyPayload(extras);
            }
            if (null == storedInstallId)
                return;
            if(null!=installId) {
                if (!(installId.equals(storedInstallId))) {
                    return;  // return if storedInstall id is not matching my install id
                }
            }
            ComponentName comp = new ComponentName(context.getPackageName(), SHGcmListenerService.class.getName());
            startWakefulService(context, (intent.setComponent(comp)));
            setResultCode(Activity.RESULT_OK);
        }

    }catch (Exception e){
            e.printStackTrace();
        }
    }
}

