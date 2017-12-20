package com.psyrc3.runningman.providers;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;

import com.psyrc3.runningman.services.PathKeeper;

public class ActivityEntry {
    public int id;
    public String title;
    public long date;
    public double distance;
    public double avgPace;
    public long timeElapsed;
    public String track;
    public String type;

    public ActivityEntry(String title, String type, PathKeeper path) {
        this.title = title;
        date = path.getStartTime();
        distance = path.getDistance();
        avgPace = path.getAvgPace();
        timeElapsed = path.getTimeElapsed();
        track = "";
        this.type = type;
    }

//    public ActivityEntry(int id, String title, long date, double distance, double avgPace,
//                         long timeElapsed, String track, Stringt )

    public ActivityEntry(int res_id, ContentResolver contentResolver) {
        Cursor c = contentResolver.query(ActivityProviderContract.ACTIVITY_URI,
                null, String.valueOf(res_id), null, null);
        this.id = c.getInt(ActivityProviderContract._ID);
        this.title = c.getString(ActivityProviderContract._TITLE);
        this.date = c.getLong(ActivityProviderContract._DATE);
        this.distance = c.getDouble(ActivityProviderContract._DISTANCE);
        this.avgPace = c.getDouble(ActivityProviderContract._AVGPACE);
        this.timeElapsed = c.getLong(ActivityProviderContract._TIMEELAPSED);
        this.track = c.getString(ActivityProviderContract._TRACK);
        this.type = c.getString(ActivityProviderContract._TYPE);
    }

    public ActivityEntry(ContentValues vals) {
        title = vals.getAsString(ActivityProviderContract.TITLE);
        date = vals.getAsLong(ActivityProviderContract.DATE);
        distance = vals.getAsLong(ActivityProviderContract.DISTANCE);
        avgPace = vals.getAsLong(ActivityProviderContract.AVGPACE);
        timeElapsed = vals.getAsLong(ActivityProviderContract.TIMEELAPSED);
        track = vals.getAsString(ActivityProviderContract.TRACK);
        type = vals.getAsString(ActivityProviderContract.TYPE);
    }

    public ContentValues toContentValues() {
        ContentValues newEntry = new ContentValues();
        newEntry.put(ActivityProviderContract.TITLE, title);
        newEntry.put(ActivityProviderContract.DATE, date);
        newEntry.put(ActivityProviderContract.DISTANCE, distance);
        newEntry.put(ActivityProviderContract.AVGPACE, avgPace);
        newEntry.put(ActivityProviderContract.TIMEELAPSED, timeElapsed);
        newEntry.put(ActivityProviderContract.TRACK, track);
        newEntry.put(ActivityProviderContract.TYPE, type);
        return newEntry;
    }

}
