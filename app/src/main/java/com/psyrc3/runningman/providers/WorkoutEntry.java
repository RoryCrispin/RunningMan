package com.psyrc3.runningman.providers;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;

import com.psyrc3.runningman.GPXHelper;
import com.psyrc3.runningman.services.PathKeeper;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/*
    This class is used to represent saved PathKeeper workouts. We bake the distance,
    avg pace, time elapsed ect. details into the data structure so that they could be
    displayed/searched in listview ect without having to parse the raw GPX data of every workout
    in the listviews.
 */

public class WorkoutEntry {
    public int id;
    public String title;
    public long date;
    public double distance;
    public double avgPace;
    public long timeElapsed;
    public String track;
    public String type;
    public PathKeeper path;

    public WorkoutEntry(String title, String type, PathKeeper path) {
        this.title = title;
        date = path.getStartTime();
        distance = path.getDistance();
        avgPace = path.getAvgPace();
        timeElapsed = path.getTimeElapsed();
        track = path.reprGPX();
        this.type = type;
    }
    public WorkoutEntry(int res_id, ContentResolver contentResolver) {
        Cursor c = contentResolver.query(WorkoutProviderContract.WORKOUT_URI,
                null, String.valueOf(res_id), null, null);
        this.id = res_id;
        this.title = c.getString(WorkoutProviderContract._TITLE);
        this.date = c.getLong(WorkoutProviderContract._DATE);
        this.distance = c.getDouble(WorkoutProviderContract._DISTANCE);
        this.avgPace = c.getDouble(WorkoutProviderContract._AVGPACE);
        this.timeElapsed = c.getLong(WorkoutProviderContract._TIMEELAPSED);
        this.track = c.getString(WorkoutProviderContract._TRACK);
        this.type = c.getString(WorkoutProviderContract._TYPE);
        c.close();
        try {
            path = GPXHelper.parseTrack(track);
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
    }
    public WorkoutEntry(ContentValues vals) {
        id = vals.getAsInteger(WorkoutProviderContract.ID);
        title = vals.getAsString(WorkoutProviderContract.TITLE);
        date = vals.getAsLong(WorkoutProviderContract.DATE);
        distance = vals.getAsLong(WorkoutProviderContract.DISTANCE);
        avgPace = vals.getAsLong(WorkoutProviderContract.AVGPACE);
        timeElapsed = vals.getAsLong(WorkoutProviderContract.TIMEELAPSED);
        track = vals.getAsString(WorkoutProviderContract.TRACK);
        type = vals.getAsString(WorkoutProviderContract.TYPE);
        try {
            path = GPXHelper.parseTrack(track);
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
    }

    public ContentValues toContentValues() {
        ContentValues newEntry = new ContentValues();
        newEntry.put(WorkoutProviderContract.ID, id);
        newEntry.put(WorkoutProviderContract.TITLE, title);
        newEntry.put(WorkoutProviderContract.DATE, date);
        newEntry.put(WorkoutProviderContract.DISTANCE, distance);
        newEntry.put(WorkoutProviderContract.AVGPACE, avgPace);
        newEntry.put(WorkoutProviderContract.TIMEELAPSED, timeElapsed);
        newEntry.put(WorkoutProviderContract.TRACK, track);
        newEntry.put(WorkoutProviderContract.TYPE, type);
        return newEntry;
    }

}
