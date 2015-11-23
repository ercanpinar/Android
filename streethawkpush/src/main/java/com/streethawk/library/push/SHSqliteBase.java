package com.streethawk.library.push;


import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.streethawk.library.core.Util;

public abstract class SHSqliteBase extends SQLiteOpenHelper {

    private static final String STREETHAWK_DATABASE  = "streethawk.db";
    private static final int STREETHAWK_DATABASE_VERSION = 2;
    public static final String PUSH_NOTIFICATION_TABLE_NAME = "pushnotification";
    private final String KEY_IBEACON = "shKeyIBeacon";
    private final String KEY_GEOFENCE = "shKeyGeofenceList";
    private Context mContext;

    protected static final String COLUMN_MSGID = "MsgID";
    protected static final String COLUMN_CODE = "code";
    protected static final String COLUMN_TITLE = "title";
    protected static final String COLUMN_MSG = "msg";
    protected static final String COLUMN_DATA = "data";
    protected static final String COLUMN_P = "p";
    protected static final String COLUMN_O = "o";
    protected static final String COLUMN_S = "s";
    protected static final String COLUMN_N = "n";
    protected static final String COLUMN_SOUND = "sound";
    protected static final String COLUMN_BADGE = "badge";

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

    protected static final String BEACON_TABLE_NAME    = "beacondata";
    protected static final String COLUMN_BEACONID      = "beaconid";
    protected static final String COLUMN_UUID          = "uuid";
    protected static final String COLUMN_MAJOR_NUMBER  = "majorno";
    protected static final String COLUMN_MINOR_NUMBER  = "minorno";


    protected static final String GEOFENCE_TABLE_NAME  = "geofence";
    protected static final String COLUMN_GEOFENCEID    = "id";
    protected static final String COLUMN_LATITUDE      = "latitude";
    protected static final String COLUMN_LONGITUDE     = "longitude";
    protected static final String COLUMN_RADIUS        = "radius";
    protected static final String COLUMN_PARENT        = "parent";
    protected static final String COLUMN_NODE          = "geofences";


    private final String PUSH_NOTIFICATION_DATABASE_CREATE = "create table "
            + PUSH_NOTIFICATION_TABLE_NAME + "(" + COLUMN_MSGID
            + TEXT + PRIMARY_KEY + UNIQUE + COMA
            + COLUMN_CODE + TEXT + COMA
            + COLUMN_TITLE + TEXT + COMA
            + COLUMN_MSG + TEXT + COMA
            + COLUMN_DATA + TEXT + COMA
            + COLUMN_P + TEXT + COMA
            + COLUMN_O + TEXT + COMA
            + COLUMN_S + TEXT + COMA
            + COLUMN_N + TEXT + COMA
            + COLUMN_SOUND + TEXT + COMA
            + COLUMN_BADGE + TEXT
            + ")";

    private final String BEACON_DATABASE_CREATE = "create table "
            + BEACON_TABLE_NAME + "(" + COLUMN_BEACONID
            + TEXT + PRIMARY_KEY + UNIQUE + COMA
            + COLUMN_UUID + TEXT + NOT+NULL+COMA
            + COLUMN_MAJOR_NUMBER + INTEGER +NOT+NULL + COMA
            + COLUMN_MINOR_NUMBER + INTEGER +NOT+NULL
            + ")";

    private final String GEOFENCE_DATABASE_CREATE = "create table "
            + GEOFENCE_TABLE_NAME + "(" + COLUMN_GEOFENCEID
            + TEXT + PRIMARY_KEY + UNIQUE + COMA
            + COLUMN_LATITUDE + TEXT + COMA
            + COLUMN_LONGITUDE + TEXT + COMA
            + COLUMN_RADIUS + TEXT + COMA
            + COLUMN_PARENT + TEXT + COMA
            + COLUMN_NODE + TEXT
            + ")";

    public SHSqliteBase(Context context) {
        super(context, STREETHAWK_DATABASE, null, STREETHAWK_DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public  void onCreate(SQLiteDatabase database){
        database.execSQL(PUSH_NOTIFICATION_DATABASE_CREATE);
        database.execSQL(BEACON_DATABASE_CREATE);
        database.execSQL(GEOFENCE_DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + PUSH_NOTIFICATION_TABLE_NAME);
        database.execSQL("DROP TABLE IF EXISTS " + BEACON_DATABASE_CREATE);
        database.execSQL("DROP TABLE IF EXISTS " + GEOFENCE_TABLE_NAME);
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = sharedPreferences.edit();
        e.putString(KEY_IBEACON, null);
        e.putString(KEY_GEOFENCE, null);
        e.commit();
        onCreate(database);
    }

}