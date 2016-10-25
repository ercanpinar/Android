package com.streethawk.library.feeds;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public abstract class SHSqliteBase extends SQLiteOpenHelper {

    private static final String STREETHAWK_DATABASE = "streethawk_feeds.db";
    private static final int STREETHAWK_DATABASE_VERSION = 2;

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

    protected final static String COLUMN_FEED_ID        = "feed_id";
    protected final static String COLUMN_TOOL           = "tool";
    protected final static String COLUMN_SETUP          = "setup";
    protected final static String COLUMN_VIEW           = "view";
    protected final static String COLUMN_ACTIONED       = "actioned";
    protected final static String COLUMN_JSON           = "rawjson";
    protected final static String COLUMN_TRIGGER        = "trigger";

    final String TRIGGER_TABLE_CREATE = "create table "
            + TRIGGER_TABLE_NAME + "(" + COLUMN_FEED_ID
            + TEXT + PRIMARY_KEY + UNIQUE + COMA
            + COLUMN_VIEW + TEXT + COMA
            + COLUMN_TOOL + TEXT + COMA
            + COLUMN_JSON + TEXT + COMA
            + COLUMN_TRIGGER + TEXT + COMA
            + COLUMN_ACTIONED + INTEGER + COMA
            + COLUMN_SETUP + TEXT
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
        database.execSQL("DROP TABLE IF EXISTS " + TRIGGER_TABLE_NAME);
        onCreate(database);
    }
}