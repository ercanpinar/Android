package streethawk.com.streethawkauthor;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Spinner;

import com.streethawk.library.core.WidgetDB;

import org.json.JSONObject;

import java.util.ArrayList;

public class Authoring implements Constants{

    private View.OnTouchListener mActivityTouchListener;


    class ViewDetails{
        public String viewName;
        public float viewX;
        public float viewY;
        public float viewWidth;
        public float viewHeight;

    }

    private Activity mActivity;
    private int mStepNumber = 0;
    private String mOption = null;

    private final String TOUR   = "Tour";
    private final String TIP    = "Tip";
    private final String MODAL  = "Modal";
    private ArrayList<ViewDetails> mViewsOnActivity;

    private JSONObject mPayload;
    public Authoring(Activity activity){
        this.mActivity = activity;
    }


    private void selectTriggerPosition(){

    }

    private void showStep2Dialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle(mActivity.getResources().getString(R.string.step2_title));
        builder.setPositiveButton(mActivity.getResources().getString(R.string.step1_next),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(mActivity,AuthoringActivity.class);
                        intent.putExtra(EXTRA_TOOL_TYPE,mOption);
                        mActivity.startActivity(intent);
                    }
                });
        builder.setNegativeButton(mActivity.getResources().getString(R.string.step1_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.e("Anurag","Cancel pressed");

            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    /**
     * 1. Create trigger
     * 2. Select widget
     * 3. Recurssion
     */
    private void createTour(){
        mStepNumber++;
    }

    /**
     * 1. Create trigger
     * 2. Select widget
     * 3. Save
     */
    private void createTip(){
        Log.e("Anurag","Create a tip");
    }

    /**
     * 1. Select trigger
     * 2. Select view
     * 3. Save
     */
    private void createModal(){

    }

    /**
     * Function returns viewname by stripping package name from it
     *
     * @param fullyQualifiedName
     * @return
     */
    private String getViewName(String fullyQualifiedName) {
        String className = new StringBuilder(fullyQualifiedName).reverse().toString();
        int indexOfPeriod = className.indexOf(".");
        if (-1 != indexOfPeriod) {
            className = className.subSequence(0, className.indexOf(".")).toString();
            className = new StringBuilder(className).reverse().toString();
            return className;
        }
        return null;
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void fillViewList() {
        mViewsOnActivity = new ArrayList<>();
        String viewName = getViewName(mActivity.getClass().getName());
        Context context = mActivity.getApplicationContext();
        WidgetDB.WidgetDBHelper helper = new WidgetDB(context).new WidgetDBHelper(context);
        SQLiteDatabase database = helper.getReadableDatabase();
        String query = "select * from " + WidgetDB.WidgetDBHelper.TOOLTIP_TABLE_NAME +
                " where " + WidgetDB.WidgetDBHelper.COLUMN_PARENT_VIEW + " = '" + viewName.trim() + "'";
        Cursor cursor = database.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                int resId = (cursor.getInt(cursor.getColumnIndex(WidgetDB.WidgetDBHelper.COLUMN_RES_ID)));
                if (-1 != resId) {
                    View view = mActivity.findViewById(resId);
                    if (null != view) {
                        ViewDetails viewDetails = new ViewDetails();
                        viewDetails.viewX = view.getX();
                        viewDetails.viewY = view.getY();
                        viewDetails.viewWidth = view.getWidth();
                        viewDetails.viewHeight = view.getHeight();
                        mViewsOnActivity.add(viewDetails);
                    }
                }
                cursor.moveToNext();
            }
        }
    }

    public void startAuthoring() {
        if(null==mActivity)
            return;
        Intent intent = new Intent(mActivity,AuthoringService.class);
        mActivity.startService(intent);
        fillViewList();
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle(mActivity.getResources().getString(R.string.step1_title));
        builder.setPositiveButton(mActivity.getResources().getString(R.string.step1_next),
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                View view =  mActivity.getLayoutInflater().inflate( R.layout.step1spinner, null );
                Spinner spinner = (Spinner)view.findViewById(R.id.step1type);
                mOption = spinner.getSelectedItem().toString();
                if((mOption.equals(TOUR)) ||(mOption.equals(TIP)))
                    showStep2Dialog();
                else{
                    //TODO: modals
                }

            }
        });
        builder.setNegativeButton(mActivity.getResources().getString(R.string.step1_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.e("Anurag","Cancel pressed");
                if(mStepNumber!=0){
                    mStepNumber--;
                }
            }
        });
        View view = mActivity.getLayoutInflater().inflate( R.layout.step1spinner, null );
        builder.setView(view);
        builder.show();
    }
}
