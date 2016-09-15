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
}
