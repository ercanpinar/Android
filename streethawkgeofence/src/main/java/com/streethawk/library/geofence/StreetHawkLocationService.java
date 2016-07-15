package com.streethawk.library.geofence;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.streethawk.library.core.Util;

/**
 * Location service to fetch user's location
 */
public class StreetHawkLocationService extends Service implements Constants{
        LocationManager locationManager;
        Context mContext;
        private static StreetHawkLocationService mStreethawkLocationService;
        public static StreetHawkLocationService getInstance() {
            if (null == mStreethawkLocationService)
                mStreethawkLocationService = new StreetHawkLocationService();
            return mStreethawkLocationService;
        }

        public StreetHawkLocationService() {
        }


    private void registerScheduledTask() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean taskRegistered = (PendingIntent.getBroadcast(mContext, 0,
                        new Intent(BROADCAST_APP_STATUS_CHK),
                        PendingIntent.FLAG_NO_CREATE) != null);
                if (taskRegistered) {
                    return;
                }
                SharedPreferences pref = mContext.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
                SharedPreferences.Editor e = pref.edit();
                e.putLong(SHTASKTIME, System.currentTimeMillis());
                e.commit();
                AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(mContext, SHCoreModuleReceiver.class);
                intent.setAction(BROADCAST_APP_STATUS_CHK);
                intent.putExtra(SHPACKAGENAME,mContext.getPackageName());
                PendingIntent appStatusIntent = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                long DEBUG_INTERVAL_2MINUTES = 120000l;
                alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), AlarmManager.INTERVAL_HOUR, appStatusIntent);
                //alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), DEBUG_INTERVAL_2MINUTES, appStatusIntent);
                if(Util.getSHDebugFlag(mContext)){
                    Log.d(Util.TAG,"*** Running hourly task for locations ");
                }
            }
        }).start();
    }
        @Override
        public void onCreate() {
            mContext = getApplicationContext();
            registerScheduledTask();
        }

    public void StoreLocationsForLogging(Context context){
        Location location = getLastKnownLocation(context);
        SharedPreferences sharedPreferences = context.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = sharedPreferences.edit();
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        e.putString(Util.LOG_LAT,Double.toString(lat));
        e.putString(Util.LOG_LNG,Double.toString(lng));
        e.commit();
        if(Util.getSHDebugFlag(context)){
            Log.d(Util.TAG,"  "+"*** Stored locations for logging"+lat+","+lng);
        }
    }

        public Location getLastKnownLocation(Context context){
            locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
            String locationProvider=null;
            try {
                if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                    locationProvider = LocationManager.GPS_PROVIDER;
                }
            } catch(Exception ex) {
                locationProvider = null;
            }
            try {
                if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                    locationProvider = LocationManager.NETWORK_PROVIDER;
                }
            } catch(Exception ex) {
                locationProvider = null;
            }
            if(null==locationProvider)
                return null;
            try {
                return locationManager.getLastKnownLocation(locationProvider);
            }catch(SecurityException e){
                return null;
            }
        }

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
    }
