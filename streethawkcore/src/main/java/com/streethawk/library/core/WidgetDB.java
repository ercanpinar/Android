package com.streethawk.library.core;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Database and database helper function for widgets
 */
public class WidgetDB implements Constants {

    public class WidgetDBHelper extends SHSqliteBase {
        public WidgetDBHelper(Context context) {
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
    private WidgetDBHelper mDbHelper;
    private Context mContext;
    public WidgetDB(Context context) {
        this.mContext = context;
        mDbHelper = new WidgetDBHelper(context);
    }
    public void open() throws SQLException {
        mDatabase = mDbHelper.getWritableDatabase();
    }
    public void close() {
        mDbHelper.close();
        mDatabase.close();
    }
    public void storeWidgetData(WidgetObject object) {
        ContentValues values = new ContentValues();
        values.put(WidgetDBHelper.COLUMN_TEXT_ID, object.getTextID());
        values.put(WidgetDBHelper.COLUMN_RES_ID, object.getResID());
        values.put(WidgetDBHelper.COLUMN_PARENT_VIEW, object.getParentViewName());
        mDatabase.insert(WidgetDBHelper.TOOLTIP_TABLE_NAME, null, values);
    }

    /**
     * Call forceDeleteAllRecords when app status states to reset data
     */
    public void forceDeleteAllRecords(){
        mDatabase.execSQL("delete from " + WidgetDBHelper.TOOLTIP_TABLE_NAME);
    }

/*
    public WidgetObject getWidgetObject(ToolTipObject tt){
        if(null==tt){
            return null;
        }
        String viewName = tt.getView();
        String widget = tt.getWidget();

        if(null==viewName) {
            return null;
        }
        if(null==widget) {
            return null;
        }
        if(viewName.isEmpty()) {
            return null;
        }
        if(widget.isEmpty()) {
            return null;
        }
        WidgetObject wb = new WidgetObject();
        WidgetDBHelper helper = new WidgetDBHelper(mContext);
        SQLiteDatabase database = helper.getReadableDatabase();

        String query = "select * from " + WidgetDBHelper.TOOLTIP_TABLE_NAME +
                WHERE + WidgetDBHelper.COLUMN_TEXT_ID + EQUALS + DOUBLE_QUOTE + widget.trim() + DOUBLE_QUOTE +
                AND  + WidgetDBHelper.COLUMN_PARENT_VIEW + EQUALS +DOUBLE_QUOTE + viewName.trim() + DOUBLE_QUOTE;
        try {
            Cursor cursor = database.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                wb.setTextID(widget);
                wb.setParentViewName(viewName);
                wb.setX(cursor.getInt(cursor.getColumnIndex(WidgetDBHelper.COLUMN_X)));
                wb.setY(cursor.getInt(cursor.getColumnIndex(WidgetDBHelper.COLUMN_Y)));
                wb.setResID(cursor.getInt(cursor.getColumnIndex(WidgetDBHelper.COLUMN_RES_ID)));
                return wb;
            }else{
                cursor.close();
                database.close();
                helper.close();
                return null;
            }
        }catch(SQLException e){
            e.printStackTrace();
            database.close();
            helper.close();
            return null;
        }
    }
    */
}
