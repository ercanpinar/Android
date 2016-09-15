package streethawk.com.streethawkauthor;

import android.app.Activity;

/**
 * Created by anuragkondeya on 13/09/2016.
 */
public class ActivityTracker {


    private static Activity mActivity;

    public Activity getCurrentActivity(){
        return mActivity;
    }


    public void onLeavingNewActivity(Activity activity) {

        //TODO
    }

    public void onApplicationForegronded(Activity activity) {
        //TODO

    }

    public void onApplicationBackgrounded(Activity activity) {
        //TODO

    }

    public void onOrientationChange(Activity activity) {

        //TODO

    }

    public void onEnteringNewActivity(Activity activity) {

        mActivity = activity;

    }


}
