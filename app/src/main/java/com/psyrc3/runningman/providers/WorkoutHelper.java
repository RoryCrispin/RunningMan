package com.psyrc3.runningman.providers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class WorkoutHelper extends SQLiteOpenHelper {

    public WorkoutHelper(Context c) {
        super(c, "workoutDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table workouts (_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title TEXT," +
                "date DOUBLE, " +
                "distance DOUBLE," +
                "avgpace DOUBLE," +
                "timeElapsed LONG," +
                "track TEXT," +
                "type TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
