package streethawk.com.streethawkauthor;

import android.app.Activity;
import android.util.Log;

public class ActivityTracker {
    private static Activity mActivity;

    public String getNameOfCurrentActivity() {
        if(null==mActivity)
            return null;
        String className = new StringBuilder(mActivity.getClass().getName()).reverse().toString();
        int indexOfPeriod = className.indexOf(".");
        if (-1 != indexOfPeriod) {
            className = className.subSequence(0, className.indexOf(".")).toString();
            className = new StringBuilder(className).reverse().toString();
            return className;
        }
        return null;
    }

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
        Authoring.getInstance(mActivity).forceDismissToolBar();
    }

    public void onOrientationChange(Activity activity) {

        //TODO

    }

    public void onEnteringNewActivity(Activity activity) {
        mActivity = activity;

    }


}
