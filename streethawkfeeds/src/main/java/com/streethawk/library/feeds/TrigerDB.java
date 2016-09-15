package com.streethawk.library.feeds;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


public class TrigerDB {

    public class TriggerDBHelper extends SHSqliteBase {
        public TriggerDBHelper(Context context) {
            super(context);
        }

        @Override
        public void onCreate(SQLiteDatabase database) {
            super.onCreate(database);
        }
        @Override
        public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
            super.onUpgrade(database,oldVersion,newVersion);
        }
    }// End class push notification helper

    private SQLiteDatabase mDatabase;
    private TriggerDBHelper mDbHelper;
    private Context mContext;
    public TrigerDB(Context context) {
        this.mContext = context;
        mDbHelper = new TriggerDBHelper(context);
    }
    public void open() throws SQLException {
        mDatabase = mDbHelper.getWritableDatabase();
    }
    public void close() {
        mDbHelper.close();
        mDatabase.close();
    }

    /**
     * Store trigger data in db
     * @param object
     */
    public void storeTriggerData(SHTriger object) {
        ContentValues values = new ContentValues();
        values.put(TriggerDBHelper.COLUMN_TOOL_ID, object.getToolId());
        values.put(TriggerDBHelper.COLUMN_DISPLAY, object.getDisplay());
        values.put(TriggerDBHelper.COLUMN_TRIGER, object.getTriger());
        values.put(TriggerDBHelper.COLUMN_TARGET, object.getTarget());
        values.put(TriggerDBHelper.COLUMN_VIEW, object.getView());
        values.put(TriggerDBHelper.COLUMN_DELAY, object.getDelay());
        values.put(TriggerDBHelper.COLUMN_TOOL, object.getTool());
        values.put(TriggerDBHelper.COLUMN_WIDGET_TYPE, object.getWidgetType());
        values.put(TriggerDBHelper.COLUMN_WIDGET_LABEL, object.getWidgetLabel());
        values.put(TriggerDBHelper.COLUMN_WIDGET_CSS, object.getWidgetCss());
        values.put(TriggerDBHelper.COLUMN_WIDGET_BGCOLOR, object.getBGColor());
        values.put(TriggerDBHelper.COLUMN_WIDGET_PLACEMENT, object.getPlacement());
        mDatabase.insert(TriggerDBHelper.TRIGGER_TABLE_NAME, null, values);
    }

    /**
     * Call forceDeleteAllRecords when app status states to reset data
     */
    public void forceDeleteAllRecords(){
        mDatabase.execSQL("delete from " + TriggerDBHelper.TRIGGER_TABLE_NAME);
    }

    public void getTrigerForView(String viewName,SHTriger result) {
        TriggerDBHelper helper = new TriggerDBHelper(mContext);
        SQLiteDatabase database = helper.getReadableDatabase();
        if (null == viewName) {
            database.close();
            helper.close();
            return;
        } else {
            String query = "select * from " + TriggerDBHelper.TRIGGER_TABLE_NAME +
                    " where " + TriggerDBHelper.COLUMN_VIEW + " = '" + viewName + "'";
            Cursor cursor = database.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                String Display = cursor.getString(cursor.getColumnIndex(TriggerDBHelper.COLUMN_DISPLAY));;
                String Trigger= cursor.getString(cursor.getColumnIndex(TriggerDBHelper.COLUMN_TRIGER));
                String Target= cursor.getString(cursor.getColumnIndex(TriggerDBHelper.COLUMN_TARGET));
                String View= cursor.getString(cursor.getColumnIndex(TriggerDBHelper.COLUMN_VIEW));
                int    Delay= cursor.getInt(cursor.getColumnIndex(TriggerDBHelper.COLUMN_DELAY));
                String Tool= cursor.getString(cursor.getColumnIndex(TriggerDBHelper.COLUMN_TOOL));
                String WidgetType= cursor.getString(cursor.getColumnIndex(TriggerDBHelper.COLUMN_WIDGET_TYPE));
                String WidgetLabel= cursor.getString(cursor.getColumnIndex(TriggerDBHelper.COLUMN_WIDGET_LABEL));
                String WidgetCss= cursor.getString(cursor.getColumnIndex(TriggerDBHelper.COLUMN_WIDGET_CSS));
                String bgColor= cursor.getString(cursor.getColumnIndex(TriggerDBHelper.COLUMN_WIDGET_BGCOLOR));
                String ToolId= cursor.getString(cursor.getColumnIndex(TriggerDBHelper.COLUMN_TOOL_ID));
                String placement = cursor.getString(cursor.getColumnIndex(TriggerDBHelper.COLUMN_WIDGET_PLACEMENT));
                cursor.close();
                database.close();
                helper.close();
                result.setDisplay(Display);
                result.setTriger(Trigger);
                result.setTarget(Target);
                result.setView(View);
                result.setDelay(Delay);
                result.setTool(Tool);
                result.setWidgetType(WidgetType);
                result.setWidgetLabel(WidgetLabel);
                result.setWidgetCss(WidgetCss);
                result.setBGColor(bgColor);
                result.setToolID(ToolId);
                result.setPlacement(placement);
            }else {
                result = null;
                cursor.close();
                database.close();
                helper.close();
                return;
            }
        }
        return;
    }
}
