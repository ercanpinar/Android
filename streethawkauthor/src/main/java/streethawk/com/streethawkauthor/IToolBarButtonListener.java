package streethawk.com.streethawkauthor;

import android.view.MotionEvent;

/**
 * Created by anuragkondeya on 15/09/2016.
 */
interface IToolBarButtonListener {
    public void onButtonClick(String button);
    public void onTouchClick(MotionEvent motionEvent);
}
