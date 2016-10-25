package com.streethawk.library.feeds;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

/**
 * Created by anuragkondeya on 20/10/16.
 */

public class PointiziActionListener implements IPointziClickEventsListener,Constants {

    private static PointiziActionListener instance=null;
    private static Activity mActivity;

    private PointiziActionListener(){}

    public static PointiziActionListener getInstance(Activity activity) {
        if (null == instance) {
            instance = new PointiziActionListener();
        }
        mActivity = activity;
        return instance;
    }
    public void setActionFlag(String feedid, int flag){
        if(null!=mActivity) {
            Context context = mActivity.getApplicationContext();
            TrigerDB trigerDb = new TrigerDB(context);
            trigerDb.open();
            trigerDb.updateActionedFlag(feedid,flag);
            trigerDb.close();
        }
    }
    @Override
    public void onButtonClickedOnTip(TipObject object, int[] feedResults) {
        setActionFlag(object.getId(),FLAG_ACTIONED);
    }

    @Override
    public void onButtonClickedOnTour(TipObject object, int[] feedResults) {
        setActionFlag(object.getId(),FLAG_ACTIONED);
    }

    @Override
    public void onButtonClickedOnModal(TipObject object, int[] feedResults) {
        setActionFlag(object.getId(),FLAG_ACTIONED);
    }
}
