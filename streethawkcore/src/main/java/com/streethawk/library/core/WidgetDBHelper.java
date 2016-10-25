package com.streethawk.library.core;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
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
        super.onUpgrade(database, oldVersion, newVersion);
    }

}
