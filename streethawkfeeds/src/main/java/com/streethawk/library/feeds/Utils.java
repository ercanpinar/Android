package com.streethawk.library.feeds;

import android.app.Activity;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.streethawk.library.core.WidgetDB;

/**
 * Contains utility class
 */
class Utils {


    /**
     * Function returns viewname by stripping package name from it
     *
     * @param fullyQualifiedName
     * @return
     */
    protected String getViewName(String fullyQualifiedName) {
        String className = new StringBuilder(fullyQualifiedName).reverse().toString();
        int indexOfPeriod = className.indexOf(".");
        if (-1 != indexOfPeriod) {
            className = className.subSequence(0, className.indexOf(".")).toString();
            className = new StringBuilder(className).reverse().toString();
            return className;
        }
        return null;
    }

    /**
     * Function returns widgetID from the widgetName provided.
     * TODO: Write a function to store widget name
     *
     * @param widgetName
     * @return
     */
    protected int getResIdFromWidgetName(Activity activity, String widgetName) {
        if (null == widgetName)
            return -1;
        WidgetDB.WidgetDBHelper helper = new WidgetDB(activity).new WidgetDBHelper(activity.getApplicationContext());
        SQLiteDatabase database = helper.getReadableDatabase();

        String parent = getViewName(activity.getClass().getName());
        String WHERE = " where ";
        String EQUALS = " = ";
        String AND = " and ";
        String DOUBLE_QUOTE = "\"";

        String query = "select * from " + WidgetDB.WidgetDBHelper.TOOLTIP_TABLE_NAME +
                WHERE + WidgetDB.WidgetDBHelper.COLUMN_TEXT_ID + EQUALS + DOUBLE_QUOTE + widgetName.trim() + DOUBLE_QUOTE +
                AND + WidgetDB.WidgetDBHelper.COLUMN_PARENT_VIEW + EQUALS + DOUBLE_QUOTE + parent.trim() + DOUBLE_QUOTE;
        try {
            Cursor cursor = database.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getInt(cursor.getColumnIndex(WidgetDB.WidgetDBHelper.COLUMN_RES_ID));
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

}
