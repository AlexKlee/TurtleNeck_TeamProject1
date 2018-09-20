package com.akl.turtleneck;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyLogDBHelper extends SQLiteOpenHelper {
    MyLogDBHelper(Context c){
        super(c, "LogDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS logTbl (id CHAR(10) PRIMARY KEY NOT NULL, pass CHAR(16) NOT NULL);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS logTbl;");
        onCreate(db);
    }
}//MyLogDBHelper end