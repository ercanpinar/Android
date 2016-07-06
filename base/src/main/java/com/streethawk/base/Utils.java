package com.streethawk.base;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Calendar;
import java.util.Date;

/**
 * Utils to be used by other modules
 */
public class Utils implements Constants{

    /**
     * Returns Streethawk SDK version
     * @return
     */
    public static String getSHLibrarySDKVersion(){
        return SH_LIBRARY_VERSION;
    }


    /**
     * Returns name of the platform
     * @return
     */
    public static String getPlatformName() {
        switch (getPlatformType()) {
            case PLATFORM_ANDROID_NATIVE:
                return "native";
            case PLATFORM_XAMARIN:
                return "xamarin";
            case PLATFORM_TITANIUM:
                return "titanium";
            case PLATFORM_PHONEGAP:
                return "phonegap";
            case PLATFORM_UNITY:
                return "unity";
            default:
                return "native";
        }
    }

    /**
     * Function to get numeric type for development platform, where type is
     * ANDROID_NATIVE = 0;
     * PHONEGAP       = 1;
     * TITANIUM       = 2;
     * XAMARIN        = 3;
     * UNITY          = 4;
     *
     * @return integer representating platform type for development platform.
     */
    public static int getPlatformType() {
        return RELEASE_PLATFORM;
    }


    /**
     * Returns distribution type
     * @return
     */
    public static final String getDistributionType() {
        switch(getPlatformType()){
            case PLATFORM_ANDROID_NATIVE:
                return DISTRIBUTION_AAR;
            default:
                return DISTRIBUTION_REFERENCE_LIB;
        }
    }

    public static String getInstallId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
        String id = prefs.getString(INSTALL_ID, null);
        return id;
    }


    /**
     * Api returns timezone offset in minutes
     *
     * @return timezone offset in minutes
     */
    public static int getTimeZoneOffsetInMinutes() {
        Calendar c2 = Calendar.getInstance();
        c2.getTimeZone();
        Date date = new Date();
        boolean timezone = c2.getTimeZone().inDaylightTime(date);
        if (timezone)
            return ((c2.getTimeZone().getRawOffset() / (1000 * 60)) + (c2.getTimeZone().getDSTSavings() / (1000 * 60)));
        else
            return ((c2.getTimeZone().getRawOffset() / (1000 * 60)));
    }




}
