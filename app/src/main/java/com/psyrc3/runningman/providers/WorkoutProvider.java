package com.psyrc3.runningman.providers;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Locale;


public class WorkoutProvider extends ContentProvider {

    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(WorkoutProviderContract.AUTHORITY, "workout/", 0);
        uriMatcher.addURI(WorkoutProviderContract.AUTHORITY, "workouts/", 1);
    }

    private WorkoutHelper workoutHelper;

    @Override
    public boolean onCreate() {
        this.workoutHelper = new WorkoutHelper(this.getContext());
        return false;
    }

    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String query,
                        @Nullable String[] strings1, @Nullable String s1) {
        SQLiteDatabase db = workoutHelper.getWritableDatabase();
        query = sanitiseInput(query);
        if (uriMatcher.match(uri) == 0) {
            Cursor cc = db.rawQuery("SELECT * FROM workouts WHERE _id == " + query, null);
            cc.moveToFirst();
            return cc;
        } else if (uriMatcher.match(uri) == 1) {
            Cursor cc;
            if (query.equals("*")) {
                cc = db.rawQuery("SELECT * FROM workouts", null);
            } else {
                cc = db.rawQuery("SELECT * FROM workouts WHERE type == '" + query + "'", null);
            }
            cc.moveToFirst();
            return cc;
        }
        throw new IllegalArgumentException();
    }

    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        if (uriMatcher.match(uri) == 0) {
            SQLiteDatabase db = workoutHelper.getWritableDatabase();
            WorkoutEntry entry = new WorkoutEntry(contentValues);
            db.execSQL(String.format(Locale.ENGLISH,
                    "INSERT INTO workouts " +
                            "(title, date, distance, avgPace, timeElapsed, track, type) " +
                            "VALUES ('%s', %d, %f, %f, %d, '%s', '%s');",
                    sanitiseInput(entry.title), entry.date, entry.distance, entry.avgPace,
                    entry.timeElapsed, entry.track, entry.type));
            Log.d("G53MDP", "Inserted into db!");
            return null;
        }
        throw new IllegalArgumentException();
    }


    @Override
    public int delete(@NonNull Uri uri, @Nullable String id, @Nullable String[] strings) {
        if (uriMatcher.match(uri) == 0) {
            SQLiteDatabase db = workoutHelper.getWritableDatabase();
            db.execSQL("DELETE FROM workouts WHERE _id =" + sanitiseInput(id));
            return 1;
        }
        throw new IllegalArgumentException();
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s,
                      @Nullable String[] strings) {
        if (uriMatcher.match(uri) == 0) {
            SQLiteDatabase db = workoutHelper.getWritableDatabase();
            WorkoutEntry entry = new WorkoutEntry(contentValues);
            db.execSQL(String.format(Locale.ENGLISH, "UPDATE workouts SET title = \"%s\", " +
                    "type = \"%s\" WHERE _id = %d", sanitiseInput(entry.title),
                    sanitiseInput(entry.type), entry.id));
            return 1;
        }
        throw new IllegalArgumentException();
    }

    // If we were to export the content provider, we ought to implement this function
    // to prevent from SQL Injection.
    // At the moment, users could in theory inject through the activity title,
    // but they'd only be able to hack themselves. We're concerned about other
    // Aplications extracting informaiton only.
    private String sanitiseInput(String input){
        //TODO: Stub: escape characters before exporting service
        return input;
    }
}
