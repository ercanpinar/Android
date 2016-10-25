package com.streethawk.library.feeds;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.HashSet;


public class TrigerDB  implements Constants {

    public class TriggerDBHelper extends SHSqliteBase{
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
    public void storeTriggerData(SHTriger object) throws SQLiteConstraintException{
        ContentValues values = new ContentValues();
        values.put(TriggerDBHelper.COLUMN_FEED_ID, object.getFeedID());
        values.put(TriggerDBHelper.COLUMN_SETUP, object.getSetup());
        values.put(TriggerDBHelper.COLUMN_VIEW, object.getView());
        values.put(TriggerDBHelper.COLUMN_TOOL, object.getTool());
        values.put(TriggerDBHelper.COLUMN_ACTIONED,object.getActioned());
        values.put(TriggerDBHelper.COLUMN_JSON,object.getJSON());
        mDatabase.insert(TriggerDBHelper.TRIGGER_TABLE_NAME, null, values);
    }

    /**
     * Call forceDeleteAllRecords when app status states to reset data
     */
    public void forceDeleteAllRecords(){
        mDatabase.execSQL("delete from " + TriggerDBHelper.TRIGGER_TABLE_NAME);
    }

    public void getTrigerForParsing(String viewName,HashSet<SHTriger> result){
        getTrigerForView(viewName,MODAL,TRIGGER_ON_PAGE_OPEN,result);
        if(result.isEmpty()){
            getTrigerForView(viewName,TOUR,TRIGGER_ON_PAGE_OPEN,result);
        }
        if(result.isEmpty()){
            getTrigerForView(viewName,TIP,TRIGGER_ON_PAGE_OPEN,result);
        }
        getTrigerForView(viewName,MODAL,null,result);
        if(result.isEmpty()){
            getTrigerForView(viewName,TOUR,null,result);
        }
        if(result.isEmpty()){
            getTrigerForView(viewName,TIP,null,result);
        }
    }


    public void updateActionedFlag(String feed_id,int flag){
        /*
        if(null==feed_id){
            Log.e("Anurag","Feedid is null returning");
            return;
        }
        TriggerDBHelper helper = new TriggerDBHelper(mContext);
        SQLiteDatabase database = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TriggerDBHelper.COLUMN_ACTIONED,flag);
        database.update( TriggerDBHelper.TRIGGER_TABLE_NAME, values,TriggerDBHelper.COLUMN_FEED_ID + " = ? ",new String[]{ String.valueOf(feed_id)});
        */
    }

    public void getTrigerForView(String viewName,String type,String trigger,HashSet<SHTriger> result) {
        TriggerDBHelper helper = new TriggerDBHelper(mContext);
        SQLiteDatabase database = helper.getReadableDatabase();
        if (null == viewName) {
            database.close();
            helper.close();
            return;
        } else {
            String query=null;
            if(null!=trigger){
                query = "select * from " + TriggerDBHelper.TRIGGER_TABLE_NAME +
                        " where " + TriggerDBHelper.COLUMN_VIEW + " = '" + viewName
                        + "' and " + TriggerDBHelper.COLUMN_TRIGGER + " = '" +trigger
                        + "' and " + TriggerDBHelper.COLUMN_ACTIONED + " = 0"
                        + " and " + TriggerDBHelper.COLUMN_TOOL + " = '" + type + "'";
            }
            else {
                query = "select * from " + TriggerDBHelper.TRIGGER_TABLE_NAME +
                        " where " + TriggerDBHelper.COLUMN_VIEW + " = '" + viewName
                        + "' and " + TriggerDBHelper.COLUMN_ACTIONED + " = 0"
                        + " and " + TriggerDBHelper.COLUMN_TOOL + " = '" + type + "'";
            }
            Cursor cursor = database.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                while(!cursor.isAfterLast()) {
                    SHTriger obj = new SHTriger();
                    obj.setFeedID(cursor.getString(cursor.getColumnIndex(TriggerDBHelper.COLUMN_FEED_ID)));
                    obj.setSetup(cursor.getString(cursor.getColumnIndex(TriggerDBHelper.COLUMN_SETUP)));
                    obj.setView(cursor.getString(cursor.getColumnIndex(TriggerDBHelper.COLUMN_VIEW)));
                    obj.setTool(cursor.getString(cursor.getColumnIndex(TriggerDBHelper.COLUMN_TOOL)));
                    obj.setJSON(cursor.getString(cursor.getColumnIndex(TriggerDBHelper.COLUMN_JSON)));
                    obj.setTrigger(cursor.getString(cursor.getColumnIndex(TriggerDBHelper.COLUMN_TRIGGER)));
                    obj.setActioned(cursor.getInt(cursor.getColumnIndex(TriggerDBHelper.COLUMN_ACTIONED)));
                    result.add(obj);
                    cursor.moveToNext();
                }
            }else {
                result = null;
            }
            cursor.close();
            database.close();
            helper.close();
        }
        return;
    }
}
