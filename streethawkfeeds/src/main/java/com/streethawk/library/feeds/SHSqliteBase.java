package com.streethawk.library.feeds;


import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.streethawk.library.core.Util;

public abstract class SHSqliteBase extends SQLiteOpenHelper {

    private static final String STREETHAWK_DATABASE  = "streethawk_feeds.db";
    private static final int STREETHAWK_DATABASE_VERSION = 1;

    private Context mContext;

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

    protected final static String COLUMN_TOOL_ID           = "id";
    protected final static String COLUMN_DISPLAY           = "display";
    protected final static String COLUMN_TRIGER            = "trigger";
    protected final static String COLUMN_TARGET            = "target";
    protected final static String COLUMN_VIEW              = "view";
    protected final static String COLUMN_DELAY             = "delay";
    protected final static String COLUMN_TOOL              = "tool";
    protected final static String COLUMN_WIDGET_TYPE       = "widgettype";
    protected final static String COLUMN_WIDGET_LABEL      = "widgetlabel";
    protected final static String COLUMN_WIDGET_CSS        = "widgetcss";
    protected final static String COLUMN_WIDGET_BGCOLOR    = "widgetbgcolor";
    protected final static String COLUMN_WIDGET_PLACEMENT  = "widgetplacement";



    final String TRIGGER_TABLE_CREATE = "create table "
            + TRIGGER_TABLE_NAME + "(" + COLUMN_TOOL_ID
            + TEXT + PRIMARY_KEY + UNIQUE + COMA
            + COLUMN_DISPLAY + TEXT + COMA
            + COLUMN_TRIGER + TEXT + COMA
            + COLUMN_TARGET + TEXT + COMA
            + COLUMN_VIEW + TEXT + COMA
            + COLUMN_DELAY + INTEGER + COMA
            + COLUMN_TOOL + TEXT + COMA
            + COLUMN_WIDGET_TYPE + TEXT + COMA
            + COLUMN_WIDGET_LABEL + TEXT + COMA
            + COLUMN_WIDGET_CSS + TEXT + COMA
            + COLUMN_WIDGET_BGCOLOR + TEXT +COMA
            + COLUMN_WIDGET_PLACEMENT + TEXT
            + ")";

    public SHSqliteBase(Context context) {
        super(context, STREETHAWK_DATABASE, null, STREETHAWK_DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public  void onCreate(SQLiteDatabase database){
        database.execSQL(TRIGGER_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + TRIGGER_TABLE_CREATE);
        onCreate(database);
    }
}