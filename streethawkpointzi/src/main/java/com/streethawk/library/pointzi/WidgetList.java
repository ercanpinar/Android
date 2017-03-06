package com.streethawk.library.pointzi;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Spinner;

import com.streethawk.library.core.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Class collects widgetlist for a given activity
 */
public class WidgetList implements Constants {

    private ArrayList<WidgetObject> mWidgetListForSaving;
    final String ID = "id";

    private final String LIST_VIEW = "ListView";
    private final String SPINNER = "Spinner";
    private final String GRIDVIEW = "GridView";
    private final String GENERIC = "generic";
    private final String UNKNOWN = "UNKNOWN";


    private final String TEXTID = "id";
    private final String PARENT = "parent";
    private final String TYPE = "type";
    private final String STARTX = "x";
    private final String STARTY = "y";
    private final String WIDTH = "width";
    private final String HEIGHT = "height";

    /*Meta*/
    private final String DEVICE_NAME = "device";
    private final String VIEW = "view";
    private final String ORIENTATION = "orientation";
    private final String SCREEN_X = "x";
    private final String SCREEN_Y = "y";
    private final String APP_VERSION = "version";
    private final String OS = "os";
    private final String ANDROID = "android";

    private final String JSON_PARAM_META = "meta";
    private final String JSON_PARAM_WIDGET = "widget";
    private final String ORIENTATION_PORTRAIT = "portrait";
    private final String ORIENTATION_LANDSCAPE = "landscape";


    /**
     * Function converts the widgetlist into JSONArray string for sending to server
     *
     * @return
     */
    private String convertArrayListToJSON() {
        if (null == mWidgetListForSaving) {
            return null;
        } else {
            try {
                JSONArray array = new JSONArray();
                for (WidgetObject widget : mWidgetListForSaving) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(TEXTID, widget.getTextID());
                    jsonObject.put(PARENT, widget.getParentViewName());
                    jsonObject.put(TYPE, widget.getType());
                    jsonObject.put(STARTX, widget.getStartX());
                    jsonObject.put(STARTY, widget.getStartY());
                    jsonObject.put(WIDTH, widget.getWidth());
                    jsonObject.put(HEIGHT, widget.getHeight());
                    array.put(jsonObject);
                }
                return array.toString();
            } catch (JSONException e) {
                return null;
            }
        }
    }


    private String getMeta(Activity activity) {
        if (null == activity)
            return null;

        JSONObject meta = new JSONObject();
        try {
            int orientation_code = activity.getResources().getConfiguration().orientation;
            String orientation = null;
            switch (orientation_code) {
                case Configuration.ORIENTATION_PORTRAIT:
                    orientation = ORIENTATION_PORTRAIT;
                    break;
                case Configuration.ORIENTATION_LANDSCAPE:
                    orientation = ORIENTATION_LANDSCAPE;
                    break;
                default:
                    orientation = UNKNOWN;
                    break;
            }

            meta.put(DEVICE_NAME, getDeviceName());
            meta.put(VIEW, getViewName(activity.getClass().getName()));
            meta.put(ORIENTATION, orientation);
            Context context = activity.getApplicationContext();
            int[] dimensions = getScreenDimensions(context);
            meta.put(SCREEN_X, dimensions[0]);
            meta.put(SCREEN_Y, dimensions[1]);
            meta.put(APP_VERSION, getAppVersionName(context));
            meta.put(OS, ANDROID);
            return meta.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Returns names of the views which can have child views
     *
     * @param view
     * @return
     */
    private String getViewType(View view) {
        if (view instanceof ListView)
            return LIST_VIEW;
        if (view instanceof Spinner)
            return SPINNER;
        if (view instanceof GridView)
            return GRIDVIEW;
        return GENERIC;
    }

    /**
     * Returns screen dimensions of the device
     *
     * @param context
     * @return
     */
    private int[] getScreenDimensions(Context context) {
        final WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        int[] dimensions = new int[2];
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            dimensions[0] = display.getWidth();
            dimensions[1] = display.getHeight();

        } else {
            try {
                Point size = new Point();
                display.getRealSize(size);
                dimensions[0] = size.x;
                dimensions[1] = size.y;
            } catch (Exception e) {
                dimensions[0] = 0;
                dimensions[1] = 0;
            }
        }
        return dimensions;
    }

    /**
     * Function to get version name of application
     *
     * @param context
     * @return version name
     */
    private String getAppVersionName(Context context) {
        try {
            String app_version = null;
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            app_version = packageInfo.versionName;
            if (null == app_version) {
                Log.e(TAG, "Application's version name is missing in AndroidManifest.xml");
                return UNKNOWN;
            }
            if (app_version.isEmpty())
                Log.e(TAG, "Application's version name is empty in AndroidManifest.xml");
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Exception in getAppVersionName");
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * Returns model String
     *
     * @return
     */
    private String getDeviceName() {
        String modelStr = "(" + Build.MANUFACTURER + ") " + Build.BRAND + " " + Build.MODEL;
        if (modelStr.length() >= 64) {
            Log.w(Util.TAG, "Resizing modelname to fit 64 chars");
            modelStr = modelStr.substring(0, 63);   // clip model name to 64 chars
        }
        return modelStr;
    }


    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    protected void fillViewList(Activity activity) {
        if (null == activity) {
            return;
        } else {
            mWidgetListForSaving = new ArrayList<>();
            ViewGroup rootView = (ViewGroup) activity.findViewById(android.R.id.content).getRootView();
            fillViewList(activity, rootView);
        }
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

    protected void displayWidgetListForSaving() {
        if (mWidgetListForSaving != null) {
            for (WidgetObject obj : mWidgetListForSaving) {
                obj.displayMyData("Anurag");
            }
        }
    }

    /**
     * Function returns identifier of the widget by stripping package name from it
     *
     * @param fullyQualifiedName
     * @return
     */
    private String getTextID(String fullyQualifiedName) {
        String className = new StringBuilder(fullyQualifiedName).reverse().toString();
        int indexOfPeriod = className.indexOf("di:");
        if (-1 != indexOfPeriod) {
            className = className.subSequence(0, className.indexOf("di:")).toString();
            className = new StringBuilder(className).reverse().toString();
            className = className.substring(1);
            return className;
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    private void fillViewList(Activity activity, View view) {
        if (null == view)
            return;
        int id = view.getId();
        if (-1 != id) {
            String viewTextID = null;
            Resources res = view.getResources();
            if (null == res)
                return;
            viewTextID = res.getResourceName(id);
            WidgetObject obj = new WidgetObject();
            String viewName = getViewName(activity.getClass().getName());
            String widgetID = getTextID(viewTextID);
            obj.setParentViewName(viewName);
            obj.setTextID(widgetID);
            obj.setResID(id);
            obj.setType(getViewType(view));
            obj.setWidth(view.getWidth());
            obj.setHeight(view.getHeight());
            obj.setStartX(view.getX());
            obj.setStartY(view.getY());

            mWidgetListForSaving.add(obj);
        }
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            int childCount = viewGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                fillViewList(activity, viewGroup.getChildAt(i));
            }
        }
    }
}
