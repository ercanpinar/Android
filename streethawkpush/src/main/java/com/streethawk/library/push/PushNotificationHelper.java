package com.streethawk.library.push;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Helper class for push notification db
 */
class PushNotificationHelper extends SHSqliteBase {
    public PushNotificationHelper(Context context) {
        super(context);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        super.onCreate(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        super.onUpgrade(database, oldVersion, newVersion);
    }
}