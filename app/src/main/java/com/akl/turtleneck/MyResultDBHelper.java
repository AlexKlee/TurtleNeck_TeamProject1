package com.akl.turtleneck;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyResultDBHelper extends SQLiteOpenHelper {
    MyResultDBHelper(Context c){
        super(c, "LogDB2", null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS resultTbl (num INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, id CHAR(10) NOT NULL, time INTEGER, year INTEGER, month INTEGER, day INTEGER);");
    }//Table생성, id, 사용시간, 날짜(년, 월, 일)

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS resultTbl");
        onCreate(db);
    }
}
