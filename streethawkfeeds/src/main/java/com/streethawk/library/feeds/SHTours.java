package com.streethawk.library.feeds;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.streethawk.library.core.Logging;
import com.streethawk.library.core.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Class synchronises tours
 */

public class SHTours implements Constants, ITipClickEvents {
    Activity mActivity;
    private int mTourStepNumber = 0;
    private ArrayList<TipObject> mTourList;
    private String mtourID = null;

    public SHTours(Activity activity) {
        mActivity = activity;
    }

    public void startTour(String tourId) {
        if (null == mActivity)
            return;
        mtourID = tourId;

        Context context = mActivity.getApplicationContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHSHARED_PREF_FEEDLIST,
                Context.MODE_PRIVATE);
        String tourJSON = sharedPreferences.getString(tourId, null);
        if (null == tourJSON) {
            Log.e("SHTours", "tourId " + tourId + " not found");
            return;
        }
        try {
            if (null == mTourList) {
                mTourList = new ArrayList<TipObject>();
            }
            JSONArray tourPayLoad = new JSONArray(tourJSON);
            if (null != tourPayLoad) {
                for (int i = 0; i < tourPayLoad.length(); i++) {
                    TipObject obj = new TipObject();
                    SHTips tips = new SHTips();
                    tips.parseJSONToTipObject(obj, tourPayLoad.getJSONObject(i));
                    mTourList.add(i, obj);
                    tips = null;  //GC
                }
            }
            mTourStepNumber = 0;
            SHTips tips = new SHTips();
            tips.registerClickListener(this);
            tips.showTip(mActivity, mTourList.get(0), false);     //false not to send feed result
        } catch (JSONException e) {
            Log.e("SHTour", "Invalid JSON in tour payload");
            e.printStackTrace();
        }
    }

    @Override
    public void onButtonClickedOnTip(TipObject object, int[] feedResults) {
        JSONObject status = new JSONObject();
        Bundle params = new Bundle();
        params.putString(Util.CODE, Integer.toString(CODE_FEED_RESULT));
        int isNext = feedResults[0];
        try {
            params.putInt(SHFEEDID, Integer.parseInt(mtourID));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return; //No feed result sent if feedid is not a string
        }
        if(-2==feedResults[0]){
            try {
                status.put(RESULT_FEED_DELETE, true);
                if (1 == feedResults[1]) {
                    status.put(RESULT_RESULT, object.getAcceptedButtonTitle());
                } else {
                    status.put(RESULT_RESULT, object.getDelineButtonTitle());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Logging manager = Logging.getLoggingInstance(mActivity.getApplicationContext());
            manager.addLogsForSending(params);
            return;
        }
        try {
            params.putInt(SHFEEDID, Integer.parseInt(mtourID));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return; //No feed result sent if feedid is not a string
        }
        try {
            if (1 == isNext) {
                status.put(RESULT_RESULT, object.getAcceptedButtonTitle());
            } else {
                status.put(RESULT_RESULT, object.getDelineButtonTitle());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mTourStepNumber += feedResults[0];
        if (mTourStepNumber < mTourList.size()) {
            try {
                status.put(RESULT_FEED_DELETE, false);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            Logging manager = Logging.getLoggingInstance(mActivity.getApplicationContext());
            manager.addLogsForSending(params);
            if(mTourStepNumber>=0)
                new SHTips().showTip(mActivity, mTourList.get(mTourStepNumber), false);     //false not to send feed result
            } else {
            try {
                status.put(RESULT_FEED_DELETE, true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Logging manager = Logging.getLoggingInstance(mActivity.getApplicationContext());
            manager.addLogsForSending(params);
        }
    }
}