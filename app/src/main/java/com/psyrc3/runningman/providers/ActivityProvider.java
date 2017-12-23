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


public class ActivityProvider extends ContentProvider {

    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(ActivityProviderContract.AUTHORITY, "activity/", 0);
        uriMatcher.addURI(ActivityProviderContract.AUTHORITY, "activities/", 1);

    }

    private ActivityHelper activityHelper;

    @Override
    public boolean onCreate() {
        this.activityHelper = new ActivityHelper(this.getContext());
        return false;
    }

    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String query,
                        @Nullable String[] strings1, @Nullable String s1) {
        SQLiteDatabase db = activityHelper.getWritableDatabase();
        if (uriMatcher.match(uri) == 0) {
            Cursor cc = db.rawQuery("SELECT * FROM activities WHERE _id == " + query, null);
            cc.moveToFirst();
            return cc;
        } else if (uriMatcher.match(uri) == 1) {
            Cursor cc;
            if (query.equals("*")) {
                cc = db.rawQuery("SELECT * FROM activities", null);
            } else {
                cc = db.rawQuery("SELECT * FROM activities WHERE type == '" + query + "'", null);
            }
            cc.moveToFirst();
            return cc;
        }
        return null;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        if (uriMatcher.match(uri) == 0) {
            SQLiteDatabase db = activityHelper.getWritableDatabase();
            ActivityEntry entry = new ActivityEntry(contentValues);
            db.execSQL(String.format(Locale.ENGLISH,
                    "INSERT INTO activities " +
                            "(title, date, distance, avgPace, timeElapsed, track, type) " +
                            "VALUES ('%s', %d, %f, %f, %d, '%s', '%s');",
                    entry.title, entry.date, entry.distance, entry.avgPace,
                    entry.timeElapsed, entry.track, entry.type));
            Log.d("G53MDP", "Inserted into db!");
        }
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String id, @Nullable String[] strings) {
        if (uriMatcher.match(uri) == 0) {
            SQLiteDatabase db = activityHelper.getWritableDatabase();
            db.execSQL("DELETE FROM activities WHERE _id =" + id);
            return 1;
        }
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s,
                      @Nullable String[] strings) {
        if (uriMatcher.match(uri) == 0) {
            SQLiteDatabase db = activityHelper.getWritableDatabase();
            ActivityEntry entry = new ActivityEntry(contentValues);
            db.execSQL(String.format(Locale.ENGLISH, "UPDATE activities SET title = \"%s\", " +
                    "type = \"%s\" WHERE _id = %d", entry.title, entry.type, entry.id));
            return 1;
        }
        return 0;
    }
}
