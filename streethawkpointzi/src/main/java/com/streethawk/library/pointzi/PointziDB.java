package com.streethawk.library.pointzi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by anuragkondeya on 31/10/16.
 */

public class PointziDB extends SQLiteOpenHelper implements Constants{


    private static final String STREETHAWK_DATABASE = "streethawk_feeds.db";
    private static final int STREETHAWK_DATABASE_VERSION = 2;

    private Context mContext;
    private SQLiteDatabase mDatabase;


    public static final String TRIGGER_TABLE_NAME = "table_trigger";
    protected final String PRIMARY_KEY = "primary key ";
    protected final String INTEGER = " integer ";
    protected final String REAL = " real ";
    protected final String TEXT = " text ";
    protected final String BLOB = " blob ";
    protected final String NOT = " not ";
    protected final String NULL = " null ";
    protected final String UNIQUE = " unique ";
    protected final String COMA = ", ";
    protected final String SINGLE_QUOTE = "'";


    protected final static String COLUMN_FEED_ID        = "feed_id";
    protected final static String COLUMN_TOOL           = "tool";
    protected final static String COLUMN_SETUP          = "setup";
    protected final static String COLUMN_VIEW           = "view";
    protected final static String COLUMN_ACTIONED       = "actioned";
    protected final static String COLUMN_JSON           = "rawjson";
    protected final static String COLUMN_TRIGGER        = "trigger";
    protected final static String COLUMN_TYPE           = "type";
    protected final static String COLUMN_TARGET         = "target";
    protected final static String COLUMN_LAUNCHER_JSON  = "launcher_json";


    final String TRIGGER_TABLE_CREATE = "create table "
            + TRIGGER_TABLE_NAME + "(" + COLUMN_FEED_ID
            + TEXT + PRIMARY_KEY + UNIQUE + COMA
            + COLUMN_VIEW + TEXT + COMA
            + COLUMN_TOOL + TEXT + COMA
            + COLUMN_JSON + TEXT + COMA
            + COLUMN_TRIGGER + TEXT + COMA
            + COLUMN_ACTIONED + INTEGER + COMA
            + COLUMN_SETUP + TEXT + COMA
            + COLUMN_TARGET + TEXT + COMA
            + COLUMN_LAUNCHER_JSON + TEXT + COMA
            + COLUMN_TYPE + TEXT
            + ")";

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(TRIGGER_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int old, int newId) {
        database.execSQL("DROP TABLE IF EXISTS " + TRIGGER_TABLE_NAME);
        onCreate(database);
    }

    public PointziDB(Context context) {
        super(context, STREETHAWK_DATABASE, null, STREETHAWK_DATABASE_VERSION);
        this.mContext = context;
    }
    public void open() throws SQLException {
        mDatabase = this.getWritableDatabase();
    }
    public void close() {
        mDatabase.close();
    }

    public void storeTriggerData(Trigger object) throws SQLiteConstraintException {
        ContentValues values = new ContentValues();
        values.put(COLUMN_FEED_ID, object.getFeedID());
        values.put(COLUMN_SETUP, object.getSetup());
        values.put(COLUMN_VIEW, object.getView());
        values.put(COLUMN_TOOL, object.getTool());
        values.put(COLUMN_ACTIONED,object.getActioned());
        values.put(COLUMN_JSON,object.getJSON());
        values.put(COLUMN_TRIGGER,object.getTrigger());
        values.put(COLUMN_TYPE,object.getTriggerType());
        values.put(COLUMN_TARGET,object.getTarget());
        values.put(COLUMN_LAUNCHER_JSON,object.getLauncherJSON());
        mDatabase.insertWithOnConflict(TRIGGER_TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public void forceDeleteAllRecords(){
        mDatabase.execSQL("delete from " + TRIGGER_TABLE_NAME);
    }

    public void updateActionedFlag(String feed_id,int flag){

        if(null==feed_id){
            Log.e("Anurag","Feedid is null returning");
            return;
        }
        PointziDB helper = new PointziDB(mContext);
        SQLiteDatabase database = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PointziDB.COLUMN_ACTIONED,flag);
        int result = database.update( PointziDB.TRIGGER_TABLE_NAME, values,PointziDB.COLUMN_FEED_ID+"="+feed_id,null);
        Log.e("Anurag","Update result "+result);
    }

    public void getTriggerForView(String viewName,String trigger,int action,ArrayList<Trigger> result) {
        SQLiteDatabase database = this.getReadableDatabase();
        if (null == viewName) {
            database.close();
            return;
        } else {
            String query=null;
            if(null!=trigger){
                query = "select * from " + TRIGGER_TABLE_NAME +
                        " where " + COLUMN_VIEW + " = '" + viewName
                        + "' and " + COLUMN_TYPE + " = '" +trigger
                        + "' and " + COLUMN_ACTIONED + " = "+action;
            }else{
                query = "select * from " + TRIGGER_TABLE_NAME +
                        " where " + COLUMN_VIEW + " = '" + viewName
                        + "' and " + COLUMN_ACTIONED + " = "+action;
            }
            Log.e("Anurag","Query "+query);
            Cursor cursor = database.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                while(!cursor.isAfterLast()) {
                    Trigger obj = new Trigger();
                    obj.setFeedID(cursor.getString(cursor.getColumnIndex(COLUMN_FEED_ID)));
                    obj.setSetup(cursor.getString(cursor.getColumnIndex(COLUMN_SETUP)));
                    obj.setView(cursor.getString(cursor.getColumnIndex(COLUMN_VIEW)));
                    obj.setTool(cursor.getString(cursor.getColumnIndex(COLUMN_TOOL)));
                    obj.setJSON(cursor.getString(cursor.getColumnIndex(COLUMN_JSON)));
                    obj.setTrigger(cursor.getString(cursor.getColumnIndex(COLUMN_TRIGGER)));
                    obj.setActioned(cursor.getInt(cursor.getColumnIndex(COLUMN_ACTIONED)));
                    obj.setTriggerType(cursor.getString(cursor.getColumnIndex(COLUMN_TYPE)));
                    obj.setTarget(cursor.getString(cursor.getColumnIndex(COLUMN_TARGET)));
                    obj.setLauncherJSON(cursor.getString(cursor.getColumnIndex(COLUMN_LAUNCHER_JSON)));
                    result.add(obj);
                    cursor.moveToNext();
                }
            }else {
                result = null;
            }
            cursor.close();
            database.close();
        }
        return;
    }
}
