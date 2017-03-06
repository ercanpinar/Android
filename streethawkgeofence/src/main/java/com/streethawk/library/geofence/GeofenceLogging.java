package com.streethawk.library.geofence;

import android.content.Context;
import android.os.Bundle;

import com.streethawk.library.core.Logging;

/**
 * Wrapper class over Logging to handling missing logging class in Xamarin
 */
class GeofenceLogging {

    private static GeofenceLogging mInstance = null;
    private static Context mContext;

    private GeofenceLogging() {
    }

    public static GeofenceLogging getInstance() {
        if (null == mInstance) {
            mInstance = new GeofenceLogging();
        }
        return mInstance;
    }

    public void sendLogs(Context context, Bundle params) {
        Logging.getLoggingInstance(context).addLogsForSending(params);

    }
}
