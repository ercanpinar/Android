package com.streethawk.library.push;


import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.streethawk.library.core.Util;

public abstract class SHSqliteBase extends SQLiteOpenHelper {

    private static final String STREETHAWK_DATABASE = "streethawk.db";
    private static final int STREETHAWK_DATABASE_VERSION = 9;
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
    // Interactive push
    private static final String COLUMN_CONTENT_AVAILABLE = "contentavailable";
    private static final String COLUMN_CATEGORY = "category";

    /* Start custom button */
    private static final String COLOUMN_BT1Title = "btn1title";
    private static final String COLOUMN_BT2Title = "btn2title";
    private static final String COLOUMN_BT3Title = "btn3title";

    private static final String COLUMN_BT1ICON = "btn1icon";
    private static final String COLUMN_BT2ICON = "btn2icon";
    private static final String COLUMN_BT3ICON = "btn3icon";
    /* End custom  button*/


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

    protected static final String BEACON_TABLE_NAME = "beacondata";
    protected static final String COLUMN_BEACONID = "beaconid";
    protected static final String COLUMN_UUID = "uuid";
    protected static final String COLUMN_MAJOR_NUMBER = "majorno";
    protected static final String COLUMN_MINOR_NUMBER = "minorno";


    protected static final String GEOFENCE_TABLE_NAME = "geofence";
    protected static final String COLUMN_GEOFENCEID = "id";
    protected static final String COLUMN_LATITUDE = "latitude";
    protected static final String COLUMN_LONGITUDE = "longitude";
    protected static final String COLUMN_RADIUS = "radius";
    protected static final String COLUMN_PARENT = "parent";
    protected static final String COLUMN_NODE = "geofences";

    protected static final String BUTTON_PAIR_TABLE_NAME = "interactivepush";
    protected static final String COLUMN_BTNPAIRID = "id";
    protected static final String COLUMN_B1TITLE = "b1title";
    protected static final String COLUMN_B1ICON = "b1icon";
    protected static final String COLUMN_B2TITLE = "b2title";
    protected static final String COLUMN_B2ICON = "b2icon";
    protected static final String COLUMN_B3TITLE = "b3title";
    protected static final String COLUMN_B3ICON = "b3icon";

    public SHSqliteBase(Context context) {
        super(context, STREETHAWK_DATABASE, null, STREETHAWK_DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {

        final String PUSH_NOTIFICATION_DATABASE_CREATE = "create table "
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
                + COLUMN_BADGE + TEXT + COMA
                + COLUMN_CONTENT_AVAILABLE + TEXT + COMA
                + COLUMN_CATEGORY + TEXT + COMA
                + COLOUMN_BT1Title + TEXT + COMA
                + COLOUMN_BT2Title + TEXT + COMA
                + COLOUMN_BT3Title + TEXT + COMA
                + COLUMN_BT1ICON + INTEGER + COMA
                + COLUMN_BT2ICON + INTEGER + COMA
                + COLUMN_BT3ICON + INTEGER
                + ")";

        final String BEACON_DATABASE_CREATE = "create table "
                + BEACON_TABLE_NAME + "(" + COLUMN_BEACONID
                + TEXT + PRIMARY_KEY + UNIQUE + COMA
                + COLUMN_UUID + TEXT + NOT + NULL + COMA
                + COLUMN_MAJOR_NUMBER + INTEGER + NOT + NULL + COMA
                + COLUMN_MINOR_NUMBER + INTEGER + NOT + NULL
                + ")";

        final String GEOFENCE_DATABASE_CREATE = "create table "
                + GEOFENCE_TABLE_NAME + "(" + COLUMN_GEOFENCEID
                + TEXT + PRIMARY_KEY + UNIQUE + COMA
                + COLUMN_LATITUDE + TEXT + COMA
                + COLUMN_LONGITUDE + TEXT + COMA
                + COLUMN_RADIUS + TEXT + COMA
                + COLUMN_PARENT + TEXT + COMA
                + COLUMN_NODE + TEXT
                + ")";

        final String BTNPAIR_DATABASE_CREATE = "create table "
                + BUTTON_PAIR_TABLE_NAME + "(" + COLUMN_BTNPAIRID
                + TEXT + PRIMARY_KEY + UNIQUE + COMA
                + COLUMN_B1TITLE + TEXT + COMA
                + COLUMN_B1ICON + INTEGER + COMA
                + COLUMN_B2TITLE + TEXT + COMA
                + COLUMN_B2ICON + INTEGER + COMA
                + COLUMN_B3TITLE + TEXT + COMA
                + COLUMN_B3ICON + INTEGER
                + ")";
        database.execSQL(PUSH_NOTIFICATION_DATABASE_CREATE);
        database.execSQL(BEACON_DATABASE_CREATE);
        database.execSQL(GEOFENCE_DATABASE_CREATE);
        database.execSQL(BTNPAIR_DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + PUSH_NOTIFICATION_TABLE_NAME);
        database.execSQL("DROP TABLE IF EXISTS " + BEACON_TABLE_NAME);
        database.execSQL("DROP TABLE IF EXISTS " + GEOFENCE_TABLE_NAME);
        database.execSQL("DROP TABLE IF EXISTS " + BUTTON_PAIR_TABLE_NAME);

        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = sharedPreferences.edit();
        e.putString(KEY_IBEACON, null);
        e.putString(KEY_GEOFENCE, null);
        e.commit();
        onCreate(database);
    }

}