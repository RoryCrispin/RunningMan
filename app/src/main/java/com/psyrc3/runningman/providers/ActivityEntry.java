package com.psyrc3.runningman.providers;

import android.content.ContentValues;

import com.psyrc3.runningman.services.PathKeeper;

public class ActivityEntry {
    public int id;
    public String title;
    public long date;
    public double distance;
    public double avgPace;
    public double timeElapsed;
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
