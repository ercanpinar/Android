package com.streethawk.library.feeds;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.streethawk.library.core.WidgetDBHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

/**
 * Class for tracking changes in activity refelcted from core module
 */
public class TrigerActivityTracker implements Constants {

    private static TrigerActivityTracker instance;
    private Activity mActivity;

    private TrigerActivityTracker() {
    }

    public static TrigerActivityTracker getInstance() {
        if (null == instance) {
            instance = new TrigerActivityTracker();
        }
        return instance;
    }


    private class displayTriggerAsyncTask extends AsyncTask<HashSet<SHTriger>, Void, Void> {
        private HashSet<SHTriger> objSet;

        @Override
        protected Void doInBackground(HashSet<SHTriger>... shTrigers) {
            objSet = shTrigers[0];
            return null;
        }

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

        private int getResIdFromWidgetName(Activity activity, String widgetName) {
            if (null == widgetName)
                return -1;
            WidgetDBHelper helper = new WidgetDBHelper(activity.getApplicationContext());
            SQLiteDatabase database = helper.getReadableDatabase();

            String parent = getViewName(activity.getClass().getName());
            String WHERE = " where ";
            String EQUALS = " = ";
            String AND = " and ";
            String DOUBLE_QUOTE = "\"";

            String query = "select * from " + WidgetDBHelper.TOOLTIP_TABLE_NAME +
                    WHERE + WidgetDBHelper.COLUMN_TEXT_ID + EQUALS + DOUBLE_QUOTE + widgetName.trim() + DOUBLE_QUOTE +
                    AND + WidgetDBHelper.COLUMN_PARENT_VIEW + EQUALS + DOUBLE_QUOTE + parent.trim() + DOUBLE_QUOTE;
            try {
                Cursor cursor = database.rawQuery(query, null);
                if (cursor != null && cursor.moveToFirst()) {
                    return cursor.getInt(cursor.getColumnIndex(WidgetDBHelper.COLUMN_RES_ID));
                } else {
                    cursor.close();
                    database.close();
                    helper.close();
                    return -1;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                database.close();
                helper.close();
                return -1;
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (null == objSet) {
                return;
            }

            for (SHTriger obj : objSet) {
                /**
                 * Logic: if trigger is for page open, then show and break
                 * else for others display all the triggers
                 */
                switch (obj.getTool()) {
                    case MODAL:

                        Modal modal = new Modal();
                        modal.registerClickListener(PointiziActionListener.getInstance(mActivity));
                        modal.showModal(mActivity, obj);
                        if (obj.getTrigger() == TRIGGER_ON_PAGE_OPEN) {
                            return;
                        }
                        break;
                    case TOUR:
                        SHTours tours = new SHTours(mActivity);
                        tours.registerClickListener(PointiziActionListener.getInstance(mActivity));
                        tours.startTour(obj);
                        if (obj.getTrigger() == TRIGGER_ON_PAGE_OPEN) {
                            return;
                        }
                        break;
                    case TIP:
                        SHTips tips = new SHTips();
                        tips.registerClickListener(PointiziActionListener.getInstance(mActivity));
                        tips.showTip(mActivity, obj, true);
                        break;
                    default:
                        break;
                }

            }
        }
    }  //End of AsyncTaskClass

    private void displayTriggerInCurrentView(final Activity activity, final HashSet<SHTriger> obj) {
        mActivity = activity;
        displayTriggerAsyncTask task = new displayTriggerAsyncTask();
        task.execute(obj);
    }

    public void onOrientationChange(Activity activity) {
        //TODO
    }

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

    public void onEnteringNewActivity(Activity activity) {
        final String viewName = getViewName(activity.getClass().getName());
        HashSet<SHTriger> triger = new HashSet<>();
        TrigerDB tdb = new TrigerDB(activity.getApplicationContext());
        tdb.getTrigerForParsing(viewName, triger);
        if (null != triger) {
            if (!triger.isEmpty())
                Log.e("Anurag", "OnEnteringActivity 2");
            displayTriggerInCurrentView(activity, triger);
            return;
        }
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


}
