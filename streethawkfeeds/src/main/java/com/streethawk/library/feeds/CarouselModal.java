package com.streethawk.library.feeds;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.streethawk.library.core.Logging;
import com.streethawk.library.core.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Display group of modals in Carousel view
 */
public class CarouselModal implements ITipClickEvents,Constants{

    private int mModalStepNumber = 0;
    private ArrayList<TipObject> mModalList;
    private String mModalId = null;
    private Activity mActivity;

    public CarouselModal(Activity activity) {
        mActivity = activity;
    }


    public void startCarousel(String tourId) {
        if (null == mActivity)
            return;
        mModalId = tourId;

        Context context = mActivity.getApplicationContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHSHARED_PREF_FEEDLIST,
                Context.MODE_PRIVATE);
        String tourJSON = sharedPreferences.getString(tourId, null);
        if (null == tourJSON) {
            Log.e("SHTours", "tourId " + tourId + " not found");
            return;
        }
        try {
            if (null == mModalList) {
                mModalList = new ArrayList<TipObject>();
            }
            JSONArray tourPayLoad = new JSONArray(tourJSON);
            if (null != tourPayLoad) {
                for (int i = 0; i < tourPayLoad.length(); i++) {
                    TipObject obj = new TipObject();
                    SHTips tips = new SHTips();
                    tips.parseJSONToTipObject(obj, tourPayLoad.getJSONObject(i));
                    mModalList.add(i, obj);
                    tips = null;  //GC
                }
            }
            mModalStepNumber = 0;
            Modal tips = new Modal();
            tips.registerClickListener(this);
            tips.showModal(mActivity, mModalList.get(0));     //false not to send feed result
        } catch (JSONException e) {
            Log.e("SHTour", "Invalid JSON in tour payload");
            e.printStackTrace();
        }
    }

    @Override
    public void onButtonClickedOnTip(TipObject object, int[] feedResults) {
        JSONObject status = new JSONObject();
        Bundle params = new Bundle();
        params.putInt(Util.CODE,CODE_FEED_RESULT);
        int isNext = feedResults[0];
        try {
            params.putInt(SHFEEDID, Integer.parseInt(mModalId));
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
            params.putInt(SHFEEDID, Integer.parseInt(mModalId));
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
        mModalStepNumber += feedResults[0];
        if (mModalStepNumber < mModalList.size()) {
            try {
                status.put(RESULT_FEED_DELETE, false);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            Logging manager = Logging.getLoggingInstance(mActivity.getApplicationContext());
            manager.addLogsForSending(params);
            if(mModalStepNumber>=0)
                new SHTips().showTip(mActivity, mModalList.get(mModalStepNumber), false);     //false not to send feed result
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
