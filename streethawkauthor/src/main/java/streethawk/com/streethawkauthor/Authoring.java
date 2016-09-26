package streethawk.com.streethawkauthor;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Spinner;

import com.streethawk.library.core.WidgetDB;

import org.json.JSONObject;

import java.util.ArrayList;

public class Authoring implements Constants, IToolBarButtonListener {

    Authoring(){}
    private static Authoring instance =null;
    public static Authoring getInstance(Activity activity){
        mActivity = activity;
        if(null==instance){
            instance = new Authoring();
        }
        return instance;
    }


    private static Activity mActivity;
    private int mStepNumber = 0;
    private String mOption = null;

    private final String TOUR = "Tour";
    private final String TIP = "Tip";
    private final String MODAL = "Modal";


    private JSONObject mPayload;

    private static WindowManager windowManager;
    private static View mBarView;
    WindowManager.LayoutParams winParams;


    private void selectTriggerPosition() {

    }

    /**
     * 1. Create trigger
     * 2. Select widget
     * 3. Recurssion
     */
    private void createTour() {
        mStepNumber++;
    }

    /**
     * 1. Create trigger
     * 2. Select widget
     * 3. Save
     */
    private void createTip() {
        Log.e("Anurag", "Create a tip");
    }

    /**
     * 1. Select trigger
     * 2. Select view
     * 3. Save
     */
    private void createModal() {

    }
    public void forceDismissToolBar(){
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(null!=mBarView){
                    ((WindowManager) mActivity.getApplicationContext().getSystemService(Activity.
                            WINDOW_SERVICE)).removeView(mBarView);
                }

            }
        });
    }
    private void addButtonListener() {
        final Activity currentActivity = new ActivityTracker().getCurrentActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(currentActivity);
        builder.setTitle(currentActivity.getResources().getString(R.string.step1_title));
        builder.setPositiveButton(currentActivity.getResources().getString(R.string.step1_next),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        View view = currentActivity.getLayoutInflater().inflate(R.layout.step1spinner, null);
                        Spinner spinner = (Spinner) view.findViewById(R.id.step1type);
                        mOption = spinner.getSelectedItem().toString();
                        if ((mOption.equals(TOUR)) || (mOption.equals(TIP))) {
                            Intent intent = new Intent(currentActivity, AuthoringActivity.class);
                            intent.putExtra(EXTRA_TOOL_TYPE, mOption);
                            intent.putExtra(EXTRA_PARENT, new ActivityTracker().getNameOfCurrentActivity());
                            AuthoringService instance = AuthoringService.getInstance();
                            instance.clearTipListObject();
                            instance.setType(mOption);
                            currentActivity.startActivity(intent);

                        } else {
                            //TODO: modals
                        }

                    }
                });
        builder.setNegativeButton(currentActivity.getResources().getString(R.string.step1_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.e("Anurag", "Cancel pressed");
                if (mStepNumber != 0) {
                    mStepNumber--;
                }
            }
        });
        View view = currentActivity.getLayoutInflater().inflate(R.layout.step1spinner, null);
        builder.setView(view);
        forceDismissToolBar();
        builder.show();
    }

    private void backButtonListener() {
        Log.e("Anurag", "back button clicked");
    }

    private void saveButtonListener() {
        AuthoringService instance = AuthoringService.getInstance();
        instance.prepareJSONForCampaign();
        //TODO: create dialog to create campaign , ask for title message etc.
        instance.sendToolTipToServer();
        //TODO: create dialog to create another tour
    }

    private void previewButtonListener() {
        Log.e("Anurag", "preview button clicked");
        AuthoringService instance = AuthoringService.getInstance();
        instance.prepareJSONForCampaign();
       //TODO review the tip
    }

    private void nextButtonListener() {
        Log.e("Anurag", "next button clicked");

    }

    private void cancelButtonListener() {
        Log.e("Anurag", "cancel button clicked");
        //TODO add confirmation dialog box here
        forceDismissToolBar();
    }

    @Override
    public void onButtonClick(String title) {

        switch (title) {
            case BUTTON_ADD:
                addButtonListener();
                break;
            case BUTTON_BACK:
                backButtonListener();
                break;
            case BUTTON_SAVE:
                saveButtonListener();
                break;
            case BUTTON_PREVIEW:
                previewButtonListener();
                break;
            case BUTTON_NEXT:
                nextButtonListener();
                break;
            case BUTTON_CANCEL:
                cancelButtonListener();
                break;
            default:
                Log.e("Anurag", "UnHandled button " + title);
                break;
        }
    }

    private static int initialX;
    private static int initialY;
    private static float initialTouchX;
    private static float initialTouchY;

    @Override
    public void onTouchClick(MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                initialX = winParams.x;
                initialY = winParams.y;
                initialTouchX = motionEvent.getRawX();
                initialTouchY = motionEvent.getRawY();
                return;
            case MotionEvent.ACTION_UP:
                return;
            case MotionEvent.ACTION_MOVE:
                winParams.x = initialX + (int) (motionEvent.getRawX() - initialTouchX);
                winParams.y = initialY + (int) (motionEvent.getRawY() - initialTouchY);
                windowManager.updateViewLayout(mBarView, winParams);
                return;
        }
    }



    private View.OnTouchListener toolBarListener() {
        return new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = winParams.x;
                        initialY = winParams.y;
                        initialTouchX = motionEvent.getRawX();
                        initialTouchY = motionEvent.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        winParams.x = initialX + (int) (motionEvent.getRawX() - initialTouchX);
                        winParams.y = initialY + (int) (motionEvent.getRawY() - initialTouchY);
                        windowManager.updateViewLayout(mBarView, winParams);
                        return true;
                }
                return false;
            }
        };
    }



    public void displayMainToolBar(int mode){

        switch(mode){
            case TOOLBAR_ADD: {
                windowManager = (WindowManager) mActivity.getSystemService(Activity.WINDOW_SERVICE);
                winParams = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.TYPE_PHONE,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSLUCENT);
                winParams.gravity = Gravity.TOP | Gravity.LEFT;
                winParams.x = 0;
                winParams.y = windowManager.getDefaultDisplay().getHeight() / 2;
                Toolbar bar = new Toolbar();
                bar.showAddButton = true;
                bar.showCancelButton = true;
                bar.registerClickListener(this);
                mBarView = bar.getToolBarView(mActivity.getApplicationContext());
                mBarView.setOnTouchListener(toolBarListener());
                windowManager.addView(mBarView, winParams);
            }
                break;
            case TOOLBAR_TIP_SAVE: {
                windowManager = (WindowManager) mActivity.getSystemService(Activity.WINDOW_SERVICE);
                winParams = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.TYPE_PHONE,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSLUCENT);
                winParams.gravity = Gravity.TOP | Gravity.LEFT;
                winParams.x = 0;
                winParams.y = windowManager.getDefaultDisplay().getHeight() / 2;
                Toolbar bar = new Toolbar();
                bar.showSaveButton = true;
                bar.showPlayButton = true;
                bar.showCancelButton = true;
                bar.registerClickListener(this);
                mBarView = bar.getToolBarView(mActivity.getApplicationContext());
                mBarView.setOnTouchListener(toolBarListener());
                windowManager.addView(mBarView, winParams);
            }
                break;
            case TOOLBAR_TOUR_SAVE:
                break;
            default:
                break;

        }
    }

    public void startAuthoring() {
        if (null == mActivity)
            return;
        Intent intent = new Intent(mActivity, AuthoringService.class);
        mActivity.startService(intent);
        displayMainToolBar(TOOLBAR_ADD);
    }
}
