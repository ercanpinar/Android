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
package com.streethawk.library.growth;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.streethawk.library.core.PluginBase;
import com.streethawk.library.core.Util;

public class Growth extends PluginBase{

    private static Activity mActivity;
    private static Context mContext;
    private final String SUBTAG = "Growth";
    private static boolean activityLifecycleRegistered = false;
    private String REGISTERED = "flaggrowthregister";

    private static Growth mGrowth = null;

    private Growth(){}

    public static Growth getInstance(Application app,Activity activity){
        setActivityLifecycleCallbacks(app);
        mActivity = activity;
        mContext = activity.getApplicationContext();
        if(null==mGrowth)
            mGrowth = new Growth();
        return mGrowth;
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private static void setActivityLifecycleCallbacks(Application application) {
        if (!activityLifecycleRegistered) {
            application.registerActivityLifecycleCallbacks(GrowthActivityLifecycleCallback.getInstance());
            activityLifecycleRegistered = true;
        }
    }

    /**
     * Use originateShareWithCampaign to get the share URL.
     *
     * @param utm_campaign            Id to be used in StreetHawk Analytics (optional)
     * @param shareUrl                deeplink uri of page to be opened when referred user installs the application on his device (optional)
     * @param IGrowth                 instance of IGrowth. If null, the API automatically fires and intent with Intent.ACTION_SEND
     */
    public void originateShareWithCampaign(String utm_campaign, String shareUrl, IGrowth IGrowth) {
        getShareUrlForAppDownload(utm_campaign, shareUrl, null, null, null, null, null, IGrowth);
    }

    /**
     * Use originateShareWithCampaign to get the share URL.
     *
     * @param utm_campaign     Id to be used in StreetHawk Analytics (optional)
     * @param URI              deeplink uri of page to be opened when referred user installs the application on his device (optional)
     * @param utm_source       Source on which url will be posted (Example facebook, twitter whatsapp etc)
     * @param utm_medium       medium as url will be posted. (Example cpc)
     * @param utm_term         keywords for campaing
     * @param campaign_content contents of campaign
     * @param default_url      Fallback url if user opens url on non mobile devices.
     * @param object           instance of IStreetHawkGrowth. If null, the API automatically fires and intent with Intent.ACTION_SEND
     */
    public void originateShareWithCampaign(String utm_campaign, String URI,
                                                  String utm_source, String utm_medium, String utm_term, String campaign_content, String default_url,
                                                  final IGrowth object) {
        getShareUrlForAppDownload(utm_campaign, URI, utm_source, utm_medium, utm_term, campaign_content, default_url, object);
    }

    /**
     * Use getShareUrlForAppDownload to get the share URL.
     *
     * @param ID                      Id to be used in StreetHawk Analytics (optional)
     * @param deeplink_uri            deeplink uri of page to be opened when referred user installs the application on his device (optional)
     * @param IGrowth instance of     IGrowth. If null, the API automatically fires and intent with Intent.ACTION_SEND
     */
    public void getShareUrlForAppDownload(String ID, String deeplink_uri,
                                                 String utm_source, String utm_medium, String utm_term, String campaign_content, String default_url,
                                                 final IGrowth IGrowth) {
        String scheme = "";

        int index;
        if (null == deeplink_uri)
            deeplink_uri = "";
        if (ID == null)
            ID = "";
        try {
            index = deeplink_uri.indexOf(':');
            if (index > 0) {
                scheme = deeplink_uri.substring(0, index);
                deeplink_uri = deeplink_uri.replaceAll(scheme + "://", "");
            } else {
                scheme = "";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        if (null == utm_source)
            utm_source = "";
        if (null == utm_medium)
            utm_medium = "";
        if (null == utm_term)
            utm_term = "";
        if (null == campaign_content)
            campaign_content = "";
        if (null == default_url)
            default_url = "";

        final String IDTmp = ID;
        final String DLUrlTmp = deeplink_uri;
        final String schemeTmp = scheme;
        final String default_urlTmp = default_url;
        final String campaign_contentTmp = campaign_content;
        final String utm_termTmp = utm_term;
        final String utm_mediumTmp = utm_medium;
        final String utm_sourceTmp = utm_source;

        new Thread(new Runnable() {
            @Override
            public void run() {
                Share share = new Share(mActivity);
                share.originateShare(IDTmp, schemeTmp, DLUrlTmp, utm_sourceTmp, utm_mediumTmp,
                        utm_termTmp, campaign_contentTmp, default_urlTmp, IGrowth);
            }
        }).start();
    }

    /**
     * Call addGrowthModule() to add growth modules in installs which have already been released with StreetHawk core module.
     */
    public void addGrowthModule(){
        String installId = Util.getInstallId(mContext);
        if(null==installId) {
            // For this case
            Log.e(Util.TAG, SUBTAG + " install not registered when init was called");
            return;
        }
        else{
            new Thread(new Runnable() {
                @Override
                public void run() {
                    boolean isRegistered = mContext.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE).getBoolean(REGISTERED,false);
                    if(!isRegistered){
                        Register object =  new Register(mContext);
                        object.registerStreetHawkGrowth();
                    }
                }
            }).start();
            return;
        }
    }



    @Override
    public void notifyInstallRegistered(Context context) {
        Register object =  new Register(mContext);
        object.registerStreetHawkGrowth();
    }
}
